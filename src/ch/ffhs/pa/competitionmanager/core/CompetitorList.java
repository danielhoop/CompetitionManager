package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.gui.CompetitorTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds information on competitors.
 */
public class CompetitorList {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private List<Competitor> competitors;
    private boolean ofEvent;

    /**
     * Private constructor for factory pattern.
     */
    private CompetitorList(Event event, boolean ofEvent) {
        this.ofEvent = ofEvent;
        this.event = event;
        this.competitors = new LinkedList<>();
        loadCompetitorsFromDb(event, ofEvent);
    }

    /**
     * Factory to build instances of the object.
     */
    public static class Build {
        /**
         * Create object with competitors of all events (not event-specific).
         * @param event The event.
         * @return A new CompetitorList instance.
         */
        public static CompetitorList withAllCompetitors(Event event) {
            return new CompetitorList(event, false);
        }
        /**
         * Create object with competitors of given event only.
         * @param event The event.
         * @return A new CompetitorList instance.
         */
        public static CompetitorList havingScoresForEvent(Event event) {
            return new CompetitorList(event, true);
        }
    }

    /**
     * Reload competitors from database.
     */
    public void reloadFromDb() {
        loadCompetitorsFromDb(event, ofEvent);
    }

    /**
     * Get competitors as a list.
     * @return The competitors as a list.
     */
    public List<Competitor> getCompetitors() {
        return competitors;
    }

    /**
     * Get competitors as a table model to be used  in a JTable.
     * @return The table model.
     */
    public CompetitorTableModel getCompetitorsAsTableModel() {
        CompetitorTableModel competitorTableModel = new CompetitorTableModel(this);
        return competitorTableModel;
    }

    /**
     * Load competitors from database.
     * @param event The event.
     * @param ofEvent Indicating if competitors of the event should be loaded (true) or all competitors in database (false).
     */
    private void loadCompetitorsFromDb(Event event, boolean ofEvent) {

        long eventId = event.getId();
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        try {
            if (ofEvent) {
                stmt.execute(Query.getCompetitorsWithScoreForEvent(eventId));
            } else {
                stmt.execute(Query.getAllCompetitors());
            }

            ResultSet rs = stmt.getResultSet();
            competitors.clear();

            while (rs.next()) {
                competitors.add(new Competitor(
                        rs.getLong("id"),
                        rs.getString("name"),
                        Gender.valueOf(rs.getInt("gender")),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getInt(Query.ageColumnName(eventId))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When getting all competitors from the database and storing them into a list, the following error occurred: ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }
}
