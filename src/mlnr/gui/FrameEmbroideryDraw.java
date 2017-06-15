/*
 * FrameEmbroideryDraw.java
 *
 * Created on July 24, 2006, 4:34 PM
 *
 */

package mlnr.gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.Integer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import mlnr.EmbroideryDraw;
import mlnr.Measurement;
import mlnr.draw.DrawingDesign;
import mlnr.draw.Vertex;
import mlnr.draw.area.FillGraphSystem;
import mlnr.draw.expt.pem.PemSettings;
import mlnr.embd.Version;

import mlnr.gui.cpnt.*;
import mlnr.gui.cpnt.DesignPanel;
import mlnr.gui.dlg.DialogAbout;
import mlnr.gui.dlg.DialogCalibrate;
import mlnr.gui.dlg.DialogGridSettings;
import mlnr.gui.dlg.DialogSettings;
import mlnr.gui.tool.opt.*;
import mlnr.util.DefaultExceptionHandler;
import mlnr.util.ProgramActivation;
import mlnr.util.gui.FileMenuList;
import mlnr.util.gui.InterfaceFileMenuList;

/**
 *
 * @author Robert Molnar II
 */
public class FrameEmbroideryDraw extends JFrame implements WindowListener, InterfaceFrameOperation, InterfaceFileMenuList {
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    /** DEBUG MODE -- This will turn on the debug menu. */
    private static final boolean DEBUG_MODE = false;    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Component Fields ">
    /** This is the file list menu. */
    FileMenuList fMenuList = new FileMenuList(mlnr.embd.Version.getVersion(), this, 4);
    /** Status bar for the frame. */
    JStatusPanel statusBar = new JStatusPanel();
    /** This is the panel that contains all the tools bars. */
    JPanel panelToolBars = new JPanel(new GridLayout(2, 1));
    /** This is the panel that contains the layer info, tool options. */
    JPanel panelEast = new JPanel(new BorderLayout());
    /** This is the panel that contains the tool options. */
    JPanel panelOptions = new JPanel(new BorderLayout());
    /** This is the layer panel that contains the layer info. */
    LayerPanel panelLayer = new LayerPanel(this);
    /** This is where each design view is placed. */
    DesignTabbedPane tabbedDesignView = new DesignTabbedPane(this);
    /** This is the frame container. */
    Container frameContainer;    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    /** This is this frame. */
    JFrame thisFrame = this;
    /** This is the stage the gui is in. */
    int guiStage = GUISTAGE_VECTOR;
    /** This is a list of the bitmap settings JMenuItem. */
    LinkedList ltBitmapSettings = new LinkedList();
    /** The key is: (int) EmbroideryDraw.TOOL_*
     * The value is: (JButtonToggle) of the button on the menu bar.
     */
    HashMap hmButtonInfo = new HashMap();
    /** Contains a list of menu items that need to be (dis)enable when changing stages. */
    LinkedList ltMenuButtonInfo = new LinkedList();
    /** This will perform the copy, cut, and paste operations. */
    CopyCutPaste copyCutPaste = new CopyCutPaste(this);
    /** Contains all the view menu zoom items. */
    LinkedList ltViewMenuZoom = new LinkedList();    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Menu Item Fields (Menu) ">
    /** This is the main menubar. */
    JMenuBar menuBar = new JMenuBar();    // This is the file menu.
    JMenu menuFile = new JMenu("File");
    JMenuItem newFileMenu = new JMenuItem("New..", 'N');
    JMenuItem openFileMenu = new JMenuItem("Open", 'O');
    JMenuItem closeFileMenu = new JMenuItem("Close", 'C');
    JMenuItem closeAllFileMenu = new JMenuItem("Close All");
    JMenuItem saveFileMenu = new JMenuItem("Save", 'S');
    JMenuItem saveAsFileMenu = new JMenuItem("Save As", 'A');
    JMenuItem saveAllFileMenu = new JMenuItem("Save All", 'L');
    JMenuItem printFileMenu = new JMenuItem("Print", 'P');
    JMenuItem exitFileMenu = new JMenuItem("Exit", 'X');
    /** The number of open files to save in the File menu.  Max of 9. */
    private static final int NUMBER_OF_OPEN_FILE = 4;    // This is the conversion menu.
    JMenu menuConversion = new JMenu("Conversion");
    JMenuItem pemv4ConversionMenu = new JMenuItem("Save As PEM V4");
    JMenuItem pemSettingsConversionMenu = new JMenuItem("PEM Settings");
    JMenuItem bitmapConversionMenu = new JMenuItem("Save As Bitmap");
//    JMenuItem emfConversionMenu = new JMenuItem("Save As EMF");
    // This is under the conversion menu.
    JMenu bitmapSettingsMenu = new JMenu("Bitmap Settings");
    JMenuItem defaultBitmapSettingsMenu = new JMenuItem("Default");
    JMenuItem generationsBitmapSettingsMenu = new JMenuItem("Generations");
    JMenuItem settingsBitmapSettingsMenu = new JMenuItem("Settings");    // This is the edit menu.
    JMenu menuEdit = new JMenu("Edit");
    JMenuItem undoEditMenu = new JMenuItem("Undo");
    JMenuItem redoEditMenu = new JMenuItem("Redo");
    JMenuItem cutEditMenu = new JMenuItem("Cut");
    JMenuItem copyEditMenu = new JMenuItem("Copy");
    JMenuItem pasteIntoCurrentLayerEditMenu = new JMenuItem("Paste Into Current Layer");
    JMenuItem pasteIntoNewLayerEditMenu = new JMenuItem("Paste Into New Layer");
    JMenuItem pasteAsNewDesignEditMenu = new JMenuItem("Paste As New Design");
    JMenuItem settingsEditMenu = new JMenuItem("Settings..");    // This is the view menu.
    JMenu menuView = new JMenu("View");
    JRadioButtonMenuItem zoomDrawingViewMenu = new JRadioButtonMenuItem("Zoom To Fit Drawing");
    JRadioButtonMenuItem zoomLayerViewMenu = new JRadioButtonMenuItem("Zoom To Fit Layer");
    JRadioButtonMenuItem zoom100ViewMenu = new JRadioButtonMenuItem("Zoom To 100%");
    JRadioButtonMenuItem zoom200ViewMenu = new JRadioButtonMenuItem("Zoom To 200%");
    JRadioButtonMenuItem zoom400ViewMenu = new JRadioButtonMenuItem("Zoom To 400%");
    JRadioButtonMenuItem zoom800ViewMenu = new JRadioButtonMenuItem("Zoom To 800%");
    JRadioButtonMenuItem zoom75ViewMenu = new JRadioButtonMenuItem("Zoom To 75%");
    JRadioButtonMenuItem zoom50ViewMenu = new JRadioButtonMenuItem("Zoom To 50%");
    JRadioButtonMenuItem zoom25ViewMenu = new JRadioButtonMenuItem("Zoom To 25%");
    JRadioButtonMenuItem gridViewMenu = new JRadioButtonMenuItem("Grid On/Off");
    JRadioButtonMenuItem snapToGridMenu = new JRadioButtonMenuItem("Snap to Grid On/Off");
    JRadioButtonMenuItem showControlPointsMenu = new JRadioButtonMenuItem("Show Control Points");
    JMenuItem changeGridViewMenu = new JMenuItem("Change Grid Settings..");    
    JMenuItem calibrateDrawingMenu = new JMenuItem("Calibrate Drawing Measurements...");
    /** This is the image menu. */
    JMenu menuImage = new JMenu("Image");
    JMenuItem loadImageMenu = new JMenuItem("Load Image");
    JMenuItem removeAllImageMenu = new JMenuItem("Remove All Images");
    JMenuItem deleteImageMenu = new JMenuItem("Remove Image");
    JMenuItem rotateImageMenu = new JMenuItem("Rotate Image");
    JMenuItem scaleImageMenu = new JMenuItem("Resize Image");
    JMenuItem moveImageMenu = new JMenuItem("Move Image");
    JMenuItem restoreAllImageMenu = new JMenuItem("Restore Images To Original Size");
    JRadioButtonMenuItem turnImagesOnOffImageMenu = new JRadioButtonMenuItem("Image(s) On/Off");
    JRadioButtonMenuItem turnLighteningOnOffImageMenu = new JRadioButtonMenuItem("Lighten Image(s) On/Off");
    JMenuItem setLighteningImageMenu = new JMenuItem("Image Settings...");    // This is the design menu.
    JMenu menuDesign = new JMenu("Drawing");
    JMenuItem resizeDesignMenu = new JMenuItem("Resize Drawing Pad");
    JMenuItem detailsDesignMenu = new JMenuItem("Edit Details");
    JMenuItem statsDesignMenu = new JMenuItem("Stats");    // This is the custom tools menu.
    JMenu menuPatternTools = new JMenu("Patterns");
    JMenuItem showDirectoryPatternMenu = new JMenuItem("Show Directory");
    JMenuItem reloadPatternsMenu = new JMenuItem("Reload Patterns");    // This is the layer menu.
    JMenuItem newLayerMenu = new JMenuItem("New Layer");
    JMenuItem deleteLayerMenu = new JMenuItem("Delete Layer");
    JMenuItem deleteAllLayerMenu = new JMenuItem("Delete All Layers");
    JMenuItem updateInfoLayerMenu = new JMenuItem("Update Layer Information..");
    JMenuItem layersOnOffLayerMenu = new JMenuItem("Layer Visible On/Off");
    JMenuItem mergeTwoLayerMenu = new JMenuItem("Merge Two Layers");
    JMenuItem mergeAllLayersToMasterLayerMenu = new JMenuItem("Merge All Layers To Master");
    JMenuItem selectLayerMenu = new JMenuItem("Select Layer");    // This is the stage menu.
    JMenu menuStage = new JMenu("Stage Mode");
    JMenuItem colorFillStateMenu = new JMenuItem("Color Fill Mode");
    JMenuItem vectorStateMenu = new JMenuItem("Vector Mode");    // This is the help menu.
    JMenu menuHelp = new JMenu("Help");
    JMenuItem helpHelpMenu = new JMenuItem("Embroidery Draw Videos (www.embroiderydraw.com/)");
    JMenuItem websiteHelpMenu = new JMenuItem("www.embroiderydraw.com");
    JMenuItem aboutHelpMenu = new JMenuItem("About..");    // This is the debug menu.
    JMenu menuDebug = new JMenu("Debug");
    JMenuItem printDesignDebugMenu = new JMenuItem("Print Design Info");
    JMenuItem printUndoListDebugMenu = new JMenuItem("Print Undo Info");
    JMenuItem printFileDesignDebugMenu = new JMenuItem("File - Print Design Info");
    JMenuItem printFileUndoListDebugMenu = new JMenuItem("File - Print Undo Info");
    JMenuItem rmolnarTensionDebugMenu = new JMenuItem("RMolnar Tension");
    JMenuItem clearFileOpenListDebugMenu = new JMenuItem("Clear File Open List");
    JMenuItem distanceBetweenPointDebugMenu = new JMenuItem("Distance Between Points (PEM Export Only)");
    JMenuItem showNumberDesignDebugMenu = new JMenuItem("Show Numbers for Lines and Vertices");
    JMenuItem removeProgramKeyDebugMenu = new JMenuItem("Remove Program Key");
    JMenuItem setTrialDaysDebugMenu = new JMenuItem("Set Trial Days");
    JMenuItem toggleColorModeDebugMenu = new JMenuItem("Color Fill-In DEBUG Toggle");    // This will listen for file menu actions.
    FileMenuListen fileMenuListener = new FileMenuListen();
    ConversionMenuListen conversionMenuListener = new ConversionMenuListen();
    EditMenuListen editMenuListener = new EditMenuListen();
    ViewMenuListen viewMenuListener = new ViewMenuListen();
    ImageMenuListen imageMenuListener = new ImageMenuListen();
    DesignMenuListen designMenuListener = new DesignMenuListen();
    PatternMenuListen patternMenuListener = new PatternMenuListen();
    LayerMenuListen layerMenuListener = new LayerMenuListen();
    StageMenuListen stageMenuListener = new StageMenuListen();
    HelpMenuListen helpMenuListener = new HelpMenuListen();
    DebugMenuListen debugMenuListener = new DebugMenuListen();    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" JMenuBar Fields (Buttons) ">
    JToolBar topBar = new JToolBar();
    JToolBar bottomBar = new JToolBar();    // This is the previous button that was toggled.
    JToggleButton togglePrevious = null;    // Design Tools
    JToggleButton btnDesignToolRectangle = new JToggleButton(new ImageIcon("images/icons/rectangle.gif"));
    JToggleButton btnDesignToolCircle = new JToggleButton(new ImageIcon("images/icons/circle.gif"));
    JToggleButton btnDesignToolLine = new JToggleButton(new ImageIcon("images/icons/line.gif"));
    JToggleButton btnDesignToolBezier = new JToggleButton(new ImageIcon("images/icons/bezier.gif"));
    JToggleButton btnDesignToolRMolnar = new JToggleButton(new ImageIcon("images/icons/rmolnar.gif"));
    JToggleButton btnComplexPatterns = new JToggleButton(new ImageIcon("images/icons/pattern.gif"));
    JToggleButton btnDesignToolDelete = new JToggleButton(new ImageIcon("images/icons/delete.gif"));
    JToggleButton btnDesignToolAddPoint = new JToggleButton(new ImageIcon("images/icons/addPoint.gif"));
    JToggleButton btnDesignToolBezierControl = new JToggleButton(new ImageIcon("images/icons/bezierControl.gif"));
    JToggleButton btnDesignToolMoverControl = new JToggleButton(new ImageIcon("images/icons/mover.gif"));
    JToggleButton btnDesignToolSelect = new JToggleButton(new ImageIcon("images/icons/select.gif"));
    JToggleButton btnDesignToolTranslate = new JToggleButton(new ImageIcon("images/icons/translate.gif"));
    JToggleButton btnDesignToolRotate = new JToggleButton(new ImageIcon("images/icons/rotate.gif"));
    JToggleButton btnDesignToolResize = new JToggleButton(new ImageIcon("images/icons/resize.gif"));
    JToggleButton btnDesignToolMirror = new JToggleButton(new ImageIcon("images/icons/Mirrow.gif"));
    JToggleButton btnDesignToolPullLine = new JToggleButton(new ImageIcon("images/icons/Pull-line-apart.gif"));
    JToggleButton btnDesignToolConnectRMolnar = new JToggleButton(new ImageIcon("images/icons/connect.gif"));
    JToggleButton btnDesignToolDEBUG = new JToggleButton("DEBUG");
    JToggleButton btnDesignToolMagifyingGlass = new JToggleButton(new ImageIcon("images/icons/Zoom-in-Zoom-out.gif"));
    ComboBoxZoom comboBoxZoom = new ComboBoxZoom(this); 
    JButton btnGenerateFreeStandingLace = new JButton(new ImageIcon("images/icons/fsl.gif")); 
    JButton btnGenerateDollie = new JButton(new ImageIcon("images/icons/dollies.gif"));
    JButton btnImageToolLoad = new JButton(new ImageIcon("images/icons/newimage-load.gif"));
    JToggleButton btnImageToolDelete = new JToggleButton(new ImageIcon("images/icons/deleteImage.gif"));
    JToggleButton btnImageToolRotate = new JToggleButton(new ImageIcon("images/icons/rotateimage.gif"));
    JToggleButton btnImageToolResize = new JToggleButton(new ImageIcon("images/icons/Resizeimage.gif"));
    JToggleButton btnImageToolMove = new JToggleButton(new ImageIcon("images/icons/moveimage.gif"));
    JToggleButton btnImageToolCenter = new JToggleButton(new ImageIcon("images/icons/Image-center.gif"));
    JButton btnImageToolRestoreAll = new JButton(new ImageIcon("images/icons/Restore-all-images.gif"));
    JToggleButton btnImageToolImagesOnOff = new JToggleButton(new ImageIcon("images/icons/Image-on-off2.gif"));
    JToggleButton btnImageToolLighteningOnOff = new JToggleButton(new ImageIcon("images/icons/lighten-images.gif"));
    JButton btnImageToolSetLightening = new JButton(new ImageIcon("images/icons/settings-for-images.gif"));
    JButton btnImageToolRemoveAll = new JButton(new ImageIcon("images/icons/Delete-all-images.gif"));    
    JButton btnConversionToolPemV4 = new JButton(new ImageIcon("images/icons/pemV4.gif"));
    JButton btnConversionToolToolDesign = new JButton(new ImageIcon("images/icons/txml.gif"));
    JButton btnConversionToolBitmap = new JButton(new ImageIcon("images/icons/bitmap.gif"));
//    JButton btnConversionToolEMF = new JButton(new ImageIcon("images/icons/save-EMF.gif"));
    // Design Operations tools
    JToggleButton btnSnapToGrid = new JToggleButton(new ImageIcon("images/icons/snap-to-grid.gif"));
    JToggleButton btnShowGrid = new JToggleButton(new ImageIcon("images/icons/showgrid.gif"));
    JToggleButton btnShowControlPoints = new JToggleButton(new ImageIcon("images/icons/Show-control-points.gif"));  
    JButton btnStandardToolNew = new JButton(new ImageIcon("images/icons/new.gif"));
    JButton btnStandardToolOpen = new JButton(new ImageIcon("images/icons/open.gif"));
    JButton btnStandardToolSave = new JButton(new ImageIcon("images/icons/save.gif"));
    JButton btnStandardToolCut = new JButton(new ImageIcon("images/icons/cut.gif"));
    JButton btnStandardToolCopy = new JButton(new ImageIcon("images/icons/copy.gif"));
    JButton btnStandardToolPasteCurrentLayer = new JButton(new ImageIcon("images/icons/pasteIntoNewLayer.gif"));
    JButton btnStandardToolPasteNewLayer = new JButton(new ImageIcon("images/icons/pasteIntoCurrentLayer.gif"));
    JButton btnStandardToolPasteNewDesign = new JButton(new ImageIcon("images/icons/pasteIntoNewDesign.gif"));
    JButton btnStandardToolUndo = new JButton(new ImageIcon("images/icons/undo1.gif"));
    JButton btnStandardToolRedo = new JButton(new ImageIcon("images/icons/redo1.gif"));   
    JButton btnFillInVector = new JButton(new ImageIcon("images/icons/vector-mode.gif"));
    JButton btnFillInColor = new JButton(new ImageIcon("images/icons/color-mode.gif"));
    JToggleButton btnFillInColorTool = new JToggleButton(new ImageIcon("images/icons/fill-color.gif"));
    /** This will listen for the ToolBar actions. */
    BarDesignListen barDesignListner = new BarDesignListen();
    BarGeneratorListen barGeneratorListner = new BarGeneratorListen();
    BarColorFillListen barColorFillListener = new BarColorFillListen();
    BarImageListen barImageListener = new BarImageListen();
    BarStandardListen barStandardListener = new BarStandardListen();
    BarCustomUserTools barCustomUserToolListener = new BarCustomUserTools();
    BarDesignOperationsListen barDesignOperationsListener = new BarDesignOperationsListen();    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Tool Option Fields ">
    DeletePanel toolDeletePanel = new DeletePanel();
    AddPointPanel toolAddPointPanel = new AddPointPanel();
    SelectPanel toolSelectPanel = new SelectPanel();
    MirrorPanel toolMirrorPanel = new MirrorPanel();
    ResizePanel toolResizePanel = new ResizePanel(this);
    RotatePanel toolRotatePanel = new RotatePanel(this);
    TranslatePanel toolTranslatePanel = new TranslatePanel(this);
    BezierControlPanel toolBezierControlPanel = new BezierControlPanel();
    ConnectRMolnarPanel toolConnectRMolnarPanel = new ConnectRMolnarPanel();
    PullLineApartPanel toolPullLineApartPanel = new PullLineApartPanel();
    ImagePanel toolImagePanel = new ImagePanel(this);
    ColorPanel toolColorPanel = new ColorPanel();
    PatternPanel toolPatternPanel = new PatternPanel(this);
    InternalPatternPanel toolInternalPatternPanel = new InternalPatternPanel();
    private File fOpen;
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    /** Creates a new instance of FrameEmbroideryDraw */
    public FrameEmbroideryDraw() {
        // Fire-up the brain class.
        EmbroideryDraw.createInstance(this, this);

        // Setup the frame looks.
        ImageIcon ii = new ImageIcon("images/tiger.gif");
        setIconImage(ii.getImage());
        setTitle("Embroidery Draw " + Version.getCurrentVersion());
        setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);

        // Setup the menu file system.
        setupMenuFile();
        setupMenuEdit();
        setupMenuView();
        setupMenuConversion();
        setupMenuPatternTools();
        setupMenuDesign();
        setupMenuImage();
        setupMenuStage();
        setupMenuHelp();
        setupMenuDebug();
        this.setJMenuBar(menuBar);

        // Setup the JMenuBar toolbars.
        setupToolbarStandard();
        setupToolBarDesignOperations();
        setupToolbarConversion();
        setupToolbarImage();
        setupToolbarFillColor();
        setupToolbarDesign();
        setupToolbarGenerators();

        // Setup the JMenuBar toolbars.
        GridLayout glToolBars = new GridLayout(2, 1);
        panelToolBars = new JPanel(glToolBars);
        JPanel jpTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel jpBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jpTop.add(topBar);
        jpBottom.add(bottomBar);
        bottomBar.setFloatable(false);
        topBar.setFloatable(false);
        panelToolBars.add(jpTop);
        panelToolBars.add(jpBottom);

        // Setup the east panel.
        panelEast.add(panelLayer, BorderLayout.NORTH);

        // Setup the layout of the frame.
        frameContainer = getContentPane();
        frameContainer.setLayout(new BorderLayout(0, 0));
        frameContainer.add(panelToolBars, BorderLayout.NORTH);
        frameContainer.add(tabbedDesignView, BorderLayout.CENTER);
        frameContainer.add(statusBar, BorderLayout.SOUTH);
        frameContainer.add(panelEast, BorderLayout.EAST);
        frameContainer.validate();

        // This will setup the button information to know which ones are enabled at each gui stage.
        setupButtonInfos();
        setupMenuButtonInfos();


        // Set the gui to empty since no drawing is opened.
        setGUIStage(GUISTAGE_EMPTY);

        // Update the grid, snap, and show menu/button items.
        invalidateGridSnapShow();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" ButtonInfo Setup ">
    /** This will setup menu button information.
     */
    private void setupMenuButtonInfos() {
        // If a menu item is always on then it is not included in this list of menu items.

        ltMenuButtonInfo.add(new ButtonInfo(closeFileMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(closeAllFileMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(saveFileMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(saveAsFileMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(saveAllFileMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(printFileMenu, false, true, true));

        ltMenuButtonInfo.add(new ButtonInfo(pemv4ConversionMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(pemSettingsConversionMenu, true, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(bitmapConversionMenu, false, true, true));

        ltMenuButtonInfo.add(new ButtonInfo(undoEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(redoEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(cutEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(copyEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(pasteIntoCurrentLayerEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(pasteIntoNewLayerEditMenu, false, false, false));
        ltMenuButtonInfo.add(new ButtonInfo(pasteAsNewDesignEditMenu, false, false, false));

        ltMenuButtonInfo.add(new ButtonInfo(loadImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(removeAllImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(deleteImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(rotateImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(scaleImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(moveImageMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(restoreAllImageMenu, false, true, true));

        ltMenuButtonInfo.add(new ButtonInfo(resizeDesignMenu, false, true, false));
        ltMenuButtonInfo.add(new ButtonInfo(detailsDesignMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(statsDesignMenu, false, true, true));

        ltMenuButtonInfo.add(new ButtonInfo(menuStage, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(colorFillStateMenu, false, true, false));
        ltMenuButtonInfo.add(new ButtonInfo(vectorStateMenu, false, false, true));

        ltMenuButtonInfo.add(new ButtonInfo(zoomDrawingViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoomLayerViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom100ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom200ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom400ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom800ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom75ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom50ViewMenu, false, true, true));
        ltMenuButtonInfo.add(new ButtonInfo(zoom25ViewMenu, false, true, true));
        
    }

    /** This will setup button information.
     */
    private void setupButtonInfos() {
        // Tool Buttons.
        hmButtonInfo.put(EmbroideryDraw.TOOL_NONE, ButtonInfo.newDummy(EmbroideryDraw.TOOL_NONE));
        hmButtonInfo.put(EmbroideryDraw.TOOL_CURRENT, ButtonInfo.newDummy(EmbroideryDraw.TOOL_CURRENT));
        hmButtonInfo.put(EmbroideryDraw.TOOL_LINE, new ButtonInfo(EmbroideryDraw.TOOL_LINE, btnDesignToolLine, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_BEZIER, new ButtonInfo(EmbroideryDraw.TOOL_BEZIER, btnDesignToolBezier, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_RMOLNAR, new ButtonInfo(EmbroideryDraw.TOOL_RMOLNAR, btnDesignToolRMolnar, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_CIRCLE, new ButtonInfo(EmbroideryDraw.TOOL_CIRCLE, btnDesignToolCircle, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_SQUARE, new ButtonInfo(EmbroideryDraw.TOOL_SQUARE, btnDesignToolRectangle, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_SELECT, new ButtonInfo(EmbroideryDraw.TOOL_SELECT, btnDesignToolSelect, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_ROTATE, new ButtonInfo(EmbroideryDraw.TOOL_ROTATE, btnDesignToolRotate, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_RESIZE, new ButtonInfo(EmbroideryDraw.TOOL_RESIZE, btnDesignToolResize, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_TRANSLATE, new ButtonInfo(EmbroideryDraw.TOOL_TRANSLATE, btnDesignToolTranslate, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_MIRROR, new ButtonInfo(EmbroideryDraw.TOOL_MIRROR, btnDesignToolMirror, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_DELETE, new ButtonInfo(EmbroideryDraw.TOOL_DELETE, btnDesignToolDelete, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_ADDPOINT, new ButtonInfo(EmbroideryDraw.TOOL_ADDPOINT, btnDesignToolAddPoint, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_CONNECTRMOLNAR, new ButtonInfo(EmbroideryDraw.TOOL_CONNECTRMOLNAR, btnDesignToolConnectRMolnar, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_PULL_LINEAPART, new ButtonInfo(EmbroideryDraw.TOOL_PULL_LINEAPART, btnDesignToolPullLine, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_SIMPLEMOVER, new ButtonInfo(EmbroideryDraw.TOOL_SIMPLEMOVER, btnDesignToolMoverControl, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_BEZIERCONTROL, new ButtonInfo(EmbroideryDraw.TOOL_BEZIERCONTROL, btnDesignToolBezierControl, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_IMAGE_CENTER, new ButtonInfo(EmbroideryDraw.TOOL_IMAGE_CENTER, btnImageToolCenter, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_IMAGE_DELETE, new ButtonInfo(EmbroideryDraw.TOOL_IMAGE_DELETE, btnImageToolDelete, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_IMAGE_MOVER, new ButtonInfo(EmbroideryDraw.TOOL_IMAGE_MOVER, btnImageToolMove, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_IMAGE_RESIZE, new ButtonInfo(EmbroideryDraw.TOOL_IMAGE_RESIZE, btnImageToolResize, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_IMAGE_ROTATE, new ButtonInfo(EmbroideryDraw.TOOL_IMAGE_ROTATE, btnImageToolRotate, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_FILL_COLOR, new ButtonInfo(EmbroideryDraw.TOOL_FILL_COLOR, btnFillInColorTool, false, false, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_COMPLEX_PATTERN, new ButtonInfo(EmbroideryDraw.TOOL_COMPLEX_PATTERN, btnComplexPatterns, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.TOOL_MAGNIFYGLASS, new ButtonInfo(EmbroideryDraw.TOOL_MAGNIFYGLASS, btnDesignToolMagifyingGlass, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.TOOL_DEBUG, new ButtonInfo(EmbroideryDraw.TOOL_DEBUG, btnDesignToolDEBUG, false, true, true));

        // Operation buttons (not tool buttons) a.k.a. JButton types.
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_NEW, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_NEW, btnImageToolLoad, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_RESTORE_ALL, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_RESTORE_ALL, btnImageToolRestoreAll, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_ONOFF, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_ONOFF, btnImageToolImagesOnOff, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_LIGHTENING_ONOFF, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_LIGHTENING_ONOFF, btnImageToolLighteningOnOff, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_SET_LIGHTHENING, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_SET_LIGHTHENING, btnImageToolSetLightening, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_IMAGE_REMOVE_ALL, new ButtonInfo(EmbroideryDraw.NONTOOL_IMAGE_REMOVE_ALL, btnImageToolRemoveAll, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_CONVERSION_PEMV4, new ButtonInfo(EmbroideryDraw.NONTOOL_CONVERSION_PEMV4, btnConversionToolPemV4, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_CONVERSION_BITMAP, new ButtonInfo(EmbroideryDraw.NONTOOL_CONVERSION_BITMAP, btnConversionToolBitmap, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_DESIGN_SNAP_TO_GRID, new ButtonInfo(EmbroideryDraw.NONTOOL_DESIGN_SNAP_TO_GRID, btnSnapToGrid, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_DESIGN_SHOW_GRID, new ButtonInfo(EmbroideryDraw.NONTOOL_DESIGN_SHOW_GRID, btnShowGrid, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_DESIGN_SHOW_CONTROL_PTS, new ButtonInfo(EmbroideryDraw.NONTOOL_DESIGN_SHOW_CONTROL_PTS, btnShowControlPoints, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_NEW, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_NEW, btnStandardToolNew, true, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_OPEN, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_OPEN, btnStandardToolOpen, true, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_SAVE, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_SAVE, btnStandardToolSave, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_CUT, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_CUT, btnStandardToolCut, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_COPY, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_COPY, btnStandardToolCopy, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_PASTE_CURRENT_LAYER, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_PASTE_CURRENT_LAYER, btnStandardToolPasteCurrentLayer, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_PASTE_NEW_LAYER, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_PASTE_NEW_LAYER, btnStandardToolPasteNewLayer, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_NEW_DESIGN, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_NEW_DESIGN, btnStandardToolPasteNewDesign, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_UNDO, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_UNDO, btnStandardToolUndo, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STANDARD_REDO, new ButtonInfo(EmbroideryDraw.NONTOOL_STANDARD_REDO, btnStandardToolRedo, false, false, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STAGE_VECTOR, new ButtonInfo(EmbroideryDraw.NONTOOL_STAGE_VECTOR, btnFillInVector, false, false, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_STAGE_COLOR, new ButtonInfo(EmbroideryDraw.NONTOOL_STAGE_COLOR, btnFillInColor, false, true, false));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_ZOOMVIEW, new ButtonInfo(EmbroideryDraw.NONTOOL_ZOOMVIEW, comboBoxZoom, false, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_GENERATOR_FSL, new ButtonInfo(EmbroideryDraw.NONTOOL_GENERATOR_FSL, btnGenerateFreeStandingLace, true, true, true));
        hmButtonInfo.put(EmbroideryDraw.NONTOOL_GENERATOR_DOLLIE, new ButtonInfo(EmbroideryDraw.NONTOOL_GENERATOR_DOLLIE, btnGenerateDollie, true, true, true));


    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Menu Item Setup ">
    /** This will setup the menu edit.
     */
    private void setupMenuEdit() {

        // Add the menu items to the edit menu.
        menuEdit.add(undoEditMenu);
        menuEdit.add(redoEditMenu);
        menuEdit.addSeparator();
        menuEdit.add(cutEditMenu);
        menuEdit.add(copyEditMenu);
        menuEdit.addSeparator();
        menuEdit.add(pasteIntoCurrentLayerEditMenu);
        menuEdit.add(pasteIntoNewLayerEditMenu);
        menuEdit.add(pasteAsNewDesignEditMenu);
        menuEdit.addSeparator();
        menuEdit.add(settingsEditMenu);

        // Add the menu action listeners.
        undoEditMenu.addActionListener(editMenuListener);
        redoEditMenu.addActionListener(editMenuListener);
        cutEditMenu.addActionListener(editMenuListener);
        copyEditMenu.addActionListener(editMenuListener);
        pasteIntoCurrentLayerEditMenu.addActionListener(editMenuListener);
        pasteIntoNewLayerEditMenu.addActionListener(editMenuListener);
        pasteAsNewDesignEditMenu.addActionListener(editMenuListener);
        settingsEditMenu.addActionListener(editMenuListener);

        // Setup the accelerators.
        undoEditMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        redoEditMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        copyEditMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        cutEditMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        pasteIntoCurrentLayerEditMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));


        // Setup the menu file.
        menuEdit.setMnemonic('E');
        menuEdit.addMenuListener(editMenuListener);
        menuBar.add(menuEdit);
    }

    /** This will setup the menu view.
     */
    private void setupMenuView() {

        ltViewMenuZoom.add(zoomDrawingViewMenu);
        ltViewMenuZoom.add(zoomLayerViewMenu);
        ltViewMenuZoom.add(zoom800ViewMenu);
        ltViewMenuZoom.add(zoom400ViewMenu);
        ltViewMenuZoom.add(zoom200ViewMenu);
        ltViewMenuZoom.add(zoom100ViewMenu);
        ltViewMenuZoom.add(zoom75ViewMenu);
        ltViewMenuZoom.add(zoom50ViewMenu);
        ltViewMenuZoom.add(zoom25ViewMenu);

        menuView.add(zoomDrawingViewMenu);
        menuView.add(zoomLayerViewMenu);
        menuView.addSeparator();
        menuView.add(zoom800ViewMenu);
        menuView.add(zoom400ViewMenu);
        menuView.add(zoom200ViewMenu);
        menuView.add(zoom100ViewMenu);
        menuView.add(zoom75ViewMenu);
        menuView.add(zoom50ViewMenu);
        menuView.add(zoom25ViewMenu);
        menuView.addSeparator();
        menuView.add(snapToGridMenu);
        menuView.add(gridViewMenu);
        menuView.add(showControlPointsMenu);
        menuView.addSeparator();
        menuView.add(changeGridViewMenu);
        menuView.add(calibrateDrawingMenu);
        

        // Add the menu action listeners.
        zoomDrawingViewMenu.addActionListener(viewMenuListener);
        zoomLayerViewMenu.addActionListener(viewMenuListener);
        zoom800ViewMenu.addActionListener(viewMenuListener);
        zoom400ViewMenu.addActionListener(viewMenuListener);
        zoom200ViewMenu.addActionListener(viewMenuListener);
        zoom100ViewMenu.addActionListener(viewMenuListener);
        zoom75ViewMenu.addActionListener(viewMenuListener);
        zoom50ViewMenu.addActionListener(viewMenuListener);
        zoom25ViewMenu.addActionListener(viewMenuListener);
        gridViewMenu.addActionListener(viewMenuListener);
        snapToGridMenu.addActionListener(viewMenuListener);
        showControlPointsMenu.addActionListener(viewMenuListener);
        changeGridViewMenu.addActionListener(viewMenuListener);
        calibrateDrawingMenu.addActionListener(viewMenuListener);

        // Setup the menu file.
        menuView.setMnemonic('V');
        menuBar.add(menuView);
    }

    /** This will setup the menu conversion.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private void setupMenuConversion() {
        updateBitmapSettings(true);

        // Add the menu items to the conversion menu.
        menuConversion.add(pemv4ConversionMenu);
        menuConversion.add(pemSettingsConversionMenu);
        menuConversion.addSeparator();
        menuConversion.add(bitmapConversionMenu);
//        menuConversion.add(emfConversionMenu);
        menuConversion.add(bitmapSettingsMenu);

        // Add the menu action listeners.
        pemv4ConversionMenu.addActionListener(conversionMenuListener);
        pemSettingsConversionMenu.addActionListener(conversionMenuListener);
        bitmapConversionMenu.addActionListener(conversionMenuListener);
        //       emfConversionMenu.addActionListener(conversionMenuListener);

        // Setup the accelerators.
        pemv4ConversionMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));

        // Setup the menu file.
        menuConversion.setMnemonic('C');
        menuBar.add(menuConversion);
    }

    /** This will update the menu bitmap settings dialog.
     * @param firstTime is true if this is the initial run of this function.
     */
    private void updateBitmapSettings(boolean firstTime) {
        // Add the menu items to the conversion menu.
        bitmapSettingsMenu.removeAll();

        int index = BitmapSettings.getIndexCurrentSetting();

        // Create the list of JMenuItems from the BitmapSettings.
        ltBitmapSettings.clear();
        int i = 0;
        for (Iterator itr = BitmapSettings.ltBitmapSettings.iterator(); itr.hasNext(); i++) {
            BitmapSettings bitSettings = (BitmapSettings) itr.next();

            // See if it needs to be checked.
            boolean checked = false;
            if (index == i)
                checked = true;
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(bitSettings.getName(), checked);

            ltBitmapSettings.add(menuItem);
            bitmapSettingsMenu.add(menuItem);
            menuItem.addActionListener(conversionMenuListener);
        }

        // the seperator and settings to the menu.
        bitmapSettingsMenu.addSeparator();
        bitmapSettingsMenu.add(settingsBitmapSettingsMenu);

        // Add the menu action listeners.
        if (firstTime)
            settingsBitmapSettingsMenu.addActionListener(conversionMenuListener);
    }

    /** This will setup the menu file. It should be the first one and only called once.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private static final char KEY_NUMBERS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private void setupMenuFile() {
        // Add the menu items to the file menu.
        menuFile.add(newFileMenu);
        menuFile.add(openFileMenu);
        menuFile.add(closeFileMenu);
        menuFile.add(closeAllFileMenu);
        menuFile.addSeparator();
        menuFile.add(saveFileMenu);
        menuFile.add(saveAsFileMenu);
        menuFile.add(saveAllFileMenu);
        menuFile.addSeparator();
        menuFile.add(printFileMenu);
        menuFile.addSeparator();
        fMenuList.setupFileList(menuFile);
        menuFile.addSeparator();
        menuFile.add(exitFileMenu);

        // Add the menu action listeners.
        newFileMenu.addActionListener(fileMenuListener);
        openFileMenu.addActionListener(fileMenuListener);
        closeFileMenu.addActionListener(fileMenuListener);
        closeAllFileMenu.addActionListener(fileMenuListener);
        saveFileMenu.addActionListener(fileMenuListener);
        saveAsFileMenu.addActionListener(fileMenuListener);
        saveAllFileMenu.addActionListener(fileMenuListener);
        printFileMenu.addActionListener(fileMenuListener);
        exitFileMenu.addActionListener(fileMenuListener);

        // Setup the accelerators.
        openFileMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        printFileMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));

        // Setup the menu file.
        menuFile.setMnemonic('F');
        menuFile.addMenuListener(fileMenuListener);
        menuBar.add(menuFile);
    }

    /** This will setup the menu image.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private void setupMenuImage() {
        // Add the menu items to the conversion menu.
        menuImage.add(loadImageMenu);
        menuImage.addSeparator();
        menuImage.add(removeAllImageMenu);
        menuImage.addSeparator();
//        menuImage.add(rotateImageMenu);
//        menuImage.add(scaleImageMenu);
//        menuImage.add(moveImageMenu);
//        menuImage.addSeparator();
//        menuImage.add(deleteImageMenu);
//        menuImage.addSeparator();
        menuImage.add(turnImagesOnOffImageMenu);
        menuImage.add(turnLighteningOnOffImageMenu);
        menuImage.addSeparator();
        menuImage.add(setLighteningImageMenu);
        menuImage.addSeparator();
        menuImage.add(restoreAllImageMenu);

        // Add the menu action listeners.
        loadImageMenu.addActionListener(imageMenuListener);
        removeAllImageMenu.addActionListener(imageMenuListener);
        deleteImageMenu.addActionListener(imageMenuListener);
        rotateImageMenu.addActionListener(imageMenuListener);
        scaleImageMenu.addActionListener(imageMenuListener);
        moveImageMenu.addActionListener(imageMenuListener);
        restoreAllImageMenu.addActionListener(imageMenuListener);
        turnImagesOnOffImageMenu.addActionListener(imageMenuListener);
        turnLighteningOnOffImageMenu.addActionListener(imageMenuListener);
        setLighteningImageMenu.addActionListener(imageMenuListener);

        // Setup the accelerators.
        turnImagesOnOffImageMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        turnLighteningOnOffImageMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));

        // Defualt is on for the lightening image option.
        turnImagesOnOffImageMenu.setSelected(true);

        // Setup the menu file.
        menuImage.setMnemonic('I');
        menuBar.add(menuImage);
    }

    /** This will setup the menu design.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private void setupMenuDesign() {
        // Add the menu items to the conversion menu.
        menuDesign.add(resizeDesignMenu);
        menuDesign.addSeparator();
        menuDesign.add(detailsDesignMenu);
        menuDesign.addSeparator();
        menuDesign.add(statsDesignMenu);

        // Add the menu action listeners.
        resizeDesignMenu.addActionListener(designMenuListener);
        detailsDesignMenu.addActionListener(designMenuListener);
        statsDesignMenu.addActionListener(designMenuListener);

        // Setup the menu file.
        menuDesign.setMnemonic('D');
        menuBar.add(menuDesign);
    }

    /** This will setup the menu pattern.
     */
    private void setupMenuPatternTools() {
        menuPatternTools.add(showDirectoryPatternMenu);
        menuPatternTools.add(reloadPatternsMenu);

        // Add the menu action listeners.
        showDirectoryPatternMenu.addActionListener(patternMenuListener);
        reloadPatternsMenu.addActionListener(patternMenuListener);

        // Setup the menu file.
        menuPatternTools.setMnemonic('P');
        menuBar.add(menuPatternTools);
    }

    /** This will setup the menu layer.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private void setupMenuLayer() {
        JMenu menuLayer = new JMenu("Layer");

        // Add the menu items to the conversion menu.
        menuLayer.add(newLayerMenu);
        menuLayer.addSeparator();
        // menuLayer.add(selectLayerMenu);
        // menuLayer.addSeparator();
        menuLayer.add(mergeTwoLayerMenu);
        menuLayer.add(mergeAllLayersToMasterLayerMenu);
        menuLayer.addSeparator();
        menuLayer.add(deleteLayerMenu);
        menuLayer.add(deleteAllLayerMenu);
        menuLayer.addSeparator();
        menuLayer.add(layersOnOffLayerMenu);
        menuLayer.addSeparator();
        menuLayer.add(updateInfoLayerMenu);

        // Add the menu action listeners.
        newLayerMenu.addActionListener(layerMenuListener);
        deleteLayerMenu.addActionListener(layerMenuListener);
        deleteAllLayerMenu.addActionListener(layerMenuListener);
        updateInfoLayerMenu.addActionListener(layerMenuListener);
        layersOnOffLayerMenu.addActionListener(layerMenuListener);
        mergeTwoLayerMenu.addActionListener(layerMenuListener);
        mergeAllLayersToMasterLayerMenu.addActionListener(layerMenuListener);
        selectLayerMenu.addActionListener(layerMenuListener);

        // Setup the menu file.
        menuLayer.setMnemonic('L');
        menuBar.add(menuLayer);
    }

    /** This will setup the stage mode menu.
     */
    private void setupMenuStage() {
        if (DEBUG_MODE == false)
            return;

        // Add the menu items to the stage menu.
        menuStage.add(vectorStateMenu);
        menuStage.add(colorFillStateMenu);

        // Add the menu action listeners.
        vectorStateMenu.addActionListener(stageMenuListener);
        colorFillStateMenu.addActionListener(stageMenuListener);

        // Setup the accelerators.
        vectorStateMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        colorFillStateMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));

        // Setup the menu stage.
        menuStage.setMnemonic('S');
        menuBar.add(menuStage);
    }

    /** This will setup the menu Help.
     */
    private void setupMenuHelp() {
        // Add the menu items to the conversion menu.
        menuHelp.add(helpHelpMenu);
        menuHelp.addSeparator();
        menuHelp.add(websiteHelpMenu);
        menuHelp.addSeparator();
        menuHelp.add(aboutHelpMenu);

        // Add the menu action listeners.
        aboutHelpMenu.addActionListener(helpMenuListener);
        websiteHelpMenu.addActionListener(helpMenuListener);
        helpHelpMenu.addActionListener(helpMenuListener);

        // Setup the accelerators.
        helpHelpMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

        // Setup the menu file.
        menuHelp.setMnemonic('H');
        menuBar.add(menuHelp);
    }

    /** This will setup the menu Debug.
     * @param doActionListeners true if it should add the menu action listeners, else false do
     * not add the action listeners.
     */
    private void setupMenuDebug() {
        if (DEBUG_MODE == false)
            return;

        // Add the menu items to the conversion menu.
        menuDebug.add(printDesignDebugMenu);
        menuDebug.add(printUndoListDebugMenu);
        menuDebug.add(printFileDesignDebugMenu);
        menuDebug.add(printFileUndoListDebugMenu);
        menuDebug.add(rmolnarTensionDebugMenu);
        menuDebug.add(clearFileOpenListDebugMenu);
        menuDebug.add(distanceBetweenPointDebugMenu);
        menuDebug.add(showNumberDesignDebugMenu);
        menuDebug.add(toggleColorModeDebugMenu);
        menuDebug.addSeparator();
        menuDebug.add(removeProgramKeyDebugMenu);
        menuDebug.add(setTrialDaysDebugMenu);

        // Add the menu action listeners.
        printDesignDebugMenu.addActionListener(debugMenuListener);
        printUndoListDebugMenu.addActionListener(debugMenuListener);
        printFileDesignDebugMenu.addActionListener(debugMenuListener);
        printFileUndoListDebugMenu.addActionListener(debugMenuListener);
        rmolnarTensionDebugMenu.addActionListener(debugMenuListener);
        clearFileOpenListDebugMenu.addActionListener(debugMenuListener);
        distanceBetweenPointDebugMenu.addActionListener(debugMenuListener);
        showNumberDesignDebugMenu.addActionListener(debugMenuListener);
        removeProgramKeyDebugMenu.addActionListener(debugMenuListener);
        setTrialDaysDebugMenu.addActionListener(debugMenuListener);
        toggleColorModeDebugMenu.addActionListener(debugMenuListener);

        // Setup the menu file.
        menuDebug.setMnemonic('G');
        menuBar.add(menuDebug);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" JMenuBar Setup ">
    private void setupToolbarFillColor() {
        if (DEBUG_MODE == false)
            return;

        JToolBar fillColorBar = topBar;

        fillColorBar.addSeparator();
        fillColorBar.add(btnFillInVector);
        fillColorBar.add(btnFillInColor);
        fillColorBar.addSeparator();
        fillColorBar.add(btnFillInColorTool);

        btnFillInVector.setToolTipText("Vector Edit Mode");
        btnFillInColor.setToolTipText("Color Fill-In Edit Mode");
        btnFillInColorTool.setToolTipText("Color Fill-In Tool");

        btnFillInVector.addActionListener(barColorFillListener);
        btnFillInColor.addActionListener(barColorFillListener);
        btnFillInColorTool.addActionListener(barColorFillListener);
    }

    /** This will setup the toolbar generators.
     */
    private void setupToolbarGenerators() {
        JToolBar designBar = bottomBar;

        designBar.addSeparator();
        designBar.add(btnGenerateFreeStandingLace);
        designBar.add(btnGenerateDollie);

        btnGenerateFreeStandingLace.setToolTipText("This will generate free standing lace patterns.");
        btnGenerateDollie.setToolTipText("This will generate doily patterns.");
        
        btnGenerateFreeStandingLace.addActionListener(barGeneratorListner);
        btnGenerateDollie.addActionListener(barGeneratorListner);
    }

    /** This will setup the toolbar design.
     */
    private void setupToolbarDesign() {
        JToolBar designBar = bottomBar;

        if (DEBUG_MODE) {
            designBar.add(btnDesignToolDEBUG);
            btnDesignToolDEBUG.setToolTipText("DEBUG TOOL");
            btnDesignToolDEBUG.addActionListener(barDesignListner);
        }

        designBar.add(btnDesignToolRectangle);
        designBar.add(btnDesignToolCircle);
        designBar.add(btnDesignToolLine);
        designBar.add(btnDesignToolBezier);
        designBar.add(btnDesignToolRMolnar);
        designBar.add(btnComplexPatterns);
        designBar.addSeparator();
        designBar.add(btnDesignToolDelete);
        designBar.add(btnDesignToolAddPoint);
        designBar.add(btnDesignToolPullLine);
        designBar.add(btnDesignToolConnectRMolnar);
        designBar.add(btnDesignToolBezierControl);
        designBar.add(btnDesignToolMoverControl);
        designBar.addSeparator();
        designBar.add(btnDesignToolMagifyingGlass);
        designBar.add(comboBoxZoom);
        designBar.addSeparator();
        designBar.add(btnDesignToolSelect);
        designBar.add(btnDesignToolTranslate);
        designBar.add(btnDesignToolRotate);
        designBar.add(btnDesignToolResize);
        designBar.add(btnDesignToolMirror);

        btnDesignToolCircle.setToolTipText("Draw Circle");
        btnDesignToolRectangle.setToolTipText("Draw Rectangle");
        btnDesignToolLine.setToolTipText("Draw Line");
        btnDesignToolBezier.setToolTipText("Draw Bezier");
        btnDesignToolRMolnar.setToolTipText("Draw Curve");
        btnComplexPatterns.setToolTipText("Draw Pattern");
        btnDesignToolDelete.setToolTipText("Delete Items");
        btnDesignToolAddPoint.setToolTipText("Add A Point to a Line");
        btnDesignToolPullLine.setToolTipText("Pull Line Apart");
        btnDesignToolConnectRMolnar.setToolTipText("(De)Connect Curve");
        btnDesignToolMagifyingGlass.setToolTipText("Zoom In or Out");
        btnDesignToolBezierControl.setToolTipText("Adjust Bezier Points");
        btnDesignToolMoverControl.setToolTipText("Simple Mover");
        btnDesignToolSelect.setToolTipText("Select Items");
        btnDesignToolTranslate.setToolTipText("Mover");
        btnDesignToolRotate.setToolTipText("Rotate");
        btnDesignToolResize.setToolTipText("Resize");
        btnDesignToolMirror.setToolTipText("Mirror");

        btnDesignToolCircle.addActionListener(barDesignListner);
        btnDesignToolRectangle.addActionListener(barDesignListner);
        btnDesignToolLine.addActionListener(barDesignListner);
        btnDesignToolBezier.addActionListener(barDesignListner);
        btnDesignToolRMolnar.addActionListener(barDesignListner);
        btnComplexPatterns.addActionListener(barDesignListner);
        btnDesignToolDelete.addActionListener(barDesignListner);
        btnDesignToolAddPoint.addActionListener(barDesignListner);
        btnDesignToolPullLine.addActionListener(barDesignListner);
        btnDesignToolConnectRMolnar.addActionListener(barDesignListner);
        btnDesignToolMagifyingGlass.addActionListener(barDesignListner);
        //comboBoxZoom.addActionListener(barDesignListner);
        btnDesignToolBezierControl.addActionListener(barDesignListner);
        btnDesignToolMoverControl.addActionListener(barDesignListner);
        btnDesignToolSelect.addActionListener(barDesignListner);
        btnDesignToolTranslate.addActionListener(barDesignListner);
        btnDesignToolRotate.addActionListener(barDesignListner);
        btnDesignToolResize.addActionListener(barDesignListner);
        btnDesignToolMirror.addActionListener(barDesignListner);

    // panelToolBars.add(designBar);
    }

    /** This will setup the toolbar image.
     */
    private void setupToolbarImage() {
        JToolBar imageBar = topBar;
        imageBar.add(btnImageToolLoad);
        imageBar.add(btnImageToolDelete);
        imageBar.add(btnImageToolRotate);
        imageBar.add(btnImageToolResize);
        imageBar.add(btnImageToolMove);
        imageBar.add(btnImageToolCenter);
        imageBar.add(btnImageToolRestoreAll);
        imageBar.add(btnImageToolRemoveAll);
        imageBar.add(btnImageToolImagesOnOff);
        imageBar.add(btnImageToolLighteningOnOff);
        imageBar.add(btnImageToolSetLightening);

        btnImageToolLoad.setToolTipText("Load Image");
        btnImageToolDelete.setToolTipText("Delete Image");
        btnImageToolRotate.setToolTipText("Rotate Image");
        btnImageToolResize.setToolTipText("Resize Image");
        btnImageToolMove.setToolTipText("Move Image");
        btnImageToolRestoreAll.setToolTipText("Restore All Image(s) To Original Size");
        btnImageToolRemoveAll.setToolTipText("Remove All Image(s)");
        btnImageToolImagesOnOff.setToolTipText("Images On/Off");
        btnImageToolLighteningOnOff.setToolTipText("Image Lightening On/Off");
        btnImageToolSetLightening.setToolTipText("Image Settings");
        btnImageToolCenter.setToolTipText("Center Image");

        btnImageToolLoad.addActionListener(barImageListener);
        btnImageToolDelete.addActionListener(barImageListener);
        btnImageToolRotate.addActionListener(barImageListener);
        btnImageToolResize.addActionListener(barImageListener);
        btnImageToolMove.addActionListener(barImageListener);
        btnImageToolRestoreAll.addActionListener(barImageListener);
        btnImageToolImagesOnOff.addActionListener(barImageListener);
        btnImageToolLighteningOnOff.addActionListener(barImageListener);
        btnImageToolSetLightening.addActionListener(barImageListener);
        btnImageToolRemoveAll.addActionListener(barImageListener);
        btnImageToolCenter.addActionListener(barImageListener);

        btnImageToolImagesOnOff.setSelected(true);
    // panelToolBars.add(imageBar);
    }

    /** This will setup the toolbar conversion.
     */
    private void setupToolbarConversion() {
        //JToolBar conversionBar = topBar;
        //conversionBar.add(btnConversionToolPemV4);
        //conversionBar.add(btnConversionToolToolDesign);
        //conversionBar.add(btnConversionToolBitmap);

        btnConversionToolPemV4.setToolTipText("Save Design As PEM File.");
        btnConversionToolToolDesign.setToolTipText("Save Design As Custom Tool.");
        btnConversionToolBitmap.setToolTipText("Save Design As Bitmap.");
        //btnConversionToolEMF.setToolTipText("Save Design AS EMF");

        btnConversionToolPemV4.addActionListener(barStandardListener);
        btnConversionToolToolDesign.addActionListener(barStandardListener);
        btnConversionToolBitmap.addActionListener(barStandardListener);
    //btnConversionToolEMF.addActionListener(barConversionListener);
    // panelToolBars.add(conversionBar);
    }

    /** This will setup the toolbar design operations.
     */
    private void setupToolBarDesignOperations() {
        JToolBar bar = topBar;

        btnSnapToGrid.setToolTipText("Snap To Grid");
        btnShowGrid.setToolTipText("Show Grid");
        btnShowControlPoints.setToolTipText("Show Points");

        btnShowControlPoints.setSelected(true);
        btnShowGrid.setSelected(true);

        bar.add(btnSnapToGrid);
        bar.add(btnShowGrid);
        bar.add(btnShowControlPoints);
        bar.addSeparator();

        btnSnapToGrid.addActionListener(barDesignOperationsListener);
        btnShowGrid.addActionListener(barDesignOperationsListener);
        btnShowControlPoints.addActionListener(barDesignOperationsListener);
    }

    /** This will setup the toolbar standard.
     */
    private void setupToolbarStandard() {
        JToolBar standardBar = topBar;
        standardBar.add(btnStandardToolNew);
        standardBar.add(btnStandardToolOpen);
        standardBar.add(btnStandardToolSave);
        standardBar.add(btnConversionToolPemV4);
        standardBar.add(btnConversionToolBitmap);
        // standardBar.add(btnConversionToolEMF);
        standardBar.addSeparator();
        standardBar.add(btnStandardToolCut);
        standardBar.add(btnStandardToolCopy);
        standardBar.add(btnStandardToolPasteCurrentLayer);
        standardBar.add(btnStandardToolPasteNewLayer);
        standardBar.add(btnStandardToolPasteNewDesign);
        standardBar.addSeparator();
        standardBar.add(btnStandardToolUndo);
        standardBar.add(btnStandardToolRedo);
        standardBar.addSeparator();

        btnStandardToolNew.setToolTipText("New Drawing");
        btnStandardToolOpen.setToolTipText("Open Drawing");
        btnStandardToolSave.setToolTipText("Save Drawing");
        btnStandardToolCut.setToolTipText("Cut Selected Items");
        btnStandardToolCopy.setToolTipText("Copy Selected Items");
        btnStandardToolPasteCurrentLayer.setToolTipText("Paste Into Current Layer");
        btnStandardToolPasteNewLayer.setToolTipText("Paste Into New Layer");
        btnStandardToolPasteNewDesign.setToolTipText("Paste Into New Drawing");
        btnStandardToolUndo.setToolTipText("Undo");
        btnStandardToolRedo.setToolTipText("Redo");

        btnStandardToolNew.addActionListener(barStandardListener);
        btnStandardToolOpen.addActionListener(barStandardListener);
        btnStandardToolSave.addActionListener(barStandardListener);
        btnStandardToolCut.addActionListener(barStandardListener);
        btnStandardToolCopy.addActionListener(barStandardListener);
        btnStandardToolPasteCurrentLayer.addActionListener(barStandardListener);
        btnStandardToolPasteNewLayer.addActionListener(barStandardListener);
        btnStandardToolPasteNewDesign.addActionListener(barStandardListener);
        btnStandardToolUndo.addActionListener(barStandardListener);
        btnStandardToolRedo.addActionListener(barStandardListener);

    // panelToolBars.add(standardBar);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" File Menu Methods ">
    private void close() {
        tabbedDesignView.closeDesign();
    }

    private void closeAll() {
        tabbedDesignView.closeAllDesigns();
    }

    private void newFile() {
         DesignDocument document = EmbroideryDraw.getEmbroideryDraw().newFile();
        if (document != null)
            addDocument(document);
    }

    public void openFile() {
        DesignDocument document = EmbroideryDraw.getEmbroideryDraw().openFile();
        if (document != null)
            addDocument(document);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" GUI Methods ">
    /** This will change the gui to the new stage type.
     * @param guiStage is the new gui stage mode to set the gui to.
     */
    private void setGUIStage(int guiStage) {
        // Save the previous guiStage.
        int prevGuiStage = this.guiStage;

        // Already on that gui stage, do nothing.
        if (this.guiStage == guiStage)
            return;
        this.guiStage = guiStage;

        // Notify LayerPanel of the change in gui stages.
        panelLayer.setGUIStage(this.guiStage);

        // For each button defined, enable/disable it.
        for (Iterator itr = hmButtonInfo.values().iterator(); itr.hasNext();) {
            ButtonInfo button = (ButtonInfo) itr.next();
            button.setEnabled(guiStage);
        }

        // For each menu button defined, enable/disable it.
        for (Iterator itr = ltMenuButtonInfo.iterator(); itr.hasNext();) {
            ButtonInfo button = (ButtonInfo) itr.next();
            button.setEnabled(guiStage);
        }

        // Notify CopyCutPaste about change.
        copyCutPaste.onGuiStageChange(guiStage);

        // When changing stages see if the tool is compatible if not than change it.
        if (guiStage != GUISTAGE_EMPTY) {
            ButtonInfo bInfo = (ButtonInfo) hmButtonInfo.get(EmbroideryDraw.getEmbroideryDraw().getToolType());
            if (bInfo == null)
                throw new IllegalStateException("setGUIStage(" + guiStage + ") Unknown tool type: " + EmbroideryDraw.getEmbroideryDraw().getToolType());
            if (prevGuiStage == GUISTAGE_EMPTY)
                if (tabbedDesignView.getDesignDocument().getGuiStage() == GUISTAGE_VECTOR)
                    tabbedDesignView.newTool(EmbroideryDraw.TOOL_LINE);
                else
                    tabbedDesignView.newTool(EmbroideryDraw.TOOL_FILL_COLOR);
            else if (bInfo.isEnabled(guiStage) == false)
                if (tabbedDesignView.getDesignDocument().getGuiStage() == GUISTAGE_VECTOR)
                    tabbedDesignView.newTool(EmbroideryDraw.TOOL_LINE);
                else
                    tabbedDesignView.newTool(EmbroideryDraw.TOOL_FILL_COLOR);
        }
        
        // If the gui is empty make sure new tool is line.
        if (guiStage == GUISTAGE_EMPTY) {
            EmbroideryDraw.getEmbroideryDraw().newTool(EmbroideryDraw.TOOL_NONE, null);
        }
    }

    /** This will invalidate the grid, snap, and show menu/buttons.
     * Call this to have each item selected or deselected.
     */
    private void invalidateGridSnapShow() {
        // Set up the initial values for the menu items.
        gridViewMenu.setSelected(Measurement.isGridVisible());
        snapToGridMenu.setSelected(DrawingPad.isSnapToGrid());
        showControlPointsMenu.setSelected(Vertex.isControlPointsVisible());

        // Set up the initial values for the button items.
        btnShowGrid.setSelected(Measurement.isGridVisible());
        btnSnapToGrid.setSelected(DrawingPad.isSnapToGrid());
        btnShowControlPoints.setSelected(Vertex.isControlPointsVisible());
    }
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc=" Tool Option Methods ">
    public DeletePanel getDeleteOption() {
        return toolDeletePanel;
    }

    public AddPointPanel getAddPointOptions() {
        return toolAddPointPanel;
    }

    public SelectPanel getSelectOptions() {
        return toolSelectPanel;
    }

    public MirrorPanel getMirrorOptions() {
        return toolMirrorPanel;
    }

    public ResizePanel getResizeOptions() {
        return toolResizePanel;
    }

    public RotatePanel getRotateOptions() {
        return toolRotatePanel;
    }

    public TranslatePanel getTranslateOptions() {
        return toolTranslatePanel;
    }

    public BezierControlPanel getBezierControlOptions() {
        return toolBezierControlPanel;
    }

    public ConnectRMolnarPanel getConnectRMolnarOptions() {
        return toolConnectRMolnarPanel;
    }

    public PullLineApartPanel getPullLineApartOptions() {
        return toolPullLineApartPanel;
    }

    public ImagePanel getImageOptions() {
        return toolImagePanel;
    }

    public ColorPanel getColorOptions() {
        return toolColorPanel;
    }

    public PatternPanel getPatternOptions() {
        return toolPatternPanel;
    }

    public InternalPatternPanel getInternalPatternOptions() {
        return toolInternalPatternPanel;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Button Methods ">
    private void toggleViewMenuItem(String item) {
        // Untoggle all view menu items.
        for (Iterator itr = ltViewMenuZoom.iterator(); itr.hasNext();) {
            JRadioButtonMenuItem button = (JRadioButtonMenuItem) itr.next();
            button.setSelected(false);
        }

        if (item.equals(InterfaceFrameOperation.ITEM_DRAWING))
            zoomDrawingViewMenu.setSelected(true);
        else if (item.equals(InterfaceFrameOperation.ITEM_LAYER))
            zoomLayerViewMenu.setSelected(true);
        else if (item.equals(InterfaceFrameOperation.ITEM_100))
            zoom100ViewMenu.setSelected(true);
        else
            throw new IllegalArgumentException("toggleViewMenuItem(" + item + ") Unknown item to zoom on.");
    }

    /** This will toggle the view menu items.
     * @param percentage is the new view percentage.
     */
    private void toggleViewMenuItem(float percentage) {
        // Untoggle all view menu items.
        for (Iterator itr = ltViewMenuZoom.iterator(); itr.hasNext();) {
            JRadioButtonMenuItem button = (JRadioButtonMenuItem) itr.next();
            button.setSelected(false);
        }

        int iPercentage = (int) (percentage * 100.0f);
        if (iPercentage > 799 && iPercentage < 801)
            zoom800ViewMenu.setSelected(true);
        else if (iPercentage > 399 && iPercentage < 401)
            zoom400ViewMenu.setSelected(true);
        else if (iPercentage > 199 && iPercentage < 201)
            zoom200ViewMenu.setSelected(true);
        else if (iPercentage > 99 && iPercentage < 101)
            zoom100ViewMenu.setSelected(true);
        else if (iPercentage > 74 && iPercentage < 76)
            zoom75ViewMenu.setSelected(true);
        else if (iPercentage > 49 && iPercentage < 51)
            zoom50ViewMenu.setSelected(true);
        else if (iPercentage > 24 && iPercentage < 26)
            zoom25ViewMenu.setSelected(true);
    }

    /** This will toggle the previous tool button to off since it is not needed
     * now. In the case that the current tool is the previous tool button then it
     * will toggle the button to on.
     * @param toggleButton is the new button that is to be turned on.
     */
    private void toggleToolPreviousButton(JToggleButton toggleButton) {
        toggleButton.setSelected(true);

        // Check to make sure that the previous and current are not the same.
        if (toggleButton == togglePrevious)
            return;
        if (togglePrevious != null)
            togglePrevious.setSelected(false);
        togglePrevious = toggleButton;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" DesignDocument Methods ">
    /** This will add the DesignDocument to the DesignTabbedPane
     * @param document this is the DesignDocument to be added to the frame's DesignTabbedPane.
     */
    private void addDocument(DesignDocument document) {
        if (document == null)
            throw new IllegalArgumentException("addDocument:: document cannot be null.");
        if (getContentPane().isAncestorOf(panelEast) == false)
            getContentPane().add(panelEast, BorderLayout.EAST);
        tabbedDesignView.addDesignDocument(document);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Generate Methods ">
    private void generateFreeStandingLace() {
        // Get the current design document.
        DesignDocument current = tabbedDesignView.getDesignDocument();
        
        // Generate the FSL. If it returns an array, then these are DesignDocuments that need to be added.
        DesignDocument[] array = EmbroideryDraw.getEmbroideryDraw().generateFreeStandingLace(current);
        if (array == null)
            return;
        for (int i = 0; i < array.length; i++) {
            addDocument(array[i]);
        }
    }
    private void generateDollie() {
        // Get the current design document.
        DesignDocument current = tabbedDesignView.getDesignDocument();
        
        // Generate the Dollies. If it returns an array, then these are DesignDocuments that need to be added.
        DesignDocument[] array = EmbroideryDraw.getEmbroideryDraw().generateDollie(current);
        if (array == null)
            return;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null)
                continue;
            addDocument(array[i]);
        }
    }
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc=" Interface WindowListener ">
    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        // Attempt to close all designs.
        closeAll();

        // Exit only if there are no more files opened.
        if (tabbedDesignView.getTabCount() == 0) {
            System.exit(0);
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Interface InterfaceFrameOperation ">
    public void enableUndoable(boolean enable) {
        undoEditMenu.setEnabled(enable);
        btnStandardToolUndo.setEnabled(enable);
    }

    public void enableRedoable(boolean enable) {
        redoEditMenu.setEnabled(enable);
        btnStandardToolRedo.setEnabled(enable);
    }

    public void enableAdvanceTools(boolean enabled) {
        btnDesignToolTranslate.setEnabled(enabled);
        btnDesignToolRotate.setEnabled(enabled);
        btnDesignToolResize.setEnabled(enabled);
        btnDesignToolMirror.setEnabled(enabled);
    }

    public void enableCopyCut(boolean enabled) {
        cutEditMenu.setEnabled(enabled);
        copyEditMenu.setEnabled(enabled);
        btnStandardToolCopy.setEnabled(enabled);
        btnStandardToolCut.setEnabled(enabled);
    }

    public void enablePaste(boolean enableDesign, boolean enableNew) {

        pasteIntoCurrentLayerEditMenu.setEnabled(enableDesign);
        pasteIntoNewLayerEditMenu.setEnabled(enableDesign);
        btnStandardToolPasteCurrentLayer.setEnabled(enableDesign);
        btnStandardToolPasteNewLayer.setEnabled(enableDesign);

        pasteAsNewDesignEditMenu.setEnabled(enableNew);
        btnStandardToolPasteNewDesign.setEnabled(enableNew);
    }

    public void validateLayerPanel() {
        panelLayer.validate();
    }

    public void validateImagePanel() {
        toolImagePanel.setDrawingPad(tabbedDesignView.getCurrentDrawingPad());
        toolImagePanel.validate();

        // Toggle the buttons and menu items.
        btnImageToolImagesOnOff.setSelected(DrawingPad.isImagesOnOff());
        btnImageToolLighteningOnOff.setSelected(DrawingPad.isImageLighten());
        turnImagesOnOffImageMenu.setSelected(DrawingPad.isImagesOnOff());
        turnLighteningOnOffImageMenu.setSelected(DrawingPad.isImageLighten());
    }

    public JFrame getFrame() {
        return this;
    }

    public void notifyDocumentChanged(boolean changed) {
        tabbedDesignView.notifyDocumentChanged(changed);
    }

    public void notifyDocumentFocus(DesignDocument document) {
        if (document != null) {
            // Notify layer about the change.
            panelLayer.setDocument(document);
            panelLayer.validate();

            // Notify zoom about the change.
            comboBoxZoom.setZoom(tabbedDesignView.getZoom());

            // Update gui based on gui stage and current stage.
            setGUIStage(document.getGuiStage());

            document.notifyDocumentFocus();
        } else
            // No more documents.
            setGUIStage(GUISTAGE_EMPTY);
    }

    public void notifyToolChanged() {
        // Remove the old tool's panel from the eastPanel.
        int count = panelEast.getComponentCount();
        if (count == 2) {
            // Remove the tool's option panel.
            for (int i = 0; i < count; i++) {
                if (panelEast.getComponent(i) != panelLayer)
                    panelEast.remove(i);
            }

            panelEast.updateUI();
        }

        // Make sure buttons are selected correctly. Only one tool can be selected at a time. Since the tool can be selected
        // within the source code (not selected by user) there needs to be a way to make sure it is correct.
        ButtonInfo bInfo = (ButtonInfo) hmButtonInfo.get(EmbroideryDraw.getEmbroideryDraw().getPreviousToolType());
        bInfo.setSelected(false);
        bInfo = (ButtonInfo) hmButtonInfo.get(EmbroideryDraw.getEmbroideryDraw().getToolType());
        bInfo.setSelected(true);

        // Add the tool's panel to the eastPanel. toolType is not used because it could be EmbroideryDraw.TOOL_CURRENT
        panelOptions.removeAll();
        int newToolType = EmbroideryDraw.getEmbroideryDraw().getToolType();
        switch (newToolType) {
            case EmbroideryDraw.TOOL_DEBUG:
            case EmbroideryDraw.TOOL_MAGNIFYGLASS:
            case EmbroideryDraw.TOOL_LINE:
            case EmbroideryDraw.TOOL_BEZIER:
            case EmbroideryDraw.TOOL_RMOLNAR:
            case EmbroideryDraw.TOOL_NONE:
                // Tools with no panel
                break;
            case EmbroideryDraw.TOOL_CIRCLE:
            case EmbroideryDraw.TOOL_SQUARE:
                panelOptions.add(toolInternalPatternPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_COMPLEX_PATTERN:
                panelOptions.add(toolPatternPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_DELETE:
//                toolDeletePanel.setMaximumSize(toolDeletePanel.getPreferredSize());
                panelOptions.add(toolDeletePanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_ADDPOINT:
                panelOptions.add(toolAddPointPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_SIMPLEMOVER:
                panelOptions.add(toolSelectPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_CONNECTRMOLNAR:
                panelOptions.add(toolConnectRMolnarPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_BEZIERCONTROL:
                panelOptions.add(toolBezierControlPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_PULL_LINEAPART:
                panelOptions.add(toolPullLineApartPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_SELECT:
                panelOptions.add(toolSelectPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_ROTATE:
                panelOptions.add(toolRotatePanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_RESIZE:
                panelOptions.add(toolResizePanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_TRANSLATE:
                panelOptions.add(toolTranslatePanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_MIRROR:
                panelOptions.add(toolMirrorPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_IMAGE_CENTER:
            case EmbroideryDraw.TOOL_IMAGE_DELETE:
            case EmbroideryDraw.TOOL_IMAGE_MOVER:
            case EmbroideryDraw.TOOL_IMAGE_RESIZE:
            case EmbroideryDraw.TOOL_IMAGE_ROTATE:
                panelOptions.add(toolImagePanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            case EmbroideryDraw.TOOL_FILL_COLOR:
                panelOptions.add(toolColorPanel, BorderLayout.NORTH);
                panelEast.add(panelOptions, BorderLayout.CENTER);
                break;
            default:
                throw new UnsupportedOperationException("FrameEmbroideryDraw.notifyToolChange() unknown tool type: " + newToolType);
        }

        panelEast.updateUI();

    }

    public void setZoom(float percentage) {
        tabbedDesignView.setZoom(percentage);
        comboBoxZoom.setZoom(percentage);
        toggleViewMenuItem(percentage);
    }

    public void zoomItem(String item) {
        float percentage = tabbedDesignView.setZoom(item);
        comboBoxZoom.setZoom(percentage);
        toggleViewMenuItem(item);
    }

    public FileMenuList getFileMenuList() {
        return fMenuList;
    }

    public int getStage() {
        return guiStage;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Interface InterfaceFileMenuList ">
    /** This function is called also when starting the program up and the program received a file to load.
     * Also, if the program is already running and it receives a file to load from another instance of Embroidery Draw 2.
     * It will also load that file from here. So because of this, it needs to make sure the window is focused.
     */
    public void fileMenuListOpen(File f) {
        fOpen = f;

        // The reason this is executed later is because this function can be called from outside the event-dispatching thread.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (!isFocused())
                    toFront();

                DesignDocument document = EmbroideryDraw.getEmbroideryDraw().openFile(fOpen);
                if (document != null)
                    addDocument(document);
            }

        });
    }
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc=" Listeners - Menu Items (Menu Bar on Frame) ">
    // <editor-fold defaultstate="collapsed" desc=" Class ConversionMenuListen Menu">
    class ConversionMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object obj = evt.getSource();
            tabbedDesignView.finalizeAdvanceSelectTools();

            if (obj == pemSettingsConversionMenu)
                EmbroideryDraw.getEmbroideryDraw().pemSettings();
            else if (obj == pemv4ConversionMenu)
                tabbedDesignView.exportDocumentAsPEM();
            else if (obj == settingsBitmapSettingsMenu) {
                EmbroideryDraw.getEmbroideryDraw().onSettingsBitmap();
                updateBitmapSettings(false);
            } else if (obj == bitmapConversionMenu)
                tabbedDesignView.exportDocumentAsBitmap();
            else {
                // See if it is one of the bitmap settings changed.
                int i = 0;
                for (Iterator itr = ltBitmapSettings.iterator(); itr.hasNext(); i++) {
                    JMenuItem menuItem = (JMenuItem) itr.next();
                    if (obj == menuItem) {
                        BitmapSettings.setIndexCurrentSetting(i);
                        BitmapSettings.getSettings().save();
                        menuItem.setSelected(true);
                    } else
                        menuItem.setSelected(false);
                }
            }
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class FileMenuListen Menu">
    class FileMenuListen implements ActionListener, MenuListener {

        public void actionPerformed(ActionEvent evt) {
            Object obj = evt.getSource();

            tabbedDesignView.finalizeAdvanceSelectTools();

            if (obj == exitFileMenu) {
                closeAll();

                // Exit only if there are no more files opened.
                if (tabbedDesignView.getTabCount() == 0) {
                    System.exit(0);
                }
            } else if (obj == newFileMenu)
                newFile();
            else if (obj == openFileMenu)
                openFile();
            else if (obj == saveFileMenu)
                tabbedDesignView.writeDocument(false);
            else if (obj == printFileMenu)
                tabbedDesignView.printDocument();
            else if (obj == closeFileMenu)
                close();
            else if (obj == closeAllFileMenu)
                closeAll();
            else if (obj == saveAsFileMenu)
                tabbedDesignView.writeDocument(true);
            else if (obj == saveAllFileMenu)
                tabbedDesignView.writeAllDocuments();

        /*
        for (int i=0; i < openFilesMenu.length; i++) {
        if (obj == openFilesMenu[i]) {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        String absolutePath = prefs.get(LIST_OPEN_FILE + i, null);
        if (absolutePath == null) {
        JOptionPane message = new JOptionPane();
        message.showMessageDialog(null, "Unknown java error. Unable to open the Preferences.");
        break;
        }
        
        // Open a design.
        openFile(embroideryDraw.onOpenFile(absolutePath));
        }
        }
         **/
        }

        public void menuSelected(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class EditMenuListen Menu">
    class EditMenuListen implements ActionListener, MenuListener {

        public void actionPerformed(ActionEvent evt) {
            Object obj = evt.getSource();

            tabbedDesignView.finalizeAdvanceSelectTools();

            if (obj == undoEditMenu)
                tabbedDesignView.editUndo();
            else if (obj == redoEditMenu)
                tabbedDesignView.editRedo();
            else if (obj == cutEditMenu)
                copyCutPaste.onCut(tabbedDesignView.getDesignDocument());
            else if (obj == copyEditMenu)
                copyCutPaste.onCopy(tabbedDesignView.getDesignDocument());
            else if (obj == pasteAsNewDesignEditMenu) {
                DesignDocument document = EmbroideryDraw.getEmbroideryDraw().newFile();
                if (document != null) {
                    addDocument(document);
                    copyCutPaste.onPasteIntoCurrentLayer(document);
                }

            } else if (obj == pasteIntoCurrentLayerEditMenu)
                copyCutPaste.onPasteIntoCurrentLayer(tabbedDesignView.getDesignDocument());
            else if (obj == pasteIntoNewLayerEditMenu)
                copyCutPaste.onPasteIntoNewLayer(panelLayer, tabbedDesignView.getDesignDocument());
            else if (obj == settingsEditMenu) {
                DialogSettings dialog = new DialogSettings(thisFrame, true);
                dialog.setVisible(true);
                if (dialog.changePenSize() && tabbedDesignView.getTabCount() != 0)
                    tabbedDesignView.forceZoom();
                tabbedDesignView.repaint();
            }
        }

        public void menuSelected(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class ViewMenuListen Menu">
    class ViewMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == snapToGridMenu)
                DrawingPad.setSnapToGrid(!DrawingPad.isSnapToGrid());
            else if (src == showControlPointsMenu)
                Vertex.setControlPointsVisible(!Vertex.isControlPointsVisible());
            else if (src == gridViewMenu)
                Measurement.setShowGrid(!Measurement.isGridVisible());
            else if (src == zoomDrawingViewMenu)
                zoomItem(InterfaceFrameOperation.ITEM_DRAWING);
            else if (src == zoomLayerViewMenu)
                zoomItem(InterfaceFrameOperation.ITEM_LAYER);
            else if (src == zoom800ViewMenu)
                setZoom(8.0f);
            else if (src == zoom400ViewMenu)
                setZoom(4.0f);
            else if (src == zoom200ViewMenu)
                setZoom(2.0f);
            else if (src == zoom100ViewMenu)
                setZoom(1.0f);
            else if (src == zoom75ViewMenu)
                setZoom(0.75f);
            else if (src == zoom50ViewMenu)
                setZoom(0.50f);
            else if (src == zoom25ViewMenu)
                setZoom(0.25f);
            else if (src == changeGridViewMenu) {
                new DialogGridSettings(thisFrame, true).setVisible(true);
                tabbedDesignView.repaint();
            } else if (src == calibrateDrawingMenu) {
                new DialogCalibrate(thisFrame, true).setVisible(true);
                zoomItem(ITEM_100);
            }

            tabbedDesignView.repaint();
            invalidateGridSnapShow();
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class ImageMenuListen Menu">
    class ImageMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            tabbedDesignView.finalizeAdvanceSelectTools();

            // Create new tool.
            if (src == loadImageMenu)
                tabbedDesignView.loadImage();
            else if (src == restoreAllImageMenu)
                tabbedDesignView.restoreAllImages();
            else if (src == turnImagesOnOffImageMenu)
                tabbedDesignView.toggleImagesOnOff();
            else if (src == turnLighteningOnOffImageMenu)
                tabbedDesignView.toggleImagesLighteningOnOff();
            else if (src == setLighteningImageMenu)
                tabbedDesignView.setImagesSettings();
            else if (src == removeAllImageMenu)
                tabbedDesignView.removeAllImages();
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class DesignMenuListen Menu">
    class DesignMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            tabbedDesignView.finalizeAdvanceSelectTools();

            if (src == detailsDesignMenu)
                tabbedDesignView.editDocumentDetails();
            else if (src == statsDesignMenu)
                tabbedDesignView.viewStats();
            else if (src == resizeDesignMenu)
                tabbedDesignView.resizeDesign();
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class PatternMenuListen Menu">
    class PatternMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            tabbedDesignView.finalizeAdvanceSelectTools();

            if (src == showDirectoryPatternMenu)
                toolPatternPanel.showDirectory();
            else if (src == reloadPatternsMenu)
                toolPatternPanel.reloadPatterns();
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class LayerMenuListen Menu">
    class LayerMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            tabbedDesignView.finalizeAdvanceSelectTools();

        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class StageMenuListen Menu">
    class StageMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            tabbedDesignView.finalizeAdvanceSelectTools();

        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class HelpMenuListen Menu">
    class HelpMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object obj = evt.getSource();

            tabbedDesignView.finalizeAdvanceSelectTools();

            if (obj == aboutHelpMenu) {
                DialogAbout dialog = new DialogAbout(thisFrame, true);
                dialog.setVisible(true);
            } else if (obj == websiteHelpMenu || obj == helpHelpMenu)
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler www.embroiderydraw.com");
                } catch (Exception e) {
                    JOptionPane mess = new JOptionPane();
                    mess.showMessageDialog(frameContainer, "Unable to open browser. Exception: " + e.getMessage(), "Embroidery Draw", JOptionPane.ERROR_MESSAGE);
                    System.err.println(e.getMessage());
                }
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class DebugMenuListen Menu">
    class DebugMenuListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            if (printDesignDebugMenu == evt.getSource()) {
                DesignPanel dPanel = tabbedDesignView.getDesignDocument().getDesignPanel();
                if (dPanel != null) {
                    System.out.println("========== DEBUG Print Design Info ==============");
                    System.out.println(dPanel.getDesign().toString());
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                }
            } else if (printUndoListDebugMenu == evt.getSource()) {
                DesignPanel dPanel = tabbedDesignView.getDesignDocument().getDesignPanel();
                if (dPanel != null) {
                    System.out.println("========== DEBUG Print Undo/Redo Info ==============");
                    dPanel.getDesign().debugPrintUndo();
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                    System.out.println("=================================================");
                }
            } else if (printFileDesignDebugMenu == evt.getSource()) {
                DesignPanel dPanel = tabbedDesignView.getDesignDocument().getDesignPanel();
                if (dPanel != null) {
                    StringBuffer sBuffer = new StringBuffer();
                    sBuffer.append("========== DEBUG Print Design Info ==============\n");
                    sBuffer.append(dPanel.getDesign().toString());
                    sBuffer.append("=================================================\n");
                    sBuffer.append("=================================================\n");
                    sBuffer.append("=================================================\n");
                    sBuffer.append("=================================================\n");
                    DefaultExceptionHandler.printToLog(sBuffer.toString());
                }
            } else if (printFileUndoListDebugMenu == evt.getSource()) {
                DesignPanel dPanel = tabbedDesignView.getDesignDocument().getDesignPanel();
                if (dPanel != null)
                    dPanel.getDesign().debugPrintUndoLog();
            } else if (showNumberDesignDebugMenu == evt.getSource()) {
                DrawingDesign.debugTogglePrintLines();
                if (tabbedDesignView.getDesignDocument() != null)
                    tabbedDesignView.getDesignDocument().repaint();
            } else if (distanceBetweenPointDebugMenu == evt.getSource()) {
                JOptionPane getNewDistance = new JOptionPane();
                String sampling = getNewDistance.showInputDialog(null, "Sampling Distance For Curves", PemSettings.getSamplingRate());
                float curveDistance = PemSettings.getSamplingRate();
                try {
                    curveDistance = Float.parseFloat(sampling);
                } catch (Exception e) {
                }
                PemSettings.setSamplingRate(curveDistance);

            } else if (toggleColorModeDebugMenu == evt.getSource())
                FillGraphSystem.setDebug(!FillGraphSystem.isDebug());
            else if (setTrialDaysDebugMenu == evt.getSource()) {
                JOptionPane message = new JOptionPane();
                String input = message.showInputDialog(null, "Set trail days:", 15);
                try {
                    int trialDays = Integer.parseInt(input);
                    ProgramActivation program = new ProgramActivation();
                    program.setTrialDays(trialDays);
                } catch (Exception e) {
                }

            } else if (removeProgramKeyDebugMenu == evt.getSource()) {
                ProgramActivation program = new ProgramActivation();
                program.removeProgramKey();
            }
        }

    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Listeners - JMenuBar (Buttons On Frame) ">
    // <editor-fold defaultstate="collapsed" desc=" Class BarGeneratorListen JMenuBar">
    class BarGeneratorListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            if (src == btnGenerateFreeStandingLace) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                generateFreeStandingLace();
            } else if (src == btnGenerateDollie) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                generateDollie();
            }
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarDesignListen JMenuBar">
    class BarDesignListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            // Toggle the previous button to off.
            toggleToolPreviousButton((JToggleButton) src);

            // Create new tool.
            if (src == btnDesignToolLine)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_LINE);
            else if (src == btnDesignToolBezier)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_BEZIER);
            else if (src == btnDesignToolRMolnar)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_RMOLNAR);
            else if (src == btnComplexPatterns)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_COMPLEX_PATTERN);
            else if (src == btnDesignToolDelete)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_DELETE);
            else if (src == btnDesignToolAddPoint)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_ADDPOINT);
            else if (src == btnDesignToolConnectRMolnar)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_CONNECTRMOLNAR);
            else if (src == btnDesignToolBezierControl)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_BEZIERCONTROL);
            else if (src == btnDesignToolPullLine)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_PULL_LINEAPART);
            else if (src == btnDesignToolMoverControl)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_SIMPLEMOVER);
            else if (src == btnDesignToolSelect)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_SELECT);
            else if (src == btnDesignToolRotate)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_ROTATE);
            else if (src == btnDesignToolResize)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_RESIZE);
            else if (src == btnDesignToolMirror)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_MIRROR);
            else if (src == btnDesignToolTranslate)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_TRANSLATE);
            else if (src == btnDesignToolRectangle)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_SQUARE);
            else if (src == btnDesignToolCircle)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_CIRCLE);
            else if (src == btnDesignToolMagifyingGlass)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_MAGNIFYGLASS);
            else if (src == btnDesignToolDEBUG)
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_DEBUG);
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarImageListen JMenuBar">
    class BarImageListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            // Create new tool.
            if (src == btnImageToolLoad) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.loadImage();
            } else if (src == btnImageToolDelete) {
                toggleToolPreviousButton((JToggleButton) src);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_IMAGE_DELETE);
            } else if (src == btnImageToolRotate) {
                toggleToolPreviousButton((JToggleButton) src);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_IMAGE_ROTATE);
            } else if (src == btnImageToolResize) {
                toggleToolPreviousButton((JToggleButton) src);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_IMAGE_RESIZE);
            } else if (src == btnImageToolMove) {
                toggleToolPreviousButton((JToggleButton) src);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_IMAGE_MOVER);
            } else if (src == btnImageToolCenter) {
                toggleToolPreviousButton((JToggleButton) src);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_IMAGE_CENTER);
            } else if (src == btnImageToolRestoreAll) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.restoreAllImages();
            } else if (src == btnImageToolImagesOnOff) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.toggleImagesOnOff();
            } else if (src == btnImageToolLighteningOnOff) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.toggleImagesLighteningOnOff();
            } else if (src == btnImageToolSetLightening) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.setImagesSettings();
            } else if (src == btnImageToolRemoveAll) {
                tabbedDesignView.finalizeAdvanceSelectTools();
                tabbedDesignView.removeAllImages();
            }
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarStandardListen JMenuBar">
    class BarStandardListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object obj = evt.getSource();

            tabbedDesignView.finalizeAdvanceSelectTools();

            if (obj == btnStandardToolUndo)
                tabbedDesignView.editUndo();
            else if (obj == btnStandardToolRedo)
                tabbedDesignView.editRedo();
            else if (obj == btnConversionToolPemV4)
                tabbedDesignView.exportDocumentAsPEM();
            else if (obj == btnConversionToolBitmap)
                tabbedDesignView.exportDocumentAsBitmap();
            else if (obj == btnStandardToolNew)
                newFile();
            else if (obj == btnStandardToolOpen)
                openFile();
            else if (obj == btnStandardToolSave)
                tabbedDesignView.writeDocument(false);
            else if (obj == btnStandardToolCopy)
                copyCutPaste.onCopy(tabbedDesignView.getDesignDocument());
            else if (obj == btnStandardToolCut)
                copyCutPaste.onCut(tabbedDesignView.getDesignDocument());
            else if (obj == btnStandardToolPasteCurrentLayer)
                copyCutPaste.onPasteIntoCurrentLayer(tabbedDesignView.getDesignDocument());
            else if (obj == btnStandardToolPasteNewDesign) {
                DesignDocument document = EmbroideryDraw.getEmbroideryDraw().newFile();
                if (document != null) {
                    addDocument(document);
                    copyCutPaste.onPasteIntoCurrentLayer(document);
                }

            } else if (obj == btnStandardToolPasteNewLayer)
                copyCutPaste.onPasteIntoNewLayer(panelLayer, tabbedDesignView.getDesignDocument());
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarCustomUserTools JMenuBar">
    class BarCustomUserTools implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarDesignOperationsListen JMenuBar">
    class BarDesignOperationsListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            if (src == btnSnapToGrid)
                DrawingPad.setSnapToGrid(!DrawingPad.isSnapToGrid());
            else if (src == btnShowGrid)
                Measurement.setShowGrid(!Measurement.isGridVisible());
            else if (src == btnShowControlPoints)
                Vertex.setControlPointsVisible(!Vertex.isControlPointsVisible());

            tabbedDesignView.repaint();
            invalidateGridSnapShow();
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Class BarColorFillListen JMenuBar">
    class BarColorFillListen implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            tabbedDesignView.finalizeAdvanceSelectTools();

            if (evt.getSource() == btnFillInColor) {
                toggleToolPreviousButton(btnFillInColorTool);
                tabbedDesignView.setGuiStage(FrameEmbroideryDraw.GUISTAGE_COLOR);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_FILL_COLOR);
                setGUIStage(FrameEmbroideryDraw.GUISTAGE_COLOR);

            } else if (evt.getSource() == btnFillInVector) {
                toggleToolPreviousButton(btnDesignToolLine);
                tabbedDesignView.setGuiStage(FrameEmbroideryDraw.GUISTAGE_VECTOR);
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_LINE);
                setGUIStage(FrameEmbroideryDraw.GUISTAGE_VECTOR);

            } else if (evt.getSource() == btnFillInColorTool) {
                toggleToolPreviousButton((JToggleButton) evt.getSource());
                // Toggle the previous button to off.
                tabbedDesignView.newTool(EmbroideryDraw.TOOL_FILL_COLOR);
            }
        }

    }    // </editor-fold>
    // </editor-fold>
    ////////////////////////////////////////////////////////////////////////////

}

