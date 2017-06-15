 /*
 * LayerPanel.java
 *
 * Created on August 5, 2006, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mlnr.gui.cpnt;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import mlnr.draw.LayerInfo;
import mlnr.gui.FrameEmbroideryDraw;
import mlnr.draw.DrawingDesign;
import mlnr.gui.ButtonInfo;
import mlnr.gui.dlg.*;

/**
 *
 * @author Robert Molnar II
 */
public class LayerPanel extends JPanel implements ActionListener, MouseListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">

    /** Contains the ButtonInfo for this layer panel.
     */
    private LinkedList ltButtonInfo = new LinkedList();
    
    /** This is the main parentFrame of the program. */
    private FrameEmbroideryDraw parentFrame;
    
    /** This is the current drawing pad being worked on. */
    private DrawingPad drawingPad;
    
    /** This is the current design being worked on. */
    private DrawingDesign design;

    /** This is the popup layerinfo that is being worked on. */
    private LayerInfo lPopupInfo;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Component Fields ">
    
    private DesignDocument document;
    
    private JPanel panelButton = new JPanel();    
    private JButton btnNewLayer;
    private JButton btnDeleteAll;
    private JButton btnMergeTwoLayers;
    private JButton btnShowAllLayers;
    private JButton btnMergeAllToMaster;
    private LayerList layerList;
    
    private JMenuItem menuDeleteLayer;
    private JMenuItem menuChangeColor;
    private JMenuItem menuChangeName;
    private JMenuItem menuZoomLayer;
    private JMenuItem menuCopyLayer;
    private JMenuItem menuShowThisLayerOnly;
    private JMenuItem menuTurnLayerOnOff;
    private JMenuItem menuSaveAsRxml;
    private JMenuItem menuSaveAsBitMap;
    private JMenuItem menuSaveAsPEM;
    
    /** This is the menu when they right click on a layer */
    private JPopupMenu rightClickOnLayer;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor And Setup ">
    
    /** Creates a new instance of LayerPanel */
    public LayerPanel(FrameEmbroideryDraw parentFrame) {
        super();
        
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Layer Info"));
        setPreferredSize(new Dimension(210, 182));
        
        setupLayerList();
        setupButtons();
        setupPopupMenu();
        setupButtonInfos();
    }
    
    /** This will setup the layer list for the LayerPanel.
     */
    private void setupLayerList() {        
        layerList = new LayerList();
        layerList.addMouseListener(this);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroller = new JScrollPane(layerList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scroller, BorderLayout.CENTER);
    }

    /** This will setup button information. 
     */
    private void setupButtonInfos() {
        ltButtonInfo.add(new ButtonInfo(btnNewLayer, false, true, false));
        ltButtonInfo.add(new ButtonInfo(btnDeleteAll, false, true, false));
        ltButtonInfo.add(new ButtonInfo(btnMergeTwoLayers, false, true, false));
        ltButtonInfo.add(new ButtonInfo(btnShowAllLayers, false, true, true));
        ltButtonInfo.add(new ButtonInfo(btnMergeAllToMaster, false, true, false));
        
        ltButtonInfo.add(new ButtonInfo(menuDeleteLayer, false, true, false));
        ltButtonInfo.add(new ButtonInfo(menuChangeColor, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuChangeName, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuZoomLayer, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuCopyLayer, false, true, false));
        ltButtonInfo.add(new ButtonInfo(menuShowThisLayerOnly, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuTurnLayerOnOff, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuSaveAsRxml, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuSaveAsBitMap, false, true, true));
        ltButtonInfo.add(new ButtonInfo(menuSaveAsPEM, false, true, true));
    }
    
    /** This will setup the buttons for the layer design.
     */
    private void setupButtons() {
        btnNewLayer = new JButton(new ImageIcon("images\\icons\\Layer-New-layer.gif"));
        btnNewLayer.setToolTipText("New Layer");
        btnNewLayer.addActionListener(this);
        btnNewLayer.setBorder(null);
        
        btnDeleteAll = new JButton(new ImageIcon("images\\icons\\Layer-Delete-All.gif"));
        btnDeleteAll.setToolTipText("Delete All");
        btnDeleteAll.addActionListener(this);
        btnDeleteAll.setBorder(null);
        
        btnShowAllLayers = new JButton(new ImageIcon("images\\icons\\Layer-Show-All-Layers.gif"));
        btnShowAllLayers.setToolTipText("Show All Layers");
        btnShowAllLayers.addActionListener(this);
        btnShowAllLayers.setBorder(null);
        
        btnMergeTwoLayers = new JButton(new ImageIcon("images\\icons\\Layer-Merge-Two-Layers.gif"));
        btnMergeTwoLayers.setToolTipText("Merge Two Layers");
        btnMergeTwoLayers.addActionListener(this);
        btnMergeTwoLayers.setBorder(null);
        
        btnMergeAllToMaster = new JButton(new ImageIcon("images\\icons\\Layer-Merge-All-To-Master-Layer.gif"));
        btnMergeAllToMaster.setToolTipText("Merge All Layers to Current Layer");
        btnMergeAllToMaster.addActionListener(this);
        btnMergeAllToMaster.setBorder(null);        
        
        // Setup the panel for the buttons.
        panelButton.add(btnNewLayer);
        panelButton.add(btnDeleteAll);
        panelButton.add(btnMergeTwoLayers);
        panelButton.add(btnShowAllLayers);
        panelButton.add(btnMergeAllToMaster);
        add(panelButton, BorderLayout.SOUTH);
        
    }
    
    /** This will setup the popup menu.
     */
    private void setupPopupMenu() {
        rightClickOnLayer = new JPopupMenu();
        
        menuShowThisLayerOnly = new JMenuItem("Show This Layer Only");
        menuShowThisLayerOnly.addActionListener(this);
        
        menuTurnLayerOnOff = new JMenuItem("Turn Layer On/Off");
        menuTurnLayerOnOff.addActionListener(this);
        
        menuChangeColor = new JMenuItem("Change Color");
        menuChangeColor.addActionListener(this);
        
        menuChangeName = new JMenuItem("Change Name");
        menuChangeName.addActionListener(this);
        
        menuZoomLayer = new JMenuItem("Zoom Layer");
        menuZoomLayer.addActionListener(this);
        
        menuDeleteLayer = new JMenuItem("Delete Layer");
        menuDeleteLayer.addActionListener(this);

        menuSaveAsRxml = new JMenuItem("Save As Rxml");
        menuSaveAsRxml.addActionListener(this);

        menuSaveAsBitMap = new JMenuItem("Save As BitMap");
        menuSaveAsBitMap.addActionListener(this);

        menuSaveAsPEM = new JMenuItem("Save As PEM");
        menuSaveAsPEM.addActionListener(this);
        
        //       menuCopyLayer = new JMenuItem("Copy Layer");
        //       menuCopyLayer.addActionListener(this);
        
        rightClickOnLayer.add(menuShowThisLayerOnly);
        rightClickOnLayer.add(menuTurnLayerOnOff);
        rightClickOnLayer.addSeparator();
        rightClickOnLayer.add(menuZoomLayer);
        rightClickOnLayer.addSeparator();
        rightClickOnLayer.add(menuChangeColor);
        rightClickOnLayer.add(menuChangeName);
        rightClickOnLayer.addSeparator();
        rightClickOnLayer.add(menuSaveAsRxml);
        rightClickOnLayer.add(menuSaveAsBitMap);
        rightClickOnLayer.add(menuSaveAsPEM);
        rightClickOnLayer.addSeparator();
        rightClickOnLayer.add(menuDeleteLayer);
        
        rightClickOnLayer.setLightWeightPopupEnabled(false);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">

    /** This will change the button's based on the gui stage.
     */
    public void setGUIStage(int guiStage) {        
        // For each button defined, enable/disable it.
        for (Iterator itr = ltButtonInfo.iterator(); itr.hasNext(); ) {
            ButtonInfo button = (ButtonInfo)itr.next();           
            button.setEnabled(guiStage);
        }        
        
        // Remove the layers since there is no documents.
        if (guiStage == FrameEmbroideryDraw.GUISTAGE_EMPTY) 
            layerList.clearAll();
    }
    
    /** This will set the current design being worked on. It will also get the
     * LayerInfo from the design and update the LayerList.
     * @param d  is the design that is currently being worked on. 
     */
    public void setDocument(DesignDocument document) {
        this.document = document;
        this.drawingPad = document.getDesignPanel().getDrawingPad();
        this.design = drawingPad.getDesign();
        validate();
    }
    
    /** This will validate the LayerList to see if anything was changed.
     */
    public void validate() {
        super.validate();
        
        // Update the LayerInfo in the List.
        if (drawingPad != null) {
            layerList.update(drawingPad.getDesign().getLayerInfos());
            layerList.selectLayer(drawingPad.getDesign().getSelectedLayerInfo());
        }
    }
        
    private void checkForTriggerEvent(MouseEvent evt) {
        if (evt.isPopupTrigger()) {            
            LayerInfo l = layerList.getLayerAtClick(evt.getPoint());
            
            // No layer.
            if (l == null)
                return;
           
            // Set the popup.
            lPopupInfo = l;
            
            menuShowThisLayerOnly.setText("Make Layer The Only Visible for [" + l.getName() + "] Layer");
            
            // Cannot turn layer visibility off if it is the only layer visible.
            String onOff = "On";
            menuTurnLayerOnOff.setVisible(true);
            if (l.isVisible()) {
                if (layersVisible() <= 1)
                    menuTurnLayerOnOff.setVisible(false);
                else
                    menuTurnLayerOnOff.setText("Turn Visibility Off for [" + l.getName() + "] Layer");                 
            } else
                menuTurnLayerOnOff.setText("Turn Visibility On for [" + l.getName() + "] Layer"); 
            
            menuChangeColor.setText("Change Color for [" + l.getName() + "] Layer"); 
            
            menuChangeName.setText("Change Name for [" + l.getName() + "] Layer"); 
            
            menuZoomLayer.setText("Zoom on [" + l.getName() + "] Layer");
                    
            menuDeleteLayer.setText("Delete [" + l.getName() + "] Layer"); 
            menuSaveAsRxml.setText("Save [" + l.getName() + "] Layer As RXML");
            menuSaveAsBitMap.setText("Save [" + l.getName() + "] Layer As BITMAP");
            menuSaveAsPEM.setText("Save [" + l.getName() + "] Layer As PEM");
            rightClickOnLayer.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    /** This will get the number of visible.
     * @return the number of visible layers.
     */
    private int layersVisible() {
        LayerInfo []lInfo = drawingPad.getDesign().getLayerInfos();
        int count=0;
        for (int i=0; i < lInfo.length; i++) {
            if (lInfo[i].isVisible())
                count++;
        }
        
        return count;
    }
    
    /** This will create a new layer. If a new layer is created than it will become the current layer.
     * @return true if create a new layer or false didn't create a new layer.
     */
    public boolean newLayer() {
        DialogNewLayer dialog = new DialogNewLayer(parentFrame, true);
        dialog.setVisible(true);
        // Create the new layer.
        if (dialog.isCreateNewLayer()) {
            String layerName = dialog.getLayerName();
            Color layerColor = dialog.getLayerColor();
            design.addLayer(new LayerInfo(layerName, layerColor));
            return true;
        }
        
        return false;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface ActionListener ">
    
    public void actionPerformed(ActionEvent evt) {        
        Object obj = evt.getSource();
        if (obj == btnNewLayer) {
            newLayer();
        } else if (obj == btnDeleteAll) {
            JOptionPane message = new JOptionPane();
            int answer = message.showConfirmDialog(parentFrame, "Are you sure you want to delete all layers?", "Delete All Layers", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                design.deleteAllLayers();
                drawingPad.repaint();
            }
            
        } else if (obj == btnMergeTwoLayers) {
            LayerInfo []array = design.getLayerInfos();
            
            // Array must be greater than equal to 2 since a merge requires atleast 2 layers to merge.
            if (array.length < 2) {
                JOptionPane message = new JOptionPane();
                message.showMessageDialog(parentFrame, "Must have two or more layers before you can merge.", "Merge Two Layers", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            DialogMergeTwoLayers dialog = new DialogMergeTwoLayers(parentFrame, true);
            dialog.setLayerInfo(array);
            dialog.setVisible(true);
            
            // Merge the two layers.
            if (dialog.isMergeLayers()) {
                LayerInfo source = dialog.getSourceLayer();
                LayerInfo destination = dialog.getDestinationLayer();                
                design.mergeLayers(source, destination);
                drawingPad.repaint();
            }
            
        } else if (obj == btnShowAllLayers) {
            design.setAllLayersVisability(true);
            drawingPad.repaint();
            
        } else if (obj == btnMergeAllToMaster) {
            JOptionPane message = new JOptionPane();
            int answer = message.showConfirmDialog(parentFrame, "Are you sure you want to merge all layers to the current layer?", "Merge All Layers", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                design.mergeAll(design.getCurrentLayer());
                drawingPad.repaint();
            }
            
        } else if (obj == menuDeleteLayer) {            
            JOptionPane message = new JOptionPane();
            int answer = message.showConfirmDialog(parentFrame, "Are you sure you want to delete [" + lPopupInfo.getName() + "] layer?", "Delete Layer [" + lPopupInfo.getName() + "]", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                design.deleteLayer(lPopupInfo);
                drawingPad.repaint();
            }
            
        } else if (obj == menuChangeColor) {
            JColorChooser color = new JColorChooser();
            Color c = color.showDialog(parentFrame, "Choose color for layer [" + lPopupInfo.getName() + "]", lPopupInfo.getColor());
            if (c != null) {
                lPopupInfo.setColor(c);
                design.updateLayer(lPopupInfo);
                drawingPad.repaint();
            }

        } else if (obj == menuZoomLayer) {
            document.getDesignPanel().zoomTo(lPopupInfo, 16.0f);
            parentFrame.setZoom(document.getDesignPanel().getZoom());
            
        } else if (obj == menuChangeName) {
            JOptionPane message = new JOptionPane();
            String newLayerName = message.showInputDialog(parentFrame, "Change layer name[" + lPopupInfo.getName() + "] to name:", "Update Layer Name for Layer [" + lPopupInfo.getName() + "]", JOptionPane.QUESTION_MESSAGE);
            if (newLayerName != null) {
                lPopupInfo.setName(newLayerName);
                design.updateLayer(lPopupInfo);
                drawingPad.repaint();
            }
        } else if (obj == menuShowThisLayerOnly) {
            design.setAllLayersVisability(false);
            
            lPopupInfo.setVisible(true);
            design.selectLayer(lPopupInfo);
            design.updateLayer(lPopupInfo);
            drawingPad.repaint();
            
        } else if (obj == menuTurnLayerOnOff) {
            
            if (lPopupInfo.isVisible()) {
                lPopupInfo.setVisible(false);
            } else
                lPopupInfo.setVisible(true);
            
            design.updateLayer(lPopupInfo);
            design.selectVisibleLayer();
            drawingPad.repaint();
            
        } else if (obj == menuSaveAsRxml) {
            document.writeDocument(lPopupInfo, true);
            
        } else if (obj == menuSaveAsBitMap) {
            document.writeBitmap(lPopupInfo);
            
        } else if (obj == menuSaveAsPEM) {
            document.writePEM(lPopupInfo);
        }
        
        validate();
    }
 
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">
    
    public void mouseClicked(MouseEvent e) {
        // If user double clicked, then edit color.
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            LayerInfo l = layerList.getLayerSelected();
            if (l == null)
                return;
            
            Color c = JColorChooser.showDialog(parentFrame, "Choose color for layer [" + l.getName() + "]", l.getColor());
            if (c != null) {
                l.setColor(c);
                design.updateLayer(l);
                drawingPad.repaint();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        checkForTriggerEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
        checkForTriggerEvent(e);
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            LayerInfo l = layerList.getLayerSelected();
            if (l != null) {
                // Select the layer.
                design.selectLayer(l);
                
                // If layer is not visible then turn the visiblilty on.
                if (l.isVisible() == false) {
                    l.setVisible(true);
                    design.updateLayer(l);
                }
                
                // Repaint the drawing pad.
                drawingPad.repaint();
            }            
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    // </editor-fold>
}
