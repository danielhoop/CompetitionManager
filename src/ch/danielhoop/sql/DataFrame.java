package ch.danielhoop.sql;

import java.util.ArrayList;

public class DataFrame {
	private final String[] colnames;
	private final boolean colnamesAvailable;
	private final Object[][] data;
	private final int[] allRows, allCols;
	private final int nrow, ncol;
	private final Class<?>[] classes;
	
	// Constructors
	public DataFrame(Object[][] data, String[] colnames, Class<?>[] classes) {
		nrow = data.length;
		ncol = nrow > 0  ?  data[0].length  :  0;
		
		if(ncol > 0  &&  colnames.length != data[0].length) {
			throwException( "*ERROR* colnames and array dimensions do not match:  colnames.length != data[0].length" );
		}
		if(ncol > 0  &&  classes.length != data[0].length) {
			throwException( "*ERROR* classes and array dimensions do not match:  classes.length != data[0].length" );
		}
		if(colnames.length != classes.length) {
			throwException( "*ERROR* colnames and classes do not match:  colnames.length != classes.length" );
		}
		this.data = data;
		this.colnames = colnames;
		this.colnamesAvailable = true; // data[0].length==colnames.length;
		this.classes = classes;
		
		allRows = new int[nrow];
		for(int i=0; i<allRows.length; i++) {
			allRows[i] = i;			
		}
		allCols = new int[ncol];
		for(int i=0; i<allCols.length; i++) {
			allCols[i] = i;			
		}
	}
	public DataFrame(Object[][] data, String[] colnames){
		this(data, colnames, new Class<?>[colnames.length]);
	}
	public DataFrame(Object[][] data) {
		this(data,
				new String[ data.length > 0  ?  data[0].length  :  0 ],
				new Class<?>[ data.length > 0  ?  data[0].length  :  0 ]);
	}
	
	// For all primitive constructors use toObject()
	public DataFrame(boolean[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(char[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(byte[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(short[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(int[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(long[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(float[][] data) { this(toObject(data), new String[data[0].length]); }
	public DataFrame(double[][] data) { this(toObject(data), new String[data[0].length]); }
	//
	public DataFrame(boolean[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Boolean.class, colnames.length) ); }
	public DataFrame(char[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Character.class, colnames.length) ); }
	public DataFrame(byte[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Byte.class, colnames.length) ); }
	public DataFrame(short[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Short.class, colnames.length) ); }
	public DataFrame(int[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Integer.class, colnames.length) ); }
	public DataFrame(long[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Long.class, colnames.length) ); }
	public DataFrame(float[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Float.class, colnames.length) ); }
	public DataFrame(double[][] data, String[] colnames){ this(toObject(data), colnames, makeClassArray(Double.class, colnames.length) ); }
	
	public String[] getColnames(){
		return colnames;
	}
	public String[] getColnames(int cols){
		return getColnames(new int[]{cols}) ;
	}
	public String[] getColnames(String[] cols){
		return getColnames( translateColnamesToIndices(cols) ) ;
	}
	public String[] getColnames(int[] cols){
		String[] newColnames = new String[cols.length];
		for(int c=0; c<cols.length; c++){
			newColnames[c] = colnames[cols[c]];		
		}
		return newColnames;
	}
	
	public Class<?>[] getClasses(){
		return classes;
	}
	public Class<?>[] getClasses(int cols){
		return getClasses(new int[]{cols}) ;
	}
	public Class<?>[] getClasses(String[] cols){
		return getClasses( translateColnamesToIndices(cols) ) ;
	}
	public Class<?>[] getClasses(int[] cols){
		Class<?>[] newClasses = new Class<?>[cols.length];
		for(int c=0; c<cols.length; c++){
			newClasses[c] = classes[cols[c]];		
		}
		return newClasses;
	}
	
	public int getNrow(){
		return data.length;
	}
	public int getNcol(){
		if(data.length > 0) {
			return data[0].length;
		} else {
			return colnames.length;
		}
	}
	
	
	// For arguments int, String
	public Object[][] getTable(int rows, String cols) {
		return getTable(new int[]{rows}, new String[]{cols});
	}
	public Object[][] getTable(int[] rows, String cols) {
		return getTable(rows, new String[]{cols});
	}
	public Object[][] getTable(int rows, String[] cols) {
		return getTable(new int[]{rows}, cols);
	}
	public Object[][] getTable(int[] rows, String[] cols) {
		return getTable(rows, translateColnamesToIndices(cols));
	}
	public Object[][] getTableFromRows(int[] rows) {
		return getTable(rows, allCols);
	}
	public Object[][] getTableFromCols(String[] cols) {
		return getTable(allRows, translateColnamesToIndices(cols));
	}

	
	// For arguments int, int
	public Object[][] getTable() {
		return data;
	}
	public Object[][] getTable(int rows, int cols) {
		return getTable(new int[]{rows}, new int[]{cols});
	}
	public Object[][] getTable(int[] rows, int cols) {
		return getTable(rows, new int[]{cols});
	}
	public Object[][] getTable(int rows, int[] cols) {
		return getTable(new int[]{rows}, cols);
	}
	public Object[][] getTableFromCols(int[] cols) {
		return getTable(allRows, cols);
	}
	public Object[][] getTable(int[] rows, int[] cols) {
		if(data.length==0) return new Object[0][0];
		
		Object[][] res = new Object[rows.length][cols.length];
		for(int r=0; r<rows.length; r++){
			for(int c=0; c<cols.length; c++){
				res[r][c] = data[rows[r]][cols[c]];
			}
		}
		return res;
	}
	
	
	// For arguments int, String
	public Object[] getVector(int[] rows, String cols){
		return getVector(rows, translateColnamesToIndices(cols));
	}
	public Object[] getVector(int rows, String[] cols){
		return getVector(rows, translateColnamesToIndices(cols));
	}
	public Object[] getVectorFromCol(String cols){
		return  getVector(allRows, translateColnamesToIndices(cols) );
	}
	
	
	// For arguments int, int
	public Object[] getVectorFromCol(int cols){
		return  getVector(allRows, cols);
	}
	public Object[] getVectorFromRow(int rows){
		return getVector(rows, allCols);
	}
	public Object[] getVector(int[] rows, int cols){
		Object[] res = new Object[rows.length];
		for(int r=0; r<rows.length; r++){
			res[r] = data[rows[r]][cols];
		}
		return res;
	}
	public Object[] getVector(int rows, int[] cols){
		Object[] res = new Object[cols.length];
		for(int c=0; c<cols.length; c++){
			res[c] = data[rows][cols[c]];
		}
		return res;
	}
	
	// For whole DataFrames
	// int int
	public DataFrame getDataFrameFromRows(int[] rows){
		return new DataFrame( getTableFromRows(rows), getColnames(), getClasses() );
	}
	public DataFrame getDataFrameFromCols(int[] cols){
		return new DataFrame( getTableFromCols(cols), getColnames(cols), getClasses(cols) );
	}
	public DataFrame getDataFrame(int[] rows, int[] cols){
		return new DataFrame( getTable(rows, cols), getColnames(cols), getClasses(cols) );
	}
	public DataFrame getDataFrame(int rows, int[] cols){
		return new DataFrame( getTable(rows, cols), getColnames(cols), getClasses(cols) );
	}
	public DataFrame getDataFrame(int[] rows, int cols){
		return new DataFrame( getTable(rows, cols), getColnames(cols), getClasses(cols) );
	}
	// int String
	public DataFrame getDataFrameFromCols(String[] cols){
		int[] colsInd = translateColnamesToIndices(cols);
		return new DataFrame( getTableFromCols(colsInd), getColnames(colsInd), getClasses(colsInd) );
	}
	public DataFrame getDataFrame(int[] rows, String[] cols){
		int[] colsInd = translateColnamesToIndices(cols);
		return new DataFrame( getTable(rows, colsInd), getColnames(colsInd), getClasses(colsInd) );
	}
	public DataFrame getDataFrame(int rows, String[] cols){
		int[] colsInd = translateColnamesToIndices(cols);
		return new DataFrame( getTable(rows, colsInd), getColnames(colsInd), getClasses(colsInd) );
	}
	public DataFrame getDataFrame(int[] rows, String cols){
		int colsInd = translateColnamesToIndices(cols);
		return new DataFrame( getTable(rows, colsInd), getColnames(colsInd), getClasses(colsInd) );
	}


	// Printing...
	public void printTable(){
		printTable(data, colnames, colnamesAvailable);
	}
	public void printTable(boolean printColnames){
		printTable(data, colnames, printColnames);
	}
	public static void printTable(Object[][] a){
		printTable(a, new Object[0], false);
	}
	public static void printTable(Object[][] a, Object[] colnames){
		printTable(a, colnames, true);
	}
	private static void printTable(Object[][] a, Object[] colnames, boolean printColnames){
		String addspace = " ";
		
		// Only print colnames, in case there are no rows.
		if(a.length==0 && printColnames){
			for(int j=0; j<colnames.length; j++){
				System.out.print(colnames[j] + addspace);
			}
			System.out.println("\n--- no rows to be printed! ---");
			
			// Print nothin, in case there are no rows and colnames shouldn't be printed.
		} else if (a.length==0 && !printColnames) {
			System.out.println("--- no rows to be printed! ---");
			
			// Else print full table.
		} else {
			if(printColnames && a[0].length != colnames.length){
				throwException("colnames are of wrong length! This condition must hold: a[0].length == colnames.length");
			}
				
			// Set all null to "", so you won't get a NullPointerException. loop is col by col.
			for(int j=0; j<a[0].length; j++){
				if(printColnames && colnames[j] == null) colnames[j] = "";
				
				for(int i=0; i<a.length; i++){
					if(a[i][j] == null) a[i][j] = "";
				}
			}
			// Count number of characters in all places of table. loop is col by col.
			int[] nd = new int[a[0].length];
			for(int j=0; j<a[0].length; j++){
				if(printColnames) nd[j] = Math.max(nd[j], colnames[j].toString().length());
				
				for(int i=0; i<a.length; i++){
					nd[j] = Math.max(nd[j], a[i][j].toString().length());  
				}  
			}
			// Add necessary whites paces before strings & print. loop is row by row.
			if(printColnames){
				for(int j=0; j<a[0].length; j++){
					System.out.print(repString(addspace, nd[j]-colnames[j].toString().length())  +  colnames[j]  +  addspace);
				}
				System.out.print("\n");
			}
			
			for(int i=0; i<a.length; i++){
				for(int j=0; j<a[0].length; j++){
					System.out.print(repString(addspace, nd[j]-a[i][j].toString().length())  +  a[i][j]  +   addspace);
					if(j==a[0].length-1) System.out.print("\n");
				}  
			}
		}
		
	}
	
	public static DataFrame combineDataFrames(DataFrame[] dfList) throws IllegalArgumentException {
		
		if( dfList.length == 0 ) {
			return null;
		}
		
		// Check which data frames are not null and nrow != 0.
		ArrayList<Integer> dataFrameNotNull= new ArrayList<>();
		ArrayList<Integer> dataFrameNotNullAndNrowGt0 = new ArrayList<>();
		for(int i=0; i<dfList.length; i++){
			if( dfList[i]!=null                              ) dataFrameNotNull.add(i);
			if( dfList[i]!=null   &&  dfList[i].getNrow()!=0 ) dataFrameNotNullAndNrowGt0.add(i);
		}
		if( dataFrameNotNull.size() == 0 ) return null;
		
		// Find the first data frame not null. This will be used to compare colnames.
		int minI = Integer.MAX_VALUE;
		for(int i : dataFrameNotNull){
			minI = Math.min(minI, i);
		}
		
		// Calculate the sum of all nrow in the data frames. Check if colnames and classes are equal for all data frames not null.
		// This is necessary if there are all with nrow 0.
		int nrowSum = 0;		
		for(int i : dataFrameNotNull){
			if(i>0) {
				if( !allElementsEqualOrNull( dfList[minI].getColnames(), dfList[i].getColnames() ) ) throw new RuntimeException("Colnames of DataFrame 0 and DataFrame "+i+" do not match!");
				if( !allElementsEqualOrNull( dfList[minI].getClasses(), dfList[i].getClasses() ) ) throw new RuntimeException("Classes of DataFrame 0 and DataFrame "+i+" do not match!");
				if( dfList[minI].getNrow() > 0 && dfList[minI].getNcol() != dfList[i].getNcol() )  throw new RuntimeException("Number of columns of DataFrame 0 and DataFrame "+i+" do not match!");
			}
			nrowSum += dfList[i].getNrow();
		}
		// If all nrow==0, then return a empty data frame. This is necessary here, because the loop below will not work.
		if( dataFrameNotNullAndNrowGt0.size() == 0 ){
			return new DataFrame(new Object[0][ dfList[minI].getColnames().length ], new String[]{"a","b","c"}, new Class<?>[]{String.class, String.class, String.class} );
		}
		
		// Fill the new data frame with all data and return.
		Object[][] resultData = new Object[nrowSum][dfList[minI].getNcol()];
		int rowCounter = 0; 
		for(int i : dataFrameNotNullAndNrowGt0){
			for(int j=0; j<dfList[i].getNrow(); j++){
				resultData[rowCounter] = dfList[i].getVectorFromRow(j);
				rowCounter++;
			}
		}
		
		return new DataFrame( resultData, dfList[minI].getColnames(), dfList[minI].getClasses() );
	}
	
	
	
	private static boolean allElementsEqualOrNull(Object[] a, Object[] b) throws IllegalArgumentException {
		if(a.length != b.length) return false;
		for(int i=0; i < a.length; i++){
			if( a[i]!=null && b[i]!=null && !a[i].equals(b[i]) ) return false;
		}
		return true;
	}
	
	public void printRow(int rows) {		
		if(colnamesAvailable) {
			printTable( getTable(rows, allCols), colnames );
		} else {
			printTable( getTable(rows, allCols) );
		}
	}
	public void printCol(int cols) {
		if(colnamesAvailable) {
			printTable( getTable(allRows, cols), new String[]{colnames[cols]} );
		} else {
			printTable( getTable(allRows, cols) );
		}
	}
	public void printCol(String cols){
		printCol( this.translateColnamesToIndices(cols) );
	}
	public static void printVector(Object[] a){
		printVector(a, "", false);
	}
	public static void printVector(Object[] a, Object colnames){
		printVector(a, colnames, true);
	}
	private static void printVector(Object[] a, Object colnames, boolean printColnames){
		Object[][] b = new Object[a.length][1];
		for(int i=0; i<a.length; i++){
			b[i][0] = a[i];
		}
		printTable(b, new Object[]{colnames}, printColnames);
	}
	

	private static String repString(String str, int times){
		StringBuilder res = new StringBuilder();
		for(int i=0; i<times; i++){
			res.append(str);
		}
		return res.toString();
	}
	
	private String colnameNotContained(String[] cols){
		boolean found;
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<cols.length; i++){
			found = false;
			for(int j=0; j<colnames.length; j++){
				if( cols[i].equals(colnames[j]) ) found = true;
			}
			if( !found ) sb = sb.append(cols[i]);
		}
		return sb.toString();
	}
	
	private int translateColnamesToIndices(String cols){
		return translateColnamesToIndices( new String[]{cols} )[0];
	}
	private int[] translateColnamesToIndices(String[] cols){
		String notContained = colnameNotContained(cols);
		if( !notContained.equals("") ) {
			throwException("Some chosen colnames are not contained in the DataFrame: " + notContained);
		}
		
		int[] colsInd = new int[cols.length];
		for(int i=0; i<cols.length; i++){
			for(int j=0; j<colnames.length; j++){
				if( cols[i].equals(colnames[j]) ) colsInd[i] = j;
			}
		}
		return colsInd;
	}
	
	// Constructor help classes
	private static Class<?>[] makeClassArray(Class<?> cl, int length){
		Class<?>[] classes = new Class<?>[length];
		for(int i=0; i<length; i++) classes[i] = cl;
		return classes;
	}
	private static Boolean[][] toObject(boolean[][] in){
		Boolean[][] out = new Boolean[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Character[][] toObject(char[][] in){
		Character[][] out = new Character[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Byte[][] toObject(byte[][] in){
		Byte[][] out = new Byte[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Short[][] toObject(short[][] in){
		Short[][] out = new Short[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Integer[][] toObject(int[][] in){
        Integer[][] out = new Integer[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Long[][] toObject(long[][] in){
		Long[][] out = new Long[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Float[][] toObject(float[][] in){
		Float[][] out = new Float[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	private static Double[][] toObject(double[][] in){
		Double[][] out = new Double[in.length][in[0].length];
        for(int i=0; i<in.length; i++) for(int j=0; j<in[0].length; j++) out[i][j] = in[i][j];
        return out;
    }
	
	// Exception handling
	private static void throwException(String message){
		throw new RuntimeException(message);
	}
}
