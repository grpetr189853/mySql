package ua.com.juja.service;

import ua.com.juja.model.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public interface Service {

    List<String> commands();

    DatabaseManager connect(String database, String user, String password);

    List<List<String>> find(DatabaseManager manager, String tableName);

    List<List<String>> clear(DatabaseManager manager, String table);

    String newDatabase(DatabaseManager manager, String databaseName);

    default List<List<String>> getTableData(DatabaseManager manager, String tableName) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(manager.getColumns(tableName)));
        rows.addAll(manager.getRows(tableName));

        return rows;
    }
}