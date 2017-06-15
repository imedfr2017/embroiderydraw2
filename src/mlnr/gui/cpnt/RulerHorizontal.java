/*
 * RulerHorizontal.java
 *
 * Created on October 3, 2005, 12:44 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
        
import java.awt.geom.AffineTransform;
import mlnr.Measurement;

/**
 *
 * @author Robert Molnar II
 */
public class RulerHorizontal extends AbstractRuler {
    
    /** Creates a new instance of RulerHorizontal */
    public RulerHorizontal(InterfaceScale iScale) {
        super(iScale);
    }
    
    /** This will draw the ruler.
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D)g;
        
        // Draw the bottom line.
        Rectangle clipBounds = g.getClipBounds();
        g2D.setColor(Color.BLACK);
        g2D.drawLine(clipBounds.x, SIZE-1,  clipBounds.x + clipBounds.width, SIZE-1);
                
        // Translate based on the offset of the drawing pad's design.
        g2D.translate((double)iScale.getXOffsetOfDesign(), 0.0);
        g2D.scale((double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement(), 1.0);
        g2D.translate((double)-iScale.getXOffsetIntoDesign(), 0.0);
        
        // Set the font to be used.
        g2D.setFont(font);
        
        // Set the pen size.
        g2D.setStroke(stroke);
        
        // Calculate for optimized tick drawing and text placement.
        float gridSize = Measurement.getGridSize();
        float smallGridSize = Measurement.getFineGridSize();
        float xStart = (float)Math.floor(g.getClipBounds().getX());
        if (xStart < 0.0f)
            xStart = 0.0f;
        if (xStart > iScale.getWidthOfDesign())
            return;
        
        float tmp = (float)clipBounds.getWidth() / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale());
        float xEnd = (float)Math.ceil(xStart + tmp);
        
        // Check to see if it is past the end of the design.
        if (xEnd > iScale.getWidthOfDesign())
            xEnd = iScale.getWidthOfDesign();        
        
        // Check to see what type of ticks need to be drawn.
        float smallTicksPixelSize = smallGridSize * iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement();
        float ticksPixelSize = gridSize * iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement();
        
        // Ticks cannot be smaller than 25 pixels.        
        boolean smallTicks = Measurement.isFineGridVisible();
        int smallTickCount = 0;
        if (smallTicksPixelSize < 10) {
            smallTicks = false;
        } else
            smallTickCount = (int)(gridSize / smallGridSize) - 1;
        if (ticksPixelSize < 30)
            gridSize *= 5.0f;
        
        // Draw the ticks and put text there.
        int majorTickCount = (int)(Math.ceil((xEnd - xStart) / gridSize)) + 2;
        if (majorTickCount > iScale.getWidthOfDesign() / gridSize)
            majorTickCount = (int)(iScale.getWidthOfDesign() / gridSize) + 1;
        float currentMajorTick = (float)(Math.floor(xStart / gridSize) * gridSize);        
        
        // Make sure total major ticks are greater than equal to the size of the grid.
        if ((majorTickCount -1) * gridSize < iScale.getWidthOfDesign())
            majorTickCount++;
        
        for (int i=0; i < majorTickCount; i++, currentMajorTick += gridSize) {
            // Draw the major tick.
            fLine.setLine(currentMajorTick, FSIZE, currentMajorTick, 15);
            g2D.draw(fLine);
            
            // Draw the small ticks.
            if (smallTicks && i != majorTickCount-1) {
                float smallTickPos = currentMajorTick + smallGridSize;
                for (int ss=0; ss < smallTickCount; ss++, smallTickPos += smallGridSize) {
                    fLine.setLine(smallTickPos, FSIZE, smallTickPos, 20);
                    g2D.draw(fLine);
                }
            }
            
            // Print the major tick measurement.
            if (Measurement.isMetric())
                g2D.drawString(String.format("%.2f", Measurement.convertMeasurementToMM(currentMajorTick)), currentMajorTick, 10);
            else
                g2D.drawString(String.format("%.2f", Measurement.convertMeasurementToInch(currentMajorTick)), currentMajorTick, 10);
        }
        
        // Draw the cursor.
        
        // Set up the graphics so that it will be a small line and that it will erase what is there.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(stroke);
        
        // Draw the line.
        fLine.setLine(cursorPos, 0, cursorPos, FSIZE);
        g2D.draw(fLine);
    }
        
    /** This will scale the ruler.
     * @param scale is the new scale size.
     */
    public void zoomTo(float scale) {
        AffineTransform affineTrans = new AffineTransform();
        affineTrans.scale(1.0 / (iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement()), 1.0);
        font = FONT.deriveFont(affineTrans);
    }
       
    /** This will set the position of the cursor for this ruler (must be measurements). 
     * @param cursorPos is the new position in the design for this ruler.
     */
    public void setCursorPos(float cursorPos) {
        Graphics2D g2D = (Graphics2D)getGraphics();
        
        // Translate based on the offset of the drawing pad's design.
        g2D.translate((double)iScale.getXOffsetOfDesign(), 0.0);
        g2D.scale((double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement(), 1.0);
        g2D.translate((double)-iScale.getXOffsetIntoDesign(), 0.0);
        
        // Set up the graphics so that it will be a small line and that it will erase what is there.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(stroke);
        
        // Draw the old line first then the new line.
        fLine.setLine(this.cursorPos, 0, this.cursorPos, FSIZE);
        g2D.draw(fLine);
        fLine.setLine(cursorPos, 0, cursorPos, FSIZE);
        g2D.draw(fLine);
        
        this.cursorPos = cursorPos;
    }
}
