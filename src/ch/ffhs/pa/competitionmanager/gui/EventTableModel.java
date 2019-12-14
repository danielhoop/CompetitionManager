package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

public class EventTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
    DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

    private List<Event> events;
    private String[] columns;
    private final int nameIdx, dateIdx, timeRelevantIdx;

    public EventTableModel(EventList eventList) {
        events = eventList.getEvents();
        columns = new String[]{
                bundle.getString("Event.name"),
                bundle.getString("Event.date"),
                bundle.getString("Event.isTimeRelevant")
        };
        nameIdx = 0;
        dateIdx = 1;
        timeRelevantIdx = 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        if (columnIndex == nameIdx)
            return event.getName();
        if (columnIndex == dateIdx)
            return dateStringConverter.asString(event.getDate());;
        if (columnIndex == timeRelevantIdx)
            return event.isTimeRelevant();
        return null;
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
