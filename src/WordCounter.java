/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/17/2017
 * 
 * WordCounter.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * A class that counts words in both txt files and zip files
 *
 */
public class WordCounter {
	
//*********************************************************____________________
//*****************STATIC METHODS**************************____________________
//*********************************************************____________________

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
	public static ArrayList<Integer> countWordsInTextFiles(
			ArrayList<String> files){
		
		//Create a return array of word count values
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		//Iterate through all the files
		for(String f : files){
			
			//Handle zip files
			if(f.endsWith(FileUtil.ZIP_EXTENSION)){
				ZipFile zip = null;
				try{
				    zip = new ZipFile(f);
				    ArrayList<String> contents = new ArrayList<String>();
				    //Get entries in the zip file
				    Enumeration<? extends ZipEntry> zipFiles = zip.entries();
				    //Iterate through all files inside
				    while(zipFiles.hasMoreElements()){
				        ZipEntry entry = zipFiles.nextElement();
				        if(entry.getName().endsWith(FileUtil.ZIP_EXTENSION)){
					        contents.addAll(
					        		RecursiveFileReader.scanZipRecursive(
					        			zip.getInputStream(entry),
					        			FileUtil.TXT_EXTENSION)
					        		);
				        }else if(entry.getName()
				        		.endsWith(FileUtil.TXT_EXTENSION)){
				        	contents.add(FileUtil.inputStreamToString(
				        			zip.getInputStream(entry)));
				        }
				    }
				    //Add word counts
				    for(String s : contents){
					    counts.add(wordCountString(s));
				    }
				}catch(Exception e){ //Error reading from zip
					System.err.println("There was an error reading from "
												+ "zip file: " + f + "!");
					e.printStackTrace();
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
					c = wordCountString(FileUtil.readFile(f));
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
	 * Count the words in a string.
	 * 
	 * @param s The string to count the words
	 * @return The word count as an integer
	 */
	private static int wordCountString(String s){
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
