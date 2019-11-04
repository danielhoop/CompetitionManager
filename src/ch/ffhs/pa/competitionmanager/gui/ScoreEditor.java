package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.utils.DateStringHandler;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreEditor {

    CompetitorTableModel competitorTableModel;
    CompetitorList competitorList;
    TableRowSorter<CompetitorTableModel> competitorTableSorter;

    private JPanel outerPanel;
    private JLabel text1;
    private JTextField nameTextField;
    private JTextField dateOfBirthTextField;
    private JTable competitorTable;
    private JTextField timeNeededTextField;
    private JCheckBox checkBox1;
    private JButton saveButton;
    private JTextField pointsAchievedTextField;
    private JLabel timeNeededLabel;
    private JLabel pointsAchievedLabel;
    private JButton neueNWettkämpferInButton;
    private JButton reloadCompetitorsButton;
    private JScrollPane competitorScrollPane;
    private JButton wettkämpferInBearbeitenButton;
    private boolean createNew;

    private ScoreEditor(boolean createNew) {
        this.createNew = createNew;
        createUIComponents();
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        // Create text1 string, depending on whether time is relevant or points.
        // TODO: Does not work.
        String text1String = bundle.getString("ScoreEditor.text1.start");
        if (globalState.getEvent().isTimeRelevant()) {
            text1String += bundle.getString("ScoreEditor.text1.time");
        } else {
            text1String += bundle.getString("ScoreEditor.text1.points");
        }
        text1 = new JLabel(text1String);

        nameTextField = new JTextField();
        /*nameTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action performed in nameTextField.");
                filterCompetitorTable();
            }
        });*/
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("keyReleased in nameTextField.");
                filterCompetitorTable();
            }
        });

        // Table
        // TODO: Add a filter depending on the inputs in name of competitor / dateOfBirth / gender.
        // http://www.java2s.com/Tutorial/Java/0240__Swing/JTableFiltering.htm
        competitorList = globalState.getCompetitorList();
        competitorTableModel = competitorList.getCompetitorsAsTableModel();
        competitorTable = new JTable(competitorTableModel);
        // Sorter & filter
        competitorTableSorter = new TableRowSorter<CompetitorTableModel>(competitorTableModel);
        competitorTable.setRowSorter(competitorTableSorter);

        // Time needed or points achieved
        timeNeededLabel = new JLabel();
        timeNeededTextField = new JTextField();
        pointsAchievedLabel = new JLabel();
        pointsAchievedTextField = new JTextField();
        if (globalState.getEvent().isTimeRelevant()) {
            pointsAchievedLabel.setVisible(false);
            pointsAchievedTextField.setVisible(false);
        } else {
            timeNeededLabel.setVisible(false);
            timeNeededTextField.setVisible(false);
        }

        // Reload Competitors Button
        reloadCompetitorsButton = new JButton();
        reloadCompetitorsButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                globalState.reloadCompetitorListFromDb();
                competitorTableModel.fireTableDataChanged();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Button must either say "save" or "save changes".
        // TODO: Does not work.
        if (createNew) {
            saveButton = new JButton(bundle.getString("save"));
        } else {
            saveButton = new JButton(bundle.getString("change"));
        }
        saveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO: Save or adapt data.
                if (createNew) {

                } else {

                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    public static void main(boolean createNew, Object formToSetNull) {
        formToSetNull = null;

        SwingUtilities.invokeLater(() -> {
            ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

            JFrame frame = new JFrame(bundle.getString("ScoreEditor.title"));
            frame.setContentPane(new ScoreEditor(createNew).outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }


    /*// https://docs.oracle.com/javase/8/docs/api/javax/swing/RowFilter.html
    private class CompetitorRowFilter {
        private String text;
        private String date;
        public CompetitorRowFilter(String text, String date) {
            this.text = text;
            this.date = date;
        }
        private class StartsWithRowFilter<M, I> extends RowFilter<M, I> {
            @Override
            public boolean include(Entry<? extends M, ? extends I> entry) {
                for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                    if (text != "" &&
                            entry.getStringValue(i).startsWith(text) &&
                            date != "" &&
                            entry.getStringValue(i).startsWith(date)) {
                        return true;
                    }
                }
                // None of the columns start with "a"; return false so that this
                // entry is not shown
                return false;
            }
        }
    }*/

    private void filterCompetitorTable() {
        List<RowFilter<Object,Object>> filters = new LinkedList<RowFilter<Object,Object>>();
        // First, filter on name
        String name = nameTextField.getText().replaceAll("\\.", "\\.");
        String text = nameTextField.getText();
        //System.out.println(nameTextField.getText().replaceAll("\\.", "\\\\."));
        //System.out.println(text);

        // 2019-11-04: Debug here.
        System.out.println("String before replacing with ...: " + name);
        nameTextField.setText("XYZ");
        System.out.println("String after replacing with ...: " + name);
        try {
            // If current expression doesn't parse, don't update.
            filters.add(RowFilter.regexFilter(name, 0));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        // Then filter on birthday
        /*try {
            filters.add(RowFilter.dateFilter(
                    RowFilter.ComparisonType.EQUAL,
                    new DateStringHandler(GlobalState.getInstance().getLocale()).asDate(dateOfBirthTextField.getText()),
                    0));
        } catch (java.text.ParseException e) {
            return;
        }*/
        // Combine filters and apply. https://stackoverflow.com/questions/31372553/jtable-rowfilter-between-two-dates-same-column
        competitorTableSorter.setRowFilter(RowFilter.andFilter(filters));
    }
}
