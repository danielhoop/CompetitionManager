package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.gui.EventTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class EventList {

    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

    private List<Event> events;

    public EventList() {
        this.events = new LinkedList<>();
        getEventsFromDb();
    }

    public void reloadFromDb() {
        getEventsFromDb();
    }

    public List<Event> getEvents() {
        return events;
    }

    public EventTableModel getEventsAsTableModel() {
        EventTableModel eventTableModel = new EventTableModel(this);
        return eventTableModel;
    }

    private void getEventsFromDb() {

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