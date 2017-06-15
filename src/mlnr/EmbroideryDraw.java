/*
 * EmbroideryDraw.java
 *
 * Created on July 24, 2006, 4:27 PM
 *
 */

package mlnr;

import java.awt.Cursor;
import java.awt.Dialog;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mlnr.draw.ComplexPattern;
import mlnr.draw.DrawingLayer;
import mlnr.draw.expt.pem.PemSettings;
import mlnr.gui.BitmapSettings;
import mlnr.gui.FrameEmbroideryDraw;
import mlnr.gui.InterfaceFrameOperation;
import mlnr.gui.cpnt.DesignDocument;
import mlnr.gui.cpnt.DesignPreview;
import mlnr.gui.dlg.*;
import mlnr.gui.cpnt.DesignPanel;
import mlnr.gui.cpnt.DrawingPad;
import mlnr.gui.cpnt.ImageInfo;
import mlnr.gui.gen.DollieGenerator;
import mlnr.gui.gen.FreeStandingLaceGenerator;
import mlnr.gui.tool.*;

/** The brain class.
 * @author Robert Molnar II
 */
public class EmbroideryDraw {
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    /** The one and only instance of the EmbroideryDraw class running. */
    private static EmbroideryDraw INSTANCE = null;    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Tool Type Static Fields ">
    // Must stay above 0 since none tools are negative numbers.
    /** This is the tool that indicates no tool at all. */
    public static final int TOOL_NONE = 0;
    /** This will use the current tool type to create a new tool. Needed when the same tool needs to
     * be moved to another DrawingPad. */
    public static final int TOOL_CURRENT = 1;
    public static final int TOOL_LINE = 101;
    public static final int TOOL_BEZIER = 102;
    public static final int TOOL_RMOLNAR = 103;
    public static final int TOOL_CIRCLE = 104;
    public static final int TOOL_SQUARE = 105;
    public static final int TOOL_SELECT = 201;
    public static final int TOOL_ROTATE = 202;
    public static final int TOOL_RESIZE = 203;
    public static final int TOOL_TRANSLATE = 204;
    public static final int TOOL_MIRROR = 205;
    public static final int TOOL_DELETE = 301;
    public static final int TOOL_ADDPOINT = 302;
    public static final int TOOL_CONNECTRMOLNAR = 303;
    public static final int TOOL_PULL_LINEAPART = 304;
    public static final int TOOL_SIMPLEMOVER = 305;
    public static final int TOOL_BEZIERCONTROL = 306;
    public static final int TOOL_IMAGE_CENTER = 401;
    public static final int TOOL_IMAGE_DELETE = 402;
    public static final int TOOL_IMAGE_MOVER = 403;
    public static final int TOOL_IMAGE_RESIZE = 404;
    public static final int TOOL_IMAGE_ROTATE = 405;
    public static final int TOOL_FILL_COLOR = 501;
    public static final int TOOL_COMPLEX_PATTERN = 601;
    public static final int TOOL_MAGNIFYGLASS = 701;
    public static final int TOOL_DEBUG = 777000;    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Non-Tool Type Static Fields ">
    // Must stay under 0 since tool ids are above -1.
    public static final int NONTOOL_IMAGE_NEW = -1;
    public static final int NONTOOL_IMAGE_RESTORE_ALL = -2;
    public static final int NONTOOL_IMAGE_ONOFF = -3;
    public static final int NONTOOL_IMAGE_LIGHTENING_ONOFF = -4;
    public static final int NONTOOL_IMAGE_SET_LIGHTHENING = -5;
    public static final int NONTOOL_IMAGE_REMOVE_ALL = -6;
    public static final int NONTOOL_CONVERSION_PEMV4 = -101;
    public static final int NONTOOL_CONVERSION_BITMAP = -102;
    public static final int NONTOOL_DESIGN_SNAP_TO_GRID = -201;
    public static final int NONTOOL_DESIGN_SHOW_GRID = -202;
    public static final int NONTOOL_DESIGN_SHOW_CONTROL_PTS = -203;
    public static final int NONTOOL_STANDARD_NEW = -301;
    public static final int NONTOOL_STANDARD_OPEN = -302;
    public static final int NONTOOL_STANDARD_SAVE = -303;
    public static final int NONTOOL_STANDARD_CUT = -304;
    public static final int NONTOOL_STANDARD_COPY = -305;
    public static final int NONTOOL_STANDARD_PASTE_CURRENT_LAYER = -306;
    public static final int NONTOOL_STANDARD_PASTE_NEW_LAYER = -307;
    public static final int NONTOOL_STANDARD_NEW_DESIGN = -308;
    public static final int NONTOOL_STANDARD_UNDO = -309;
    public static final int NONTOOL_STANDARD_REDO = -310;
    public static final int NONTOOL_STAGE_VECTOR = -401;
    public static final int NONTOOL_STAGE_COLOR = -402;
    public static final int NONTOOL_ZOOMVIEW = -501;
    public static final int NONTOOL_GENERATOR_FSL = -601;
    public static final int NONTOOL_GENERATOR_DOLLIE = -601;    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    /** This is the current tool */
    FrameEmbroideryDraw parentFrame;
    /** This is the interface used to operation the gui from the main frame. */
    InterfaceFrameOperation iFrameOperator;
    /** This is the current tool being used. */
    AbstractTool currTool = null;
    /** This is the current tool's type. */
    int currToolType = TOOL_NONE;
    /** This is the previous tool's type. */
    int prevToolType = TOOL_NONE;
    /** Contains the tools that are selecting type tools. */
    HashMap hmSelectTools = new HashMap();
    /** This is the pattern for the circle. */
    ComplexPattern patternCircle = ComplexPattern.open(new File("tools/circle.rxml"), true);
    /** This is the pattern for the square. */
    ComplexPattern patternSquare = ComplexPattern.open(new File("tools/square.rxml"), true);
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc=" Constructors and load methods">
    /** This will create an instance of EmbroideryDraw.
     */
    static public void createInstance(FrameEmbroideryDraw parentFrame, InterfaceFrameOperation iFrameOperator) {
        INSTANCE = new EmbroideryDraw(parentFrame, iFrameOperator);
    }

    /** Creates a new instance of EmbroideryDraw */
    private EmbroideryDraw(FrameEmbroideryDraw parentFrame, InterfaceFrameOperation iFrameOperator) {
        this.parentFrame = parentFrame;
        this.iFrameOperator = iFrameOperator;

        // Selecting based tools.
        hmSelectTools.put(TOOL_ROTATE, TOOL_ROTATE);
        hmSelectTools.put(TOOL_RESIZE, TOOL_RESIZE);
        hmSelectTools.put(TOOL_TRANSLATE, TOOL_TRANSLATE);
        hmSelectTools.put(TOOL_MIRROR, TOOL_MIRROR);
        hmSelectTools.put(TOOL_MAGNIFYGLASS, TOOL_MAGNIFYGLASS);

        // Load up the settings.
        ImageInfo.getSettings().load();
        new PemSettings().load();
        BitmapSettings.loadBitmapSettings();
        Measurement.getSettings().load();
        DrawingLayer.getLayerSettings().load();
        DialogNewFile.getSettings().load();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Get Instance ">
    /** This will get the singleton class that represents EmbroideryDraw. */
    static public EmbroideryDraw getEmbroideryDraw() {
        return INSTANCE;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" File Methods ">
    /** This will create a new design document file.
     * @return a new design document, or if cancelled then it will return null.
     */
    public DesignDocument newFile() {
        DialogNewFile dialog = new DialogNewFile(parentFrame, true);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            DesignDocument dd = new DesignDocument(parentFrame.getFileMenuList());
            dd.createDesign(iFrameOperator, dialog.getDesignWidth(), dialog.getDesignHeight());
            return dd;
        }

        return null;
    }

    /** This will open a design document file.
     * @return a design document with the loaded information or null if problems or user cancelled.
     */
    public DesignDocument openFile() {
        // Get the file name filters.
        FileNameFilter supportFilter = new FileNameFilter(".rxml", "Embroidery Draw File");

        // Setup the dialog file chooser and get the absolute path.
        DialogFileChooser dfChooser = new DialogFileChooser("ed_openFile", supportFilter, "Open Drawing");
        if (dfChooser.showLoadDialog(parentFrame, null, new DesignPreview(200, 200)))
            return DesignDocument.openDocument(iFrameOperator, dfChooser.getFile());

        return null;
    }

    /** This will open a design document file.
     * @param fOpen is the file to open.
     * @return a design document with the loaded information or null if problems or user cancelled.
     */
    public DesignDocument openFile(File fOpen) {
        return DesignDocument.openDocument(iFrameOperator, fOpen);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Edit Methods ">
    /** This will undo an operation in the DesignPanel.
     * @return the new tool to use after this operation.
     */
    public AbstractTool editUndo(DesignPanel dPanel) {
        // Need to completely finalize the tool.
        AbstractTool abTool = EmbroideryDraw.getEmbroideryDraw().getTool();
        abTool.onCompleteFinalize();

        // Undo the current undo.
        dPanel.getDesign().undo();

        // Create a new tool.
        if (hmSelectTools.get(currToolType) != null)
            return newTool(EmbroideryDraw.TOOL_SELECT, dPanel);
        else
            return newTool(EmbroideryDraw.TOOL_CURRENT, dPanel);
    }

    /** This will redo an operation in the DesignPanel.
     * @return the new tool to use after this operation.
     */
    public AbstractTool editRedo(DesignPanel dPanel) {
        // Need to completely finalize the tool.
        AbstractTool abTool = EmbroideryDraw.getEmbroideryDraw().getTool();
        abTool.onCompleteFinalize();

        // Redo the current undo.
        dPanel.getDesign().redo();

        // Create a new tool.
        if (hmSelectTools.get(currToolType) != null)
            return newTool(EmbroideryDraw.TOOL_SELECT, dPanel);
        else
            return newTool(EmbroideryDraw.TOOL_CURRENT, dPanel);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Conversion Methods ">
    /**  This will bring up the settings bitmap.
     */
    public void onSettingsBitmap() {
        DialogBitmapSettings dialog = new DialogBitmapSettings(parentFrame, true);
        dialog.setVisible(true);
    }

    /** This will bring up the pem settings dialog box.
     */
    public void pemSettings() {
        DialogPemSettings dialog = new DialogPemSettings(parentFrame, true, PemSettings.getSamplingRate());
        dialog.setVisible(true);
    }

    // </editor-fold>            
    // <editor-fold defaultstate="collapsed" desc=" Generate Methods ">
    /** This will bring up the Dollie choose option and then the simple/advance dollie generator to create dollies.
     * @param currentDocument is the current document being used, can be null. If null then it means there are no drawings opened.
     * @return an array of DesignDocuments that need to be added to the program view. Can be null if  no DesignDocuments are needed.
     */
    public DesignDocument[] generateDollie(DesignDocument currentDocument) {
        DialogChooseDollie dialogchoice = new DialogChooseDollie(parentFrame, true);
        dialogchoice.setVisible(true);

        // User cancelled.
        if (dialogchoice.getChoiceOption() == DialogChooseDollie.CHOOSE_CANCEL)
            return null;

        // User wants to create a simple dollie.
        if (dialogchoice.getChoiceOption() == DialogChooseDollie.CHOOSE_SIMPLE) {
            DialogSimpleDollieGenerator dialog = new DialogSimpleDollieGenerator(parentFrame, true);

            // Enable only the new drawing if no drawing is opened.
            if (currentDocument == null)
                dialog.enableNewDrawingOnly();
            dialog.setVisible(true);

            // User cancelled.
            if (dialog.isOk() == false)
                return null;

            if (dialog.getPlacement() == DialogSimpleDollieGenerator.NEW_DRAWING) { // Place the items into new drawings.
                DesignDocument[] arrayDocuments = new DesignDocument[2];

                // Do whole pie.
                if (dialog.getGenerate() == DialogSimpleDollieGenerator.WHOLE_PIE || dialog.getGenerate() == DialogSimpleDollieGenerator.BOTH) {
                    arrayDocuments[0] = new DesignDocument(parentFrame.getFileMenuList());
                    arrayDocuments[0].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
                    arrayDocuments[0].generateSimpleDollie(dialog, DialogSimpleDollieGenerator.WHOLE_PIE);
                }

                // Do layer for the slice.
                if (dialog.getGenerate() == DialogSimpleDollieGenerator.SLICE || dialog.getGenerate() == DialogSimpleDollieGenerator.BOTH) {
                    arrayDocuments[1] = new DesignDocument(parentFrame.getFileMenuList());
                    arrayDocuments[1].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
                    arrayDocuments[1].generateSimpleDollie(dialog, DialogSimpleDollieGenerator.SLICE);
                }

                return arrayDocuments;
            } else {
                // Place the items into the current drawing. The calling function will determine which layer to place them in.
                currentDocument.generateSimpleDollie(dialog, dialog.getGenerate());
                return null;
            }

        } else { // User wants to create an advance dollie.
            DialogAdvanceDollieGenerator dialog = new DialogAdvanceDollieGenerator(parentFrame, true);

            // Enable only the new drawing if no drawing is opened.
            if (currentDocument == null)
                dialog.enableNewDrawingOnly();
            dialog.setVisible(true);

            // User cancelled.
            if (dialog.isOk() == false)
                return null;

            if (dialog.getPlacement() == DialogAdvanceDollieGenerator.NEW_DRAWING) { // Place the items into new drawings.
                DesignDocument[] arrayDocuments = new DesignDocument[3];

                // Do whole pie.
                if ((dialog.getGenerate() & DialogAdvanceDollieGenerator.WHOLE_PIE) != 0) {
                    arrayDocuments[0] = new DesignDocument(parentFrame.getFileMenuList());
                    arrayDocuments[0].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
                    arrayDocuments[0].generateAdvanceDollie(dialog, DialogAdvanceDollieGenerator.WHOLE_PIE);
                }

                // Do layer for the slice.
                if ((dialog.getGenerate() & DialogAdvanceDollieGenerator.SLICE) != 0) {
                    arrayDocuments[1] = new DesignDocument(parentFrame.getFileMenuList());
                    arrayDocuments[1].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
                    arrayDocuments[1].generateAdvanceDollie(dialog, DialogAdvanceDollieGenerator.SLICE);
                }
                
                // Do layer for inner pie.
                if ((dialog.getGenerate() & DialogAdvanceDollieGenerator.INNER_PIE) != 0) {
                    arrayDocuments[2] = new DesignDocument(parentFrame.getFileMenuList());
                    arrayDocuments[2].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
                    arrayDocuments[2].generateAdvanceDollie(dialog, DialogAdvanceDollieGenerator.INNER_PIE);
                }

                return arrayDocuments;
            } else {
                // Place the items into the current drawing. The calling function will determine which layer to place them in.
                currentDocument.generateAdvanceDollie(dialog, dialog.getGenerate());
                return null;
            }
            
        }
    }

    /** This will bring up the Free Standing Lace dialog box to create a free standing lace patterns.
     * @param currentDocument is the current document being used, can be null.
     * @return an array of DesignDocuments that need to be added to the program view. Can be null if 
     * no DesignDocuments are needed.
     */
    public DesignDocument[] generateFreeStandingLace(DesignDocument currentDocument) {
        DialogFreeStandingLaceGenerator dialog = new DialogFreeStandingLaceGenerator(parentFrame, true);
        if (currentDocument == null)
            dialog.enableNewDrawingOnly();
        dialog.setVisible(true);

        // Cancelled.
        if (dialog.isOk() == false)
            return null;

        if (dialog.isPlacementNewDrawings()) {
            // Place the items into new drawings.
            DesignDocument[] arrayDocuments = new DesignDocument[2];
            arrayDocuments[0] = new DesignDocument(parentFrame.getFileMenuList());
            arrayDocuments[0].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
            arrayDocuments[0].generateFreeStandingLace(dialog, FreeStandingLaceGenerator.GENERATE_BOTTOM);
            arrayDocuments[1] = new DesignDocument(parentFrame.getFileMenuList());
            arrayDocuments[1].createDesign(parentFrame, DialogNewFile.getDefaultWidth(), DialogNewFile.getDefaultHeight());
            arrayDocuments[1].generateFreeStandingLace(dialog, FreeStandingLaceGenerator.GENERATE_SIDE);
            return arrayDocuments;
        } else {
            // Place the items into the current drawing.
            currentDocument.generateFreeStandingLace(dialog, FreeStandingLaceGenerator.GENERATE_BOTH);
            return null;
        }
    }
    // </editor-fold>
    /** @return the parent frame. This is the main frame of the program.
     */
    public JFrame getProgramFrame() {
        return parentFrame;
    }

    /** This will enable the gui for design.
     * @param dPanel is the new DesignPanel that will be focused.
     */
    public void enableGUIForDesign(DesignPanel dPanel) {
        dPanel.getDesign().enableGUIForDesign();

        iFrameOperator.enableAdvanceTools(false);
    }

    /** This will get the current tool being used.
     * @return the current tool being used, else null (no tool being used).
     */
    public AbstractTool getTool() {
        return currTool;
    }

    /** This will create a new tool based on the tool type. It will also finalize the
     * current tool.
     * @param toolType is the new tool type.
     * @param dPanel is the current design panel.
     */
    public AbstractTool newTool(int toolType, DesignPanel dPanel) {
        return newTool(toolType, dPanel, false);
    }

    /** @return true if the tool is an advance select operation tool such as translate, rotate, resize, mirror, etc.., else false it is not.
     */
    public boolean isAdvanceSelectTool() {
        switch (currToolType) {
            case TOOL_ROTATE:
            case TOOL_RESIZE:
            case TOOL_MIRROR:
            case TOOL_TRANSLATE:
                return true;
            default:
                return false;
        }
    }

    /** This will create a new tool based on the tool type. It will also finalize the
     * current tool.
     * @param toolType is the new tool type.
     * @param dPanel is the current design panel.
     * @param overrideFinalize is true if to override the finalize in that it must complete its operation.
     */
    public AbstractTool newTool(int toolType, DesignPanel dPanel, boolean overrideFinalize) {
        // Garbage Collector
        System.gc();

        // Current Tool becomes previous tool.
        prevToolType = currToolType;

        if (toolType == TOOL_CURRENT)
            toolType = currToolType;
        if (toolType == TOOL_NONE) {
            currToolType = TOOL_NONE;
            currTool = null;
            iFrameOperator.notifyToolChanged();
            iFrameOperator.enableAdvanceTools(false);
            return null;
        }

        DrawingPad dPad = dPanel.getDrawingPad();

        // Finalize the current tool.
        boolean bFinalized = false;
        if (currTool != null)
            if (overrideFinalize || hmSelectTools.get(toolType) == null) {
                bFinalized = true;
                currTool.onFinalize(true);
            } else
                currTool.onFinalize(false);

        // If a tool operates on the selected items and it completely finalized (meaning no more 
        // selected items) then tool must change to a tool which does not operate on the selected items.
        if (bFinalized && hmSelectTools.get(toolType) != null)
            toolType = TOOL_SELECT;

        switch (toolType) {
            case TOOL_CIRCLE:
                currToolType = TOOL_CIRCLE;
                currTool = new ToolInternalComplexPattern(parentFrame, dPad, parentFrame.getInternalPatternOptions(), patternCircle, "Circle Tool Options");
                break;
            case TOOL_SQUARE:
                currToolType = TOOL_SQUARE;
                currTool = new ToolInternalComplexPattern(parentFrame, dPad, parentFrame.getInternalPatternOptions(), patternSquare, "Rectangle Tool Options");
                break;
            case TOOL_LINE:
                currToolType = TOOL_LINE;
                currTool = new ToolLine(parentFrame, dPad);
                break;
            case TOOL_BEZIER:
                currToolType = TOOL_BEZIER;
                currTool = new ToolBezier(parentFrame, dPad);
                break;
            case TOOL_RMOLNAR:
                currToolType = TOOL_RMOLNAR;
                currTool = new ToolRMolnar(parentFrame, dPad);
                break;
            case TOOL_DELETE:
                currToolType = TOOL_DELETE;
                currTool = new ToolDelete(parentFrame, dPad, parentFrame.getDeleteOption());
                break;
            case TOOL_ADDPOINT:
                currToolType = TOOL_ADDPOINT;
                currTool = new ToolAddPoint(parentFrame, dPad, parentFrame.getAddPointOptions());
                break;
            case TOOL_SIMPLEMOVER:
                currToolType = TOOL_SIMPLEMOVER;
                currTool = new ToolSimpleMover(parentFrame, dPad, parentFrame.getSelectOptions());
                break;
            case TOOL_CONNECTRMOLNAR:
                currToolType = TOOL_CONNECTRMOLNAR;
                currTool = new ToolConnectRMolnar(parentFrame, dPad, parentFrame.getConnectRMolnarOptions());
                break;
            case TOOL_BEZIERCONTROL:
                currToolType = TOOL_BEZIERCONTROL;
                currTool = new ToolBezierControl(parentFrame, dPad, parentFrame.getBezierControlOptions());
                break;
            case TOOL_PULL_LINEAPART:
                currToolType = TOOL_PULL_LINEAPART;
                currTool = new ToolPullLineApart(parentFrame, dPad, parentFrame.getPullLineApartOptions());
                break;
            case TOOL_SELECT:
                currToolType = TOOL_SELECT;
                currTool = new ToolSelect(parentFrame, dPad, parentFrame.getSelectOptions());
                break;
            case TOOL_ROTATE:
                currToolType = TOOL_ROTATE;
                currTool = new ToolRotate(parentFrame, dPad, parentFrame.getRotateOptions());
                break;
            case TOOL_RESIZE:
                currToolType = TOOL_RESIZE;
                currTool = new ToolResize(parentFrame, dPad, parentFrame.getResizeOptions());
                break;
            case TOOL_MIRROR:
                currToolType = TOOL_MIRROR;
                currTool = new ToolMirror(parentFrame, dPad, parentFrame.getMirrorOptions());
                break;
            case TOOL_TRANSLATE:
                currToolType = TOOL_TRANSLATE;
                currTool = new ToolMove(parentFrame, dPad, parentFrame.getTranslateOptions());
                break;
            case TOOL_IMAGE_CENTER:
                currToolType = TOOL_IMAGE_CENTER;
                currTool = new ToolImageCenter(parentFrame, dPad, parentFrame.getImageOptions());
                break;
            case TOOL_IMAGE_DELETE:
                currToolType = TOOL_IMAGE_DELETE;
                currTool = new ToolImageDelete(parentFrame, dPad, parentFrame.getImageOptions());
                break;
            case TOOL_IMAGE_MOVER:
                currToolType = TOOL_IMAGE_MOVER;
                currTool = new ToolImageMover(parentFrame, dPad, parentFrame.getImageOptions());
                break;
            case TOOL_IMAGE_RESIZE:
                currToolType = TOOL_IMAGE_RESIZE;
                currTool = new ToolImageResize(parentFrame, dPad, parentFrame.getImageOptions());
                break;
            case TOOL_IMAGE_ROTATE:
                currToolType = TOOL_IMAGE_ROTATE;
                currTool = new ToolImageRotate(parentFrame, dPad, parentFrame.getImageOptions());
                break;
            case TOOL_FILL_COLOR:
                currToolType = TOOL_FILL_COLOR;
                currTool = new ToolFillColor(parentFrame, dPad, parentFrame.getColorOptions());
                break;
            case TOOL_COMPLEX_PATTERN:
                currToolType = TOOL_COMPLEX_PATTERN;
                currTool = new ToolComplexPattern(parentFrame, dPad, parentFrame.getPatternOptions());
                break;
            case TOOL_MAGNIFYGLASS:
                currToolType = TOOL_MAGNIFYGLASS;
                currTool = new ToolMagifyGlass(parentFrame, dPanel);
                break;
            case TOOL_DEBUG:
                currToolType = TOOL_DEBUG;
                currTool = new ToolDebug(parentFrame, dPad);
                break;
        }

        iFrameOperator.notifyToolChanged();
        iFrameOperator.enableAdvanceTools(dPad.isSelectedItems());
        return currTool;
    }

    /** This will get the current tool type.
     */
    public int getToolType() {
        return currToolType;
    }

    /** This will get the previous tool type.
     */
    public int getPreviousToolType() {
        return prevToolType;
    }

}
