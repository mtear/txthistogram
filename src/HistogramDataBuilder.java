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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
				WordCounter.countWordsInTextFiles(
				RecursiveFileReader.findAllFilesRecursive(
						new File(mPath),
						FileUtil.TXT_EXTENSION
				))),interval);
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
	
}
