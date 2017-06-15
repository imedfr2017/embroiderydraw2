/*
 * ToolRMolnar.java
 *
 * Created on August 3, 2006, 12:16 AM
 *
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mlnr.draw.RMolnarInfo;
import mlnr.draw.TransformGraph;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.geom.RMolnarCubicCurve2D;
import mlnr.type.FPointType;
import mlnr.util.InterfaceUndoItem;
import mlnr.util.UndoSystem;

/** After the second point is clicked then there must be two rmolnars being drawn or being worked on.
 * @author Robert Molnar II
 */
public class ToolRMolnar extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    TransformGraph gRMolnar = null;
    int state = STATE_NONE;
    boolean erase = true;
    FPointType prevMouse = new FPointType();
    
    /** This is the current RMolnar information. */
    RMolnarInfo iRMolnarCurr = null;
    /** This is the other RMolnar information. */
    RMolnarInfo iRMolnarOther = null;
    
    /** This is the current RMolnar being drawn. */
    RMolnarCubicCurve2D fRmolnarCurr = new RMolnarCubicCurve2D();
    /** This is the other RMolnar being drawn. */
    RMolnarCubicCurve2D fRmolnarOther = new RMolnarCubicCurve2D();
    
    /** This is a system that will perform the undo/redo for the operations of adding into the*/
    UndoSystemRMolnar undoSystem = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    static final int STATE_NONE=0;
    static final int STATE_LAST_END_PT=1;
    static final int STATE_LAST_CONTROL_PT=2;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolLine */
    public ToolRMolnar(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad) {
        super(iFrameOperator, drawingPad);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        
        // Get the mouse position.
        FPointType fptPressed = getFilterPoint(gRMolnar);
        
        // Process button.
        int button = evt.getButton();
        if (fptPressed.equals(prevMouse)) {
          finishRMolnar();  
        } else if (button == MouseEvent.BUTTON1) {
            if (state == STATE_NONE)
                createNewGraph(fptPressed);
            else
                processRMolnar(fptPressed);
        } else if (button == MouseEvent.BUTTON3 && state != STATE_NONE)
            finishRMolnar();
        
        // Set the previous point.
        if (state != STATE_NONE) {
           prevMouse.x = fptPressed.x;
           prevMouse.y = fptPressed.y;
        } 
       
        drawingPad.repaint();
    }
    
    public void mouseMoved(MouseEvent evt) {
        super.mouseMoved(evt);
        
        if (state != STATE_NONE)
            drawRMolnar(drawingPad.getTransformedGraphics());
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        
        if (state != STATE_NONE)
            drawRMolnar(drawingPad.getTransformedGraphics());
    }
    
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == UNDO_KEYCODE)
            undoRMolnar();
        else if (keyCode == REDO_KEYCODE)
            redoRMolnar();
    }
    
    public void onDrawTool(Graphics2D g2d) {
        if (gRMolnar == null)
            return;
        
        // Draw the current graph of lines.
        g2d.setColor(Color.RED);
        g2d.setStroke(drawingPad.getDesignStroke());
        gRMolnar.draw(g2d, false);
        
        // Draw the current line.
        erase = false;
        drawRMolnar(g2d);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will create a new graph for the bezier curves.
     */
    private void createNewGraph(FPointType fptPressed) {
        gRMolnar = new TransformGraph();
        
        // Create the bezier info and set the first end point.
        iRMolnarCurr = new RMolnarInfo();
        iRMolnarCurr.setControlPoint1(fptPressed);
        iRMolnarCurr.setEndPoint1(fptPressed);
        
        // Setup the undo system.
        undoSystem = new UndoSystemRMolnar(gRMolnar);
        
        // Now the state is to get the right end point.
        state = STATE_LAST_END_PT;
    }
    
    /** This will process the bezier when the mouse has been clicked.
     */
    private void processRMolnar(FPointType fptPressed) {
        
        switch (state) {
            case STATE_LAST_END_PT:
                // Set the end point for the current RMolnar.
                iRMolnarCurr.setEndPoint2(fptPressed);
                
                // The other one is now created. (It is infront of the current RMolnar.
                iRMolnarOther = new RMolnarInfo();
                iRMolnarOther.setControlPoint1(iRMolnarCurr.getEndPoint1());
                iRMolnarOther.setEndPoint1(iRMolnarCurr.getEndPoint2());
                
                state = STATE_LAST_CONTROL_PT;
                break;
                
            case STATE_LAST_CONTROL_PT:
                // Set the end control point for the current RMolnar.
                iRMolnarCurr.setControlPoint2(fptPressed);
                
                // Add the rmolnar to the graph.
                undoSystem.add(gRMolnar.add(iRMolnarCurr));
                
                // Set the end point for the other RMolnar.
                iRMolnarOther.setEndPoint2(fptPressed);
                
                // The other becomes the current and a new other is created.
                iRMolnarCurr = iRMolnarOther;
                iRMolnarOther = new RMolnarInfo();
                iRMolnarOther.setControlPoint1(iRMolnarCurr.getEndPoint1());
                iRMolnarOther.setEndPoint1(iRMolnarCurr.getEndPoint2());
                
                // Redraw the drawing pad since the graph has been updated.
                fRmolnarOther = new RMolnarCubicCurve2D();
                fRmolnarCurr = new RMolnarCubicCurve2D();
                break;
        }
    }
    
    /** This will finish the bezier. It will add the created graph to the
     * design.
     */
    private void finishRMolnar() {
        if (state == STATE_LAST_CONTROL_PT) {
            // Complete the current RMolnar and add it to the graph.
            iRMolnarCurr.setControlPoint2(iRMolnarCurr.getEndPoint2());
            gRMolnar.add(iRMolnarCurr);

            // Add the graph for of lines to the design.
            drawingPad.getDesign().add(gRMolnar);
        }
        
        state = STATE_NONE;
        fRmolnarOther = new RMolnarCubicCurve2D();
        fRmolnarCurr = new RMolnarCubicCurve2D();
        
        gRMolnar = null;
        undoSystem = null;
        prevMouse = new FPointType();
        
    }
    
    /** This will draw the current bezier.
     * @param erase is true if it should erase the previous bezier.
     */
    private void drawRMolnar(Graphics2D g2D) {
        // Setup the graphics.
        g2D.setColor(Color.BLACK);
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(strokeDrawing);
        
        // Erase the previous line.
        if (erase) {
            g2D.draw(fRmolnarCurr);
            g2D.draw(fRmolnarOther);
        }
        
        // Get the mouse position, update the bezier curve information, and draw the curve.
        FPointType fptCurr = getFilterPoint(gRMolnar);
        if (state == STATE_LAST_END_PT) {
            fRmolnarCurr.setCurve(iRMolnarCurr.getEndPoint1(), fptCurr, iRMolnarCurr.getControlPoint1(), fptCurr);
            g2D.draw(fRmolnarCurr);
        } else if (state == STATE_LAST_CONTROL_PT) {
            fRmolnarCurr.setCurve(iRMolnarCurr.getEndPoint1(), iRMolnarCurr.getEndPoint2(), iRMolnarCurr.getControlPoint1(), fptCurr);
            g2D.draw(fRmolnarCurr);
            
            fRmolnarOther.setCurve(iRMolnarOther.getEndPoint1(), fptCurr, iRMolnarOther.getControlPoint1(), fptCurr);
            g2D.draw(fRmolnarOther);
        }
        
        erase = true;
    }
    
    /** This will undo a bezier action.
     */
    private void undoRMolnar() {
        if (undoSystem == null || !undoSystem.isUndoPossible())
            return;
        
        // State will always be STATE_LAST_CONTROL_PT
        
        // Get the undo rmolnar.
        iRMolnarCurr = undoSystem.getUndoRMolnar();
        iRMolnarOther = new RMolnarInfo();
        iRMolnarOther.setControlPoint1(iRMolnarCurr.getEndPoint1());
        iRMolnarOther.setEndPoint1(iRMolnarCurr.getEndPoint2());
        
        // Undo the rmolnar and redraw the DrawingPad.
        undoSystem.undo();
        drawingPad.repaint();
    }
    
    /** This will redo a bezier action.
     */
    private void redoRMolnar() {
        if (undoSystem == null || !undoSystem.isRedoPossible())
            return;
        
        // Get the redo rmolnar.
        RMolnarInfo iRedo = undoSystem.getRedoRMolnar();
        iRMolnarCurr = new RMolnarInfo();
        iRMolnarOther = new RMolnarInfo();
        iRMolnarCurr.setControlPoint1(iRedo.getEndPoint1());
        iRMolnarCurr.setEndPoint1(iRedo.getEndPoint2());
        iRMolnarCurr.setEndPoint2(iRedo.getControlPoint2());
        iRMolnarOther.setControlPoint1(iRMolnarCurr.getEndPoint1());
        iRMolnarOther.setEndPoint1(iRMolnarCurr.getEndPoint2());
        
        // Redo the rmolnar and redraw the DrawingPad.
        undoSystem.redo();
        drawingPad.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class UndoSystemRMolnar ">
    
    /** Class that is used to do the undo/redo and get information about the undo/redo lines
     * needed to know where the next will be drawn at.
     */
    class UndoSystemRMolnar extends UndoSystem {
        
        /** This is the graph that this UndoSystem is operating on. */
        TransformGraph g;
        
        /** This will create the UndoSystemRMolnar with the graph that is used in this undo system.
         * @param g is the graph that is used in this undo system.
         */
        UndoSystemRMolnar(TransformGraph g) {
            this.g = g;
        }
        
        /** This will get the current rmolnar that was just added to the undo.
         * @return A RMolnarInfo about the last rmolnar that was just added to the undo.
         * @throws IllegalStateException No more undos.
         */
        RMolnarInfo getUndoRMolnar() {
            if (ltUndo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more undos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltUndo.getFirst();
            return (RMolnarInfo)g.getAbstractLineInfo(iUndo);
        }
        
        /** This will get the current rmolnar that was redo.
         * @return A RMolnarInfo about the last rmolnar that was just added to the redo.
         * @throws IllegalStateException No more redos.
         */
        RMolnarInfo getRedoRMolnar() {
            if (ltRedo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more redos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltRedo.getFirst();
            return (RMolnarInfo)g.getAbstractLineInfo(iUndo);
        }
    }
    
    // </editor-fold>
    
}
