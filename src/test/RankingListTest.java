package test;

import ch.danielhoop.sql.DynamicDriverLoader;
import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.entities.Score;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RankingListTest {
    /**
     * This method checks if the ranking list is sorted properly
     */

    @Test
    void getScores() {
        GlobalState globalState = GlobalState.getInstance();
        String driverPath, driverName, address, user, password;

        driverPath = "C:/Users/cr_io/IdeaProjects/ninja-warriors2/lib/mysql-connector-java-8.0.17.jar";
        driverName = "com.mysql.cj.jdbc.Driver";
        address = "jdbc:mysql://localhost:3306/CompetitionManager?autoReconnect=true&verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT";
        user = "NinjaWarrior";
        password = "Asdf-Poiu-0987-1234";

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

        globalState.setEventList(new EventList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        Event ev = new Event(1,"Ninja Warriors", LocalDate.parse("11/09/2018", formatter),"11. September 2018","First event organized with software",true);
        globalState.setEvent(ev);

        Map<Category, List<Score>> scores = GlobalState.getInstance().getRankingList().getScores();

        LocalTime timeCheck;


        for (Map.Entry<Category, List<Score>> cat : scores.entrySet()) {


            Boolean isSorted = true;

            Iterator<Score> iter = cat.getValue().iterator();
            Score current, previous = iter.next();
            while (iter.hasNext()) {
                current = iter.next();
                if (previous.getTimeNeeded().compareTo(current.getTimeNeeded()) > 0) {
                    isSorted = false;
                }
                previous = current;


            }
            assertTrue(isSorted);

        }


    }
}