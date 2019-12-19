package test;

import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void getId() {
    }
    /**
     * This method tests if the get Methods of Category Class are working properly
     */

    @Test
    void getName() {

        Category cat = new Category(1,2,"Test Category","Description",18,23, Gender.FEMALE);
        assertEquals(1,cat.getId());
        assertEquals(2,cat.getEventId());
        assertEquals("Test Category",cat.getName());
        assertEquals("Description",cat.getDescription());
        assertEquals(18,cat.getMinAgeInclusive());
        assertEquals(23,cat.getMaxAgeInclusive());
        assertEquals(Gender.FEMALE,cat.getGender());
    }
}