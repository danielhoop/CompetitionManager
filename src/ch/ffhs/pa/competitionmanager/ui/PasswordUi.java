package ch.ffhs.pa.competitionmanager.ui;

import ch.ffhs.pa.competitionmanager.dto.DbCredentials;

import javax.swing.*;

public class PasswordUi {

    private DbCredentials dbCred;

    public PasswordUi(DbCredentials databaseCredentials) {
        // Option 3: Hidden characters in password field & NICE.
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Please enter password for database user '" + databaseCredentials.getUsername() + "':");
        JPasswordField pass = new JPasswordField(20);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Password required",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        if(option == 0) { // If OK button was pressed.
            databaseCredentials.setPassword(new String(pass.getPassword()));
        } else {
            System.exit(1);
        }
        // Option 1: Plain text password field.
        /*password = JOptionPane.showInputDialog();*/

        // Option 2: Hidden characters in password field, but not so nice.
            /*JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf,
                    "Enter database password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (okCxl == JOptionPane.OK_OPTION) {
                password = new String(pf.getPassword());
            }*/

        this.dbCred = databaseCredentials;
    }

    public DbCredentials getDbCredentials() {
        return dbCred;
    }
}
