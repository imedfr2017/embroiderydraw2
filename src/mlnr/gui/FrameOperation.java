/*
 * FrameOperation.java
 *
 * Created on August 22, 2006, 12:37 PM
 *
 */

package mlnr.gui;

import javax.swing.JFrame;
import mlnr.gui.cpnt.DesignDocument;
import mlnr.util.gui.FileMenuList;

/** Empty class that implements the InterfaceFrameOperation Interface.
 * @author Robert Molnar II
 */
public class FrameOperation implements InterfaceFrameOperation {
    
    public void enableUndoable(boolean enable) {
    }
    
    public void enableRedoable(boolean enable) {
    }
    
    public void enableAdvanceTools(boolean enabled) {
    }
    
    public void enableCopyCut(boolean enabled) {
    }
    
    public void enablePaste(boolean enableDesign, boolean enableNew) {
    }
    
    public void validateLayerPanel() {
    }    

    public void validateImagePanel(boolean reloadTransform) {
    }
    
    public JFrame getFrame() {
        throw new UnsupportedOperationException("FrameOperation::getFrame() is not supported.");
    }

    public void validateImagePanel() {
    }
    
    public void notifyDocumentChanged(boolean changed) {        
    }    
    
    public void notifyDocumentFocus(DesignDocument focusDocument) {
        
    }
    
    public void notifyToolChanged() {
        
    }
    
    public void notifyGuiChange(int gui) {
        
    }
    
    public int getStage() {
        return InterfaceFrameOperation.GUISTAGE_VECTOR;
    }

    public void setZoom(float percentage) {
    }

    public void zoomItem(String item) {
    }

    public FileMenuList getFileMenuList() {
        throw new UnsupportedOperationException("FrameOperation::getFileMenuList() is not supported.");
    }
}
