/*
 * ToolMagifyGlass.java
 *
 * Created on January 20, 2007, 11:37 AM
 * 
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DesignPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolMagifyGlass extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the zoom increment slow. */
    private static final float ZOOM_INCREMENT = 0.1f;
    /** This is the zoom increment fast. */
    private static final float ZOOM_FAST_INCREMENT = 0.5f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is needed to zoom in/out on the DesignPanel. */
    DesignPanel dPanel;
    
    /** Where the mouse went down at. */
    FPointType fptMouseDown;
    
    /** Used to show the zoom into area. */
    Rectangle2D.Float fRectangle = new Rectangle2D.Float();
    
    /** True if the left mouse button is down. */
    boolean bLeftMouseDown = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /**
     * Creates a new instance of ToolMagifyGlass
     */
    public ToolMagifyGlass(InterfaceFrameOperation iFrameOperator, DesignPanel dPanel) {
        super(iFrameOperator, dPanel.getDrawingPad());
        this.dPanel = dPanel;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        fptMouseDown = drawingPad.getMousePositionMeasurement();
        
        if (evt.getButton() == MouseEvent.BUTTON1)
            bLeftMouseDown = true;
        else
            bLeftMouseDown = false;
    }
    
    public void mouseReleased(MouseEvent evt) {
        bLeftMouseDown = false;
        
        if (evt.getButton() == MouseEvent.BUTTON1)
            zoomIn(evt.isShiftDown());
        else if (evt.getButton() == MouseEvent.BUTTON3)
            zoomOut(evt.isShiftDown());
    }
        
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (bLeftMouseDown)
            draw(drawingPad.getTransformedGraphics(), true);
    }
    
    public void onFinalize(boolean complete) {
        complete(complete);
    }
    
    public void onCompleteFinalize() {
        complete(true);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will complete the translating.
     */
    private void complete(boolean complete) {
        if (drawingPad.isSelectedItems() == false)
            return;
        
        drawingPad.getSelectedItems().finalizeMovement();
        
        if (complete) {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
            drawingPad.finalizeSelectedItems();
        }
        
        drawingPad.repaint();
    }

    /** This will zoom in by 10% percent or if the mouse was dragged far enough then it will zoom in on that dragging. However
     * a limit of 1600% is impossed on the mouse drag zoom in.
     */
    private void zoomIn(boolean fasterZoom) {
        FPointType fptMouseUp = drawingPad.getMousePositionMeasurement();
        
        // If the mouse drag is greater than 3 measurements than zoom in on that area.
        if (fptMouseUp.distance(fptMouseDown) > 3) {
            float width = Math.abs(fptMouseUp.x - fptMouseDown.x);
            float height = Math.abs(fptMouseUp.y - fptMouseDown.y);
            float x = fptMouseDown.x;
            float y = fptMouseDown.y;
            
            // Get the upper left position.
            if (fptMouseUp.x < fptMouseDown.x)
                x = fptMouseUp.x;
            if (fptMouseUp.y < fptMouseDown.y)
                y = fptMouseUp.y;
            
            // Area to zoom in on. Limit zoom to 1600%.
            Rectangle2D.Float fZoomArea = new Rectangle2D.Float(x, y, width, height);
            dPanel.zoomTo(fZoomArea, 16.0f);
        } else {
            // Get the new percentage to zoom in.
            float percentage = dPanel.getZoom();            
            if (fasterZoom)
                percentage += ZOOM_FAST_INCREMENT;
            else
                percentage += ZOOM_INCREMENT;
            
            dPanel.zoomTo(fptMouseUp, percentage);
        }
        
        // Update zoom combobox.
        iFrameOperator.setZoom(dPanel.getZoom());
        dPanel.repaint();
        
        // Reset drawing selected area.
        fRectangle = new Rectangle2D.Float();
    }
    
    /** this will zoom out by 10% percent.
     * @param fasterZoom is true if it should zoom by 50% else false 10%.
     */
    private void zoomOut(boolean fasterZoom) {
        // Get the new percentage to zoom in.
        float percentage = dPanel.getZoom();            
        if (fasterZoom)
            percentage -= ZOOM_FAST_INCREMENT;
        else
            percentage -= ZOOM_INCREMENT;
        
        // Zoom in.
        dPanel.zoomTo(percentage);
        
        // Update zoom combobox.
        iFrameOperator.setZoom(dPanel.getZoom());
        dPanel.repaint();
    }
    
    /** This will draw the selecting rectangle.
     */
    private void draw(Graphics2D g2d, boolean erase) {
        // Set the stroke.
        Stroke sOld = g2d.getStroke();
        g2d.setStroke(strokeDrawing);
        g2d.setXORMode(Color.WHITE);
        g2d.setColor(Color.RED);
        
        // Draw the rectangle.
        if (erase)
            g2d.draw(fRectangle);
        setRect(fRectangle);
        g2d.draw(fRectangle);
        
        g2d.setStroke(sOld);
    }
    
    /** This will set the rectangle for drawing.
     */
    private void setRect(Rectangle2D.Float fRect) {
        float xPos, yPos, width, height;
        
        // The current position.
        FPointType fptPoint = drawingPad.getMousePositionMeasurement();
        
        // This will set the position x and width.
        if (fptPoint.x > fptMouseDown.x) {
            xPos = fptMouseDown.x;
            width = fptPoint.x - fptMouseDown.x;
        } else {
            xPos = fptPoint.x;
            width = fptMouseDown.x - fptPoint.x;
        }
        
        // This will set the position y and height.
        if (fptPoint.y > fptMouseDown.y) {
            yPos = fptMouseDown.y;
            height = fptPoint.y - fptMouseDown.y;
        } else {
            yPos = fptPoint.y;
            height = fptMouseDown.y - fptPoint.y;
        }
                
        fRect.setRect(xPos, yPos, width, height);
    }
    
    // </editor-fold>
}
