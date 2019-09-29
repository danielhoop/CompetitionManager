package ch.danielhoop.utils;


import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/**
 * ExceptionVisualizer takes different kinds of exceptions and shows them in a JOptionPane as information.
 * Optionally, own messages can be added to make debugging easier. 
 * 
 * @author Daniel Hoop
 * @version 2018.02.24
 */
public class ExceptionVisualizer {
	public static void showAndAddMessage(Exception e, String message){
		SwingUtilities.invokeLater( () -> JOptionPane.showMessageDialog(null, message + e.getMessage(), getExceptionType(e), JOptionPane.INFORMATION_MESSAGE) );
		e.printStackTrace();
	}
	public static void showWithOwnMessage(Exception e, String message){
		SwingUtilities.invokeLater( () -> JOptionPane.showMessageDialog(null, message, getExceptionType(e), JOptionPane.INFORMATION_MESSAGE) );
		e.printStackTrace();
	}
	public static void show(Exception e){
		SwingUtilities.invokeLater( () -> JOptionPane.showMessageDialog(null, e.getMessage(), getExceptionType(e), JOptionPane.INFORMATION_MESSAGE) );
		e.printStackTrace();
	}
	private static String getExceptionType(Exception e){
		return e.getClass().getName();
	}
}
