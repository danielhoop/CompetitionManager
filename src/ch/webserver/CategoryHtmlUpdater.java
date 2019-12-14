package ch.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

public class CategoryHtmlUpdater implements INotifiable {
    @Override
    public void notifyMe() {
        HtmlPage.writetoHTML(HtmlPage.CategoryIDs(GlobalState.getInstance().getCategoryList().getCategories()).render(), "category_content.txt");
    }
}
