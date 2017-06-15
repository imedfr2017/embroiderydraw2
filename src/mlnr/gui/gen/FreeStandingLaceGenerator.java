/*
 * FreeStandingLaceGenerator.java
 *
 * Created on February 5, 2007, 6:00 PM
 *
 */

package mlnr.gui.gen;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import mlnr.draw.DrawingDesign;
import mlnr.draw.TransformGraph;
import mlnr.gui.dlg.DialogFreeStandingLaceGenerator;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class FreeStandingLaceGenerator extends PolygonGenerator {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** Generate both lace items. */
    public static final int GENERATE_BOTH = 1;
    /** Generate bottom lace item. */
    public static final int GENERATE_BOTTOM = 2;
    /** Generate side lace item. */
    public static final int GENERATE_SIDE = 3;

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the design to place the generated items into. */
    private DrawingDesign design;
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">

    /** Creates a new instance of FreeStandingLaceGenerator */
    public FreeStandingLaceGenerator(DrawingDesign design) {
        this.design = design;
    }
   
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    
    /** This will retrieve the paramters from the dialog and create the free standing lace.
     * @param dialog is assumed to be already ran.
     * @param generateItems is the FreeStandingLace.GENERATE_* option.
     */
    public void generateFreeStandingLace(DialogFreeStandingLaceGenerator dialog, int generateItems) {
        switch (generateItems) {
            case GENERATE_BOTH:
                generateBoth(dialog);
                break;
            case GENERATE_BOTTOM:
                generateBottom(dialog);
                break;
            case GENERATE_SIDE:
                generateSide(dialog);
                break;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Private Methods ">

    /** This will generate the both parts at the center of the design.
     * @param dialog is assumed to be already ran and populated with parameter values.
     */
    private void generateBoth(DialogFreeStandingLaceGenerator dialog) {
        int sideCount = dialog.getBottomSideCount();
        int sideSideCount = dialog.getSideSideCount();
        float length = dialog.getLengthBottom();
        
        // Generate the equal sided polygon.
        FPointType fptZero = new FPointType(0.0f, 0.0f);       
        TransformGraph gBottom = generateEqualPolygon(fptZero, sideCount, length);        
        
        // Generate the FSL polygon.
        TransformGraph gSide = null;
        switch (sideSideCount) {
            case 4:
                gSide = generateFSLSideCup(fptZero, dialog.getLengthBottom(), dialog.getLengthTop(), dialog.getLengthTopHeight());
                break;
            case 5:
                gSide = generateFSLSidePentagon(fptZero, dialog.getLengthBottom(), dialog.getLengthMiddle(), dialog.getLengthTopHeight(), dialog.getLengthBottomHeight());
                break;
            case 6:
                gSide = generateFSLSideHexagon(fptZero, dialog.getLengthBottom(), dialog.getLengthMiddle(), dialog.getLengthTop(), dialog.getLengthTopHeight(), dialog.getLengthBottomHeight());
                break;
            default:
                throw new IllegalArgumentException("generateSide() Unknown type of side to generate.");
        }
        
        // Get the size of the graphs.
        Rectangle2D.Float rectBottom = gBottom.getBounds2D();
        Rectangle2D.Float rectSide = gSide.getBounds2D();
        
        // Position to place the side polygon.
        FPointType fptSide = new FPointType();
        fptSide.x = (design.getWidth() - (float)rectSide.getWidth()) / 2 - 3;
        fptSide.y = (design.getHeight() / 2);
        
        // Position to place the bottom polygon.
        FPointType fptBottom = new FPointType();
        fptBottom.x = (design.getWidth() + (float)rectBottom.getWidth()) / 2 + 3;
        fptBottom.y = fptSide.y;
        
        // Move the polygons.
        gSide.setAllTransformable(true);
        gSide.center(fptSide);
        gSide.finalizeMovement();        
        gBottom.setAllTransformable(true);
        gBottom.center(fptBottom);
        gBottom.finalizeMovement();        
        
        // Add the graphs to the design.
        design.addUndoMarker();
        design.add(gSide);
        design.add(gBottom);
        design.completeUndo();
    }

    /** This will generate the bottom part at the center of the design.
     * @param dialog is assumed to be already ran and populated with parameter values.
     */
    private void generateBottom(DialogFreeStandingLaceGenerator dialog) {   
        int sideCount = dialog.getBottomSideCount();
        float length = dialog.getLengthBottom();
        
        // Generate the equal sided polygon and add it to the design.
        FPointType fptCenterPt = new FPointType(design.getWidth() / 2.0f, design.getHeight() / 2.0f);       
        design.add(generateEqualPolygon(fptCenterPt, sideCount, length));
    }

    /** This will generate the side part at the center of the design.
     * @param dialog is assumed to be already ran and populated with parameter values.
     */
    private void generateSide(DialogFreeStandingLaceGenerator dialog) {        
        int sideCount = dialog.getSideSideCount();
        FPointType fptCenterPt = new FPointType(design.getWidth() / 2.0f, design.getHeight() / 2.0f);       
        
        // Generate the FSL polygon and add it to the design.
        switch (sideCount) {
            case 4:
                design.add(generateFSLSideCup(fptCenterPt, dialog.getLengthBottom(), dialog.getLengthTop(), dialog.getLengthTopHeight()));
                break;
            case 5:
                design.add(generateFSLSidePentagon(fptCenterPt, dialog.getLengthBottom(), dialog.getLengthMiddle(), dialog.getLengthTopHeight(), dialog.getLengthBottomHeight()));                
                break;
            case 6:
                design.add(generateFSLSideHexagon(fptCenterPt, dialog.getLengthBottom(), dialog.getLengthMiddle(), dialog.getLengthTop(), dialog.getLengthTopHeight(), dialog.getLengthBottomHeight()));
                break;
            default:
                throw new IllegalArgumentException("generateSide() Unknown type of side to generate.");
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Generate FSL Polygons ">
    
    /** This will generate a free standing lace side pentagon:
     *  <pre>
     *     /\
     *    /  \
     *   /    \
     *   \    /
     *    \  /
     *     ---
     *  </pre>
     *  Note that the middle part is at the y distance of height divide by 2.
     * @param ftpCenterPt is the center position of the polygon in the Graph.
     * @param lengthBottom is the length of the bottom part of this polygon.
     * @param lengthMiddle is the length of the middle part of this polygon.
     * @param topHeight is the distance between the top point and the middle point.
     * @param bottomHeight is the distance between the middle point and the bottom point.
     * @return a graph of the generated polygon.
     */
    private TransformGraph generateFSLSidePentagon(FPointType fptCenterPt, float lengthBottom, float lengthMiddle, float topHeight, float bottomHeight) {
        // Note y-coordinates are flipped because the screen coordinates are flipped.
        
        // Calculate middle points.
        float xMiddleLeft = -lengthMiddle / 2;
        float xMiddleRight = xMiddleLeft + lengthMiddle;
        float yMiddle = topHeight;
        
        // Calculate bottom points.
        float xBottomLeft = -lengthBottom / 2;
        float xBottomRight = xBottomLeft + lengthBottom;
        float yBottom = topHeight + bottomHeight;
        
        // Start at the top and go clockwise from there.
        LinkedList ltPoints = new LinkedList();        
        ltPoints.add(new FPointType(0.0f, 0.0f));
        ltPoints.add(new FPointType(xMiddleRight, yMiddle));
        ltPoints.add(new FPointType(xBottomRight, yBottom));
        ltPoints.add(new FPointType(xBottomLeft, yBottom));
        ltPoints.add(new FPointType(xMiddleLeft, yMiddle));
                
        // Create the polygon and center it to the fptCenterPt point.
        TransformGraph tGraph = TransformGraph.createPolygon(ltPoints);
        tGraph.setAllTransformable(true);
        tGraph.center(fptCenterPt);
        tGraph.finalizeMovement();
        
        return tGraph;
    }
    
    /** This will generate a free standing lace side hexagon:
     *  <pre>
     *     ---
     *    /   \
     *    \   /
     *     ---
     *  </pre>
     *  Note that the middle part is at the y distance of height divide by 2.
     * @param ftpCenterPt is the center position of the polygon in the Graph.
     * @param lengthBottom is the length of the bottom part of this polygon.
     * @param lengthMiddle is the length of the middle part of this polygon.
     * @param lengthTop is the length of the top part of this polygon.
     * @param topHeight is the distance between the top point and the middle point.
     * @param bottomHeight is the distance between the middle point and the bottom point.
     * @return a graph of the generated polygon.
     */
    private TransformGraph generateFSLSideHexagon(FPointType fptCenterPt, float lengthBottom, float lengthMiddle, float lengthTop, float topHeight, float bottomHeight) {
        // Note y-coordinates are flipped because the screen coordinates are flipped.
        
        // Calculate middle points.
        float xMiddleLeft = (lengthTop - lengthMiddle) / 2;
        float xMiddleRight = xMiddleLeft + lengthMiddle;
        float yMiddle = topHeight;
        
        // Calculate bottom points.
        float xBottomLeft = (lengthTop - lengthBottom) / 2;
        float xBottomRight = xBottomLeft + lengthBottom;
        float yBottom = topHeight + bottomHeight;
        
        // Start at the top-left and go clockwise from there.
        LinkedList ltPoints = new LinkedList();        
        ltPoints.add(new FPointType(0.0f, 0.0f));
        ltPoints.add(new FPointType(lengthTop, 0.0f));
        ltPoints.add(new FPointType(xMiddleRight, yMiddle));
        ltPoints.add(new FPointType(xBottomRight, yBottom));
        ltPoints.add(new FPointType(xBottomLeft, yBottom));
        ltPoints.add(new FPointType(xMiddleLeft, yMiddle));
                
        // Create the polygon and center it to the fptCenterPt point.
        TransformGraph tGraph = TransformGraph.createPolygon(ltPoints);
        tGraph.setAllTransformable(true);
        tGraph.center(fptCenterPt);
        tGraph.finalizeMovement();
        
        return tGraph;
    }
    
    /** This will generate a free standing lace side cup:
     *  <pre>
     *  _____
     *  \   /
     *   \ /
     *    -
     * </pre>                                                
     * @param ftpCenterPt is the center position of the polygon in the Graph.
     * @param lengthBottom is the length of the bottom part of this polygon.
     * @param lengthTop is the length of the top part of this polygon.
     * @param height is the distance between the top and bottom parts.
     * @return a graph of the generated polygon.
     */
    private TransformGraph generateFSLSideCup(FPointType fptCenterPt, float lengthBottom, float lengthTop, float height) {
        // Note y-coordinates are flipped because the screen coordinates are flipped.
                
        // Calculate bottom points.
        float xBottomLeft = (lengthTop - lengthBottom) / 2;
        float xBottomRight = xBottomLeft + lengthBottom;
        
        // Start at the top-left and go clockwise from there.
        LinkedList ltPoints = new LinkedList();        
        ltPoints.add(new FPointType(0.0f, 0.0f));
        ltPoints.add(new FPointType(lengthTop, 0.0f));
        ltPoints.add(new FPointType(xBottomRight, height));
        ltPoints.add(new FPointType(xBottomLeft, height));
                
        // Create the polygon and center it to the fptCenterPt point.
        TransformGraph tGraph = TransformGraph.createPolygon(ltPoints);
        tGraph.setAllTransformable(true);
        tGraph.center(fptCenterPt);
        tGraph.finalizeMovement();
        
        return tGraph;
    }    
    
    // </editor-fold>
    
}
