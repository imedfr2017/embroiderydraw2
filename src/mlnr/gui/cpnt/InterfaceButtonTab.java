/*
 * InterfaceButtonTab.java
 *
 * Created on December 27, 2006, 5:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mlnr.gui.cpnt;

/**
 *
 * @author Robert Molnar II
 */
public interface InterfaceButtonTab {
    /** This is called when the close button is clicked on. */
    public void onCloseTab();
    
    /** This is the title to be displayed in the interface. */
    public String getTitle();
}
