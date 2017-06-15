/*
 * ComplexPattern.java
 *
 * Created on November 11, 2006, 3:51 PM
 *
 */

package mlnr.draw;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mlnr.gui.FrameOperation;
import mlnr.util.DefaultExceptionHandler;
import org.w3c.dom.*;
import mlnr.util.XmlUtil;

/** This class is used for loading the .rxml file to be used as a Complex Pattern to draw and for Design preview.
 * @author Robert Molnar II
 */
public class ComplexPattern implements Comparator {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the current place this DesignTransform is located. Can be null if does not exists. Must always contain
     * ".rxml" at the end of it. */
    private String fileName = null;
    
    /** This is the absolute path of the current place to save the .rxml file. Starts off as null. */
    File fAbsolutePath = null;
    
    /** This contains extra information about the drawing when saving it such as email, web site, and author's name. */
    MetaDrawingInfo metaDrawingInfo = null;
    
    /** This is the drawing design that represents this complex pattern. */
    DrawingDesign design = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of ComplexPattern */
    public ComplexPattern() {
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public General Methods ">
    
    /** This will draw the pattern for a preview into a size of width and height.
     * @param width is the width of the preview.
     * @param height is the height of the preview.
     */
    public void drawPreview(Graphics2D g2d, float width, float height) {        
        // Get the bounding size of the design.
        Rectangle.Float fRect = design.getBounds2D();
        if (fRect == null)
            return;
        
        // calculate the scaling factor.
        float xScale = width / (float)fRect.getWidth();
        float yScale = height / (float)fRect.getHeight();      
        
        // Choose smaller one.
        float scale = xScale;
        if (yScale < xScale)
            scale = yScale;
        
        // Get the scaled width and height of the component.
        float widthScaled = width / scale;
        float heightScaled = height / scale;
        
        // Get the offset.
        float xOffset = (widthScaled - (float)fRect.getWidth()) / 2;
        float yOffset = (heightScaled - (float)fRect.getHeight()) / 2;
        
        // Transform the graphics and draw.
        g2d.setStroke(new BasicStroke(0.0f));
        g2d.scale(scale, scale);
        g2d.translate(-fRect.getX(), -fRect.getY());
        g2d.translate(xOffset+1.0f, yOffset);               // The +1 is too get it from clipping on the left side.
        
        design.draw(g2d);
    }
    
    /** @return the MetaDrawingInfo about the pattern.
     */
    public MetaDrawingInfo getMetaDrawingInfo() {
        return metaDrawingInfo;
    }

    /** @return the file name.
     */
    public String getFileName() {
        return fileName;
    }
    
    /** This will print out the file name.
     */
    public String toString() {
        return getFileName();
    }
    
    /** @return a new TransformDesign of this ComplexPattern. It have all of its vertices moveable and selected.
     * @throws IllegalStateException if design is null.
     */
    public TransformDesign toTransformDesign() {
        if (design == null)
            throw new IllegalStateException("Design does not exist.");        
        return TransformDesign.valueOf(design);
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Serialize Support ">
    
    /** This will load the drawing from a file.
     * @param fRxml is the Rxml file to load.
     * @param showErrorMessage is true if it should pop up an error message to the user.
     * @return The loaded ComplexPattern or null if unable to load it.
     */
    public static ComplexPattern open(File fRxml, boolean showErrorMessage) {
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
            
            // Each version is different therefore they required different loading techniques.
            String versionNumber = XmlUtil.getAttributeString(root, "v");
            ComplexPattern complexPattern = null;
            if ("1.0".equals(versionNumber))
                complexPattern = openVersion10(root);
            else if ("1.1".equals(versionNumber))
                complexPattern = openVersion11(root);
            else if ("2.0".equals(versionNumber))
                complexPattern = openVersion20(root);
            
            // Set the name of the file.
            complexPattern.fileName = fRxml.getName();
            complexPattern.fAbsolutePath = fRxml;
            
            return complexPattern;
        } catch (Exception e) {
            if (showErrorMessage)
                DefaultExceptionHandler.printExceptionToLog(e, "Unable to open the drawing at the path [" + fRxml.getAbsolutePath() + "].");
            else
                DefaultExceptionHandler.printExceptionToLog(e);
        }
        
        return null;
    }
    
    /** This will open version 1.0 rxml file. Coordinates are in integers and at a resolution of 20, therefore all coordinates
     * need to be converted to floating point numbers by dividing by 20.
     * @param root is the root element of the rxml file.
     */
    private static ComplexPattern openVersion10(Element root) throws Exception {
        ComplexPattern complexPattern = new ComplexPattern();                
        complexPattern.design = DrawingDesign.loadVersion10(root, new FrameOperation());
        complexPattern.metaDrawingInfo = MetaDrawingInfo.createEmpty();
        return complexPattern;
    }
    
    /** This will open version 1.1 rxml file. The difference between version 1.0 and 1.1 is the image tag is 
     * added to version 1.1. Coordinates are in integers and at a resolution of 20, therefore all coordinates
     * need to be converted to floating point numbers by dividing by 20.
     * @param root is the root element of the rxml file.
     */
    private static ComplexPattern openVersion11(Element root) throws Exception {
        ComplexPattern complexPattern = new ComplexPattern();        
        complexPattern.design = DrawingDesign.loadVersion10(root, new FrameOperation());
        complexPattern.metaDrawingInfo = MetaDrawingInfo.createEmpty();
        return complexPattern;
    }   
    
    /** This will open version 2.0 rxml file.
     * @param iFrameOperator is the interface used to operation the main frame.
     * @param root is the root element of the rxml file.
     */
    private static ComplexPattern openVersion20(Element root) throws Exception {
        Element eDesign = XmlUtil.getElementByTagName(root, "design");
        ComplexPattern complexPattern = new ComplexPattern();        
        complexPattern.design = DrawingDesign.loadVersion20(eDesign, new FrameOperation());
        complexPattern.metaDrawingInfo = MetaDrawingInfo.createEmpty();
        Element eDrawing = XmlUtil.getElementByTagName(root, "metaDrawingInfo");
        complexPattern.metaDrawingInfo.loadVersion20(eDrawing);
        return complexPattern;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface Comparator ">

    public int compare(Object o1, Object o2) {
        return ((ComplexPattern)o1).getFileName().compareTo(((ComplexPattern)o2).getFileName());
    }
    
    // </editor-fold>
    
}
