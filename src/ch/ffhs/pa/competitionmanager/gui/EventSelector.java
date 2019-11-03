package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Event;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.ResourceBundle;

public class EventSelector {

    private EventTableModel eventTableModel;
    private JFrame mainFrame;
    private int selectedRow;

    private JTable eventTable;
    private JPanel outerPanel;
    private JButton okButton;
    private JButton editEventButton;
    private JButton createEventButton;
    private JButton editCategoriesButton;
    private JButton showRankingButton;
    private JScrollPane eventScrollPane;

    private EventSelector(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        createUIComponents();
    }

    public static void main() {
        SwingUtilities.invokeLater(() -> {
            ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

            JFrame frame = new JFrame(bundle.getString("EventSelector.title"));
            frame.setContentPane(new EventSelector(frame).outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        // Table
        EventList eventList = new EventList();
        eventTableModel = eventList.getEventsAsTableModel();
        eventTable = new JTable(eventTableModel);

        // DONT do this!!!!!
        // *** Otherwise the row selection will not work ***!
        // Select the right row in the table, if a event is already contained in the global state.
        /*int rowIndexToSelect = 0;
        Event globalEvent = globalState.getEvent();
        if (globalEvent != null) {
            List<Event> events = eventList.getEvents();
            for (int i=0; i < events.size(); i++) {
                if (events.get(i).getId() == globalEvent.getId()) {
                    rowIndexToSelect = i;
                    break;
                }
            }
        }
        eventTable.changeSelection(rowIndexToSelect, 0, false, false);*/

        int rowIndexToSelect = 0;
        selectedRow = rowIndexToSelect;

        // Add a listener and save the selected row because otherwise it does not work when button is pressed.
        eventTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedRow = eventTable.rowAtPoint(e.getPoint());
                openEmptyScoreEditor();
                // System.out.println("RowSelectionEvent fired. selectedRow: " + selectedRow);
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = eventTable.getSelectedRow();
            // System.out.println("RowSelectionEvent fired. selectedRow: " + selectedRow);
        });


        // Buttons
        okButton = new JButton(bundle.getString("OK"));
        editEventButton = new JButton(bundle.getString("EventSelector.editExisting"));
        createEventButton = new JButton(bundle.getString("EventSelector.createNewEvent"));

        okButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                openEmptyScoreEditor();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        editEventButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO: Open "EventEditor" which is filled with the existing event details.
                Event event = eventTableModel.getEventFromRow(eventTable.getSelectedRow());
                System.out.println("Selected event: " + event.getId());
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        createEventButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO: Open an empty EventEditor.
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void openEmptyScoreEditor() {
        EventSelector eventSelectorInstance = this;
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
        } else {
            Event event = eventTableModel.getEventFromRow(selectedRow);
            GlobalState.getInstance().setEvent(event);
            mainFrame.dispose();
            ScoreEditor.main(true, eventSelectorInstance);
        }
    }
}
