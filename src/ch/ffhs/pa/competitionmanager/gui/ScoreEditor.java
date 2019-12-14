package ch.ffhs.pa.competitionmanager.gui;

import org.apache.commons.lang3.time.StopWatch;
import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


/**
 * Singleton.
 */
public class ScoreEditor {

    private static ScoreEditor scoreEditor = null;

    private GlobalState globalState;
    private ResourceBundle bundle;
    private CompetitorTableModel competitorTableModel;
    private CompetitorList competitorList;
    private TableRowSorter<CompetitorTableModel> competitorTableSorter;
    private Score scoreToEdit;
    private boolean editExisting;
    private JFrame mainFrame;

    private JPanel outerPanel;
    private JLabel text1;
    private JTextField nameTextField;
    private JTextField dateOfBirthTextField;
    private JTable competitorTable;
    private JTextField timeNeededTextField;
    private JCheckBox isValidCheckBox;
    private JButton saveButton;
    private JTextField pointsAchievedTextField;
    private JLabel timeNeededLabel;
    private JLabel pointsAchievedLabel;
    private JButton newCompetitorButton;
    private JButton reloadCompetitorsButton;
    private JScrollPane competitorScrollPane;
    private JButton editCompetitorButton;
    private JButton navigateToEventSelectorButton;
    private JButton timerButton;
    private JPanel scorePanel;

    private StopWatch stopWatch = new StopWatch();
    private Timer timer;
    private boolean isTimerRunning = false;
    private int selectedRow;

    public static ScoreEditor getInstanceAndSetVisible() {
        return getInstanceAndSetVisible(null, null);
    }

    public static ScoreEditor getInstanceAndSetVisible(Score scoreToEdit, Competitor competitor) {
        if (scoreEditor == null) {
            scoreEditor = ScoreEditor.main(true);
        } else {
            scoreEditor.mainFrame.setVisible(true);
            scoreEditor.reloadCompetitorsFromDb();
        }
        scoreEditor.setScore(scoreToEdit);
        scoreEditor.setCompetitor(competitor);
        return scoreEditor;
    }

    private static ScoreEditor main(boolean setVisible) {

        ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

        JFrame frame = new JFrame(bundle.getString("ScoreEditor.title"));
        ScoreEditor scoreEditor = new ScoreEditor(frame);
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(scoreEditor.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(setVisible);
        });
        return scoreEditor;
    }


    public static void focusOnScoreTextField() {
        if (scoreEditor != null) {
            GlobalState globalState = GlobalState.getInstance();
            if (globalState.getEvent().isTimeRelevant()) {
                scoreEditor.timeNeededTextField.requestFocus();
            } else {
                scoreEditor.pointsAchievedTextField.requestFocus();
            }
        }
    }

    private ScoreEditor(JFrame mainFrame) {
        this.scoreToEdit = null;
        this.editExisting = scoreToEdit != null;
        this.mainFrame = mainFrame;
        this.timer = new Timer(100, evt -> {
            timeNeededTextField.setText(stopWatch.toString());
        });
        createUIComponents();


        // Do not move the code below into createUIComponents() !
        globalState = GlobalState.getInstance();
        bundle = globalState.getGuiTextBundle();

        // Create text1 string, depending on whether time is relevant or points.
        SwingUtilities.invokeLater(() -> {
            String text1String = bundle.getString("ScoreEditor.text1.start");
            if (globalState.getEvent().isTimeRelevant()) {
                text1String += bundle.getString("ScoreEditor.text1.time");
            } else {
                text1String += bundle.getString("ScoreEditor.text1.points");
            }
            text1.setText(text1String);
        });

        // Add listeners to text fields.
        // It won't work in the createUIComponents() method!!!
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        dateOfBirthTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        timeNeededTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (Character.toUpperCase(e.getKeyChar()) == 'S') {
                    startStopTimer();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Table
        competitorList = globalState.getCompetitorList();
        competitorTableModel = competitorList.getCompetitorsAsTableModel();
        competitorTable.setModel(competitorTableModel);
        // Sorter & filter. See also filterCompetitorTable()
        competitorTableSorter = new TableRowSorter<CompetitorTableModel>(competitorTableModel);
        competitorTable.setRowSorter(competitorTableSorter);

        competitorTable.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = competitorTable.getSelectedRow();
        });

        // Time needed or points achieved
        SwingUtilities.invokeLater(() -> {
            if (globalState.getEvent().isTimeRelevant()) {
                pointsAchievedLabel.setVisible(false);
                pointsAchievedTextField.setVisible(false);
            } else {
                timeNeededLabel.setVisible(false);
                timeNeededTextField.setVisible(false);
            }
        });

        // Timer button
        timerButton.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                startStopTimer();
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
        timerButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                startStopTimer();
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

        // Reload Competitors Button
        reloadCompetitorsButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                reloadCompetitorsFromDb();
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });


        saveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean savingHasWorked = saveOrEditScore();
                if (savingHasWorked) {
                    // Success message, dispose old window and open new one.
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    clearAllFields();
                } // else { JOptionPane.showMessageDialog(null, bundle.getString("savingToDbFailed")); }
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

        editCompetitorButton.addActionListener(e -> {
            int selectedRow = getSelectedRowOfTable();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorNoCompetitorSelected"));
                return;
            }
            Competitor selectedCompetitor = competitorTableModel.getCompetitorFromRow(selectedRow).clone();
            CompetitorEditor.getInstanceAndSetVisible(selectedCompetitor);
            setInvisibleAndClearAllFields();
        });

        newCompetitorButton.addActionListener(e -> {
            CompetitorEditor.getInstanceAndSetVisible();
            setInvisibleAndClearAllFields();
        });

        // Navigate to event selector
        navigateToEventSelectorButton.addActionListener(e -> {
            setInvisibleAndClearAllFields();
            EventSelector.getInstanceAndSetVisible();
        });
    }

    private void createUIComponents() {
    }

    private void clearAllFields() {
        scoreToEdit = null;
        SwingUtilities.invokeLater(() -> {
            nameTextField.setText("");
            dateOfBirthTextField.setText("");
            competitorTable.clearSelection();
            competitorTableSorter.setRowFilter(null);
            competitorTableModel.fireTableDataChanged();

            timeNeededTextField.setText("");
            timeNeededTextField.setEditable(true);
            pointsAchievedTextField.setText("");
            isValidCheckBox.setSelected(true);
        });
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

    private void filterCompetitorTable() {
        List<RowFilter<Object, Object>> filters = new LinkedList<>();
        // First, filter on name
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String name = "(?i)" + java.util.regex.Pattern.quote(nameTextField.getText());
            filters.add(RowFilter.regexFilter(name, 0));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        // Then filter on birthday
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String dateOfBirth = java.util.regex.Pattern.quote(dateOfBirthTextField.getText());
            filters.add(RowFilter.regexFilter(dateOfBirth, 1));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }

        // Combine filters and apply. https://stackoverflow.com/questions/31372553/jtable-rowfilter-between-two-dates-same-column
        competitorTableSorter.setRowFilter(RowFilter.andFilter(filters));
        competitorTableModel.fireTableDataChanged();
    }

    private int getSelectedRowOfTable() {
        int selectedRow = -1;
        // Exception happens when competitorTable.getSelectedRow() is -1.
        try {
            selectedRow = competitorTable.convertRowIndexToModel(competitorTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = -1;
        }
        if (!editExisting && selectedRow == -1 && competitorTable.getRowCount() == 1) {
            selectedRow = competitorTable.convertRowIndexToModel(0);
        }
        return selectedRow;
    }

    public void reloadCompetitorsFromDb() {
        globalState.reloadCompetitorListFromDb();
        competitorTableModel.fireTableDataChanged();
    }

    private boolean saveOrEditScore() {
        boolean shouldContinue = true;
        boolean isValid = isValidCheckBox.isSelected();
        int selectedRow = getSelectedRowOfTable();

        // Continue only if a row is selected or only 1 row is displayed
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorNoCompetitorSelected"));
            shouldContinue = false;
        }

        // Continue only if time and double can be parsed.
        LocalTime timeNeeded = null;
        Double pointsAchieved = null;
        if (shouldContinue) {
            if (globalState.getEvent().isTimeRelevant()) {
                try {
                    timeNeeded = LocalTime.parse(timeNeededTextField.getText(), DateTimeFormatter.ISO_LOCAL_TIME);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorTimeNotParsed"));
                    shouldContinue = false;
                }
            } else {
                try {
                    pointsAchieved = Double.valueOf(pointsAchievedTextField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorPointsNotParsed"));
                    shouldContinue = false;
                }
            }
        }

        if (shouldContinue) {
            // Get competitor
            Competitor competitor;
            if (editExisting && selectedRow == -1) {
                competitor = scoreToEdit.getCompetitor();
            } else {
                competitor = competitorTableModel.getCompetitorFromRow(selectedRow);
                //System.out.println("Selected competitor: " + competitor.getName());
            }

            // Assert that competitor was not changed by by accident.
            if (editExisting) {
                if (scoreToEdit.getCompetitor() != competitor) {
                    int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("ScoreEditor.hintCompetitorIsNotTheSame"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    shouldContinue = shouldBeZero == 0;
                }
                if (shouldContinue) {
                    scoreToEdit.setCompetitor(competitor);
                    scoreToEdit.setTimeNeeded(timeNeeded);
                    scoreToEdit.setPointsAchieved(pointsAchieved);
                    scoreToEdit.setNumberOfTries(1);
                    scoreToEdit.setValid(isValid);
                    // Update in database.
                    return scoreToEdit.update();
                }
            } else {
                // Create object and save into database (score.create()).
                Score score = new Score(-1, globalState.getEvent().getId(), competitor, timeNeeded, null, 1, isValid, LocalDateTime.now());
                return score.create();
            }
        }
        return false;
    }

    private void setCompetitor(Competitor competitor) {
        if (competitor != null) {
            SwingUtilities.invokeLater(() -> {
                this.nameTextField.setText(competitor.getName());
                this.dateOfBirthTextField.setText(new DateStringConverter(globalState.getLocale()).asString(competitor.getDateOfBirth()));

                filterCompetitorTable();
                if (getSelectedRowOfTable() == -1) {
                    reloadCompetitorsFromDb();
                    filterCompetitorTable();
                }
            });
        }
    }

    private void setScore(Score score) {
        this.editExisting = score != null;
        this.scoreToEdit = score;

        // Button must either say "save" or "save changes".
        SwingUtilities.invokeLater(() -> {
            if (editExisting) {
                saveButton.setText(bundle.getString("change"));
            } else {
                saveButton.setText(bundle.getString("save"));
            }
        });
    }

    private void startStopTimer() {

        if (!isTimerRunning) {
            if (!timeNeededTextField.getText().equals(""))
                return;
            isTimerRunning = true;
            stopWatch.start();
            timer.start();
            SwingUtilities.invokeLater(() -> {
                String stopString = globalState.getGuiTextBundle().getString("ScoreEditor.timerButtonStop");
                timerButton.setText(stopString);
                competitorTable.setEnabled(false);
                outerPanel.setBackground(new java.awt.Color(154, 205, 50, 255));
            });

        } else {
            System.out.println("debug");
            isTimerRunning = false;
            stopWatch.stop();
            timer.stop();
            SwingUtilities.invokeLater(() -> {
                String startString = globalState.getGuiTextBundle().getString("ScoreEditor.timerButtonStart");
                timerButton.setText(startString);
                timeNeededTextField.setText(stopWatch.toString());
                timeNeededTextField.setEditable(false);
                competitorTable.setEnabled(true);
                outerPanel.setBackground(scorePanel.getBackground());
                // Reset must come after writing into timeNeededTextField. Otherwise it will be 00:00:00
                stopWatch.reset();
            });
        }
    }

}
