/*
 * ToolFillColor.java
 *
 * Created on September 20, 2006, 12:54 PM
 *
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.event.MouseEvent;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.ColorPanel;

/**
 *
 * @author Robert Molnar II
 */
public class ToolFillColor extends AbstractTool {
    ColorPanel colorOptions;
    
    /** Creates a new instance of ToolFillColor */
    public ToolFillColor(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, ColorPanel colorOptions) {
        super(iFrameOperator, drawingPad);
        
        this.colorOptions = colorOptions;
    }
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            fillGeneralPath();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            updateCurrentColor();
        }
    }
    
    /** This will fill a general path with color.
     */
    public void fillGeneralPath() {
        drawingPad.getDesign().fill(drawingPad.getMousePositionMeasurement(), colorOptions.getCurrentColor());
        drawingPad.repaint();
    }
    
    /** This will update the current color.
     */
    public void updateCurrentColor() {
        Color c = drawingPad.getDesign().getAreaColor(drawingPad.getMousePositionMeasurement());
        if (c != null)
            colorOptions.setCurrentColor(c);
    }
}