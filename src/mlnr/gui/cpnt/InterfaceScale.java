/*
 * InterfaceScale.java
 *
 * Created on July 28, 2006, 1:34 PM
 *
 */

package mlnr.gui.cpnt;

/** This interface is used to retrieve information about the current scale of the drawing.
 * @author Robert Molnar II
 */
public interface InterfaceScale {
    /** @return the number of pixels per scale, a.k.a. the scaling of the measurement. 
     */
    public float getMeasurementScale();
    
    /** @return the width of the design in measurements. */
    public float getWidthOfDesign();
    
    /** @return the height of the design in measurements. */
    public float getHeightOfDesign();
    
    /** @return the offset into the design in measurements.  */
    public float getXOffsetIntoDesign();
    
    /** @return the offset into the design in measurements.  */
    public float getYOffsetIntoDesign();
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getXOffsetOfDesign();
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getYOffsetOfDesign();
}
