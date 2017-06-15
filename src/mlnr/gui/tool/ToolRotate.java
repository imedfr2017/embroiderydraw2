/*
 * ToolRotate.java
 *
 * Created on August 25, 2006, 11:31 AM
 *
 */

package mlnr.gui.tool;

import java.awt.Color; 
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.RotatePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolRotate extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    RotatePanel rotatePanel;
    boolean bLeftMouseDown;
    boolean completed;
    boolean useCalculatedPt = false;
    FPointType fptUserCalculated = null;
    RotateCenterObject rotateCenterObject = new RotateCenterObject();
    /** This is the rotate offset when mouse goes down. */
    float currentRad = 0.0f;
    /** This is the rotate offset that accumulated from the previous mouse goes down until it is reset. */
    float offsetRad = 0.0f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
            
    /** Creates a new instance of ToolRotate */
    public ToolRotate(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, RotatePanel rotatePanel) {
        super(iFrameOperator, drawingPad);
        this.rotatePanel = rotatePanel;        
        rotatePanel.setRotateTool(this);
        
        // Calculate the center.
        if (drawingPad.isSelectedItems()) {
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            Graphics2D g2D = drawingPad.getTransformedGraphics();
            rotateCenterObject.draw(g2D, false);
            rotatePanel.setRadian(currentRad + offsetRad);
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            startRotate();
        else if (evt.getButton() == MouseEvent.BUTTON3)
            setCalculatePosition();
    }
    
    public void mouseReleased(MouseEvent evt) {        
        if (evt.getButton() == MouseEvent.BUTTON1)
            finishRotate();       
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (bLeftMouseDown) {
            rotate();
            
            // Draw the selected items and the degree vector.
            Graphics2D g2D = drawingPad.getTransformedGraphics();
            drawingPad.drawSelectedItems(g2D);
            rotateCenterObject.draw(g2D, true);
        }
    }
    
    public void onFinalize(boolean complete) {
        complete(complete);
    }
    
    public void onCompleteFinalize() {
        complete(true);
        completed = true;
    }
    
    public void onDrawTool(Graphics2D g2d) {
        rotateCenterObject.draw(g2d, false);
    }
    
    // </editor-fold>
     
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will rotate the selected items.
     */
    private void rotate() {
        currentRad = drawingPad.getSelectedItems().onRotate(drawingPad.getMousePositionMeasurement(), offsetRad) - offsetRad;
        rotatePanel.setRadian(currentRad + offsetRad);
    }

    private void setCalculatePosition() {
        // This is the filtered mouse position.
        fptUserCalculated = getFilterPointDesign(drawingPad.getSelectedItems(), false);
        drawingPad.getSelectedItems().calculate(fptUserCalculated);
        useCalculatedPt = true;
        offsetRad = currentRad = 0.0f;
        rotatePanel.setRadian(currentRad + offsetRad);
        drawingPad.repaint();
    }    
    
    /** This will start the rotate moving of the selected items.
     */
    private void startRotate() {
        if (drawingPad.isSelectedItems() == false)
            throw new IllegalStateException("ToolRotate::startRotate cannot rotate since there is no selected items.");
        
        // This is the filtered mouse position.
        FPointType fptDown = getFilterPointDesign(drawingPad.getDesign(), true);
        // Set the start position for the selected items.
        drawingPad.getSelectedItems().setBeginPosition(fptDown);
        // Mouse is down and ready to rumble.
        bLeftMouseDown = true;
    }
    
    /** This will finish the rotate moving of the selected items.
     */
    private void finishRotate() {
        if (!bLeftMouseDown)
            return;
        offsetRad = drawingPad.getSelectedItems().getRadian();
        currentRad = 0.0f;
        bLeftMouseDown = false;
    }

    /** This will complete the rotating.
     */
    private void complete(boolean complete) {
        if (completed)
            return;
        
        drawingPad.getSelectedItems().finalizeMovement();
        
        if (complete) {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
            drawingPad.finalizeSelectedItems();
        }
        
        drawingPad.repaint();
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Rotate Methods ">

    /** This will rotate the selected items.
     */
    public void rotate(float radian) {
        currentRad = 0.0f;
        offsetRad = radian;
        drawingPad.getSelectedItems().onRotate(currentRad + offsetRad);
            
        // Draw the selected items and the degree vector.
        Graphics2D g2D = drawingPad.getTransformedGraphics();
        drawingPad.drawSelectedItems(g2D);
        rotateCenterObject.draw(g2D, true);
    }

    public void resetPointToCenter() {        
        // Calculate the center.
        useCalculatedPt = false;
        if (drawingPad.isSelectedItems()) {
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            offsetRad = currentRad = 0.0f;
            rotatePanel.setRadian(offsetRad + currentRad);
            drawingPad.repaint();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RotateCenterObject Class ">

    class RotateCenterObject {
        private final Color CROSS_COLOR_1 = Color.BLUE;
        private final Color CROSS_COLOR_2 = Color.RED;
        private final float CROSS_SIZE=0.15f;
        private final float CROSS_DEGREE_SIZE = 20.0f;
        private Line2D.Float fLine = new Line2D.Float();
        private Line2D.Float fLineCross1 = new Line2D.Float();
        private Line2D.Float fLineCross2 = new Line2D.Float();
        private Ellipse2D.Float fEllipse = new Ellipse2D.Float();
        
        public RotateCenterObject() {
            
        }
        
        /** This will draw the rotating object.
         */
        void draw(Graphics2D g, boolean erase) {
            drawCross(g, erase);
            drawDegreeVector(g, erase);
        }
        
        /** This will draw the center cross.
         */
        private void drawCross(Graphics2D g, boolean erase) {
            float x = fptUserCalculated.x;
            float y = fptUserCalculated.y;
            
            // Save the color and stroke
            Color old = g.getColor();
            Stroke sOld = g.getStroke();

            // Set up the graphics device.
            g.setXORMode(Color.WHITE);
            g.setStroke(strokeDrawing);

            // erase the cross.
            if (erase) {
                g.draw(fLineCross1);
                g.draw(fLineCross2);
                g.draw(fEllipse);
            }
            
            // Draw first cross.
            g.setColor(CROSS_COLOR_1);
            fLineCross1.setLine(x - CROSS_SIZE, y, x + CROSS_SIZE, y);
            g.draw(fLineCross1);
            g.setColor(CROSS_COLOR_2);
            fLineCross2.setLine(x, y - CROSS_SIZE, x, y + CROSS_SIZE);
            g.draw(fLineCross2);
            fEllipse.setFrame(x - CROSS_SIZE, y - CROSS_SIZE, CROSS_SIZE*2, CROSS_SIZE*2);
            g.draw(fEllipse);

            // Restore the color and stroke.
            g.setColor(old);
            g.setStroke(sOld);
        }

        /** This will draw the vector that is aligned with the current degree.
         * @param g is the graphics device.
         * @param erase is true if it should erase the old coordinates.
         */
        private void drawDegreeVector(Graphics2D g, boolean erase) {
            // Setup the graphics device.
            Color old = g.getColor();            
            Stroke sOld = g.getStroke();            
            g.setColor(CROSS_COLOR_1);
            g.setXORMode(Color.WHITE);
            g.setStroke(strokeDrawing);
            
            // Erase the old line.
            if (erase)
                g.draw(fLine);
            
            // Get the new coordinates for the degree line.
            float fx = (float)Math.cos(offsetRad + currentRad) * CROSS_DEGREE_SIZE + fptUserCalculated.x;
            float fy = (float)Math.sin(offsetRad + currentRad) * CROSS_DEGREE_SIZE + fptUserCalculated.y;
            
            // Draw the line.
            fLine.setLine(fptUserCalculated.x, fptUserCalculated.y, fx, fy);
            g.draw(fLine);
       
            // Restore the graphics device.
            g.setColor(old);
            g.setStroke(sOld);
        }
    }
    
    // </editor-fold>
}
