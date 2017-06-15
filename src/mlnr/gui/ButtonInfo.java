/*
 * ButtonInfo.java
 *
 * Created on January 8, 2007, 5:11 PM
 *
 */

package mlnr.gui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

/**
 *
 * @author Robert Molnar II
 */
public class ButtonInfo {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This field is used for menu items, but doesn't do anything. */
    public static final int NO_ITEM_NUMBER = -200000000;
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    int toolId;
    
    JComponent abstractButton;
    
    boolean activeGuiStageEmpty = false;
    
    boolean activeGuiStageVector = false;
   
    boolean activeGuiStageColor = false;
    
    // </editor-fold>
    
    /** Creates a new instance of ButtonInfo. Uses NO_ITEM_NUMBER for toolId.
     * @param abstractButton is the button corresponding to the toolId.
     * @param activeGuiStageEmpty is true if button should be active when in empty stage, or false if it should be greyed.
     * @param activeGuiStageVector is ture if button should be active when in vector, or false if it should be greyed.
     * @param activeGuiStageColor is true if button should be active when in color, or false if it should be greyed.
     */
    public ButtonInfo(JComponent abstractButton, boolean activeGuiStageEmpty, boolean activeGuiStageVector, boolean activeGuiStageColor) {
        this.toolId = NO_ITEM_NUMBER;
        this.abstractButton = abstractButton;
        this.activeGuiStageEmpty = activeGuiStageEmpty;
        this.activeGuiStageVector = activeGuiStageVector;
        this.activeGuiStageColor = activeGuiStageColor;
    }
    
    /** Creates a new instance of ButtonInfo 
     * @param toolId is the EmbroideryDraw.* tool id.
     * @param abstractButton is the button corresponding to the toolId.
     * @param activeGuiStageEmpty is true if button should be active when in empty stage, or false if it should be greyed.
     * @param activeGuiStageVector is ture if button should be active when in vector, or false if it should be greyed.
     * @param activeGuiStageColor is true if button should be active when in color, or false if it should be greyed.
     */
    public ButtonInfo(int toolId, JComponent abstractButton, boolean activeGuiStageEmpty, boolean activeGuiStageVector, boolean activeGuiStageColor) {
        this.toolId = toolId;
        this.abstractButton = abstractButton;
        this.activeGuiStageEmpty = activeGuiStageEmpty;
        this.activeGuiStageVector = activeGuiStageVector;
        this.activeGuiStageColor = activeGuiStageColor;
    }
    
    /** This will create a dummy ButtonInfo.
     * @param toolId is the EmbroideryDraw.* tool id.
     */
    public static ButtonInfo newDummy(int toolId) {
        return new  ButtonInfo(toolId, null, false, false, false);
    }
    
    /** This will set the button selected.
     * @param selected is the value to set the button to.
     */
    public void setSelected(boolean selected) {
        if (abstractButton == null)
            return;
        if (abstractButton instanceof AbstractButton)
            ((AbstractButton)abstractButton).setSelected(selected);
    }
    
    /** This will (dis)enable the button based on if it is active for the gui stage.
     * @param guiStage is the FrameEmbroideryDraw.GUISTAGE_*.
     */
    public void setEnabled(int guiStage) {
        // Dummy ButtonInfo.
        if (abstractButton == null)
            return;
        
        switch (guiStage) {
            case FrameEmbroideryDraw.GUISTAGE_COLOR:
                abstractButton.setEnabled(activeGuiStageColor);
                break;
            case FrameEmbroideryDraw.GUISTAGE_EMPTY:
                abstractButton.setEnabled(activeGuiStageEmpty);
                break;
            case FrameEmbroideryDraw.GUISTAGE_VECTOR:
                abstractButton.setEnabled(activeGuiStageVector);
                break;
            default:
                throw new IllegalArgumentException("setEnabled(" + guiStage + ") unknown guiStage.");
        }
    }
    
    /**  @param guiStage is the gui stage to see if it this button is enabled.
     * @return true if the button is enabled for this gui stage.
     */
    public boolean isEnabled(int guiStage) {
        switch (guiStage) {
            case FrameEmbroideryDraw.GUISTAGE_COLOR:
                return activeGuiStageColor;
            case FrameEmbroideryDraw.GUISTAGE_EMPTY:
                return activeGuiStageEmpty;
            case FrameEmbroideryDraw.GUISTAGE_VECTOR:
                return activeGuiStageVector;
        }
        
        throw new IllegalArgumentException("isEnabled(" + guiStage + ") unknown guiStage.");
    }
}
