package ch.danielhoop.sql;


public class SQLToolboxTutorial {

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater( () -> {
			try(
				SQLToolbox db = new SQLToolbox(
						"com.mysql.jdbc.Driver",
						"jdbc:mysql://danielhoop.goip.de:3306/lucius?autoReconnect=true&verifyServerCertificate=false&useSSL=true","dbs","db$s-ffh!s.FS2017", true, true)
				) {
				
				// No need to use openConnection(). Is already connected.
				

				// Execute simple query and show data.
				// For simple queries on a simple statement, you can simply use executeQuery and will get a DataFrame.
				DataFrame df = db.executeQuery("select * from person;");
				df.printTable();
				
				// Make prepared statements and execute.
				// Here you have to use the method SQLUtils.resultSetToDataFrame( ResultSet );
				java.sql.PreparedStatement prepst = db.getConnection().prepareStatement("select * from person where id < ?;");
				prepst.setInt(1, 5);
				SQLToolbox.resultSetToDataFrame( prepst.executeQuery() ).printTable();
				prepst.close();
				
				// Close the connection and the simple statement that was automatically created inside the SQLUtils object.
				db.closeConnection();

				// Create a simple JFrame with JTable. Add the data into the table.
				javax.swing.JFrame frame = new javax.swing.JFrame("SQLUtils tutorial");
				javax.swing.JPanel panel = new javax.swing.JPanel();
				javax.swing.JTable table = new javax.swing.JTable();
				javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
				java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
				scrollPane.setPreferredSize( new java.awt.Dimension(500,500) );
				panel.setLayout( new java.awt.GridBagLayout() );
				c.weightx = 1; c.weighty = 1; c.fill = java.awt.GridBagConstraints.BOTH;
				
				// Crucial lines below!
				table.setModel(new javax.swing.table.DefaultTableModel(
						df.getTable(), // Hand over the Object[][]  which contains the data from the query.
						df.getColnames() )); // Hand over the String[] which contains the colnames.
				// Crucial lines above!
				
				panel.add(scrollPane, c);
				frame.setContentPane(panel);
				frame.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE ); frame.pack(); frame.setLocationRelativeTo(null);
				frame.setVisible(true);

			} catch (Exception e) { e.printStackTrace(); }
		});

	}

}
