/*
 * ToolBezier.java
 *
 * Created on August 3, 2006, 12:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import mlnr.draw.BezierInfo;
import mlnr.draw.TransformGraph;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.type.FPointType;
import mlnr.util.InterfaceUndoItem;
import mlnr.util.UndoSystem;

/**
 *
 * @author Robert Molnar II
 */
public class ToolBezier extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    BezierInfo iBezier = null;
    TransformGraph gBezier = null;
    CubicCurve2D.Float fBezier = new CubicCurve2D.Float();
    int state = STATE_NONE;
    
    /** This is a system that will perform the undo/redo for the operations of adding into the*/
    UndoSystemBezier undoSystem = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    static final int STATE_NONE=0;
    static final int STATE_RIGHT_END_PT=1;
    static final int STATE_LEFT_CONTROL_PT=2;
    static final int STATE_RIGHT_CONTROL_PT=3;
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolLine */
    public ToolBezier(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad) {
        super(iFrameOperator, drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        
        // Get the mouse position.
        FPointType fptPressed = getFilterPoint(gBezier);
                    
        // Process button.
        int button = evt.getButton();
        if (isBezierFinished(fptPressed)) {
            finishBezier();  
        } else if (button == MouseEvent.BUTTON1) {
            if (state == STATE_NONE)
                createNewGraph(fptPressed);
            else
                processBezier(fptPressed);
        } else if (button == MouseEvent.BUTTON3)
            finishBezier();
    }
    
    public void mouseMoved(MouseEvent evt) {
       super.mouseMoved(evt);
       if (state != STATE_NONE)
           drawBezier(drawingPad.getTransformedGraphics(), true);
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
       if (state != STATE_NONE)
           drawBezier(drawingPad.getTransformedGraphics(), true);
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == UNDO_KEYCODE)
            undoBezier();
        else if (keyCode == REDO_KEYCODE)
            redoBezier();
    }
    
    public void onDrawTool(Graphics2D g2d) {
        if (gBezier == null)
            return;
        
        // Draw the current graph of lines.
        g2d.setColor(Color.RED);
        g2d.setStroke(drawingPad.getDesignStroke());
        gBezier.draw(g2d, false);
                
        // Draw the current line.
        drawBezier(g2d, false);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** @return true if the bezier needs to finshed.
     */
    private boolean isBezierFinished(FPointType fptPressed) {
        if (state == STATE_RIGHT_END_PT)
            return iBezier.getEndPoint1().equals(fptPressed);
        return false;
    }
    
    /** This will create a new graph for the bezier curves.
     */
    private void createNewGraph(FPointType fptPressed) {  
        gBezier = new TransformGraph();
        
        // Create the bezier info and set the first end point.
        iBezier = new BezierInfo();
        iBezier.setEndPoint1(fptPressed);
        
        // Setup the undo system.
        undoSystem = new UndoSystemBezier(gBezier);
        
        // Now the state is to get the right end point.
        state = STATE_RIGHT_END_PT;
    }

    /** This will process the bezier when the mouse has been clicked.
     */
    private void processBezier(FPointType fptPressed) {
        switch (state) {
            case STATE_RIGHT_END_PT:
                iBezier.setEndPoint2(fptPressed);
                state = STATE_LEFT_CONTROL_PT;
                break;
                
            case STATE_LEFT_CONTROL_PT:
                iBezier.setControlPoint1(fptPressed);
                state = STATE_RIGHT_CONTROL_PT;
                break;
                
            case STATE_RIGHT_CONTROL_PT:
                iBezier.setControlPoint2(fptPressed);
                fBezier = new CubicCurve2D.Float();
                
                // Add the bezier to the graph and redraw screen.
                undoSystem.add(gBezier.add(iBezier));
                drawingPad.repaint();
                
                // Create a new BezierInfo.
                FPointType fptEndPoint2 = iBezier.getEndPoint2();
                iBezier = new BezierInfo();
                iBezier.setEndPoint1(fptEndPoint2);
                
                state = STATE_RIGHT_END_PT;
                break;
        }
    }

    /** This will finish the bezier. It will add the created graph to the
     * design.
     */
    private void finishBezier() {
        // Add the graph for of lines to the design. Don't add an empty graph.
        if (gBezier != null && gBezier.getLineCount() != 0)
            drawingPad.getDesign().add(gBezier);
        
        state = STATE_NONE;
        fBezier = new CubicCurve2D.Float();
        gBezier = null;
        undoSystem = null;
        drawingPad.repaint();
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
        if (erase) 
            g2D.draw(fBezier);

        // Get the mouse position and update the bezier curve information.
        FPointType fptCurr = getFilterPoint(gBezier);
        FPointType fptEndPt1;
        FPointType fptEndPt2;
        FPointType fptControlPt1;
        switch (state) {
            case STATE_RIGHT_END_PT:
                fptEndPt1 = iBezier.getEndPoint1();
                fBezier.setCurve(fptEndPt1.x, fptEndPt1.y, fptEndPt1.x, fptEndPt1.y, fptCurr.x, fptCurr.y, fptCurr.x, fptCurr.y);
                break;
            case STATE_LEFT_CONTROL_PT:
                fptEndPt1 = iBezier.getEndPoint1();
                fptEndPt2 = iBezier.getEndPoint2();
                fBezier.setCurve(fptEndPt1.x, fptEndPt1.y, fptCurr.x, fptCurr.y, fptCurr.x, fptCurr.y, fptEndPt2.x, fptEndPt2.y);
                break;
            case STATE_RIGHT_CONTROL_PT:
                fptEndPt1 = iBezier.getEndPoint1();
                fptEndPt2 = iBezier.getEndPoint2();
                fptControlPt1 = iBezier.getControlPoint1();
                fBezier.setCurve(fptEndPt1.x, fptEndPt1.y, fptControlPt1.x, fptControlPt1.y, fptCurr.x, fptCurr.y, fptEndPt2.x, fptEndPt2.y);
                break;
        }
        
        // Draw the bezier line.
        g2D.draw(fBezier);
    }
            
    /** This will undo a bezier action.
     */
    private void undoBezier() {
        if (undoSystem == null || !undoSystem.isUndoPossible())
            return;
        
        // If it is a control point then undo to the start of the current bezier.
        if (state == STATE_LEFT_CONTROL_PT || state == STATE_RIGHT_CONTROL_PT) {
            state = STATE_RIGHT_END_PT;
            return;
        }
        
        // Get the undo bezier.
        iBezier = undoSystem.getUndoBezier();
        
        // Undo the bezier and redraw the DrawingPad.
        undoSystem.undo();
        drawingPad.repaint();
    }
    
    /** This will redo a bezier action.
     */
    private void redoBezier() {
        if (undoSystem == null || !undoSystem.isRedoPossible())
            return;
        
        // Get the redo line.
        iBezier = new BezierInfo();
        iBezier.setEndPoint1(undoSystem.getRedoBezier().getEndPoint2());

        // Set the state to STATE_RIGHT_END_PT.
        state = STATE_RIGHT_END_PT;
        
        // Redo the line and redraw the DrawingPad.
        undoSystem.redo();
        drawingPad.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class UndoSystemBezier ">
    
    /** Class that is used to do the undo/redo and get information about the undo/redo lines
     * needed to know where the next will be drawn at.
     */
    class UndoSystemBezier extends UndoSystem {
        
        /** This is the graph that this UndoSystem is operating on. */
        TransformGraph g;
        
        /** This will create the UndoSystemLine with the graph that is used in this undo system.
         * @param g is the graph that is used in this undo system.
         */
        UndoSystemBezier(TransformGraph g) {
            this.g = g;
        }
        
        /** This will get the current bezier that was just added to the undo.
         * @return A BezierInfo about the last bezier that was just added to the undo.
         * @throws IllegalStateException No more undos.
         */
        BezierInfo getUndoBezier() {
            if (ltUndo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more undos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltUndo.getFirst();
            return (BezierInfo)g.getAbstractLineInfo(iUndo);
        }
        
        /** This will get the current bezier that was redo.
         * @return A BezierInfo about the last bezier that was just added to the redo.
         * @throws IllegalStateException No more redos.
         */
        BezierInfo getRedoBezier() {
            if (ltRedo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more redos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltRedo.getFirst();
            return (BezierInfo)g.getAbstractLineInfo(iUndo);
        }
    }
    
    // </editor-fold>
    
}
