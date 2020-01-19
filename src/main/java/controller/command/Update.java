package controller.command;

import model.DataSet;
import model.DatabaseManager;
import view.View;

public class Update implements Command {

    private static final String COMMAND_SAMPLE = "update|tableName|column1|value1|column2|value2";
    private DatabaseManager manager;
    private View view;

    public Update(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void process(String command) {
        commandValidation(COMMAND_SAMPLE, command);

        String[] data = command.split("\\|");
//        String[] commandToInsert = COMMAND_SAMPLE.split("\\|");
//        if (data.length != commandToInsert.length) {
//            throw new IllegalArgumentException(String.format(
//                    "Invalid number of parameters separated by '|'. Expected %s. You enter ==> %s.\n" +
//                            "Use command 'update|tableName|column1|value1|column2|value2'",
//                    commandToInsert.length, data.length));
//        }

        DataSet set = new DataSet();
        set.put(data[2], data[3]);

        DataSet where = new DataSet();
        where.put(data[4], data[5]);

        String tableName = data[1];
        manager.update(tableName, set, where);
        view.write(String.format("Record '%s' updated.", where.getValues().get(0)));
        // print table with values
        new Find(manager, view).printTableHeader(tableName);
        new Find(manager, view).printValues(tableName);
    }
}