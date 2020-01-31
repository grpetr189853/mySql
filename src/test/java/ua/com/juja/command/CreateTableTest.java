package ua.com.juja.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.juja.model.DatabaseManager;
import ua.com.juja.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CreateTableTest {

    private Command command;
    private DatabaseManager manager;
    private View view;

    @Before
    public void setup() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new CreateTable(manager, view);
    }

    @Test
    public void testCanProcess_CorrectCreateTableCommand() {
        assertTrue(command.canProcess("create|test|id"));
    }

    @Test
    public void testCanProcess_WrongCreateTableCommand() {
        assertFalse(command.canProcess("create"));
    }

    @Test
    public void testProcess_CreateTable() {
        // given
        Map<String, String> input = new LinkedHashMap<>();
        input.put("id", "");

        // when
        command.process("create|test|id");

        // then
        verify(manager, atMostOnce()).createTable("test", input.keySet());
        verify(view).write("Table 'test' created.");
    }

    @Test
    public void testProcess_CreateTableCommandWithInvalidParametersNumber() {
        // when
        try {
            command.process("create|test|");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // then
            assertEquals("" +
                    "Invalid parameters number separated by '|'.\n" +
                    "Expected min 3. You enter ==> 2.\n" +
                    "Use command 'create|tableName|column1|column2|...|columnN'", e.getMessage());
        }
    }
}