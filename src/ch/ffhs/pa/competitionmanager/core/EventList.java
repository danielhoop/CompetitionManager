package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.gui.EventTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Holds information on events.
 */
public class EventList {

    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

    private List<Event> events;

    /**
     * Constructor
     */
    public EventList() {
        this.events = new LinkedList<>();
        loadEventsFromDb();
    }

    /**
     * Reload internal data from database.
     */
    public void reloadFromDb() {
        loadEventsFromDb();
    }

    /**
     * Get events as a list.
     * @return The events as a list.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Get events as a table model to be used in a JTable.
     * @return The table model.
     */
    public EventTableModel getEventsAsTableModel() {
        EventTableModel eventTableModel = new EventTableModel(this);
        return eventTableModel;
    }

    /**
     * Load events from database.
     */
    private void loadEventsFromDb() {

        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        try {
            stmt.execute(Query.getAllEvents());
            ResultSet rs = stmt.getResultSet();
            events.clear();
            while (rs.next()) {
                events.add(new Event(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("date_descr"),
                        rs.getString("description"),
                        rs.getBoolean("is_time_relevant")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When trying to get the events from the database, the following error occurred: ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }
}