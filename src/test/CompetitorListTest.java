package test;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.entities.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorListTest {

    // Assert that competitors are ordered by 'name' attribute.
    @Test
    void getCompetitors() {
        TestDataPreparator.prepare();

        GlobalState globalState = GlobalState.getInstance();
        globalState.setEvent(Event.getById(1));
        List<Competitor> competitors = globalState.getCompetitorList().getCompetitors();

        assertEquals(competitors.get(0).getName(), "Andreas Juppi");
        assertEquals(competitors.get(8).getName(), "Max Müller");
        assertEquals(competitors.get(11).getName(), "Ueli Schläpfi");
    }
}