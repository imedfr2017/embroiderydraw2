/*
 * CopyCutPaste.java
 *
 * Created on January 11, 2007, 9:06 PM
 *
 */

package mlnr.gui;

import mlnr.EmbroideryDraw;
import mlnr.draw.DrawingDesign;
import mlnr.draw.TransformDesign;
import mlnr.gui.cpnt.DesignDocument;
import mlnr.gui.cpnt.LayerPanel;

/**
 *
 * @author Robert Molnar II
 */
public class CopyCutPaste {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the currently selected items. null:: no items. */
    TransformDesign design = null;
    
    /** Used to operate the copy, cut, paste buttons. */
    InterfaceFrameOperation iFrameOperation;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of CopyCutPaste */
    public CopyCutPaste(InterfaceFrameOperation iFrameOperation) {
        this.iFrameOperation = iFrameOperation;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gui Methods ">
    
    /** This is called when the gui stage is changed.
     * @param guiStage is the FrameEmbroideryDraw.GUISTAGE_*
     */
    void onGuiStageChange(int guiStage) {
        if (design == null) {
            iFrameOperation.enablePaste(false, false);
            return;
        }
        
        if (guiStage == FrameEmbroideryDraw.GUISTAGE_EMPTY) {
                iFrameOperation.enablePaste(false, true);
            
        } else if (guiStage == FrameEmbroideryDraw.GUISTAGE_VECTOR) {
                iFrameOperation.enablePaste(true, true);
            
        } else if (guiStage == FrameEmbroideryDraw.GUISTAGE_COLOR) {
                iFrameOperation.enablePaste(false, true);            
        }                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Copy, Cut, and Paste Methods ">
    
    /** This will copy any selected items.
     */
    public void onCopy(DesignDocument dDocument) {        
        // Get the selected items.
        design = dDocument.getDesignPanel().getDesign().copySelectedItems();
        
        // Make sure something was selected.
        if (design.getLineCount() == 0)
            return;
        
        // Enable the paste options.
        iFrameOperation.enablePaste(true, true);
    }
    
    /** This will cut any selected items.
     */
    public void onCut(DesignDocument dDocument) {
        DrawingDesign d = dDocument.getDesignPanel().getDesign();
        
        // Get the selected items.
        design = dDocument.getDesignPanel().getDesign().copySelectedItems();
        
        // Make sure something was selected.
        if (design.getLineCount() == 0)
            return;
        
        // Remove the selected items.
        d.deleteSelectedLines(false);
        
        // Enable the paste options.
        dDocument.getDesignPanel().getDrawingPad().repaint();
        iFrameOperation.enablePaste(true, true);
    }
    
    /** This will paste the current items into the design.
     */
    public void onPasteIntoCurrentLayer(DesignDocument dDocument) {
        if (design == null)
            throw new IllegalStateException("onPasteIntoCurrentLayer(): Nothing to paste.");        
        DrawingDesign d = dDocument.getDesignPanel().getDesign();        
        
        // Switch over to the select tool. By doing a line tool first it will force any tool to finalize completely needed for the advance
        // tools.
        dDocument.setTool(EmbroideryDraw.getEmbroideryDraw().newTool(EmbroideryDraw.TOOL_LINE, dDocument.getDesignPanel()));
        dDocument.setTool(EmbroideryDraw.getEmbroideryDraw().newTool(EmbroideryDraw.TOOL_SELECT, dDocument.getDesignPanel()));
        
        // Duplicate the design and place into the current layer.
        TransformDesign dTransformCopy = design.duplicate();
        dTransformCopy.setMoved(true);
        dTransformCopy.setAllTransformable();
        
        dDocument.getDesignPanel().getDrawingPad().setSelectedItems(dTransformCopy);
        dDocument.getDesignPanel().getDrawingPad().repaint();
        
        // Enable advance tools.
        iFrameOperation.enableAdvanceTools(true);
    }

    /** This will paste the current items into a new layer if the user so chooses.
     */
    void onPasteIntoNewLayer(LayerPanel panelLayer, DesignDocument designDocument) {
        // User cancelled the new layer.
        if (panelLayer.newLayer() == false)
            return;
        onPasteIntoCurrentLayer(designDocument);
    }
    
    // </editor-fold>    
}
