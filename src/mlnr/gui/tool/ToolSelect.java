/*
 * ToolSelect.java
 *
 * Created on August 25, 2006, 11:31 AM
 *
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.draw.DrawingDesign;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.SelectPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolSelect extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    SelectPanel selectPanel;
    Rectangle2D.Float fRectangle = new Rectangle2D.Float();
    FPointType fptMouseDown = new FPointType();    
    boolean bLeftMouseDown = false;    
    boolean bControlDown = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the rectangle used to select everything. */
    private static final Rectangle2D.Float RECTANLE_MAX = new Rectangle2D.Float(-2000000000.0f, -2000000000.0f, 4000000000.0f, 4000000000.0f);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolSelect */
    public ToolSelect(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, SelectPanel selectPanel) {
        super(iFrameOperator, drawingPad);
        this.selectPanel = selectPanel;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            bLeftMouseDown = true;
            bControlDown = evt.isControlDown();
            startSelecting(evt.isShiftDown(), evt.isControlDown());
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            endSelecting();
        if (evt.getButton() == MouseEvent.BUTTON3)
            complete(true);
        
        bLeftMouseDown = false;
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (bLeftMouseDown)
            draw(drawingPad.getTransformedGraphics(), true);
    }
    
    public void onDrawTool(Graphics2D g2d) {
    }
    
    public void onFinalize(boolean complete) {
        complete(complete);
    }
    
    public void onCompleteFinalize() {
        complete(true);
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            // Get the selected items.
//            DrawingDesign dCopiedItems = drawingPad.getDesign().copySelectedLines();
//            if (dCopiedItems == null)
//                return;
            
            // Remove the selected items.
            drawingPad.getDesign().deleteSelectedLines(false);
            drawingPad.setSelectedItems(null);
            drawingPad.repaint();
            
            // Update the GUI
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    private void complete(boolean complete) {
        if (complete) {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
            drawingPad.finalizeSelectedItems();
        } else {
            // Add the selected items.
            if (drawingPad.getDesign().isSelectedItems() == false)
                return;
            
            //  Convert the selected lines and vertices into a TransformGraph.
            TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
            if (dTransform != null ) {
                drawingPad.setSelectedItems(dTransform);
            }
        }
        
        drawingPad.repaint();
    }
    
    /** This will start the selecting process.
     * @param shiftDown is true if the shift key is held down.
     * @param controlDown is true if the control key is held down.
     */
    private void startSelecting(boolean shiftDown, boolean controlDown) {
        // If shift isn't down then the previously selected items need to be deselected.
        if (!shiftDown && !controlDown) {
            iFrameOperator.enableCopyCut(false);
            drawingPad.finalizeSelectedItems();
            drawingPad.setSelectedItems(null);
            drawingPad.repaint();
        }
        
        // This will filter the mouse position.
        fptMouseDown = drawingPad.getMousePositionMeasurement();
    }
    
    /** This will end the selecting process.
     */
    private void endSelecting() {
        // Get the current layer.
        boolean currentLayer = selectPanel.isCurrentLayer();
        
        // Get the rectangle for selecting.
        Rectangle2D.Float fRect = new Rectangle2D.Float();
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        if (fptMouseDown.equals(fpt)) {
            fRect = getPointBounds(fpt);
        } else
            setRect(fRect);
        
        // Get the selected items and add it to the drawing pad.
        if (bControlDown)
            TransformDesign.deselect(drawingPad, selectPanel, fRect);
        else
            TransformDesign.select(drawingPad, selectPanel, fRect, false);
        
        // Add the selected items and enable the copy and cut button.
        if (drawingPad.getDesign().isSelectedItems()) {
            iFrameOperator.enableCopyCut(true);
            iFrameOperator.enableAdvanceTools(true);
        } else {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
        }
        
        // Repaint.
        fRectangle = new Rectangle2D.Float();
        drawingPad.repaint();
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