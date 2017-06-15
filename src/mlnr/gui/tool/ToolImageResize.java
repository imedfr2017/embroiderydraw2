/*
 * ToolImageResize.java
 *
 * Created on September 15, 2006, 11:44 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.tool.dlg.DialogResizeSelection;
import mlnr.gui.tool.opt.ImagePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolImageResize extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the image panel that is displayed below the layer panel. */
    ImagePanel imagePanel;
    
    /** This is the selected image to move. */
    ImageInfo iInfo = null;
    
    /** This is the initial size of the image. */
    FPointType fptInitSize = new FPointType();
    
    /** This is the position the mouse was clicked down. */
    FPointType fptMouseDown = new FPointType();
    
    /** This is true if it should resize uniform size. */
    boolean uniformSize = false;
        
    /** Variable used to indicate dragging ok. */
    boolean mouseDrag = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolImageCenter */
    public ToolImageResize(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ImagePanel imagePanel) {
        super(iFrameOperator, drawingPad);
        
        this.imagePanel = imagePanel;
        imagePanel.setDrawingPad(drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
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
        if (mouseDrag) {
            super.mouseDragged(evt);        
            update();
        }
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL)
            uniformSize = true;
    }    

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL)
            uniformSize = false;
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
        
    /** This will resize the image.
     */
    private void imageResize() {
        fptMouseDown = drawingPad.getMousePositionMeasurement();
        ImageInfo ii = drawingPad.getImagePool().getImage(fptMouseDown);        
        if (ii == null)
            return;
        
        DialogResizeSelection dialog = new DialogResizeSelection(iFrameOperator.getFrame(), true);
        dialog.setCurrentSize(ii.getXSize(), ii.getYSize());
        dialog.setVisible(true);
        
        if (dialog.isResize()) {
            ii.setSize(dialog.getWidthInMeasurements(), dialog.getHeightInMeasurements());
            imagePanel.validateImageInfo();
            drawingPad.repaint();
        }
    }
    
    /** This will select the image to move.
     */
    private void select() {
        fptMouseDown = drawingPad.getMousePositionMeasurement();
        iInfo = drawingPad.getImagePool().getImage(fptMouseDown);        
        if (iInfo == null)
            return;
        
        // Set the image selected.
        imagePanel.setSelected(iInfo.getId());
        
        fptInitSize.x = iInfo.getXSize();
        fptInitSize.y = iInfo.getYSize();
        
        mouseDrag = true;
    }
    
    private void update() {        
        if (iInfo == null)
            return;
        
        FPointType fptCurr = drawingPad.getMousePositionMeasurement();
        
        float width = fptInitSize.x + (fptCurr.x - fptMouseDown.x);
        float height = fptInitSize.y + (fptCurr.y - fptMouseDown.y);
        
        if (uniformSize)
            height = width * fptInitSize.y / fptInitSize.x;
        
        iInfo.setSize(width, height);
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
