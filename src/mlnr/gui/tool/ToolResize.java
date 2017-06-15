/*
 * ToolResize.java
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
import java.awt.geom.Rectangle2D;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.ResizePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolResize extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    ResizePanel resizePanel;
    FPointType fptUserCalculated;
    boolean bLeftMouseDown = false;
    boolean completed = false;
    float xScaleOffset;
    float yScaleOffset;
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    // settings for the cross where the object is to be rotated around.
    private static Color CROSS_COLOR_1 = Color.BLUE;
    private static Color CROSS_COLOR_2 = Color.RED;
    private static float CROSS_SIZE=0.25f;
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolResize */
    public ToolResize(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ResizePanel resizePanel) {
        super(iFrameOperator, drawingPad);
        this.resizePanel = resizePanel;
        resizePanel.setResizeTool(this);
        
        // Calculate the center.
        if (drawingPad.isSelectedItems()) {
            drawingPad.getSelectedItems().beginScaling();
            
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            drawResizeCenter(drawingPad.getTransformedGraphics());
            xScaleOffset = yScaleOffset = 0.0f;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            startResize();
        else if (evt.getButton() == MouseEvent.BUTTON3)
            setCalculatePosition();
    }
    
    public void mouseReleased(MouseEvent evt) {        
        if (evt.getButton() == MouseEvent.BUTTON1)
            finishResize();       
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (bLeftMouseDown) {
            resize();
            
            // Draw the selected items and the degree vector.
            Graphics2D g2D = drawingPad.getTransformedGraphics();
            drawingPad.drawSelectedItems(g2D);
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
        drawResizeCenter(g2d);
    }
    
    // </editor-fold>
     
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will resize the selected items.
     */
    private void resize() {
        if (resizePanel.isResizeAnyDirection())
            drawingPad.getSelectedItems().onResizeAnyDirection(getFilterPoint(null, true), xScaleOffset, yScaleOffset);
        else if (resizePanel.isResizeHorizontal())
            drawingPad.getSelectedItems().onResizeHorizontal(getFilterPoint(null, true).x, xScaleOffset);
        else if (resizePanel.isResizeVertical())
            drawingPad.getSelectedItems().onResizeVertical(getFilterPoint(null, true).y, yScaleOffset);
        else if (resizePanel.isResizeUniform())
            drawingPad.getSelectedItems().onResizeUniform(getFilterPoint(null, true), xScaleOffset, yScaleOffset);
        else
            throw new IllegalStateException("ToolResize::resize() Unknown resize method.");
    }

    private void setCalculatePosition() {
        // This is the filtered mouse position.
        fptUserCalculated = getFilterPointDesign(drawingPad.getSelectedItems(), true);
        drawingPad.getSelectedItems().finalizeMovement();
        drawingPad.getSelectedItems().calculate(fptUserCalculated);
        drawingPad.repaint();
        xScaleOffset = yScaleOffset = 0.0f;
    }    
    
    /** This will start the resize moving of the selected items.
     */
    private void startResize() {
        if (drawingPad.isSelectedItems() == false)
            throw new IllegalStateException("ToolResize::startResize cannot resize since there is no selected items.");
        
        // This is the filtered mouse position.
        FPointType fptDown = drawingPad.getMousePositionMeasurement();
        // Set the start position for the selected items.
        drawingPad.getSelectedItems().setBeginPosition(fptDown);
        // Mouse is down and ready to rumble.
        bLeftMouseDown = true;
    }
    
    /** This will finish the resize moving of the selected items.
     */
    private void finishResize() {
        if (!bLeftMouseDown)
            return;
        bLeftMouseDown = false;
        
        xScaleOffset = drawingPad.getSelectedItems().getXScale() - 1.0f;
        yScaleOffset = drawingPad.getSelectedItems().getYScale() - 1.0f;
    }

    /** This will complete the resize.
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

    /** This will draw the resize Center point.
     */
    private void drawResizeCenter(Graphics2D g) {
        Color old = g.getColor();
        g.setPaintMode();
        Stroke sOld = g.getStroke();
        g.setStroke(strokeDrawing);
        
        // Draw first cross.
        g.setColor(CROSS_COLOR_1);
        g.draw(new Line2D.Float(fptUserCalculated.x - CROSS_SIZE, fptUserCalculated.y, fptUserCalculated.x + CROSS_SIZE, fptUserCalculated.y));        
        g.draw(new Line2D.Float(fptUserCalculated.x, fptUserCalculated.y - CROSS_SIZE, fptUserCalculated.x, fptUserCalculated.y + CROSS_SIZE));        
        g.setColor(CROSS_COLOR_2);
        g.draw(new Ellipse2D.Float(fptUserCalculated.x - CROSS_SIZE, fptUserCalculated.y - CROSS_SIZE, CROSS_SIZE*2, CROSS_SIZE*2));
        
        // Restore the color.
        g.setColor(old);
        g.setStroke(sOld);
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Resize Methods ">

    public void resetPointToCenter() {
        xScaleOffset = yScaleOffset = 0.0f;
        
        // Calculate the center.
        if (drawingPad.isSelectedItems()) {
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            drawingPad.repaint();
        }
    }

    public FPointType getCurrentSelectedSize() {
        Rectangle2D.Float fRect = drawingPad.getSelectedItems().getBounds2D();
        return new FPointType(fRect.width, fRect.height);
    }

    public void setCurrentSelectedSize(float newWidth, float newHeight) {
        // Need to finalize first before calculating the size so that it will get the current size.
        drawingPad.getSelectedItems().finalizeMovement();
        drawingPad.getSelectedItems().calculateCenter();
        drawingPad.getSelectedItems().onResizeAbsolute(newWidth, newHeight);
        drawingPad.getSelectedItems().finalizeMovement();
        xScaleOffset = yScaleOffset = 0.0f;
        fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
        drawingPad.repaint();
    }
    
    // </editor-fold>
}
