package ua.com.juja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.juja.model.ActionMessages;
import ua.com.juja.model.DatabaseManager;
import ua.com.juja.service.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    private Service service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main() {
        return "redirect:/menu";
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help() {
        return "help";
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public String menu(Model model) {
        model.addAttribute("commands", getFormattedData(service.getCommands()));
        return "menu";
    }

    @RequestMapping(value = "/connect", method = RequestMethod.GET)
    public String connect(HttpSession session, Model model) {
        String page = (String) session.getAttribute("fromPage");
        session.removeAttribute("fromPage");

        model.addAttribute("connection", new Connection(page));
        return "connect";
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public String connecting(@ModelAttribute("connection") Connection connection,
                             Model model, HttpSession session) {
        try {
            DatabaseManager manager = getDatabaseManager();
            manager.connect(connection.getDatabase(), connection.getUser(), connection.getPassword());

            session.setAttribute("manager", manager);
            return "redirect:" + connection.getPage();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "/newDatabase", method = RequestMethod.GET)
    public String newDatabase(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/newDatabase", manager, session)) return "redirect:/connect";

        model.addAttribute("command", "newDatabase");
        return "setName";
    }

    @RequestMapping(value = "/newDatabase", params = {"name"}, method = RequestMethod.GET)
    public String newDatabase(Model model,
                              @RequestParam(value = "name") String databaseName,
                              HttpSession session) {
        try {
            getManager(session).createDatabase(databaseName);
            model.addAttribute("report", String.format(ActionMessages.DATABASE_NEW.toString(), databaseName));
            return "report";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "/dropDatabase", method = RequestMethod.GET)
    public String dropDatabase(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/dropDatabase", manager, session)) return "redirect:/connect";

        setAttributes("Databases", getFormattedData(manager.getDatabases()), "dropDatabase", model);
        return "tables";
    }

    @RequestMapping(value = "/dropDatabase/{name}", method = RequestMethod.GET)
    public String dropDatabase(Model model,
                               @PathVariable(value = "name") String databaseName,
                               HttpSession session) {
        getManager(session).dropDatabase(databaseName);

        model.addAttribute("report", String.format(ActionMessages.DROP_DB.toString(), databaseName));
        return "report";
    }

    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public String tables(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/tables", manager, session)) return "redirect:/connect";

        setAttributes("Tables", getFormattedData(manager.getTables()), "tables", model);
        return "tables";
    }

    @RequestMapping(value = "/tables/{name}", method = RequestMethod.GET)
    public String table(Model model,
                        @PathVariable(value = "name") String tableName,
                        HttpSession session) {
        model.addAttribute("rows", getRows(getManager(session), tableName));
        return "table";
    }

    @RequestMapping(value = "/newTable", method = RequestMethod.GET)
    public String newTable(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/newTable", manager, session)) return "redirect:/connect";
        model.addAttribute("command", "newTable");
        return "setName";
    }

    @RequestMapping(value = "/newTable", params = {"name", "count"}, method = RequestMethod.GET)
    public String newTable(Model model,
                           @RequestParam(value = "name") String tableName,
                           @RequestParam(value = "count") String count) {
        model.addAttribute("name", tableName);
        model.addAttribute("count", count);
        return "createTable";
    }

    @RequestMapping(value = "/newTable", method = RequestMethod.POST)
    public String newTable(Model model, @RequestParam Map<String, String> queryMap,
                           HttpSession session) {
        try {
            String tableName = queryMap.get("name");
            queryMap.remove("name");

            getManager(session).createTable(tableName, new LinkedHashSet(new LinkedList(queryMap.values())));

            model.addAttribute("report", String.format(ActionMessages.CREATE.toString(), tableName));
            return "report";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "/dropTable", method = RequestMethod.GET)
    public String dropTable(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/dropTable", manager, session)) return "redirect:/connect";

        setAttributes("Tables", getFormattedData(manager.getTables()), "dropTable", model);
        return "tables";
    }

    @RequestMapping(value = "/dropTable/{name}", method = RequestMethod.GET)
    public String dropTable(Model model,
                            @PathVariable(value = "name") String tableName,
                            HttpSession session) {
        getManager(session).dropTable(tableName);

        model.addAttribute("report", String.format(ActionMessages.DROP.toString(), tableName));
        return "report";
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public String clear(Model model, HttpSession session) {
        DatabaseManager manager = getManager(session);

        if (managerNull("/clear", manager, session)) return "redirect:/connect";

        setAttributes("Tables", getFormattedData(manager.getTables()), "clear", model);
        return "tables";
    }

    @RequestMapping(value = "/clear/{name}", method = RequestMethod.GET)
    public String clear(Model model,
                        @PathVariable(value = "name") String tableName,
                        HttpSession session) {
        DatabaseManager manager = getManager(session);
        manager.clear(tableName);

        model.addAttribute("report", String.format(ActionMessages.CLEAR.toString(), tableName));
        model.addAttribute("rows", getRows(manager, tableName));
        return "table";
    }

    @Lookup
    public DatabaseManager getDatabaseManager() {
        return null;
    }

    private String getFormattedData(List<String> data) {
        return data.toString().substring(1, data.toString().length() - 1);
    }

    private List<List<String>> getRows(DatabaseManager manager, String tableName) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(manager.getColumns(tableName)));
        rows.addAll(manager.getRows(tableName));
        return rows;
    }

    private DatabaseManager getManager(HttpSession session) {
        return (DatabaseManager) session.getAttribute("manager");
    }

    private void setAttributes(String head, String tableData, String command, Model model) {
        model.addAttribute("head", head);
        model.addAttribute("tables", tableData);
        model.addAttribute("command", command);
    }

    private boolean managerNull(String fromPage, DatabaseManager manager, HttpSession session) {
        if (manager == null) {
            session.setAttribute("fromPage", fromPage);
            return true;
        }
        return false;
    }
}