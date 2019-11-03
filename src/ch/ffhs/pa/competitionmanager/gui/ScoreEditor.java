package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.GlobalState;

import javax.swing.*;
import java.util.ResourceBundle;

public class ScoreEditor {
    private JPanel outerPanel;
    private JLabel text1;
    private boolean createNew;

    private ScoreEditor(boolean createNew) {
        this.createNew = createNew;
        createUIComponents();
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = globalState.getGuiTextBundle();

        // Create text1 string, depending on whether time is relevant or points.
        String text1String = bundle.getString("ScoreEditor.text1.start");
        if (globalState.getEvent().isTimeRelevant()) {
            text1String += bundle.getString("ScoreEditor.text1.time");
        } else {
            text1String += bundle.getString("ScoreEditor.text1.points");
        }
        text1 = new JLabel(text1String);

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
