package ch.ffhs.pa.competitionmanager.webserver;
import ch.ffhs.pa.competitionmanager.entities.*;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.webserver.j2html.tags.Tag;

import java.util.*;
import java.io.*;
import static ch.ffhs.pa.competitionmanager.webserver.j2html.TagCreator.*;

public class HtmlPage {


public List<Category> CategoryList () {
    LinkedList list = new LinkedList();
    List<Category> categoryList = new LinkedList<>();
    categoryList.add(new Category(1,1,"Profi","Profi",18,50,Gender.MALE));
    return categoryList;
}

public static Tag FullContent(Map<Category, List<Score>> scores)
{

    return ul(
            each(scores, j->
                    li(
            input().withType("radio").withName("tabs").withId("tab"+j.getKey().getId()),
            label().withText(j.getKey().getName()).attr("for=\"tab" + j.getKey().getId() + "\""),
            div(
                    table(
                            tr(
                                    th("Name"),
                                    th("Time")
                            ),
                            each(j.getValue(),i ->
                                    tr(
                                            td(i.getCompetitor().getName()),
                                            td(i.getTimeNeeded().toString())
                                    ))
                    ).withId("ranking")
            ).withId("tab-content" + j.getKey().getId()).withClass("content")
    ).withClass("tab"))
    ).withClass("tabs");
    }

    public static Tag CategoryAmount(List<Category> categories)
    {
        int size = categories.size();
        return div().withStyle("display:none").withId("Amount").withText(Integer.toString(size));
    }

    public static Tag CategoryIDs(List<Category> categories)
    {
     return form(
       each(categories, i->
               input().withType("hidden").withName("category").withValue(String.valueOf(i.getId())).withId("category")
       )).withId("formc");
    }


public static void writetoHTML(String text,String filename)
{
    File f= new File(filename);
    try {
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(text);
        bw.close();


    }
    catch (IOException e)
    {
        System.out.println(e);
    }


}





}
