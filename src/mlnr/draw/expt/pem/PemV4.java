/*
 * PemV4.java
 *
 * Created on November 17, 2006, 4:00 PM
 *
 */

package mlnr.draw.expt.pem;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import mlnr.draw.DrawingDesign;
import mlnr.draw.GeneralTree;
import mlnr.draw.GeneralTreeNode;
import mlnr.draw.GeneralTreeSegment;
import mlnr.draw.LayerInfo;
import mlnr.gui.dlg.DialogFileChooser;
import mlnr.gui.dlg.FileNameFilter;
import mlnr.type.*;
import mlnr.util.DefaultExceptionHandler;

/** This class will output the pem file in version 4 format.
 *
 * @author Robert Molnar II
 */
public class PemV4 {
    
    // <editor-fold defaultstate="collapsed" desc=" Static Fields ">
    
    /** This is the number which all BTREE ids get added to before they are printed out. */
    private static final int BTREE_ADDITION=10000000;
    
    /** This is the number which all BTREE ids get incremented so that they are all unique. */
    private static final int BTREE_INCREMENT=1000;
    
    /** This is the scale used from the Measurements to PEM measurements */
    private static final float SCALE = 20.0f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    
    /** This is the current unique btree id. Use the BTREE_INCREMENT before a new tree is created. */
    private int uniqueBTree=0;
    
    /** This is the current unique bnode id. */
    private int uniqueBNode=0;
    
    /** This is the writer for the PEM v4 file. */
    PrintWriter out;
    
    /** This is the design to write as a PEM v4 file. */
    DrawingDesign design;
    
    /** This is the design starting position. */
    Point ptDesignStart = new Point();
    
    /** This is the design ending position. */
    Point ptDesignEnd = new Point();
    
    /** List of TreeType. */
    LinkedList<TreeType> ltTreeType = new LinkedList();
    
    /** This is the name of the design without the .rxml. */
    String fileName;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    
    /** Creates a new instance of PemV4.
     */
    public PemV4(DrawingDesign d, String fileName) {
        this.design = d;
        this.fileName = fileName;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Save Method ">
    
    /**  This will save the Design as a PEM.
     * @param layerSave is the layer to save or if null than all.
     */
    public void save(LayerInfo layerSave) {
        try {
            // Get the file to save to.
            File fPemSave = getSaveFile();
            if (fPemSave == null)
                return;
            
            // Build and print out the PEM file.
            PrintWriter out = new PrintWriter(fPemSave);
            build(layerSave);
            print(out);
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e, "Unable to save the design as a PEM.");
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Get Save File Method ">
    
    /** @return the file to save the pem to. Can be null if no file selected.
     */
    private File getSaveFile() {
        // Get the file name filters.
        FileNameFilter supportFilter = new FileNameFilter(".pem", "PEM file");
        
        // Setup the dialog file chooser and get the absolute path.
        DialogFileChooser dfChooser = new DialogFileChooser("pemSaveDrawing", supportFilter, "Save Drawing As Pem");
        if (dfChooser.showSaveDialog(null, fileName + ".pem", null))
            return dfChooser.getFile();
        return null;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Print Methods ">
    
    /** This will print the PEM structure out. 
     */
    private void print(PrintWriter out) {
        
        // Print header information.
        printHeader(out);
        
        // Print out each TREE
        for (Iterator<TreeType> itr = ltTreeType.iterator(); itr.hasNext(); ) {
            TreeType tree = itr.next();
            tree.print(out);
        }
        
        out.close();
    }
    
    /** This will print out the header information.
     */
    private void printHeader(PrintWriter out) {
        out.println("@VERSION 40000");
        out.println("@UNIT 11");
        out.println("@COOD 2 0 0 8000 8000");
        out.println();
        out.println("@SYSENV " + (int)design.getWidth() +  " " + (int)design.getHeight() + " 19 7 5 5.000000 0 0 1 0");
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Build Methods ">
    
    /** This will build the PEM structure.
     * @param layerSave is the layer to save or if null than all.
     */
    private void build(LayerInfo layerSave) {
        calculateDesignDimension();
        
        LinkedList<GeneralTree> ltGeneralTree = design.buildGeneralTrees(layerSave);
        for (Iterator<GeneralTree> itr = ltGeneralTree.iterator(); itr.hasNext(); ) {
            GeneralTree gt = itr.next();
            
            // Need to convert tree to trinary and compress the segments. Note that this will introduce dummy nodes and
            // multiple segments.
            gt.convertToTrinaryTree();
            gt.convertToCompressedTree();
            
            // Build the TreeType from the GeneralTree.
            ltTreeType.add(new TreeType(gt));
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Calculate Methods ">
    
    /** This will calculate the design dimensions used in the PEM file based on the internal dimensions used in the program.
     */
    private void calculateDesignDimension() {
        // The size of the design.
        int designWidth = convertToPEM(design.getWidth());
        int designHeight = convertToPEM(design.getHeight());;
        
        ptDesignStart.x = 2000 - (designWidth / 2);
        ptDesignStart.y = 2000 - (designHeight / 2);
        
        ptDesignEnd.x = 2000 + (designWidth / 2);
        ptDesignEnd.y = 2000 + (designHeight / 2);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Conversion Methods ">
    
    /** This will convert the Measurement based fpt to PEM measurement.
     * @param fpt is the point in Measurement.
     * @return PEM measurement of the point.
     */
    static Point convertToPEM(FPointType fpt) {
        Point pt = new Point();
        pt.x = (int)(fpt.x * SCALE + .5f);
        pt.y = (int)(fpt.y * SCALE + .5f);
        return pt;
    }
    
    /** This will convert the Measurement based to PEM measurement.
     * @param measurement is in native measurement.
     * @return PEM measurement of the native measurement.
     */
    static int convertToPEM(float measurement) {
        return (int)(measurement * SCALE + .5f);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class TreeType ">
    
    /** This class represents the @TREE structure in the PEM file format.
     */
    class TreeType {
        private int id;
        private BNodeType root;
        
        /** @param gt is the GeneralTree which this TreeType will be based off of.
         */
        TreeType(GeneralTree gt) {
            uniqueBTree += BTREE_INCREMENT;
            id = BTREE_ADDITION + uniqueBTree;
            
            build(gt);
        }
        
        /** This will build the TreeType.
         */
        private void build(GeneralTree gt) {
            // Restart the counter for BNodes.
            uniqueBNode=0;
            
            // This is the root BNodeType.
            root = new BNodeType(gt.getRoot());
        }
        
        /** This will print out this Tree.
         */
        void print(PrintWriter out) {
            out.println("@TREE " + id + " -1 -1 -1 -1 1.000000 1.000000 " + ptDesignStart.x + " " + ptDesignStart.y
                    + " " + ptDesignEnd.x + " " + ptDesignEnd.y + " " + ptDesignStart.x + " " + ptDesignStart.y + " " + ptDesignEnd.x + " " + ptDesignEnd.y + " 19 0");
            
            // Print out the BNODES.
            root.print(out);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Class BNodeType ">
    
    class BNodeType {
        
        // <editor-fold defaultstate="collapsed" desc=" Fields ">
        
        /** Parent BNode of this BNode. */
        private BNodeType parent = null;
        /** Id assigned to it. */
        private int id;
        /** List of points for this BNodeType. */
        private LinkedList<Point> ltPoints = new LinkedList();
        /** List of BNodes that are under it. */
        private LinkedList<BNodeType> ltBNode = new LinkedList();
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Constructors ">
        
        /** Create the root BNode.
         * @param root is the
         */
        BNodeType(GeneralTreeNode root) {
            uniqueBNode += 1;
            id = uniqueBTree + uniqueBNode;
            
            // There should only be one point for the root.
            ltPoints.add(convertToPEM(root.getNodePosition()));
            
            // Now create BNodes of the next nodes belog this segment.
            for (Iterator<GeneralTreeSegment> itr = root.getChildrenSegments().iterator(); itr.hasNext(); ) {
                BNodeType bnode = new BNodeType(this, root, itr.next());
                ltBNode.add(bnode);
            }
        }
        
        /** This will build the BNODE.
         * @param parent is the parent BNODE to this BNODE.
         * @param node is the starting position of this BNODE.
         * @param segment is used to produce the points for this BNODE.
         */
        BNodeType(BNodeType parent, GeneralTreeNode node, GeneralTreeSegment segment) {
            uniqueBNode += 1;
            id = uniqueBTree + uniqueBNode;
            
            this.parent = parent;
            
            // process the segment or if dummy segment then get the parent's position and use that.
            if (segment.isDummySegment() == false)
                processSegmentPoints(segment);
            else {
                if (node.isRootNode())
                    ltPoints.add(convertToPEM(node.getNodePosition()));
                else {
                    GeneralTreeNode nodeParent = node.getParentNotDummy();
                    ltPoints.add(convertToPEM(nodeParent.getNodePosition()));
                }
            }
            
            // Now create BNodes of the next nodes belog this segment.
            GeneralTreeNode childNode = segment.getToNode();
            for (Iterator<GeneralTreeSegment> itr = childNode.getChildrenSegments().iterator(); itr.hasNext(); ) {
                BNodeType bnode = new BNodeType(this, childNode, itr.next());
                ltBNode.add(bnode);
            }
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Print Methods ">
        
        void print(PrintWriter out) {
            // Number of points in this BNODE.
            int size = ltPoints.size();
            
            // Print the BNODE's data except the child's id and bnodes.
            if (parent == null)
                printRoot(out);
            else if (size == 1)
                printDummy(out);
            else
                printNormal(out, size);
            
            // Print out the children's ids and bnodes.
            printChildren(out);
            
            // Finish the line.
            out.println();
            
            // Print out the children now.
            for (Iterator itr = ltBNode.iterator(); itr.hasNext(); )
                ((BNodeType)itr.next()).print(out);
        }
        
        /** This will print out the children ids and bnodes.
         */
        void printChildren(PrintWriter out) {
            out.print(ltBNode.size() + " ");
            for (Iterator<BNodeType> itr = ltBNode.iterator(); itr.hasNext(); ) {
                BNodeType child = itr.next();
                out.print(child.id + " 0 0 ");
            }
        }
        
        /** This will print the BNODE as if it is a root.
         */
        void printRoot(PrintWriter out) {
            Point pt = ltPoints.getLast();
            out.print("@BNODE " + id + " -1 " + pt.x + " " + pt.y + " 1 0 2 20 0 0 ");
        }
        
        /** This will print the BNODE as if it is a dummy.
         */
        void printDummy(PrintWriter out) {
            Point pt = ltPoints.getLast();
            out.print("@BNODE " + id + " " + parent.id + " " + pt.x + " " + pt.y + " 1 0 2 20 0 2 " + " " + pt.x + " " + pt.y + " " + pt.x + " " + pt.y + " ");
        }
        
        /** This will print the BNODE as if it is a normal BNODE (more than 1 point).
         * @param size is the number of points in the list.
         */
        void printNormal(PrintWriter out, int size) {
            Point ptLast = ltPoints.getLast();
            out.print("@BNODE " + id + " " + parent.id + " " + ptLast.x + " " + ptLast.y + " 1 0 2 20 0 " + size + " ");
            
            // Print out the points.
            for (Iterator<Point> itr = ltPoints.iterator(); itr.hasNext(); ) {
                Point pt = itr.next();
                out.print(pt.x + " " + pt.y + " ");
            }
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Process Methods ">
                
        /** This will process the points from the segment into the correct distance from each other and
         * convert them into PEM measurement space.
         * @param segment is the segment to process its points.
         */
        private void processSegmentPoints(GeneralTreeSegment segment) {
            // Sample using roughly 100 points per segment. Multiple will have more.
            LinkedList<LinkedList<SFPointType>> ltSampledPoints = segment.getSampledPoints(100);
            // Run the points through the sample process.
            SampledPointProcess sampleProcess = new SampledPointProcess(ltSampledPoints);
            ltPoints = sampleProcess.sample();
        }
        
        // </editor-fold>        
    }
    
    // </editor-fold>
}
