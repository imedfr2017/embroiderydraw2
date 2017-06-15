/*
 * ToolConnectRMolnar.java
 *
 * Created on September 11, 2006, 3:07 PM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.ConnectRMolnarPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolConnectRMolnar extends AbstractTool {
    ConnectRMolnarPanel connectPanel;
    
    /** Creates a new instance of ToolConnectRMolnar */
    public ToolConnectRMolnar(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ConnectRMolnarPanel connectPanel) {
        super(iFrameOperator, drawingPad);
        
        this.connectPanel = connectPanel;
    }
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            connect();
        else if (evt.getButton() == MouseEvent.BUTTON3) 
            disconnect();
    }
    
    public void onFinalize(boolean complete) {
    }
    
    /** This will connect two RMolnar curves together.
     */
    private void connect() {
        // Only current layer?
        boolean currLayer = connectPanel.isCurrentLayer();
        
        // Get the mouse position.
        FPointType fptMousePos = drawingPad.getMousePositionMeasurement();
        
        // Select on the vertex and get the selected items as a TransformDesign.
        drawingPad.getDesign().selectVertices(getPointBounds(fptMousePos), true, currLayer);
        TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
        
        // If the user clicked on a vertex then attemp to connect the RMolnar curves.
        if (dTransform != null) {
            drawingPad.setSelectedItems(dTransform);
            
            if (dTransform.connect(fptMousePos) == TransformDesign.CONNECT_TOO_MANY_CURVES) {
                // There existed multiple RMolnar curves at this point.
                JOptionPane message = new JOptionPane();
                message.showMessageDialog(iFrameOperator.getFrame(), "Cannot connect curve at point, too many curves. Point must only have two curves connected to it.");                
            }
            drawingPad.finalizeSelectedItems();
        }        
        
        drawingPad.repaint();
    }
    
    private void disconnect() {
        // Only current layer?
        boolean currLayer = connectPanel.isCurrentLayer();
        
        // Get the mouse position.
        FPointType fptMousePos = drawingPad.getMousePositionMeasurement();
        
        // Select on the vertex and get the selected items as a TransformDesign.
        drawingPad.getDesign().selectVertices(getPointBounds(fptMousePos), true, currLayer);
        TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
        
        // If the user clicked on a vertex then attemp to disconnect the RMolnar curves.
        if (dTransform != null) {
            drawingPad.setSelectedItems(dTransform);
            dTransform.disconnect(fptMousePos);
            drawingPad.finalizeSelectedItems();
        }        
        
        drawingPad.repaint();
    }       
}
