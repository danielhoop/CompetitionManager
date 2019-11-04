package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

public class ScoreEditor {

    CompetitorTableModel competitorTableModel;
    CompetitorList competitorList;

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

        // Table
        // TODO: Add a filter depending on the inputs in name of competitor / dateOfBirth / gender.
        // http://www.java2s.com/Tutorial/Java/0240__Swing/JTableFiltering.htm
        competitorList = globalState.getCompetitorList();
        competitorTableModel = competitorList.getCompetitorsAsTableModel();
        competitorTable = new JTable(competitorTableModel);

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
                //competitorTableModel.fireTableDataChanged();
                competitorTableModel = globalState.getCompetitorList().getCompetitorsAsTableModel();
                competitorTable = new JTable(competitorTableModel);
                competitorScrollPane.getViewport().add(competitorTable);
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
}
