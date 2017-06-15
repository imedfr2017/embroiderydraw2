/*
 * ButtonTab.java
 *
 * Created on December 27, 2006, 5:49 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author Robert Molnar II
 */
public class ButtonTab extends JPanel {
        
    // <editor-fold defaultstate="collapsed" desc=" static fields ">

    /** This is the size of the button. */
    private static final int SIZE = 10;

    /** Margin from the sides. */
    private static final int MARGIN = 2;
    
    /** This is the stoke used to draw the X. */
    private static final BasicStroke STROKE = new BasicStroke(2);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the label for the panel. */
    private final JLabel label;
    
    /** This is the button for the panel. */
    private final JButton button;
    
    /** This is the interface used to notify the user wants to close this file. */
    InterfaceButtonTab iButtonTab;
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ButtonTab */
    public ButtonTab(InterfaceButtonTab iButtonTab) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.iButtonTab = iButtonTab;
        
        // Set up this Panel.
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
        // Create the label.
        label = new JLabel(iButtonTab.getTitle());
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        add(label);
        
        // Create the button.
        button = new TabButton(iButtonTab.getTitle());
        add(button);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public Method ">
    
    /** This will notify the document has changed.
     * @param changed is true if the document has been changed, else false document
     * is saved.
     */
    public void notifyDocumentChanged(boolean changed) {
        if (changed)
            label.setText(iButtonTab.getTitle() + " *");
        else
            label.setText(iButtonTab.getTitle() );
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class TabButton ">
    
    /** This is the class used to draw the close button.
     */
    private class TabButton extends JButton implements ActionListener, MouseListener {
        
        // <editor-fold defaultstate="collapsed" desc=" Constructor ">
        
        public TabButton(String title) {
            // Size of button.
            setPreferredSize(new Dimension(SIZE, SIZE));
            // tool tip of button.            
            setToolTipText("Close " + title + " file");
            // Use basic LaF.
            setUI(new BasicButtonUI());
            // Make it transparent.
            setContentAreaFilled(false);
            // Can't focus button.
            setFocusable(false);
            // Set border to etched.
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            // Listener for buttons.
            addMouseListener(this);
            // Listener for actions.
            addActionListener(this);
            // Set the rollover effect.
            setRolloverEnabled(true);
        }        
        
        // </editor-fold>        
        
        // <editor-fold defaultstate="collapsed" desc=" Interface ActionListener ">
        
        public void actionPerformed(ActionEvent e) {
            iButtonTab.onCloseTab();
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">
                
        public void mouseEntered(MouseEvent e) {
            setBorderPainted(true);
        }
        
        public void mouseExited(MouseEvent e) {
            setBorderPainted(false);
        }
        
        public void mouseClicked(MouseEvent e) {
            
        }

        public void mousePressed(MouseEvent e) {
            
        }

        public void mouseReleased(MouseEvent e) {
            
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Override Methods ">
        
        public void updateUI() {
            // Do not update UI for this button, it uses the basic LaF.
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
                        
            int width = getWidth();
            int height = getHeight();
            
            // Fill in the button.
            g2d.setColor(Color.RED);
            if (getModel().isRollover())
                g2d.fill3DRect(0, 0, width, height, false);
            else
                g2d.fill3DRect(0, 0, width, height, true);
                        
            // Save the stroke and set it and color.
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(STROKE);
            if (getModel().isRollover())
                g2d.setColor(Color.MAGENTA);
            else
                g2d.setColor(Color.WHITE);
            
            // Draw the X.
            g2d.drawLine(MARGIN, MARGIN, width - MARGIN, height - MARGIN);
            g2d.drawLine(width - MARGIN, MARGIN, MARGIN, height - MARGIN);
            
            // Restore the stroke.
            g2d.setStroke(oldStroke);
        }
        
        // </editor-fold>
                
    }

    // </editor-fold>    
    
}
