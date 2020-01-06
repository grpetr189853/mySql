package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCManager {

    private Connection connection;

    public static void main(String[] args) throws SQLException {
        JDBCManager jdbcManager = new JDBCManager();

        String database = "business";
        String user = "root";
        String password = "root";
        jdbcManager.connect(database, user, password);
        jdbcManager.getTablesNames();
    }

    public List getTablesNames() throws SQLException {
        DatabaseMetaData data = connection.getMetaData();
        ResultSet tables = data.getTables(null, null, "%", null);
        List result = new ArrayList<String>();
        while (tables.next()) {
            result.add(tables.getString("TABLE_NAME"));
        }
        return result;
    }

    public void connect(String database, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
        try {
            if (connection != null) {
                connection.close();
            }
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + database + "?autoReconnect=true&useSSL=false", user, password);
        } catch (SQLException e) {
            connection = null;
            throw new RuntimeException(String.format("Can't get connection for database: %s, user: %s ",
                    database, user), e);
        }
    }
}
