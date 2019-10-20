
package ch.danielhoop.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ch.danielhoop.utils.ArgumentInterpreter;


/**
 * Decorator/wrapper class for data base basics.
 * @author Daniel Hoop
 *
 */
public class SQLToolbox implements AutoCloseable {
	private Connection conn;
	private Statement st;
	private ResultSet rs;
	private String pathToDriverJar, driverClassName, dbLoginAdress, dbUser, dbPassword;
	private boolean connect, dynamicDriver, verbose;
	
	/**
	 * The main function can be used from the command line to read a SQL file, and direct the output from the query (a 2 dimensional table) to a specified file.
	 * The command line arguments must look like: 
	 * --driverPath "driverPath" --driverName "driverClassName" --address "databaseAddress" --user "user" --password "password" --inputFile "inputSQLFile" --outputFile "outputDataFile" --columnSeparator "columnSeparatorForOutputFile" --nullValue "0"
	 * Optional arguments are:  --replaceRegex "anyRegexStringWillOnlyWorkForStingAndDateColumns" --deleteFile
	 * @param args
	 */
	public static void main(String[] args){
		// Display help
		if (args.length==0 || args[0].equals("-help") || args[0].equals("--help")) {
			System.out.println(
					"*******************\n"
					+ "*** SQL Toolbox ***\n"
					+ "*******************\n"
					+ "Daniel Hoop (daniel.hoop at gmx.net)\n"
					+ "\nMandatory arguments:"
					+ "\n  --driverPath \"driverPath\""
					+ "\n  --driverName \"driverClassName\""
					+ "\n  --address \"databaseAddress\""
					+ "\n  --user \"user\""
					+ "\n  --password \"password\""
					+ "\n  --inputFile \"inputSQLFile\""
					+ "\n  --outputFile \"outputDataFile\""
					+ "\n  --columnSeparator \"columnSeparatorForOutputFile\""
					+ "\n  --nullValue \"0\""
					+ "\nOptional arguments:"
					+ "\n  --replaceRegex \"anyRegexString - WillOnlyWorkForStingAndDateColumns\""
					+ "\n  --insertInsteadOfRegex \"someCharacterToReplaceTheRegexPattern\" default is a space \" \""
					+ "\n  --deleteFile"
					+ "\n  --writeToConsoleOnly"
					+ "\nExample:"
					+ "\n  --driverPath \"C:/Example/ojdbc6.jar\" --driverName \"oracle.jdbc.driver.OracleDriver\" --address \"jdbc:oracle:thin:@//www.databaseServer.com:1521/schemaName\" --user \"anyUser\" --password \"secret\" --inputFile \"C:/Example/sqlFile.sql\" --outputFile \"C:/Example/sqlFile.csv\" --columnSeparator \";\" --nullValue \"0\" --replaceRegex \"anyRegex\""
					+ "\nHints:"
					+ "\n  Strings that match --replaceRegex will be replaced with an empty character \"\"."
					+ "\n  --replaceRegex will only be applied to String and Date formatted columns."
					+ "\n  You don't have to include the column separator or the new line character \"\\n\" into the --replaceRegex. This is done automatically.");
			System.exit(0);
		}
		
		// Evaluate arguments and store in map
		// Only allow double hyphen arguments. Don't make case sensitive.
		ArgumentInterpreter args1 = new ArgumentInterpreter(
				new String[]{"driverPath","driverName","address","user","password","inputFile","outputFile","columnSeparator","nullValue"},
				new String[]{"driverPath","driverName","address","user","password","inputFile","outputFile","columnSeparator","nullValue", "replaceRegex", "insertInsteadOfRegex", "deleteFile", "writeToConsoleOnly"},
				true, false, true, true, false)
				.readArgs(args);
		boolean writeToConsoleOnly = args1.argIsSetEmptyOrTrue("writeToConsoleOnly");
		boolean verbose = !writeToConsoleOnly;
		boolean deleteInputFile = args1.argIsSetEmptyOrTrue("deleteInputFile");

		String insertInsteadOfRegex = args1.get("insertInsteadOfRegex");
		if (insertInsteadOfRegex == null) {
			insertInsteadOfRegex = " ";			
		}
		
		// Do SQL query and save to file.
		try (SQLToolbox db = new SQLToolbox(args1.get("driverPath"), args1.get("driverName"), args1.get("address"), args1.get("user"), args1.get("password"), true, verbose)  ) {	
			db.readSqlAndSaveData(args1.get("inputFile"), args1.get("outputFile"), args1.get("columnSeparator"), args1.get("nullValue"), args1.get("replaceRegex"), insertInsteadOfRegex, writeToConsoleOnly);
			if (deleteInputFile)
				 new File(args1.get("inputFile")).delete();
			if (verbose)
				System.out.println("Success!");
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | MalformedURLException | FileNotFoundException  e) {
			if (deleteInputFile) new File(args1.get("inputFile")).delete();
			e.printStackTrace(); // throw new RuntimeException(e);
			System.exit(1); // Sends exit status 1 to console.
		} catch (IOException e){
			if (deleteInputFile) new File(args1.get("inputFile")).delete();
			e.printStackTrace();
			System.exit(1);
		}
		
		System.exit(0);
	}

	/**
	 * Constructor for the case that the jdbc driver (jar) is included in the project.
	 * @see SQLToolbox(String pathToDriverJar, String driverClassName, String dbLoginAdress, String dbUser, String dbPassword, boolean connect)
	 */
	public SQLToolbox(String driverClassName, String dbLoginAdress, String dbUser, String dbPassword, boolean connect, boolean verbose)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		dynamicDriver = false;
		if (verbose) System.out.print("Loading database driver...");
		Class.forName(driverClassName).newInstance();
		if (verbose) System.out.println(" Done.");

		setBasics("", driverClassName, dbLoginAdress, dbUser, dbPassword, verbose);
		if (connect) { openConnection(); }
	}
	
	/**
	 * Constructor for the case that the jdbc driver (jar) is *not* included in the project.
	 * @param pathToDriverJar
	 * @param driverClassName
	 * @param dbLoginAdress
	 * @param dbUser
	 * @param dbPassword
	 * @param connect
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 */
	public SQLToolbox(String pathToDriverJar, String driverClassName, String dbLoginAdress, String dbUser, String dbPassword, boolean connect, boolean verbose)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException, FileNotFoundException {

		dynamicDriver = true;
		if (verbose) System.out.print("Loading database driver...");
		DynamicDriverLoader.registerDriver(pathToDriverJar, driverClassName);
		if (verbose) System.out.println(" Done.");

		setBasics(pathToDriverJar, driverClassName, dbLoginAdress, dbUser, dbPassword, verbose);
		if (connect) { openConnection(); }
	}
	
	
	public SQLToolbox copyInstance() {
		try {
			if (dynamicDriver){	return new SQLToolbox(pathToDriverJar, driverClassName, dbLoginAdress, dbUser, dbPassword, connect, verbose);
			} else {			return new SQLToolbox(driverClassName, dbLoginAdress, dbUser, dbPassword, connect, verbose); }
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException | FileNotFoundException | SQLException e) {
			// Usually cannot occur because the object was already instantiated without problems.
			e.printStackTrace();
		}
		return null;
	}
	
	private void setBasics(String pathToDriverJar, String driverClassName, String dbLoginAdress, String dbUser, String dbPassword, boolean verbose){
		this.pathToDriverJar = pathToDriverJar;
		this.driverClassName = driverClassName;
		this.dbLoginAdress = dbLoginAdress;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.verbose = verbose;
	}
	
	public void setVerbosity(boolean value){
		this.verbose = value;
	}
	
	public void openConnection()
			throws SQLException {
		if (conn == null || conn.isClosed()){
			if (verbose) System.out.print("Connecting to database...");
			conn = DriverManager.getConnection(dbLoginAdress, dbUser, dbPassword);
			conn.setAutoCommit(false);
			st = conn.createStatement();
			if (verbose) System.out.println(" Done.");
		} else {
			if (verbose) System.out.println("Connecting not necessary. Already connected!");
		}
	}

	public void closeConnection() {
		try {
			if (st != null) {
				st.close(); st = null;
			}
			if (conn != null){
				conn.close(); conn = null;
			}
			if (verbose) System.out.println("Connection & statement closed.");
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	// For Interface AutoCloseable
	@Override
	public void close() throws SQLException {
		closeConnection();
	}
	
	public Connection getConnection(){
		return conn;
	}

	public DataFrame executeQuery(String sql) throws SQLException {
		System.out.print("Executing query...");
		rs = st.executeQuery( sql );
		System.out.println(" Done.");
		
		return resultSetToDataFrame(rs, true);
	}
	
	public void executeUpdate(String sql) throws SQLException{
		st.executeUpdate(sql);
	}
	
	public static DataFrame resultSetToDataFrame(ResultSet rs) throws SQLException {
		return resultSetToDataFrame(rs, false);
	}
	
	private static DataFrame resultSetToDataFrame(ResultSet rs, boolean closeSetAndSetNull) throws SQLException {
		
		if (rs == null){
			System.out.println("ResultSet was null an therefore null was returned!");
			return null;
		}
		
		ResultSetMetaData meta = rs.getMetaData() ; 
		String[] colnames = new String[meta.getColumnCount()];
		for(int i=0; i<colnames.length; i++){
			colnames[i] = meta.getColumnName(i+1); // Attention. Starts with 1!
		}

		// Show classes in result set.
		//for(int c=0; c<colnames.length; c++){
		//	System.out.println( meta.getColumnClassName(c+1) + "\n" + meta.getColumnType(c+1) + "\n---------------------");
		//}

		ArrayList<Object[]> data = new ArrayList<>();
		Class<?>[] classes = new Class<?>[colnames.length]; 

		int r = -1;
		while(rs.next()){
			r++;
			data.add( new Object[colnames.length] );

			for(int c=0; c<colnames.length; c++){
				if (meta.getColumnClassName(c+1).toLowerCase().matches(".*integer")) {
					if (r==0) classes[c] = Integer.class;
					data.get(r)[c] = rs.getInt(c+1);
				}  else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*boolean")) {
					if (r==0) classes[c] = Boolean.class;
					data.get(r)[c] = rs.getBoolean(c+1);
				}  else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*short")) {
					if (r==0) classes[c] = Short.class;
					data.get(r)[c] = rs.getShort(c+1);
				}  else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*long")) {
					if (r==0) classes[c] = Long.class;
					data.get(r)[c] = rs.getLong(c+1);
				}  else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*double")) {
					if (r==0) classes[c] = Double.class;
					data.get(r)[c] = rs.getDouble(c+1);
				} else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*string")) {
					if (r==0) classes[c] = String.class;
					data.get(r)[c] = rs.getString(c+1);
				} else if (meta.getColumnClassName(c+1).toLowerCase().matches(".*date")) {
					if (r==0) classes[c] = Date.class;
					data.get(r)[c] = rs.getDate(c+1);
				} else {
					if (r==0) classes[c] = Object.class;
					data.get(r)[c] = rs.getObject(c+1);
				}
			}
		}
		if (closeSetAndSetNull) {
			rs.close();
			rs = null;
		}
		
		return new DataFrame( data.toArray(new Object[data.size()][colnames.length]), colnames, classes ); // data.get(0).length
		
	}

	
	// Methode zum Importieren eines SQL-Strings aus einer Datei
	public static String readFile(String path, java.nio.charset.Charset encoding)
		throws IOException {
			byte[] encoded = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
			return new String(encoded, encoding);
	}
	
	/**
	 * Method to read SQL query from a txt file and save the result to a csv file.
	 * @param infoText An information text that is printed to the console when the method is executed.
	 * @param sqlFileName The relative/absolute path where the txt file containing the SQL query is located.
	 * @param dataFileName The relative/absolute path where the file with the resulting data should be saved.
	 * @param columnSeparator The String to separate colums in the data file.
	 * @param fillNullValuesWith The String to replace null values. Can also be "null". But not null!
	 */
	public void readSqlAndSaveData(String sqlFileName, String dataFileName, String columnSeparator, String fillNullValuesWith, String replaceRegex, String insertInsteadOfRegex, boolean writeToConsoleOnlyNoFile) 
		throws SQLException, FileNotFoundException, UnsupportedEncodingException, IOException {
			String sqlString;
			ResultSet rs;
			boolean verbose = this.verbose && !writeToConsoleOnlyNoFile;
			
			if (verbose) System.out.print("Reading SQL...");
			sqlString = readFile(sqlFileName, java.nio.charset.Charset.defaultCharset());
			if (verbose) System.out.println(" Done.");
			if (verbose) System.out.print("Executing query...");
			rs = st.executeQuery( sqlString );
			if (verbose) System.out.println(" Done.");
			if (verbose) System.out.print("Replacing forbidden characters & saving to data file...");
			convertResultSetToFile(rs, dataFileName, columnSeparator, fillNullValuesWith, replaceRegex, insertInsteadOfRegex, writeToConsoleOnlyNoFile);
			if (verbose) System.out.println(" Done.");
			rs.close();
	}

	// Methode, um Daten in File zu schreiben.
	public static void convertResultSetToFile(ResultSet rs2, String path, String columnSeparator, String fillNullValuesWith, String replaceRegex, String insertInsteadOfRegex, boolean writeToConsoleOnlyNoFile)
			throws SQLException, FileNotFoundException {
		
		// final String regexBase = "\"|\\\\|\032|\n|" + columnSeparator;
		String regexBase = "";
		if (replaceRegex == null) {
			replaceRegex = "";
		}
		String regexFull;
		if (!replaceRegex.equals("") & !regexBase.equals("")) {
			regexFull = regexBase + "|" + replaceRegex;
		} else {
			regexFull = regexBase + replaceRegex;
		}
		boolean applyRegexAtAll = regexFull != "";
		
		PrintWriter csvWriter; 
		if (writeToConsoleOnlyNoFile){
			csvWriter = new PrintWriter(new OutputStreamWriter(System.out, java.nio.charset.Charset.defaultCharset()));
		} else {
			csvWriter = new PrintWriter(new File(path));
		}
		
		ResultSetMetaData meta = rs2.getMetaData();
		int numberOfColumns = meta.getColumnCount();
		
		// Only apply regex to Sting or Date.
		boolean[] noRegexApply = new boolean[numberOfColumns+1]; // default is false
		for (int i=1; i < 1+numberOfColumns; i++) {
			noRegexApply[i] = ! meta.getColumnClassName(i).toLowerCase().matches(".*(string|date)");
		}
		// Write column names
		StringBuilder row = new StringBuilder();
		String colName, colValue;
		for (int i=1; i < 1+numberOfColumns; i++) { 
			if (i > 1) {
				row.append(columnSeparator);
			}
			applyRegexToStringAndAddToRow(row, meta.getColumnName(i), fillNullValuesWith, regexFull, insertInsteadOfRegex, applyRegexAtAll);
		}
		csvWriter.println(row.toString()) ;
		// Write data row by row
		while (rs2.next()) {
			// Make first column separately, so you won't have to check for column separator every time.
			row = new StringBuilder();
			if (noRegexApply[1]) {
				row.append(nullToString(rs2.getString(1), fillNullValuesWith));
			} else {
				applyRegexToStringAndAddToRow(row, rs2.getString(1), fillNullValuesWith, regexFull, insertInsteadOfRegex, applyRegexAtAll);
			}
			// Columns 2 to ...
			for (int i=2; i < 1+numberOfColumns; i++) {
				row.append(columnSeparator);
				if (noRegexApply[i]) {
					row.append(nullToString(rs2.getString(i), fillNullValuesWith));
				} else {
					applyRegexToStringAndAddToRow(row, rs2.getString(i), fillNullValuesWith, regexFull, insertInsteadOfRegex, applyRegexAtAll);
				}
			}
			csvWriter.println(row.toString()) ;
		}
		csvWriter.close();
	}
	
	private static void applyRegexToStringAndAddToRow(StringBuilder sb, String addString, String fillNullValuesWith, String regexPattern, String insertInsteadOfRegex, boolean applyRegexAtAll) {
		String value = nullToString(addString, fillNullValuesWith);
		if (applyRegexAtAll) {
			value = value.replaceAll(regexPattern, insertInsteadOfRegex);
		}
		value = value.replaceAll("\"",  "\"\""); // Escape double quotes by adding another double quote.
		sb.append("\"").append(value).append("\""); // Wrap text y double quotes.
	}
	
	private static String nullToString(String s, String fillNullValuesWith){
		if (s == null) return fillNullValuesWith; else return s;
	}
}
