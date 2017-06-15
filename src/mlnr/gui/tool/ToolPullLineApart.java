/*
 * ToolPullLineApart.java
 *
 * Created on September 11, 2006, 3:03 PM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.PullLineApartPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolPullLineApart extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    PullLineApartPanel pullLinePanel;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolPullLineApart */
    public ToolPullLineApart(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, PullLineApartPanel pullLinePanel) {
        super(iFrameOperator, drawingPad);
        
        this.pullLinePanel = pullLinePanel;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1 && drawingPad.isSelectedItems() == false)
            pullLine();
    }

    public void mouseReleased(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            finalizeMovement();
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        
        if (drawingPad.isSelectedItems()) {
            drawingPad.getSelectedItems().onTranslateAnyDirection(getFilterPoint(null, pullLinePanel.isCurrentLayer()));
            drawingPad.drawSelectedItems(drawingPad.getTransformedGraphics());
        }
    }
    
    public void onFinalize(boolean complete) {
        finalizeMovement();
    }   
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will start the pull line apart.
     */
    private void pullLine() {
        // Only current layer?
        boolean currLayer = pullLinePanel.isCurrentLayer();
        
        // Get the mouse position.
        FPointType fptMousePos = drawingPad.getMousePositionMeasurement();
        
        // Select on the vertex and get the selected items as a TransformDesign.
        drawingPad.getDesign().selectLines(getPointBounds(fptMousePos), true, currLayer);
        TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
        
        // If the user clicked on a line then pull it apart.
        if (dTransform != null) {
            dTransform.setMoved(currLayer);
            dTransform.setBeizerControls(false);
            
            drawingPad.setSelectedItems(dTransform);
            dTransform.pullLineApart(fptMousePos);
            dTransform.setBeginPosition(fptMousePos);
        }        
        
        drawingPad.repaint();
    }
    
    /** This will finalize the movement.
     */
    private void finalizeMovement() {
        if (drawingPad.isSelectedItems()) {
            drawingPad.getSelectedItems().finalizeMovement();
            drawingPad.finalizeAddPoint();
            drawingPad.repaint();
        }
    }
    
    // </editor-fold>
}
