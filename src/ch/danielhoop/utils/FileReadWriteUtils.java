package ch.danielhoop.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * FileReadWriteUtils is a utility class to read and write data to/from different object structures such as HashMaps.
 * 
 * @author Daniel Hoop
 * @version 2017.06.04
 */
public class FileReadWriteUtils {
	private String fileName;

	public FileReadWriteUtils(String fileName){
		this.fileName = fileName;
	}

	public void writeArrayToFile (String[] x) throws IOException {
		BufferedWriter outputWriter;
		outputWriter = new BufferedWriter(new FileWriter(fileName));
		for (int i = 0; i < x.length; i++) {
			if(i!=0) outputWriter.newLine();
			outputWriter.write(x[i]+"");
		}
		outputWriter.flush();  
		outputWriter.close();
	}

	public <T> T readFile(T classType) throws FileNotFoundException, IOException {
		return readFile(classType, "\n", "=", false);
	}
	public <T> T readFile(T classType, String rowSeparator, String colSeparator) throws FileNotFoundException, IOException {
		return readFile(classType, rowSeparator, colSeparator, false);
	}
	public <T> T readFile(T classType, String rowSeparator, String colSeparator, boolean verbose) throws FileNotFoundException, IOException {		
		return readFile(classType, rowSeparator, colSeparator, verbose);
	}
	
	
	public <K,V> HashMap<K,V> readFile(HashMap<K,V> classType, String rowSeparator, String colSeparator, boolean verbose) throws FileNotFoundException, IOException {
		return( readStringToHashMap(classType, readFile(verbose), rowSeparator, colSeparator, verbose) );
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public <K,V> HashMap<K,V> readStringToHashMap(HashMap<K,V> classType, String str, String rowSeparator, String colSeparator, boolean verbose) {
		int rowSepInd = str.indexOf(rowSeparator);
		int colSepInd = -1;
		String partStr = "";
		HashMap<K,V> map = new HashMap<K,V>();
		// Loop until there are no more new lines
		while(rowSepInd != -1){
			partStr = str.substring(0,rowSepInd);
			colSepInd = partStr.indexOf(colSeparator);
			if(colSepInd != -1){
				map.put( (K) partStr.substring(0,colSepInd),  	(V) partStr.substring(colSepInd+1,partStr.length()));
			}
			if(rowSepInd+1 >= str.length()-1) break;
			str = str.substring(rowSepInd+1, str.length());
			rowSepInd = str.indexOf(rowSeparator);
		}
		// Optionally show the HashMap
		if(verbose){
			System.out.println("-- Map entries:");
			for(K s : map.keySet()){
				System.out.println(s + " : " + map.get(s));
			}
		}
		return map;
	}

	
	public String readFile() throws FileNotFoundException, IOException {
		return readFile(false);
	}
	public String readFile(boolean verbose) throws FileNotFoundException, IOException {
		BufferedReader br;

		br = new BufferedReader( new InputStreamReader( new FileInputStream(fileName) ) );
		StringBuilder builder = new StringBuilder();
		String currentLine; 
		while ((currentLine = br.readLine()) != null) {
			builder.append(currentLine);
			builder.append("\n");
		}
		br.close(); br = null;
		String str = builder.toString();
		
		if(verbose){
			System.out.println("-- Raw file content.");
			System.out.println(str);				
		}
		
		return str;
	}
	
	public void writeSerializable(Object o) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(o);
		oos.close();
		fos.close();
	}
	
	@SuppressWarnings("unused")
	public <T> T readSerializable(T classType) throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fis = new FileInputStream(fileName);
         ObjectInputStream ois = new ObjectInputStream(fis);
         @SuppressWarnings("unchecked")
         T result = (T) ois.readObject();
         ois.close();
         fis.close();
         return result;
	}
}
