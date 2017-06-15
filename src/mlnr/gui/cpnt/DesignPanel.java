/*
 * DesignPanel.java
 *
 * Created on July 25, 2006, 10:27 AM
 *
 */

package mlnr.gui.cpnt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import mlnr.Measurement;
import mlnr.draw.DrawingDesign;
import mlnr.draw.LayerInfo;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.type.FPointType;
import org.w3c.dom.Element;

/**
 *
 * @author Robert Molnar II
 */
public class DesignPanel extends JPanel implements InterfaceScale, AdjustmentListener, MouseWheelListener, MouseMotionListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the measurement scale. */
    float measurementScale = 1.0f;
    
    /** Used to operate the frame. */
    InterfaceFrameOperation iFrameOperator;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Component Fields ">
    
    /** This is the east scrollbar for y movement. */
    private JScrollBar scrollBarVertical = new JScrollBar(JScrollBar.VERTICAL);
    
    /** This is the south scrollbar for x movement. */
    private JScrollBar scrollBarHorizontal = new JScrollBar(JScrollBar.HORIZONTAL);
    
    /** This is the ruler for the columns (X). */
    private RulerHorizontal rulerColumn = new RulerHorizontal(this);
    
    /** This is the ruler for the rows (Y). */
    private RulerVertical rulerRow = new RulerVertical(this);
    
    /** This is the drawing pad where the design is drawn in. */
    private DrawingPad drawingPad = new DrawingPad(this);
    
    /** This is the upper-left corner piece. */
    private Corner corner = new Corner(25);
    
    // </editor-fold>
    
    /** Creates a new instance of DesignPanel */
    public DesignPanel() {
        super(new BorderLayout());
        
        setOpaque(true);
        setBackground(Color.WHITE);
        
        drawingPad.addMouseWheelListener(this);
        drawingPad.addMouseMotionListener(this);
        
        measurementScale = 1.0f;
        
        
        // Add the design component.
        add(drawingPad, BorderLayout.CENTER);
        
        // Add the scroll bars.
        add(scrollBarVertical, BorderLayout.EAST);
        add(scrollBarHorizontal, BorderLayout.SOUTH);
        
        // Add the rulers.
        add(rulerRow, BorderLayout.WEST);
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.setBackground(Color.WHITE);
        panelNorth.add(corner, BorderLayout.WEST);
        panelNorth.add(rulerColumn);
        add(panelNorth, BorderLayout.NORTH);
        
        // Set the scrollbars up.
        scrollBarVertical.addAdjustmentListener(this);
        scrollBarHorizontal.addAdjustmentListener(this);
    }
    
    /** This will open the RXML file and create a DesignPanel.
     * @param root is the root element, should be "rxml".
     * @param InterfaceFrameOperation is the interface used to operation the main frame.
     * @return A new DesignPanel containing the design.
     */
    public static DesignPanel openDesign(Element root, InterfaceFrameOperation iFrameOperator) throws Exception {
        DesignPanel dPanel = new DesignPanel();
        dPanel.drawingPad.openRXML(root, iFrameOperator);
        dPanel.iFrameOperator = iFrameOperator;
        return dPanel;
    }
    
    /** This will create the design with the width and height measurements.
     * @param InterfaceFrameOperation is the interface used to operation the main frame.
     * @param designWidth is the width of the new design in measurements.
     * @param designHeight is the height of the new design in measurements.
     */
    public void createDesign(InterfaceFrameOperation iFrameOperator, float designWidth, float designHeight) {
        drawingPad.createDesign(iFrameOperator, designWidth, designHeight);
        this.iFrameOperator = iFrameOperator;
    }
    
    public DrawingDesign getDesign() {
        return drawingPad.getDesign();
    }
    
    public DrawingPad getDrawingPad() {
        return drawingPad;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Zoom and Position Changing Methods ">
    
    /** @return percentage of the current zoom 100% = 1.0f.
     */
    public float getZoom() {
        return measurementScale;
    }
    
    /** This will zoom in on the layer area.
     * @param lInfo can be null. In that case it will zoom on the entire design.
     * @param maxZoomPercentage is the maximum percentage to zoom in on the zoom area. If the area to zoom in
     * is so small that it would require a greater than maxZoomPercentage than it will limit the zoom to the
     * maxZoomPercentage.
     */
    public void zoomTo(LayerInfo lInfo, float maxZoomPercentage) {
        // Zoom in on the design.
        Rectangle2D.Float fZoomArea;
        if (lInfo == null) {
            // Could be null if nothing in the design.
            fZoomArea = getDesign().getBounds2D();
            if (fZoomArea == null) {
                new JOptionPane().showMessageDialog(this, "No lines to zoom on in drawing.");
                return;
            }
        } else {
            // Could be null if nothing in the layer.
            fZoomArea = getDesign().getLayerBounds2D(lInfo);
            if (fZoomArea == null) {
                new JOptionPane().showMessageDialog(this, "No lines to zoom on in the layer [" + lInfo.getName() + "].");
                return;
            }
        }
        
        // Enlarge the zoom area a little so that the edge vertices are not clipped off a little.
        fZoomArea.x -= 2.0f;
        fZoomArea.y -= 2.0f;
        fZoomArea.width += 4.0f;
        fZoomArea.height += 4.0f;
        
        // Zoom in on the area.
        zoomTo(fZoomArea, maxZoomPercentage);
    }
    
    /** This will zoom in on the zoom area.
     * @param fZoomArea is the area to zoom in on at in Measurements.
     * @param maxZoomPercentage is the maximum percentage to zoom in on the zoom area. If the area to zoom in
     * is so small that it would require a greater than maxZoomPercentage than it will limit the zoom to the
     * maxZoomPercentage.
     */
    public void zoomTo(Rectangle2D.Float fZoomArea, float maxZoomPercentage) {
        measurementScale = drawingPad.zoomTo(fZoomArea, maxZoomPercentage);
        
        // Align the scroll bars.
        alignScrollBars();
        rulerColumn.zoomTo(measurementScale);
        rulerRow.zoomTo(measurementScale);
    }
    
    /** This will zoom to the fptZoomTo position with the percentage.
     * @param fptZoomTo is the position to zoom to.
     * @param percentage is the zoom percentage to zoom to.
     */
    public void zoomTo(FPointType fptZoomTo, float percentage) {
        drawingPad.zoomTo(percentage, fptZoomTo);
        
        // Change the measurement.
        measurementScale = percentage;
        
        // Align the scroll bars.
        alignScrollBars();
        rulerColumn.zoomTo(measurementScale);
        rulerRow.zoomTo(measurementScale);
    }
    
    /** This will change the scaling of the design.
     * @param percentage 100% = 1.0f, must be as a percentage. If percentage is less than 10% than it will set it
     * to 10%.
     */
    public void zoomTo(float percentage) {
        if (percentage < 0.1f)
            percentage = 0.1f;
        
        // Update the drawing pad positions and offsets.
        drawingPad.zoomTo(percentage);
        
        // Change the measurement.
        measurementScale = percentage;
        
        // Align the scroll bars.
        alignScrollBars();
        rulerColumn.zoomTo(measurementScale);
        rulerRow.zoomTo(measurementScale);
    }
    
    /** This will align the scroll bars after a zoom so that they are correct. This should be
     * called after the drawingPad is zoomed and the measurementScale has been updated.
     */
    private void alignScrollBars() {
        // Need all measurements converted into pixels for the scroll bars.
        float widthDesignPixel = getWidthOfDesign() * measurementScale * Measurement.getPixelsPerMeasurement();
        float heightDesignPixel = getHeightOfDesign() * measurementScale * Measurement.getPixelsPerMeasurement();
        float widthDrawingPadPixel = drawingPad.getWidth();
        float heightDrawingPadPixel = drawingPad.getHeight();
        float xOffsetDesignPixel = getXOffsetIntoDesign() * measurementScale * Measurement.getPixelsPerMeasurement();
        float yOffsetDesignPixel = getYOffsetIntoDesign() * measurementScale * Measurement.getPixelsPerMeasurement();
        
        // The design will need the scroll bars to scroll the design since it is bigger than the view port.
        if (widthDesignPixel > widthDrawingPadPixel) {
            scrollBarHorizontal.setVisible(true);
            scrollBarHorizontal.setMaximum((int)widthDesignPixel);
            scrollBarHorizontal.setVisibleAmount((int)widthDrawingPadPixel);
            scrollBarHorizontal.setValue((int)xOffsetDesignPixel);
            scrollBarHorizontal.setBlockIncrement((int)(widthDesignPixel / 10));
            scrollBarHorizontal.setUnitIncrement((int)(widthDesignPixel / 100));
        } else {
            // Scroll bar is not needed.
            scrollBarHorizontal.setVisible(false);
        }
        
        // The design will need the scroll bars to scroll the design since it is bigger than the view port.
        if (heightDesignPixel > heightDrawingPadPixel) {
            scrollBarVertical.setVisible(true);
            scrollBarVertical.setMaximum((int)heightDesignPixel);
            scrollBarVertical.setVisibleAmount((int)heightDrawingPadPixel);
            scrollBarVertical.setValue((int)yOffsetDesignPixel);
            scrollBarVertical.setBlockIncrement((int)(heightDesignPixel / 10));
            scrollBarVertical.setUnitIncrement((int)(heightDesignPixel / 100));
        } else {
            // Scroll bar is not needed.
            scrollBarVertical.setVisible(false);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    /** Called whenever there is a change in the layout.
     */
    public void doLayout() {
        super.doLayout();
        zoomTo(measurementScale);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface InterfaceScale ">
    
    public float getMeasurementScale() {
        return measurementScale;
    }
    
    public float getWidthOfDesign() {
        return drawingPad.getDesign().getWidth();
    }
    
    public float getHeightOfDesign() {
        return drawingPad.getDesign().getHeight();
    }
    
    public float getXOffsetIntoDesign() {
        return drawingPad.getXOffsetIntoDesign();
    }
    
    public float getYOffsetIntoDesign() {
        return drawingPad.getYOffsetIntoDesign();
    }
    
    public float getXOffsetOfDesign() {
        return drawingPad.getXOffsetOfDesign();
    }
    
    public float getYOffsetOfDesign() {
        return drawingPad.getYOffsetOfDesign();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface AdjustmentListener ">
    
    public void adjustmentValueChanged(AdjustmentEvent ae) {
        float valueMeasurement = ae.getValue() / (Measurement.getPixelsPerMeasurement() * measurementScale);
        
        if (ae.getSource() == scrollBarHorizontal && valueMeasurement != getXOffsetIntoDesign()) {
            float measurementOffset = (float)ae.getValue() / (measurementScale * Measurement.getPixelsPerMeasurement());
            drawingPad.setXOffsetIntoDesign(measurementOffset);
            drawingPad.repaint();
            rulerColumn.repaint();
        } else if (ae.getSource() == scrollBarVertical && valueMeasurement != getYOffsetIntoDesign()) {
            float measurementOffset = (float)ae.getValue() / (measurementScale * Measurement.getPixelsPerMeasurement());
            drawingPad.setYOffsetIntoDesign(measurementOffset);
            drawingPad.repaint();
            rulerRow.repaint();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseWheelListener ">
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        float newMeasurement = measurementScale;
        if (e.getUnitsToScroll() > 0) {
            if (e.isShiftDown())
                newMeasurement -= 0.5;
            else
                newMeasurement -= 0.1;            
            if (newMeasurement < 0.1f)
                newMeasurement = 0.1f;
        } else {
            if (e.isShiftDown())
                newMeasurement += 0.5;
            else
                newMeasurement += 0.1;
        }
        
        zoomTo(newMeasurement);
        iFrameOperator.setZoom(newMeasurement);
        drawingPad.repaint();
        rulerColumn.repaint();
        rulerRow.repaint();
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseMotionListener ">
    
    public void mouseDragged(MouseEvent e) {
        
    }
    
    public void mouseMoved(MouseEvent e) {
        FPointType fpt = drawingPad.getMousePositionMeasurement();
        rulerColumn.setCursorPos(fpt.x);
        rulerRow.setCursorPos(fpt.y);
    }
    
    // </editor-fold>
    
}