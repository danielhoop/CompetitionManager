package ch.ffhs.pa.competitionmanager.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;
/**
 * When notified calls the notifyMe() Method which in turn calls the HTMLPage.writetoHTML Method to write category details into category_content.txt and content into content.txt
 */

public class RankingHtmlUpdater implements INotifiable {
    /**
     * Calls the writetoHTML Method from the HTMLPage Class, which will write the categories into the html\category_content.txt
     * and the assigned rankinglist into the html\content.txt
     */
    @Override
    public void notifyMe() {
        HtmlPage.writetoHTML(HtmlPage.FullContent(GlobalState.getInstance().getRankingList().getScores()).render(),"html/content.txt");
        HtmlPage.writetoHTML(HtmlPage.CategoryIDs(GlobalState.getInstance().getCategoryList().getCategories()).render(), "html/category_content.txt");
    }
}