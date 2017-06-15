/*
 * ToolImageDelete.java
 *
 * Created on September 15, 2006, 11:44 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.tool.opt.ImagePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolImageDelete extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    ImagePanel imagePanel;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolImageCenter */
    public ToolImageDelete(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ImagePanel imagePanel) {
        super(iFrameOperator, drawingPad);
        
        this.imagePanel = imagePanel;
        imagePanel.setDrawingPad(drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            delete();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            if (isImageHit())
                imagePanel.popupMenuForImage(evt);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    private void delete() {        
        ImageInfo iInfo = drawingPad.getImagePool().getImage(drawingPad.getMousePositionMeasurement());        
        if (iInfo == null)
            return;
        
        // Make sure if it is ok to delete the image.
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(iFrameOperator.getFrame(), "Are you sure want to delete [" + iInfo.getName() + "] image?", 
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION)
            return;
        
        // Delete, update image panel, and repaint drawing pad.
        drawingPad.getImagePool().delete(iInfo.getId());
        imagePanel.validate();
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
