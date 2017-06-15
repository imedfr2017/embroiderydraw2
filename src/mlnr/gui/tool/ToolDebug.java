/*
 * ToolDebug.java
 *
 * Created on August 16, 2006, 11:55 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;
import javax.swing.*;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;

/** This is a tool used to get debug information from the drawing.
 *
 * @author Robert Molnar II
 */
public class ToolDebug extends AbstractTool {
    
    /** This is the menu when they right click on a layer */
    private JPopupMenu rightClickOnLayer = new JPopupMenu();
    
    /** Creates a new instance of ToolDebug */
    public ToolDebug(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad) {
        super(iFrameOperator, drawingPad);        
        rightClickOnLayer.setLightWeightPopupEnabled(false);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">
    
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Get rectangle of mouse position.
            Rectangle2D.Float fBounds = getPointBounds(getFilterPoint(null));        
            
            // See if there is a line at the place clicked at.
            String lineInfo = drawingPad.getDesign().debugGetInfo(fBounds, false);
            
            // Show line information.
            if (lineInfo != null) {
                System.out.println();
                System.out.println(">>>> TOOLDEBUG ---- Line Info ---- ");
                StringTokenizer sTok = new StringTokenizer(lineInfo, "|");
                while(sTok.hasMoreTokens())
                    System.out.println(sTok.nextToken());
                System.out.println("<<<< END TOOLDEBUG ---- Line Info ---- ");
                
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    // </editor-fold>
    
}
