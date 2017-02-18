/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/17/2017
 * 
 * RecursiveFileReader.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * A class for reading files from a directory.
 *
 */
public class RecursiveFileReader {

//*********************************************************____________________
//*****************STATIC METHODS**************************____________________
//*********************************************************____________________

	/**
	 * Find all files in a directory and all the directory's
	 * subdirectories of the chosen file extensions
	 * 
	 * Includes archive files as their contents will be read at a later
	 * stage.
	 * 
	 * @param rootfile The root directory to begin searching from
	 * @param A string array of file extensions to match against
	 * @return A list of all the .txt files and archive files found
	 */
	public static ArrayList<String> findAllFilesRecursive(File rootfile,
			String... fileExtensions)
	{
		ArrayList<String> textFiles = new ArrayList<String>();
		File[] files = null;
		try{
			files = rootfile.listFiles(); 
		}catch(Exception e){
			//A failure can occur if the program does not have read
			//priviledges to a folder. In that case as the function
			//is recursive we will return the empty list
			return textFiles;
		}

	    for (File file : files) {
	        if (file.isFile()){
	        	//Check if file ends with file extension
	        	boolean matches = false;
	        	for(String extension : fileExtensions){
	        		if(file.getName().endsWith(extension)){
	        			matches = true; break;
	        		}
	        	}
	        	//Add the file to the list if it is a file
	        	//and matches an extension
	            if(matches) textFiles.add(file.getAbsolutePath());
	        } else if (file.isDirectory()) {
	            textFiles.addAll(findAllFilesRecursive(file, fileExtensions));
	        }
	    }
	    
	    return textFiles;
	}
	
	/**
	 * Scans a zip file and all zip files inside that zip file contains
	 * for files of a chosen extension. Collects the contents of all those
	 * files as Strings and then returns them.
	 * 
	 * @param inStream An InputStream to a ZipEntry from a ZipFile
	 * @return A list of Strings of the content of the files in the zip and in
	 * nested zip archives
	 * @throws IOException Errors could occur during file reading
	 */
	public static ArrayList<String> scanZipRecursive(InputStream inStream,
			String fileExtension) throws IOException {
		ArrayList<String> contents = new ArrayList<String>();
	    ZipInputStream input = new ZipInputStream(inStream);
	    ZipEntry entry = null;
	    //Iterate over files in the zip archive
	    while ( (entry = input.getNextEntry()) != null ) {
	       if (entry.getName().endsWith(FileUtil.ZIP_EXTENSION)) {
	    	   //Recursive loop on other zip archives
	    	   contents.addAll(scanZipRecursive(input, fileExtension));
	       }else if (entry.getName().endsWith(fileExtension)){
	    	   //Read the file from the stream
	    	   byte[] inner = new byte[(int)entry.getSize()];
	    	   input.read(inner);
	    	   String s = new String(inner);
	    	   contents.add(s);
	       }
	    }
	    return contents;
	}
	
}
