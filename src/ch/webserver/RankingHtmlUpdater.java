package ch.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

public class RankingHtmlUpdater implements INotifiable {
    @Override
    public void notifyMe() {
        HtmlPage.writetoHTML(HtmlPage.FullContent(GlobalState.getInstance().getRankingList().getScores()).render(),"html/content.txt");
    }
}