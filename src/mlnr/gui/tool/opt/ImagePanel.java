/*
 * ImagePanel.java
 *
 * Created on September 15, 2006, 11:52 AM
 */

package mlnr.gui.tool.opt;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mlnr.Measurement;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.cpnt.ImagePool;
import mlnr.gui.tool.dlg.DialogGetRotate;
import mlnr.gui.tool.dlg.DialogMoveGetLocation;
import mlnr.gui.tool.dlg.DialogResizeSelection;
import mlnr.util.DefaultExceptionHandler;

/**
 *
 * @author  Robert Molnar II
 */
public class ImagePanel extends javax.swing.JPanel implements ActionListener, MouseListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    private static final int IMAGEPREV_WIDTH = 190;
    private static final int IMAGEPREV_HEIGHT = 190;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    JFrame parentFrame;
    
    DrawingPad drawingPad;
    
    ImagePreviewComponent iPreview = new ImagePreviewComponent();
    
    ImageInfo iInfoForPopup;
    
    DnDListModel listModel = new DnDListModel();
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pop Up Menu ">
    
    private JLabel menuImage = new JLabel();
    private JMenuItem menuMoveUp1 = new JMenuItem("Move Up By 1");
    private JMenuItem menuMoveDown1 = new JMenuItem("Move Down By 1");
    private JMenuItem menuMoveTop = new JMenuItem("Move To Top");
    private JMenuItem menuMoveBottom = new JMenuItem("Move To Bottom");
    private JMenuItem menuDelete = new JMenuItem("Delete");
    private JMenuItem menuMoveTo = new JMenuItem("Move To");
    private JMenuItem menuSize = new JMenuItem("Resize");
    private JMenuItem menuRestore = new JMenuItem("Restore To Original");
    private JMenuItem menuRotate = new JMenuItem("Rotate");
    private JMenuItem menuCenter = new JMenuItem("Center");
    
    /** This is the menu when they right click on a layer */
    private JPopupMenu rightClickOnImage = new JPopupMenu();
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor And Setup ">
    
    /** Creates new form ImagePanel */
    public ImagePanel(JFrame frame) {
        initComponents();
        listModel.clear();
        
        parentFrame = frame;
        
        // Set up the list.
//        jListImages.setModel(defaultListModel);
//        defaultListModel.clear();
        
        // Set up the image preview.
        jPanelImagePreview.add(iPreview);
        jListImages.addListSelectionListener(iPreview);
        jListImages.addMouseListener(this);
        iPreview.addMouseListener(this);
        updateUI();
        
        // Setup the popup menu.
        setupPopupMenu();
        
    }
    
    /** This will setup the popup menu.
     */
    private void setupPopupMenu() {
        menuMoveUp1.addActionListener(this);
        menuMoveDown1.addActionListener(this);
        menuMoveTop.addActionListener(this);
        menuMoveBottom.addActionListener(this);
        menuDelete.addActionListener(this);
        menuMoveTo.addActionListener(this);
        menuSize.addActionListener(this);
        menuRestore.addActionListener(this);
        menuRotate.addActionListener(this);
        menuCenter.addActionListener(this);
        
        rightClickOnImage.add(menuImage);
        rightClickOnImage.add(menuMoveUp1);
        rightClickOnImage.add(menuMoveDown1);
        rightClickOnImage.add(menuMoveTop);
        rightClickOnImage.add(menuMoveBottom);
        rightClickOnImage.addSeparator();
        rightClickOnImage.add(menuMoveTo);
        rightClickOnImage.add(menuCenter);
        rightClickOnImage.add(menuSize);
        rightClickOnImage.add(menuRotate);
        rightClickOnImage.addSeparator();
        rightClickOnImage.add(menuDelete);
        rightClickOnImage.addSeparator();
        rightClickOnImage.add(menuRestore);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Operation Methods ">
    
    /** @return the DataFlavor of the images, used for Drag and Drop transfer.
     */
    public static DataFlavor getDataFlavor() {
        return ImageInfoSelection.imageInfoFlavor;
    }
    
    /** This will set the current drawing pad being used.
     */
    public void setDrawingPad(DrawingPad drawingPad) {
        this.drawingPad = drawingPad;
        validate();
    }
    
    /** This will validate the ImageList to see if anything was changed.
     * @param reloadTransform is true if it should reload and transformed else false.
     */
    public void validate() {
        super.validate();
        
        if (drawingPad == null)
            return;
        
        // Get the selected ImageInfo.
        ImageListInfo ili = (ImageListInfo)jListImages.getSelectedValue();
        int selectedId = -1;
        if (ili != null)
            selectedId = ili.id;
        
        // Update the ImageInfo in the List.
        listModel.clear();
        for (Iterator itr = drawingPad.getImagePool().getImageInfos().iterator(); itr.hasNext(); ) {
            ImageInfo ii = (ImageInfo)itr.next();
            ImageListInfo iListInfo = new ImageListInfo(ii.getName(), ii.getId());
            listModel.addElement(iListInfo);
            if (ii.getId() == selectedId)
                jListImages.setSelectedValue(iListInfo, true);
        }
        
        // Clear the preview image.
        iPreview.clear();
        iPreview.valueChanged(null);
        validateImageInfo();
        iPreview.repaint();
    }
    
    /** This will update the image information, the position, size and rotation.
     */
    public void validateImageInfo() {
        // make sure the user has an image selected.
        int index = jListImages.getSelectedIndex();
        if (index == -1) {
            validateImageInfo(null);
            return;
        }
        
        // Get the selected ImageInfo.
        ImageListInfo iListInfo = (ImageListInfo)jListImages.getSelectedValue();
        if (iListInfo == null) {
            validateImageInfo(null);
            return;
        }
        
        validateImageInfo(drawingPad.getImagePool().getImageInfo(iListInfo.id));
    }
    
    /** This will set the image as selected.
     * @param id is the id of the image to set selected.
     */
    public void setSelected(int id) {
        int size = listModel.getSize();
        for (int i=0; i < size; i++) {
            ImageListInfo ili = (ImageListInfo)listModel.getElementAt(i);
            if (ili.id == id) {
                jListImages.setSelectedIndex(i);
                return;
            }
            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic ">
    
    /** This will update the image information, the position, size and rotation.
     * @param iInfo is the image which contains the information, can be null in that case remove information.
     */
    private void validateImageInfo(ImageInfo iInfo) {
        if (iInfo == null) {
            jLabelPosition.setText("Position:");
            jLabelSize.setText("Size:");
            jLabelRotate.setText("Rotate:");
            return;
        }
        
        jLabelPosition.setText("Position: " + Measurement.convertMeasurement(iInfo.getXPosition(), 2) + " "
                + Measurement.getTextualName() + " x " + Measurement.convertMeasurement(iInfo.getYPosition(), 2) + " mm");
        jLabelSize.setText("Size: " + Measurement.convertMeasurement(iInfo.getXSize(), 2) + " "
                + Measurement.getTextualName() + " x " + Measurement.convertMeasurement(iInfo.getYSize(), 2) + " mm");
        
        float degree = (float)Math.toDegrees(iInfo.getRotate());
        if (iInfo.getRotate() != 0.0f)
            degree = 360 - degree;
        jLabelRotate.setText("Rotation: " + degree + " degree");
    }

    /** This will move the image from the oldIndex to the newIndex. All items between must be shifted.
     * @param iInfo is the image which contains the information of the image.
     * @param offset is the offset to move the image.
     */
    void imageMove(ImageListInfo iInfo, int offset) {
        ImagePool iPool = drawingPad.getImagePool();
        if (offset < 0) {
            // Move image up.
            while (offset < 0) {
                iPool.moveHigher(iInfo.id);
                offset++;
            }
        } else {
            // Move image down.
            while (offset > 0) {
                iPool.moveLower(iInfo.id);
                offset--;
            }
        }
        
        // Repaint to show results.
        validate();
        drawingPad.repaint();
    }
    
    
    /** This will move the image up by 1 in the list of images and thus it will be drawn by 1 up.
     */
    private void imageMoveUp1() {
        drawingPad.getImagePool().moveHigher(iInfoForPopup.getId());
        drawingPad.repaint();
    }
    
    /** This will move the image down by 1 in the list of images and thus it will be drawn by 1 down.
     */
    private void imageMoveDown1() {
        drawingPad.getImagePool().moveLower(iInfoForPopup.getId());
        drawingPad.repaint();
    }
    
    /** This will move the image to the top, which means that it will be drawn first and can be picked first.
     */
    private void imageMoveTop() {
        drawingPad.getImagePool().moveTop(iInfoForPopup.getId());
        drawingPad.repaint();
    }
    
    /** This will move the image to the bottom, which means that it will be drawn last and can be picked last.
     */
    private void imageMoveBottom() {
        drawingPad.getImagePool().moveBottom(iInfoForPopup.getId());
        drawingPad.repaint();
    }
    
    /** This will delete the image.
     */
    private void imageDelete() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(parentFrame, "Are you sure want to delete [" + iInfoForPopup.getName() + "] image?",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            drawingPad.getImagePool().delete(iInfoForPopup.getId());
            drawingPad.repaint();
            repaint();
        }
    }
    
    /** This will move the image.
     */
    private void imageMoveTo() {
        DialogMoveGetLocation dialog = new DialogMoveGetLocation(parentFrame, true, iInfoForPopup.getXPosition(), iInfoForPopup.getYPosition());
        dialog.setVisible(true);
        
        if (dialog.isOk()) {
            iInfoForPopup.setPosition(dialog.getXPosition(), dialog.getYPosition());
            drawingPad.repaint();
        }
    }
    
    /** This will center the image.
     */
    private void imageCenter() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(parentFrame, "Are you sure want to center [" + iInfoForPopup.getName() + "] image?",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            iInfoForPopup.setPosition(drawingPad.getDesign().getWidth() / 2.0f, drawingPad.getDesign().getHeight() / 2.0f);
            drawingPad.repaint();
            repaint();
        }
    }
    
    /** This will resize the image.
     */
    private void imageSize() {
        DialogResizeSelection dialog = new DialogResizeSelection(parentFrame, true);
        dialog.setCurrentSize(iInfoForPopup.getXSize(), iInfoForPopup.getYSize());
        dialog.setVisible(true);
        
        if (dialog.isResize()) {
            iInfoForPopup.setSize(dialog.getWidthInMeasurements(), dialog.getHeightInMeasurements());
            drawingPad.repaint();
        }
    }
    
    /** This will restore the image to its default measurements.
     */
    private void imageRestore() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(parentFrame, "Are you sure want to restore [" + iInfoForPopup.getName() + "] image?",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            drawingPad.getImagePool().restore(iInfoForPopup.getId());
            drawingPad.repaint();
            repaint();
        }
    }
    
    /** This will rotate the image.
     */
    private void imageRotate() {
        DialogGetRotate dialog = new DialogGetRotate(parentFrame, true);
        dialog.setVisible(true);
        
        if (dialog.isOk()) {            
            iInfoForPopup.setRotate(dialog.getRadian() + iInfoForPopup.getRotate());
            drawingPad.repaint();
        }
    }
    
    /** This will show the pop up menu.
     */
    private void checkForTriggerEvent(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            // make sure the user clicked on a cell.
            int index = jListImages.getSelectedIndex();
            if (index == -1)
                return;
            
            popupMenuForImage(evt);
        }
    }
    
    /** This will show the pop up menu for the current image selected.
     *  @param evt is the MouseEvent that triggered the event.
     */
    public void popupMenuForImage(MouseEvent evt) { 
            // Get the selected ImageInfo.
            ImageListInfo iListInfo = (ImageListInfo)jListImages.getSelectedValue();
            if (iListInfo == null)
                return;
            iInfoForPopup = drawingPad.getImagePool().getImageInfo(iListInfo.id);
            
            // Setup the menu items.
            String namePicture = iInfoForPopup.getName();
            if (namePicture.length() > 25)
                namePicture = namePicture.substring(0, 25) + "...";
            menuImage.setBackground(Color.RED);
            menuImage.setText(namePicture);
            
            // show popup menu.
            rightClickOnImage.show(evt.getComponent(), evt.getX(), evt.getY());
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface ActionListener ">
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        
        if (src == menuMoveUp1) {
            imageMoveUp1();
        } else if (src == menuMoveDown1) {
            imageMoveDown1();
        } else if (src == menuMoveTop) {
            imageMoveTop();
        } else if (src == menuMoveBottom) {
            imageMoveBottom();
        } else if (src == menuDelete) {
            imageDelete();
        } else if (src == menuMoveTo) {
            imageMoveTo();
        } else if (src == menuSize) {
            imageSize();
        } else if (src == menuRestore) {
            imageRestore();
        } else if (src == menuRotate) {
            imageRotate();
        } else if (src == menuCenter) {
            imageCenter();
        }
        
        validate();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
        checkForTriggerEvent(e);
    }
    
    public void mouseReleased(MouseEvent e) {
        checkForTriggerEvent(e);
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class ImagePreviewComponent ">
    
    /**
     *
     * @author Robert Molnar II
     */
    class ImagePreviewComponent extends JComponent implements ListSelectionListener {
        private ImageInfo iInfo = null;
        
        /** Creates a new instance of ImagePreviewComponent */
        public ImagePreviewComponent() {
            Dimension sz = new Dimension(IMAGEPREV_WIDTH, IMAGEPREV_HEIGHT);
            setPreferredSize(sz);
        }
        
        public void valueChanged(ListSelectionEvent e) {
            ImageListInfo iListInfo = (ImageListInfo)jListImages.getSelectedValue();
            if (iListInfo == null)
                return;
            
            iInfo = drawingPad.getImagePool().getImageInfo(iListInfo.id);
            
            jLabelPosition.setText("Position: " + Measurement.convertMeasurement(iInfo.getXPosition(), 2) + " "
                    + Measurement.getTextualName() + " x " + Measurement.convertMeasurement(iInfo.getYPosition(), 2) + " mm");
            jLabelSize.setText("Size: " + Measurement.convertMeasurement(iInfo.getXSize(), 2) + " "
                    + Measurement.getTextualName() + " x " + Measurement.convertMeasurement(iInfo.getYSize(), 2) + " mm");
            
            float degree = (float)Math.toDegrees(iInfo.getRotate());
            if (iInfo.getRotate() != 0.0f)
                degree = 360 - degree;
            jLabelRotate.setText("Rotation: " + degree + " degree");
            
            this.repaint();
        }
        
        public void paintComponent(Graphics g) {
            
            // fill the background
//            g.setColor(Color.gray);
//            g.fillRect(0,0,getWidth(),getHeight());
            
            if (iInfo != null) {
                BufferedImage bi = iInfo.getBufferedImage();
                
                // calculate the scaling factor
                int w = bi.getWidth(null);
                int h = bi.getHeight(null);
                int side = Math.max(w,h);
                double scale = 200.0/(double)side;
                w = (int)(scale * (double)w);
                h = (int)(scale * (double)h);
                
                int xPos = (IMAGEPREV_WIDTH - w) / 2;
                int yPos = (IMAGEPREV_HEIGHT - h) / 2;
                
                // draw the image
                g.drawImage(bi,xPos, yPos,w,h,null);
                
            } else {
                // print a message.
                g.setColor(Color.black);
                g.drawString("No image selected.",30,100);
            }
        }
        
        private void clear() {
            iInfo = null;
        }
    }
    
    // </editor-fold>
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jListImages = new JDnDList(listModel);
        jPanelImagePreview = new javax.swing.JPanel();
        jLabelPosition = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jLabelRotate = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Image Details"));
        jScrollPane1.setViewportView(jListImages);

        jPanelImagePreview.setLayout(new java.awt.BorderLayout());

        jLabelPosition.setText("Position:");

        jLabelSize.setText("Size:");

        jLabelRotate.setText("Rotation:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
            .add(jPanelImagePreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
            .add(jLabelPosition, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
            .add(jLabelSize, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
            .add(jLabelRotate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelImagePreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 192, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelPosition)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelSize)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabelRotate))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelPosition;
    private javax.swing.JLabel jLabelRotate;
    private javax.swing.JLabel jLabelSize;
    private JDnDList jListImages;
    private javax.swing.JPanel jPanelImagePreview;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    // <editor-fold defaultstate="collapsed" desc=" class JDnDList ">
    
    /** This is a first attempt hack at drag and drop with a tutorial.
     * @author Robert Molnar II
     */
    class JDnDList extends JList implements DragSourceListener, DragGestureListener, DropTargetListener {
        /** This is the source of the drag which is this class. */
        DragSource dragSource;
        /** This is the target of the drag which is this class. */
        DropTarget dropTarget;
        /** This is the index of the dragged item. */
        int draggedIndex;
        /** True if dragging the items. */
        boolean dragging;
        int overIndex;
        
        /** Creates a new instance of JDnDList */
        public JDnDList(DnDListModel model) {
            super(model);
            
            // Configure ourselves to be a drag source
            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_MOVE, this);
            
            // Configure ourselves to be a drop target
            dropTarget = new DropTarget( this, this );
        }
        
        /** Listener for the drag gesture.
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            // This is the index which wil be dragged.
            draggedIndex = getSelectedIndex();
            
            // The object of the selected index.
            Object obj = getSelectedValue();
            if (obj == null)
                return;
            
            // The objects in the List are ImageListInfo. This will create a transferable for the object.
            Transferable transfer = ((ImageListInfo)obj).getTransferable();
            
            // Start dragging the object
            this.dragging = true;
            dragSource.startDrag( dge, DragSource.DefaultMoveDrop, transfer, this );
        }
        
        public void dragDropEnd(DragSourceDropEvent dsde) {
            this.dragging = false;
        }
        public void dragExit(DropTargetEvent dte) {
            this.overIndex = -1;
        }
        public void dragEnter(DropTargetDragEvent dtde) {
            this.overIndex = this.locationToIndex( dtde.getLocation() );
            this.setSelectedIndex( this.overIndex );
        }
        public void dragOver(DropTargetDragEvent dtde) {
            // See who we are over...
            int overIndex = this.locationToIndex( dtde.getLocation() );
            if( overIndex != -1 && overIndex != this.overIndex ) {
                // If the value has changed from what we were previously over
                // then change the selected object to the one we are over; this
                // is a visual representation that this is where the drop will occur
                this.overIndex = overIndex;
                this.setSelectedIndex( this.overIndex );
            }
        }
        
        public void drop(DropTargetDropEvent dtde) {
            // Must be from this file.
            Transferable transferable = dtde.getTransferable();
            if( transferable.isDataFlavorSupported( ImageInfoSelection.imageInfoFlavor) == false) {
                dtde.rejectDrop();
                return;
            }
            
            // Accept the drop since it came from this file.
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            
            // Find out where the item was dropped at.
            int newIndex = this.locationToIndex(dtde.getLocation());
            
            // Now move the item to the new index.
            try {
                imageMove((ImageListInfo)transferable.getTransferData(ImageInfoSelection.imageInfoFlavor), newIndex - draggedIndex);
            } catch (Exception e) {
                DefaultExceptionHandler.printExceptionToLog(e, "Unable to drag image.");
            }
            
            // Reset the over index.
            this.overIndex = -1;
            
            // Drop is complete.
            dtde.getDropTargetContext().dropComplete( true );
        }
        
        public void dragEnter(DragSourceDragEvent dsde) {
        }
        
        public void dragOver(DragSourceDragEvent dsde) {
        }
        
        public void dropActionChanged(DragSourceDragEvent dsde) {
        }
        
        public void dragExit(DragSourceEvent dse) {
        }
        
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" class DnDListModel ">
    
    /**
     *
     * @author Robert Molnar II
     */
    class DnDListModel extends AbstractListModel {
        ArrayList items = new ArrayList();
        
        public void clear() {
            int index1 = items.size()-1;
            items.clear();
            if (index1 >= 0) {
                fireIntervalRemoved(this, 0, index1);
            }
        }
        
        public void addElement(Object obj) {
            items.add(obj);
            
            // Tell the list to update itself
            this.fireContentsChanged( this, 0, this.items.size() - 1 );
        }
        
        
        /**
         * Inserts a collection of items before the specified index
         */
        public void insertItems( int index, Collection objects ) {
            // Handle the case where the items are being added to the end of the list
            if( index == -1 ) {
                // Add the items
                for( Iterator i = objects.iterator(); i.hasNext(); ) {
                    Object item = ( Object )i.next();
                    items.add( item );
                }
            } else {
                // Insert the items
                for( Iterator i = objects.iterator(); i.hasNext(); ) {
                    Object item = ( Object )i.next();
                    items.add( index++, item );
                }
            }
            
            // Tell the list to update itself
            this.fireContentsChanged( this, 0, this.items.size() - 1 );
        }
        
        public void itemsMoved( int newIndex, int[] indicies ) {
            // Copy the objects to a temporary ArrayList
            ArrayList objects = new ArrayList();
            for( int i=0; i<indicies.length; i++ ) {
                objects.add( this.items.get( indicies[ i ] ) );
            }
            
            // Delete the objects from the list
            for( int i=indicies.length-1; i>=0; i-- ) {
                this.items.remove( indicies[ i ] );
            }
            
            // Insert the items at the new location
            insertItems( newIndex, objects );
        }
        
        public int getSize() {
            return items.size();
        }
        
        public Object getElementAt(int index) {
            return items.get(index);
        }
    }
    // </editor-fold>
    
}

/** Class used for the Image List so that the name and id can be stored but only the name show up.
 */
class ImageListInfo {
    String name;
    int id;
    
    ImageListInfo(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    public String toString() {
        return name;
    }
    
    public Transferable getTransferable() {
        return new ImageInfoSelection(this);
    }
    
}

// <editor-fold defaultstate="collapsed" desc=" class ImageInfoSelection ">

class ImageInfoSelection implements Transferable {
    
    private static final int IMAGEINFO = 0;
    
    private ImageListInfo iInfo;
    
    static final DataFlavor imageInfoFlavor = new DataFlavor(ImageListInfo.class, "ImageInfo");
    
    
    private static final DataFlavor[] flavors = {imageInfoFlavor};
    
    ImageInfoSelection(ImageListInfo iInfo) {
        this.iInfo = iInfo;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(imageInfoFlavor);
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor != imageInfoFlavor)
            throw new UnsupportedFlavorException(flavor);
        return (Object)iInfo;
    }
}

// </editor-fold>
