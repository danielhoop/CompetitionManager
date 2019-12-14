package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Event;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

/**
 * Singleton
 */
public class EventSelector {

    private static EventSelector eventSelector = null;

    private EventTableModel eventTableModel;
    private JFrame mainFrame;
    private int selectedRow;

    private JTable eventTable;
    private JPanel outerPanel;
    private JButton editScoresButton;
    private JButton editEventButton;
    private JButton editCategoriesButton;
    private JButton showRankingButton;
    private JScrollPane eventScrollPane;
    private JLabel tableCaption;
    private JLabel text1;

    public static EventSelector getInstanceAndSetVisible() {
        if (eventSelector == null) {
            eventSelector = EventSelector.main();
        } else {
            eventSelector.mainFrame.setVisible(true);
        }
        return eventSelector;
    }

    private EventSelector(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        createUIComponents();
    }

    private static EventSelector main() {
        ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
        JFrame frame = new JFrame(bundle.getString("EventSelector.title"));
        EventSelector eventSelector = new EventSelector(frame);
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(eventSelector.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        return eventSelector;
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        SwingUtilities.invokeLater(() -> {
            tableCaption.setVisible(false);
            text1.setVisible(false);
        });

        // Table
        EventList eventList = globalState.getEventList();
        eventTableModel = eventList.getEventsAsTableModel();
        eventTable.setModel(eventTableModel);
        selectedRow = -1;

        // Add a listener and save the selected row because otherwise it does not work when button is pressed.
        eventTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedRow = eventTable.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2) {
                    openEmptyScoreEditor();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = eventTable.getSelectedRow();
        });


        // Buttons
        editScoresButton.addMouseListener(new MouseListener() {
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
                SwingUtilities.invokeLater(() -> mainFrame.setVisible(false));
                EventEditor.getInstanceAndSetVisible();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        editCategoriesButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                openCategoryEditor();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void openEmptyScoreEditor() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
        } else {
            Event event = eventTableModel.getEventFromRow(selectedRow);
            globalState.setEvent(event);

            SwingUtilities.invokeLater(() -> mainFrame.setVisible(false));
            ScoreEditor.getInstanceAndSetVisible();

            // Test ScoreEditor to edit existing score.
            //Category category = globalState.getCategoryList().getCategories().get(0);
            //ScoreEditor.main(globalState.getRankingList().getScores().get(category).get(0));
        }
    }

    private void openCategoryEditor() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
        } else {
            Event event = eventTableModel.getEventFromRow(selectedRow);
            globalState.setEvent(event);

            SwingUtilities.invokeLater(() -> mainFrame.setVisible(false));
            CategoryEditor.getInstanceAndSetVisible();
        }
    }
}
