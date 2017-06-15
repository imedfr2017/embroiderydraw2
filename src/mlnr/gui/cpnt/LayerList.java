/*
 * LayerList.java
 *
 * Created on August 5, 2005, 11:04 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import mlnr.EmbroideryDraw;
import mlnr.draw.LayerInfo;

/** JList that contains information about a layer.
 * @author Robert Molnar II
 */
public class LayerList extends JList {
    
    static ImageIcon imageStar, imageEmpty;
    static Color listForeground, listBackground;
    
    static {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        listForeground = uid.getColor("List.foreground");
        listBackground = uid.getColor("List.background");
        imageStar = new ImageIcon("images\\layerStar.gif");
        imageEmpty = new ImageIcon("images\\layerEmpty.jpg");
    }
    
    DefaultListModel defaultListModel = new DefaultListModel();
    
    /** Creates a new instance of LayerList */
    public LayerList() {
        super();
        setCellRenderer(new LayerListCellRenderer());
        setModel(defaultListModel);
    }
    
    /** This will update the layer list information.
     */
    public void update(LayerInfo []array) {
        clearAll();
        for (int i=0; i < array.length; i++)
            defaultListModel.addElement(array[i]);
    }    
    
    /** This will get the selected Layer information.
     */
    public LayerInfo getLayerSelected() {
        return (LayerInfo)getSelectedValue();
        
    }
    
    /** This will get the layer at the click position.
     * @param ptClick is the position where the mouse was righted-clicked.
     * @return the LayerInfo at that position or null.
     */
    public LayerInfo getLayerAtClick(Point ptClick) {
        
        int index = locationToIndex(ptClick);
        if (index == -1 || getCellBounds(index, index).contains(ptClick) == false)
            return null;
        return (LayerInfo)defaultListModel.get(index);
    }
    
    /** This will select a layer.
     * @param l is the layerInfo. The layer's id will be used to selected the layer.
     */
    public void selectLayer(LayerInfo l) {
        setSelectedIndex(getLayerIndex(l));
    }
    
    /** This will get the index position of the layer.
     * @param layerName is the name of the layer.
     * @return index position of the layer.
     * @exception Unknown layer name.
     */
    private int getLayerIndex(LayerInfo l) throws IllegalArgumentException {
        int size = defaultListModel.getSize();
        for (int i=0; i < size; i++) {
            LayerInfo lInfo = (LayerInfo)defaultListModel.get(i);
            if (lInfo.getId() == l.getId())
                return i;
        }
        
        throw new IllegalArgumentException("Layer[" + l.getString() + "] does not exist in the layer list.");
    }

    /** This will clear all layer information.
     */
    public void clearAll() {
        defaultListModel.removeAllElements();
    }
    
    class LayerListCellRenderer extends JComponent implements ListCellRenderer {
        DefaultListCellRenderer defaultComp;
        JCheckBox checkVisible = new JCheckBox();
        JLabel lblStar;
        // JLabel lblColor;
        
        public LayerListCellRenderer() {
            lblStar = new JLabel(imageStar);
            defaultComp = new DefaultListCellRenderer();
            
             setLayout(new BorderLayout());
             add(lblStar, BorderLayout.WEST);
             add(defaultComp, BorderLayout.CENTER);
             add(checkVisible, BorderLayout.EAST);
            
            //add(lblStar);
            //add(defaultComp);
            //add(checkVisible);
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            defaultComp.getListCellRendererComponent(list, value, index, isSelected,  cellHasFocus);
            
            LayerInfo lv = (LayerInfo)value;
            checkVisible.setSelected(lv.isVisible());
            
            // If selected then it is the one which is currently being used.
            if (isSelected)
                lblStar.setIcon(imageStar);
            else
                lblStar.setIcon(imageEmpty);
            
            // Get the layer color.
            Color c = lv.getColor();
            
            // Color the components.
            Component[] comps = getComponents();
            for (int i=0; i < comps.length; i++) {
                int colorIntensity = c.getRed() + c.getBlue() + c.getGreen();
                if (colorIntensity < 300)
                    comps[i].setForeground(Color.WHITE);
                else
                    comps[i].setForeground(Color.BLACK);
                
                comps[i].setBackground(c);
                
                //Color c2 = new Color(c.getRed());
            }
            return this;
        }
    }
}
