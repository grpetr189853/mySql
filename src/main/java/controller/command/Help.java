package controller.command;

import view.View;

public class Help implements Command {

    private View view;

    public Help(View view) {
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return command.equals("help");
    }

    @Override
    public void process(String command) {
        view.write("Existing commands:");

        view.write("\tlist");
        view.write("\t\tto display a list of tables");

        view.write("\thelp");
        view.write("\t\tto display a list of commands");

        view.write("\tfind|tableName");
        view.write("\t\tto retrieve content from the 'tableName'");

        view.write("\texit");
        view.write("\t\tto exit from the programm");
    }
}