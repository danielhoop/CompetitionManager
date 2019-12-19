package test;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryListTest {

    // Assert that all Categories for that event are given.
    @Test
    void getCategories() {
        TestDataPreparator.prepare();

        GlobalState globalState = GlobalState.getInstance();
        globalState.setEvent(Event.getById(1));
        List<Category> categories = globalState.getCategoryList().getCategories();

        assertEquals(categories.get(0).getName(), "Jungs");
        assertEquals(categories.get(0).getMinAgeInclusive(), 0);
        assertEquals(categories.get(1).getName(), "Mädchen");
        assertEquals(categories.get(1).getMaxAgeInclusive(), 17);

        assertEquals(categories.get(4).getName(), "Alle");
        assertEquals(categories.get(4).getMinAgeInclusive(), 0);
        assertEquals(categories.get(4).getMaxAgeInclusive(), 999);
        assertEquals(categories.get(4).getDescription(), "Alle Teilnehmer, unabhängig von Alter und Geschlecht");
        assertEquals(categories.get(4).getGender(), Gender.NOT_RELEVANT);
    }
}