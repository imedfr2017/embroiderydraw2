/*
 * ComboBoxZoom.java
 *
 * Created on January 19, 2007, 11:34 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import mlnr.gui.InterfaceFrameOperation;

/**
 *
 * @author Robert Molnar II
 */
public class ComboBoxZoom extends JComboBox {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    ActionKeyListener listener = new ActionKeyListener();
    InterfaceFrameOperation iFrameOperator;        
    
    /** Creates a new instance of ComboBoxZoom */
    public ComboBoxZoom(InterfaceFrameOperation iFrameOperator) {
        super();
        
        this.iFrameOperator = iFrameOperator;
        
        model.addElement(InterfaceFrameOperation.ITEM_DRAWING);
        model.addElement(InterfaceFrameOperation.ITEM_LAYER);
        model.addElement("1600%");
        model.addElement("800%");
        model.addElement("400%");
        model.addElement("200%");
        model.addElement("150%");
        model.addElement("125%");
        model.addElement("100%");
        model.addElement("90%");
        model.addElement("75%");
        model.addElement("50%");
        model.addElement("25%");
        setModel(model);
        setMaximumRowCount(100);
        model.setSelectedItem("100%");
        
        setEditable(true);
        setPreferredSize(new Dimension (85, 25));
        
        addActionListener(listener);
        addKeyListener(listener);
        addFocusListener(listener);
    }
    
    /** This will set the zoom for the combo box.
     * @param percentage where 1.0 = 100%.
     */
    public void setZoom(float percentage) {
        percentage *= 100.f;
        int iPercentage = (int)percentage;
        model.setSelectedItem(Integer.toString(iPercentage) + "%");
    }
    
    class ActionKeyListener implements ActionListener, KeyListener, FocusListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        public void actionPerformed(ActionEvent e) {
            // Get the zoom percentage and change the zoom percentage.
            String zoomPercentage = model.getSelectedItem().toString();
            
            // Zoom in on the drawing or layer.
            if (InterfaceFrameOperation.ITEM_DRAWING.equals(zoomPercentage)) {
                iFrameOperator.zoomItem(InterfaceFrameOperation.ITEM_DRAWING);
                return;
            } else if (InterfaceFrameOperation.ITEM_LAYER.equals(zoomPercentage)) {
                iFrameOperator.zoomItem(InterfaceFrameOperation.ITEM_LAYER);
                return;
            }
            
            // Remove the percentage sign.
            int indexSign = zoomPercentage.indexOf("%");
            if (indexSign > 0)
                zoomPercentage = zoomPercentage.substring(0, indexSign);
            
            // Convert string to float.
            float percentage;
            try {
                percentage = Float.parseFloat(zoomPercentage);
            } catch (Exception ee) {
                percentage = -1.0f;
            }
            
            // Since the percentage doesn't exist or is incorrect set it to what the current selected index is.
            if (percentage <= 0.0f)
                percentage = 100.0f;
            
            // Call frame to update the zoom.
            iFrameOperator.setZoom(percentage / 100.0f);
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
        }
    }
}