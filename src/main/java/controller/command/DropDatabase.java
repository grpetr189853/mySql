package controller.command;

import model.DatabaseManager;
import view.View;

public class DropDatabase implements Command {

    private static final String COMMAND_SAMPLE = "dropDatabase|databaseName";
    private DatabaseManager manager;
    private View view;

    public DropDatabase(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("dropDatabase|");
    }

    @Override
    public void process(String command) {
        String[] data = command.split("\\|");
        String[] commandToDrop = COMMAND_SAMPLE.split("\\|");
        if (data.length != commandToDrop.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid number of parameters separated by '|'. Expected %s. You enter ==> %s",
                    commandToDrop.length, data.length));
        }

        String databaseName = data[1];
        // TODO check db already exists or catch exception in JDBC and wrap into RuntimeException

        manager.dropDatabase(databaseName);
        view.write(String.format("Database '%s' deleted.", databaseName));
    }
}