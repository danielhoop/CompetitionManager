package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Event;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

public class EventTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

    private List<Event> events;
    private String[] columns;

    public EventTableModel(EventList eventList) {
        this.events = eventList.getEvents();
        this.columns = new String[]{
                bundle.getString("Event.name"),
                bundle.getString("Event.date"),
                bundle.getString("Event.isTimeRelevant")
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0: return event.getName();
            case 1: return event.getDate();
            case 2: return event.isTimeRelevant();
            default: return null;
        }
    }

    public Event getEventFromRow(int rowIndex) {
        return events.get(rowIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    // Doing similar things with the default table model...

    /*DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{
                        bundle.getString("Event.name"),
                        bundle.getString("Event.date"),
                        bundle.getString("Event.isTimeRelevant"),
                },
                0);

        for (Event event : events) {
            tableModel.addRow(new Object[]{
                    event.getName(),
                    event.getDate(),
                    event.isTimeRelevant()
            });
        }
        return tableModel;*/
}
