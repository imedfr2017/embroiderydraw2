/*
 * ToolImageMover.java
 *
 * Created on September 15, 2006, 11:44 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.tool.opt.ImagePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolImageMover extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the image panel that is displayed below the layer panel. */
    ImagePanel imagePanel;
    
    /** This is the selected image to move. */
    ImageInfo iInfo = null;
    
    /** The offset from the click down to the position of the image initially. */
    FPointType fptOffset = new FPointType();
    
    /** Variable used to indicate dragging ok. */
    boolean mouseDrag = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolImageCenter */
    public ToolImageMover(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ImagePanel imagePanel) {
        super(iFrameOperator, drawingPad);
        
        this.imagePanel = imagePanel;
        imagePanel.setDrawingPad(drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            select();
            
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            if (isImageHit())
                imagePanel.popupMenuForImage(evt);
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
        iInfo = null;
        mouseDrag = false;
    }
    
    public void mouseDragged(MouseEvent evt) {
        if (mouseDrag)
            update();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will select the image to move.
     */
    private void select() {
        FPointType fptMouseDown = drawingPad.getMousePositionMeasurement();
        iInfo = drawingPad.getImagePool().getImage(fptMouseDown);
        
        if (iInfo == null)
            return;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        fptOffset.x = fptMouseDown.x - iInfo.getXPosition();
        fptOffset.y = fptMouseDown.y - iInfo.getYPosition();
        
        mouseDrag  = true;
    }
    
    private void update() {        
        if (iInfo == null)
            return;
        
        FPointType fptMouseDown = drawingPad.getMousePositionMeasurement();
        
        float x = fptMouseDown.x - fptOffset.x;
        float y = fptMouseDown.y - fptOffset.y;
        
        iInfo.setPosition(x, y);
        imagePanel.validateImageInfo();            
        drawingPad.repaint();
    }    
    
    /** @return true if mouse position clicked on an image, else false it did not.
     */
    private boolean isImageHit() {
        FPointType fptMouseDown = drawingPad.getMousePositionMeasurement();
        iInfo = drawingPad.getImagePool().getImage(fptMouseDown);
        
        if (iInfo == null)
            return false;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        return true;
    }
    
    // </editor-fold>
}
