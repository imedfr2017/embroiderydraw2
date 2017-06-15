/*
 * Measurement.java
 *
 * Created on August 11, 2005, 11:23 PM
 *
 */

package mlnr;

import java.awt.*;
import java.util.prefs.*;

import mlnr.util.*;

/** This class is used to convert between measurements and grid settings.
 * @author Robert Molnar II
 */
public class Measurement {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Final Fields ">

    /** This is the number of measurements that it takes to make a millimeter. */
    private static final float MEASUREMENT_TO_MM = 1.0f;
    /** This is the number of measurements that it take to make 2 millimeters. */
    private static final float MEASUREMENT_MM_2 = 2.0f;    
    /** This is the number of measurements that it take to make 5 millimeters. */
    private static final float MEASUREMENT_MM_5 = 5.0f;    
    /** This is the number of measurements that it take to make 10 millimeters. */
    private static final float MEASUREMENT_MM_10 = 10.0f;    
    /** Fine Grid Type: Used to indicate 1 mm. */
    public static final int MM_1 = 1;
    /** Fine Grid Type: Used to indicate 2 mm. */
    public static final int MM_2 = 2;
    /** Fine Grid Type: Used to indicate 5 mm. */
    public static final int MM_5 = 3;
    
    /** This is the number of measurements that it takes to make an inch. */
    private static final float MEASUREMENT_TO_IN = 25.4f;    
    /** This is the number of measurements that it take to make 1/2 an inch. */
    private static final float MEASUREMENT_IN_1_2 = 12.7f;    
    /** This is the number of measurements that it take to make 1/4 an inch. */
    private static final float MEASUREMENT_IN_1_4 = 6.35f;
    /** This is the number of measurements that it take to make 1/8 an inch. */
    private static final float MEASUREMENT_IN_1_8 = 3.175f;
    /** This is the number of measurements that it take to make 1/16 an inch. */
    private static final float MEASUREMENT_IN_1_16 = 1.5875f;
    /** Fine Grid Type: Used to indicate 1/2 in. */
    public static final int IN_1_2 = 3;
    /** Fine Grid Type: Used to indicate 1/4 in. */
    public static final int IN_1_4 = 2;
    /** Fine Grid Type: Used to indicate 1/8 in. */
    public static final int IN_1_8 = 4;
    /** Fine Grid Type: Used to indicate 1/16 in. */
    public static final int IN_1_16 = 1;
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields (Settings) ">
    
    /** True if the main grid should be shown. */
    private static boolean showGrid = true;
    /** True if the fine grid should be shown. */
    private static boolean showFineGrid = true;    
    /** Size of main grid in Measurements. */
    private static float gridMainSize = 10.0f;
    /** Type of the fine grid setting. (This is what gets saved). */
    private static int gridFineType = MM_1;
    /** Color of main grid in Measurements. */
    private static Color gridMainColor = new Color(153, 153, 153);
    /** Color of fine grid in Measurements. */
    private static Color gridFineColor = new Color(230, 220, 230);
    
    /** This is the number of pixels per measurement. It should be calibrated for real life 1:1 size on screen.*/
    private static float pixelsPerMeasurement = 5.2f;
    
    /** True: If using metric measurements, else false: using english measurements. */
    private static boolean metricEnglish = true;
        
    /** This is the size of the stroke used for the drawing of the design. */
    private static int PEN_DESIGN_SIZE = 1;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    /** Creates a new instance of Measurement */
    private Measurement() {
    }
 
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Conversion Methods ">
    
    /** This will convert the measurement to the current real life measurement system.
     * @param measurement is the measurement to convert to real life measurement system.
     * @param decimalPos is the number of decimals it should have (2-3).
     * @return the current real life measurement system.
     */
    static public float convertMeasurement(float measurement, int decimalPos) {
        float conv;
        if (metricEnglish)
            conv = convertMeasurementToMM(measurement);
        else
            conv = convertMeasurementToInch(measurement);
        
        if (decimalPos == 2) {
            conv *= 100.0f;
            conv = (int)conv;
            conv /= 100.0f;
        } else if (decimalPos == 3) {
            conv *= 1000.0f;
            conv = (int)conv;
            conv /= 1000.0f;
        }
        
        return conv;
    }
    
    /** This will convert the current real life measurement to measurement.
     * @param real is the current real life measurement.
     * @return the measurement value.
     */
    static public float convReal(float real) {
        if (metricEnglish)
            return convertMMToMeasurement(real);
        return convertInchesToMeasurement(real);
    }
    
    /** This will convert the measurement to millimeters.
     * @param measurement is the measurement to convert to millimeters.
     * @return the number of millimeters the measurement is.
     */
    static public float convertMeasurementToMM(float measurement) {
        return measurement;
    }
    
    /** This will convert the measurement to millimeters.
     * @param measurement is the measurement to convert to millimeters.
     * @param decimalPos is the number of decimals it should have (2-3).
     * @return the number of millimeters the measurement is.
     */
    static public float convertMeasurementToMM(float measurement, int decimalPos) {
        float conv = measurement;
        
        if (decimalPos == 2) {
            conv *= 100.0f;
            conv = (int)conv;
            conv /= 100.0f;
        } else if (decimalPos == 3) {
            conv *= 1000.0f;
            conv = (int)conv;
            conv /= 1000.0f;
        }
        
        return conv;
    }
    
    /** This will convert the measurement to inches.
     * @param measurement is the measurement to convert to inches.
     * @return the number of inches the measurement is.
     */
    static public float convertMeasurementToInch(float measurement) {
        return measurement / MEASUREMENT_TO_IN;
    }
    
    /** This will convert the measurement to inches.
     * @param measurement is the measurement to convert to inches.
     * @param decimalPos is the number of decimals it should have (2-3).
     * @return the number of inches the measurement is.
     */
    static public float convertMeasurementToInch(float measurement, int decimalPos) {
        float conv = measurement / MEASUREMENT_TO_IN;
        
        if (decimalPos == 2) {
            conv *= 100.0f;
            conv = (int)conv;
            conv /= 100.0f;
        } else if (decimalPos == 3) {
            conv *= 1000.0f;
            conv = (int)conv;
            conv /= 1000.0f;
        }
        
        return conv;
    }
    
    /** This will convert millimeters to measurement.
     * @param mm is the millimeters to convert to measurement.
     * @return the number of measurements the mm is.
     */
    static public float convertMMToMeasurement(float mm) {
        return mm;
    }
    
    /** This will convert inches to measurement.
     * @param mm is the inches to convert to measurement.
     * @return the number of inches the mm is.
     */
    static public float convertInchesToMeasurement(float inches) {
        return inches * MEASUREMENT_TO_IN;
    }
    
    /** @return the number of pixels per measurement for 100% scaling.
     */
    static public float getPixelsPerMeasurement() {
        return pixelsPerMeasurement;
    }
    
    /** This will set the number of pixels per measurement.
     * @param ppm pixels per measurement.
     */
    static public void setPixelsPerMeasurement(float ppm) {
        pixelsPerMeasurement = ppm;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fine Grid Methods ">
    
    /** This will get the fine grid type.
     * @return gridType can be MM_* or IN_*.
     */
    static public int getFineGridType() {
        return gridFineType;
    }
    
    /** This will set the fine grid size.
     * @param gridType can be MM_* or IN_*.
     */
    static public void setFineGridType(int gridType) {
        gridFineType = gridType;
    }
    
    /** @return the size of the fine grid.
     */
    static public float getFineGridSize() {
        if (metricEnglish) {
            switch (gridFineType) {
                case MM_1:
                    return MEASUREMENT_TO_MM;
                case MM_2:
                case IN_1_8:
                    return MEASUREMENT_MM_2;
                case MM_5:
                    return MEASUREMENT_MM_5;
                default:
                    throw new IllegalStateException("Unknown fine grid setting: " + gridFineType);
            }
        } else {
            switch (gridFineType) {
                case IN_1_2:
                    return MEASUREMENT_IN_1_2;
                case IN_1_4:
                    return MEASUREMENT_IN_1_4;
                case IN_1_8:
                    return MEASUREMENT_IN_1_8;
                case IN_1_16:
                    return MEASUREMENT_IN_1_16;
                default:
                    throw new IllegalStateException("Unknown fine grid setting: " + gridFineType);
            }
        }
    }
    
    /** @param show is true if the fine grid is to be shown, else false turn off
     * the fine grid view. The big grid must be turned on before the fine grid 
     * will show.
     */
    static public void setFineShowGrid(boolean show) {
        showFineGrid = show;
    }
    
    /** @return true if the fine grid is visible.
     */
    static public boolean isFineGridVisible() {
        return showFineGrid;
    }
        
    /** @param color of the fine grid.
     */
    static public void setFineGridColor(Color c) {
        if (c == null)
            return;
        gridFineColor = c;
    }
    
    /** @return the color for the fine grid.
     */
    static public Color getFineGridColor() {
        return gridFineColor;
    }
    
    /** @return default fine grid type.
     */
    static public int getDefaultSettingFineGridType() {
        if (metricEnglish)
            return MM_1;
        else
            return IN_1_4;
    }    
    
    /** @return default fine grid color.
     */
    static public Color getDefaultSettingFineColor() {
        return new Color(230, 220, 230);
    }
    
    /** @return default show fine grid.
     */
    static public boolean getDefaultSettingShowFineGrid() {
        return false;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Grid Methods ">
    
    /** @return the grid size.
     */
    static public float getGridSize() {
        if (metricEnglish)
            return MEASUREMENT_MM_10;
        else
            return MEASUREMENT_TO_IN;
    }
    
    /** @param show is true if the grid is to be shown, else false turn off
     * the grid view.
     */
    static public void setShowGrid(boolean show) {
        showGrid = show;
    }
    
    /** @return true if the grid is visible.
     */
    static public boolean isGridVisible() {
        return showGrid;
    }
        
    /** @param color of the grid.
     */
    static public void setGridColor(Color c) {
        if (c == null)
            return;
        gridMainColor = c;
    }
    
    /** @return the color for the grid.
     */
    static public Color getGridColor() {
        return gridMainColor;
    }
    
    /** @return default grid color.
     */
    static public Color getDefaultSettingColor() {
        return new Color(153, 153, 153);        
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Metric/English Methods ">

    /** This will save the metricEnglish settings to the factory settings.
     */
    public static void restoreMetricEnglishFactorySettings() {
        metricEnglish = true;
        getSettings().save();
    }
    
    /** @return true if using the Metric measurements, else false if using the
     * English measurements.
     */
    static public boolean isMetric() {
        return metricEnglish;
    }
    
    /** This will set the measurement system being used.
     * @param bMetricEnglish is true if the measurement system is metric, else false
     * in that it is English.
     */
    static public void setMetricEnglish(boolean bMetricEnglish) {
        metricEnglish = bMetricEnglish;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Static Measurement Name Methods ">
    
    /** This will get the textual representation of the current measurement.
     */
    static public String getTextualName() {
        if (metricEnglish)
            return "mm";
        else
            return "in";
    }
    
    /** This will get the textual representation of the current measurement.
     * @param metricEnglish is true if metric else flase english.
     */
    static public String getTextualName(boolean metricEnglish) {
        if (metricEnglish)
            return "mm";
        else
            return "in";
    }
    
    /** @return the long name of the textual representation of the current measurement. 
     */
    static public String getTextualLongName() {
        if (metricEnglish)
            return "Millimeters";
        else
            return "Inches";
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Pen Size Methods ">

    /** This will save the pen settings to the factory settings.
     */
    public static void restorePenFactorySettings() {
        PEN_DESIGN_SIZE = getDefaultDesignPenSize();
        getSettings().save();
    }

    /** @return the design pen size in pixel size.
     */
    static public int getDesignPenSize() {
        return PEN_DESIGN_SIZE;
    }

    /** @param penSize is the new design pen size in pixels.
     */
    static public void setDesignPenSize(int penSize) {
        PEN_DESIGN_SIZE = penSize;
    }
 
    static public int getDefaultDesignPenSize() {
        return 1;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Settings Methods ">
    
    /** @return Get the settings for this class.
     */
    static public InterfaceSettings getSettings() {
        return new MeasurementSettings();
    }
    
    /** This will save the settings but not to load term storage.
     * @param gridColor is the grid color to save.
     * @param fineColor is the fine grid color to save.
     * @param useFineGrid is true if the find grid should be shown.
     * @param
     */
    static public void saveSettings(Color gridColor, Color fineColor, boolean useFineGrid, int fineGridType) {
        setGridColor(gridColor);
        setFineGridColor(fineColor);
        setFineShowGrid(useFineGrid);
        setFineGridType(fineGridType);
    }
    
    /** This will restore the color settings to the initial settings the program first came with.
     */
    static public void restoreColorSettings() {
        setGridColor(getDefaultSettingColor());
        setFineGridColor(getDefaultSettingFineColor());
        getSettings().save();
    }
    
    /** This will restore the settings the initial settings the program first came with.
     */
    static public void restoreSettings() {
        setGridColor(getDefaultSettingColor());
        setFineGridColor(getDefaultSettingFineColor());
        setFineShowGrid(getDefaultSettingShowFineGrid());
        setFineGridType(getDefaultSettingFineGridType());
        setDesignPenSize(getDefaultDesignPenSize());
    }  
    
    // </editor-fold>
}

// <editor-fold defaultstate="collapsed" desc=" Class MeasurementSettigns ">

/** This will save the Measurement savings.
 */
class MeasurementSettings implements InterfaceSettings {
    static private String METRIC_ENGLISH = "MeasurementMetricEnglish"; // Boolean
    static private String SHOW_FINE_GRID = "MeasurementShowFineGrid"; // Boolean
    static private String GRID_COLOR = "MeasurementGridColor"; // Color
    static private String GRID_FINE_COLOR = "MeasurementGridFineColor";
    static private String GRID_FINE_SIZE = "MeasurementGridFineSizeType"; // Int
    static private String PIXELS_PER_MEASUREMENT = "MeasurementPPM"; // Float
    static private String PEN_SIZE = "MeasurementPenSize"; // Int
    
    public MeasurementSettings() {
    }
    
    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        
        prefs.putBoolean(METRIC_ENGLISH, Measurement.isMetric());        
        
        ColorSave.saveColor(prefs, GRID_COLOR, Measurement.getGridColor());
        
        prefs.putBoolean(SHOW_FINE_GRID, Measurement.isFineGridVisible());
        ColorSave.saveColor(prefs, GRID_FINE_COLOR, Measurement.getFineGridColor());
        prefs.putInt(GRID_FINE_SIZE, Measurement.getFineGridType());
        
        prefs.putFloat(PIXELS_PER_MEASUREMENT, Measurement.getPixelsPerMeasurement());
        
        prefs.putInt(PEN_SIZE, Measurement.getDesignPenSize());
    }
    
    public void load() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        
        Measurement.setMetricEnglish(prefs.getBoolean(METRIC_ENGLISH, Measurement.isMetric()));
        
        Measurement.setGridColor(ColorSave.loadColor(prefs, GRID_COLOR, Measurement.getDefaultSettingColor()));
        
        Measurement.setFineShowGrid(prefs.getBoolean(SHOW_FINE_GRID, Measurement.isFineGridVisible()));
        Measurement.setFineGridColor(ColorSave.loadColor(prefs, GRID_FINE_COLOR, Measurement.getDefaultSettingFineColor()));
        
        Measurement.setFineGridType(prefs.getInt(GRID_FINE_SIZE, Measurement.getFineGridType()));
        
        Measurement.setPixelsPerMeasurement(prefs.getFloat(PIXELS_PER_MEASUREMENT, Measurement.getPixelsPerMeasurement()));
        
        Measurement.setDesignPenSize(prefs.getInt(PEN_SIZE, Measurement.getDesignPenSize()));

     }
}    

// </editor-fold>
