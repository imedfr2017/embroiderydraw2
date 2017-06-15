/*
 * AbstractRuler.java
 *
 * Created on October 3, 2005, 12:43 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/**
 *
 * @author Robert Molnar II
 */
abstract public class AbstractRuler extends JComponent {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is used to get current information about the design and scaling. */
    protected InterfaceScale iScale;
    
    /** This is true if it should erase first. */
    protected boolean erase = false;

    /** This is the position of the cursor in measurements. */
    protected float cursorPos;
    
    /** This is the class used to draw the ticks. */
    protected Line2D.Float fLine = new Line2D.Float();
    
    /** This is the font derived from the FONT. */
    protected Font font = new Font("SansSerif", Font.PLAIN, 10);
    
    /** This is the stroke used to draw the lines. */
    protected Stroke stroke = new BasicStroke(0.0f);
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the font of the ruler. */
    protected static final Font FONT = new Font("SansSerif", Font.PLAIN, 10);
    
    /** The size of the ruler. */
    protected static final int SIZE=25;
    protected static final float FSIZE=25.0f;
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of AbstractRuler */
    public AbstractRuler(InterfaceScale iScale) {
        this.iScale = iScale;
        setPreferredSize(new Dimension(SIZE, SIZE));
    }
    
    // </editor-fold>        
}
