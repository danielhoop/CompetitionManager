package test;

import ch.danielhoop.sql.DynamicDriverLoader;
import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.entities.Event;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventListTest {

    // Assert that events are ordered descending by date.
    @Test
    void getEvents() {
        TestDataPreparator.prepare();

        EventList eventList = new EventList();
        List<Event> events = eventList.getEvents();

        assertEquals(events.get(0).getName(), "Ninja Warriors 2019");
        assertEquals(events.get(1).getDateDescription(), "11. September 2018");
    }


}