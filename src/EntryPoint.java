/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/4/2017
 * 
 * EntryPoint.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.util.HashMap;

//*****************************************************************************
//*******************************CLASSES***************************************
//*****************************************************************************

/**
 * A class containing the entry point for the program.
 * 
 * This class does little more than handle the execution of the program
 * based on specified arguments.
 * 
 */
public class EntryPoint {
	
//*********************************************************____________________
//******************CLASS METHODS**************************____________________
//*********************************************************____________________
	
	/**
	 * The main entry point for the program.
	 * 
	 * If there are no arguments specified this prints the help text.
	 * If there are arguments specified this will parse them and perform
	 * a histogram calculation.
	 * 
	 * A PNG output can be specified with the -PNG flag.
	 * Otherwise a command line output will be all that's produced.
	 * 
	 * txthistogram directory [interval] [-PNG width height]
	 *
	 * @param args The arguments for the command line application
	 */
	public static void main(String[] args){
		//Print help if there are no arguments
		if(args.length == 0){
			printHelp();
		}else{
			//Default values
			int interval = 1;
			int width = 500, height = 500;
			String path = ".";
			boolean pngoutput = false;
			
			//Parse arguments
			try{
				if(args.length == 1){ 
					//txthistogram directory
					path = args[0];
				}else if(args.length == 2){ 
					//txthistogram directory interval
					path = args[0];
					interval = Integer.parseInt(args[1]);
				}else if(args.length == 4){ 
					//txthistogram directory -PNG width height
					path = args[0];
					if(!args[1].equals("-PNG")) throw new Exception();
					width = Integer.parseInt(args[2]);
					height = Integer.parseInt(args[3]);
					pngoutput = true;
				}else if(args.length == 5){ 
					//txthistogram directory interval -PNG width height
					path = args[0];
					interval = Integer.parseInt(args[1]);
					if(!args[2].equals("-PNG")) throw new Exception();
					width = Integer.parseInt(args[3]);
					height = Integer.parseInt(args[4]);
					pngoutput = true;
				}else{ //Unexpected input
					System.out.println("Unexpected arguments!");
					printHelp();
					System.exit(1);
				}
				//Account for bad size input
				if(width <= 0 || height <= 0) throw new Exception();
			}catch(Exception e){
				System.out.println("Unexpected arguments!");
				printHelp();
				System.exit(1);
			}
			
			//Get and print
			HashMap<Integer, Integer> histogram = 
					new HistogramDataBuilder(path).build(interval);
			HistogramDataBuilder.printHistogramData(histogram, interval);
			
			//Make PNG
			if(pngoutput){
				new PNGHistogramBuilder().build(path + "/output.png", width,
						height, histogram, interval);
			}
		}
	}
	
	/**
	 * Prints the help text for the command line arguments.
	 */
	public static void printHelp(){
		System.out.println("\n");
		System.out.println("Scan a directory and subdirectories for txt"
				+ " files. Archive files (zip) will be scanned as well.");
		System.out.println("A histogram will be generated in that directory"
				+ " as a PNG file if the -PNG flag is used.\n");
		System.out.println("Usage: txthistogram directory [interval] [-PNG width height]");
		System.out.println("directory: The root directory to scan for txt files");
		System.out.println("\n");
	}
	
}
