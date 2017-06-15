/*
 * JStatusPanel.java
 *
 * Created on October 31, 2005, 10:46 AM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Robert Molnar II
 */
public class JStatusPanel extends JPanel implements InterfaceStatusBar {
    Color []colorBarTop = {new Color(156, 154, 140), new Color(196, 194, 183), new Color(218, 215, 201), new Color(233, 231, 217)};
    JLabel labelToolPosition = new JLabel("");
    
    /** Creates a new instance of JStatusPanel */
    public JStatusPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(getWidth(), 23));
        add(labelToolPosition);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int width = getWidth();
        for (int y=0; y < colorBarTop.length; y++) {
            g.setColor(colorBarTop[y]);
            g.drawLine(0, y, width, y);
        }
    }
    
    public void setToolPosition(int x, int y) {
//        String textual = Measurement.getTextualName();
        
//        String message = String.format("X: %.2f " + textual + " Y: %.2f " + textual, Measurement.convertPixelToMeasurment(x),
//                Measurement.convertPixelToMeasurment(y));
        labelToolPosition.setText("TODO TOOL POSITION: x[" + x + "] y[" + y + "]");
    }
}