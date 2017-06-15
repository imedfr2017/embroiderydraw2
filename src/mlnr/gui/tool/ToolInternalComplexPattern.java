/*
 * ToolComplexPattern.java
 *
 * Created on November 13, 2006, 12:16 PM
 *
 */

package mlnr.gui.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mlnr.draw.ComplexPattern;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.InternalPatternPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolInternalComplexPattern extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    public static final int STATE_PLACEMENT = 1;
    public static final int STATE_RESIZE = 2;
    public static final int STATE_ROTATE = 3;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the pattern panel containing the different patterns to draw and draw options. */
    InternalPatternPanel iPatternPanel;
    
    /** This is the master pattern copy and should not be modified. */
    ComplexPattern masterCopy;
    
    /** This is the current state. */
    int state = STATE_PLACEMENT;
    
    /** This is the DesignTransform which contains the design to draw. */
    TransformDesign design = null;
    
    BasicStroke bStroke = new BasicStroke(0.0f);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolBezierControl
     * @param iFrameOperator used to operate the frame.
     * @param drawingPad is the DrawingPad used to draw the pattern on.
     * @param iPatternPanel is the Panel containing the options for this tool.
     * @param borderTitleName is the border title name.
     */
    public ToolInternalComplexPattern(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, 
            InternalPatternPanel iPatternPanel, ComplexPattern pattern, String borderTitleName) {
        super(iFrameOperator, drawingPad);
        this.iPatternPanel = iPatternPanel;
        this.masterCopy = pattern;
        
        iPatternPanel.setBorderTitle(borderTitleName);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        if (evt.getButton() == MouseEvent.BUTTON1) {
            switch (state) {
                case STATE_PLACEMENT:
                    begin();
                    break;
                case STATE_RESIZE:
                    rotate();
                    break;
                case STATE_ROTATE:
                    end();
                    break;
            }
        } else if (evt.getButton() == MouseEvent.BUTTON3)
            cancel();
    }
    
    public void mouseMoved(MouseEvent evt) {
        super.mouseMoved(evt);
        draw();
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        draw();
    }
    
    public void keyPressed(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
    }
    
    public void onDrawTool(Graphics2D g2d) {
        if (design != null) {
            g2d.setStroke(bStroke);
            g2d.setXORMode(Color.WHITE);
            design.draw(g2d, false);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will begin the placement of the pattern.
     */
    private void begin() {
        // Get a copy of the currently selected pattern.
        design = masterCopy.toTransformDesign();
        
        // Set the position to the current mouse position.
        FPointType fpt = getFilterPoint(null, true);
        design.onTranslateAbsolute(fpt, iPatternPanel.isCenterChecked());
        design.finalizeMovement();
        
        // Begin scaling.
        if (iPatternPanel.isOriginalSizeChecked() == false) {
            calculate();
            design.beginScaling();
            design.setBeginPosition(fpt);
            design.onResizeAnyDirection(fpt, -0.99f, -0.99f);
            design.finalizeMovement();
            calculate();
            
            // Change state to resizing.
            state = STATE_RESIZE;
        } else {
            if (iPatternPanel.isNoRotateChecked())
                end();
            else {
                calculate();
                design.setBeginPosition(fpt);
                state = STATE_ROTATE;
            }
        }
        
        drawingPad.repaint();
    }
    
    /** This will draw the pattern.
     */
    private void draw() {
        if (state == STATE_PLACEMENT)
            return;
        
        // Perform the operation on the pattern.
        FPointType fpt = getFilterPoint(null, true);
        switch (state) {
            case STATE_RESIZE:
                if (iPatternPanel.isAspectRatioChecked()) {
                    if (iPatternPanel.isCenterChecked())
                        design.onResizeUniform(fpt, -1.0f, -1.0f);
                    else
                        design.onResizeUniformUpperLeft(fpt, -1.0f, -1.0f);
                } else {
                    if (iPatternPanel.isCenterChecked())
                        design.onResizeAnyDirection(fpt, -1.0f, -1.0f);
                    else
                        design.onResizeAnyDirectionUpperLeft(fpt, -1.0f, -1.0f);
                }
                break;
            case STATE_ROTATE:
                design.onRotate(fpt, 0.0f);
                break;
        }
        
        // Draw the pattern.
        Graphics2D g2D = drawingPad.getTransformedGraphics();
        g2D.setStroke(bStroke);
        g2D.setXORMode(Color.WHITE);
        design.draw(g2D, true);
    }
    
    /** This will begin the rotating of the pattern.
     */
    private void rotate() {
        FPointType fpt = getFilterPoint(null, true);
        design.finalizeMovement();
        
        // No rotation so we are done.
        if (iPatternPanel.isNoRotateChecked()) {
            end();
            return;
        }
        
        calculate();
        design.setBeginPosition(fpt);
        state = STATE_ROTATE;
    }
    
    /** This will calculate the DesignTransform to Center or Upper-Left of the pattern.
     */
    private void calculate() {
        if (iPatternPanel.isCenterChecked())
            design.calculateCenter();
        else
            design.calculateUpperLeft();
    }
    
    /** This will end the placement of the pattern. It will place the pattern into the DrawingPad's Design.
     */
    private void end() {
        design.finalizeMovement();
        
        // Add the transformed pattern to the design.
        drawingPad.getDesign().add(design, false);
            
        state = STATE_PLACEMENT;
        design = null;
        drawingPad.repaint();
    }
    
    /** This will cancel the drawing.
     */
    private void cancel() {
        state = STATE_PLACEMENT;
        design = null;
        drawingPad.repaint();
    }
    
    // </editor-fold>
        
}
