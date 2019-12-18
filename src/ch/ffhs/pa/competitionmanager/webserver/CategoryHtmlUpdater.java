package ch.ffhs.pa.competitionmanager.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;
/**
 * When notified calls the notifyMe() Method which in turn calls the HTMLPage.writetoHTML Method to write category details into category_content.txt
 */

public class CategoryHtmlUpdater implements INotifiable {
    /**
     * Calls the writetoHTML Method from the HTMLPage Class, which will write the categories into the html\category_content.txt
     */

    @Override
    public void notifyMe() {
        HtmlPage.writetoHTML(HtmlPage.CategoryIDs(GlobalState.getInstance().getCategoryList().getCategories()).render(), "html/category_content.txt");
    }
}
