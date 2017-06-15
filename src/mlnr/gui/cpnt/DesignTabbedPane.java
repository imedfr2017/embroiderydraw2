/*
 * DesignTabbedPane.java
 *
 * Created on January 5, 2007, 8:57 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.Cursor;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mlnr.EmbroideryDraw;
import mlnr.draw.LayerInfo;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.dlg.DialogImageSettings;
import mlnr.gui.tool.AbstractTool;
import mlnr.util.gui.FileMenuList;

/**
 *
 * @author Robert Molnar II
 */
public class DesignTabbedPane extends JTabbedPane implements ChangeListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the interface to use for the main frame. */
    InterfaceFrameOperation iFrameOperation;
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** @param iFrameOperation this is the interface to use for the main frame. 
     */
    public DesignTabbedPane(InterfaceFrameOperation iFrameOperation) {
        this.iFrameOperation = iFrameOperation;
        
        setOpaque(true);
                
        // This will listen for the changing of documents.
        addChangeListener(this);
        
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Serialize Methods ">
    
    /** This will write the document out to a file as a rxml file.
     * @param saveAs is true if it should perform a 'Save As' operation, else false perform a 'Save' operation.
     */
    public void writeDocument(boolean saveAs) {        
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalStateException("writeDocument(" + saveAs + "): Unable to write document, no documents exist.");
        document.writeDocument(null, saveAs);
    }
    
    /** This will write all the documents out to a file as rxml files. Assumed false to 'save as' operation.
     */
    public void writeAllDocuments() {
        DesignDocument[] arr = getDesignDocuments();
        for (int i=0; i < arr.length; i++)
            arr[i].writeDocument(null, false);
    }
    
    /** This will save the current drawing as a PEM file.
     */
    public void exportDocumentAsPEM() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("exportDocumentAsPEM() document must exist.");        
        
        // save as a PEM.
        document.writePEM(null);
    }
    
    /** This will save the current drawing as a bitmap file.
     */
    public void exportDocumentAsBitmap() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("exportDocumentAsPEM() document must exist.");        
        
        // save as bitmap.
        document.writeBitmap(null);
    }
    
    // </editor-fold>
    
    /** This will print out the current document.
     */
    public void printDocument() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalStateException("Unable to print out document.");
        document.printDocument();
    }
    
    /** @return an array of DesignDocuments that are currently opened.
     */
    public DesignDocument[] getDesignDocuments() {
        DesignDocument[] arr = new DesignDocument[getTabCount()];
        int tabCount = getTabCount();
        for (int i=0; i < tabCount; i++)
            arr[i] = ((DesignDocument)getComponentAt(i));
        
        return arr;
    }
    
    /** @return the current DesignDocument that is currently in use, else null
     * if none in use.
     */
    public DesignDocument getDesignDocument() {
        return (DesignDocument)getSelectedComponent();
    }
    
    /** This will add the DesignDocument to this tabbed pane and it will become focused. Notice
     * when this function is called the stateChanged() will be called which will set the tool
     * for the document.
     * @param document is the DesignDocument to be added to this tabbed pane.
     */
    public void addDesignDocument(DesignDocument document) {
        if (document == null)
            throw new IllegalArgumentException("addDesignPanel:: dPanel cannot be null.");        
        
        // This will remove the tool from the previous DesignDocument.
        DesignDocument ddOld = getDesignDocument();
        if (ddOld != null)
            ddOld.removeTool();
        
        // Link up the DesignDocument to this DesignTabbedPane.
        document.setJTabbedPane(this);
        
        // Add the DesignDocument to the end of the DesignDocuments.
        int tabCount = getTabCount();
        add(document);
        setTabComponentAt(tabCount, new ButtonTab(document));        
        validate();
        
        // Zoom to 1.0f to allow for components to be set in the DesignPanel.
        document.getDesignPanel().zoomTo(1.0f);
        document.getDesignPanel().repaint();
        
        // Focus the incoming DesignDocument.
        setSelectedComponent(document);
    }
    
    /** This will notify the document has changed (saved or not saved).
     * @param changed is true if the document has been changed, else false document
     * is saved.
     */
    public void notifyDocumentChanged(boolean changed) {
        // Do nothing if there are no selected designs.
        int selectedIndex = getSelectedIndex();
        if (selectedIndex == -1)
            return;
        
        // Notify about document change.
        ((ButtonTab)getTabComponentAt(selectedIndex)).notifyDocumentChanged(changed);
    }

    /** This should only be called if the newTool() function will not be called. It will finalize the tool if it is an advance select tool. After that it will
     * change the tool to the SELECT tool. Does nothing unless tool is an advance select tool (translate, rotate, resize, etc..).
     */
    public void finalizeAdvanceSelectTools() {
        // Get the current document.
        DesignDocument document = (DesignDocument)getSelectedComponent();
        if (document == null)
            return;
        // Not an advance select operation tool then just return (keep the current tool).
        if (EmbroideryDraw.getEmbroideryDraw().isAdvanceSelectTool() == false)
            return;
        
        // Set the current document to the new select tool.
        document.setTool(EmbroideryDraw.getEmbroideryDraw().newTool(EmbroideryDraw.TOOL_SELECT, document.getDesignPanel(), true));
    }
    
    /** This will setup the DesignDocument to handle the new tool, which will in turn called EmbroideryDraw.newTool() to
     * setup the tool and which will notify the FrameEmbroideryDraw about the tool change.
     */
    public void newTool(int newTool) {
        // Remove the previous tool.
        DesignDocument []arr = getDesignDocuments();
        for (int i=0; i < arr.length; i++)
            arr[i].removeTool();
        
        // Get the current document.
        DesignDocument document = (DesignDocument)getSelectedComponent();
        if (document == null)
            throw new IllegalStateException("Unable to change the tool since there are no design documents open.");
        
        // Set the current document to the new tool.
        document.setTool(EmbroideryDraw.getEmbroideryDraw().newTool(newTool, document.getDesignPanel()));
    }
    
    /** This will edit the document details.
     */
    public void editDocumentDetails() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("editDocumentDetails() document must exist.");        
        document.editDesignDetails(iFrameOperation.getFrame());
    }
     
    /** This will view the stats on the current design.
     */
    public void viewStats() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("viewStats() document must exist.");        
        document.viewStats();
    }
    
    /** This will resize the current design.
     */
    public void resizeDesign() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("resizeDesign() document must exist.");        
        document.resizeDesign();
    }
    
    /** @return the current document's zoom setting.
     */
    public float getZoom() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("setZoom() document must exist.");        
        return document.getDesignPanel().getZoom();
    }
    
    /** This is called to set the zoom for the current design.
     * @param item should be a ITEM_* constant to indicate which item to zoom on.
     * @return the new zoom percentage.
     */
    public float setZoom(String item) {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("setZoom() document must exist.");        
        
        // Update the zoomings.
        if (item == InterfaceFrameOperation.ITEM_DRAWING)
            document.getDesignPanel().zoomTo((LayerInfo)null, 16.0f);
        else if (item == InterfaceFrameOperation.ITEM_LAYER)
            document.getDesignPanel().zoomTo((LayerInfo)document.getDesignPanel().getDesign().getCurrentLayer(), 16.0f);
        else if (item == InterfaceFrameOperation.ITEM_100)
            document.getDesignPanel().zoomTo(1.0f);
        else
            throw new IllegalArgumentException("setZoom(" + item + ") unknown item to zoom to.");
        
        document.getDesignPanel().repaint();
        
        return document.getDesignPanel().getZoom();
    }

    /** this will force a zoom to take place.
     */
    public void forceZoom() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("forceZoom() document must exist.");        
        
        // Update the zoomings.
        document.getDesignPanel().zoomTo(document.getDesignPanel().getZoom());
        document.getDesignPanel().repaint();
    }
    
    /** This will set the zoom on the current design document.
     * @param percentage is the percentage to set the design document to.
     */
    public void setZoom(float percentage) {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("setZoom() document must exist.");        
        
        // Don't zoom if same percentage.
        if (percentage == document.getDesignPanel().getZoom())
            return;
        
        // Update the zoomings.
        document.getDesignPanel().zoomTo(percentage);
        document.getDesignPanel().repaint();
    }
    
    /** This will attempt to close the current design document.
     */
    public void closeDesign() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalArgumentException("closeDesign() document must exist.");
        
        document.onCloseTab();
    }
    
    /** This will attempt to close all design documents.
     */
    public void closeAllDesigns() {
        DesignDocument []arrayDocs = getDesignDocuments();
        for(int i=0; i < arrayDocs.length; i++) {
            arrayDocs[i].onCloseTab();
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Image Methods ">
    
    /** This will load an image into the current DesignDocument's DrawingPad.
     */
    public void loadImage() {
        getDesignDocument().getDesignPanel().getDrawingPad().loadImage();
        iFrameOperation.validateImagePanel();
    }
    
    /** This will restore all images in the current DesignDocument's DrawingPad.
     */
    public void restoreAllImages() {
        getDesignDocument().getDesignPanel().getDrawingPad().restoreAllImages();
        iFrameOperation.validateImagePanel();
    }
    
    /** This will toggle all images on/off.
     */
    public void toggleImagesOnOff() {
        DrawingPad.toggleImagesOnOff();
        getDesignDocument().getDesignPanel().getDrawingPad().repaint();
        iFrameOperation.validateImagePanel();
    }
    
    /** This will toggle all image lightening on/off.
     */
    public void toggleImagesLighteningOnOff() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(this, "Are you sure want to lighten on/off? Can take a few seconds to perform if many images are loaded into Embroidery Draw.",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            
            // Set cursor.
            Cursor oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Toggle lightening on/off for each drawing pad.
            DrawingPad.toggleImagesLighteningOnOff();
            DesignDocument []arr = getDesignDocuments();
            for (int i = 0; i < arr.length; i++) {
                arr[i].getDesignPanel().getDrawingPad().reloadImagesAndTransform();
            }
            iFrameOperation.validateImagePanel();            
            
            // Restore cursor.
            setCursor(oldCursor);
        }
    }
    
    public void setImagesSettings() {
        DialogImageSettings dialog = new DialogImageSettings(iFrameOperation.getFrame(), true);
        dialog.setVisible(true);
        if (dialog.isNeedReload()) {
            // Set cursor.
            Cursor oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Reload all images.
            DesignDocument []arr = getDesignDocuments();
            for (int i = 0; i < arr.length; i++)
                arr[i].getDesignPanel().getDrawingPad().reloadImagesAndTransform();
            iFrameOperation.validateImagePanel();
            
            // Restore cursor.
            setCursor(oldCursor);
        }
        
    }
    
    public void removeAllImages() {
        getDesignDocument().getDesignPanel().getDrawingPad().removeAllImages();
        iFrameOperation.validateImagePanel();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Edit Methods ">
    
    /** This will undo one operation in the DesignDocument.
     */
    public void editUndo() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalStateException("editUndo(): No document exists.");
        
        // Undo the operation.
        AbstractTool newTool = EmbroideryDraw.getEmbroideryDraw().editUndo(document.getDesignPanel());
        document.setTool(newTool);
        
        // Repaint the design.
        document.getDesignPanel().repaint();
    }
    
    /** This will redo one operation in the DesignDocument.
     */
    public void editRedo() {
        DesignDocument document = getDesignDocument();
        if (document == null)
            throw new IllegalStateException("editUndo(): No document exists.");
        
        // Redo the operation.
        AbstractTool newTool = EmbroideryDraw.getEmbroideryDraw().editRedo(document.getDesignPanel());
        document.setTool(newTool);
        
        // Repaint the design.
        document.getDesignPanel().repaint();
        
    }
    
    public void editCut() {
        
    }
    
    public void editCopy() {
        
    }
    
    public void editPasteAsNewDesign() {
        
    }
    
    public void editPasteIntoCurrentLayer() {
        
    }
    
    public void editPasteIntoNewLayer() {
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface ChangeListener ">

    public void stateChanged(ChangeEvent e) {
        // Remove the previous tool.
        DesignDocument []arr = getDesignDocuments();
        for (int i=0; i < arr.length; i++)
            arr[i].removeTool();
        
        // Get the new document.
        DesignDocument document = (DesignDocument)getSelectedComponent();

        // Set the new tool and override finalize because of switching to a new design.
        if (document != null) {
            document.setTool(EmbroideryDraw.getEmbroideryDraw().newTool(EmbroideryDraw.TOOL_CURRENT, document.getDesignPanel(), true));
        }
        
        // Notify the frame about this change in documents.
        iFrameOperation.notifyDocumentFocus(document);        
    }
    
    // </editor-fold>

    /** This will set the gui stage for the current document.
     * @param guiStage is the FrameEmbroideryDraw.GUI* value.
     */
    public void setGuiStage(int guiStage) {
        DesignDocument document = (DesignDocument)getSelectedComponent();
        if (document == null)
            throw new IllegalStateException("setGuiStage: document must exist.");
        document.setGuiStage(guiStage);
    }    

    /** @return the current drawing pad.
     */
    public DrawingPad getCurrentDrawingPad() {
        DesignDocument document = (DesignDocument)getSelectedComponent();
        if (document == null)
            throw new IllegalStateException("getCurrentDrawingPad: document must exist.");
        return document.getDesignPanel().getDrawingPad();
    }
}
