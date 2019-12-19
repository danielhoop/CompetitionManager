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
        TestDataPreparator.prepare();

        GlobalState globalState = GlobalState.getInstance();
        globalState.setEvent(Event.getById(1));

        Map<Category, List<Score>> scores = GlobalState.getInstance().getRankingList().getScores();

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