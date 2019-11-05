package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CompetitorEditor {
    private JPanel outerPanel;
    private JButton navigateToScoreEditor;
    private JButton navigateToEventSelector;
    private JButton einenNeuenTeilnehmerErstellenButton;
    private JLabel descriptionLabel;
    private JTextField competitorNameField;
    private JTextField competitorDateField;
    private JRadioButton maennlichRadioButton;
    private JRadioButton weiblichRadioButton;
    private boolean createNew;

    GlobalState globalState = GlobalState.getInstance();
    ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

    private CompetitorEditor(boolean createNew) {
        this.createNew = createNew;
        createUIComponents();
    }

    private void createUIComponents (){

        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        // TODO : Remove Later just for Testing/Building Reasons
        LocalDate localDate = LocalDate.of(1988,05,31);
        globalState.setCompetitor(new Competitor('5',"Ludwig", Gender.MALE, localDate, 31));
        /*Remove till here*/

        if (!createNew){
            Competitor editedCompetitor = globalState.getCompetitor();

            descriptionLabel.setText(bundle.getString("CompetitorEditor.descEditCompetitor") + editedCompetitor.getName());
            competitorNameField.setText(editedCompetitor.getName());
            String competitorBirthDate = dateStringConverter.asString(editedCompetitor.getDateOfBirth());
            competitorDateField.setText(competitorBirthDate);
            if(editedCompetitor.getGender() == Gender.FEMALE) {
                weiblichRadioButton.setSelected(true);
            } else if (editedCompetitor.getGender() == Gender.MALE){
                maennlichRadioButton.setSelected(true);
            }
        }
        maennlichRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (maennlichRadioButton.isSelected() == true){
                    weiblichRadioButton.setSelected(false);
                }
                maennlichRadioButton.setSelected(true);
            }
        });

        weiblichRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (weiblichRadioButton.isSelected() == true){
                    maennlichRadioButton.setSelected(false);
                }
                weiblichRadioButton.setSelected(true);
            }
        });



    }

    public static void main( boolean createNew) {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("CompetitorEditor.title"));
        frame.setContentPane(new CompetitorEditor(createNew).outerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
