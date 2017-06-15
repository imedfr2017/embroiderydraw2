/*
 * AbstractTool.java
 *
 * Created on June 3, 2005, 5:18 PM
 */

package mlnr.gui.tool;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import mlnr.Measurement;
import mlnr.draw.DrawingDesign;
import mlnr.draw.TransformDesign;
import mlnr.draw.TransformGraph;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.type.FPointType;

/** This is the base class all tools must inherit to be used on as a tool on the drawing canvas.
 * @author Robert Molnar II
 */
abstract public class AbstractTool implements MouseListener, MouseMotionListener, KeyListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    protected InterfaceFrameOperation iFrameOperator;
    protected DrawingPad drawingPad;
    
    /** Stroke used for selection. */
    protected BasicStroke strokeSelected = new BasicStroke(0.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{0,6,0,6}, 0);
    /** Stroke used for drawing. */
    protected BasicStroke strokeDrawing = new BasicStroke(0.0f);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    // Keys used to perform undo/redo operations within the tool.
    protected static final int UNDO_KEYCODE = KeyEvent.VK_B;
    protected static final int REDO_KEYCODE = KeyEvent.VK_F;
    
    // Radius of bounds.
    private static final float RADIUS_BOUNDS = 0.15f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of AbstractTool
     * @param frame is the main frame of the program (Used to create dialog boxes).
     * @param drawingPad is the DrawingPad which this tool will be working on.
     */
    public AbstractTool(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad) {
        this.iFrameOperator = iFrameOperator;
        this.drawingPad = drawingPad;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Utility Methods ">
    
    /** This will create a rectangle with the center point being fpt. It is used when
     * a function in the Design requires a rectangle.
     * @param fpt is the center point for the rectangle.
     * @return rectangle that is used for the Design when a function requires a rectangle and
     * not a point.
     */
    protected Rectangle2D.Float getPointBounds(FPointType fpt) {
        return new Rectangle2D.Float(fpt.x - RADIUS_BOUNDS, fpt.y - RADIUS_BOUNDS, RADIUS_BOUNDS * 2, RADIUS_BOUNDS * 2);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Possible Overriden Methods ">
    
    /** Called when the tool needs to be drawn.
     * @param g2d is the Graphis2D which is already transformed into measurement coordinates.
     */
    public void onDrawTool(Graphics2D g2d) {}
    
    /** Called when the tool is no longer needed.
     * @param complete is true if the tool should completely finalize the operation. This is
     * only used by tools that operate with selected items.
     */
    public void onFinalize(boolean complete) {}
    
    /** This is called when the tool should completely finish what it is doing. It should complete
     * the undo operation and not leave it open for another tool to complete it. Note that this will
     * be called before onFinalize() is called and the onFinalize() function will be called.
     */
    public void onCompleteFinalize() {}
    
    /** The sub class can create a filter to be used. This is called before any other filter is
     * called on the point received from the DrawingPad.
     * @param fpt is the point to be filtered.
     */
    protected void filterPoint(FPointType fpt) {}
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Filter Method ">
    
    protected FPointType getFilterPoint(TransformGraph g) {
        // Get the point the mouse is currently at.
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        
        // Save the old values.
        float xOld = fpt.x;
        float yOld = fpt.y;
        
        // Filter the point.
        filterPoint(fpt);
        drawingPad.getDesign().filterPoint(fpt, true);
        if (g != null)
            g.filterPoint(fpt);
        
        // Filter to snap to the grid now.
        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
            // Get the grid size.
            float gridSize;
            if (Measurement.isFineGridVisible())
                gridSize = Measurement.getFineGridSize();
            else
                gridSize = Measurement.getGridSize();
            
            // Now calculate the correct position.
            float xGridCount = (float)Math.floor((fpt.x + gridSize / 2) / gridSize);
            float yGridCount = (float)Math.floor((fpt.y + gridSize / 2) / gridSize);
            
            fpt.x = xGridCount * gridSize;
            fpt.y = yGridCount * gridSize;
        }        
        
        return fpt;
    }
    
    protected FPointType getFilterPoint(TransformGraph g, boolean currLayerOnly) {
        // Get the point the mouse is currently at.
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        
        // Save the old values.
        float xOld = fpt.x;
        float yOld = fpt.y;
        
        // Filter the point.
        filterPoint(fpt);
        drawingPad.getDesign().filterPoint(fpt, currLayerOnly);
        if (g != null)
            g.filterPoint(fpt);
        
        // Filter to snap to the grid now.
        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
            // Get the grid size.
            float gridSize;
            if (Measurement.isFineGridVisible())
                gridSize = Measurement.getFineGridSize();
            else
                gridSize = Measurement.getGridSize();
            
            // Now calculate the correct position.
            float xGridCount = (float)Math.floor((fpt.x + gridSize / 2) / gridSize);
            float yGridCount = (float)Math.floor((fpt.y + gridSize / 2) / gridSize);
            
            fpt.x = xGridCount * gridSize;
            fpt.y = yGridCount * gridSize;
        }        
        
        return fpt;
    }
    
    /** This will get the current point the mouse position is and filter it.
     * @param g is the graph currently being made, can be null.
     * @return current position of the mouse and being filtered through the design and graph g.
     */
//    protected FPointType getFilterPoint(Graph g) {
//        // Get the point the mouse is currently at.
//        FPointType fpt = drawingPad.getMousePositionMeasurement();
//        
//        // Save the old values.
//        float xOld = fpt.x;
//        float yOld = fpt.y;
//        
//        // Filter the point.
//        filterPoint(fpt);
//        drawingPad.getDesign().filterPoint(fpt, true);
//        if (g != null)
//            g.filterPoint(fpt);
//        
//        // Filter to snap to the grid now.
//        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
//            if (Measurement.isFineGridVisible()) {
//                float fineGridSize = Measurement.getFineGridSize();
//                fpt.x = (float)Math.floor((fpt.x + (fineGridSize / 2)) / fineGridSize);
//                fpt.y = (float)Math.floor((fpt.y + (fineGridSize / 2)) / fineGridSize);
//            } else {
//                float gridSize = Measurement.getGridSize();
//                fpt.x = (float)Math.floor((fpt.x + (gridSize / 2)) / gridSize);
//                fpt.y = (float)Math.floor((fpt.y + (gridSize / 2)) / gridSize);
//            }
//        }        
//        
//        return fpt;
//    }
    
    /** This will get the current point the mouse position is and filter it.
     * @param g is the graph currently being made, can be null.
     * @param currLayerOnly is true if the current layer should only be used to filter.
     * @return current position of the mouse and being filtered through the design and graph g.
     */
//    protected FPointType getFilterPoint(Graph g, boolean currLayerOnly) {
//        // Get the point the mouse is currently at.
//        FPointType fpt = drawingPad.getMousePositionMeasurement();
//        
//        // Save the old values.
//        float xOld = fpt.x;
//        float yOld = fpt.y;
//        
//        // Filter the point.
//        filterPoint(fpt);
//        drawingPad.getDesign().filterPoint(fpt, currLayerOnly);
//        if (g != null)
//            g.filterPoint(fpt);
//        
//        // Filter to snap to the grid now.
//        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
//            if (Measurement.isFineGridVisible()) {
//                float fineGridSize = Measurement.getFineGridSize();
//                fpt.x = (float)Math.floor((fpt.x + (fineGridSize / 2)) / fineGridSize);
//                fpt.y = (float)Math.floor((fpt.y + (fineGridSize / 2)) / fineGridSize);
//            } else {
//                float gridSize = Measurement.getGridSize();
//                fpt.x = (float)Math.floor((fpt.x + (gridSize / 2)) / gridSize);
//                fpt.y = (float)Math.floor((fpt.y + (gridSize / 2)) / gridSize);
//            }
//        }        
//        
//        return fpt;
//    }
    
    /** This will get the current point the mouse position is and filter it.
     * @param d is the design currently being used, can be null. It will filter all points in it.
     * @param currLayerOnly is true if the current layer should only be used to filter.
     * @return current position of the mouse and being filtered through the design and graph g.
     */
    protected FPointType getFilterPointDesign(DrawingDesign d, boolean currLayerOnly) {
        // Get the point the mouse is currently at.
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        
        // Save the old values.
        float xOld = fpt.x;
        float yOld = fpt.y;
        
        // Filter the point.
        filterPoint(fpt);
        drawingPad.getDesign().filterPoint(fpt, currLayerOnly);
        if (d != null)
            d.filterPoint(fpt, false);
                
        // Filter to snap to the grid now.
        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
            if (Measurement.isFineGridVisible()) {
                float fineGridSize = Measurement.getFineGridSize();
                fpt.x = (float)Math.floor((fpt.x + (fineGridSize / 2)) / fineGridSize);
                fpt.y = (float)Math.floor((fpt.y + (fineGridSize / 2)) / fineGridSize);
            } else {
                float gridSize = Measurement.getGridSize();
                fpt.x = (float)Math.floor((fpt.x + (gridSize / 2)) / gridSize);
                fpt.y = (float)Math.floor((fpt.y + (gridSize / 2)) / gridSize);
            }
        }        
        
        return fpt;
    }
    
    /** This will get the current point the mouse position is and filter it. Filter is in this order: DrawingDesign, TransformDesign, and Grid.
     * @param d is the design currently being used. Must not be null.
     * @param tDesign is the transform design currently selecteed. Must not be null.
     * @param currLayerOnly is true if the current layer should only be used to filter.
     * @return current position of the mouse and being filtered through the design and graph g.
     */
    protected FPointType getFilterPointDesign(TransformDesign tDesign, boolean currLayerOnly) {
        // Get the point the mouse is currently at.
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        
        // Save the old values.
        float xOld = fpt.x;
        float yOld = fpt.y;
        
        // Filter the point to the DrawingDesign.
        filterPoint(fpt);
        drawingPad.getDesign().filterPoint(fpt, currLayerOnly);
        tDesign.filterPoint(fpt);
                
        // Filter to snap to the grid now.
        if (DrawingPad.isSnapToGrid() && xOld == fpt.x && yOld == fpt.y) {
            if (Measurement.isFineGridVisible()) {
                float fineGridSize = Measurement.getFineGridSize();
                fpt.x = (float)Math.floor((fpt.x + (fineGridSize / 2)) / fineGridSize);
                fpt.y = (float)Math.floor((fpt.y + (fineGridSize / 2)) / fineGridSize);
            } else {
                float gridSize = Measurement.getGridSize();
                fpt.x = (float)Math.floor((fpt.x + (gridSize / 2)) / gridSize);
                fpt.y = (float)Math.floor((fpt.y + (gridSize / 2)) / gridSize);
            }
        }        
        
        return fpt;
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">
    
    public void mouseReleased(MouseEvent evt) {}
    
    public void mousePressed(MouseEvent evt) {
        drawingPad.requestFocus();
    }
    
    public void mouseExited(MouseEvent evt) {}
    
    public void mouseEntered(MouseEvent evt) {}
    
    public void mouseClicked(MouseEvent evt) {}
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseMotionListener ">
    
    public void mouseMoved(MouseEvent evt) {
        drawingPad.requestFocus();
    }
    
    public void mouseDragged(MouseEvent evt) {
        drawingPad.requestFocus();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface KeyListener ">
    
    public void keyTyped(KeyEvent e) {}
    
    public void keyPressed(KeyEvent e) {}
    
    public void keyReleased(KeyEvent e) {}
    
    // </editor-fold>
}
