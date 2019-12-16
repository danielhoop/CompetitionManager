package ch.ffhs.pa.competitionmanager;

import ch.danielhoop.sql.DynamicDriverLoader;
import ch.danielhoop.utils.ArgumentInterpreter;
import ch.danielhoop.utils.GuiLookAndFeelUtils;
import ch.ffhs.pa.competitionmanager.core.*;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.DbCredentials;
import ch.ffhs.pa.competitionmanager.db.DbPreparator;
import ch.ffhs.pa.competitionmanager.gui.EventSelector;
import ch.ffhs.pa.competitionmanager.gui.PasswordGui;
import ch.ffhs.pa.competitionmanager.webserver.WebServer;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.*;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Main function of the project.
 * Must be called from the command line. Either start the application in editing mode or in displaying mode to show the ranking lists.
 * Command line arguments are arguments for the database connection. An example of command lime arguments that could work is:
 * --driverPath "E:/Workspaces/IntelliJ/CompetitionManager/lib/mysql-connector-java-8.0.17.jar" --driverName "com.mysql.cj.jdbc.Driver" --address "jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT" --user "NinjaWarrior" --password "Asdf-Poiu-0987-1234" --httpPort 80
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-help") || args[0].equals("--help")) {
            System.out.println(
                    "*******************\n"
                            + "Competition Manager\n"
                            + "*******************\n"
                            + "Daniel Hoop, Cristian Ion, Reto Laesser\n"
                            + "\nMandatory arguments:"
                            + "\n  --driverPath \"databaseDriverPath\""
                            + "\n  --driverName \"databaseDriverClassName\""
                            + "\n  --address \"databaseAddress\""
                            + "\n  --user \"databaseUser\""
                            + "\n  --password \"databasePassword\""
                            + "\nOptional arguments:"
                            + "\n  --httpPort \"portForHttpServer\" -> If not specified, then port 80 will be used"
                            + "\nExample:"
                            + "\n  --driverPath \"C:/path/ojdbc6.jar\" --driverName \"oracle.jdbc.driver.OracleDriver\" --address \"jdbc:oracle:thin:@//www.databaseServer.com:1521/schemaName\" --user \"anyUser\" --password \"secret\" --httpPort 88"
                            + "\n  --driverPath \"C:/path/mysql-connector-java-8.0.17.jar\" --driverName \"com.mysql.cj.jdbc.Driver\" --address \"jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true\" --user \"anyUser\" --password \"secret\" --httpPort 88"
                            + "\nHints:"
                            + "\n  No hints at the time.");
            System.exit(0);
        }

        // Set Gui Look and feel
        GuiLookAndFeelUtils.set();

        // Evaluate arguments and store in map-like structure.
        ArgumentInterpreter args1 = null;
        try {
            args1 = new ArgumentInterpreter(
                    new String[]{"driverPath", "driverName", "address", "user"},
                    new String[]{"driverPath", "driverName", "address", "user", "password", "httpPort"},
                    false, false, true, true, false)
                    .readArgs(args);
        } catch (IllegalArgumentException e) {
            JOptionPane.showConfirmDialog(null, "Error when parsing command line arguments:\n" + e.getMessage(), "Error",  JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }

        // Initialize objects
        GlobalState globalState = GlobalState.getInstance();
        String driverPath, driverName, address, user, password;

        driverPath = args1.get("driverPath");
        driverName = args1.get("driverName");
        address = args1.get("address");
        user = args1.get("user");

        // Ask for the password in case it was not given.
        if (args1.argIsSet("password")) {
            password = args1.get("password");
        } else {
            DbCredentials dbCred = new DbCredentials(user, null);
            PasswordGui pwUi = new PasswordGui(dbCred);
            if (dbCred.getPassword() == null) {
                JOptionPane.showConfirmDialog(null, "The password must not be null.", "Error",  JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                System.exit(1);
            }
            password = dbCred.getPassword();
        }

        try {
            // Prepare database driver and connection to database.
            System.out.print("Loading database driver...");
            DynamicDriverLoader.registerDriver(driverPath, driverName);
            globalState.setDbConnector(new DbConnector(address, user, password));
            System.out.println(" Done.");
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException | FileNotFoundException e) {
            JOptionPane.showConfirmDialog(null, "Something went wrong when trying to register the database driver:\n" + e.getMessage(), "Error",  JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }

        // If database connection cannot be established, try to create the schema.
        try {
            DriverManager.getConnection(address, user, password);
        } catch (SQLException e) {
            ResourceBundle bundle = globalState.getGuiTextBundle();
            int shouldBeZero = JOptionPane.showConfirmDialog(null,
                    bundle.getString("Main.dbErrorShouldSchemaBeCreated") + e.getMessage(),
                    bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (shouldBeZero == 0) {
                address = address.replaceAll("CompetitionManager", "");
                globalState.setDbConnector(new DbConnector(address, user, password));
                DbPreparator.createSchemaIfNotExists();
            } else {
                System.exit(1);
            }
        }
        // Port
        int port = 80;
        if (args1.argIsSet("httpPort")) {
            try {
                port = Integer.parseInt(args1.get("httpPort"));
                if (port > 65535 || port < 0)
                    throw new NumberFormatException("Port out of range.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, globalState.getGuiTextBundle().getString("Main.portNotValid"));
            }
        }
        globalState.setHttpPort(port);

        // EventList can be set only after the database connector has been established!
        globalState.setEventList(new EventList());

        // Open the EventSelector
        EventSelector.getInstanceAndSetVisible();

        // Start Webserver - It will block forever! Don't place anything after this line.
        WebServer.startWebserver(port);
    }
}
