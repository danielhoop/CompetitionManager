package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.DbPreparator;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.enums.SupportedLocale;
import ch.ffhs.pa.competitionmanager.utils.IpFinder;
import ch.ffhs.pa.competitionmanager.webserver.CategoryHtmlUpdater;
import ch.ffhs.pa.competitionmanager.webserver.RankingHtmlUpdater;

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
    private boolean websiteHasBeenOpened = false;
    private int httpPort;
    private String ipOfComputer;
    private String webServerAddress = "";

    private Event event;

    private CategoryList categoryList;
    private CompetitorList competitorList;
    private EventList eventList;
    private RankingList rankingList;
    private DbMonitor dbMonitor;

    private CategoryHtmlUpdater categoryHtmlUpdater;

    private GlobalState() {
        locales = new HashMap<>();
        locales.put(SupportedLocale.en_US, new Locale.Builder().setLanguage("en").setRegion("US").build());
        locales.put(SupportedLocale.de_CH, new Locale.Builder().setLanguage("de").setRegion("CH").build());
        //  Default locale is de_CH. setLocale will also set guiTextBundle.
        setLocale(SupportedLocale.de_CH);
        // Get IP of computer
        ipOfComputer = IpFinder.getLocalIp();
        webServerAddress = "http://" + ipOfComputer + "/html/index.html";
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
        // CategoryHtmlUpdater
        categoryHtmlUpdater = new CategoryHtmlUpdater();
        categoryHtmlUpdater.notifyMe();
        // Start DbMonitor
        if (dbMonitor != null) {
            dbMonitor.stop();
        }
        dbMonitor = new DbMonitor(rankingList, 5, new DbPuller());
        dbMonitor.addObjectToNotify(new RankingHtmlUpdater());
        dbMonitor.start();
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

    public EventList getEventList() {
        return eventList;
    }
    public void setEventList(EventList eventList) {
        this.eventList = eventList;
    }

    public boolean getWebsiteHasBeenOpened() {
        return websiteHasBeenOpened;
    }
    public void setWebsiteHasBeenOpened(boolean websiteHasBeenOpened) {
        this.websiteHasBeenOpened = websiteHasBeenOpened;
    }
    public int getHttpPort() {
        return httpPort;
    }
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
        this.webServerAddress = "http://" + ipOfComputer + ":" + httpPort + "/html/index.html";
    }
    public String getIpOfComputer() {
        return ipOfComputer;
    }
    public String getWebServerAddress() {
        return webServerAddress;
    }


    public void reloadCompetitorListFromDb() {
        competitorList.reloadFromDb();
    }

    public void reloadCategoryListFromDb() {
        categoryList.reloadFromDb();
        categoryHtmlUpdater.notifyMe();
    }

    public void reloadEventListFromDb() { eventList.reloadFromDb(); }

    // DbMonitor: No setter!
    public DbMonitor getDbMonitor() {
        return dbMonitor;
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
