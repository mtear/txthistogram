/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/17/2017
 * 
 * FileUtil.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
	
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
	 * Read an InputStream object into a String
	 * 
	 * @param inStream The input stream to read from
	 * @return A String of the input stream's contents
	 * @throws IOException An exception occurs if there was an error reading
	 * from the stream
	 */
	public static String inputStreamToString(InputStream inStream)
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
	public static String readFile(String fileName) throws IOException {
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
	
}
