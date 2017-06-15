/*
 * PatternPanel.java
 *
 * Created on November 3, 2006, 4:28 PM
 */

package mlnr.gui.tool.opt;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.*;
import mlnr.draw.ComplexPattern;
import mlnr.draw.MetaDrawingInfo;
import mlnr.gui.cpnt.DesignPreview;
import mlnr.gui.dlg.DialogViewMetaInfo;
import mlnr.gui.dlg.FileNameFilter;
import mlnr.util.DefaultExceptionHandler;

/**
 *
 * @author  Robert Molnar II
 */
public class PatternPanel extends javax.swing.JPanel implements MouseListener, ActionListener {
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** main frame of this program.*/
    JFrame frame;
    
    /** This is the selected pattern. */
    ComplexPattern selectedPattern = null;
    
    /** This is a list of all loaded complex patterns (RXML files). */
    LinkedList<ComplexPattern> ltComplexPattern = new LinkedList();
    
    private JPopupMenu rightClickPattern = new JPopupMenu();
    private JPopupMenu rightClickFolder = new JPopupMenu();
    private JMenuItem menuReload = new JMenuItem("Reload Patterns");
    private JMenuItem menuShowDirectory = new JMenuItem("Show Directory");
    private JMenuItem menuSortByCategory = new JMenuItem("Sort by Category");
    private JMenuItem menuSortByAuthor = new JMenuItem("Sort by Author");
    private JMenuItem menuSortBySet = new JMenuItem("Sort by Set");
    private JMenuItem menuSortByAuthorCategory = new JMenuItem("Sort by Author, Category");
    private JMenuItem menuSortByAuthorSet = new JMenuItem("Sort by Author, Set");
    private JMenuItem menuViewMeta = new JMenuItem("View Details");
    
    private DesignPreview dPreview;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates new form PatternPanel */
    public PatternPanel(JFrame frame) {
        initComponents();
        
        this.frame = frame;
        
        // Group the radio buttons.
        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButtonResizeCenter);
        bg.add(jRadioButtonResizeUpperLeft);
        
        // Center and force one layer should be selected.
        jRadioButtonResizeCenter.setSelected(true);
        jCheckBoxForceOneLayer.setSelected(true);
        
        // Add the DesignPreview.
        dPreview = new DesignPreview(jPanelPreview.getWidth() - 1, jPanelPreview.getHeight() - 1);
        jPanelPreview.add(dPreview);
        
        // Only able to select one item at a time.
        jTreePatterns.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreePatterns.addMouseListener(this);
        
        // Create the pop-up menus.
        createPopupMenus();
        
        // Now load the patterns in.
        reloadPatterns();
        
        // Now sort and load the JTree.
        sortPatterns(LoadJTree.SORT_AUTHOR);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    
    /** @return the currently selected pattern or null.
     */
    public ComplexPattern getSelectedPattern() {
        return selectedPattern;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Query Methods ">
    
    public boolean isAspectRatioChecked() {
        return jCheckBoxAspectRatio.isSelected();
    }
    
    public boolean isForceOneLayerChecked() {
        return jCheckBoxForceOneLayer.isSelected();
    }
    
    public boolean isNoRotateChecked() {
        return jCheckBoxNoRotate.isSelected();
    }
    
    public boolean isOriginalSizeChecked() {
        return jCheckBoxOriginalSize.isSelected();
    }
    
    public boolean isCenterChecked() {
        return jRadioButtonResizeCenter.isSelected();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pattern Methods ">
    
    /** This will load the patterns which the user has so that they can be used. It will recursively load in each folder 
     * in the dir. This can be called multiple times if the user added new patterns and need to reload the patterns, just 
     * make sure to clear the LinkedList.
     * @param fDir is the location to load in the patterns.
     */
    private void loadPatterns(File fDir) {
        try {
            
            // Gets a list of tools in the directory.
            FileNameFilter toolFileNameFilter = new FileNameFilter(".rxml", "Embroidery Draw RXML");
            toolFileNameFilter.setAllowDirs(false);
            File []fTools = fDir.listFiles(toolFileNameFilter);
            
            // Now load each tool.
            for (int i=0; i < fTools.length; i++) {
                ComplexPattern cPattern = ComplexPattern.open(fTools[i], true);
                if (cPattern != null)
                    ltComplexPattern.add(cPattern);
            }
            
            // recursively load the directories.
            File []fDirs = fDir.listFiles();
            for (int i=0; i < fDirs.length; i++) {
                if (!fDirs[i].isDirectory())
                    continue;
                loadPatterns(fDirs[i]);
            }
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to load custom patterns from directory[" + fDir.getAbsolutePath() + "].");            
        }
    }
    
    /** This will resort the patterns in the JTree.
     * @param sortMethod is a LoadJTree.SORT_*
     */
    private void sortPatterns(int sortMethod) {
        LoadJTree load = new LoadJTree(jTreePatterns, ltComplexPattern);
        load.createTree(sortMethod);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static Public Methods ">
    
    /** This will show the directory where all the patterns are loaded from.
     */
    public void showDirectory() {
        try {
            Runtime.getRuntime().exec ("explorer.exe \"" + System.getProperty("user.home") + "\\embroideryDraw\"");
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to open directory where the patterns are stored.");            
        }
    }
    
    /** This will recursively reload all patters
     */
    public void reloadPatterns() {
        // Make sure file exists, if not then create folder.
        String toolsDir = System.getProperty("user.home") + "/embroideryDraw";
        File fToolsDir = new File(toolsDir);
        if (fToolsDir.isDirectory() == false)
            fToolsDir.mkdirs();
        
        // This will load the tools directory.
        Cursor oldCursor = getCursor();
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        ltComplexPattern.clear();
        loadPatterns(fToolsDir);
        LoadJTree load = new LoadJTree(jTreePatterns, ltComplexPattern);
        load.createTree();
        setCursor(oldCursor);
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" GUI Methods ">

    /** This will create the popup menus.
     */
    private void createPopupMenus() {        
        menuShowDirectory.setToolTipText("This will show the directory where the patterns are loaded from (must be rxml files).");
        menuReload.setToolTipText("This will reload all patterns found in the pattern directory (rxml files only).");
        
        rightClickFolder.add(menuSortByCategory);
        rightClickFolder.add(menuSortByAuthor);
        rightClickFolder.add(menuSortBySet);
        rightClickFolder.add(menuSortByAuthorCategory);
        rightClickFolder.add(menuSortByAuthorSet);
        rightClickFolder.addSeparator();
        rightClickFolder.add(menuShowDirectory);
        rightClickFolder.add(menuReload);
        
        rightClickPattern.add(menuViewMeta);        
        
        menuSortByCategory.addActionListener(this);
        menuSortByAuthor.addActionListener(this);
        menuSortBySet.addActionListener(this);
        menuSortByAuthorCategory.addActionListener(this);
        menuSortByAuthorSet.addActionListener(this);
        menuShowDirectory.addActionListener(this);
        menuReload.addActionListener(this);
        
        menuViewMeta.addActionListener(this);
        
    }

    private void selectPattern(MouseEvent evt) {
        DefaultTreeSelectionModel dtsm = (DefaultTreeSelectionModel)jTreePatterns.getSelectionModel();

        // Didn't select anything
        TreePath tPath = dtsm.getSelectionPath();
        if (tPath == null)
            return;

        // Get the select pattern.
        Object obj = ((DefaultMutableTreeNode)tPath.getLastPathComponent()).getUserObject();
        if (obj instanceof ComplexPattern)
            selectedPattern = ((ComplexPattern)obj);
        
        // Now update the panel which contains the preview.
        dPreview.changePattern(selectedPattern);
    }
    
    
    private void checkForTriggerEvent(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            DefaultTreeSelectionModel dtsm = (DefaultTreeSelectionModel)jTreePatterns.getSelectionModel();
            
            // Didn't select anything
            TreePath tPath = dtsm.getSelectionPath();
            if (tPath == null) {
                rightClickFolder.show(evt.getComponent(), evt.getX(), evt.getY());
                return;
            }
            
            Object obj = ((DefaultMutableTreeNode)tPath.getLastPathComponent()).getUserObject();
            if (obj instanceof ComplexPattern) {
                selectedPattern = ((ComplexPattern)obj);
                menuViewMeta.setText("View Details: " + selectedPattern.getFileName());
                rightClickPattern.show(evt.getComponent(), evt.getX(), evt.getY());
            } else 
                rightClickFolder.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface ActionListener ">

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == menuViewMeta)
            new DialogViewMetaInfo(frame, true, selectedPattern.getMetaDrawingInfo(), selectedPattern.getFileName()).setVisible(true);
        else if (obj == menuSortByCategory)
            sortPatterns(LoadJTree.SORT_CATEGORY);
        else if (obj == menuSortByAuthor)
            sortPatterns(LoadJTree.SORT_AUTHOR);
        else if (obj == menuSortBySet)
            sortPatterns(LoadJTree.SORT_SET);
        else if (obj == menuSortByAuthorCategory)
            sortPatterns(LoadJTree.SORT_AUTHOR_CATEGORY);
        else if (obj == menuSortByAuthorSet)
            sortPatterns(LoadJTree.SORT_AUTHOR_SET);
        else if (obj == menuShowDirectory)
            showDirectory();
        else if (obj == menuReload)
            reloadPatterns();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interface MouseListener ">

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        checkForTriggerEvent(e);
        if (!e.isPopupTrigger())
            selectPattern(e);
    }

    public void mouseReleased(MouseEvent e) {
        checkForTriggerEvent(e);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
    // </editor-fold>
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jCheckBoxAspectRatio = new javax.swing.JCheckBox();
        jCheckBoxNoRotate = new javax.swing.JCheckBox();
        jRadioButtonResizeCenter = new javax.swing.JRadioButton();
        jRadioButtonResizeUpperLeft = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreePatterns = new javax.swing.JTree();
        jPanelPreview = new javax.swing.JPanel();
        jCheckBoxOriginalSize = new javax.swing.JCheckBox();
        jCheckBoxForceOneLayer = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Pattern Options")));
        jCheckBoxAspectRatio.setText("Use Aspect Ratio For Size (X=Y)");
        jCheckBoxAspectRatio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxAspectRatio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBoxNoRotate.setText("No Rotate");
        jCheckBoxNoRotate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxNoRotate.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonResizeCenter.setText("Center");
        jRadioButtonResizeCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonResizeCenter.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonResizeUpperLeft.setText("Upper-Left");
        jRadioButtonResizeUpperLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonResizeUpperLeft.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jScrollPane1.setViewportView(jTreePatterns);

        jPanelPreview.setLayout(new java.awt.BorderLayout());

        jCheckBoxOriginalSize.setText("Use Original Size");
        jCheckBoxOriginalSize.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOriginalSize.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBoxForceOneLayer.setText("Load In As 1 Layer");
        jCheckBoxForceOneLayer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxForceOneLayer.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jCheckBoxAspectRatio)
            .add(layout.createSequentialGroup()
                .add(jCheckBoxOriginalSize)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBoxNoRotate)
                .addContainerGap(11, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(jRadioButtonResizeCenter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButtonResizeUpperLeft)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(jCheckBoxForceOneLayer)
                .addContainerGap())
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
            .add(jPanelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jCheckBoxAspectRatio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBoxOriginalSize)
                    .add(jCheckBoxNoRotate))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBoxForceOneLayer)
                .add(6, 6, 6)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonResizeCenter)
                    .add(jRadioButtonResizeUpperLeft))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxAspectRatio;
    private javax.swing.JCheckBox jCheckBoxForceOneLayer;
    private javax.swing.JCheckBox jCheckBoxNoRotate;
    private javax.swing.JCheckBox jCheckBoxOriginalSize;
    private javax.swing.JPanel jPanelPreview;
    private javax.swing.JRadioButton jRadioButtonResizeCenter;
    private javax.swing.JRadioButton jRadioButtonResizeUpperLeft;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTreePatterns;
    // End of variables declaration//GEN-END:variables
    
}
    
// <editor-fold defaultstate="collapsed" desc=" Class LoadJTree ">

/** This is used to load the JTree's node information.
 */
class LoadJTree {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This will sort the patterns by category. */
    public static final int SORT_CATEGORY = 1;
    /** This will sort the patterns by author. */
    public static final int SORT_AUTHOR = 2;
    /** This will sort the patterns by set. */
    public static final int SORT_SET = 3;
    /** This will sort the patterns by author then category. */
    public static final int SORT_AUTHOR_CATEGORY = 4;
    /** This will sort the patterns by author then set. */
    public static final int SORT_AUTHOR_SET = 5;
    /** This is the previously used sort. */
    private static int previousSort = SORT_CATEGORY;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    LinkedList<ComplexPattern> ltComplexPattern = new LinkedList();
    
    JTree jtree;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** @param jtree is the tree which needs to load the complex patterns into it.
     * @param ltComplexPattern is the list of loaded complex patterns.
     */
    LoadJTree(JTree jtree, LinkedList<ComplexPattern> ltComplexPattern) {
        this.ltComplexPattern = ltComplexPattern;
        this.jtree = jtree;
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Create Tree Methods ">
    
    /** This will use the previously used sorting method to create the tree.
     */
    void createTree() {
        createTree(previousSort);
    }
    
    /** This will create the tree using the sorting methods SORT_*.
     */
    void createTree(int sortFilter) {
        previousSort = sortFilter;
        
        // Get the tree model used for the JTree.
        DefaultTreeModel dTreeModel = (DefaultTreeModel)jtree.getModel();        
        // This is the root node for the JTree.
        DefaultMutableTreeNode root = null; 
        
        // Sort based on the sort method.
        switch (sortFilter) {
            case SORT_CATEGORY:
                root = new DefaultMutableTreeNode("Patterns [Sort Category]");
                createTreeSingleLevel(root, sortFilter);
                break;
            case SORT_AUTHOR:
                root = new DefaultMutableTreeNode("Patterns [Sort Author]");
                createTreeSingleLevel(root, sortFilter);
                break;
            case SORT_SET:
                root = new DefaultMutableTreeNode("Patterns [Sort Drawing Set]");
                createTreeSingleLevel(root, sortFilter);
                break;
            case SORT_AUTHOR_CATEGORY:
                root = new DefaultMutableTreeNode("Patterns [Sort Author Category]");
                createTreeDoubleLevel(root, sortFilter);
                break;
            case SORT_AUTHOR_SET:
                root = new DefaultMutableTreeNode("Patterns [Sort Author Set]");
                createTreeDoubleLevel(root, sortFilter);
                break;
        }
                
        // Set the root node for the JTree.
        dTreeModel.setRoot(root);
    }
    
    /** This will create the tree from the list of patterns by using a sorting filter.
     * @param root is the root node for the tree being built.
     * @param sortFilter is one of the SORT_* constants.
     */
    void createTreeSingleLevel(DefaultMutableTreeNode root, int sortFilter) {
        HashMap <String, LinkedList> hmFirstLevel = new HashMap();
        
        // Load in each item from the list.
        for (Iterator<ComplexPattern> itr = ltComplexPattern.iterator(); itr.hasNext(); ) {
            ComplexPattern pattern = itr.next();
            MetaDrawingInfo metaInfo = pattern.getMetaDrawingInfo();
            
            // Get the key.
            String key = null;
            switch (sortFilter) {
                case SORT_CATEGORY:
                    key = metaInfo.getCategory(true);
                    break;
                case SORT_AUTHOR:
                    key = metaInfo.getAuthorName(true);
                    break;
                case SORT_SET:
                    key = metaInfo.getSetName(true);
                    break;
            }
            
            // See if the list for the key exists if not then create it.
            LinkedList lt = hmFirstLevel.get(key);
            if (lt == null) {
                lt = new LinkedList();
                hmFirstLevel.put(key, lt);
            }
            
            // Insert the pattern to the list.            
            lt.add(pattern);            
        }
        
        // Sort the lists and HashMaps.
        for (Iterator<LinkedList> itr = hmFirstLevel.values().iterator(); itr.hasNext(); ) {
            Collections.sort(itr.next(), new ComplexPattern());
        }
        Set<Entry <String, LinkedList>> set = hmFirstLevel.entrySet();
        Object []objArray = set.toArray();
        Arrays.sort(objArray, new ComparatorEntry());
        
        // Now create the nodes.
        for (int i=0; i < objArray.length; i++) {
            Entry e = (Entry)objArray[i];            
            // Create the node.
            DefaultMutableTreeNode parent = new DefaultMutableTreeNode((String)e.getKey());
            // Add the complex patterns to the node.
            for (Iterator itr = ((LinkedList)e.getValue()).iterator(); itr.hasNext(); )
                parent.add(new DefaultMutableTreeNode(itr.next()));
            // Add the node to the root.
            root.add(parent);
        }        
    }
    
    /** This will create the tree from the list of patterns by using a sorting filter.
     * @param root is the root node for the tree being built.
     * @param sortFilter is one of the SORT_* constants.
     */
    void createTreeDoubleLevel(DefaultMutableTreeNode root, int sortFilter) {
        HashMap <String, HashMap> hmFirstLevel = new HashMap();
        
        // Load in each item from the list.
        for (Iterator<ComplexPattern> itr = ltComplexPattern.iterator(); itr.hasNext(); ) {
            ComplexPattern pattern = itr.next();
            MetaDrawingInfo metaInfo = pattern.getMetaDrawingInfo();
            
            // Get the keys to the hashmap.
            String key1 = metaInfo.getAuthorName(true);
            String key2 = null;
            switch (sortFilter) {
                case SORT_AUTHOR_CATEGORY:
                    key2 = metaInfo.getCategory(true);
                    break;
                case SORT_AUTHOR_SET:
                    key2 = metaInfo.getSetName(true);
                    break;
            }
            
            // See if the HashMap for the key exists if not then create it.
            HashMap hm = hmFirstLevel.get(key1);
            if (hm == null) {
                hm = new HashMap();
                hmFirstLevel.put(key1, hm);
            }
            // See if the list for the key exists if not then create it.
            LinkedList lt = (LinkedList)hm.get(key2);
            if (lt == null) {
                lt = new LinkedList();
                hm.put(key2, lt);
            }
            
            // Insert the pattern to the list.            
            lt.add(pattern);            
        }
        
        // Sort the lists.
        for (Iterator<HashMap> itrHM = hmFirstLevel.values().iterator(); itrHM.hasNext(); ) {
            HashMap hm = itrHM.next();
            for (Iterator itrLL = hm.values().iterator(); itrLL.hasNext(); ) {
                LinkedList ll = (LinkedList)itrLL.next();
                Collections.sort(ll, new ComplexPattern());
            }
        }
        // Sort the first level hashmaps.
        Set<Entry <String, HashMap>> set = hmFirstLevel.entrySet();
        Object []objArray = set.toArray();
        Arrays.sort(objArray, new ComparatorEntry());
        
        // Now create the nodes.
        for (int i=0; i < objArray.length; i++) {
            Entry e = (Entry)objArray[i];            
            
            // Create the first level node.
            DefaultMutableTreeNode parent = new DefaultMutableTreeNode((String)e.getKey());
            
            // Get the second level hashmap and sort it.
            HashMap hmSecondLevel = (HashMap)e.getValue();
            Set<Entry <String, LinkedList>> set2 = hmSecondLevel.entrySet();
            Object []objArray2 = set2.toArray();
            Arrays.sort(objArray2, new ComparatorEntry());

            // Now create the second level nodes.
            for (int k=0; k < objArray2.length; k++) {
                Entry e2 = (Entry)objArray2[k];            
                // Create the node.
                DefaultMutableTreeNode child = new DefaultMutableTreeNode((String)e2.getKey());
                // Add the complex patterns to the node.
                for (Iterator itr = ((LinkedList)e2.getValue()).iterator(); itr.hasNext(); )
                    child.add(new DefaultMutableTreeNode(itr.next()));
                // Add the node to the root.
                parent.add(child);
            }        
            
            root.add(parent);
        }        
    }
    
    // </editor-fold>
    
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" class ComparatorEntry ">

class ComparatorEntry implements Comparator {
    ComparatorEntry() {}

    public int compare(Object o1, Object o2) {
        String s1 = (String)((Entry)o1).getKey();
        String s2 = (String)((Entry)o2).getKey();
        return s1.compareTo(s2);
    }
}

// </editor-fold>
