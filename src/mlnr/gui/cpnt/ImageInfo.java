/*
 * ImageInfo.java
 *
 * Created on September 15, 2006, 12:34 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import org.w3c.dom.*;
import mlnr.Measurement;
import mlnr.draw.InterfacePoolObject;
import mlnr.type.FPointType;
import mlnr.util.DefaultExceptionHandler;
import mlnr.util.InterfaceSettings;
import mlnr.util.XmlUtil;

/** This class will use a single buffer for the image. That means the location of the image cannot change because it
 * will be reloaded when transforming. This will save on memory since only one buffer of the image needs to be in-memory.
 *
 * @author Robert Molnar II
 */
public class ImageInfo implements InterfacePoolObject {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields (Settings) ">
    
    /** This is the percentage of the images being lightening.  */
    static final float DEFAULT_IMAGE_LIGHTENING = 0.5f;
    static private float imageLighted = DEFAULT_IMAGE_LIGHTENING;
    
    /** This is the number of pixels per measurement for the images. 11.81 pixels = 1 measurement (DPI of 300). */
    static final float PPM_DEFAULT = 11.81f;
    private static float pixelsPerMeasurement = PPM_DEFAULT;
    
    /** This is true if it should resize the image to a default size when loading it. */
    private static boolean defaultSizeLoad = true;
    
    /** This is true if it should use the default size of the width, else false use the default size of the height. */
    private static boolean defaultSizeWidthHeigh = false;
    
    /** (In Measurements) This is the default size for the width. */
    static final float WIDTH_DEFAULT = 75.00f;
    private static float defaultSizeWidth = WIDTH_DEFAULT;
    
    /** (In Measurements) This is the default size for the height. */
    static final float HEIGHT_DEFAULT = 75.00f;
    private static float defaultSizeHeight = HEIGHT_DEFAULT;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    private DrawingPad drawingPad;
    
    /** This is the layer id, it should be unique to all other layers in a design. */
    private int id;
    
    /** This is the z-depth for sorting the layers to know which ones to draw before the others. */
    private int zDepth;
    
    /** This is the file location for the image.  */
    private File fImage;
    
    /** This is the image buffer for the transformed image. */
    private BufferedImage bufferedImage;
    
    // This is where the image will be drawn at it will be drawn at the center of the image starting at that position.
    private float xPos;
    private float yPos;
    
    // Original size of the image in measurements.
    private float xOrgSize;
    private float yOrgSize;
    
    // This is the size of the bitmap in measurements.
    private float xBitmapSize;
    private float yBitmapSize;
    
    // Corresponds to the bitmap transformed.
    private float xScale=1.0f;
    private float yScale=1.0f;
    
    private float rotate=0.0f;
    
    private GeneralPath gPath;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ImageInfo
     * @param fImage is the location of the image.
     */
    public ImageInfo(DrawingPad drawingPad, File fImage, int zDepth) {
        this.drawingPad = drawingPad;
        this.fImage = fImage;
        this.zDepth = zDepth;
        
        xPos = drawingPad.getDesign().getWidth() / 2.0f;
        yPos = drawingPad.getDesign().getHeight() / 2.0f;
        
        reload();
        if (DrawingPad.isImageLighten())
            transform();
        resizeToDefaultSize();
    }
        
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Serialize Support ">
    
    /** This will load the ImageInfo from the xml element.
     * @param drawingPad is the DrawingPad which this Image will be drawn to.
     * @param eImageDetail is the xml element for this ImageInfo.
     * @param zDepth is z position for this image, first image gets the lowest and the last image gets the largest.
     * @return a new ImageInfo constructed by the xml element or null if path to image does not exist. Id isn't set so it
     * must obtain a new id.
     */
    public static ImageInfo loadVersion11(DrawingPad drawingPad, Element eImageDetail, int zDepth) throws Exception {
        String path = XmlUtil.getAttributeString(eImageDetail, "absolutePath");
        double x = (double)XmlUtil.getAttributeInteger(eImageDetail, "xPos") / 20.0;
        double y = (double)XmlUtil.getAttributeInteger(eImageDetail, "yPos") / 20.0;
        double xScale = XmlUtil.getAttributeDouble(eImageDetail, "xScale");
        double yScale = XmlUtil.getAttributeDouble(eImageDetail, "yScale");
        double rotate = XmlUtil.getAttributeDouble(eImageDetail, "rotate");
        
        // Make sure file exists.
        File fPath = new File(path);
        if (fPath.isFile() == false)
            return null;
        
        // Create the new ImageInfo.
        ImageInfo iInfo = new ImageInfo(drawingPad, fPath, zDepth);
        iInfo.setPosition((float)x, (float)y);
        iInfo.setScale((float)xScale, (float)yScale);
        iInfo.setRotate((float)rotate);
        return iInfo;        
    }
    
    /** This will load the ImageInfo from the xml element.
     * @param drawingPad is the DrawingPad which this Image will be drawn to.
     * @param eImageInfo is the xml element for this ImageInfo.
     * @return a new ImageInfo constructed by the xml element or null if path to image does not exist.
     */
    public static ImageInfo loadVersion20(DrawingPad drawingPad, Element eImageInfo) throws Exception {
        int id = XmlUtil.getAttributeInteger(eImageInfo, "id");
        int zDepth = XmlUtil.getAttributeInteger(eImageInfo, "zDepth");
        String path = XmlUtil.getAttributeString(eImageInfo, "path");
        double x = XmlUtil.getAttributeDouble(eImageInfo, "x");
        double y = XmlUtil.getAttributeDouble(eImageInfo, "y");
        double xScale = XmlUtil.getAttributeDouble(eImageInfo, "xScale");
        double yScale = XmlUtil.getAttributeDouble(eImageInfo, "yScale");
        double rotate = XmlUtil.getAttributeDouble(eImageInfo, "rotate");
        
        // Make sure file exists.
        File fPath = new File(path);
        if (fPath.isFile() == false)
            return null;
        
        // Create the new ImageInfo.
        ImageInfo iInfo = new ImageInfo(drawingPad, fPath, zDepth);
        iInfo.setPosition((float)x, (float)y);
        iInfo.setScale((float)xScale, (float)yScale);
        iInfo.setRotate((float)rotate);
        return iInfo;
    }    
    
    /** This will write out the image information.
     */
    public void write(PrintWriter out) throws Exception {
        out.println("   <image id='" + id + "' zDepth='" + zDepth + "' path='" + XmlUtil.fixup(fImage.getAbsolutePath()) + "' x='"
                + xPos + "' y='" + yPos + "' xScale='" + xScale + "' yScale='" + yScale + "' rotate='" + rotate + "' />");
    }
    
    // </editor-fold>    
    
    /** This will draw the transformed image.
     */
    void draw(Graphics2D g2D) {
        // Make the current x,y position be the center of the image.
        float widthHalf = xBitmapSize / 2.0f;
        float heightHalf = yBitmapSize / 2.0f;
        
        Graphics2D gCopy = (Graphics2D)g2D.create();
        gCopy.translate(xPos - (widthHalf * xScale), yPos - (heightHalf * yScale));
        gCopy.scale(xScale / pixelsPerMeasurement, yScale / pixelsPerMeasurement);
        gCopy.drawImage(bufferedImage, null, 0, 0);
        
        // Testing out the rectangle.
        Color oldColor = g2D.getColor();
        Stroke oldStroke = g2D.getStroke();
        g2D.setColor(new Color(255,0,0));
        g2D.setStroke(new BasicStroke(0.0f));
        g2D.draw(gPath);
        g2D.setColor(oldColor);
        g2D.setStroke(oldStroke);
    }
    
    public String toString() {
        return "{ImageInfo:: id[" + id + "] name[" + fImage.getName() + "] zDepth[" + zDepth + "]}";
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Operation Methods ">
    
    /** @param fPt to find the image to delete.
     * @return true if point is inside of image.
     */
    boolean isHit(FPointType fPt) {
        return gPath.contains(fPt.x, fPt.y);
    }
    
    void restore() {
        // Original size of the image.
        xScale = 1.0f;
        yScale = 1.0f;
        
        // restore the rotate.
        if (rotate != 0.0f) {
            rotate = 0.0f;
            reload();
            transform();
        }
        
        updateGeneralPath();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Get Methods ">
    
    /** @return the zDepth.
     */
    public int getZDepth() {
        return zDepth;
    }
    
    /** @param zDepth is the new zDepth to set this ImageInfo to.
     */
    public void setZDepth(int zDepth) {
        this.zDepth = zDepth;
    }
    
    /** @return name of the image.
     */
    public String getName() {
        return fImage.getName();
    }
    
    /** @return the image associated with this ImageInfo.
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
    
    /** @return x position in measurements.
     */
    public float getXPosition() {
        return xPos;
    }
    
    /** @return y position in measurements.
     */
    public float getYPosition() {
        return yPos;
    }
    
    /** @return x size in measurements.
     */
    public float getXSize() {
        return xOrgSize * xScale;
    }
    
    /** @return y size in measurements.
     */
    public float getYSize() {
        return yOrgSize * yScale;
    }
    
    /** @return rotate in radians.
     */
    public float getRotate() {
        return rotate;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Set Methods ">
    
    /** This will set the size of the image. If the measurements are less than
     * 0.1 then it will not set the image size.
     * @param width is the new width to set the image.
     * @param height is the new height to set the image.
     */
    public void setSize(float width, float height) {
        if (width < 0.01 || height < 0.01)
            return;
        
        xScale = width / xOrgSize;
        yScale = height / yOrgSize;
        updateGeneralPath();
    }
    
    /** This will set the scale of the image. If the measurements are less than
     * 0.000001 then it will not set the image size.
     * @param xScale is the new xScale to set the image.
     * @param yScale is the new yScale to set the image.
     */
    public void setScale(float xScale, float yScale) {
        if (xScale < 0.000001 || yScale < 0.000001)
            return;
        
        this.xScale = xScale;
        this.yScale = yScale;
        updateGeneralPath();
    }
    
    /** This will set the position of the image. Note that the position is where the image
     * will be drawn at the center.
     * @param x is the new x position.
     * @param y is the new y position.
     */
    public void setPosition(float x, float y) {
        xPos = x;
        yPos = y;
        updateGeneralPath();
    }
    
    /** This will set the rotate of the image.
     * @param rad is the rotation to rotate the image by.
     */
    public void setRotate(float rad) {
        // make sure the rotation is positive and is within 0 to Math.PI.
        float pi2 = (float)Math.PI * 2;
        while (rad > pi2) {
            rad -= pi2;
        }        
        while (rad < 0.0f) {
            rad += pi2;
        }                
        
        rotate = rad;
        updateGeneralPath();
        reload();
        transform();
    }
    
    /** This will reload and transform the images.
     */
    public void reloadAndTransform() {
        reload();
        transform();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic ">
    
    /** This will transform the image by rotating it and lightening it if need be. However do not call this if neither of them need to be
     * performed as this is a costly function.
     */
    private void transform() {
        if (rotate == 0.0f && DrawingPad.isImageLighten() == false)
            return;
        
        // This is the coordinates of the bitmap in pixels.
        GeneralPath gPath = new GeneralPath(new Rectangle2D.Float(0, 0, (float)bufferedImage.getWidth(), (float)bufferedImage.getHeight()));
        
        // Create an AffineTransform with the needed rotation.
        AffineTransform transform = new AffineTransform();
        transform.rotate(rotate);
        gPath.transform(transform);
        
        // This is the new size of the bitmap that will be able to contain the rotated image.
        Rectangle2D fRect = gPath.getBounds2D();
        
        // Get the graphics information.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        
        // Create an image that supports transparent pixels
        BufferedImage biTransformed = gc.createCompatibleImage((int)fRect.getWidth() + 1, (int)fRect.getHeight() + 1, Transparency.BITMASK);
        
        // Clear image with transparent alpha by drawing a rectangle
        Graphics2D gTrans = biTransformed.createGraphics();
        
        int transformedWidth = biTransformed.getWidth(null);
        int transformedHeight = biTransformed.getHeight(null);
        
        // Move the original image to the center of the transformed image.
        float xTrans = (transformedWidth - bufferedImage.getWidth()) / 2;
        float yTrans = (transformedHeight - bufferedImage.getHeight()) / 2;
        
        // Middle point of transformed image.
        float xMiddlePoint = transformedWidth / 2;
        float yMiddlePoint = transformedHeight / 2;
        
        if (rotate != 0.0f) {
            // Move image to center of the zero axis.
            gTrans.translate(xMiddlePoint, yMiddlePoint);

            // Rotate the image.
            gTrans.rotate(rotate);

            // Now move image to center of the transformed image.
            gTrans.translate(-xMiddlePoint, -yMiddlePoint);
        }
        
        // Now lighten the image and draw it if need be.
        if (DrawingPad.isImageLighten()) {
            if (bufferedImage.getTransparency() == BufferedImage.OPAQUE) {
                Color c = new Color(1.0f, 1.0f, 1.0f, 0.5f);
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.setColor(c);
                g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
                gTrans.drawImage(bufferedImage, null, (int)xTrans, (int)yTrans);
            } else {
                  LightenFilter lf = new LightenFilter(imageLighted);
                  int width = bufferedImage.getWidth();
                  int height = bufferedImage.getHeight();
                  for (int x=0; x < width; x++) {
                      for (int y=0; y < height; y++)
                          bufferedImage.setRGB(x, y, lf.filterRGB(x, y, bufferedImage.getRGB(x, y)));
                  }
                  gTrans.drawImage(bufferedImage, null, (int)xTrans, (int)yTrans);
            }
        } else {
            // Draw the image the original into the transformed.
            gTrans.drawImage(bufferedImage, null, (int)xTrans, (int)yTrans);
        }
        
        bufferedImage.flush();
        bufferedImage = biTransformed;
        xBitmapSize = transformedWidth / pixelsPerMeasurement;
        yBitmapSize = transformedHeight / pixelsPerMeasurement;
        
        // Run the garbage collector after this operation since there will be waste to collect.
        System.gc();
    }
    
    /** This will reload the image from disk.
     * @throws IllegalStateException ImageInfo::reload() Unable to reload the file [] in path [].
     */
    private void reload() {
        try {
            // Load the image.
            if (bufferedImage != null) {
                bufferedImage.flush();
            }
            
            bufferedImage = ImageIO.read(fImage);
            MediaTracker mt = new MediaTracker(drawingPad);
            mt.addImage(bufferedImage, 0);
            mt.waitForID(0);
            mt.removeImage(bufferedImage);
            
            int type = bufferedImage.getType();
            
            // Get the graphics information.
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();

            // Create an image that supports transparent pixels
            BufferedImage biTransformed = gc.createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(), Transparency.BITMASK);
            biTransformed.createGraphics().drawImage(bufferedImage, null, 0, 0);
            bufferedImage.flush();
            bufferedImage = biTransformed;
            
            // Set the image size.
            xOrgSize = bufferedImage.getWidth() / pixelsPerMeasurement;
            yOrgSize = bufferedImage.getHeight() / pixelsPerMeasurement;
            xBitmapSize = xOrgSize;
            yBitmapSize = yOrgSize;
            
            updateGeneralPath();
            
            // Run the garbage collector after this operation since there will be waste.
            System.gc();
            
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to reload the file [" + fImage.getName() + "] in path [" + fImage.getPath() + "]. ");
        }
    }
    
    /** This will resize the image to the default size.
     */
    private void resizeToDefaultSize() {
        if (defaultSizeLoad == false)
            return;
        
        float scale;
        if (defaultSizeWidthHeigh) { // Width
            scale = defaultSizeWidth / xOrgSize;
            
            // Check to make sure that height isn't larger than the max size of the drawing if so then use the y scale to the max size.
            if (yOrgSize * scale > drawingPad.getDesign().getHeight()) {
                scale = drawingPad.getDesign().getHeight() / yOrgSize;
            }
        } else {
            // Height
            scale = defaultSizeHeight / yOrgSize;
            
            // Check to make sure that width isn't larger than the max size of the drawing if so then use the x scale to the max size.
            if (xOrgSize * scale > drawingPad.getDesign().getWidth()) {
                scale = drawingPad.getDesign().getWidth() / xOrgSize;
            }
        }
        
        xScale = scale;
        yScale = scale;
        
        updateGeneralPath();
    }
    
    /** This will update the general path that represents the bitmap bounds.
     */
    private void updateGeneralPath() {
        AffineTransform transform = new AffineTransform();
        float widthHalf = xOrgSize / 2.0f;
        float heightHalf = yOrgSize / 2.0f;
        
        // Rotated first and then it is scaled.

        // Create the GeneralPath with the original size.
        gPath = new GeneralPath(new Rectangle2D.Float(-widthHalf, -heightHalf, xOrgSize, yOrgSize));
        
        // Rotate it.
        transform.rotate(rotate);
        gPath.transform(transform);
        
        // Now scale it.
        transform = new AffineTransform();
        transform.scale(xScale, yScale);
        gPath.transform(transform);
        
        // Now move it.
        transform = new AffineTransform();
        transform.translate(xPos, yPos);
        gPath.transform(transform);
        
//        // Create the GeneralPath with the original size.
//        gPath = new GeneralPath(new Rectangle2D.Float(-widthHalf * xScale, -heightHalf * yScale, xOrgSize * xScale, yOrgSize * yScale));
//        
//        // Rotate it.
//        transform.rotate(rotate);
//        gPath.transform(transform);
//        
//        // Now move it.
//        transform = new AffineTransform();
//        transform.translate(xPos, yPos);
//        gPath.transform(transform);
        
//        AffineTransform transform = new AffineTransform();
//        float widthHalf = xOrgSize / 2.0f;
//        float heightHalf = yOrgSize / 2.0f;
//
//        // Create the GeneralPath without it being rotated.
//        gPath = new GeneralPath(new Rectangle2D.Float(xPos - (widthHalf * xScale), yPos - (heightHalf * yScale),
//                xOrgSize * xScale, yOrgSize * yScale));
//
//        // Now rotate it.
//        transform.rotate(rotate);
//        gPath.transform(transform);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" User Settings Methods ">
    
    static float getPixelsPerMeasurement() {
        return pixelsPerMeasurement;
    }
    
    static void setPixelsPerMeasurement(float ppm) {
        pixelsPerMeasurement = ppm;
    }
    
    /** @return DPI used to load in the images.
     */
    public static float getDPI() {
        return pixelsPerMeasurement * Measurement.convertInchesToMeasurement(1.0f);
    }
    
    /** This will set the dpi to load in the images.
     * @param dpi is the dpi used to load in the images.
     */
    public static void setDPI(float dpi) {
        pixelsPerMeasurement = dpi / Measurement.convertInchesToMeasurement(1.0f);
    }
    
    /** @return return true if to use default size to load in images.
     */
    public static boolean isUseDefaultSize() {
        return defaultSizeLoad;
    }
    
    /** @param enable to enable or not the default size to load in images.
     */
    public static void setUseDefaultSize(boolean enable) {
        defaultSizeLoad = enable;
    }
    
    /** @return return true if to use width, else false to use height.
     */
    public static boolean isUseWidthOrHeight() {
        return defaultSizeWidthHeigh;
    }
    
    /** @param true if to use width, else false to use height.
     */
    public static void setUseWidthOrHeight(boolean widthHeight) {
        defaultSizeWidthHeigh = widthHeight;
    }
    
    /** @return the default size used to load in images.
     */
    public static float getDefaultSizeWidth() {
        return defaultSizeWidth;
    }
    
    /** @param the new default size used to load in images.
     */
    public static void setDefaultSizeWidth(float newWidth) {
        defaultSizeWidth = newWidth;
    }
    
    /** @return the default size used to load in images.
     */
    public static float getDefaultSizeHeight() {
        return defaultSizeHeight;
    }
    
    /** @param the new default size used to load in images.
     */
    public static void setDefaultSizeHeight(float newHeight) {
        defaultSizeHeight = newHeight;
    }
    
    /** This will restore all settings to default.
     */
    public static void restoreAllSettings() {
        pixelsPerMeasurement = PPM_DEFAULT;
        defaultSizeLoad = true;
        defaultSizeWidthHeigh = false;
        defaultSizeWidth = WIDTH_DEFAULT;
        defaultSizeHeight = HEIGHT_DEFAULT;
        imageLighted = DEFAULT_IMAGE_LIGHTENING;
    }
    
    /** @return the percentage which the images need to be lightened to.
     */
    public static float getImageLightenPercentage() {
        return imageLighted;
    }
    
    /** @param the percentage which the images need to be lightened to.
     */
    public static void setImageLightenPercentage(float percentage) {                
        imageLighted = percentage;
    }
    
    /** @return a the class responsible for saving/loading the settings in this class.
     */
    public static InterfaceSettings getSettings() {
        return new ImageInfoSettings();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface InterfacePoolObject ">
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int compareTo(Object o) {
        return ((ImageInfo)o).zDepth - zDepth;
    }
    
    // </editor-fold>

    void onDelete() {
        bufferedImage.flush();
    }
    
}

// <editor-fold defaultstate="collapsed" desc=" class LightenFilter ">

/** Filter that lightens and preserves transparency.
 */
class LightenFilter extends RGBImageFilter {
    private int lighten, red, green, blue, alpha, temp;
    
    public LightenFilter(float percentage) {
        // When this is set to true, the filter will work with images
        // whose pixels are indices into a color table (IndexColorModel).
        // In such a case, the color values in the color table are filtered.
        canFilterIndexColorModel = true;
        lighten = (int)(percentage * 255);
    }
    
    // This method is called for every pixel in the image
    public int filterRGB(int x, int y, int rgb) {
        alpha = rgb & 0xff000000;
        
        temp = ((rgb >> 16) & 0x000000ff) + lighten;
        if (temp > 255) temp = 255;
        red = temp << 16;
        
        temp = ((rgb >> 8) & 0x000000ff) + lighten;
        if (temp > 255) temp = 255;
        green = temp << 8;
        
        temp = (rgb & 0x000000ff) + lighten;
        if (temp > 255) temp = 255;
        blue = temp;
        
        
        return alpha + red + green + blue;
    }
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" class ImageInfoSettings ">

/** This will save the vertex settings
 */
class ImageInfoSettings implements InterfaceSettings {
    static private String PPM = "ImageInfoPPM";  // Float
    static private String DEFAULTSIZELOAD = "ImageInfodefaultSizeLoad"; // Boolean
    static private String DEFAULTSIZEWIDTHEIGHT = "ImageInfoDefaultSizeWidthHeight"; // boolean
    static private String DEFAULTSIZEWIDTH = "ImageInfoDefaultSizeWidth"; // float
    static private String DEFAULTSIZEHEIGHT = "ImageInfoDefaultSizeHeight"; //float
    static private String LIGHTENPERCENTAGE = "ImageInfoLightPercentage"; // float
    
    public ImageInfoSettings() {
    }
    
    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.putFloat(PPM, ImageInfo.getPixelsPerMeasurement());
        prefs.putBoolean(DEFAULTSIZELOAD, ImageInfo.isUseDefaultSize());
        prefs.putBoolean(DEFAULTSIZEWIDTHEIGHT, ImageInfo.isUseWidthOrHeight());
        prefs.putFloat(DEFAULTSIZEWIDTH, ImageInfo.getDefaultSizeWidth());
        prefs.putFloat(DEFAULTSIZEHEIGHT, ImageInfo.getDefaultSizeHeight());
        prefs.putFloat(LIGHTENPERCENTAGE, ImageInfo.getImageLightenPercentage());
        
    }
    
    public void load() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        ImageInfo.setPixelsPerMeasurement(prefs.getFloat(PPM, ImageInfo.PPM_DEFAULT));
        ImageInfo.setUseDefaultSize(prefs.getBoolean(DEFAULTSIZELOAD, true));
        ImageInfo.setUseWidthOrHeight(prefs.getBoolean(DEFAULTSIZEWIDTHEIGHT, true));
        ImageInfo.setDefaultSizeWidth(prefs.getFloat(DEFAULTSIZEWIDTH, ImageInfo.WIDTH_DEFAULT));
        ImageInfo.setDefaultSizeHeight(prefs.getFloat(DEFAULTSIZEHEIGHT, ImageInfo.HEIGHT_DEFAULT));
        ImageInfo.setImageLightenPercentage(prefs.getFloat(LIGHTENPERCENTAGE, ImageInfo.DEFAULT_IMAGE_LIGHTENING));
    }
}

// </editor-fold>
