package ch.danielhoop.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to interpret command line arguments and convert them into a Java Object
 * @author Daniel Hoop
 * @version 2018.02.06
 */
public class ArgumentInterpreter {

	private final String doubleHyphenRegex = "(\\-\\-)[^0-9].*";
	private final String singleHyphenRegex = "(\\-)[^-0-9].*";
	private Map<String, String> map;
	private String[] minimumArgs, maximumArgs, missingArgs, tooManyArgs;
	private boolean allowEmptyArgs, allowSingleHyphen, caseSensitiveSingleHyphen, allowDoubleHyphen, caseSensitiveDoubleHyphen;

//	public static void main(String[] args) {
//		String[] testStr = new String[]{
//				"-bA", "false",
//				"--Argument1", "true"};
//		ArgumentInterpreter testMap = new ArgumentInterpreter(null, new String[]{"a"}, true, true, true, true, false).readArgs(testStr);
//	}

	/**
	 *
	 *
	 *
	 * param allowEmptyArgs If true, then empty arguments without a value are allowed. Instead of "--verbose true" you can then use "--verbose".
	 * @param minimumArgs The minimum required arguments that must be given.
	 * @param maximumArgs The maximum arguments that can be given.
	 * @param allowEmptyArgs Boolean value indicating if empty arguments (i.e. --verbose) without a following text should be allowed
	 * @param allowSingleHyphen If true, then arguments like '-a' will be accepted. Notice that '-ab value' will be sliced into two arguments 'a'->'' and 'b'->'value'.
	 * @param caseSensitiveSingleHyphen Boolean value indicating if single hyphen arguments should be case sensitive.
	 * @param allowDoubleHyphen If true, then arguments like '--arg' will be accepted.
	 * @param caseSensitiveDoubleHyphen Boolean value indicating if double hyphen arguments should be case sensitive.
	 */
	public ArgumentInterpreter(
			String[] minimumArgs, String[] maximumArgs,
			boolean allowEmptyArgs,
			boolean allowSingleHyphen, boolean caseSensitiveSingleHyphen,
			boolean allowDoubleHyphen, boolean caseSensitiveDoubleHyphen
			) {
		// Initialize empty.
		this.map = new HashMap<>();
		this.missingArgs = new String[0];
		this.tooManyArgs = new String[0];
		// Settings to class attributes
		this.allowEmptyArgs = allowEmptyArgs;
		this.allowSingleHyphen = allowSingleHyphen;
		this.allowDoubleHyphen = allowDoubleHyphen;
		this.caseSensitiveSingleHyphen = caseSensitiveSingleHyphen;
		this.caseSensitiveDoubleHyphen = caseSensitiveDoubleHyphen;
		// Make case insensitive, depending on settings.
		this.minimumArgs = makeCaseInsensitive(minimumArgs);
		this.maximumArgs = makeCaseInsensitive(maximumArgs);
	}
	
	/**
	 * Method to interpret command line arguments and convert them into a Map.
	 * @param args Array of arguments as given by the command line. Arguments can look like -asdf OR --asdf
	 * @return A map that contains the name of each given argument (key) with corresponding value. Note that - and -- will not be contained in the keys.
	 */
	public ArgumentInterpreter readArgs(String[] args) {
		// Initialize again
		this.map = new HashMap<>();
		this.missingArgs = new String[0];
		this.tooManyArgs = new String[0];
		
		// Check for minimum arguments.
		if (minimumArgs != null && minimumArgs.length > 0 && args.length == 0)
			throw new IllegalArgumentException("Please provide at least the following arguments: " + arrayToString(minimumArgs));

		// Error check for number of args.
		if (args.length>1 &&
				!((allowDoubleHyphen && args[0].matches(doubleHyphenRegex)) || (allowSingleHyphen && args[0].matches(singleHyphenRegex))))
			throw new IllegalArgumentException("If more than 1 word is given as argument, the first word must look like '-a' or '--argName'.");

		// Make argument mapping.
		int nKeys = 0;
		int nValues = 0;
		String lastKey = "";
		for (int i=0; i<args.length; i++) {
			if (allowSingleHyphen && args[i].matches(singleHyphenRegex)) {
				nValues = 0;
				for (int j=1; j<args[i].length(); j++){ // Start at 1. Exclude "-" at beginning of argument. 
					nKeys++;
					lastKey = args[i].substring(j, j+1); // Only one character at once.
					if (containsKey(lastKey))
						throw new IllegalArgumentException("Double keys are not allowed. This key was given more than once: " + lastKey);
					put(lastKey, ""); // In case no argument will be given later.
				}
			} else if (allowDoubleHyphen && args[i].matches(doubleHyphenRegex)) {
				nValues = 0;
				nKeys++;
				lastKey = args[i].substring(2); // Exclude "--" at beginning of argument.
				if (containsKey(lastKey))
					throw new IllegalArgumentException("Double keys are not allowed. This key was given more than once: " + lastKey);
				put(lastKey, ""); // In case no argument will be given later. 
			} else {
				nValues++;
				nKeys = 0;
				put(lastKey, args[i]);
			}
			if (nKeys > 1 && !allowEmptyArgs)
				throw new IllegalArgumentException("More than 1 key was given without a value in between. Like '--arg1 --arg2 value2'. You may have forgotten double hyphen '--' before an argument?");
			if (nValues > 1)
				throw new IllegalArgumentException("More than 1 value were given without a key in between. Like '--arg1 value1 value2'.");
		}
		
		// Check for minimum arguments
		String errorMsgCaseSensitivity = "";
		if (!caseSensitiveSingleHyphen && !caseSensitiveDoubleHyphen) {
			errorMsgCaseSensitivity = " (note that names of single-hyphen and double-hypen arguments are not case sensitive) ";
		} else if (!caseSensitiveSingleHyphen) {
			errorMsgCaseSensitivity = " (note that names of single-hyphen arguments are not case sensitive) ";
		} else if (!caseSensitiveDoubleHyphen) {
			errorMsgCaseSensitivity = " (note that names of double-hyphen arguments are not case sensitive) ";
		}
		if (!areMiminumArgumentsProvided()) {
			throw new IllegalArgumentException("Please provide at least the following arguments" + errorMsgCaseSensitivity + ": " + arrayToString(minimumArgs)
			+ "\nThese arguments are missing: " + arrayToString(missingArgs));
		}
		// Check for maximum arguments
		if (areTooManyArgumentsProvided()) {
			throw new IllegalArgumentException("Unused arguments were provided. Typo? Check these argument names" + errorMsgCaseSensitivity + ": " + arrayToString(tooManyArgs));
		}
		// Return reference to object.
		return this;
	}
	
	public boolean argIsSetEmptyOrTrue(String argName) {
		argName = makeCaseInsensitive(argName);
		return map.containsKey(argName) && (map.get(argName).equals("") || map.get(argName).toLowerCase().equals("true"));
	}
	public boolean argIsSetFalse(String argName) {
		argName = makeCaseInsensitive(argName);
		return map.containsKey(argName) && map.get(argName).toLowerCase().equals("false");
	}
	public boolean argIsSet(String argName) {
		return containsKey(argName);
	}
	private String arrayToString(String[] array){
		if (array.length == 0) return "";
		
		StringBuilder b = new StringBuilder();
		for (int i=0; i<array.length; i++) {
			b.append(array[i]);
			if (i < array.length-1 )
				b.append(", ");				
		}
		return b.toString();
	}
	private boolean areMiminumArgumentsProvided(){
		if (minimumArgs == null || minimumArgs.length==0)
			return true;
		boolean allProvided = true;

		List<String> m = new ArrayList<String>();
		List<String> k = new ArrayList<String>(map.keySet());
		for (int i=0; i<minimumArgs.length; i++) {
			if (!k.contains(minimumArgs[i])) {
				m.add( minimumArgs[i] );
				allProvided = false;
			}
		}
		missingArgs = m.toArray(new String[]{});
		return allProvided;
	}
	private boolean areTooManyArgumentsProvided(){
		if (maximumArgs == null || maximumArgs.length==0)
			return false;
		boolean tooManyProvided = false;

		List<String> t = new ArrayList<String>();
		List<String> k = new ArrayList<String>(map.keySet());
		List<String> m = Arrays.asList(maximumArgs);
		for (int i=0; i<k.size(); i++) {
			if (!m.contains(k.get(i))) {
				t.add(k.get(i));
				tooManyProvided = true;
			}
		}
		tooManyArgs = t.toArray(new String[]{});
		return tooManyProvided;
	}
	// String case sensitivity methods.
	private String makeCaseInsensitive(String s) {
		if (s == null)
			return s;
		if ((!caseSensitiveSingleHyphen && s.length() == 1) || (!caseSensitiveDoubleHyphen && s.length() > 1)) {
			return s.toLowerCase();
		}
		return s;
	}
	private String[] makeCaseInsensitive(String[] s) {
		if (s == null)
			return s;
		for (int i=0; i<s.length; i++){
			if ((!caseSensitiveSingleHyphen && s[i].length() == 1) || (!caseSensitiveDoubleHyphen && s[i].length() > 1)) {
				s[i] = s[i].toLowerCase();
			}
		}
		return s;
	}
	// Methods from map, slightly adapted
	public void put(String key, String value) {
		map.put(makeCaseInsensitive(key), value);
	}
	public String get(String key) {
		return map.get(makeCaseInsensitive(key));
	}
	public Set<String> keySet() {
		return map.keySet();
	}
	private boolean containsKey(String key) {
		return map.containsKey(makeCaseInsensitive(key));
	}
	// Get whole map
	public Map<String, String> getArgMap() {
		return map;
	}
}
