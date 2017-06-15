/*
 * ToolSimpleMover.java
 *
 * Created on August 22, 2006, 12:03 PM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.SelectPanel;
import mlnr.type.FPointType;

/** This is the tool that will move one item at a time.
 * @author Robert Molnar II
 */
public class ToolSimpleMover extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the select panel. */
    SelectPanel selectPanel;
    
    /** True if something has been selected and needs to be moved. */
    boolean selected = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolSimpleMover */
    public ToolSimpleMover(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, SelectPanel selectPanel) {
        super(iFrameOperator, drawingPad);
        
        this.selectPanel = selectPanel;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        
        if (evt.getButton() == MouseEvent.BUTTON1)
            select();
    }
    
    public void mouseReleased(MouseEvent evt) {
        super.mousePressed(evt);
        
        if (evt.getButton() == MouseEvent.BUTTON1)
            finalizeMovement();
    }
    
    public void mouseMoved(MouseEvent evt) {
        super.mouseMoved(evt);
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        
        if (drawingPad.isSelectedItems()) {
            drawingPad.getSelectedItems().onTranslateAnyDirection(getFilterPoint(null, selectPanel.isCurrentLayer()));
            drawingPad.drawSelectedItems(drawingPad.getTransformedGraphics());
        }
    }
    
    public void onFinalize(boolean complete) {
        finalizeMovement();
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will select something from the drawing.
     */
    private void select() {
        // Get rectangle of mouse position.
        Rectangle2D.Float fBounds = getPointBounds(getFilterPoint(null, selectPanel.isCurrentLayer()));
        
        // This is the filtered mouse position.
        FPointType fptFilter = getFilterPoint(null, selectPanel.isCurrentLayer());
        
        // Convert the selected lines and vertices into a TransformGraph.
        TransformDesign.select(drawingPad, selectPanel, fBounds, true);
        TransformDesign dTransform = TransformDesign.selectItems(drawingPad);
        
        // If there exists items then begin moving them.
        if (dTransform != null) {
            dTransform.setBeginPosition(fptFilter);
            drawingPad.setSelectedItems(dTransform);
            drawingPad.repaint();
        }
    }
    
    /** This will finalize the movement.
     */
    private void finalizeMovement() {
        if (drawingPad.isSelectedItems()) { 
            drawingPad.getSelectedItems().finalizeMovement();
            drawingPad.finalizeSelectedItems();
            drawingPad.setSelectedItems(null);
            drawingPad.repaint();
        }
    }
    
    // </editor-fold>
    
}