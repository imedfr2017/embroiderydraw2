/*
 * ToolImageCenter.java
 *
 * Created on September 15, 2006, 11:43 AM
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
public class ToolImageCenter extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    ImagePanel imagePanel;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolImageCenter */
    public ToolImageCenter(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ImagePanel imagePanel) {
        super(iFrameOperator, drawingPad);
        
        this.imagePanel = imagePanel;
        imagePanel.setDrawingPad(drawingPad);
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            center();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            if (isImageHit())
                imagePanel.popupMenuForImage(evt);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    private void center() {        
        ImageInfo iInfo = drawingPad.getImagePool().getImage(drawingPad.getMousePositionMeasurement());        
        if (iInfo == null)
            return;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        // Center, update image panel, and repaint drawing pad.
        iInfo.setPosition(drawingPad.getDesign().getWidth() / 2.0f, drawingPad.getDesign().getHeight() / 2.0f);
        imagePanel.validateImageInfo();            
        drawingPad.repaint();
    }
    
    /** @return true if mouse position clicked on an image, else false it did not.
     */
    private boolean isImageHit() {
        FPointType fptMouseDown = drawingPad.getMousePositionMeasurement();
        ImageInfo iInfo = drawingPad.getImagePool().getImage(fptMouseDown);
        
        if (iInfo == null)
            return false;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        return true;
    }
    
    // </editor-fold>
}
