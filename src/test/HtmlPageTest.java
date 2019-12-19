package test;

import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.webserver.HtmlPage;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class test if the HTML Page is actually generating simple HTML content.
 */

class HtmlPageTest {
    /**
     * This method tests if the CategoryID is creating a form tag at the beginning
     */
    @org.junit.jupiter.api.Test
    void categoryIDs() throws Exception{
        String testhtmltag;

        List<Category> categoryList = new LinkedList<>();
        categoryList.add(new Category(1,1,"Profi","Profi",18,50, Gender.MALE));
        testhtmltag = HtmlPage.CategoryIDs(categoryList).render();
        //testhtmltag="<form id=\"formc\">";
        String subtag;
        subtag = testhtmltag.substring(0,17);
        assertEquals("<form id=\"formc\">",subtag);
    }
}