package ch.ffhs.pa.competitionmanager.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.*;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.webserver.j2html.tags.Tag;

import java.util.*;
import java.io.*;

import static ch.ffhs.pa.competitionmanager.webserver.j2html.TagCreator.*;

public class HtmlPage {

    public static Tag FullContent(Map<Category, List<Score>> scores) {

        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();
        boolean isTimeRelevant = globalState.getEvent().isTimeRelevant();

        String competitorColumnName = bundle.getString("Competitor.name");
        String scoreColumnName;
        if (globalState.getEvent().isTimeRelevant()) {
            scoreColumnName = bundle.getString("Score.timeNeeded");
        } else {
            scoreColumnName = bundle.getString("Score.pointsAchieved");
        }

            return ul(
                    each(scores, j ->
                            li(
                                    input().withType("radio").withName("tabs").withId("tab" + j.getKey().getId()),
                                    label().withText(j.getKey().getName()).attr("for=\"tab" + j.getKey().getId() + "\""),
                                    div(
                                            table(
                                                    tr(
                                                            th(competitorColumnName),
                                                            th(scoreColumnName)
                                                    ),
                                                    each(j.getValue(), i ->
                                                            tr(
                                                                    td(i.getCompetitor().getName()),
                                                                    td(getTimeOrPoints(i, isTimeRelevant))
                                                            ))
                                            ).withId("ranking")
                                    ).withId("tab-content" + j.getKey().getId()).withClass("content")
                            ).withClass("tab"))
            ).withClass("tabs");
    }

    public static Tag CategoryAmount(List<Category> categories) {
        int size = categories.size();
        return div().withStyle("display:none").withId("Amount").withText(Integer.toString(size));
    }

    public static Tag CategoryIDs(List<Category> categories) {
        return form(
                each(categories, i ->
                        input().withType("hidden").withName("category").withValue(String.valueOf(i.getId())).withId("category")
                )).withId("formc");
    }


    public static void writetoHTML(String text, String filename) {
        File f = new File(filename);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(text);
            bw.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static String getTimeOrPoints(Score score, boolean isTimeRelevant) {
        if (isTimeRelevant) {
            return score.getTimeNeeded().toString();
        }
        return score.getPointsAchieved().toString();
    }
}
