package ch.ffhs.pa.competitionmanager.playground;

import ch.ffhs.pa.competitionmanager.core.EventList;

import javax.swing.*;

public class TableTest {

    public TableTest(EventList eventList) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JTable table = new JTable(eventList.getEventsAsTableModel());

        panel.add(new JScrollPane(table));
        frame.add(panel);
        frame.setSize(550, 400);
        frame.setVisible(true);
    }

}
