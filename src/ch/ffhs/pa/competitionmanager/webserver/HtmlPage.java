package ch.ffhs.pa.competitionmanager.webserver;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.*;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.webserver.j2html.tags.Tag;

import java.util.*;
import java.io.*;

import static ch.ffhs.pa.competitionmanager.webserver.j2html.TagCreator.*;

/**
 * Deploy Methods for creating HTML Formatted content out of the existing object classes.
 */

public class HtmlPage {
    /**
     * Generates the full content of the website to be displayed on the Live-Displays.
     * It will create a perfectly formatted html content that will display all categories, and in each category will display the ranking list accordingly
     * Using this Method we can avoid html syntax error. Loops for dynamic content are automatically managed using "each" tag
     * @param  scores - Hashmap using Category and the Score's list as stored items
     * @return fully formatted HTML Tag, created using the j2html.tags.Tag class
     */

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

    /**
     * Generates a hidden HTML div element, which holds the amount of categories to be displayed
     * This helps swapping the tabs if the Live-Display has activated automatic swapping between categories
     * @param  categories
     * @return fully formatted HTML Tag, created using the j2html.tags.Tag class
     */

    public static Tag CategoryAmount(List<Category> categories) {
        int size = categories.size();
        return div().withStyle("display:none").withId("Amount").withText(Integer.toString(size));
    }

    /**
     * Generates a hidden HTML form element to store every single category
     * This is helpful if the ids of the categories are not in ascending order
     * @param  categories
     * @return fully formatted HTML Tag, created using the j2html.tags.Tag class
     */

    public static Tag CategoryIDs(List<Category> categories) {
        return form(
                each(categories, i ->
                        input().withType("hidden").withName("category").withValue(String.valueOf(i.getId())).withId("category")
                )).withId("formc");
    }
    /**
     * Writes recveied string into a file. This method is basically used to get the formatted html tag and write them in the designated the html txt file to be picked up by the JavaScript Script
     * attached to the html file
     * @param  text - String
     * @param filename - file
     */


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

    /**
     * Prepares formating of the HTML File for both cases of: using times or points.
     * The title of the column in the ranking list is then changed accordingly from time to points
     * @param  score Gets the score
     * @param isTimeRelevant Boolean used to define if time or points are stored
     * @return either Points or Time for the selected score
     */

    private static String getTimeOrPoints(Score score, boolean isTimeRelevant) {
        if (isTimeRelevant) {
            return score.getTimeNeeded().toString();
        }
        return score.getPointsAchieved().toString();
    }
}
