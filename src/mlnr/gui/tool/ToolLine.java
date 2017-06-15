/*
 * ToolLine.java
 *
 * Created on August 2, 2006, 11:34 PM
 *
 */

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import mlnr.draw.Graph;
import mlnr.draw.LineInfo;
import mlnr.draw.TransformGraph;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.type.FPointType;
import mlnr.util.*; 

/** Class used to draw lines.
 * @author Robert Molnar II
 */
public class ToolLine extends AbstractTool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    FPointType mouseDown = new FPointType();
    FPointType prev = new FPointType();
    TransformGraph gLine = null;
    Line2D.Float fLine = new Line2D.Float();
    boolean straightLine = false;
    int state = STATE_NONE;
    
    /** This is a system that will perform the undo/redo for the operations of adding into the*/
    UndoSystemLine undoSystem = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    static final int STATE_NONE = 1;
    static final int STATE_LINE = 2;
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolLine */
    public ToolLine(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad) {
        super(iFrameOperator, drawingPad);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        
        // Get the mouse position.
        FPointType fptPressed = getFilterPoint(gLine);
        
        // Process button.
        int button = evt.getButton();
        if (button == MouseEvent.BUTTON1) {
            if (state == STATE_NONE)
                createNewGraph();
            else
                addLine(fptPressed);
        } else if (button == MouseEvent.BUTTON3)
            finishLine();
        
        // Update previous position.
        prev.x = mouseDown.x = fptPressed.x;
        prev.y = mouseDown.y = fptPressed.y;
    }
    
    public void mouseMoved(MouseEvent evt) {
       super.mouseMoved(evt);
       if (state == STATE_LINE)
           drawLine(drawingPad.getTransformedGraphics(), true);
    }
    
    public void mouseDragged(MouseEvent evt) {
        super.mouseDragged(evt);
        if (state == STATE_LINE)
            drawLine(drawingPad.getTransformedGraphics(), true);
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL)
            straightLine = true;
    }    

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL)
            straightLine = false;
        else if (keyCode == UNDO_KEYCODE)
            undoLine();
        else if (keyCode == REDO_KEYCODE)
            redoLine();
    }
    
    public void onDrawTool(Graphics2D g2d) {
        if (gLine == null)
            return;
        
        // Draw the current graph of lines.
        g2d.setColor(Color.RED);
        g2d.setStroke(drawingPad.getDesignStroke());
        gLine.draw(g2d, false);
                
        // Draw the current line.
        drawLine(g2d, false);
    }
    
    protected void filterPoint(FPointType fpt) {
        if (straightLine) {
            float xDistance = Math.abs(mouseDown.x - fpt.x);
            float yDistance = Math.abs(mouseDown.y - fpt.y);
            
            // Do nothing if one of them is zero.
            if (xDistance == 0 || yDistance == 0)
                return;
            
            // 45 degree angle.
            if ((yDistance / xDistance) <= 2 && (xDistance / yDistance) <= 2) {
                if (fpt.y > mouseDown.y)
                    fpt.y = mouseDown.y + Math.abs(fpt.x - mouseDown.x);
                else
                    fpt.y = mouseDown.y - Math.abs(fpt.x - mouseDown.x);
            } else if (xDistance > yDistance)
                fpt.y = mouseDown.y;
            else
                fpt.x = mouseDown.x;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** This will draw the current line being created.
     * @param erase is true if it should erase.
     */
    private void drawLine(Graphics2D g2D, boolean erase) {        
        // Get the mouse position.
        FPointType fptCurr = getFilterPoint(gLine);
        
        // Setup the graphics.
        g2D.setXORMode(Color.WHITE);
        g2D.setStroke(strokeDrawing);
        g2D.setColor(Color.BLACK);
        
        // Erase the previous line.
        if (erase) {
            fLine.setLine(mouseDown.x, mouseDown.y, prev.x, prev.y);
            g2D.draw(fLine);
        }
        
        // Draw the current line.
        fLine.setLine(mouseDown.x, mouseDown.y, fptCurr.x, fptCurr.y);
        g2D.draw(fLine);
            
        // Update the previous coordinates.
        prev.x = fptCurr.x;
        prev.y = fptCurr.y;
    }
    
    /** This will start a new graph of lines.
     */
    private void createNewGraph() {
        gLine = new TransformGraph();
        undoSystem = new UndoSystemLine(gLine);
        state = STATE_LINE;
    }

    /** This will add a line to the graph.
     * @param fptDown is the current place the mouse pressed down.
     */
    private void addLine(FPointType fptDown) {
        // If they both equal each one then the line is complete.
        if (mouseDown.equals(fptDown))
            finishLine();
        else {
            undoSystem.add(gLine.add(new LineInfo(mouseDown, fptDown)));
            drawingPad.repaint();
        }
    }

    /** This will finish the lines of the graph and add it to the design.
     */
    private void finishLine() {
        // Add the graph for of lines to the design. Don't add an empty graph.
        if (gLine != null && gLine.getLineCount() != 0)
            drawingPad.getDesign().add(gLine);
        
        state = STATE_NONE;
        gLine = null;
        undoSystem = null;
        drawingPad.repaint();
    }

    /** This will undo the lastest line added.
     */
    private void undoLine() {
        if (undoSystem == null || !undoSystem.isUndoPossible())
            return;
        
        // Get the undo line.
        LineInfo lInfo = undoSystem.getUndoLine();
        
        // Back up the place where the line is to be drawn at next.
        prev.x = mouseDown.x = lInfo.getEndPoint1().x;
        prev.y = mouseDown.y = lInfo.getEndPoint1().y;
        
        // Undo the line and redraw the DrawingPad.
        undoSystem.undo();
        drawingPad.repaint();
    }
    
    /** This will redo the lastest line added.
     */
    private void redoLine() {
        if (undoSystem == null || !undoSystem.isRedoPossible())
            return;
        
        // Get the redo line.
        LineInfo lInfo = undoSystem.getRedoLine();
                
        // Back up the place where the line is to be drawn at next.
        prev.x = mouseDown.x = lInfo.getEndPoint2().x;
        prev.y = mouseDown.y = lInfo.getEndPoint2().y;
        
        // Redo the line and redraw the DrawingPad.
        undoSystem.redo();
        drawingPad.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class UndoSystemLine ">
    
    /** Class that is used to do the undo/redo and get information about the undo/redo lines
     * needed to know where the next will be drawn at.
     */
    class UndoSystemLine extends UndoSystem {
        
        /** This is the graph that this UndoSystem is operating on. */
        TransformGraph g;
        
        /** This will create the UndoSystemLine with the graph that is used in this undo system.
         * @param g is the graph that is used in this undo system.
         */
        UndoSystemLine(TransformGraph g) {
            this.g = g;
        }
        
        /** This will get the current line that was just added to the undo.
         * @return A LineInfo about the last line that was just added to the undo.
         * @throws IllegalStateException No more undos.
         */
        LineInfo getUndoLine() {
            if (ltUndo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more undos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltUndo.getFirst();
            return (LineInfo)g.getAbstractLineInfo(iUndo);
        }
        
        /** This will get the current line that was redo.
         * @return A LineInfo about the last line that was just added to the redo.
         * @throws IllegalStateException No more redos.
         */
        LineInfo getRedoLine() {
            if (ltRedo.size() == 0)
                throw new IllegalStateException("ToolLine::UndoSystemLine:: No more redos.");
            InterfaceUndoItem iUndo = (InterfaceUndoItem)ltRedo.getFirst();
            return (LineInfo)g.getAbstractLineInfo(iUndo);
        }
    }
    
    // </editor-fold>
    
}
