package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.gui.CompetitorTableModel;

import javax.swing.table.TableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class CompetitorList {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private List<Competitor> competitors;
    private boolean ofEvent;

    // Private constructor with factory pattern.
    private CompetitorList(Event event, boolean ofEvent) {
        this.ofEvent = ofEvent;
        this.event = event;
        this.competitors = new LinkedList<>();
        getCompetitorsFromDb(event, ofEvent);
    }
    // Factory
    public static class Build {
        public static CompetitorList withAllCompetitors(Event event) {
            return new CompetitorList(event, false);
        }
        public static CompetitorList havingScoresForEvent(Event event) {
            return new CompetitorList(event, true);
        }
    }

    public void reloadFromDb() {
        getCompetitorsFromDb(event, ofEvent);
    }

    public List<Competitor> getCompetitors() {
        return competitors;
    }

    public CompetitorTableModel getCompetitorsAsTableModel() {
        CompetitorTableModel competitorTableModel = new CompetitorTableModel(this);
        return competitorTableModel;
    }

    private void getCompetitorsFromDb(Event event, boolean ofEvent) {

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
