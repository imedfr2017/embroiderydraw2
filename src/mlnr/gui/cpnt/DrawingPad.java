/*
 * DrawingPad.java
 *
 * Created on July 27, 2006, 11:18 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mlnr.draw.TransformDesign;
import org.w3c.dom.*;
import mlnr.EmbroideryDraw;
import mlnr.Measurement;
import mlnr.draw.DrawingDesign;
import mlnr.draw.LayerInfo;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.dlg.*;
import mlnr.gui.tool.AbstractTool;
import mlnr.type.FPointType;
import mlnr.util.XmlUtil;

/** This is the class that draws the design onto.
 * @author Robert Molnar II
 */
public class DrawingPad extends JComponent {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the stage for color. */
    public static final int STAGE_COLOR = 1;
    
    /** This is the stage for vector. */
    public static final int STAGE_VECTOR = 2;
    
    /** This is a pen size of zero which will always draw 1 pixel size. */
    private static final BasicStroke PENSIZE1 = new BasicStroke(0.0f);
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields (Settings) ">
    
    /** True if the images are to be shown, else false means that they will not be drawn.   */
    static private boolean imagesVisable = true;
    
    /** True if the images are to be shown as lightening.  */
    static private boolean imagesLighten = false;
    
    /** True if to snap-to-grid. */
    static private boolean snapToGrid = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the interface used to scale the drawing pad correctly. */
    private InterfaceScale iScale;
    
    /** This is the class that is used to store the design information and render it. */
    private DrawingDesign design;
    
    /** This is the x offset into the design (measurement wise size). */
    private float xOffsetIntoDesign = 0f;
    
    /** This is the y offset into the design (measurement wise size). */
    private float yOffsetIntoDesign = 0f;
    
    /** This is the x offset of the design relative in the drawing component (pixel wise size). */
    private float xOffsetOfDesign = 0f;
    
    /** This is the y offset of the design relative in the drawing component (pixel wise size). */
    private float yOffsetOfDesign = 0f;
    
    /** This is the current position the mouse is at in measurements. */
    private FPointType fptMousePos = new FPointType();
    
    /** This is the line class used to draw the grid. */
    private Line2D.Float fLine = new Line2D.Float();
    
    /** This is the stroke for drawing the design. */
    private BasicStroke strokeDesign;
    
    /** Stroke used for drawing the selected items. */
    private BasicStroke strokeDrawing = new BasicStroke(0.0f);
    
    /** This is the currently selected items. null: no items. */
    TransformDesign tDesign = null;
    
    /** This holds all the images for this DrawingPad. */
    ImagePool imagePool = new ImagePool(this);
    
    /** This is the current stage which Embroidery Draw is in. */
    int stage = STAGE_VECTOR;
    
    /** This is used to draw the grid. */
    private DrawGrid drawGrid = new DrawGrid();
    
    /** This is true if the tool needs to erase before drawing. */ 
    private boolean drawingToolNeedErase = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor and Setup Methods ">
    
    /** Creates a new instance of DrawingPad */
    public DrawingPad(InterfaceScale iScale) {
        // super(true);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        setOpaque(true);
        this.iScale = iScale;
    }
    
    /** This will create the design and setup the drawing pad.
     * @param InterfaceFrameOperation is the interface used to operation the main frame.
     * @param designWidth is the width of the new design.
     * @param designHeight is the height of the new design.
     */
    public void createDesign(InterfaceFrameOperation iFrameOperator, float designWidth, float designHeight) {
        design = new DrawingDesign(iFrameOperator, true);
        design.resizeDesign(new FPointType(designWidth, designHeight));
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Serialize Support ">
    
    /** This will load the drawing from a file.
     * @param root is the root element, should be "rxml".
     * @param iFrameOperator is the interface used to operation the main frame.
     * @return true if the file opened ok, else false errored out when trying to open the file.
     */
    public void openRXML(Element root, InterfaceFrameOperation iFrameOperator) throws Exception {
        if ("rxml".equals(root.getNodeName()) == false)
            throw new Exception("Missing root element [rxml]. Not rxml file.");
        
        // Each version is different therefore they required different loading techniques.
        String versionNumber = XmlUtil.getAttributeString(root, "v");
        if ("1.0".equals(versionNumber))
            openRXMLVersion10(root, iFrameOperator);
        else if ("1.1".equals(versionNumber))
            openRXMLVersion11(root, iFrameOperator);
        else if ("2.0".equals(versionNumber))
            openRXMLVersion20(root, iFrameOperator);
        else {
            new JOptionPane().showMessageDialog(this, "Unknown version number " + versionNumber
                    + ". If number is greater than 2.0 then you will need to upgrade your software to load in this file.",
                    "Error Message", JOptionPane.ERROR_MESSAGE);
            throw new Exception("Unknown version number: " + versionNumber);
        }
    }
    
    /** This will open version 1.0 rxml file. Coordinates are in integers and at a resolution of 20, therefore all coordinates
     * need to be converted to floating point numbers by dividing by 20.
     * @param iFrameOperator is the interface used to operation the main frame.
     * @param root is the root element of the rxml file.
     */
    private void openRXMLVersion10(Element root, InterfaceFrameOperation iFrameOperator) throws Exception {
        // Load the design in.
        design = DrawingDesign.loadVersion10(root, iFrameOperator);
    }
    
    /** This will open version 1.1 rxml file. The difference between version 1.0 and 1.1 is the image tag is
     * added to version 1.1. Coordinates are in integers and at a resolution of 20, therefore all coordinates
     * need to be converted to floating point numbers by dividing by 20.
     * @param iFrameOperator is the interface used to operation the main frame.
     * @param root is the root element of the rxml file.
     */
    private void openRXMLVersion11(Element root, InterfaceFrameOperation iFrameOperator) throws Exception {
        // Load the design in.
        design = DrawingDesign.loadVersion10(root, iFrameOperator);
        
        // Get the image tag and load it in.
        Element eImage = XmlUtil.getElementByTagName(root, "image");
        imagePool.loadVersion11(eImage);
    }
    
    /** This will open version 2.0 rxml file.
     * @param iFrameOperator is the interface used to operation the main frame.
     * @param root is the root element of the rxml file.
     */
    private void openRXMLVersion20(Element root, InterfaceFrameOperation iFrameOperator) throws Exception {
        // Get the stage element.
        Element eStage = XmlUtil.getElementByTagName(root, "stage");
        setStage(XmlUtil.getAttributeInteger(eStage, "value"));
        
        // Get the design element and load it.
        Element eDesign = XmlUtil.getElementByTagName(root, "design");
        design = DrawingDesign.loadVersion20(eDesign, iFrameOperator);
        
        // Get the imagePool element and load it.
        Element eImagePool = XmlUtil.getElementByTagName(root, "imagePool");
        imagePool.loadVersion20(eImagePool);
    }
    
    /** This will save the drawing into a file.
     * @param out is the file to write to.
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     */
    public void write(PrintWriter out, LayerInfo lInfo) throws Exception {
        design.write(lInfo, out);
        imagePool.write(out);
    }
    
    /** This will save the drawing as a BITMAP. It is always a "Save As".
     * @param g2d is the graphics to draw into.
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     * @param changeColor is true if it should change color for the layers. If it is false then no general paths are filled in.
     * @param fillColorOnly is true if only the fill should be drawn.
     */
    public void writeBitmap(Graphics2D g2d, LayerInfo lInfo, boolean changeColor, boolean fillColorOnly) throws Exception {
        design.drawAllBitmap(g2d, lInfo, changeColor, fillColorOnly);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Stage Methods ">
    
    /** @return the stage which this drawing is currently being edited in.
     */
    public int getStage() {
        return stage;
    }
    
    /** This will set the stage which drawing is currently being edited in.
     */
    public void setStage(int stage) {
        if (getDesign() == null)
            return;
        
        if (stage == STAGE_COLOR)
            getDesign().setStateToColorFill();
        else if (stage == STAGE_VECTOR)
            getDesign().setStateToVector();
        this.stage = stage;
    }
    
    // </editor-fold>
    
    public DrawingDesign getDesign() {
        return design;
    }
    
    /** @return The bounds of the design by using the measurements of the LayerPool (the bounds of all lines, beziers, and curves).
     */
    public Rectangle2D.Float getDesignBounds2D() {
        return design.getBounds2D();
    }
    
    /** @return a list of Colors used in this design. This will include colors from the general path pool if it exists.
     */
    public LinkedList getDesignColors() {
        return design.getColors();
    }
    
    /** @return the mouse position in measurements size. This will create a new FPointType everytime it
     * is called.
     */
    public FPointType getMousePositionMeasurement() {
        return new FPointType(fptMousePos);
    }
    
    /** This will get the transformed Graphics2D which can then be used
     * to draw into the DrawingPad.
     */
    public Graphics2D getTransformedGraphics() {
        Graphics2D g2D = (Graphics2D)getGraphics();
        transformGraphics(g2D);
        return g2D;
    }
    
    /** This will get the stroke for drawing the design.
     * @return the stroke for drawing the design.
     */
    public Stroke getDesignStroke() {
        return strokeDesign;
    }
    
    /** @param snap is the value to set the snap-to-grid setting.
     */
    public static void setSnapToGrid(boolean snap) {
        snapToGrid = snap;
    }
    
    /** @return true if snap-to-grid is turned on else false is not turned on.
     */
    public static boolean isSnapToGrid() {
        return snapToGrid;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Select Item Methods ">
    
    /** This will see if there are any selected items in this DrawingPad.
     * @return true: if there are any selected items in this DrawingPad,
     * false: There does not exist any selected items in this DrawingPad (do not call getSelectedItems).
     */
    public boolean isSelectedItems() {
        if (tDesign == null)
            return false;
        return true;
    }
    
    /** This will get the selected items which have been selected. Make sure to call 'isSelectedItems()' before
     * getting the TransformDesign.
     * @return the TransformDesign for the selected items.
     * @throws IllegalStateException No selected items.
     */
    public TransformDesign getSelectedItems() {
        if (tDesign == null)
            throw new IllegalStateException("No selected items.");
        return tDesign;
    }
    
    /** This will set the selected item (TransformDesign). Since the DrawingDesign is not modified when
     * selecting items from it, this function could be safely called to override the current TrasformDesign.
     * @param tDesign is the TransformDesign which contain the selected items. Make sure it was created
     * from the DrawingDesign.
     */
    public void setSelectedItems(TransformDesign tDesign) {
        this.tDesign = tDesign;
    }
    
    /** This will finalize the add point operation.
     */
    public void finalizeAddPoint() {
        if (tDesign == null || tDesign.hasMoved() == false) {
            design.deselectAll();
            return;
        }
        
        design.addPointFinalize(tDesign);
        design.deselectAll();
        
        this.tDesign = null;
    }
    
    /** This will finalize the movement of the selected items into this DrawingPad's DrawingDesign. It
     * is important to make sure the TransformDesign is pointing to the current DrawingPad's DrawingDesign.
     * It will also deselect all lines and vertices and make them visible.
     */
    public void finalizeSelectedItems() {
        if (tDesign == null || tDesign.hasMoved() == false) {
            design.deselectAll();
            this.tDesign = null;
            return;
        }
        
        if (tDesign.isCompletelyAttached())
            design.addSelect(tDesign);
        else
            design.add(tDesign, false);
        design.deselectAll();
        
        this.tDesign = null;
    }
    
    /** This will add the DesignTransform to this DrawingPad.
     * @param dTransform is the DesignTransform to add this DrawingPad. If there is already one
     * it will add it to that one.
     */
//    public void addSelectedItems(DesignTransform dTransform) {
//        if (this.dTransform == null)
//            this.dTransform = dTransform;
//        else
//            this.dTransform.add(dTransform);
//    }
    
    /** This will draw the selected items in the DrawingPad.
     * @param g2d is the graphics context.
     */
    public void drawSelectedItems(Graphics2D g2d) {
        if (tDesign == null)
            return;
        
        g2d.setStroke(strokeDrawing);
        g2d.setColor(Color.RED);
        g2d.setXORMode(Color.WHITE);
        tDesign.draw(g2d, drawingToolNeedErase);
        drawingToolNeedErase = true;
    }
    
    /** This is used to draw into a bitmap.
     * @param g2D is the graphics class.
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     * @param changeColor is true if it should change color for the layers.
     * @param fillColorOnly is true if only the fill should be drawn.
     */
    public void drawAllBitmap(Graphics2D g2d, LayerInfo lInfo, boolean changeColor, boolean fillColorOnly) {
        g2d.setStroke(strokeDrawing);
        design.drawAllBitmap(g2d, lInfo, changeColor, fillColorOnly);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Draw Methods ">
    
    /** This is only called when the window goes from not being shown to being
     * shown or if the window is partially shown then more is shown.
     */
    public void paint(Graphics g) {
        Rectangle r = g.getClipBounds();
        g.clearRect(0, 0, (int)r.getWidth(), (int)r.getHeight());
        Graphics2D g2D = (Graphics2D)g;
        transformGraphics(g2D);
        
        // Draw the images.
        if (imagesVisable)
            imagePool.draw(g2D);
        
        // Draw the grid.
        if (Measurement.isGridVisible())
            drawGrid.drawGrid(g2D);
        
        // Set the pen size.
        g2D.setStroke(strokeDesign);
        
        // Draw the design.
        design.draw(g2D);
        
        // Invoke the paint tools at a later point.
        Runnable doPaintTools = new Runnable() {
            public void run() {
                drawingToolNeedErase = false;
                paintTool();
            }
        };
        SwingUtilities.invokeLater(doPaintTools);
    }
    
    
    public void paintTool() {
        Graphics2D g2D = getTransformedGraphics();
        
        // Draw the tool.
        AbstractTool abTool = EmbroideryDraw.getEmbroideryDraw().getTool();
        if (abTool != null)
            abTool.onDrawTool(g2D);
        
        // This will draw the selected items.
         drawSelectedItems(g2D);
    }
    
    /** This will transform the graphics.
     */
    private void transformGraphics(Graphics2D g2D) {
        g2D.translate((double)xOffsetOfDesign, (double)yOffsetOfDesign);
        g2D.scale((double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement(), (double)iScale.getMeasurementScale() * Measurement.getPixelsPerMeasurement());
        g2D.translate((double)-xOffsetIntoDesign, (double)-yOffsetIntoDesign);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Get Design Offset Methods ">
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getXOffsetIntoDesign() {
        return xOffsetIntoDesign;
    }
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getYOffsetIntoDesign() {
        return yOffsetIntoDesign;
    }
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getXOffsetOfDesign() {
        return xOffsetOfDesign;
        
    }
    
    /** @return the offset of the design in the drawing pad in pixels.  */
    public float getYOffsetOfDesign() {
        return yOffsetOfDesign;
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Zoom and Position Changing Methods ">
    
    /** This will zoom in on the zoom area.
     * @param fZoomArea is the area to zoom in on at in Measurements.
     * @param maxZoomPercentage is the maximum percentage to zoom in on the zoom area. If the area to zoom in
     * is so small that it would require a greater than maxZoomPercentage than it will limit the zoom to the
     * maxZoomPercentage.
     * @return the new zoom percentage.
     */
    public float zoomTo(Rectangle2D.Float fZoomArea, float maxZoomPercentage) {
        // Get the drawing pad in measurements.
        float drawingPadWidthMeasurements = (float)getWidth() / Measurement.getPixelsPerMeasurement();
        float drawingPadHeightMeasurements = (float)getHeight() / Measurement.getPixelsPerMeasurement();
        
        // Get the needed zoom to percentage.
        float needZoomToWidthPercentage = drawingPadWidthMeasurements / (float)fZoomArea.getWidth();
        float needZoomToHeightPercentage = drawingPadHeightMeasurements / (float)fZoomArea.getHeight();
        
        // Get the zoom to percentage to zoom to.
        float zoomPercentage = Math.min(needZoomToHeightPercentage, needZoomToWidthPercentage);
        zoomPercentage = Math.min(zoomPercentage, maxZoomPercentage);
        
        // Center point of the fZoomArea.
        FPointType fptCenter = new FPointType();
        fptCenter.x = fZoomArea.x + fZoomArea.width / 2.0f;
        fptCenter.y = fZoomArea.y + fZoomArea.height / 2.0f;
        
        // Go zooming!!
        zoomTo(zoomPercentage, fptCenter);
        return zoomPercentage;
    }
    
    /** This will update the offset and position of the drawing pad's design. This must be called BEFORE
     * DesignPanel updates it's scale measurement since some functions reley on the DesignPanel's current
     * scaling.
     * @param newPercentage 100% = 1.0f, must be as a percentage. This is the new percentage to change to.
     */
    public void zoomTo(float newPercentage) {
        updateOffset(newPercentage, calculateCenter());
        zoomStroke(newPercentage);
    }
    
    /** This will update the offset and position of the drawing pad's design. This must be called BEFORE
     * DesignPanel updates it's scale measurement since some functions reley on the DesignPanel's current
     * scaling.
     * @param newPercentage 100% = 1.0f, must be as a percentage. This is the new percentage to change to.
     * @param fCenter is where the screen should center on the design in measurements.
     */
    public void zoomTo(float newPercentage, FPointType fCenter) {
        updateOffset(newPercentage, fCenter);
        zoomStroke(newPercentage);
    }
    
    /** This will set the x offset into the design.
     * @param offset is the new offset for the x direction into the design (measurement wise size).
     */
    public void setXOffsetIntoDesign(float offset) {
        xOffsetIntoDesign = offset;
    }
    
    /** This will set the y offset into the design.
     * @param offset is the new offset for the y direction into the design (measurement wise size).
     */
    public void setYOffsetIntoDesign(float offset) {
        yOffsetIntoDesign = offset;
    }
    
    /** This will update the offsets.
     * @param newPercentage is the new scale for the drawing pad.
     * @param fCenter is where the screen should center on the design in measurements.
     */
    private void updateOffset(float newPercentage, FPointType fCenter) {
        // In measurements.
        float designWidth = design.getWidth();
        float designHeight = design.getHeight();
        
        // In measurements.
        float drawingPadWidth = (float)getWidth() / (Measurement.getPixelsPerMeasurement() * newPercentage);
        float drawingPadHeight = (float)getHeight() / (Measurement.getPixelsPerMeasurement() * newPercentage);
        
        // Get the offsets.
        if (drawingPadWidth > designWidth) {
            // Zero since the entire design will be shown.
            xOffsetIntoDesign = 0f;
            xOffsetOfDesign = ((drawingPadWidth - designWidth) / 2f) * (Measurement.getPixelsPerMeasurement() * newPercentage);
        } else {
            // Zero since part of the design will be shown (all of the drawing pad will be used up).
            xOffsetOfDesign = 0f;
            xOffsetIntoDesign = fCenter.x - drawingPadWidth / 2;
        }
        
        if (drawingPadHeight > designHeight) {
            // Zero since the entire design will be shown.
            yOffsetIntoDesign = 0f;
            yOffsetOfDesign = ((drawingPadHeight - designHeight) / 2f) * (Measurement.getPixelsPerMeasurement() * newPercentage);
        } else {
            // Zero since part of the design will be shown (all of the drawing pad will be used up).
            yOffsetOfDesign = 0f;
            yOffsetIntoDesign = fCenter.y - drawingPadHeight / 2;
        }
    }
    
    /** This will calculate the center of the drawing pad in measurements of the design.
     * @return the center the drawing pad is looking at (the center of the screen) in the design.
     */
    private FPointType calculateCenter() {
        FPointType fCenter = new FPointType();
        
        // In measurements.
        float designWidth = design.getWidth();
        float designHeight = design.getHeight();
        
        float drawingPadWidth = (float)getWidth() / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale());
        float drawingPadHeight = (float)getHeight() / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale());
        
        // Get the center for the width.
        if (drawingPadWidth > designWidth)
            fCenter.x = designWidth / 2f;
        else
            fCenter.x = drawingPadWidth / 2f + xOffsetIntoDesign;
        
        // Get the center for the height.
        if (drawingPadHeight > designHeight)
            fCenter.y = designHeight / 2f;
        else
            fCenter.y = drawingPadHeight / 2f + yOffsetIntoDesign;
        
        return fCenter;
    }
    
    /** This will zoom the stroke for the drawing the design.
     */
    private void zoomStroke(float newPercentage) {
        // Get the pen size in pixels.
        int penSize = Measurement.getDesignPenSize();
        
        // Set to zero so that it will use 1 pixel width for the drawing.
        // Fastest drawing.
        if (penSize == 1) {
            strokeDesign = PENSIZE1;
            return;
        }
        
        // Scale the pen size so that it will draw in the number of pixels.
        float scaledPenSize = (float)penSize / (Measurement.getPixelsPerMeasurement() * newPercentage);
        
        // Now set the scaled stroke for drawing the design.
        strokeDesign = new BasicStroke(scaledPenSize);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overriden Methods ">
    
    protected void processMouseEvent(MouseEvent e) {
        Point pt = e.getPoint();
        fptMousePos.x = ((float)(pt.x - xOffsetOfDesign)) / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale()) + xOffsetIntoDesign;
        fptMousePos.y = ((float)(pt.y - yOffsetOfDesign)) / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale()) + yOffsetIntoDesign;
        
        super.processMouseEvent(e);
    }
    
    protected void processMouseMotionEvent(MouseEvent e) {
        Point pt = e.getPoint();
        fptMousePos.x = ((float)(pt.x - xOffsetOfDesign)) / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale()) + xOffsetIntoDesign;
        fptMousePos.y = ((float)(pt.y - yOffsetOfDesign)) / (Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale()) + yOffsetIntoDesign;
        
        super.processMouseMotionEvent(e);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Image Methods ">
    
    /** This will get the ImagePool.
     * @return the ImagePool for this DrawingPad. Contains the image data.
     */
    public ImagePool getImagePool() {
        return imagePool;
    }
    
    /** This will load an image into this DrawingPad.
     */
    public void loadImage() {
        // Get the file name filters.
        FileNameFilter imageSupportFilter = new FileNameFilter(".jpg", "JPG");
        imageSupportFilter.addFilterExtension(".bmp", "BITMAP");
        imageSupportFilter.addFilterExtension(".gif", "GIF");
        imageSupportFilter.addFilterExtension(".png", "PNG");
        imageSupportFilter.addFilterExtension(".tiff", "TIFF");
        imageSupportFilter.addFilterExtension(".tif", "TIF");
        
        DialogFileChooser dfChooser = new DialogFileChooser("ImageLoader", imageSupportFilter, "Load Image");
        if (dfChooser.showLoadDialog(this, null, new ImagePreview())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            imagePool.loadImage(dfChooser.getFile());
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            repaint();
        }
    }
    
    /** This will restore all images to their inital size.
     */
    public void restoreAllImages() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(this, "Are you sure want to restore all images?",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            imagePool.restoreAll();
            repaint();
        }
    }
    
    /** This will toggle images on or off.
     */
    public static void toggleImagesOnOff() {
        imagesVisable = !imagesVisable;
    }
    
    /** This will toggle lightening on or off.
     */
    public static void toggleImagesLighteningOnOff() {
        imagesLighten = !imagesLighten;
    }
    
    /** This will reload the images and transform them.
     */
    public void reloadImagesAndTransform() {
        imagePool.reloadAllAndTransform();
        repaint();
    }
    
    /** This will remove all images.
     */
    public void removeAllImages() {
        JOptionPane message = new JOptionPane();
        if (message.showConfirmDialog(this, "Are you sure want to remove all images?",
                "Embroidery Draw", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            imagePool.removeAll();
            repaint();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Image (Settings) Methods ">
    
    /** @return true if the images should be lightened.
     */
    public static boolean isImageLighten() {
        return imagesLighten;
    }
    
    /** @return true if the images should be on else false not on.
     */
    public static boolean isImagesOnOff() {
        return imagesVisable;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class DrawGrid ">
    
    /** This will draw the grid.
     */
    class DrawGridImproved {
        
        // <editor-fold defaultstate="collapsed" desc=" Fields ">
        
        private float gridSize;
        private float smallGridSize;
        private float designWidth;
        private float designHeight;
        private Color oldColor;
        private Color gridColor;
        private Color gridFineColor;
        private float xStart;
        private float yStart;
        private boolean useFineGrid;
        private float smallLineCount;
        private float xViewEnd;
        private float yViewEnd;
        private float xViewStart;
        private float yViewStart;
        private GeneralPath [][]gpGridPath = new GeneralPath[3][3];
                
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Constructor ">
        
        DrawGridImproved() {
            createGeneralPath();
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
        
        /** Draw the grid.
         */
        public final void drawGrid(Graphics2D g2D) {
            g2D.setStroke(new BasicStroke(0.0f));

            // This is the area that needs to be updated. It is in Measurements not Pixels.
            Rectangle clipBounds = g2D.getClipBounds();

            // Setup the variables to draw.
            setupColor(g2D);
            for (int i=0; i < gpGridPath.length; i++)
                for (int j=0; j < gpGridPath[i].length; j++) {
                    if (gpGridPath[i][j].intersects(clipBounds))
                        g2D.draw(gpGridPath[i][j]);
                }
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
        
        private void createGeneralPath() {    
            int count = 33;
            for (int i=0; i < gpGridPath.length; i++) {
                for (int j=0; j < gpGridPath[i].length; j++) {
                    gpGridPath[i][j] = new GeneralPath();
                    
                    for (int x=0; x < count; x++) {
                        gpGridPath[i][j].moveTo((float)x + j * count, i * count);
                        gpGridPath[i][j].lineTo((float)x + j * count, (i+1) * count);
                    }
                    for (int y=0; y < count; y++) {
                        gpGridPath[i][j].moveTo(j * count, (float)y + i * count);
                        gpGridPath[i][j].lineTo((j+1) * count, (float)y + i * count);
                    }
                }
            }
        }
        
        /** This will setup the color for the gird.
         */
        private final void setupColor(Graphics2D g2D) {
            oldColor = g2D.getColor();
            gridColor = Measurement.getGridColor();
            gridFineColor = Measurement.getFineGridColor();
        }
        
        /** This will setup the grid dimensions.
         */
        private final void setupDimensions() {
            // Need to calculate the starting upper left grid starting point and the count of grid lines.
            gridSize = Measurement.getGridSize();
            smallGridSize = Measurement.getFineGridSize();
            designWidth = design.getWidth();
            designHeight = design.getHeight();
        }
        
        /** This will calculate the bounds for drawing the grid.
         */
        private final void calculateBounds(Graphics2D g2D) {
            // This is the area that needs to be updated. It is in Measurements not Pixels.
            Rectangle clipBounds = g2D.getClipBounds();
            
            // Get the upper left grid point.
            xStart = (float)Math.floor(clipBounds.getX() / gridSize) * gridSize;
            yStart = (float)Math.floor(clipBounds.getY() / gridSize) * gridSize;
            if (xStart < 0.0f)
                xStart = 0.0f;
            if (clipBounds.getX() > designWidth)
                return; // Don't draw grid, outside of drawing pad.
            if (yStart < 0.0f)
                yStart = 0.0f;
            if (clipBounds.getY() > designHeight)
                return; // Don't draw grid, outside of drawing pad.

            // Get the upper right position of the view screen for this component.
            xViewStart = (float)clipBounds.getX();
            yViewStart = (float)clipBounds.getY();
            if (xViewStart < 0.0f)
                xViewStart = 0.0f;
            if (yViewStart < 0.0f)
                yViewStart = 0.0f;
            
            // Get the lower right position of the view screen for this component.
            xViewEnd = (float)(clipBounds.getX() + clipBounds.getWidth());
            yViewEnd = (float)(clipBounds.getY() + clipBounds.getHeight());
            
            if (xViewEnd > designWidth)
                xViewEnd = designWidth;
            if (yViewEnd > designHeight)
                yViewEnd = designHeight;
            
            // Small lines count per grid size.
            useFineGrid = true;
            smallLineCount = (int)(Measurement.getGridSize() / Measurement.getFineGridSize()) - 1;
            if (Measurement.getFineGridSize() * Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale() < 5)
                useFineGrid = false;
        }
        
        /** This will draw all the columns.
         */
        private final void drawColumns(Graphics2D g2D) {
            // Draw the column lines from xStart to the end of the design. Stop when first line past design width.
            float xDraw = xStart; // This is the place where the column is drawn at.
            float columnCount = 0.0f;
            while (true) { // Each loop produces a column drawn.
                // Draw the fine grid lines between column drawings.
                if (useFineGrid) {
                    g2D.setColor(gridFineColor);
                    for (float i=1.0f; i <= smallLineCount; i += 1.0f) {
                        xDraw = xStart + (smallGridSize * i) + (columnCount * gridSize);
                        // No more columns to draw.
                        if (xDraw >= designWidth)
                            return;
                        
                        // Draw the column.
                        fLine.setLine(xDraw, yViewStart, xDraw, yViewEnd);
                        g2D.draw(fLine);
                    }
                }
                
                // Next column (Note, it is intentional to not draw the first column).
                columnCount += 1.0f;
                
                // No more columns to draw.
                xDraw = xStart + (columnCount * gridSize);
                if (xDraw >= designWidth)
                    return;
                
                // Draw the column.
                fLine.setLine(xDraw, yViewStart, xDraw, yViewEnd);
                g2D.setColor(gridColor);
                g2D.draw(fLine);
            }
        }
        
        /** This will draw all the rows.
         */
        private final void drawRows(Graphics2D g2D) {
            // Draw the column lines from yStart to the end of the design. Stop when first line past design height.
            float yDraw = yStart; // This is the place where the row is drawn at.
            float rowCount = 0f;
            while (true) { // Each loop produces a row drawn.
                // Draw the fine grid lines between row drawings.
                if (useFineGrid) {
                    g2D.setColor(gridFineColor);
                    for (float i=1.0f; i <= smallLineCount; i += 1.0f) {
                        yDraw = yStart + (smallGridSize * i) + (rowCount * gridSize);
                        // No more rows to draw.
                        if (yDraw > designHeight)
                            return;
                        
                        // Draw the row.
                        fLine.setLine(xViewStart, yDraw, xViewEnd, yDraw);
                        g2D.draw(fLine);
                    }
                }
                
                // Next row (Note, it is intentional to not draw the first row).
                rowCount += 1.0f;

                // No more rows to draw.
                yDraw = yStart + (rowCount * gridSize);
                if (yDraw > designHeight)
                    return;

                // Draw the row.
                fLine.setLine(xViewStart, yDraw, xViewEnd, yDraw);
                g2D.setColor(gridColor);
                g2D.draw(fLine);
            }
        }
        
        /** This will draw a black line around the drawing pad area.
         */
        public final void drawOuter(Graphics2D g2D) {
            g2D.setColor(Color.BLACK);
            fLine.setLine(0f, 0f, designWidth, 0f);
            g2D.draw(fLine);
            fLine.setLine(designWidth, 0f, designWidth, designHeight);
            g2D.draw(fLine);
            fLine.setLine(designWidth, designHeight, 0f, designHeight);
            g2D.draw(fLine);
            fLine.setLine(0f, designHeight, 0f, 0f);
            g2D.draw(fLine);
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class DrawGrid ">
    
    /** This will draw the grid.
     */
    class DrawGrid {
        
        // <editor-fold defaultstate="collapsed" desc=" Fields ">
        
        private float gridSize;
        private float smallGridSize;
        private float designWidth;
        private float designHeight;
        private Color oldColor;
        private Color gridColor;
        private Color gridFineColor;
        private float xStart;
        private float yStart;
        private boolean useFineGrid;
        private float smallLineCount;
        private float xViewEnd;
        private float yViewEnd;
        private float xViewStart;
        private float yViewStart;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
        
        /** Draw the grid.
         */
        public final void drawGrid(Graphics2D g2D) {
            g2D.setStroke(new BasicStroke(0.0f));
            
            // Setup the variables to draw.
            setupColor(g2D);
            setupDimensions();
            
            // Calculate the bounds to draw.
            calculateBounds(g2D);
            
            // Draw the grid.
            drawColumns(g2D);
            drawRows(g2D);
            drawOuter(g2D);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Logic Methods ">
        
        /** This will setup the color for the gird.
         */
        private final void setupColor(Graphics2D g2D) {
            oldColor = g2D.getColor();
            gridColor = Measurement.getGridColor();
            gridFineColor = Measurement.getFineGridColor();
        }
        
        /** This will setup the grid dimensions.
         */
        private final void setupDimensions() {
            // Need to calculate the starting upper left grid starting point and the count of grid lines.
            gridSize = Measurement.getGridSize();
            smallGridSize = Measurement.getFineGridSize();
            designWidth = design.getWidth();
            designHeight = design.getHeight();
        }
        
        /** This will calculate the bounds for drawing the grid.
         */
        private final void calculateBounds(Graphics2D g2D) {
            // This is the area that needs to be updated. It is in Measurements not Pixels.
            Rectangle clipBounds = g2D.getClipBounds();
            
            // Get the upper left grid point.
            xStart = (float)Math.floor(clipBounds.getX() / gridSize) * gridSize;
            yStart = (float)Math.floor(clipBounds.getY() / gridSize) * gridSize;
            if (xStart < 0.0f)
                xStart = 0.0f;
            if (clipBounds.getX() > designWidth)
                return; // Don't draw grid, outside of drawing pad.
            if (yStart < 0.0f)
                yStart = 0.0f;
            if (clipBounds.getY() > designHeight)
                return; // Don't draw grid, outside of drawing pad.

            // Get the upper right position of the view screen for this component.
            xViewStart = (float)clipBounds.getX();
            yViewStart = (float)clipBounds.getY();
            if (xViewStart < 0.0f)
                xViewStart = 0.0f;
            if (yViewStart < 0.0f)
                yViewStart = 0.0f;
            
            // Get the lower right position of the view screen for this component.
            xViewEnd = (float)(clipBounds.getX() + clipBounds.getWidth());
            yViewEnd = (float)(clipBounds.getY() + clipBounds.getHeight());
            
            if (xViewEnd > designWidth)
                xViewEnd = designWidth;
            if (yViewEnd > designHeight)
                yViewEnd = designHeight;
            
            // Small lines count per grid size.
            useFineGrid = Measurement.isFineGridVisible();
            smallLineCount = (int)(Measurement.getGridSize() / Measurement.getFineGridSize()) - 1;
            if (Measurement.getFineGridSize() * Measurement.getPixelsPerMeasurement() * iScale.getMeasurementScale() < 5)
                useFineGrid = false;
        }
        
        /** This will draw all the columns.
         */
        private final void drawColumns(Graphics2D g2D) {
            // Draw the column lines from xStart to the end of the design. Stop when first line past design width.
            float xDraw = xStart; // This is the place where the column is drawn at.
            float columnCount = 0.0f;
            while (true) { // Each loop produces a column drawn.
                // Draw the fine grid lines between column drawings.
                if (useFineGrid) {
                    g2D.setColor(gridFineColor);
                    for (float i=1.0f; i <= smallLineCount; i += 1.0f) {
                        xDraw = xStart + (smallGridSize * i) + (columnCount * gridSize);
                        // No more columns to draw.
                        if (xDraw >= designWidth)
                            return;
                        
                        // Draw the column.
                        fLine.setLine(xDraw, yViewStart, xDraw, yViewEnd);
                        g2D.draw(fLine);
                    }
                }
                
                // Next column (Note, it is intentional to not draw the first column).
                columnCount += 1.0f;
                
                // No more columns to draw.
                xDraw = xStart + (columnCount * gridSize);
                if (xDraw >= designWidth)
                    return;
                
                // Draw the column.
                fLine.setLine(xDraw, yViewStart, xDraw, yViewEnd);
                g2D.setColor(gridColor);
                g2D.draw(fLine);
            }
        }
        
        /** This will draw all the rows.
         */
        private final void drawRows(Graphics2D g2D) {
            // Draw the column lines from yStart to the end of the design. Stop when first line past design height.
            float yDraw = yStart; // This is the place where the row is drawn at.
            float rowCount = 0f;
            while (true) { // Each loop produces a row drawn.
                // Draw the fine grid lines between row drawings.
                if (useFineGrid) {
                    g2D.setColor(gridFineColor);
                    for (float i=1.0f; i <= smallLineCount; i += 1.0f) {
                        yDraw = yStart + (smallGridSize * i) + (rowCount * gridSize);
                        // No more rows to draw.
                        if (yDraw > designHeight)
                            return;
                        
                        // Draw the row.
                        fLine.setLine(xViewStart, yDraw, xViewEnd, yDraw);
                        g2D.draw(fLine);
                    }
                }
                
                // Next row (Note, it is intentional to not draw the first row).
                rowCount += 1.0f;

                // No more rows to draw.
                yDraw = yStart + (rowCount * gridSize);
                if (yDraw > designHeight)
                    return;

                // Draw the row.
                fLine.setLine(xViewStart, yDraw, xViewEnd, yDraw);
                g2D.setColor(gridColor);
                g2D.draw(fLine);
            }
        }
        
        /** This will draw a black line around the drawing pad area.
         */
        public final void drawOuter(Graphics2D g2D) {
            g2D.setColor(Color.BLACK);
            fLine.setLine(0f, 0f, designWidth, 0f);
            g2D.draw(fLine);
            fLine.setLine(designWidth, 0f, designWidth, designHeight);
            g2D.draw(fLine);
            fLine.setLine(designWidth, designHeight, 0f, designHeight);
            g2D.draw(fLine);
            fLine.setLine(0f, designHeight, 0f, 0f);
            g2D.draw(fLine);
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
}
