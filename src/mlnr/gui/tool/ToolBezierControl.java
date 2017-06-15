/*
 * ToolBezierControl.java
 *
 * Created on September 11, 2006, 3:04 PM
 *
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import mlnr.draw.BezierInfo;
import mlnr.draw.Graph;
import mlnr.draw.TransformDesign;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.BezierControlPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolBezierControl extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    public static final int STATE_SEARCH = 1;
    public static final int STATE_CONTROLPT = 2;
    public static final int STATE_MOVEPT = 3;
    
    public static final int BEZIERCONTROL_NONE = 0;
    public static final int BEZIERCONTROL_FIRST = 7;
    public static final int BEZIERCONTROL_LAST = 9;
    
    /** This is the size of the rectangle box around the bezier control point if glue is turned on. */
    private static float glueRadius = 0.15f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">

    // Draw objects for the bezier.
    CubicCurve2D.Float fBezier = new CubicCurve2D.Float();
    Rectangle2D.Float fRectCntrl1 = new Rectangle2D.Float();
    Rectangle2D.Float fRectCntrl2 = new Rectangle2D.Float();
    Line2D.Float fLine1 = new Line2D.Float();
    Line2D.Float fLine2 = new Line2D.Float();
    
    // Options for the bezier control.
    BezierControlPanel bezierControlPanel;
    
    // Bezier information.
    BezierInfo bInfo = null;
    
    // State variables.
    int state = STATE_SEARCH;
    int bezierControl = BEZIERCONTROL_NONE;
    
    // The transformation design is saved internally here and not by the DrawingPad. That way this tool 
    // can draw the bezier and its control end points.
    TransformDesign dTransform = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolBezierControl */
    public ToolBezierControl(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, BezierControlPanel bezierControlPanel) {
        super(iFrameOperator, drawingPad);
        
        this.bezierControlPanel = bezierControlPanel;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (state == STATE_SEARCH)
                search();
            else if (state == STATE_CONTROLPT)
                controlpts();
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
        if (state == STATE_MOVEPT)
            state = STATE_CONTROLPT;
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        
        if (state == STATE_MOVEPT)
            dragControlPt();
    }
    
    public void onFinalize(boolean complete) {
        finalizeMovement();
        drawingPad.repaint();
    }   
    
    public void onDrawTool(Graphics2D g2d) {
        if (state != STATE_SEARCH)
            drawBezier(g2d, false);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will search for a bezier.
     */
    private void search() {
        Rectangle2D.Float rBounds = getPointBounds(drawingPad.getMousePositionMeasurement());
        
        // Select a bezier curve.
        if (drawingPad.getDesign().selectBezier(rBounds, bezierControlPanel.isCurrentLayer()) == false)
            return;
        
        // Get the transformation design and set it as the selected items.
        dTransform = TransformDesign.selectItems(drawingPad);
        if (dTransform == null)
            return;
        drawingPad.setSelectedItems(dTransform);
        
        // Get the bezier structure.
        bInfo = dTransform.getBezierInfo(rBounds);
        drawingPad.repaint();
                
        // Set curve, change state to manipulate control points, and repaint.
        updateBezierDrawing();
        state = STATE_CONTROLPT;
        drawingPad.repaint();
    }
    
    /** This will first search for the bezier control point and if found then it will
     * set the state to be able to move it, else it will search for another bezier and the current
     * one will be deselected. If nothing was found then the current bezier will be deselected.
     */
    private void controlpts() {
        FPointType fptMouse = drawingPad.getMousePositionMeasurement();        
        if (fRectCntrl1.contains(fptMouse.x, fptMouse.y)) {
            state = STATE_MOVEPT;
            bezierControl = BEZIERCONTROL_FIRST;
            bInfo.setControlPoint1(fptMouse);
        } else if (fRectCntrl2.contains(fptMouse.x, fptMouse.y)) {
            state = STATE_MOVEPT;
            bezierControl = BEZIERCONTROL_LAST;
            bInfo.setControlPoint2(fptMouse);
        } else {
            finalizeMovement();            
            search();
            drawingPad.repaint();
        }
    }

    private void finalizeMovement() {
        if (state == STATE_SEARCH)
            return;

        // Update bezier and finalize the movement.
        dTransform.updateBezierInfo(bInfo);
        dTransform.finalizeMovement();
        drawingPad.finalizeSelectedItems();
        
        bezierControl = BEZIERCONTROL_NONE;
        state = STATE_SEARCH;
        bInfo = null;
    }
    
    private void dragControlPt() {
        if (bezierControl == BEZIERCONTROL_FIRST)
            bInfo.setControlPoint1(drawingPad.getMousePositionMeasurement());
        else
            bInfo.setControlPoint2(drawingPad.getMousePositionMeasurement());
        
        drawBezier(drawingPad.getTransformedGraphics(), true);
    }

    /** This will draw the current bezier.
     * @param erase is true if it should erase the previous bezier.
     */
    private void drawBezier(Graphics2D g2D, boolean erase) {
        // Setup the graphics.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(strokeDrawing);
        g2D.setColor(Color.BLACK);
        
        // Erase the previous line.
        if (erase) {
            g2D.draw(fBezier);
            g2D.draw(fRectCntrl1);
            g2D.draw(fRectCntrl2);
            g2D.draw(fLine1);
            g2D.draw(fLine2);
        }

        updateBezierDrawing();
        
        g2D.draw(fBezier);
        g2D.draw(fRectCntrl1);
        g2D.draw(fRectCntrl2);
        g2D.draw(fLine1);
        g2D.draw(fLine2);
        
    }
    
    /** This will update all the drawing structures for this bezier curve.
     */
    private void updateBezierDrawing() {
        bInfo.bezierCurve(fBezier);
        
        fRectCntrl1.setRect(bInfo.getControlPoint1().x - glueRadius, bInfo.getControlPoint1().y - glueRadius, glueRadius * 2, glueRadius * 2);
        fRectCntrl2.setRect(bInfo.getControlPoint2().x - glueRadius, bInfo.getControlPoint2().y - glueRadius, glueRadius * 2, glueRadius * 2);
        
        fLine1.setLine(bInfo.getEndPoint1().x, bInfo.getEndPoint1().y, bInfo.getControlPoint1().x, bInfo.getControlPoint1().y);
        fLine2.setLine(bInfo.getEndPoint2().x, bInfo.getEndPoint2().y, bInfo.getControlPoint2().x, bInfo.getControlPoint2().y);
    }
    
    // </editor-fold>
}
