package model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class DatabaseManagerTest {

    private DatabaseManager manager;

    public abstract DatabaseManager getDatabaseManager();

    @Before
    public void setup() {
        manager = getDatabaseManager();
        //TODO create Database, create Table, then work with this DB and Table for testing
        manager.connect("business", "root", "root");
    }

    @Test
    public void testGetTablesNames() {
        // when
        List tables = manager.getTablesNames();

        // then
        assertEquals("[created_table, products, shops, users]", tables.toString());
    }

    @Test
    public void testCreateTable() {
        // given
        DataSet input = new DataSet();
        input.put("id","1");
        input.put("name","alex");
        input.put("password","1111");

        // when
        manager.createTable("created_table", input);

        // then
        assertEquals("[created_table, products, shops, users]", manager.getTablesNames().toString());
    }

    @Test
    public void testDropTable() {
        // when
        manager.dropTable("created_table");

        // then
        assertEquals("[products, shops, users]", manager.getTablesNames().toString());
    }

    @Test
    public void testGetTableColumns() {
        List columns = manager.getTableColumns("users");
        assertEquals("[id, name, password]", columns.toString());
    }

    @Test
    public void testGetTableData() {
        manager.clear("users");

        DataSet input = new DataSet();
        input.put("id", "1");
        input.put("name", "Alex");
        input.put("password", "1111");
        manager.insert("users", input);

        List<DataSet> tableData = manager.getTableData("users");
        assertEquals("[columns:[id, name, password], values:[1, Alex, 1111]]", tableData.toString());
    }

    @Test
    public void testUpdateTableData() {
        manager.clear("users");

        DataSet input = new DataSet();
        input.put("id", "1");
        input.put("name", "Alex");
        input.put("password", "1111");
        manager.insert("users", input);

        DataSet newValue = new DataSet();
        newValue.put("name", "Sasha");
        newValue.put("password", "0000");
        manager.update("users", newValue, 1);

        List<DataSet> tableData = manager.getTableData("users");
        assertEquals("[columns:[id, name, password], values:[1, Sasha, 0000]]", tableData.toString());
    }

    @Test
    public void testIsConnected() {
        assertTrue(manager.isConnected());
    }
}