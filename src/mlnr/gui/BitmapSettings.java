/*
 * BitmapSettings.java
 *
 * Created on June 27, 2006, 1:15 PM
 *
 */

package mlnr.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import mlnr.*;
import mlnr.gui.cpnt.ImagePreview;
import mlnr.gui.dlg.DialogFileChooser;
import mlnr.gui.dlg.FileNameFilter;
import mlnr.util.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.emf.EMFGraphics2D;


// <editor-fold defaultstate="collapsed" desc=" Class BitmapSettingsSettings ">

/** This class is used to store the bitmap settings.
 * @author Robert Molnar II
 */
public class BitmapSettings implements Comparable {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** Name of the bitmap setting. */
    private String name;
    /** The DPI setting to output the bitmap in. */
    private int dpi;
    /** This is the color mode, such as 1bit, 8bit, full color. */
    private int colorMode;
    /** This is the size of the pen, such as auto, 1-7. */
    private int penSize;
    /** True if anti-aliasing should be used. */
    private boolean antiAliasing;
    /** True if should only draw the color filling. */
    private boolean fillColor;
    /** Can it be edited by the user? */
    private boolean locked;
    /** This is the default output to be used in the saving dialog box. */
    private int defaultBitmap;
    /** This is used to store the image while outputting the drawing onto this image. */
    BufferedImage bImageOutput = null;
    /** This is the file used to output the image. */
    File fAbsolute = null;
    /** This is the DEFAULTBITMAP_* which the user choosen. */
    int bitmapType = 0;
    /** This is used to output an EMF file. */
    VectorGraphics vectorGraphics = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** The default output is JPG. */
    public static final int DEFAULTBITMAP_JPG = 0;
    /** The default output is BMP. */
    public static final int DEFAULTBITMAP_BMP = 1;
    /** The default output is EMF. */
    public static final int DEFAULTBITMAP_EMF = 2;
    
    public static final int COLORMODE_1BIT = 0;
    public static final int COLORMODE_8BIT = 1;
    public static final int COLORMODE_FULLCOLOR = 2;
    
    public static final int PENSIZE_DEFAULT = 0;
    
    /** This is the list of Bitmap Settings. */
    public static LinkedList<BitmapSettings> ltBitmapSettings;
    
    /** This is the current index of the Bitmap Settings used. */
    private static int indexCurrentSetting = 0;
    
    /** This is the extra space in pixels for the outputted bitmap. This is added on each side of the outputted bitmap. */
    private static final int MARGIN_X = 10;
    /** This is the extra space in pixels for the outputted bitmap. This is added on each side of the outputted bitmap. */
    private static final int MARGIN_Y = 10;
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    /** Creates a new instance of BitmapSettings */
    public BitmapSettings(String name, int dpi, boolean colorFillOnly, int colorMode, int penSize, boolean antiAliasing, int defaultBitmap, boolean locked) {
        this.name = name;
        this.dpi = dpi;
        this.fillColor = colorFillOnly;
        this.colorMode = colorMode;
        this.penSize = penSize;
        this.antiAliasing = antiAliasing;
        this.defaultBitmap = defaultBitmap;
        this.locked = locked;
    }
    
    /** This will parse a line that contains the bitmap settings.
     * @param line is a line from the file that contains a bitmap setting.
     * @exception IllegalArgumentException line doesn't contain proper data.
     */
    private BitmapSettings(String line) throws IllegalArgumentException {
        StringTokenizer strTok = new StringTokenizer(line, "|");
        
        // Get name.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the name.");
        setName(strTok.nextToken());
        
        // Get the fill color.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the color fill.");
        if (strTok.nextToken().equals("1"))
            setFillColor(true);
        else
            setFillColor(false);
        
        // Get the dpi.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the dpi.");
        setDpi(Integer.parseInt(strTok.nextToken()));
        
        // Get the colorMode.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the colorMode.");
        setColorMode(Integer.parseInt(strTok.nextToken()));
        
        // Get the penSize.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the penSize.");
        setPenSize(Integer.parseInt(strTok.nextToken()));
        
        // Get the antiAliasing.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the Anti-Aliasing.");
        if (strTok.nextToken().equals("1"))
            setAntiAliasing(true);
        else
            setAntiAliasing(false);
        
        // Get the bitmap output settings.
        if (strTok.hasMoreElements() == false)
            throw new IllegalArgumentException("line[" + line + "] does not contain the defaultBitmap.");
        setDefaultBitmap(Integer.parseInt(strTok.nextToken()));
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Serialize Methods ">
    
    /** This will get the list of the bitmap settings. The ones that are locked will always be at the start of this list.
     */
    public static void loadBitmapSettings() {
        ltBitmapSettings = new LinkedList();
        
        // Load in the bitmap setting index.
        new BitmapSettingsSettings().load();
        
        // Add the default settings in.
        ltBitmapSettings.add(new BitmapSettings("Default", 150, false, COLORMODE_FULLCOLOR, PENSIZE_DEFAULT, true, DEFAULTBITMAP_BMP, true));
        
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader("bitmapSettings.ini"));
            
            // First line must be BITMAPSETTINGS=2.0
            String line;
            if ((line = bufReader.readLine()) == null)
                throw new Exception("Empty file.");
            if (line.equals("BITMAPSETTINGS=2.0") == false)
                throw new Exception("BITMAPSETTINGS=2.0 does not exist in file.");
            
            // Now load the user defined ones in.
            while ((line = bufReader.readLine()) != null) {
                if (line.equals(""))
                    continue;
                
                ltBitmapSettings.add(new BitmapSettings(line));
            }
                       
            bufReader.close();
            
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e);            
        }
        
        // This will sort the list of settings.
        Collections.sort(ltBitmapSettings);
        
        // If the index no longer exists then it needs to be set to zero.
        if (getIndexCurrentSetting() >= ltBitmapSettings.size()) {
            setIndexCurrentSetting(0);
            getSettings().save();
        }
    }
    
    /** Write out the BitmapSettings.
     */
    private static void saveBitmapSettings() {
        try {
            PrintWriter pout = new PrintWriter("bitmapSettings.ini");
            pout.println("BITMAPSETTINGS=2.0");
            
            for (Iterator itr = ltBitmapSettings.iterator(); itr.hasNext(); ) {
                BitmapSettings bitSet = (BitmapSettings)itr.next();
                if (bitSet.isLocked() == false)
                    pout.println(bitSet.toString());
            }
            
            pout.close();
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to write to bitmapSettings.ini file.");
        }
        
        // If the index no longer exists then it needs to be set to zero.
        if (getIndexCurrentSetting() >= ltBitmapSettings.size()) {
            setIndexCurrentSetting(0);
            getSettings().save();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Operation Methods ">
    
    /** This will add a BitmapSettings to the list and save it to the file.
     */
    public static void addBitmapSettings(BitmapSettings bitSet) {
        ltBitmapSettings.add(bitSet);
        Collections.sort(ltBitmapSettings);
        
        // now set the index to the added bitmap setting.
        int index = 0;
        for (Iterator<BitmapSettings> itr = ltBitmapSettings.iterator(); itr.hasNext(); index++) {
            if (itr.next() == bitSet)
                setIndexCurrentSetting(index);
        }
        
        saveBitmapSettings();
    }
    
    /** This will edit the Bitmap Settings.
     */
    public static void editBitmapSettings(int index, BitmapSettings bitSet) {
        BitmapSettings bitSettings = (BitmapSettings)ltBitmapSettings.get(index);
        bitSettings.setAntiAliasing(bitSet.isAntiAliasing());
        bitSettings.setFillColor(bitSet.isFillColorOnly());
        bitSettings.setColorMode(bitSet.getColorMode());
        bitSettings.setDpi(bitSet.getDpi());
        bitSettings.setName(bitSet.getName());
        bitSettings.setPenSize(bitSet.getPenSize());
        bitSettings.setDefaultBitmap(bitSet.getDefaultBitmap());
        saveBitmapSettings();
    }
    
    /** This will remove the BitmapSettings.
     */
    public static void removeBitmapSettings(int index) {
        ltBitmapSettings.remove(index);
        saveBitmapSettings();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Object Methods ">
    
    public String toString() {
        int colorFill = 0;
        if (fillColor)
            colorFill = 1;
        
        int anti = 0;
        if (antiAliasing)
            anti = 1;
        
        return name + "|" + colorFill + "|" + dpi + "|" + colorMode + "|" + penSize + "|" + anti + "|" + defaultBitmap;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Get And Is Methods ">
    
    /** This will get the current index used in the Bitmap Settings.
     */
    public static int getIndexCurrentSetting() {
        return indexCurrentSetting;
    }
    
    static public InterfaceSettings getSettings() {
        return new BitmapSettingsSettings();
    }
    
    public int getDefaultBitmap() {
        return defaultBitmap;
    }
    
    /** This will get the current bitmap setting.
     * @return the current bitmap setting.
     * @throws IllegalStateException index out of range for the bitmap settings.
     */
    public static BitmapSettings getCurrentBitmapSetting() {
        if (indexCurrentSetting < 0)
            throw new IllegalStateException("index for current bitmap setting is out of range[0 - " + ltBitmapSettings.size() + "]: " + indexCurrentSetting);
        else if (indexCurrentSetting >= ltBitmapSettings.size())
            throw new IllegalStateException("index for current bitmap setting is out of range[0 - " + ltBitmapSettings.size() + "]: " + indexCurrentSetting);
        return (BitmapSettings)ltBitmapSettings.get(indexCurrentSetting);
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isFillColorOnly() {
        return fillColor;
    }
    
    public int getDpi() {
        return dpi;
    }
    
    public int getColorMode() {
        return colorMode;
    }
    
    public int getPenSize() {
        return penSize;
    }
    
    public boolean isAntiAliasing() {
        return antiAliasing;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    /** @return true when drawing the design if to change the color used to draw, else false do not change the color of
     * the pen.
     */
    public boolean isChangeColor() {
        if (colorMode == COLORMODE_1BIT)
            return false;
        return true;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Set Methods ">
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setFillColor(boolean fillColor) {
        this.fillColor = fillColor;
    }
    
    public void setDpi(int dpi) {
        this.dpi = dpi;
    }
    
    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }
    
    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }
    
    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }
    
    /** This will set the current index used in the Bitmap Settings.
     */
    public static void setIndexCurrentSetting(int index) {
        indexCurrentSetting = index;
    }
    
    public void setDefaultBitmap(int defaultBitmap) {
        this.defaultBitmap = defaultBitmap;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Bitmap Write Methods ">
    
    /** This will output the image. Make sure to call beginSaveBitmap().
     */
    public void endSaveBitmap() throws Exception {
        if (fAbsolute == null)
            throw new IllegalStateException("Call beginSaveBitmap() before calling endSaveBitmap().");
        
        if (bitmapType == BitmapSettings.DEFAULTBITMAP_EMF) {
            // Complete the process.
            vectorGraphics.endExport();
            vectorGraphics = null;
        } else {
            // Get the bitmap type.
            String strBitmapType;
            switch (bitmapType) {
                case DEFAULTBITMAP_BMP:
                    strBitmapType = "bmp";
                    break;
                case DEFAULTBITMAP_EMF:
                    throw new UnsupportedOperationException("Bitmap type not supported yet: " + bitmapType);
                case DEFAULTBITMAP_JPG:
                    strBitmapType = "jpg";
                    break;
                default:
                    throw new IllegalStateException("Unknown bitmap type: " + bitmapType);

            }

            // Now output that image to the file.
            ImageIO.write(bImageOutput, strBitmapType, fAbsolute);
            bImageOutput = null;
        }
    }
    
    /** This will ask the user where to save the bitmap at and then create the bitmap and set up the Graphics2D with the correct
     * size and all. After the drawing is completed then call endSaveBitmap() this will output the image from memory to disk. Use
     * the Graphics2D returned from this function to draw in and then call endSaveBitmap().
     * @param fileName is the name of the file without any extensions.
     * @param fRectBound is the position and size of a bounds in the design in measurements. (bounds of the drawing design, what the
     * user drawn)
     * @param iColorModel is an IndexColorModel that contains all the colors from the layers. This is only used for an 8bit color depth
     * bitmap. However, this cannot be null.
     * @param ltColors is a list of colors which if using 8 bit color mode will represent the design.
     * @return A Graphics2D object to draw on. Assumed to be drawing in it with measurements. When returned it has the pen, scaled, and
     * translated, so all that is needed is to draw into it. If color mode is Black-White then the pen will be set to black, else you
     * will need to set the color. It can be null if the user cancelled the save bitmap.
     */
    public Graphics2D beginSaveBitmap(String fileName, Rectangle2D.Float fRectBound, LinkedList ltColors) {
        try {
            // Show the save dialog.
            showSaveDialog(fileName);
            if (fAbsolute == null)
                return null;
            
            // Get the size of the design's outputted bounds.
            float width = (float)fRectBound.getWidth();
            float height = (float)fRectBound.getHeight();
            
            // Size of the bounds in pixels.
            float widthPixels = Measurement.convertMeasurementToInch(width) * (float)dpi;
            float heightPixels = Measurement.convertMeasurementToInch(height) * (float)dpi;
            
            // Size of the bitmap.
            int imageWidth = (int)widthPixels + MARGIN_X * 2;
            int imageHeight = (int)heightPixels + MARGIN_Y * 2;
            
            // Create the bitmap.
            Graphics2D g2d = null;
            if (bitmapType == BitmapSettings.DEFAULTBITMAP_EMF) {
                Properties p = new Properties();
                p.setProperty("PageSize","A5");
                vectorGraphics = new EMFGraphics2D(fAbsolute, new Dimension(imageWidth, imageHeight));
                vectorGraphics.setProperties(p);
                vectorGraphics.startExport();
                g2d = vectorGraphics;
            } else {
                // Create the image with the correct Color mode.
                if (colorMode == BitmapSettings.COLORMODE_FULLCOLOR)
                    bImageOutput = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                else if (colorMode == BitmapSettings.COLORMODE_8BIT)
                    bImageOutput = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_INDEXED, get8BitColorIndex(ltColors));
                else if (colorMode == BitmapSettings.COLORMODE_1BIT) {
                    bImageOutput = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
                } else
                    throw new IllegalStateException("Unknown color mode[" + colorMode + "] for this Bitmap Setting: [" + name + "].");
                g2d = (Graphics2D)bImageOutput.getGraphics();
            }
            
            // Set the graphics up.
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Anti-aliasing on.
            if (antiAliasing)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            else
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            // Get the scale factor.
            float scaleFactor = Measurement.convertMeasurementToInch(1.0f) * (float)dpi;
            
            // Fill bitmap with white.
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, imageWidth, imageHeight);
            
            // Scale and translate.
            g2d.scale(scaleFactor, scaleFactor);
            g2d.translate(-fRectBound.getX() + (1 / scaleFactor) * MARGIN_X, -fRectBound.getY() + (1 / scaleFactor) * MARGIN_Y);
            
            // Set the pen size.
            float fPenSize = penSize / scaleFactor;
            if (penSize == 1)
                fPenSize = 0.0f;
            g2d.setStroke(new BasicStroke(fPenSize));
            
            // Set the color if black-white.
            if (colorMode == COLORMODE_1BIT)
                g2d.setColor(Color.BLACK);            
            
            return g2d;
            
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to save bitmap. Exception: " + e.getMessage());            
        }
        
        return null;
    }
    
    /** This will get an 8 bit color index mode.
     * @param ltColors is the Colors for this index color model. Must be a list of Color.
     * @return an IndexColorModel of the list of colors.
     */
    private IndexColorModel get8BitColorIndex(LinkedList ltColors) {
        // Create the arrays of colors.
        byte[] reds = new byte[256];
        byte[] greens = new byte[256];
        byte[] blues = new byte[256];
        
        // First color is white (need white).
        reds[0] = (byte)255;
        greens[0] = (byte)255;
        blues[0] = (byte)255;
        
        // Get all the layer colors.
        int colorIndex=1;
        for (Iterator itr=ltColors.iterator(); itr.hasNext(); ) {
            Color c = (Color)itr.next();
            
            // Search for the color.
            byte red = (byte)c.getRed();
            byte green = (byte)c.getGreen();
            byte blue = (byte)c.getBlue();
            boolean bFound = false;
            for (int i=0; i < colorIndex; i++) {
                if (red == reds[i] && blue == blues[i] && green == greens[i]) {
                    bFound = true;
                    break;
                }
            }
            
            // If color not found then add it to the colors.
            if (bFound == false) {
                if (colorIndex == 255) {
                    JOptionPane.showMessageDialog(null, "Too many colors (greater than 255), some colors will not exist in bitmap, please use hi-color to use all colors.");
                    break;
                }
                
                // Add the color in.
                blues[colorIndex] = blue;
                reds[colorIndex] = red;
                greens[colorIndex] = green;
                colorIndex++;
            }
        }
        
        return new IndexColorModel(8, 256, reds, greens, blues);
    }
    
    /** This will show the save dialog and set the file and bitmap type used to save the bitmap in.
     * @param fileName is the name of the file without any extensions.
     */
    private void showSaveDialog(String fileName) {
        LinkedList<FileNameFilter> ltFileNameFilters = new LinkedList();
        
        // Current file types to save as bitmaps.
        FileNameFilter bitmapFileNameFilter = new FileNameFilter(".bmp", "Windows BITMAP");
        FileNameFilter jpgFileNameFilter = new FileNameFilter(".jpg", "JPG");
        FileNameFilter emfFileNameFilter = new FileNameFilter(".emf", "EMF");
        
        if (defaultBitmap == DEFAULTBITMAP_BMP) {
            ltFileNameFilters.add(emfFileNameFilter);
            ltFileNameFilters.add(jpgFileNameFilter);
            ltFileNameFilters.add(bitmapFileNameFilter);
        } else if (defaultBitmap == DEFAULTBITMAP_JPG) {
            ltFileNameFilters.add(emfFileNameFilter);
            ltFileNameFilters.add(bitmapFileNameFilter);
            ltFileNameFilters.add(jpgFileNameFilter);
        } else if (defaultBitmap == DEFAULTBITMAP_EMF) {
            ltFileNameFilters.add(jpgFileNameFilter);
            ltFileNameFilters.add(bitmapFileNameFilter);
            ltFileNameFilters.add(emfFileNameFilter);
        } else
            throw new IllegalStateException("Unknown default bitmap type: " + defaultBitmap);
        
        // Show the dialog
        DialogFileChooser dfChooser = new DialogFileChooser("writeBitmap", ltFileNameFilters, "Save As Bitmap Using [" + getName() + "] Setting");
        if (dfChooser.showSaveDialog(null, fileName, new ImagePreview()) == false) {
            fAbsolute = null;
            bImageOutput = null;
            return;
        }
        
        // Set the absolute file name.
        fAbsolute = dfChooser.getFile();
        
        // Get the bitmap type to save as.
        if (dfChooser.getUsedFilter() == bitmapFileNameFilter)
            bitmapType = DEFAULTBITMAP_BMP;
        else if (dfChooser.getUsedFilter() == emfFileNameFilter)
            bitmapType = DEFAULTBITMAP_EMF;
        else if (dfChooser.getUsedFilter() == jpgFileNameFilter)
            bitmapType = DEFAULTBITMAP_JPG;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface Comparable ">

    @Override
    public int compareTo(Object o) {
        if (o instanceof BitmapSettings == false)
            throw new ClassCastException("Object: " + o.toString() + " not compatible with BitmapSettings.");
        
        return name.compareTo(((BitmapSettings)o).name);
    }
    
    // </editor-fold>
    
    
}
/** This will save which BitmapSetting is currently being used.
 */
class BitmapSettingsSettings implements InterfaceSettings {
    static private String INDEX_BITMAPSETTING = "indexBitMapSetting";  // Integer
    
    public BitmapSettingsSettings() {
    }
    
    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.putInt(INDEX_BITMAPSETTING, BitmapSettings.getIndexCurrentSetting());
    }
    
    public void load() {
        // 0 is the default in BitmapSettings (Default BitmapSettings).
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        BitmapSettings.setIndexCurrentSetting(prefs.getInt(INDEX_BITMAPSETTING, 0));
    }
}

// </editor-fold>
