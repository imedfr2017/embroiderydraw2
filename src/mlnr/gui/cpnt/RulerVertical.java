/*
 * RulerVertical.java
 *
 * Created on October 3, 2005, 12:44 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.geom.AffineTransform;
import java.awt.*;
import javax.swing.*;
        
import mlnr.Measurement;

/**
 *
 * @author Robert Molnar II
 */
public class RulerVertical extends AbstractRuler {
    
    /** Creates a new instance of RulerHorizontal */
    public RulerVertical(InterfaceScale iScale) {
        super(iScale);
    }
    
    /** This will draw the ruler.
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D)g;
        
        // Draw the right line.
        Rectangle clipBounds = g.getClipBounds();
        g2D.setColor(Color.BLACK);
        g2D.drawLine(SIZE-1, clipBounds.y, SIZE-1, clipBounds.y + clipBounds.height);
                
        // Translate based on the offset of the drawing pad's design.
        g2D.translate(0.0, (double)iScale.getYOffsetOfDesign());
        g2D.scale(1.0, (double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement());
        g2D.translate(0.0, (double)-iScale.getYOffsetIntoDesign());
        
        // Set the font to be used.
        g2D.setFont(font);
        
        // Set the pen size.
        g2D.setStroke(stroke);
        
        // Calculate for optimized tick drawing and text placement.
        float gridSize = Measurement.getGridSize();
        float smallGridSize = Measurement.getFineGridSize();
        float yStart = (float)Math.floor(g.getClipBounds().getY());
        if (yStart < 0.0f)
            yStart = 0.0f;
        if (yStart > iScale.getHeightOfDesign()) // Don't draw if update is beyond design width.
            return; 
        
        float tmp = (float)clipBounds.getHeight() / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale());
        float yEnd = (float)Math.ceil(yStart + tmp);
        
        // Check to see if it is past the end of the design.
        if (yEnd > iScale.getHeightOfDesign())
            yEnd = iScale.getHeightOfDesign();        
        
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
        int majorTickCount = (int)(Math.ceil((yEnd - yStart) / gridSize)) + 2;
        if (majorTickCount > iScale.getHeightOfDesign() / gridSize)
            majorTickCount = (int)(iScale.getHeightOfDesign() / gridSize) + 1;
        float currentMajorTick = (float)(Math.floor(yStart / gridSize) * gridSize);        
        
        // Make sure total major ticks are greater than equal to the size of the grid.
        if ((majorTickCount-1)  * gridSize < iScale.getHeightOfDesign())
            majorTickCount++;
        
        for (int i=0; i < majorTickCount; i++, currentMajorTick += gridSize) {
            // Draw the major tick.
            fLine.setLine(FSIZE, currentMajorTick, 15, currentMajorTick);
            g2D.draw(fLine);
            
            // Draw the small ticks.
            if (smallTicks && i != majorTickCount-1) {
                float smallTickPos = currentMajorTick + smallGridSize;
                for (int ss=0; ss < smallTickCount; ss++, smallTickPos += smallGridSize) {
                    fLine.setLine(FSIZE, smallTickPos, 20, smallTickPos);
                    g2D.draw(fLine);
                }
            }
            
            // Print the major tick measurement.
            if (Measurement.isMetric())
                g2D.drawString(String.format("%.1f", Measurement.convertMeasurementToMM(currentMajorTick)), 1, currentMajorTick);
            else
                g2D.drawString(String.format("%.1f", Measurement.convertMeasurementToInch(currentMajorTick)), 1, currentMajorTick);
        }
                
        // Draw the cursor.
        
        // Set up the graphics so that it will be a small line and that it will erase what is there.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(stroke);
        
        // Draw the line.
        fLine.setLine(0, cursorPos, FSIZE, cursorPos);
        g2D.draw(fLine);
    }
        
    /** This will scale the ruler.
     * @param scale is the new scale size.
     */
    public void zoomTo(float scale) {
        AffineTransform affineTrans = new AffineTransform();
        affineTrans.scale(1.0, 1.0 / (iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement()));
        font = FONT.deriveFont(affineTrans);
    }
       
    /** This will set the position of the cursor for this ruler (must be measurements). 
     * @param cursorPos is the new position in the design for this ruler.
     */
    public void setCursorPos(float cursorPos) {
        Graphics2D g2D = (Graphics2D)getGraphics();
        
        // Translate based on the offset of the drawing pad's design.
        g2D.translate(0.0, (double)iScale.getYOffsetOfDesign());
        g2D.scale(1.0, (double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement());
        g2D.translate(0.0, (double)-iScale.getYOffsetIntoDesign());
        
        // Set up the graphics so that it will be a small line and that it will erase what is there.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(stroke);
        
        // Draw the old line first then the new line.
        fLine.setLine(0, this.cursorPos, FSIZE, this.cursorPos);
        g2D.draw(fLine);
        fLine.setLine(0, cursorPos, FSIZE, cursorPos);
        g2D.draw(fLine);
        
        this.cursorPos = cursorPos;
    }
}
