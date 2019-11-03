package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.enums.SupportedLocale;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Singleton containing some global variables.
 */
public class GlobalState {

    private static GlobalState globalState = null;
    private DbConnector dbConnector;
    private Map<SupportedLocale, Locale> locales;
    private Locale locale;

    private Category category;
    private Competitor competitor;
    private Event event;
    private Score score;

    private GlobalState() {
        locales = new HashMap<>();
        locales.put(SupportedLocale.en_US, new Locale.Builder().setLanguage("en").setRegion("US").build());
        locales.put(SupportedLocale.de_CH, new Locale.Builder().setLanguage("de").setRegion("CH").build());
        //  Default locale is de_CH
        locale = locales.get(SupportedLocale.de_CH);
    }

    public static GlobalState getInstance() {
        if (globalState == null) {
            globalState = new GlobalState();
        }
        return globalState;
    }

    public DbConnector getDbConnector() {
        return dbConnector;
    }
    public void setDbConnector(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public Competitor getCompetitor() {
        return competitor;
    }
    public void setCompetitor(Competitor competitor) {
        this.competitor = competitor;
    }
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
    public Score getScore() {
        return score;
    }
    public void setScore(Score score) {
        this.score = score;
    }

    public Locale getLocale() {
        return this.locale;
    }
    public void setLocale(SupportedLocale localeName) {
        this.locale = locales.get(localeName);
    }
}
