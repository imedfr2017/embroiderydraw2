/*
 * ImagePool.java
 *
 * Created on September 15, 2006, 12:31 PM
 *
 */

package mlnr.gui.cpnt;

import java.awt.Graphics2D;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.*;
import mlnr.draw.AbstractPool;
import mlnr.type.FPointType;
import mlnr.util.XmlUtil;

/** This is the class used to handle images in the drawing pad.
 * @author Robert Molnar II
 */
public class ImagePool extends AbstractPool {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    DrawingPad drawingPad;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor And Load Image">
    
    /** Creates a new instance of ImagePool */
    public ImagePool(DrawingPad drawingPad) {
        this.drawingPad = drawingPad;
    }
    
    public void loadImage(File f) {
        add(new ImageInfo(drawingPad, f, getLargestZDepth() + 100));
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Serialize Support ">
    
    /** This will load the image details found under the image element.
     * @param eImage is the image element.
     */
    public void loadVersion11(Element eImage) throws Exception {
        NodeList nList = eImage.getElementsByTagName("imageDetail");
        int length = nList.getLength();
        for (int i=0; i < length; i++) {
            Element elem = (Element)nList.item(i);
            ImageInfo iInfo = ImageInfo.loadVersion11(drawingPad, elem, getLargestZDepth() + 100);
            if (iInfo != null)
                add(iInfo);
        }        
    }

    /** This will load the image paths found under the imagePool element.
     * @param eImagePool is the imagePool element.
     */
    public void loadVersion20(Element eImagePool) throws Exception {
        NodeList nList = eImagePool.getElementsByTagName("image");
        int length = nList.getLength();
        for (int i=0; i < length; i++) {
            Element elem = (Element)nList.item(i);
            ImageInfo iInfo = ImageInfo.loadVersion20(drawingPad, elem);
            if (iInfo != null)
                add(iInfo);
        }
    }
    
    /** This will write out the image information.
     */
    public void write(PrintWriter out) throws Exception {
        out.println("  <imagePool>");
        for (Iterator itr = values().iterator(); itr.hasNext(); )
            ((ImageInfo)itr.next()).write(out);
        out.println("  </imagePool>");
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Get Methods ">
    
    /** @returns the images information as a sorted list of ImageInfo.
     */
    public LinkedList getImageInfos() {
        return valuesSorted();
    }
    
    /** @param imageId is the id of the image.
     * @return an ImageInfo corresponding to the id.
     */
    public ImageInfo getImageInfo(int imageId) {
        return (ImageInfo)get(imageId);
    }
    
    private int getLargestZDepth() {
        int zDepth = 0;
        for (Iterator itr = values().iterator(); itr.hasNext(); ) {
            ImageInfo iInfo = (ImageInfo)itr.next();
            if (iInfo.getZDepth() > zDepth)
                zDepth = iInfo.getZDepth();
        }
        
        return zDepth;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Draw Method ">
    
    /** This will draw the images in this ImagePool.
     */
    public void draw(Graphics2D g2d) {
        for (Iterator itr = valuesSortedReverse().iterator(); itr.hasNext(); ) {
            ImageInfo iInfo = (ImageInfo)itr.next();
            iInfo.draw(g2d);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Operation Methods ">
    
    /** This will delete the image from the this ImagePool.
     * @param id is the image id that needs to be deleted.
     */
    public void delete(int id) {
        ImageInfo ii = (ImageInfo)get(id);
        ii.onDelete();
        remove(ii);
    }
    
    /** This will get the image at the current position in the ImagePool.
     * @param fpt is the point where the image should be at.
     * @return the ImageInfo or null if none there.
     */
    public ImageInfo getImage(FPointType fpt) {
        for (Iterator itr = valuesSorted().iterator(); itr.hasNext(); ) {
            ImageInfo iInfo = (ImageInfo)itr.next();
            if (iInfo.isHit(fpt)) {
                return iInfo;
            }
        }
        
        return null;
    }
    
    /** This will restore the image to it's original size and rotation.
     * @param id is the image id that needs to be restored.
     */
    public void restore(int id) {
        ((ImageInfo)get(id)).restore();
    }
    
    /** This will restore all images to the original size of the image.
     */
    void restoreAll() {
        for (Iterator itr = values().iterator(); itr.hasNext();) {
            ImageInfo ii = (ImageInfo)itr.next();
            ii.restore();
        }
    }
    
    /** This will remove all images.
     */
    void removeAll() {
        LinkedList ltImagesDelete = new LinkedList();
        
        for (Iterator itr = values().iterator(); itr.hasNext();) {
            ImageInfo ii = (ImageInfo)itr.next();
            
            // Notify about deleting.
            ii.onDelete();
            
            // Store image to delete.
            ltImagesDelete.add(ii);
        }
        
        // Must perform it this way to not get a ConcurrentModificationException.
        for (Iterator itr = ltImagesDelete.iterator(); itr.hasNext(); ) {
            ImageInfo ii = (ImageInfo)itr.next();
            remove(ii);
        }
        
        System.gc();
    }
    
    /** This will reload all and transform the images.
     */
    void reloadAllAndTransform() {
        for (Iterator itr = values().iterator(); itr.hasNext(); ) {
            ImageInfo ii = (ImageInfo)itr.next();
            ii.reloadAndTransform();
        }
    }
    
    // </editor-fold>
    
}
