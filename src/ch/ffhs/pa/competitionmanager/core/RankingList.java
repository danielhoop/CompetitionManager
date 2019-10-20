package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

/**
 * The ranking list. When notified, it will pull tha database for new entries and sort the entries according
 * to their score or time (needed).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class RankingList implements INotifiable {

    private Event event;
    private Category category;
    private List<Score> scores;
    private long highestScoreIdInList;

    public RankingList(Event event, Category category, List<Score> scores, long highestScoreIdInList) {
        this.event = event;
        this.category = category;
        this.highestScoreIdInList = highestScoreIdInList;
    }

    /**
     * Gets all scores of the category from database and replaces inner list 'scores' completely.
     * @return Boolean value indicating if the transaction was successful.
     */
    public boolean updateScoresCompletely() {
        // TODO: See Javadoc description of method.
        return true;
    }

    /**
     * Only gets new scores that were not in the database before and thus is more efficient than method 'updateScoresCompletely'.
     * @return Boolean value indicating if the transaction was successful.
     */
    public boolean getNewScoresFromDb() {
        // TODO Execute query to get newest scores of which the id is above the current 'highestScoreIdInList'.
        return true;
    }

    @Override
    public void notifyMe() {
        getNewScoresFromDb();
        Collections.sort(scores);
    }

}
