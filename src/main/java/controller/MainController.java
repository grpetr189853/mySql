package controller;

import controller.command.*;
import model.DatabaseManager;
import view.View;

public class MainController {

    private View view;
    private DatabaseManager manager;
    private Command[] commands;

    public MainController(DatabaseManager manager, View view) {
        this.view = view;
        this.manager = manager;
        this.commands = new Command[]{
                new Connect(manager, view),
                new Help(view),
                new Exit(view),
                new IsConnected(manager, view),
                new Tables(manager, view),
                new Find(manager, view),
                new Create(manager, view),
                new Clear(manager, view),
                new Unsupported(view)
        };
    }

    public void run() {
        view.write("Hello, User!");
        view.write("Enter the Database name, Username and Password in the format: 'connect|database|user|password' " +
                "or help");
        try {
            doWork();
        } catch (ExitException e) {
            //
        }
    }

    private void doWork() {
        while (true) {
            String input = view.read();
            try {
                if (input == null) { // null when interrupt application Ctrl F2
                    new Exit(view).process(input);
                }
                for (Command command : commands) {
                    if (command.canProcess(input)) {
                        command.process(input);
                        break;
                    }
                }
            } catch (Exception e) {
                if (e instanceof ExitException) {
                    return;
                }
                printError(e);
            }
            view.write("Enter a command or help");
        }
    }

    private void printError(Exception e) {
        String message = "" + e.getMessage();
        if (e.getCause() != null) {
            message += "\n" + e.getCause().getMessage();
        }
        view.write("Failed by a reason ==> " + message);
    }
}