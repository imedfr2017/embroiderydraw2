/*
 * DialogFileChooser.java
 *
 * Created on September 15, 2006, 3:20 PM
 *
 */

package mlnr.gui.dlg;

import java.awt.Component;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import mlnr.gui.cpnt.AccessoryFileChooser;

/** This class is used to create a file chooser that can be an open or load one.
 * @author Robert Molnar II
 */
public class DialogFileChooser {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the unique string that identifies this dialog file chooser. Used to save the path location that it was last used in. */
    String uniqueId;
    
    /** This is the list of filters used in this dialog file chooser. */
    LinkedList<FileNameFilter> ltFileNameFilter;
    
    /** This is the title of the dialog box. */
    String title;

    /** This is the file the user choose. */
    File fileChoosen;
    
    /** This is the filter the user used. */
    javax.swing.filechooser.FileFilter filterUsed;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of DialogFileChooser 
     * @param uniqueId must be a unique string that identifies this dialog file chooser from all other dialog file chooser ids.
     * in the Vertex package as an example.
     * @param fileNameFilter is the filter used for this file chooser. Can be null.
     * @param title is the name of the file chooser it will show up in the dialog box as it's name.
     */
    public DialogFileChooser(String uniqueId, FileNameFilter fileNameFilter, String title) {
        this.uniqueId = "DialogFC_" + uniqueId;
        this.title = title;
        
        ltFileNameFilter = new LinkedList();
        if (fileNameFilter != null)
            ltFileNameFilter.add(fileNameFilter);
    }
    
    /** Creates a new instance of DialogFileChooser 
     * @param uniqueId must be a unique string that identifies this dialog file chooser from all other dialog file chooser ids.
     * in the Vertex package as an example.
     * @param ltFileNameFilter is a list of filters used for this file chooser. If this is empty then it will use the all filter than.
     * @param title is the name of the file chooser it will show up in the dialog box as it's name.
     */
    public DialogFileChooser(String uniqueId, LinkedList<FileNameFilter> ltFileNameFilter, String title) {
        this.uniqueId = "DialogFC_" + uniqueId;
        this.ltFileNameFilter = ltFileNameFilter;
        this.title = title;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    
    /** This will show the save dialog box using the selectedFile as the file to save as.
     * @param c is the component that the dialog box will show up at.
     * @param selectedFile is the file to save as. Can be null if no file to set selected.
     * @param jAccessory is the accessory shown in the dialog, can be null.
     * @return true if the user selected a file to save to, else false user cancelled.
     */
    public boolean showSaveDialog(Component c, String selectedFile, AccessoryFileChooser jAccessory) {
        return show(c, true, selectedFile, jAccessory);        
    }
    
    /** This will show the load dialog box using the selectedFile as the file to load as.
     * @param c is the component that the dialog box will show up at.
     * @param selectedFile is the file to load as.
     * @param jAccessory is the accessory shown in the dialog, can be null.
     */
    public boolean showLoadDialog(Component c, String selectedFile, AccessoryFileChooser jAccessory) {
        return show(c, false, selectedFile, jAccessory);        
    }
    
    /** This is the file choosen by the user.
     * @return the file choosen by the user.
     */
    public File getFile() {
        return fileChoosen;
    }

    /** @return the filter the user used.
     */
    public javax.swing.filechooser.FileFilter getUsedFilter() {
        return filterUsed;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Logic ">
    
    /** This will show the save dialog box using the selectedFile as the file to save as.
     * @param c is the component that the dialog box will show up at.
     * @param saveOpen is true if it should be a save dialog box or false if it should be open dialog box.
     * @param selectedFile is the file to save as. Can be null if no file to set selected.
     * @param jAccessory is the accessory shown in the dialog, can be null.
     * @return true if the user selected a file to save to, else false user cancelled.
     */
    private boolean show(Component c, boolean saveOpen, String selectedFile, AccessoryFileChooser jAccessory) {
        JEnchancedFileChooser fileChooser = null;
        
        // Work around the Exception being thrown for some users. BUG 4711700 bugs.sun.com
        try {
            fileChooser = new JEnchancedFileChooser();
        } catch (Exception e) {
                try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                fileChooser = new JEnchancedFileChooser();
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
        }
                
        // Turn off all file filter if there are filters.
        if (ltFileNameFilter.isEmpty() == false)
            fileChooser.setAcceptAllFileFilterUsed(false);
        
        // Set the dialog title.
        fileChooser.setDialogTitle(title);
        
        // Add the filters into the file chooser.
        for (Iterator itr = ltFileNameFilter.iterator(); itr.hasNext(); ) {
            FileNameFilter fileNameFilter = (FileNameFilter)itr.next();
            fileChooser.addChoosableFileFilter(fileNameFilter);
        }
        
        // set the Accessory
        if (jAccessory != null) {
            jAccessory.setJFileChooser(fileChooser);
            fileChooser.addPropertyChangeListener(jAccessory);
            fileChooser.setAccessory(jAccessory);
        }
        
        // Set the selected file.
        if (selectedFile != null)
            fileChooser.setSelectedFile(new File(selectedFile));
        
        // Set up the open load path.
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        String openPath = prefs.get(uniqueId, null);
        if (openPath != null) {
            File f = new File(openPath);
            if (f.exists() && f.isDirectory())
                fileChooser.setCurrentDirectory(f);
        }
        
        // Show the dialog box.
        int returnValue = 0;
        if (saveOpen)
            returnValue = fileChooser.showSaveDialog(c);
        else
            returnValue = fileChooser.showOpenDialog(c);                    
        
        // If the dialog
        if (returnValue != JFileChooser.APPROVE_OPTION)
            return false;
        
        // Set the open load path.
        prefs.put(uniqueId, fileChooser.getCurrentDirectory().getAbsolutePath());
        
        // This is the filter used.
        filterUsed = (FileNameFilter)fileChooser.getFileFilter();
        
        // This is the file the user choose.
        fileChoosen = fileChooser.getSelectedFile();
        if (saveOpen && filterUsed instanceof FileNameFilter)
            fileChoosen = new File(((FileNameFilter)filterUsed).addExtension(fileChoosen.getAbsolutePath()));
        
        return true;
    }
    
    // </editor-fold>
    
}

class JEnchancedFileChooser extends JFileChooser {
    int savedDialogType = OPEN_DIALOG;
    
    public void setDialogType(int dialogType) {
        savedDialogType = dialogType;
        super.setDialogType(dialogType);
    }
    
    public void approveSelection() {        
        // If saving and file exists.
        if (savedDialogType == SAVE_DIALOG) {
            File f = getSelectedFile();
            
            // Filter the filepath if a filter is being used.
            FileFilter ff = getFileFilter();
            if (ff instanceof FileNameFilter) {
                String absolutePath = ((FileNameFilter)ff).addExtension(f.getAbsolutePath());
                f = new File(absolutePath);
            }
                
            if (f.exists()) {
                if (new JOptionPane().showConfirmDialog(this, f.getAbsolutePath() 
                + " already exists. Do you want to replace it?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                    return;                
            }
        }
        
        super.approveSelection();
    }

}
