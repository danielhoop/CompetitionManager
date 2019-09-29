package ch.danielhoop.utils;

/**
* LookAndFeelUtils tries to set the System look and feel.
* If not available it tries to set the Nimbus look and feel. Otherwise the default look and feel is set.
*
* @author Daniel Hoop
* @version 2017.06.02
*/
public class GuiLookAndFeelUtils {

	public static void set() {
		// Setting Look and Feel
		try{
			// First try system Look and feel
			javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );
		} catch (Exception ex){
			// If system Look and Feel ist not available, try Nimbus, else keep the normal one.
			// For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
			try {
				for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
					System.out.println(info);
					if ("Nimbus".equals(info.getName())) { // Metal Nimbus
						javax.swing.UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception ex1) {
				java.util.logging.Logger.getLogger(javax.swing.JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex1);
			}
		}
	}
}
