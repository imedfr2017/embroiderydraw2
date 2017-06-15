/*
 * DesignDocument.java
 *
 * Created on January 5, 2007, 9:01 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mlnr.EmbroideryDraw;
import mlnr.draw.DrawingDesign;
import mlnr.draw.DrawingLayer;
import mlnr.draw.LayerInfo;
import mlnr.draw.MetaDrawingInfo;
import mlnr.draw.expt.pem.PemV4;
import mlnr.gui.BitmapSettings;
import mlnr.gui.FrameEmbroideryDraw;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.dlg.DialogAdvanceDollieGenerator;
import mlnr.gui.dlg.DialogDrawingStats;
import mlnr.gui.dlg.DialogFileChooser;
import mlnr.gui.dlg.DialogFreeStandingLaceGenerator;
import mlnr.gui.dlg.DialogResizeDrawing;
import mlnr.gui.dlg.DialogSimpleDollieGenerator;
import mlnr.gui.dlg.FileNameFilter;
import mlnr.gui.gen.DollieGenerator;
import mlnr.gui.gen.FreeStandingLaceGenerator;
import mlnr.gui.tool.AbstractTool;
import mlnr.type.FPointType;
import mlnr.util.DefaultExceptionHandler;
import mlnr.util.XmlUtil;
import mlnr.util.gui.FileMenuList;
import org.w3c.dom.*;

/**
 *
 * @author Robert Molnar II
 */
public class DesignDocument extends JPanel implements InterfaceButtonTab, Printable, Pageable {
    // <editor-fold defaultstate="collapsed" desc=" Static Fields (Settings) ">
    /** This is a counter to be used to name each new DesignDocument created. */
    static private int newFilesOpened = 1;    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    /** This is the current tool being used on this document. */
    AbstractTool abToolCurrent;
    /** This is the design panel associated with this design document. */
    DesignPanel dPanel;
    /** This is the drawing pad associated with the DesignPanel. */
    DrawingPad dPad;
    /** This is the current place this DesignDocument is located. Can be null if does not exists. Must always contain
     * ".rxml" at the end of it. */
    String fileName;
    /** This is the absolute path of the current place to save the .rxml file. Starts off as null. */
    File fAbsolutePath = null;
    /** This contains extra information about the drawing when saving it such as email, web site, and author's name. */
    MetaDrawingInfo metaDrawingInfo = new MetaDrawingInfo();
    /** This is the JTabbedPane which will contain this DesignDocument. */
    JTabbedPane tabbedPanel = null;
    /** This is the current gui stage.*/
    int guiStage = FrameEmbroideryDraw.GUISTAGE_VECTOR;
    /** This is the file menu list contains the list of previous opened files. */
    FileMenuList fMenuList;
    /** This is the print page format set up before printing.
     */
    private PageFormat pageFormat = null;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    /** Creates a new instance of DesignDocument */
    public DesignDocument(FileMenuList fMenuList) {
        this.fMenuList = fMenuList;

        dPanel = new DesignPanel();
        dPad = dPanel.getDrawingPad();

        // Set the file name.
        fileName = "Untitled" + newFilesOpened + ".rxml";
        newFilesOpened++;

        setOpaque(true);
        setLayout(new BorderLayout());
        add(dPanel, BorderLayout.CENTER);
    }

    /** Used when opening a document.
     */
    private DesignDocument(DesignPanel dPanel, FileMenuList fMenuList) {
        this.fMenuList = fMenuList;
        this.dPanel = dPanel;
        this.dPad = dPanel.getDrawingPad();

        setLayout(new BorderLayout());
        add(dPanel, BorderLayout.CENTER);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    /** This will create the design with the width and height measurements.
     * @param InterfaceFrameOperation is the interface used to operation the main frame.
     * @param designWidth is the width of the new design in measurements.
     * @param designHeight is the height of the new design in measurements.
     */
    public void createDesign(InterfaceFrameOperation iFrameOperator, float designWidth, float designHeight) {
        dPanel.createDesign(iFrameOperator, designWidth, designHeight);
    }

    /** This will remove the tool from this DesignDocument and the tool will no
     * longer be associated with this DesignDocument.
     */
    public void removeTool() {
        if (abToolCurrent == null)
            return;

        dPad.removeKeyListener(abToolCurrent);
        dPad.removeMouseListener(abToolCurrent);
        dPad.removeMouseMotionListener(abToolCurrent);

        abToolCurrent = null;
    }

    /** This will set the tool as the new tool. If a tool pre-exists then it will be removed
     * from this DesignDocument.
     * @param abTool is the new tool to set this DesignDocument to.
     */
    public void setTool(AbstractTool abTool) {
        removeTool();

        // New tool.
        abToolCurrent = abTool;

        dPad.addKeyListener(abToolCurrent);
        dPad.addMouseListener(abToolCurrent);
        dPad.addMouseMotionListener(abToolCurrent);
    }

    /** This will bring up a dialog box to edit the meta information about the Drawing.
     * @param frame is the main frame.
     */
    public void editDesignDetails(JFrame frame) {
        metaDrawingInfo.edit(frame, fileName);
    }

    /** This will view the current stats on the design.
     */
    public void viewStats() {
        DialogDrawingStats dialog = new DialogDrawingStats(EmbroideryDraw.getEmbroideryDraw().getProgramFrame(), true);
        DrawingDesign d = getDesignPanel().getDesign();
        dialog.setCounts(d.getLayerCount(), d.getLineCount(), d.getVertexCount());
        dialog.setVisible(true);
    }

    /** This will resize the current design.
     */
    public void resizeDesign() {
        DialogResizeDrawing dialog = new DialogResizeDrawing(EmbroideryDraw.getEmbroideryDraw().getProgramFrame(), true);
        DrawingDesign d = getDesignPanel().getDesign();
        dialog.setDrawingSize(d.getWidth(), d.getHeight());
        dialog.setVisible(true);

        if (dialog.isOk()) {
            FPointType fptNewDesignSize = new FPointType();
            fptNewDesignSize.x = dialog.getDesignWidth();
            fptNewDesignSize.y = dialog.getDesignHeight();
            d.resizeDesign(fptNewDesignSize);

            getDesignPanel().zoomTo(getDesignPanel().getZoom());
            getDesignPanel().repaint();
        }
    }

    /** @return the DesignPanel associated with this DesignDocument.
     */
    public DesignPanel getDesignPanel() {
        return dPanel;
    }

    /** This will set the JTabbedPane for use with the Interface InterfaceButtonTab.
     */
    public void setJTabbedPane(JTabbedPane tabbedPanel) {
        this.tabbedPanel = tabbedPanel;
    }

    /** This will attempt to close the document.
     * @return true if ok to remove this DesignDocument from the program, else false, keep it.
     */
    public boolean close() {
        int answer = new JOptionPane().showConfirmDialog(this, "Do you want to save the changes to " + getFileRxml() + "?",
                "EmbroideryDraw", JOptionPane.YES_NO_CANCEL_OPTION);

        // Do not close the document.
        if (answer == JOptionPane.CANCEL_OPTION)
            return false;

        // Save the document.
        if (answer == JOptionPane.YES_OPTION)
            return writeDocument(null, false);

        return true;
    }
    
    /** This will generate the advance dollie in the Drawing by using the paramters from the dilaog.
     * @param dialog is assumed to be already ran, and is used to retrieve the paramters for the dollie.
     * @param generateItems is the items which need to be generated in this Drawing. The reason this value is
     * passed in is because when creating new drawings the items would be passed in separately here so that
     * each drawing would have a separate piece of the dollie. This is the DialogAdvanceDollieGenerator.WHOLE_PIE
     * or DialogAdvanceDollieGenerator.SLICE or DialogAdvanceDollieGenerator.INNER_PIE, now each one of these could
     * be OR'd together.
     */
    public void generateAdvanceDollie(DialogAdvanceDollieGenerator dialog, int generateItems) {
        DollieGenerator dollieGenerator = new DollieGenerator(getDesignPanel().getDrawingPad().getDesign());

        // Place dollies in the current layer. 
        if (dialog.getPlacement() == DialogAdvanceDollieGenerator.NEW_DRAWING)
            dollieGenerator.generateAdvanceDollie(dialog, generateItems);
        else { // Place dollies in seperate layers.
            DrawingDesign design = getDesignPanel().getDrawingPad().getDesign();

            try {
                // Make the next operations on design as one transaction.
                design.addUndoMarker();

                // Do layer for the whole pie.
                if ((generateItems & DialogAdvanceDollieGenerator.WHOLE_PIE) != 0) {
                    LayerInfo lPie = new LayerInfo("Whole Pie", Color.GREEN);
                    design.addLayer(lPie);
                    dollieGenerator.generateAdvanceDollie(dialog, DialogSimpleDollieGenerator.WHOLE_PIE);
                }

                // Do layer for the slice.
                if ((generateItems & DialogAdvanceDollieGenerator.SLICE) != 0) {
                    LayerInfo lSlice = new LayerInfo("Slice Part", Color.MAGENTA);
                    design.addLayer(lSlice);
                    dollieGenerator.generateAdvanceDollie(dialog, DialogSimpleDollieGenerator.SLICE);
                }

                // Do layer for the inner pie.
                if ((generateItems & DialogAdvanceDollieGenerator.INNER_PIE) != 0) {
                    LayerInfo lSlice = new LayerInfo("Inner Pie Part", Color.CYAN);
                    design.addLayer(lSlice);
                    dollieGenerator.generateAdvanceDollie(dialog, DialogAdvanceDollieGenerator.INNER_PIE);
                }

                design.completeUndo();
            } catch (Exception e) {
                // Make sure design completes the undo or there will be problems.
                design.completeUndo();
                DefaultExceptionHandler.printExceptionToLog(e, "Exception Caught: " + e);
            }
            
        }

        repaint();
    }

    /** This will generate the simple dollie in the Drawing by using the parameters from the dialog.
     * @param dialog is assumed to be already ran, and is used to retrieve the parameters for the dollie.
     * @param generateItems is the items which need to be generated in this Drawing. The reason this value is
     * passed in is because when creating new drawings the items would be passed in separately here so that
     * each drawing would have a separate piece of the dollie. This is the DialogSimpleDollieGenerator.WHOLE_PIE
     * or DialogSimpleDollieGenerator.SLICE or DialogSimpleDollieGenerator.BOTH.
     */
    public void generateSimpleDollie(DialogSimpleDollieGenerator dialog, int generateItems) {
        DollieGenerator dollieGenerator = new DollieGenerator(getDesignPanel().getDrawingPad().getDesign());

        // Place dollies in the current layer. 
        if (dialog.getPlacement() == DialogSimpleDollieGenerator.CURRENT_LAYER || dialog.getPlacement() == DialogSimpleDollieGenerator.NEW_DRAWING)
            dollieGenerator.generateSimpleDollie(dialog, generateItems);
        else { // Place dollies in seperate layers.
            DrawingDesign design = getDesignPanel().getDrawingPad().getDesign();

            try {
                // Make the next operations on design as one transaction.
                design.addUndoMarker();

                // Do layer for the whole pie.
                if (generateItems == DialogSimpleDollieGenerator.WHOLE_PIE || generateItems == DialogSimpleDollieGenerator.BOTH) {
                    LayerInfo lPie = new LayerInfo("Pie", Color.GREEN);
                    design.addLayer(lPie);
                    dollieGenerator.generateSimpleDollie(dialog, DialogSimpleDollieGenerator.WHOLE_PIE);
                }

                // Do layer for the slice.
                if (generateItems == DialogSimpleDollieGenerator.SLICE || generateItems == DialogSimpleDollieGenerator.BOTH) {
                    LayerInfo lSlice = new LayerInfo("Slice Part", Color.MAGENTA);
                    design.addLayer(lSlice);
                    dollieGenerator.generateSimpleDollie(dialog, DialogSimpleDollieGenerator.SLICE);
                }

                design.completeUndo();
            } catch (Exception e) {
                // Make sure design completes the undo or there will be problems.
                design.completeUndo();
                DefaultExceptionHandler.printExceptionToLog(e, "Exception Caught: " + e);
            }
            
        }

        repaint();
    }

    /** This will retrieve the paramters from the dialog and create the free standing lace.
     * @param dialog is assumed to be already ran.
     * @param generateItems is the FreeStandingLace.GENERATE_* option.
     */
    public void generateFreeStandingLace(DialogFreeStandingLaceGenerator dialog, int generateItems) {
        FreeStandingLaceGenerator FSLGenerator = new FreeStandingLaceGenerator(getDesignPanel().getDrawingPad().getDesign());

        // Place items in current layer if dialog option was current or new drawing.
        if (dialog.isPlacementCurrentLayer() || dialog.isPlacementNewDrawings()) {
            FSLGenerator.generateFreeStandingLace(dialog, generateItems);
            getDesignPanel().getDrawingPad().repaint();
        } else {
            DrawingDesign design = getDesignPanel().getDrawingPad().getDesign();

            try {
                // Make the next operations on design as one transaction.
                design.addUndoMarker();

                // Get layer for the bottom part.
                LayerInfo lBottom = new LayerInfo("Bottom Part", Color.GREEN);
                design.addLayer(lBottom);
                FSLGenerator.generateFreeStandingLace(dialog, FSLGenerator.GENERATE_BOTTOM);

                // Get layer for the side part.
                LayerInfo lSide = new LayerInfo("Side Part", Color.MAGENTA);
                design.addLayer(lSide);
                FSLGenerator.generateFreeStandingLace(dialog, FSLGenerator.GENERATE_SIDE);

                design.completeUndo();
            } catch (Exception e) {
                // Make sure design completes the undo or there will be problems.
                design.completeUndo();
                DefaultExceptionHandler.printExceptionToLog(e, "Exception Caught: " + e);
            }

            getDesignPanel().getDrawingPad().repaint();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" File Name Methods ">
    /** @return the rxml file name, this will never be null. It will not include the .rxml or .RXML.
     */
    public String getFileNameWithRxml() {
        return fileName.substring(0, fileName.length() - 5);
    }

    /** @return the rxml file name, this will never be null. It will include the .rxml or .RXML.
     */
    public String getFileRxml() {
        return fileName;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Serialize Support ">
    /** This will load the drawing from a file.
     * @param iFrameOperator is the interface used to operation the main frame.
     * @param fRxml is the Rxml file to load.
     * @return true if the file opened ok, else false errored out when trying to open the file.
     */
    public static DesignDocument openDocument(InterfaceFrameOperation iFrameOperator, File fRxml) {
        try {
            // Start the xml parsing.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(new FileInputStream(fRxml));

            // This should be the <rxml> element.
            Element root = doc.getDocumentElement();
            if ("rxml".equals(root.getNodeName()) == false)
                throw new Exception("Missing root element [rxml]. Not rxml file.");

            // Load the DesignPanel.
            DesignPanel dPanel = DesignPanel.openDesign(root, iFrameOperator);

            // Create the DesignDocument.
            DesignDocument document = new DesignDocument(dPanel, iFrameOperator.getFileMenuList());

            // Version 2.0 introduced new items such as stages and meta drawing needed by this class.
            if ("2.0".equals(XmlUtil.getAttributeString(root, "v"))) {
                // Get the gui stage.
                Element eStage = XmlUtil.getElementByTagName(root, "stage");
                document.guiStage = XmlUtil.getAttributeInteger(eStage, "value");

                // Get the meta drawing information.
                Element eDrawing = XmlUtil.getElementByTagName(root, "metaDrawingInfo");
                document.metaDrawingInfo.loadVersion20(eDrawing);
            }

            // Set the name of the file.
            document.fileName = fRxml.getName();
            document.fAbsolutePath = fRxml;

            // Update the file menu list to this file.
            iFrameOperator.getFileMenuList().updateFileOpenList(fRxml.getAbsolutePath());

            return document;
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to open the drawing at the path [" + fRxml.getAbsolutePath() + "].");
        }

        return null;
    }

    /** This will create a dialog box and find out where to save it and write the drawing out.
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     * @param saveAs if true will always produce a dialog box, where false will produce a dialog
     * box if the drawing has not been saved before.
     * @return true if document was written out, else false document was not written out.
     */
    public boolean writeDocument(LayerInfo lInfo, boolean saveAs) {
        try {
            File fSaveTo = fAbsolutePath;

            // Get the file name filters.
            FileNameFilter supportFilter = new FileNameFilter(".rxml", "Embroidery Draw File");

            // Need to get the absolute path to save the file to.
            if (saveAs || (saveAs == false && fAbsolutePath == null)) {
                // Setup the dialog file chooser and get the absolute path.
                DialogFileChooser dfChooser = new DialogFileChooser("writeDrawing", supportFilter, "Save Drawing");
                if (dfChooser.showSaveDialog(this, fileName, new DesignPreview(200, 200))) {
                    fSaveTo = dfChooser.getFile();

                    // Do not save location if saving a single layer.
                    if (lInfo == null) {
                        fileName = dfChooser.getFile().getName();
                        fAbsolutePath = dfChooser.getFile();
                    }
                } else
                    return false;
            }

            // Write the file out.
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            write(lInfo, fSaveTo);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            // Only update file open list if saving the entire design.
            if (lInfo == null)
                fMenuList.updateFileOpenList(fSaveTo.getAbsolutePath());

            return true;

        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to save the drawing to the path [" + fAbsolutePath + "].");
        }

        return false;
    }

    /** This will save the drawing into a file.
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     * @param fRxml is the Rxml file to save to.
     */
    private void write(LayerInfo lInfo, File fRxml) {
        try {
            PrintWriter out = new PrintWriter(fRxml);
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println(" <rxml v='2.0'>");
            out.println("  <stage value='" + getGuiStage() + "' />");
            metaDrawingInfo.write(out);
            dPad.write(out, lInfo);
            out.println(" </rxml>");
            out.close();
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to save the rxml to the path [" + fRxml + "].");
        }
    }

    /** This will save the drawing as a PEM. It is always a "save As".
     * @param layerSave is the layer to save or if null than all.
     */
    public void writePEM(LayerInfo layerSave) {
        PemV4 pem = new PemV4(getDesignPanel().getDesign(), getFileNameWithRxml());
        pem.save(layerSave);
    }

    /** This will save the drawing as a BITMAP. It is always a "Save As".
     * @param lInfo is the layer to write out, or null if the entire design is to be written out.
     */
    public void writeBitmap(LayerInfo lInfo) {
        try {
            // Get the bounds of the bitmap.
            Rectangle2D.Float fBounds;
            if (lInfo == null)
                fBounds = dPad.getDesignBounds2D();
            else
                fBounds = dPad.getDesign().getLayerBounds2D(lInfo);

            // Make sure fBounds isn't null if it is than there is nothing to write out.
            if (fBounds == null) {
                if (lInfo == null)
                    new JOptionPane().showMessageDialog(this, "The drawing does not contain any items to create a bitmap. No bitmap created.");
                else
                    new JOptionPane().showMessageDialog(this, "The layer [" + lInfo.getName() + "] does not contain any items to create a bitmap. No bitmap created.");
                return;
            }

            // Set up for a bitmap write.
            BitmapSettings bitmapSettings = BitmapSettings.getCurrentBitmapSetting();
            Graphics2D g2d = bitmapSettings.beginSaveBitmap(getFileNameWithRxml(), fBounds, dPad.getDesignColors());
            if (g2d == null)
                return;

            // Output the bitmap.
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dPad.writeBitmap(g2d, lInfo, bitmapSettings.isChangeColor(), bitmapSettings.isFillColorOnly());
            bitmapSettings.endSaveBitmap();
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to save the drawing to the path [" + fAbsolutePath + "].");
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Interface InterfaceButtonTab ">
    public void onCloseTab() {
        if (close())
            // Remove this document if user desires to.
            tabbedPanel.remove(this);
    }

    public String getTitle() {
        return fileName;
    }
    // </editor-fold>
    /** This will print out the document.
     */
    public void printDocument() {
        // Create a printJob object.
        PrinterJob printJob = PrinterJob.getPrinterJob();

        // Set the printable class to this one since we are implementing the Printable interface.
        this.pageFormat = printJob.defaultPage();
        printJob.setPageable(this);

        // Show a print dialog to the user. If the user click the print button, then print. Otherwise cancel the print job.
        if (printJob.printDialog())
            try {
                printJob.print();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /** @return the gui stage of this DesignDocument.
     */
    public int getGuiStage() {
        return guiStage;
    }

    /** This will set the gui stage for this DesignDocument.
     * @param guiStage is the stage to set this DesignDocument to, FrameEmbroideryDraw.GUI*.
     */
    public void setGuiStage(int guiStage) {
        this.guiStage = guiStage;
        if (guiStage == FrameEmbroideryDraw.GUISTAGE_VECTOR)
            getDesignPanel().getDesign().setStateToVector();
        else if (guiStage == FrameEmbroideryDraw.GUISTAGE_COLOR)
            getDesignPanel().getDesign().setStateToColorFill();
        getDesignPanel().getDrawingPad().repaint();
    }

    /** This is called when the document becomes focused.
     */
    public void notifyDocumentFocus() {
        EmbroideryDraw.getEmbroideryDraw().enableGUIForDesign(getDesignPanel());
    }

    /**
     * Method: print <p>
     *
     * This class is responsible for rendering a page using
     * the provided parameters. The result will be a grid
     * where each cell will be half an inch by half an inch.
     *
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g2d;

        //--- Validate the page number, we only print the first page
        if (pageIndex == 0) {
            g2d = (Graphics2D) graphics;

            double pageHeight = pageFormat.getImageableHeight();
            double pageWidth = pageFormat.getImageableWidth();
            double designHeight = getDesignPanel().getDesign().getHeight();
            double designWidth = getDesignPanel().getDesign().getWidth();

            double scaleX = (pageWidth) / designWidth;
            double scaleY = (pageHeight) / designHeight;

            if (scaleX < scaleY)
                scaleY = scaleX;
            else
                scaleX = scaleY;

            //--- Translate the origin to be (0,0)
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Now scale the design to full size page.
            g2d.scale(scaleX, scaleY);

            // Now draw the design into the image.
            getDesignPanel().getDrawingPad().drawAllBitmap(g2d, null, true, false);

            return (Printable.PAGE_EXISTS);
        } else
            return (Printable.NO_SUCH_PAGE);
    }

    /**
     * Returns the number of pages in the set.
     * To enable advanced printing features,
     * it is recommended that <code>Pageable</code>
     * implementations return the true number of pages
     * rather than the
     * UNKNOWN_NUMBER_OF_PAGES constant.
     * @return the number of pages in this <code>Pageable</code>.
     */
    public int getNumberOfPages() {
        return 1;
    }

    /**
     * Returns the <code>PageFormat</code> of the page specified by
     * <code>pageIndex</code>.
     * @param pageIndex the zero based index of the page whose
     *            <code>PageFormat</code> is being requested
     * @return the <code>PageFormat</code> describing the size and
     *		orientation.
     * @throws IndexOutOfBoundsException if
     *          the <code>Pageable</code> does not contain the requested
     *		page.
     */
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pageFormat;
    }

    /**
     * Returns the <code>Printable</code> instance responsible for
     * rendering the page specified by <code>pageIndex</code>.
     * @param pageIndex the zero based index of the page whose
     *            <code>Printable</code> is being requested
     * @return the <code>Printable</code> that renders the page.
     * @throws IndexOutOfBoundsException if
     *            the <code>Pageable</code> does not contain the requested
     *		  page.
     */
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return this;
    }

}
