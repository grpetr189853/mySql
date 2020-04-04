package ua.com.juja.model;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
@Scope("prototype")
public class JDBCDatabaseManager implements DatabaseManager {

    private Connection connection;
    private JdbcTemplate template;

    @Override
    public void connect(String database, String user, String password) {
        ConnectParameters.get();
        try {
            Class.forName(ConnectParameters.driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(ActionMessages.NO_DRIVER.toString(), e);
        }
        try {
            if (connection != null) {
                connection.close();
            }
            connection = DriverManager.getConnection(
                    ConnectParameters.url + database + ConnectParameters.ssl, user, password);
            template = new JdbcTemplate(new SingleConnectionDataSource(connection, false));
        } catch (SQLException e) {
            connection = null;
            throw new RuntimeException(String.format(
                    ActionMessages.NO_CONNECTION.toString(),
                    database, user), e);
        }
    }

    @Override
    public void createDatabase(String databaseName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("create database " + databaseName);
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format(
                    ActionMessages.DATABASE_EXISTS.toString(), databaseName), e);
        }
    }

    @Override
    public List<String> getDatabases() {
        List<String> result = new LinkedList<>();
        try {
            DatabaseMetaData data = connection.getMetaData();
            ResultSet databases = data.getCatalogs();
            while (databases.next()) {
                result.add(databases.getString(1));
            }

            databases.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }

    @Override
    public void dropDatabase(String databaseName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("drop database " + databaseName);
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format(
                    ActionMessages.NOT_EXISTING_DATABASE.toString(), databaseName), e);
        }
    }

    @Override
    public void createTable(String tableName, Set<String> columns) {
        if (getTables().contains(tableName)) {
            throw new IllegalArgumentException(String.format(
                    ActionMessages.CREATE_EXISTING_TABLE.toString(), tableName));
        }
        String sql = "create table " + tableName + " (";

        for (String column : columns) {
            sql += column + " VARCHAR(45) NOT NULL,";
        }
        sql += " PRIMARY KEY (`" + columns.iterator().next() + "`))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropTable(String tableName) {
        notExistingTableValidation(tableName);

        try (Statement statement = connection.createStatement()) {
            statement.execute("drop table " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getTables() {
        List<String> result = new LinkedList<>();
        try {
            DatabaseMetaData data = connection.getMetaData();
            ResultSet tables = data.getTables(null, null, "%", null);
            while (tables.next()) {
                result.add(tables.getString("TABLE_NAME"));
            }
            tables.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }

    @Override
    public Set<String> getColumns(String tableName) {
        notExistingTableValidation(tableName);

        Set<String> set = new LinkedHashSet<>();

        return (Set<String>) template.query("select * from " + tableName,
                new ResultSetExtractor() {
                    @Override
                    public Set<String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnName(i + 1);
                            set.add(columnName);
                        }
                        return set;
                    }
                });
    }

    @Override
    public List<List<String>> getRows(String tableName) {
        notExistingTableValidation(tableName);

        return template.query("select * from " + tableName,
                new RowMapper() {
                    @Override
                    public List<String> mapRow(ResultSet resultSet, int j) throws SQLException {
                        List<String> row = new ArrayList<>();
                        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                            row.add(resultSet.getString(i + 1));
                        }
                        return row;
                    }
                });
    }

    @Override
    public void clear(String tableName) {
        notExistingTableValidation(tableName);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("delete from " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String tableName, Map<String, String> input) {
        notExistingTableValidation(tableName);

        try (Statement statement = connection.createStatement()) {
            String columns = getColumnNamesFormated(input, "%s, ");
            String values = getValuesFormated(input, "'%s', ");
            statement.executeUpdate(
                    "insert into " + tableName + " (" + columns + ") values (" + values + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(String tableName, Map<String, String> set, Map<String, String> where) {
        notExistingTableValidation(tableName);

        String setColumns = getColumnNamesFormated(set, "%s = ?, ");
        String whereColumns = getColumnNamesFormated(where, "%s = ?, ");
        String sql = "UPDATE " + tableName + " SET " + setColumns + " WHERE " + whereColumns;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            Collection<String> setValues = set.values();
            for (String value : setValues) {
                statement.setString(index++, value);
            }

            Collection<String> whereValues = where.values();
            for (String value : whereValues) {
                statement.setString(index, value);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRow(String tableName, Map<String, String> delete) {
        notExistingTableValidation(tableName);

        String columns = getColumnNamesFormated(delete, "%s = ?, ");
        String sql = "delete from " + tableName + " where " + columns;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Collection<String> values = delete.values();
            int index = 1;
            for (String value : values) {
                preparedStatement.setString(index++, value);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public boolean isDatabaseExist(String databaseName) {
        for (String database : getDatabases()) {
            if (databaseName.equals(database)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void notExistingTableValidation(String tableName) {
        if (!getTables().contains(tableName)) {
            throw new IllegalArgumentException(String.format(
                    ActionMessages.NOT_EXISTING_TABLE.toString(), tableName));
        }
    }

    private String getColumnNamesFormated(Map<String, String> input, String format) {
        String result = "";
        Set<String> columns = input.keySet();
        for (String column : columns) {
            result += String.format(format, column);
        }
        return result.substring(0, result.length() - 2);
    }

    private String getValuesFormated(Map<String, String> input, String format) {
        String result = "";
        Collection<String> values = input.values();
        for (String value : values) {
            result += String.format(format, value);
        }
        return result.substring(0, result.length() - 2);
    }
}