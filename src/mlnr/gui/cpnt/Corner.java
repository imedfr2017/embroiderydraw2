/*
 * Corner.java
 *
 * Created on July 16, 2005, 2:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package mlnr.gui.cpnt;

import java.awt.*;
import javax.swing.*;

/** This is the corner piece for the rulers.
 *
 * @author Robert Molnar II
 */
public class Corner extends JComponent {
    private int size;
    private Color color = Color.WHITE;
    
    /** Creates a new instance of Corner */
    public Corner(int size) {
        setSize(size);
    }
    
    /** This will set the size of the corner piece.
     * @param size is the new size of the corner piece.
     */
    public void setSize(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
    }
    
    /** This will set the background color of the corner piece.
     * @param color is the new color of the corner piece.
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /** This will draw the ruler.
     */
    protected void paintComponent(Graphics g) {
        g.setColor(color);
        g.fillRect(0, 0, size, size);
    }
}
