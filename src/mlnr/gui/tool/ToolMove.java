/*
 * ToolMove.java
 *
 * Created on August 25, 2006, 11:31 AM
 *
 */

package mlnr.gui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.TranslatePanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolMove extends AbstractTool {
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    TranslatePanel translatePanel;
    boolean bLeftMouseDown = false;
    boolean completed = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolMove */
    public ToolMove(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, TranslatePanel translatePanel) {
        super(iFrameOperator, drawingPad);
        this.translatePanel = translatePanel;
        this.translatePanel.setMoveTool(this);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            startTranslate();
    }
    
    public void mouseReleased(MouseEvent evt) {        
        if (evt.getButton() == MouseEvent.BUTTON1)
            finishTranslate();       
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (bLeftMouseDown) {
            translate();
            drawingPad.drawSelectedItems(drawingPad.getTransformedGraphics());
        }
    }
    
    public void onFinalize(boolean complete) {
        complete(complete);
    }
    
    public void onCompleteFinalize() {
        complete(true);
        completed = true;
    }
    
    // </editor-fold>
     
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will translate the selected items.
     */
    private void translate() {
        // This is the filtered mouse position.
        FPointType fptDown = getFilterPoint(null);
        
        if (translatePanel.isMovementAnyDirection())
            drawingPad.getSelectedItems().onTranslateAnyDirection(fptDown);
        else if (translatePanel.isMovementVertical())
            drawingPad.getSelectedItems().onTranslateVertical(fptDown.y);
        else if (translatePanel.isMovementHorizontal())
            drawingPad.getSelectedItems().onTranslateHorizontal(fptDown.x);
        else if (translatePanel.isMovementDegree())
            drawingPad.getSelectedItems().onTranslateDegree(fptDown, (float)Math.toRadians(360 - translatePanel.getDegree()));
        else
            throw new IllegalStateException("ToolMove::translate() Unknown translate method.");
    }
    
    /** This will start the translate moving of the selected items.
     */
    private void startTranslate() {
        if (drawingPad.isSelectedItems() == false)
            throw new IllegalStateException("ToolMove::startTranslate cannot move since there is no selected items.");
        
        // This is the filtered mouse position.
        FPointType fptDown = getFilterPointDesign(drawingPad.getDesign(), true);
        // Set the start position for the selected items.
        drawingPad.getSelectedItems().setBeginPosition(fptDown);
        // Mouse is down and ready to rumble.
        bLeftMouseDown = true;
    }
    
    /** This will finish the translate moving of the selected items.
     */
    private void finishTranslate() {
        if (!bLeftMouseDown)
            return;
                
        drawingPad.getSelectedItems().finalizeMovement();
        bLeftMouseDown = false;        
    }

    /** This will complete the translating.
     */
    private void complete(boolean complete) {
        if (completed)
            return;
        
        drawingPad.getSelectedItems().finalizeMovement();
        
        if (complete) {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
            drawingPad.finalizeSelectedItems();
        }
        
        drawingPad.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Translate Methods ">
    
    public void onMoveToCenterOfDesign() {                
        translateToLocation(drawingPad.getDesign().getWidth() / 2, drawingPad.getDesign().getHeight() / 2);
    }
    
    public FPointType getCenterOfSelectedItems() {
        // Get the center point of the selected items.
        Rectangle2D.Float fRect = drawingPad.getSelectedItems().getBounds2D();
        FPointType fptCenterSelected = new FPointType();
        fptCenterSelected.x = fRect.x + fRect.width / 2;
        fptCenterSelected.y = fRect.y + fRect.height / 2;
        
        return fptCenterSelected;
    }
    
    public void translateToLocation(float x, float y) {
        // Finalize the movement.
        drawingPad.getSelectedItems().finalizeMovement();
        
        // Get the center point of the selected items.
        FPointType fptCenterSelected = getCenterOfSelectedItems();
        
        // Set begin position to the center of the selected items.
        drawingPad.getSelectedItems().setBeginPosition(fptCenterSelected);
        
        // Get the center of the design.
        FPointType fptCenterDesign = new FPointType();
        fptCenterDesign.x = x;
        fptCenterDesign.y = y;
        drawingPad.getSelectedItems().onTranslateAnyDirection(fptCenterDesign);
        
        // Finalize the movement.
        drawingPad.getSelectedItems().finalizeMovement();
        drawingPad.repaint();
    }            
    
    // </editor-fold>
}
