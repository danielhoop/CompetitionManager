package ch.ffhs.pa.competitionmanager.dto;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Contains information on one score of competition.
 * Simple data object without functionality (only getter, setter and persistence methods).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class Score implements Comparable<Score>, ICRUD {

    private long id;
    private long eventId;
    private Competitor competitor;
    // Either 'timeNeeded' or 'pointsAchieved' must be filled, but not both!
    private LocalTime timeNeeded;
    // pointsAchieved is Double such that it can be null.
    private Double pointsAchieved;
    private int numberOfTries;
    private boolean isValid;
    private LocalDateTime timeOfRecording;

    public Score(long id, long eventId, Competitor competitor,
                 LocalTime timeNeeded, Double pointsAchieved, int numberOfTries, boolean isValid,
                 LocalDateTime timeOfRecording) {
        this.id = id;
        this.eventId = eventId;
        this.competitor = competitor;
        this.timeNeeded = timeNeeded;
        this.pointsAchieved = pointsAchieved;
        this.isValid = isValid;
        this.numberOfTries = numberOfTries;
        this.timeOfRecording = timeOfRecording;
    }

    // id. No setter!
    public long getId() {
        return id;
    }
    public long getEventId() {
        return eventId;
    }
    // competitor
    public ch.ffhs.pa.competitionmanager.dto.Competitor getCompetitor() {
        return competitor;
    }
    public void setCompetitor(ch.ffhs.pa.competitionmanager.dto.Competitor competitor) {
        this.competitor = competitor;
    }
    // timeNeeded
    public LocalTime getTimeNeeded() {
        return timeNeeded;
    }
    public void setTimeNeeded(LocalTime timeNeeded) {
        this.timeNeeded = timeNeeded;
    }
    // pointsAchieved
    public Double getPointsAchieved() {
        return pointsAchieved;
    }
    public void setPointsAchieved(Double pointsAchieved) {
        this.pointsAchieved = pointsAchieved;
    }
    // numberOfTries
    public int getNumberOfTries() {
        return numberOfTries;
    }
    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }
    // isValid
    public boolean isValid() {
        return isValid;
    }
    public void setValid(boolean valid) {
        isValid = valid;
    }
    // timeOfRecording
    public LocalDateTime getTimeOfRecording() {
        return timeOfRecording;
    }
    public void setTimeOfRecording(LocalDateTime timeOfRecording) {
        this.timeOfRecording = timeOfRecording;
    }


    @Override
    public int compareTo(Score o) {
        if (isValid & !o.isValid)
            return -1;
        if (!isValid & o.isValid)
            return 1;
        if (!isValid & !o.isValid)
            return 0;
        // Time: Less is better!
        if (timeNeeded != null & o.timeNeeded != null)
            return timeNeeded.compareTo(o.timeNeeded);
        // Points: More is better! Therefore the opposite of the normal 'compareTo' function.
        if (pointsAchieved != null & o.pointsAchieved != null) {
            return -pointsAchieved.compareTo(o.pointsAchieved);
        }
        return 0;
    }

    // CRUD operations
    @Override
    public boolean create() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            String queryString;

            if(pointsAchieved == null) {
                queryString = Query.createScore(eventId, competitor.getId(), timeNeeded, 0, numberOfTries, isValid, timeOfRecording);
            } else {
                queryString = Query.createScore(eventId, competitor.getId(), timeNeeded, pointsAchieved, numberOfTries, isValid, timeOfRecording);
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(queryString, stmt.RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getLong(1);
                    }
                }
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Score.create(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean update() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.updateScore(id, eventId, competitor.getId(), timeNeeded, pointsAchieved, numberOfTries, isValid, timeOfRecording))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Score.delete(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean delete() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.deleteScore(id))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Score.delete(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }
}
