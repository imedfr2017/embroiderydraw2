/*
 * DialogUtil.java
 *
 * Created on July 27, 2006, 6:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mlnr.gui.dlg;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Robert Molnar II
 */
public class DialogUtil {
    
    /** Creates a new instance of DialogUtil */
    public DialogUtil() {
    }
    
    public static final Point centerDialog(Rectangle parentRect, Rectangle dialogRect) {
        Point pt = new Point();
        pt.x = (int)(parentRect.getX() + (parentRect.getWidth() - dialogRect.getWidth()) / 2);
        pt.y = (int)(parentRect.getY() + (parentRect.getHeight() - dialogRect.getHeight()) / 2);        
        return pt;
    }
}
