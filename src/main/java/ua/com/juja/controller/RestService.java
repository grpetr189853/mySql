package ua.com.juja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.bind.annotation.*;
import ua.com.juja.model.DatabaseManager;
import ua.com.juja.model.entity.Description;
import ua.com.juja.model.resources.ActionMessages;
import ua.com.juja.service.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class RestService {

    @Autowired
    private Service service;

    @RequestMapping(value = "/menu/content", method = RequestMethod.GET)
    public List<String> menuItems() {
        return service.getCommands();
    }

    @RequestMapping(value = "/help/content", method = RequestMethod.GET)
    public List<Description> helpItems() {
        return service.getCommandsDescription();
    }

    @RequestMapping(value = "/connected", method = RequestMethod.GET)
    public String isConnected(HttpSession session) {
        DatabaseManager manager = getManager(session);
        return (manager != null) ? manager.getUserName() : null;
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public String connecting(HttpSession session, @ModelAttribute Connection connection) {
        try {
            DatabaseManager manager = getDatabaseManager();
            manager.connect(connection.getDatabase(), connection.getUser(), connection.getPassword());
            session.setAttribute("manager", manager);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/tables/content", method = RequestMethod.GET)
    public List<String> tables(HttpSession session) {
        DatabaseManager manager = getManager(session);
        if (manager == null) {
            return new LinkedList<>();
        }
        return manager.getTables();
    }

    @RequestMapping(value = "/tables/{table}/content", method = RequestMethod.GET)
    public List<List<String>> table(@PathVariable(value = "table") String tableName, HttpSession session) {
        DatabaseManager manager = getManager(session);
        if (manager == null) {
            return new LinkedList<>();
        }
        return getRows(manager, tableName);
    }

    @RequestMapping(value = "/newDatabase/{name}", method = RequestMethod.PUT)
    public String newDatabase(@PathVariable(value = "name") String databaseName, HttpSession session) {
        try {
            getManager(session).createDatabase(databaseName);
//            userActions.saveAction(String.format("NewDatabase(%s)", databaseName), user, database);
            return String.format(ActionMessages.DATABASE_NEW.toString(), databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return String.format(ActionMessages.DATABASE_EXISTS.toString(), databaseName);
        }
    }

    @RequestMapping(value = "/dropDatabase/content", method = RequestMethod.GET)
    public List<String> databases(HttpSession session) {
        DatabaseManager manager = getManager(session);
        if (manager == null) {
            return new LinkedList<>();
        }
        return manager.getDatabases();
    }

    @RequestMapping(value = "/dropDatabase/{name}", method = RequestMethod.DELETE)
    public String dropDatabase(@PathVariable(value = "name") String databaseName, HttpSession session) {
        getManager(session).dropDatabase(databaseName);
//        userActions.saveAction(String.format("DropDatabase(%s)", databaseName), user, database);
        return String.format(ActionMessages.DROP_DB.toString(), databaseName);
    }

    @RequestMapping(value = "/newTable", method = RequestMethod.POST)
    public String newTable(@RequestParam Map<String, String> queryMap, HttpSession session) {
        String tableName = queryMap.get("tableName");
        queryMap.remove("tableName");

        DatabaseManager manager = getManager(session);
        try {
            manager.createTable(tableName, new LinkedHashSet(new LinkedList(queryMap.values())));
//            userActions.saveAction(String.format("NewTable(%s)", tableName), manager.getUserName(), manager.getDatabaseName());
            return String.format(ActionMessages.CREATE.toString(), tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return String.format(ActionMessages.CREATE_EXISTING_TABLE.toString(), tableName);
        }
    }

    @RequestMapping(value = "/dropTable/{tableName}", method = RequestMethod.DELETE)
    public String dropTable(@PathVariable(value = "tableName") String tableName, HttpSession session) {
        DatabaseManager manager = getManager(session);
        manager.dropTable(tableName);
//        userActions.saveAction(String.format("DropTable(%s)", tableName), manager.getUserName(), manager.getDatabaseName());
        return String.format(ActionMessages.DROP.toString(), tableName);
    }

    @RequestMapping(value = "/insert/{tableName}/content", method = RequestMethod.GET)
    public Set<String> insert(@PathVariable(value = "tableName") String tableName, HttpSession session) {
        return getManager(session).getColumns(tableName);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public String insert(@RequestParam Map<String, String> queryMap, HttpSession session) {
        String tableName = queryMap.get("tableName");
        queryMap.remove("tableName");

        DatabaseManager manager = getManager(session);
        manager.insert(tableName, queryMap);
//        userActions.saveAction(String.format("Insert into %s", tableName), manager.getUserName(), manager.getDatabaseName());

        return String.format(ActionMessages.INSERT.toString(), queryMap.toString());
    }

    //TODO make one method to get columns names content for insert, update, delete commands
    @RequestMapping(value = "/update/{tableName}/content", method = RequestMethod.GET)
    public Set<String> update(@PathVariable(value = "tableName") String tableName, HttpSession session) {
        return getManager(session).getColumns(tableName);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@RequestParam Map<String, String> queryMap, HttpSession session) {
        String tableName = queryMap.get("tableName");
        queryMap.remove("tableName");

        Map<String, String> set = new LinkedHashMap<>();
        set.put(queryMap.get("setColumn"), queryMap.get("setValue"));

        Map<String, String> where = new LinkedHashMap<>();
        where.put(queryMap.get("whereColumn"), queryMap.get("whereValue"));

        DatabaseManager manager = getManager(session);
        manager.update(tableName, set, where);
//        userActions.saveAction(String.format("Update in %s", tableName), manager.getUserName(), manager.getDatabaseName());

        return String.format(ActionMessages.UPDATE.toString(), where.toString());
    }

    @RequestMapping(value = "/clear/{name}", method = RequestMethod.DELETE)
    public String clear(@PathVariable(value = "name") String tableName, HttpSession session) {
        DatabaseManager manager = getManager(session);
        manager.clear(tableName);
//        userActions.saveAction(String.format("Clear(%s)", tableName), manager.getUserName(), manager.getDatabaseName());

        return String.format(ActionMessages.CLEAR.toString(), tableName);
    }

    @RequestMapping(value = "/actions/{userName}/content", method = RequestMethod.GET)
    public List<UserActionLog> actions(@PathVariable(value = "userName") String userName) {
        return service.getAllFor(userName);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void connecting(HttpSession session) {
        session.removeAttribute("manager");
    }

    @Lookup
    public DatabaseManager getDatabaseManager() {
        return null;
    }

    private DatabaseManager getManager(HttpSession session) {
        return (DatabaseManager) session.getAttribute("manager");
    }

    private List<List<String>> getRows(DatabaseManager manager, String tableName) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(manager.getColumns(tableName)));
        rows.addAll(manager.getRows(tableName));
        return rows;
    }

    private String getFormattedData(List<String> data) {
        return data.toString().replace("[", "").replace("]", "");
    }
}