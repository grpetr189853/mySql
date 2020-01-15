package controller.command;

import model.DatabaseManager;
import view.View;

public class CreateDatabase implements Command {

    private static final String COMMAND_SAMPLE = "newDatabase|databaseName";
    private DatabaseManager manager;
    private View view;

    public CreateDatabase(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("newDatabase|");
    }

    @Override
    public void process(String command) {
        // TODO extract into separated method validation parameters in command String in all Command classes where it runs
        // TODO enter parameters data and commandToConnect into validated method

        String[] data = command.split("\\|");
        String[] commandToConnect = COMMAND_SAMPLE.split("\\|");
        if (data.length != commandToConnect.length) {
            // TODO create Exception class to throw the same message for all Commands when user enters Invalid number of parameters separated by '|'
            throw new IllegalArgumentException(String.format(
                    "Invalid number of parameters separated by '|'. Expected %s. You enter ==> %s",
                    commandToConnect.length, data.length));
        }

        String databaseName = data[1];
        manager.createDatabase(databaseName);
        view.write(String.format("Database '%s' created.", databaseName));
    }
}