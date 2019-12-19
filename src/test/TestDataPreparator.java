package test;

import ch.danielhoop.sql.DynamicDriverLoader;
import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDataPreparator {
    static boolean wasDriverRegistered = false;
    static boolean wasPrepared = false;

    public static void main(String[] args) {
        prepare();
    }

    public static void registerDbDriver() {
        if (!wasDriverRegistered) {
            GlobalState globalState = GlobalState.getInstance();

            try {
                DynamicDriverLoader.registerDriver("./lib/mysql-connector-java-8.0.17.jar", "com.mysql.cj.jdbc.Driver");
            } catch (SQLException | MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException | FileNotFoundException e) {
                e.printStackTrace();
            }

            globalState.setDbConnector(
                    new DbConnector("jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT",
                            "NinjaWarrior",
                            "Asdf-Poiu-0987-1234"));
        }
        wasDriverRegistered = true;
    }

    public static void prepare() {
        if (!wasPrepared) {

            registerDbDriver();
            GlobalState globalState = GlobalState.getInstance();
            DbConnector dbConnector = globalState.getDbConnector();
            Connection conn = dbConnector.getConnection();
            Statement stmt = dbConnector.createStatmentForConnection(conn);

            try {
                stmt.execute(Query.dropSchema());
                for (String query : Query.createDatabaseSchema()) {
                    if (!query.equals(";") && !query.equals("\n;")) {
                        stmt.execute(query);
                    }
                }
                for (String query : Query.createTestData()) {
                    if (!query.equals(";") && !query.equals("\n;")) {
                        stmt.execute(query);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ExceptionVisualizer.showAndAddMessage(e, "When trying to get the events from the database, the following error occurred: ");
            }

            dbConnector.closeStatement(stmt);
            dbConnector.closeConnection(conn);

            wasPrepared = true;
        }
    }
}
