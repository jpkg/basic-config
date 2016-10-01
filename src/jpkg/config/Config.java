package jpkg.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jpkg.pair.Pair;

public class Config {

	private Map<String, String> configvalues = new HashMap<>();

	/**
	 * The default character used to denote comments
	 */
	public static final transient char COMMENT = '#';

	/**
	 * The default character used to denote assignment
	 */
	public static final transient char DEF = '=';

	/**
	 * Populate this Config using a Scanner
	 * 
	 * @param sc
	 */
	public void populate(Scanner sc) {
		while (sc.hasNext()) {
			parseAndAdd(sc.nextLine());
		}
	}

	/**
	 * Populate this Config using a BufferedReader
	 * 
	 * @param sc
	 */
	public void populate(BufferedReader sc) {
		Stream<String> s = sc.lines();

		s.forEach(new Consumer<String>() {
			@Override
			public void accept(String arg0) {
				parseAndAdd(arg0);
			}
		});
	}
	
	/**
	 * Populate Config with file
	 * @param f
	 * @param filenotfoundmsg
	 */
	public void populate(File f) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			populate(br);
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Config file " + f.toString() + " not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a line in the format `key=value` to this Config
	 * 
	 * @param line
	 * @throws IllegalArgumentException In the event the line is incorrectly formatted
	 */
	public void parseAndAdd(String line) throws IllegalArgumentException {
		Pair<String, String> p = getEntryFromString(line);
		
		if(p == null)
			return;
		
		addEntry(p);
	}

	/**
	 * Given a line in the format `key=value`, this method discards any comments and 
	 * returns a Pair<key, value>. 
	 * @param entry
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Pair<String, String> getEntryFromString(String entry)
			throws IllegalArgumentException {

		// Parse comments, if there are any
		int commentindex = entry.indexOf(COMMENT);

		if (commentindex != -1) {
			entry = entry.substring(0, commentindex);
		}

		if (isBlank(entry)) {
			return null;
		}

		int index = entry.indexOf(DEF);

		// Throw exception if no `=` found
		if (index == -1) {
			throw new IllegalArgumentException("Incorrectly formatted entry!");
		}

		String name = entry.substring(0, index).trim();
		String value = entry.substring(index + 1, entry.length()).trim();

		return new Pair<>(name, value);
	}

	/**
	 * Add an entry with given name and value to the internal map.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean addEntry(String name, String value) {
		if (configvalues.containsKey(name)) {
			return false;
		} else {
			configvalues.put(name, value);
			return true;
		}
	}

	private boolean isBlank(String e) {
		for (char c : e.toCharArray()) {
			if (!Character.isWhitespace(c))
				return false;
		}

		return true;
	}

	/**
	 * Add an entry from a given Pair<key, value> to the internal map.
	 * 
	 * @param val
	 * @return
	 */
	public boolean addEntry(Pair<String, String> val) {
		return addEntry(val.getLeft(), val.getRight());
	}

	/**
	 * Get the config value for a specified key
	 * 
	 * @param cfgvalue
	 * @return The retrieved value, of null if not registered
	 */
	public String getConfigFor(String cfgvalue) {
		return configvalues.get(cfgvalue);
	}

	/**
	 * Get the config value for a specified key, or defaults
	 * 
	 * @param cfgvalue
	 * @param def Default value
	 * @return The retrieved value, of null if not registered
	 */
	public String getConfigForOrDefault(String cfgvalue, String def) {
		return configvalues.getOrDefault(cfgvalue, def);
	}

	/**
	 * Get the integer config value for a specified key
	 * 
	 * @param cfgvalue
	 * @return 
	 * @throws IllegalArgumentException If specified key is not found
	 */
	public int getIntConfigFor(String cfgvalue) {
		String s = getConfigFor(cfgvalue);
		
		if(s == null)
			throw new IllegalArgumentException("Key `" + cfgvalue + "` not found!");
		
		return Integer.parseInt(s);
	}

	/**
	 * Get the integer config value for a specified key, defaulting if not found
	 * 
	 * @param cfgvalue
	 * @param def Default value
	 * @return 
	 * @throws IllegalArgumentException If specified key is not found
	 */
	public int getIntConfigForOrDefault(String cfgvalue, int def) {
		String s = getConfigFor(cfgvalue);
		
		if(s == null)
			return def;
		
		return Integer.parseInt(s);
	}

}
