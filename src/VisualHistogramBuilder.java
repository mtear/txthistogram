/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/4/2017
 * 
 * VisualHistogramBuilder.java
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
 * An abstract class for building a visual Histogram.
 * 
 * Contains an abstract method for the output of a Histogram. This class can
 * be inherited to provide functionality for all sorts of Histogram outputs
 * such as PNG or PDF.
 * 
 */
public abstract class VisualHistogramBuilder {
	
//*********************************************************____________________
//******************CLASS METHODS**************************____________________
//*********************************************************____________________

	
	/**
	 * Build a Histogram chart from the specified parameters.
	 * 
	 * @param file The file to name the histogram chart after the operation
	 * is completed
	 * @param width The output width in pixels of the histogram chart
	 * @param height The output height in pixels of the histogram chart
	 * @param histogram A HashMap object with histogram data
	 * @param interval A number specifying the interval range of the histogram
	 * data
	 * @return True if the chart was created successfully, false otherwise.
	 * 
	 */
	public abstract boolean build(String file, int width, int height,
			HashMap<Integer, Integer> histogram, int interval);
	
}
