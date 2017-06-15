/*
 * ToolDelete.java
 *
 * Created on August 11, 2006, 6:21 PM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.DeletePanel;

/**
 *
 * @author Robert Molnar II
 */
public class ToolDelete extends AbstractTool {
    DeletePanel deleteOptions;
            
    /** Creates a new instance of ToolDelete */
    public ToolDelete(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, DeletePanel deleteOptions) {
        super(iFrameOperator, drawingPad);
        
        this.deleteOptions = deleteOptions;
    }
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            delete();
        }
    }
    
    /** This will delete items at the location.
     */
    private void delete() {        
        // Get rectangle of mouse position.
        Rectangle2D.Float fBounds = getPointBounds(getFilterPoint(null, deleteOptions.isCurrentLayer()));
               
        // Select the items and then delete them.
        TransformDesign.select(drawingPad, deleteOptions, fBounds);
        drawingPad.getDesign().deleteSelectedLines(false);
        drawingPad.repaint();
        
        // repaint.
        drawingPad.repaint();
    }    
}
