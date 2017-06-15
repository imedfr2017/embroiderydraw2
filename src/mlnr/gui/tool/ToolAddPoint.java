/*
 * ToolAddPoint.java
 *
 * Created on August 17, 2006, 8:12 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.AddPointPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolAddPoint extends AbstractTool {
    AddPointPanel addPointPanel;
    
    /** Creates a new instance of ToolAddPoint */
    public ToolAddPoint(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, AddPointPanel addPointPanel) {
        super(iFrameOperator, drawingPad);
        
        this.addPointPanel = addPointPanel;
    }
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            addPoint();
        }
    }
    
    /** This will add a point to the design.
     */
    private void addPoint() {               
        // Is current layer?
        boolean currLayer = addPointPanel.isCurrentLayer();
        
        // Get the mouse position.
        FPointType fptMousePos = drawingPad.getMousePositionMeasurement();
        
        // If user clicked on a vertex then do not proceed.
        if (drawingPad.getDesign().isPointWithinVertex(fptMousePos, currLayer))
            return;
        
        // Select the line and get the selected item as a TransformDesign.
        drawingPad.getDesign().selectLines(getPointBounds(fptMousePos), true, currLayer);        
        TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
        
        // If the line exists then break it up.
        if (dTransform != null) {
            drawingPad.setSelectedItems(dTransform);
            dTransform.addPoint(fptMousePos);
            drawingPad.finalizeSelectedItems();
        }        
        
        drawingPad.repaint();
    }
}
