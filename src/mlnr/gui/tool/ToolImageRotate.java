/*
 * ToolImageRotate.java
 *
 * Created on September 15, 2006, 11:45 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.tool.dlg.DialogGetRotate;
import mlnr.gui.tool.opt.ImagePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolImageRotate extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    ImagePanel imagePanel;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolImageCenter */
    public ToolImageRotate(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ImagePanel imagePanel) {
        super(iFrameOperator, drawingPad);
        
        this.imagePanel = imagePanel;
        imagePanel.setDrawingPad(drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            rotate();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            if (isImageHit())
                imagePanel.popupMenuForImage(evt);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    private void rotate() {        
        ImageInfo iInfo = drawingPad.getImagePool().getImage(drawingPad.getMousePositionMeasurement());        
        if (iInfo == null)
            return;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        DialogGetRotate dialog = new DialogGetRotate(iFrameOperator.getFrame(), true);
        dialog.setVisible(true);
        
        if (dialog.isOk()) {
            iInfo.setRotate(iInfo.getRotate() + dialog.getRadian());
            imagePanel.validate();
            drawingPad.repaint();
        }
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
