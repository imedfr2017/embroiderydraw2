/*
 * SampledPointProcess.java
 *
 * Created on November 25, 2006, 2:11 PM
 *
 */

package mlnr.draw.expt.pem;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import mlnr.type.*;

/** This class will process the sampled points.
 *
 * @author Robert Molnar II
 */
class SampledPointProcess {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    LinkedList<LinkedList<SFPointType>> ltSampledPoints;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the sampling rate for curve lines. a.k.a 1mm. */
    static float DISTANCE_SAMPLING_RATE = 1.0f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    SampledPointProcess(LinkedList<LinkedList<SFPointType>> ltSampledPoints) {
        this.ltSampledPoints = ltSampledPoints;
        
        if (ltSampledPoints.size() == 0)
            throw new IllegalArgumentException("SampledPointProcess(" + ltSampledPoints + ") unable to process empty list.");
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    
    /** This will sample the points.
     * @return a list of the points sampled from the raw source of points.
     */
    public LinkedList<Point> sample() {
        LinkedList<Point> ltPoints = new LinkedList();
        
        // Add the first point into the list.
        ltPoints.add(getFirstPoint());
        
        // Each segment must be sampled.
        for (Iterator<LinkedList<SFPointType>> itr = ltSampledPoints.iterator(); itr.hasNext(); ) {
            LinkedList<SFPointType> ltSFPoints = itr.next();
            
            // Check out the point and see what type of sampling we have.
            SFPointType sfPoint = ltSFPoints.getFirst();
            LinkedList<Point> ltPt = null;
            if (sfPoint.isLineSample())
                ltPt = sampleLine(ltSFPoints);
            else
                ltPt = sampleCurve(ltSFPoints);
            
            // no duplicates.
            if (ltPoints.isEmpty() == false) {
                Point ptLast = ltPoints.getLast();
                Point ptFirst = ltPt.getFirst();
                if (ptLast.equals(ptFirst))
                    ltPoints.removeLast();
            }
            
            // Add all points in from the segment sampling.
            ltPoints.addAll(ltPt);
        }
        
        return ltPoints;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Public Methods ">
    
    /** @return the distance to sample the curves. In measurements a.k.a. millimeters.
     */
    static public float getSamplingRate() {
        return DISTANCE_SAMPLING_RATE;
    }
    
    /** @param samplingRate is the distance to sample the curves. In measurements a.k.a. millimeters.
     */
    static public void setSamplingRate(float samplingRate) {
        DISTANCE_SAMPLING_RATE = samplingRate;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
    
    /** @return the first point in the list of sampled points.
     */
    private Point getFirstPoint() {
        SFPointType firstPoint = ltSampledPoints.getFirst().getFirst();
        return PemV4.convertToPEM(firstPoint);
    }
    
    /** This will sample the points in the ltSFPoints. ltSFPoints is a sampling of line points.
     * @param ltSFPoints is the list of points to sample and must be sampled from a line.
     * @return a list of Points in pem coordinates.
     */
    private LinkedList<Point> sampleLine(LinkedList<SFPointType> ltSFPoints) {
        LinkedList<Point> ltPt = new LinkedList();
        
        // sample each point.
        for (Iterator<SFPointType> itr = ltSFPoints.iterator(); itr.hasNext(); ) {
            SFPointType sPt = itr.next();
            ltPt.add(PemV4.convertToPEM(sPt));
        }
        
        return ltPt;
    }   
    
    /** This will sample the curve segments. ltSFPoints is a sampling of curve points.
     * @param ltSFPoints is the list of points to sample and must be sampled from a curve.
     * @return a list of sampled points in pem coordinates.
     */
    private LinkedList<Point> sampleCurve(LinkedList<SFPointType> ltSFPoints) {
        LinkedList<Point> ltSampledPoints = new LinkedList();
        float sampleCurveDistance = sampleCurveDistance(ltSFPoints);
        
        // Add the first point to the list of points and for a reference to get the distance from.
        SFPointType prev = ltSFPoints.getFirst();
        float distance = 0.0f;
        ltSampledPoints.add(PemV4.convertToPEM(prev));
        
        // points will only be added if the distance from the previous point is greater than sampleCurveDistance.
        for (Iterator<SFPointType> itr = ltSFPoints.iterator(); itr.hasNext(); ) {
            SFPointType curr = itr.next();
            
            // Add the point if it is greater than or equal to the sample curve distance.
            distance += curr.distance(prev);
            if (distance >= sampleCurveDistance) {
                distance -= sampleCurveDistance;
                ltSampledPoints.add(PemV4.convertToPEM(curr));
            }
            
            prev = curr;
        }
        
        // pop the last one if it is within the sampling distance of the end point and add the end point.
        Point endPoint = PemV4.convertToPEM(ltSFPoints.getLast());
        if (ltSampledPoints.size() > 1) {
            Point pt = ltSampledPoints.getLast();
            FPointType fptLast = new FPointType(pt);
            if (new FPointType(endPoint).distance(fptLast) < sampleCurveDistance)
                ltSampledPoints.removeLast();
        }
        
        // add the end point.
        ltSampledPoints.add(endPoint);
        
        return ltSampledPoints;
    }
    
    /** This will get the sample curve distance.
     * @return the distance they should be processed at.
     */
    private float sampleCurveDistance(LinkedList<SFPointType> ltSFPoints) {
        if (ltSampledPoints.isEmpty())
            return DISTANCE_SAMPLING_RATE;
        
        // Calculate the total distance.
        float totalDistance = 0.0f;
        SFPointType prev = ltSFPoints.getFirst();
        
        // If we are not sampling a curve then return the sampling rate.
        if (prev.isCurveSample() == false)
            return DISTANCE_SAMPLING_RATE;
        
        // get the distance to the next point.
        for (Iterator<SFPointType> itr = ltSFPoints.iterator(); itr.hasNext(); ) {
            SFPointType curr = itr.next();
            totalDistance += curr.distance(prev);
            prev = curr;
        }
        
        // Get the sampling rate.
        int sampleTimes = (int)(totalDistance / DISTANCE_SAMPLING_RATE);
        return totalDistance / sampleTimes;
    }
    
    // </editor-fold>    
}
