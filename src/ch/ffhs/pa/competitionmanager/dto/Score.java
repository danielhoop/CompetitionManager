package ch.ffhs.pa.competitionmanager.dto;

import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

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
        // TODO: Add a new row to database table.
        //       Then, use the new id provided by the database and update the id in this class instance.
        return false;
    }

    @Override
    public boolean update() {
        // TODO: First, set 'deleted', 'updated', and 'deletedDateTime' attribute in database to true, then create a new competitor in the database.
        //       Like this, the history of updates will be available.
        return false;
    }

    @Override
    public boolean delete() {
        // TODO: Set 'deleted' attribute in database and set 'deletedDateTime' accordingly.
        return false;
    }
}
