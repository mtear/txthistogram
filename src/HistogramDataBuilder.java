/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/4/2017
 * 
 * HistogramDataBuilder.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//*****************************************************************************
//*******************************CLASSES***************************************
//*****************************************************************************

/**
 * A class for building histogram data from a directory.
 * 
 * This class builds a word count HashMap and formats that data by a specified
 * interval value for use in plotting a visual histogram. The word counts are
 * populated from txt files and txt files inside of zip archives in a given
 * directory.
 * 
 */
public class HistogramDataBuilder {
	
//*********************************************************____________________
//****************STATIC CONSTANTS*************************____________________
//*********************************************************____________________
	
	/**
	 * A constant for the txt file extension
	 */
	final static String TXT_EXTENSION = ".txt";
	/**
	 * A constant for the zip file extension
	 */
	final static String ZIP_EXTENSION = ".zip";

//*********************************************************____________________
//*****************STATIC METHODS**************************____________________
//*********************************************************____________________
	
	/**
	 * This method prints out a command line version of a histogram.
	 * 
	 * The values in the histogram data are printed out at the data points
	 * they are linked to if the frequency is 1 (ex: [ 4 ] : 2) and according
	 * to the data ranges if
	 * the interval is greater than 1 (ex: [ 4 - 11 ] : 7).
	 *
	 * @param  histogram A HashMap object with histogram data stored in it
	 * @param  interval The interval length between histogram bars
	 */
	public static void printHistogramData(HashMap<Integer, Integer> histogram,
			int interval){
		//Get the interval keys from the histogram and sort them
		ArrayList<Integer> sortedKeys = new 
				ArrayList<Integer>(histogram.keySet());
		Collections.sort(sortedKeys);
	
		//Print the intervals to the standard output
		for(int i : sortedKeys){
			//Print the files that could not be opened if -1 word count
			if(i == -1){
				System.out.println("Files that could not be open: " + 
											histogram.get(-1) + "\n");
			}else{
				String prefix = "[ " + i + " ] : ";
				if(interval > 1) prefix = "[ " + i + " - " + 
											(i + interval-1) + " ] : ";
				System.out.println(prefix + histogram.get(i));
			}
		}
		
	}
	
//*********************************************************____________________
//******************CLASS FIELDS***************************____________________
//*********************************************************____________________
	
	/**
	 * A variable that holds the path to build the histogram from
	 */
	String mPath = "";
	
//*********************************************************____________________
//******************CONSTRUCTORS***************************____________________
//*********************************************************____________________
	
	/**
	 * The only constructor for the HistogramDataBuilder.
	 * 
	 * The path variable, in this iteration of the program, doesn't need to
	 * be a class field and would be better off as an argument to the build
	 * method. However, I chose to include it this way in case further added
	 * functionality would be easier to implement with a reference to the path.
	 * 
	 * @param path The root directory to word count txt files and build
	 * histogram data.
	 */
	public HistogramDataBuilder(String path){
		this.mPath = path;
	}
	
//*********************************************************____________________
//******************CLASS METHODS**************************____________________
//*********************************************************____________________
	
	/**
	 * Build the histogram word count data into a HashMap
	 * 
	 * This method will begin the process of counting all the words in .txt files
	 * in subdirectories and in the root directory of the path specified in the
	 * constructor.
	 * 
	 * This is the only public method to abstract the process to users of the code.
	 * 
	 * @param interval The spacing interval for the histogram data to be sorted.
	 * For example, if 5 is specified, 10-15 may be an interval where data is
	 * aggregated for the word count values in that range.
	 * @return A HashMap object with the frequency data from the .txt file word
	 * counts.
	 */
	public HashMap<Integer, Integer> build(int interval){
		return  separateHistogramIntervals(
				createHistogramData(
				countWordsInTextFiles(
				findAllTextFilesRecursive(
				new File(mPath)
				))),interval);
	}
	
	/**
	 * Count the word counts in the specified list of files.
	 * 
	 * This method will total up the word count values for all the files
	 * specified in the input argument. If an input file is an archived file
	 * (currently only a .zip file) then it will be searched through and
	 * all .txt files inside will be counted as well.
	 * 
	 * A polymorphic extension for how to handle .zip files, .tar files,
	 * or regular files could have been made for cleaner object-oriented
	 * design. I decided to go with an "if" structure for ease of
	 * implementation.
	 * 
	 * <b>If there is trouble reading from a file, -1 will be returned as
	 * the word count value for that file.</b>
	 * 
	 * @param files A list of files, .txt or archive, to perform a word
	 * count on
	 * @return A list containing how many words each found .txt file contained
	 */
	private ArrayList<Integer> countWordsInTextFiles(ArrayList<String> files){
		
		//Create a return array of word count values
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		//Iterate through all the files
		for(String f : files){
			
			//Handle zip files
			if(f.endsWith(".zip")){
				ZipFile zip = null;
				try{
				    zip = new ZipFile(f);
				    //Get entries in the zip file
				    Enumeration<? extends ZipEntry> zipFiles = zip.entries();
				    //Iterate through all files inside
				    while(zipFiles.hasMoreElements()){
				        ZipEntry entry = zipFiles.nextElement();
				        //If is a text file handle it
				        if(entry.getName().endsWith(TXT_EXTENSION)){
					        InputStream inStream = 
					        		zip.getInputStream(entry);
					        try{
					        	String zipFileWords =
					        				inputStreamToString(inStream);
						        counts.add(wordCountString(zipFileWords));
					        }catch(Exception e){
					        	System.err.println("There was an error reading"
					        			   + " from a txt file in a zip file: "
					        										+ f + "!");
					        	counts.add(-1);
					        }
				        }
				    }
				}catch(Exception e){ //Error reading from zip
					System.err.println("There was an error reading from "
												+ "zip file: " + f + "!");
				}finally{ //Close the zip archive
					try{
						zip.close();
					}catch(Exception e){
						System.err.println("Error closing zip stream!");
					}
				}
			}else{ //Handle txt files
				int c = -1;
				try{
					c = wordCountString(readFile(f));
				}catch(Exception e){
					System.err.println("There was an error reading from file: "
																	+ f + "!");
				}
				counts.add(c);
			}
			
		} //end for
		
		//Return the word counts
		return counts;
	}
	
	/**
	 * Sort a count of words into a frequency Map
	 * 
	 * @param wordCounts The word counts
	 * @return A HashMap with word counts as keys and occurrence frequencies
	 * as values
	 */
	private HashMap<Integer, Integer> createHistogramData(ArrayList<Integer
															 > wordCounts){
		HashMap<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		
		for(int c : wordCounts){
			if(!histogram.containsKey(c)){
				histogram.put(c, 1);
			}else{
				histogram.put(c, histogram.get(c) + 1);
			}
		}
		
		return histogram;
	}
	
	/**
	 * Find all the text files in a directory and all the directory's
	 * subdirectories.
	 * 
	 * Includes archive files as their contents will be read at a later
	 * stage.
	 * 
	 * @param rootfile The root directory to begin searching from
	 * @return A list of all the .txt files and archive files found
	 */
	private ArrayList<String> findAllTextFilesRecursive(File rootfile)
	{
		ArrayList<String> textFiles = new ArrayList<String>();
		File[] files = null;
		try{
			files = rootfile.listFiles(); 
		}catch(Exception e){
			//A failure can occur if the program does not have read
			//privledges to a folder. In that case as the function
			//is recursive we will return the empty list
			return textFiles;
		}

	    for (File file : files) {
	        if (file.isFile() &&
	        		(file.getName().endsWith(TXT_EXTENSION)
	        		|| file.getName().endsWith(ZIP_EXTENSION))) {
	            textFiles.add(file.getAbsolutePath());
	        } else if (file.isDirectory()) {
	            textFiles.addAll(findAllTextFilesRecursive(file));
	        }
	    }
	    
	    return textFiles;
	}
	
	/**
	 * Read an InputStream object into a String
	 * 
	 * @param inStream The input stream to read from
	 * @return A String of the input stream's contents
	 * @throws IOException An exception occurs if there was an error reading
	 * from the stream
	 */
	private String inputStreamToString(InputStream inStream)
										 throws IOException{
	    BufferedReader reader = new BufferedReader(
	    		new InputStreamReader(inStream));
	    try {
	        StringBuilder builder = new StringBuilder();
	        String line = reader.readLine();

	        while (line != null) {
	        	builder.append(line + "\n");
	            line = reader.readLine();
	        }
	        return builder.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	/**
	 * Read a file as a string.
	 * 
	 * An improvement could be made to account for file encodings.
	 * 
	 * @param fileName The file to be read
	 * @return A String of the file's contents
	 * @throws IOException An exception occurs if the file could not be read
	 */
	private String readFile(String fileName) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder builder = new StringBuilder();
	        String line = reader.readLine();

	        while (line != null) {
	        	//Append "\n" as .readLine() removed it
	        	builder.append(line + "\n");
	            line = reader.readLine();
	        }
	        return builder.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	/**
	 * Separates the histogram data by intervals and aggregates the data based
	 * on an interval.
	 * 
	 * For example, if an interval of 5 is specified, all data from 10-15 will
	 * be stored in the HashMap under the key "10" as an aggregate sum.
	 * 
	 * @param histogram The HashMap containing the histogram frequency values
	 * @param interval The interval to aggregate the data
	 * @return An updated histogram HashMap
	 */
	private HashMap<Integer, Integer> separateHistogramIntervals(
			HashMap<Integer, Integer> histogram, int interval){
		//Don't bother recalculating if the interval is 1
		if(interval == 1) return histogram;
		
		//Get the sorted interval keys
		int currentIntervalSet = 0;
		ArrayList<Integer> sortedKeys = 
				new ArrayList<Integer>(histogram.keySet());
		Collections.sort(sortedKeys);
		
		//Find start point where the histogram bars should start
		//Find the lowest non negative key
		int startPoint = (sortedKeys.get(0) == -1) ? sortedKeys.get(1) :
													  sortedKeys.get(0);
		while(currentIntervalSet + interval < startPoint)
			currentIntervalSet += interval;
		
		//Now that we have our start, sort counts into interval sections
		//Create our new Map
		HashMap<Integer, Integer> newHistogram = 
				new HashMap<Integer, Integer>();
		//Iterate through all the keys
		for(int c : sortedKeys){
			//Unaccessible files
			if(c == -1){
				newHistogram.put(-1, histogram.get(-1));
			}else{
				//If in the current interval
				while(c >= currentIntervalSet + interval){
					currentIntervalSet += interval;
					newHistogram.put(currentIntervalSet, 0);
				}
				//If added already increment
				if(newHistogram.containsKey(currentIntervalSet)){
					newHistogram.put(currentIntervalSet, 
							newHistogram.get(currentIntervalSet)
											 +histogram.get(c));
				} //Otherwise put a key with count value
				else{
					newHistogram.put(currentIntervalSet, histogram.get(c));
				}
			}
		}
		
		return newHistogram;
	}
	
	/**
	 * Count the words in a string.
	 * 
	 * @param s The string to count the words
	 * @return The word count as an integer
	 */
	private int wordCountString(String s){
		int count = 0;
		boolean inWord = false;
		
		//Keep a boolean flag of if the iteration is in a word
		//Increase the word count if in a word and then a non-word character
		//appears.
		
		for(int i = 0; i < s.length(); i++){
			if(Character.isLetter(s.charAt(i)) 
					|| Character.isDigit(s.charAt(i))){
				inWord = true;
			}else{
				if(inWord){
					count++;
					inWord = false;
				}
			}
		}
		//If in a word at the end of the string, count this word
		if(inWord) count++;
		
		return count;
	}
	
}
