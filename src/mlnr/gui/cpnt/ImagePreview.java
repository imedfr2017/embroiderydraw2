/** File came from Swing Hacks Chapter 2. Hack #31.
 */

package mlnr.gui.cpnt;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import mlnr.Measurement;
import mlnr.util.DefaultExceptionHandler;

public class ImagePreview extends AccessoryFileChooser implements PropertyChangeListener {
    private JFileChooser jfc;
    private Image img;
    
    public ImagePreview() {
        Dimension sz = new Dimension(200,220);
        setPreferredSize(sz);
    }
    
    public void setJFileChooser(JFileChooser jfc) {
        this.jfc = jfc;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            File file = jfc.getSelectedFile();
            updateImage(file);
        } catch (IOException ex) {
            DefaultExceptionHandler.printExceptionToLog(ex);
        }
    }
    
    public void updateImage(File file) throws IOException {
        if(file == null || file.isFile() == false) {
            return;
        }
        
        img = ImageIO.read(file);
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        // fill the background
        g.setColor(Color.gray);
        g.fillRect(0,0,getWidth(),getHeight());
        
        if(img != null) {
            // calculate the scaling factor
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            int side = Math.max(w,h);
            double scale = 200.0/(double)side;
            w = (int)(scale * (double)w);
            h = (int)(scale * (double)h);
            
            // draw the image
            g.drawImage(img,0,0,w,h,null);
            
            // draw the image dimensions
            String tex = Measurement.getTextualName();
            
            String dim = img.getWidth(null) + " x " + img.getHeight(null) + " pixels";  
            
            g.setColor(Color.black);
            g.drawString(dim,2,216);
                        
            // Add warning if image is too large.
            if (img.getWidth(null) > 2000 || img.getHeight(null) > 2000) {
                dim = "Warning: Large image can";
                g.drawString(dim,2,236);
                dim = "slow down Embroidery Draw.";
                g.drawString(dim,2,256);
            }
            
        } else {
            
            // print a message
            g.setColor(Color.black);
            g.drawString("Not a valid image",30,100);
        }
    }
    
    
    
    public static void main(String[] args) {
        JFileChooser jfc = new JFileChooser();
        ImagePreview preview = new ImagePreview();
        preview.setJFileChooser(jfc);
        jfc.addPropertyChangeListener(preview);
        jfc.setAccessory(preview);
        jfc.showOpenDialog(null);
    }
}
