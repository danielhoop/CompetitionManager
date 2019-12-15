package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.utils.UrlOpener;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Singleton
 */
public class EventSelector {

    private static EventSelector eventSelector = null;

    private GlobalState globalState;
    private ResourceBundle bundle;
    private EventList eventList;
    private EventTableModel eventTableModel;
    private JFrame mainFrame;
    private int selectedRow;

    private JTable eventTable;
    private JPanel outerPanel;
    private JButton editScoresButton;
    private JButton editEventButton;
    private JButton editCategoriesButton;
    private JScrollPane eventScrollPane;
    private JLabel tableCaption;
    private JLabel text1;
    private JTextPane websiteHint;
    private JButton showRankingButton;

    public static EventSelector getInstanceAndSetVisible() {
        if (eventSelector == null) {
            eventSelector = EventSelector.main();
        } else {
            eventSelector.mainFrame.setVisible(true);
            eventSelector.createOrRefreshEventTable(true);
        }
        return eventSelector;
    }

    private EventSelector(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.globalState = GlobalState.getInstance();
        this.bundle = globalState.getGuiTextBundle();
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

        SwingUtilities.invokeLater(() -> {
            tableCaption.setVisible(false);
            text1.setVisible(false);

            // Website hint
            websiteHint.setText(bundle.getString("EventSelector.webserverHint1") +
                    "http://" + globalState.getIpOfComputer() + ":" + globalState.getHttpPort() + "\n" +
                    bundle.getString("EventSelector.webserverHint2"));
            websiteHint.setBorder(null);
            websiteHint.setEditable(false);
            websiteHint.setBackground(outerPanel.getBackground());
        });

        // Table
        createOrRefreshEventTable(true);
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
                    openScoreEditor();
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
                openScoreEditor();
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

        showRankingButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
                } else {
                    showRankingButton.setVisible(false);
                    globalState.setEvent(eventTableModel.getEventFromRow(selectedRow));
                    showRanking();
                    showRankingButton.setVisible(true);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void openScoreEditor() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        if (!globalState.getWebsiteHasBeenOpened()) {
            showRanking();
        }

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
        } else {
            Event event = eventTableModel.getEventFromRow(selectedRow);
            globalState.setEvent(event);

            SwingUtilities.invokeLater(() -> mainFrame.setVisible(false));
            ScoreCreator.getInstanceAndSetVisible();

            // Test ScoreCreator to edit existing score.
            //Category category = globalState.getCategoryList().getCategories().get(0);
            //ScoreCreator.main(globalState.getRankingList().getScores().get(category).get(0));
        }
    }

    private void openCategoryEditor() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventSelector.noRowSelectedErrorHint"));
        } else {
            Event event = eventTableModel.getEventFromRow(selectedRow);
            globalState.setEvent(event);

            SwingUtilities.invokeLater(() -> mainFrame.setVisible(false));
            CategoryEditor.getInstanceAndSetVisible();
        }
    }

    private void createOrRefreshEventTable(boolean reloadFromDb) {
        selectedRow = -1;

        if (reloadFromDb && eventList != null) {
            eventList.reloadFromDb();
        }

        eventList = globalState.getEventList();
        if (eventTableModel != null) {
            eventTableModel.fireTableDataChanged();
        }
        if (eventTableModel == null
                || eventTableModel.getRowCount() != eventList.getEvents().size()) {
            System.out.println("ScoreEditor: Loading events table completely new.");
            eventTableModel = eventList.getEventsAsTableModel();
            eventTable.setModel(eventTableModel);
        }
    }

    private void showRanking() {
        globalState.setWebsiteHasBeenOpened(true);
        try {
            UrlOpener.openWebpage(new URL("http://" + globalState.getIpOfComputer() + ":" + globalState.getHttpPort()));
        } catch (MalformedURLException e) {
            // Should not happen.
            System.out.println("URL of web server is malformed.");
            e.printStackTrace();
        }
    }
}
