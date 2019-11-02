package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Event;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

public class EventSelector {
    private JTable eventTable;
    private JPanel outerPanel;
    private JButton okButton;
    private JButton editEventButton;
    private JButton createEventButton;
    private JButton editCategoriesButton;
    private JButton showRankingButton;

    GlobalState globalState = GlobalState.getInstance();
    ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

    private EventSelector() {
        createUIComponents();
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        EventTableModel eventTableModel = new EventList().getEventsAsTableModel();
        eventTable = new JTable(eventTableModel);
        SwingUtilities.invokeLater(() -> {
            eventTable.changeSelection(0, 0, false, false);
        });

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
                // TODO: Open (empty) window "ScoreEditor"
                Event event = eventTableModel.getEventFromRow(eventTable.getSelectedRow());
                System.out.println("Selected event: " + event.getId());
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

    public static void main(String[] args) {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("EventSelector.title"));
        frame.setContentPane(new EventSelector().outerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
