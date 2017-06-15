/*
 * DoubleBufferCanvas.java
 *
 * Created on June 4, 2005, 10:33 PM
 */

package mlnr.gui.cpnt;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.rmi.dgc.VMID;

/**
 *
 * @author Robert Molnar II
 */
abstract public class DoubleBufferCanvas extends Canvas {
    private static Image offScreenBuffer;
    private boolean doubleBuffered = false;
    
    /** Creates a new instance of MyCanvas */
    public DoubleBufferCanvas(boolean doubleBuffered) {
        this.doubleBuffered = doubleBuffered;
    }
    
    public void update(Graphics g) {
        if (doubleBuffered) {
            Graphics gr;
            if (offScreenBuffer==null ||
                    (! (offScreenBuffer.getWidth(this) == this.getSize().width
                    && offScreenBuffer.getHeight(this) == this.getSize().height))) {
                offScreenBuffer = this.createImage(getSize().width, getSize().height);
            }
            
            // We need to use our buffer Image as a Graphics object:
            gr = offScreenBuffer.getGraphics();
            gr.setClip(0, 0, getWidth(), getHeight());

            paint(gr); // Passes our off-screen buffer to our paint method, which,
            // unsuspecting, paints on it just as it would on the Graphics
            // passed by the browser or applet viewer.
            g.drawImage(offScreenBuffer, 0, 0, this);
        } else
            paint(g);
        
        paintTool();
    }
    
    /** This will paint the tool. The reason that it is apart from the paint function is that the paint
     * function will incorrectly paint the tools by a very small offset that I can't seem to figure out how
     * to correct it. This this function is borned.
     */
    abstract public void paintTool();
}
