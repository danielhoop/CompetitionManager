package ch.ffhs.pa.competitionmanager;

import ch.danielhoop.sql.DynamicDriverLoader;
import ch.danielhoop.utils.ArgumentInterpreter;
import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.DbMonitor;
import ch.ffhs.pa.competitionmanager.core.RankingList;
import ch.ffhs.pa.competitionmanager.db.DbConfig;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.DbCredentials;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.ui.PasswordUi;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Main function of the project.
 * Must be called from the command line. Either start the application in editing mode or in displaying mode to show the ranking lists.
 * Command line arguments are:
 * -- mode "edit" OR "display"
 * As well as other arguments for the databse connection. An example of command lime aguments that could work is:
 * --mode "display" --driverPath "E:/Workspaces/IntelliJ/CompetitionManager/lib/mysql-connector-java-8.0.17.jar" --driverName "com.mysql.cj.jdbc.Driver" --address "jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT" --user "NinjaWarrior" --password "Asdf-Poiu-0987-1234"
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("-help") || args[0].equals("--help")) {
            System.out.println(
                    "*******************\n"
                            + "Competition Manager\n"
                            + "*******************\n"
                            + "Daniel Hoop, Cristian Ion, Reto Laesser\n"
                            + "\nMandatory arguments:"
                            + "\n  --mode \"edit\" OR \"display\""
                            + "\n  --driverPath \"driverPath\""
                            + "\n  --driverName \"driverClassName\""
                            + "\n  --address \"databaseAddress\""
                            + "\n  --user \"user\""
                            + "\n  --password \"password\""
                            + "\nExample:"
                            + "\n  --mode \"edit\" --driverPath \"C:/path/ojdbc6.jar\" --driverName \"oracle.jdbc.driver.OracleDriver\" --address \"jdbc:oracle:thin:@//www.databaseServer.com:1521/schemaName\" --user \"anyUser\" --password \"secret\""
                            + "\n  --mode \"display\" --driverPath \"C:/path/mysql-connector-java-8.0.17.jar\" --driverName \"com.mysql.cj.jdbc.Driver\" --address \"jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true\" --user \"anyUser\" --password \"secret\""
                            + "\nHints:"
                            + "\n  No hints at the time.");
            System.exit(0);
        }

        // Evaluate arguments and store in map-like structure.
        ArgumentInterpreter args1 = new ArgumentInterpreter(
                new String[]{"mode","driverPath","driverName","address","user"},
                new String[]{"mode","driverPath","driverName","address","user","password"},
                false, false, true, true, false)
                .readArgs(args);

        // Initialize objects
        DbMonitor<Object> dbMonitor;
        RankingList rankingList;
        Event event;
        Category category;
        Score score;
        DbConfig dbConfig;
        Connection con;
        String mode, driverPath, driverName, address, user, password, connectionString;

        mode = args1.get("mode");
        driverPath = args1.get("driverPath");
        driverName = args1.get("driverName");
        address = args1.get("address");
        user = args1.get("user");

        // Ask for the password in case it was not given.
        if (args1.argIsSet("password")) {
            password = args1.get("password");
        } else {
            DbCredentials dbCred = new DbCredentials(user, null);
            PasswordUi pwUi = new PasswordUi(dbCred);
            if (dbCred.getPassword() == null) {
                ExceptionVisualizer.show(new IllegalArgumentException("The password must not be null."));
            }
            password = dbCred.getPassword();
        }

        // Create connection to database
        try {
            System.out.print("Loading database driver...");
            DynamicDriverLoader.registerDriver(driverPath, driverName);
            System.out.println(" Done.");

            System.out.println("Connecting to database...");
            con = DriverManager.getConnection(address, user, password);

            con.setAutoCommit(false);
            // st = con.createStatement();
            System.out.println("Connected.");


            // Close connection before leaving main method.
            con.close();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException | FileNotFoundException e) {
            ExceptionVisualizer.show(e);
        }

    }
}