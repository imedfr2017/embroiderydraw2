/*
 * ToolMirror.java
 *
 * Created on August 25, 2006, 11:31 AM
 *
 */ 

package mlnr.gui.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.tool.opt.MirrorPanel;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class ToolMirror extends AbstractTool { 
        
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    MirrorPanel mirrorPanel;
    FPointType fptUserCalculated;
    boolean completed;
    float radian = (float)(Math.PI / 2.0f);
    
    Line2D.Float fLine1 = new Line2D.Float();        
    Line2D.Float fLine2 = new Line2D.Float();        
    Line2D.Float fLineDegree = new Line2D.Float();
    Ellipse2D.Float fEllipse = new Ellipse2D.Float();        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    // settings for the cross where the object is to be rotated around.
    private static Color CROSS_COLOR_1 = Color.BLUE;
    private static Color CROSS_COLOR_2 = Color.RED;
    private static float CROSS_SIZE=0.25f;
    private final float CROSS_DEGREE_SIZE = 20.0f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ToolMirror */
    public ToolMirror(InterfaceFrameOperation iFrameOperator, DrawingPad drawingPad, MirrorPanel mirrorPanel) {
        super(iFrameOperator, drawingPad);
        this.mirrorPanel = mirrorPanel;
        mirrorPanel.setMirrorTool(this);
        
        // Calculate the center.
        if (drawingPad.isSelectedItems()) {            
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            drawMirrorCenter(drawingPad.getTransformedGraphics(), false);
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    public void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1)
            mirror();
        else if (evt.getButton() == MouseEvent.BUTTON3)
            setCalculatePosition();
    }
    
    public void onFinalize(boolean complete) {
        complete(complete);
    }
    
    public void onCompleteFinalize() {
        complete(true);
        completed = true;
    }
    
    public void onDrawTool(Graphics2D g2d) {
        drawMirrorCenter(g2d, false);
    }
    
    // </editor-fold>
     
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">

    /** This will resize the selected items.
     */
    private void mirror() {
        if (mirrorPanel.isDirectionVertical()) {
            drawingPad.getSelectedItems().onMirrorVertical();
        } else if (mirrorPanel.isDirectionHorizontal()) {
            drawingPad.getSelectedItems().onMirrorHorizontal();
        } else if (mirrorPanel.isDirectionDegree()) {
            drawingPad.getSelectedItems().onMirrorRadian(mirrorPanel.getRadian());
        } else
            throw new IllegalStateException("ToolMirror::mirror() Unknown mirror method.");
        
        // Finalize the mirror and recalculate.
        drawingPad.getSelectedItems().finalizeMovement();
        drawingPad.getSelectedItems().calculate(fptUserCalculated);
        drawingPad.repaint();
    }

    private void setCalculatePosition() {
        // This is the filtered mouse position.
        fptUserCalculated = getFilterPointDesign(drawingPad.getSelectedItems(), true);
        drawingPad.getSelectedItems().finalizeMovement();
        drawingPad.getSelectedItems().calculate(fptUserCalculated);
        drawingPad.repaint();
    }    

    /** This will complete the mirror.
     */
    private void complete(boolean complete) {
        if (completed)
            return;
        
        drawingPad.getSelectedItems().finalizeMovement();
        
        if (complete) {
            iFrameOperator.enableCopyCut(false);
            iFrameOperator.enableAdvanceTools(false);
            drawingPad.finalizeSelectedItems();
        }
        
        drawingPad.repaint();
    }

    /** This will draw the resize Center point.
     */
    private void drawMirrorCenter(Graphics2D g, boolean erase) {
        Color old = g.getColor();
        g.setXORMode(Color.WHITE);
        Stroke sOld = g.getStroke();
        g.setStroke(strokeDrawing);
        
        // Erase the lines.
        if (erase) {
            g.setColor(CROSS_COLOR_1);
            g.draw(fLine1);
            g.draw(fLine2);
            g.setColor(CROSS_COLOR_2);
            g.draw(fEllipse);
            g.setColor(CROSS_COLOR_1);
            g.draw(fLineDegree);
        }
        
        // Set the lines and ellipses with the new locations.
        fLine1.setLine(fptUserCalculated.x - CROSS_SIZE, fptUserCalculated.y, fptUserCalculated.x + CROSS_SIZE, fptUserCalculated.y);
        fLine2.setLine(fptUserCalculated.x, fptUserCalculated.y - CROSS_SIZE, fptUserCalculated.x, fptUserCalculated.y + CROSS_SIZE);
        float xPrev = fptUserCalculated.x - (float)Math.cos(radian) * CROSS_DEGREE_SIZE;
        float yPrev = fptUserCalculated.y - (float)Math.sin(radian) * CROSS_DEGREE_SIZE;
        float xNext = fptUserCalculated.x + (float)Math.cos(radian) * CROSS_DEGREE_SIZE;
        float yNext = fptUserCalculated.y + (float)Math.sin(radian ) * CROSS_DEGREE_SIZE;
        fLineDegree.setLine(xPrev, yPrev, xNext, yNext);
        fEllipse.setFrame(fptUserCalculated.x - CROSS_SIZE, fptUserCalculated.y - CROSS_SIZE, CROSS_SIZE*2, CROSS_SIZE*2);
        
        // Draw the lines now.
        g.setColor(CROSS_COLOR_1);
        g.draw(fLine1);
        g.draw(fLine2);
        g.setColor(CROSS_COLOR_2);
        g.draw(fEllipse);
        g.setColor(CROSS_COLOR_1);
        g.draw(fLineDegree);
        
        // Restore the color.
        g.setColor(old);
        g.setStroke(sOld);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Mirror Methods ">

    /** This will set the radian for the line that shows the user how the object will be mirrored.
     * @param rad is the radian for the line that shows the user how the object will be mirrored.
     */
    public void setRadian(float rad) {
        radian = rad;
        drawMirrorCenter(drawingPad.getTransformedGraphics(), true);
    }

    public void resetPointToCenter() {        
        // Calculate the center.
        if (drawingPad.isSelectedItems()) {
            fptUserCalculated = drawingPad.getSelectedItems().calculateCenter();
            drawingPad.repaint();
        }
    }
    
    // </editor-fold>
}
