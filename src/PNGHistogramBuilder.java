/**
 * 
 * Author: Nicholas Wilson
 * Date: 2/4/2017
 * 
 * PNGHistogramBuilder.java
 * 
 */

//*****************************************************************************
//***************************IMPORTED LIBRARIES********************************
//*****************************************************************************

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;

//*****************************************************************************
//*******************************CLASSES***************************************
//*****************************************************************************

public class PNGHistogramBuilder extends VisualHistogramBuilder{
	
//*********************************************************____________________
//******************CLASS METHODS**************************____________________
//*********************************************************____________________

	@Override
	public boolean build(String file, int width, int height, 
			HashMap<Integer, Integer> histogram, int interval) {
		
		//Get the sorted histogram values
		ArrayList<Integer> sortedKeys = 
				new ArrayList<Integer>(histogram.keySet());
		Collections.sort(sortedKeys);
		
		//Create a blank image with the specified width and height
		BufferedImage img = new BufferedImage(width, height, 
				BufferedImage.TYPE_INT_RGB);
		//Prepare our output file
		File output = new File(file);
		
	    try {
			//Draw output
			Graphics2D g = img.createGraphics();
			
			//White background
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			
			//Draw Title
			g.setColor(Color.BLACK);
		    Font font = new Font("Sans-Serif", Font.PLAIN, height/20);
		    Font font2 = new Font("Sans-Serif", Font.PLAIN, height/30);
		    Font font3 = new Font("Sans-Serif", Font.PLAIN, height/60);
		    drawCenteredString(g, "Word Count Histogram", 
		    		new Rectangle(0, 0, width, height/6), font);
			
		    //Draw bottom bar
			g.drawLine(width/6, (height/6)*5, (width/6)*5, (height/6)*5);
			drawCenteredString(g, "Word Count", 
					new Rectangle(0, height-height/6, width, height/6), font2);
			
			//Draw side bar
			g.drawLine(width/6, height/6, width/6, (height/6)*5);
			drawCenteredString90D(g, "Frequency", 
					new Rectangle(0, 0, width/6, height), font2);
			
			//Draw content
			g.setColor(Color.ORANGE);
			int contentWidth = (width/6)*4;
			int contentHeight = (height/6)*4;
			int barWidth = contentWidth / sortedKeys.size();
			float maxValue = -1;
			//Find max value from histogram data for proportions
			for(int c : sortedKeys){
				if(histogram.get(c) > maxValue){
					maxValue = (float)histogram.get(c);
				}
			}
			
			//Starting bar drawing position
			int drawPos = width/6+1;
			
			//Draw min, max, and middle frequencies for simplicity
			g.setColor(Color.BLACK);
			g.setFont(font2);
			g.drawString("0", drawPos-(height/30)*2, (height/6)*5);
			g.drawString(""+(maxValue/2), drawPos-(height/30)*2, (height/6)*3);
			g.drawString(""+(int)maxValue, drawPos-(height/30)*2, (height/6));
			
			int lastc = 0; //Keep track of last value for last label
			for(int c : sortedKeys){
				if(c == -1){ //Files that couldn't be read
					drawCenteredString(g, c + " files could not be read", 
							new Rectangle(0,0,width,20), font3);
					continue;
				}
				//Draw a bar proportionally
				int v = histogram.get(c);
				int drawHeight = (int)( (((float)v)/maxValue) * contentHeight);
				g.setColor(Color.ORANGE);
				g.fillRect(drawPos+1, (height/6)*5-drawHeight, barWidth-1,
															drawHeight-1);
				g.setColor(Color.CYAN);
				g.drawRect(drawPos+1, (height/6)*5-drawHeight, barWidth-1,
															drawHeight-1);
				//Draw label
				g.setFont(font2);
				g.setColor(Color.BLACK);
				g.drawString(""+c, drawPos, (height/6)*5+height/30);
				drawPos += width/6;
				lastc = c;
			}
			g.drawString(""+(lastc+interval), drawPos, (height/6)*5+height/30);
		    
			//End drawing
			g.dispose();
			
			//Save the output file
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			//If there was an error rendering the file abort
			System.err.println("Error rendering file!");
			return false;
		}
	    return true;
	}
	
	/**
	 * Draw centered text in the specified region.
	 * 
	 * @param g The graphics to draw on
	 * @param text The text to display
	 * @param rect The rectangle to calculate the center from
	 * @param font The font to use when drawing the text
	 */
	public void drawCenteredString(Graphics g, String text, Rectangle rect,
																Font font) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    //Measure the string and draw it centered in the rectangle
	    int x = ((rect.width - metrics.stringWidth(text)) / 2) +
	    		(int)rect.getX();
	    int y = ((rect.height - metrics.getHeight()) / 2) + 
	    		metrics.getAscent() + (int)rect.getY();
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	
	/**
	 * Draw centered vertical facing text in the specified region.
	 * 
	 * @param g The graphics to draw on
	 * @param text The text to display
	 * @param rect The rectangle to calculate the center from
	 * @param font The font to use when drawing the text
	 */
	public void drawCenteredString90D(Graphics2D g, String text, 
									Rectangle rect, Font font) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    //Determine the centered location to draw from
	    int x = ((rect.width - metrics.stringWidth(text)) / 2) + 
	    		(int)rect.getX();
	    int y = ((rect.height - metrics.getHeight()) / 2) + 
	    		metrics.getAscent() + (int)rect.getY();
	    g.setFont(font);
	    // Translate, rotate, and draw the string
	    g.translate(x,y);
	    g.rotate(-Math.toRadians(90));
	    g.drawString(text,0,0);
	    g.rotate(Math.toRadians(90));
	    g.translate(-x,-y);
	}

}
