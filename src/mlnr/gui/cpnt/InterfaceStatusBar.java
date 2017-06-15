/*
 * InterfaceStatusBar.java
 *
 * Created on October 31, 2005, 11:14 AM
 *
 */

package mlnr.gui.cpnt;

/**
 *
 * @author Robert Molnar II
 */
public interface InterfaceStatusBar {   
    
    /** This will set tell the status bar that the tool is at position (x, y).
     * @param x is in pixel coordinates.
     * @param y is in pixel coordinates.
     */
    public void setToolPosition(int x, int y);
}
