package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.DbPreparator;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.enums.SupportedLocale;
import ch.ffhs.pa.competitionmanager.gui.CompetitorEditor;
import ch.ffhs.pa.competitionmanager.gui.EventSelector;
import ch.ffhs.pa.competitionmanager.gui.ScoreEditor;

import java.util.*;

/**
 * Singleton containing some global variables.
 */
public class GlobalState {

    private static GlobalState globalState = null;
    private DbConnector dbConnector;
    private Map<SupportedLocale, Locale> locales;
    private Locale locale;
    private SupportedLocale localName;
    private Collection<SupportedLocale> allSupportedLocals;
    private ResourceBundle guiTextBundle;

    private Category category;
    private Competitor competitor;
    private Event event;
    private Score score;

    private CategoryList categoryList;
    private CompetitorList competitorList;
    private RankingList rankingList;
    private DbMonitor dbMonitor;

    private GlobalState() {
        locales = new HashMap<>();
        locales.put(SupportedLocale.en_US, new Locale.Builder().setLanguage("en").setRegion("US").build());
        locales.put(SupportedLocale.de_CH, new Locale.Builder().setLanguage("de").setRegion("CH").build());
        //  Default locale is de_CH. setLocale will also set guiTextBundle.
        setLocale(SupportedLocale.de_CH);
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
        // CategoryList
        categoryList = new CategoryList(event);
        // Prepare database for event.
        DbPreparator.prepare(event, categoryList);
        // RankingList
        rankingList = new RankingList(event, categoryList);
        // CompetitorList
        competitorList = CompetitorList.Build.withAllCompetitors(event);
        // Start DbMonitor
        if (dbMonitor != null) {
            dbMonitor.stop();
        }
        dbMonitor = new DbMonitor(rankingList, 5, new DbPuller());
        dbMonitor.start();
    }

    public Score getScore() {
        return score;
    }
    public void setScore(Score score) {
        this.score = score;
    }

    // CategoryList: No setter!
    public CategoryList getCategoryList() {
        return categoryList;
    }
    // RankingList: No setter!
    public RankingList getRankingList() {
        return rankingList;
    }

    public CompetitorList getCompetitorList() {
        return competitorList;
    }

    public void reloadCompetitorListFromDb() {
        competitorList.reloadFromDb();
    }

    public Locale getLocale() {
        return this.locale;
    }
    public void setLocale(SupportedLocale localeName) {
        locale = locales.get(localeName);
        guiTextBundle = ResourceBundle.getBundle("GuiText", locale);
        localName = localeName;
    }

    public SupportedLocale getLocalName() {
        return localName;
    }
    public Collection<SupportedLocale> getAllSupportedLocals() {
        return allSupportedLocals;
    }

    public ResourceBundle getGuiTextBundle() {
        return guiTextBundle;
    }
    
}
