/*
 * DesignPreview.java
 *
 * Created on November 21, 2005, 3:03 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.beans.*;
import java.awt.Dimension;
import java.awt.*;
import java.io.*;
import mlnr.draw.ComplexPattern;
import mlnr.util.DefaultExceptionHandler;


/**
 *
 * @author Robert Molnar II
 */
public class DesignPreview extends AccessoryFileChooser implements PropertyChangeListener, MouseListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    private JFileChooser jfc;
    private ComplexPattern pattern = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of DesignPreview
     * @param width is the width of this JComponent.
     * @param height is the height of this JComponet.
     */
    public DesignPreview(int width, int height) {
        Dimension sz = new Dimension(width, height);
        setPreferredSize(sz);
        
        addMouseListener(this);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public General Methods ">
    
    /** This will change the current pattern to the new one and repaint the component.
     * @param pattern is the new pattern to change to.
     */
    public void changePattern(ComplexPattern pattern) {
        this.pattern = pattern;
        repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Private Methods ">
    
    private void load(File f) throws IOException {
        if (f == null)
            return;
        
        this.pattern = ComplexPattern.open(f, false);
        repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Implemented Methods AccessoryFileChooser ">
    
    public void setJFileChooser(JFileChooser jfc) {
        this.jfc = jfc;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface PropertyChangeListener ">
    
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            File f = jfc.getSelectedFile();
            if (f != null && f.isFile())
                load(jfc.getSelectedFile());
        } catch (IOException ex) {
            DefaultExceptionHandler.printExceptionToLog(ex);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden JComponent Methods ">
    
    public void paintComponent(Graphics g) {
        int componentWidth = getWidth();
        int componentHeight = getHeight();
        // fill the background
        g.setColor(Color.WHITE);
        g.fillRect(0,0,getWidth(),getHeight());
        
        if (pattern == null) {
            // print a message
            g.setColor(Color.black);
            g.drawString("Not a valid drawing", componentWidth / 4, (componentHeight / 2) - 10);
            return;
        }
        
        // Draw the pattern into this JComponent.
        pattern.drawPreview((Graphics2D)g, componentWidth-10.0f, componentHeight-5.0f);
        
    }
    
    // </editor-fold>

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
}
