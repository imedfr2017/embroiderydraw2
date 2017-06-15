/*
 * PolygonGenerator.java
 *
 * Created on February 5, 2007, 6:09 PM
 *
 */

package mlnr.gui.gen;

import java.util.LinkedList;
import mlnr.draw.Graph;
import mlnr.draw.TransformGraph;
import mlnr.type.FPointType;

/**
 *
 * @author Robert Molnar II
 */
public class PolygonGenerator {
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of PolygonGenerator */
    public PolygonGenerator() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Generate Polygon Methods ">
    
    /** This will generate a polygon in the Graph, where each side of the polygon will be equal.
     * @param ftpCenterPt is the center position of the polygon in the Graph.
     * @param sideCount must be greater than equal to 3.
     * @param length is the length of each side.
     * @return a graph of the generated polygon.
     */
    protected TransformGraph generateEqualPolygon(FPointType fptCenterPt, int sideCount, float length) {
        if (sideCount < 3)
            throw new IllegalArgumentException("generatePolygon requires atleast a 3 sided polygon.");       
        
        // For each side calculate a point.
        FPointType ptPrev = new FPointType(0.0f, 0.0f);
        LinkedList ltPoints = new LinkedList();        
        float radianPerSide = ((float)Math.PI * 2) / (float)sideCount;
        float radian = 0.0f;
        for (int i=0; i < sideCount; i++) {
            FPointType ptCurr = new FPointType();
            
            // Calculate the next point.
            ptCurr.x = ptPrev.x + (float)Math.cos(radian) * length;
            ptCurr.y = ptPrev.y + (float)Math.sin(radian) * length;
            ltPoints.add(ptCurr);
            
            radian += radianPerSide;
            ptPrev = ptCurr;                    
        }
                
        // Create the polygon and center it to the fptCenterPt point.
        TransformGraph tGraph = TransformGraph.createPolygon(ltPoints);
        tGraph.setAllTransformable(true);
        tGraph.center(fptCenterPt);
        tGraph.finalizeMovement();
        
        return tGraph;
    }
    
    // </editor-fold>
}
