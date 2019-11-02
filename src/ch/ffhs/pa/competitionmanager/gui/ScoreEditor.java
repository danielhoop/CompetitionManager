package ch.ffhs.pa.competitionmanager.gui;

import javax.swing.*;

public class ScoreEditor {

    private JLabel titleComp;
    private JTextArea nameComp;
    private JLabel generComp;
    private JLabel dateOfBirthComp;
    private JLabel ageComp;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setData(ScoreEditor data) {
    }

    public void getData(ScoreEditor data) {
    }

    public boolean isModified(ScoreEditor data) {
        return false;
    }
}
