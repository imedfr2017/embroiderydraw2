/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mlnr.gui.gen;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import mlnr.draw.AbstractLineInfo;
import mlnr.draw.BezierInfo;
import mlnr.draw.DrawingDesign;
import mlnr.draw.LineInfo;
import mlnr.draw.TransformGraph;
import mlnr.gui.dlg.DialogAdvanceDollieGenerator;
import mlnr.gui.dlg.DialogSimpleDollieGenerator;
import mlnr.type.FPointType;

/**
 *
 * @author Rob
 */
public class DollieGenerator extends PolygonGenerator {
    // <editor-fold defaultstate="collapsed" desc=" Fields ">
    /** This is the design to place the generated items into. */
    private DrawingDesign design;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Constructor ">
    /** Creates a new instance of FreeStandingLaceGenerator */
    public DollieGenerator(DrawingDesign design) {
        this.design = design;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Public Methods ">
    /** This will generate the advance dollies.
     * @param dialog contains the options the user has choosen to create the dollie.
     * @param generateItems is the DialogSimpleDollieGenerator.WHOLE_PIE or SLICE or INNER_PIE. Each one could be OR'd together.
     */
    public void generateAdvanceDollie(DialogAdvanceDollieGenerator dialog, int generateItems) {
        FPointType fptCenterPt = new FPointType(design.getWidth() / 2.0f, design.getHeight() / 2.0f);
        TransformGraph tGraph = null;

        // Generate the graphs.
        if (generateItems == DialogAdvanceDollieGenerator.WHOLE_PIE)
            tGraph = generateAdvanceWholePie(dialog);

        if (generateItems == DialogAdvanceDollieGenerator.SLICE)
            tGraph = generateAdvanceSlice(dialog);

        if (generateItems == DialogAdvanceDollieGenerator.INNER_PIE)
            tGraph = generateAdvanceInnerPie(dialog);

        tGraph.setAllTransformable(true);
        if (generateItems == DialogAdvanceDollieGenerator.SLICE)
            tGraph.center(fptCenterPt);
        else
            tGraph.translate(fptCenterPt.x, fptCenterPt.y);
        tGraph.finalizeMovement();
        design.add(tGraph);
    }

    /** This will generate the simple dollie.
     * @param dialog contains the options the user has choosen to create the dollie.
     * @param generateItems is the DialogSimpleDollieGenerator.WHOLE_PIE or SLICE or BOTH.
     */
    public void generateSimpleDollie(DialogSimpleDollieGenerator dialog, int generateItems) {
        FPointType fptCenterPt = new FPointType(design.getWidth() / 2.0f, design.getHeight() / 2.0f);

        if (generateItems == DialogSimpleDollieGenerator.WHOLE_PIE) {
            // Generate the whole pie only and add it to the design.
            TransformGraph tGraph = generateWholePie(dialog);
            tGraph.setAllTransformable(true);
            tGraph.translate(fptCenterPt.x, fptCenterPt.y);
            tGraph.finalizeMovement();
            design.add(tGraph);

        } else if (generateItems == DialogSimpleDollieGenerator.SLICE) {
            // Generate the slice only and add it to the design.
            TransformGraph tGraph = generateSlice(dialog);
            tGraph.setAllTransformable(true);
            tGraph.center(fptCenterPt);
            tGraph.finalizeMovement();
            design.add(tGraph);

        } else {
            // Generate the whole pie.
            TransformGraph tGraphPie = generateWholePie(dialog);
            tGraphPie.setAllTransformable(true);
            // Generate the slice.
            TransformGraph tGraphSlice = generateSlice(dialog);
            tGraphSlice.setAllTransformable(true);

            // Now both whole pie and slice are at the center of their graphs, need to move them so that they don't overlap each other.
            Rectangle2D.Float rectPie = tGraphPie.getBounds2D();
            Rectangle2D.Float rectSlice = tGraphSlice.getBounds2D();

            double totalWidth = rectPie.getWidth() + rectSlice.getWidth();
            FPointType fptCenterPie = new FPointType(fptCenterPt.x + totalWidth / 4 + 5.0f, fptCenterPt.y);
            FPointType fptCenterSlice = new FPointType(fptCenterPt.x - totalWidth / 4 - 5.0f, fptCenterPt.y);

            // Now center and add graphs into the design.
            tGraphPie.center(fptCenterPie);
            tGraphSlice.center(fptCenterSlice);
            tGraphPie.finalizeMovement();
            tGraphSlice.finalizeMovement();
            design.addUndoMarker();
            design.add(tGraphPie);
            design.add(tGraphSlice);
            design.completeUndo();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Advance Private Methods ">
    /** This will generate a slice at 90 degree angle. 
     * @param dialog contains the options the user has choosen to create the dollie.
     * @return a graph of the slice.
     */
    private TransformGraph generateAdvanceSlice(DialogAdvanceDollieGenerator dialog) {
        LinkedList<AbstractLineInfo> ltLineInfo = new LinkedList(); // Used to contain all the line information.

        boolean outerStraight = (dialog.getOuterPath() == DialogAdvanceDollieGenerator.STRAIGHT);
        boolean innerStraight = (dialog.getInnerPath() == DialogAdvanceDollieGenerator.STRAIGHT);
        int sliceCount = dialog.getSliceCount();
        float innerRadius = dialog.getInnerRadius();
        float outerRadius = dialog.getOuterRadius() + innerRadius;
        double radianSliceDegree = (Math.PI * 2.0f) / sliceCount; // radian degree per slice.

        // See http://en.wikipedia.org/wiki/Bezier_curves under Terminology section and http://whizkidtech.redprince.net/bezier/circle/
        double kappa = (4.0 / 3.0) * Math.tan(radianSliceDegree / 4);
        double lengthInnerControlPoint = kappa * innerRadius;
        double lengthOuterControlPoint = kappa * outerRadius;

        // The start and end radian degrees of the slice being worked on.
        double startRadian = Math.PI / 2 - radianSliceDegree / 2;
        double endRadian = Math.PI / 2 + radianSliceDegree / 2;

        // Get the points on the circle where those start and end radians are at.
        FPointType ptInnerStart = new FPointType(innerRadius * Math.cos(startRadian), innerRadius * Math.sin(startRadian));
        FPointType ptInnerEnd = new FPointType(innerRadius * Math.cos(endRadian), innerRadius * Math.sin(endRadian));
        FPointType ptOuterStart = new FPointType(outerRadius * Math.cos(startRadian), outerRadius * Math.sin(startRadian));
        FPointType ptOuterEnd = new FPointType(outerRadius * Math.cos(endRadian), outerRadius * Math.sin(endRadian));

        // Add the lines from the inner to outer circles.
        ltLineInfo.add(new LineInfo(ptInnerStart, ptOuterStart));
        ltLineInfo.add(new LineInfo(ptInnerEnd, ptOuterEnd));

        // If straight lines then add line from start to end for inner and outer.
        if (innerStraight)
            ltLineInfo.add(new LineInfo(ptInnerStart, ptInnerEnd));
        if (outerStraight)
            ltLineInfo.add(new LineInfo(ptOuterStart, ptOuterEnd));

        // Curved, now calculate the bezier curve between start and end. See, http://en.wikipedia.org/wiki/Bezier_curves in the terminology section about the (4/3)tan(t/4) details.

        if (innerStraight == false) {
            // Calculate the control point from ptStart Inner.
            double controlRadian = startRadian + Math.PI / 2;
            FPointType ptControlStart = new FPointType(ptInnerStart.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerStart.y + Math.sin(controlRadian) * lengthInnerControlPoint);

            // Calculate the control point from ptEnd.
            controlRadian = endRadian - Math.PI / 2;
            FPointType ptControlEnd = new FPointType(ptInnerEnd.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerEnd.y + Math.sin(controlRadian) * lengthInnerControlPoint);

            ltLineInfo.add(new BezierInfo(ptInnerStart, ptInnerEnd, ptControlStart, ptControlEnd));
        }

        if (outerStraight == false) {
            // Calculate the control point from ptStart Inner.
            double controlRadian = startRadian + Math.PI / 2;
            FPointType ptControlStart = new FPointType(ptOuterStart.x + Math.cos(controlRadian) * lengthOuterControlPoint, ptOuterStart.y + Math.sin(controlRadian) * lengthOuterControlPoint);

            // Calculate the control point from ptEnd.
            controlRadian = endRadian - Math.PI / 2;
            FPointType ptControlEnd = new FPointType(ptOuterEnd.x + Math.cos(controlRadian) * lengthOuterControlPoint, ptOuterEnd.y + Math.sin(controlRadian) * lengthOuterControlPoint);

            ltLineInfo.add(new BezierInfo(ptOuterStart, ptOuterEnd, ptControlStart, ptControlEnd));
        }

        // Now Place the information into the Design at the center position.
        TransformGraph tGraph = new TransformGraph();
        tGraph.addAll(ltLineInfo);

        return tGraph;
    }

    /** This will generate the inner pie. 
     * @param dialog contains the options the user has choosen to create the dollie.
     * @return a graph of the inner pie.
     */
    private TransformGraph generateAdvanceInnerPie(DialogAdvanceDollieGenerator dialog) {
        LinkedList<AbstractLineInfo> ltLineInfo = new LinkedList(); // Used to contain all the line information.
        FPointType ptCenter = new FPointType();

        boolean innerStraight = (dialog.getInnerPath() == DialogAdvanceDollieGenerator.STRAIGHT);
        int sliceCount = dialog.getSliceCount();
        float innerRadius = dialog.getInnerRadius();
        double radianSliceDegree = (Math.PI * 2.0f) / sliceCount; // radian degree per slice.

        // See http://en.wikipedia.org/wiki/Bezier_curves under Terminology section and http://whizkidtech.redprince.net/bezier/circle/
        double kappa = (4.0 / 3.0) * Math.tan(radianSliceDegree / 4);
        double lengthInnerControlPoint = kappa * innerRadius;

        // Generate the Inner Pie.
        for (int i = 0; i < sliceCount; i++) {
            // The start and end radian degrees of the slice being worked on.
            double startRadian = i * radianSliceDegree;
            double endRadian = (i + 1) * radianSliceDegree;

            // Get the points on the circle where those start and end radians are at.
            FPointType ptInnerStart = new FPointType(innerRadius * Math.cos(startRadian), innerRadius * Math.sin(startRadian));
            FPointType ptInnerEnd = new FPointType(innerRadius * Math.cos(endRadian), innerRadius * Math.sin(endRadian));

            // Add the lines from the inner to outer circles.
            // ltLineInfo.add(new LineInfo(ptCenter, ptInnerStart));
            // ltLineInfo.add(new LineInfo(ptCenter, ptInnerEnd));

            // If straight lines then add line from start to end for inner and outer.
            if (innerStraight)
                ltLineInfo.add(new LineInfo(ptInnerStart, ptInnerEnd));

            // Curved, now calculate the bezier curve between start and end. See, http://en.wikipedia.org/wiki/Bezier_curves in the terminology section about the (4/3)tan(t/4) details.

            if (innerStraight == false) {
                // Calculate the control point from ptStart Inner.
                double controlRadian = startRadian + Math.PI / 2;
                FPointType ptControlStart = new FPointType(ptInnerStart.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerStart.y + Math.sin(controlRadian) * lengthInnerControlPoint);

                // Calculate the control point from ptEnd.
                controlRadian = endRadian - Math.PI / 2;
                FPointType ptControlEnd = new FPointType(ptInnerEnd.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerEnd.y + Math.sin(controlRadian) * lengthInnerControlPoint);

                ltLineInfo.add(new BezierInfo(ptInnerStart, ptInnerEnd, ptControlStart, ptControlEnd));
            }
        }

        // Now Place the information into the Design at the center position.
        TransformGraph tGraph = new TransformGraph();
        tGraph.addAll(ltLineInfo);
        return tGraph;
    }

    /** This will generate the whole pie. The advance whole pie has an inner pie inside it.
     * @param dialog contains the options the user has choosen to create the dollie.
     * @return a graph of the whole dollie pie.
     */
    private TransformGraph generateAdvanceWholePie(DialogAdvanceDollieGenerator dialog) {
        LinkedList<AbstractLineInfo> ltLineInfo = new LinkedList(); // Used to contain all the line information.

        boolean outerStraight = (dialog.getOuterPath() == DialogAdvanceDollieGenerator.STRAIGHT);
        boolean innerStraight = (dialog.getInnerPath() == DialogAdvanceDollieGenerator.STRAIGHT);
        int sliceCount = dialog.getSliceCount();
        float innerRadius = dialog.getInnerRadius();
        float outerRadius = dialog.getOuterRadius() + innerRadius;
        double radianSliceDegree = (Math.PI * 2.0f) / sliceCount; // radian degree per slice.

        // See http://en.wikipedia.org/wiki/Bezier_curves under Terminology section and http://whizkidtech.redprince.net/bezier/circle/
        double kappa = (4.0 / 3.0) * Math.tan(radianSliceDegree / 4);
        double lengthInnerControlPoint = kappa * innerRadius;
        double lengthOuterControlPoint = kappa * outerRadius;

        // Generate the Whole Pie.
        for (int i = 0; i < sliceCount; i++) {
            // The start and end radian degrees of the slice being worked on.
            double startRadian = i * radianSliceDegree;
            double endRadian = (i + 1) * radianSliceDegree;

            // Get the points on the circle where those start and end radians are at.
            FPointType ptInnerStart = new FPointType(innerRadius * Math.cos(startRadian), innerRadius * Math.sin(startRadian));
            FPointType ptInnerEnd = new FPointType(innerRadius * Math.cos(endRadian), innerRadius * Math.sin(endRadian));
            FPointType ptOuterStart = new FPointType(outerRadius * Math.cos(startRadian), outerRadius * Math.sin(startRadian));
            FPointType ptOuterEnd = new FPointType(outerRadius * Math.cos(endRadian), outerRadius * Math.sin(endRadian));

            // Add the lines from the inner to outer circles.
            ltLineInfo.add(new LineInfo(ptInnerStart, ptOuterStart));
            ltLineInfo.add(new LineInfo(ptInnerEnd, ptOuterEnd));

            // If straight lines then add line from start to end for inner and outer.
            if (innerStraight)
                ltLineInfo.add(new LineInfo(ptInnerStart, ptInnerEnd));
            if (outerStraight)
                ltLineInfo.add(new LineInfo(ptOuterStart, ptOuterEnd));

            // Curved, now calculate the bezier curve between start and end. See, http://en.wikipedia.org/wiki/Bezier_curves in the terminology section about the (4/3)tan(t/4) details.

            if (innerStraight == false) {
                // Calculate the control point from ptStart Inner.
                double controlRadian = startRadian + Math.PI / 2;
                FPointType ptControlStart = new FPointType(ptInnerStart.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerStart.y + Math.sin(controlRadian) * lengthInnerControlPoint);

                // Calculate the control point from ptEnd.
                controlRadian = endRadian - Math.PI / 2;
                FPointType ptControlEnd = new FPointType(ptInnerEnd.x + Math.cos(controlRadian) * lengthInnerControlPoint, ptInnerEnd.y + Math.sin(controlRadian) * lengthInnerControlPoint);

                ltLineInfo.add(new BezierInfo(ptInnerStart, ptInnerEnd, ptControlStart, ptControlEnd));
            }

            if (outerStraight == false) {
                // Calculate the control point from ptStart Inner.
                double controlRadian = startRadian + Math.PI / 2;
                FPointType ptControlStart = new FPointType(ptOuterStart.x + Math.cos(controlRadian) * lengthOuterControlPoint, ptOuterStart.y + Math.sin(controlRadian) * lengthOuterControlPoint);

                // Calculate the control point from ptEnd.
                controlRadian = endRadian - Math.PI / 2;
                FPointType ptControlEnd = new FPointType(ptOuterEnd.x + Math.cos(controlRadian) * lengthOuterControlPoint, ptOuterEnd.y + Math.sin(controlRadian) * lengthOuterControlPoint);

                ltLineInfo.add(new BezierInfo(ptOuterStart, ptOuterEnd, ptControlStart, ptControlEnd));
            }
        }

        // Now Place the information into the Design at the center position.
        TransformGraph tGraph = new TransformGraph();
        tGraph.addAll(ltLineInfo);
        return tGraph;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Simple Private Methods ">
    /** This will generate a slice from the whole pie. It will center around 90 degrees.
     * @param dialog contains the options the user has choosen to create the dollie.
     * @return a graph of the slice.
     */
    private TransformGraph generateSlice(DialogSimpleDollieGenerator dialog) {
        LinkedList<AbstractLineInfo> ltLineInfo = new LinkedList(); // Used to contain all the line information.

        boolean straight = (dialog.getPath() == DialogSimpleDollieGenerator.STRAIGHT);
        int sliceCount = dialog.getSliceCount();
        float diameter = dialog.getDiameterMeasurement();
        float radius = diameter / 2.0f;
        double radianSliceDegree = (Math.PI * 2.0f) / sliceCount; // radian degree per slice.        
        FPointType ptCenter = new FPointType();

        // See http://en.wikipedia.org/wiki/Bezier_curves under Terminology section and http://whizkidtech.redprince.net/bezier/circle/
        double kappa = (4.0 / 3.0) * Math.tan(radianSliceDegree / 4);
        double lengthControlPoint = kappa * radius;

        // Center the slice around the 90 degree angle.
        double startRadian = Math.PI / 2 - radianSliceDegree / 2;
        double endRadian = Math.PI / 2 + radianSliceDegree / 2;

        // Get the points on the circle where those start and end radians are at.
        FPointType ptStart = new FPointType(radius * Math.cos(startRadian), radius * Math.sin(startRadian));
        FPointType ptEnd = new FPointType(radius * Math.cos(endRadian), radius * Math.sin(endRadian));

        // Add the lines from the start point to center and end point to center.
        ltLineInfo.add(new LineInfo(ptCenter, ptStart));
        ltLineInfo.add(new LineInfo(ptCenter, ptEnd));

        // If straight lines then add line from start to end.
        if (straight)
            ltLineInfo.add(new LineInfo(ptStart, ptEnd));
        else {

            // Curved, now calculate the bezier curve between start and end. See, http://en.wikipedia.org/wiki/Bezier_curves in the terminology section about the (4/3)tan(t/4) details.

            // Calculate the control point from ptStart.
            double controlRadian = startRadian + Math.PI / 2;
            FPointType ptControlStart = new FPointType(ptStart.x + Math.cos(controlRadian) * lengthControlPoint, ptStart.y + Math.sin(controlRadian) * lengthControlPoint);

            // Calculate the control point from ptEnd.
            controlRadian = endRadian - Math.PI / 2;
            FPointType ptControlEnd = new FPointType(ptEnd.x + Math.cos(controlRadian) * lengthControlPoint, ptEnd.y + Math.sin(controlRadian) * lengthControlPoint);

            ltLineInfo.add(new BezierInfo(ptStart, ptEnd, ptControlStart, ptControlEnd));
        }

        // Now create the TransformGraph and add in the items into it.
        TransformGraph tGraph = new TransformGraph();
        tGraph.addAll(ltLineInfo);
        return tGraph;
    }

    /** This will generate the whole pie of a dollie into the design.
     * @param dialog contains the options the user has choosen to create the dollie.
     * @return TransformGraph of the whole pie. It will then need to be added into the design.
     * @return a graph of the whole pie.
     */
    private TransformGraph generateWholePie(DialogSimpleDollieGenerator dialog) {
        LinkedList<AbstractLineInfo> ltLineInfo = new LinkedList(); // Used to contain all the line information.

        boolean straight = (dialog.getPath() == DialogSimpleDollieGenerator.STRAIGHT);
        int sliceCount = dialog.getSliceCount();
        float diameter = dialog.getDiameterMeasurement();
        float radius = diameter / 2.0f;
        double radianSliceDegree = (Math.PI * 2.0f) / sliceCount; // radian degree per slice.        
        FPointType ptCenter = new FPointType();

        // See http://en.wikipedia.org/wiki/Bezier_curves under Terminology section and http://whizkidtech.redprince.net/bezier/circle/
        double kappa = (4.0 / 3.0) * Math.tan(radianSliceDegree / 4);
        double lengthControlPoint = kappa * radius;

        // Generate the Whole Pie.
        for (int i = 0; i < sliceCount; i++) {
            // The start and end radian degrees of the slice being worked on.
            double startRadian = i * radianSliceDegree;
            double endRadian = (i + 1) * radianSliceDegree;

            // Get the points on the circle where those start and end radians are at.
            FPointType ptStart = new FPointType(radius * Math.cos(startRadian), radius * Math.sin(startRadian));
            FPointType ptEnd = new FPointType(radius * Math.cos(endRadian), radius * Math.sin(endRadian));

            // Add the lines from the start point to center and end point to center.
            ltLineInfo.add(new LineInfo(ptCenter, ptStart));
            ltLineInfo.add(new LineInfo(ptCenter, ptEnd));

            // If straight lines then add line from start to end.
            if (straight) {
                ltLineInfo.add(new LineInfo(ptStart, ptEnd));
                continue;
            }

            // Curved, now calculate the bezier curve between start and end. See, http://en.wikipedia.org/wiki/Bezier_curves in the terminology section about the (4/3)tan(t/4) details.

            // Calculate the control point from ptStart.
            double controlRadian = startRadian + Math.PI / 2;
            FPointType ptControlStart = new FPointType(ptStart.x + Math.cos(controlRadian) * lengthControlPoint, ptStart.y + Math.sin(controlRadian) * lengthControlPoint);

            // Calculate the control point from ptEnd.
            controlRadian = endRadian - Math.PI / 2;
            FPointType ptControlEnd = new FPointType(ptEnd.x + Math.cos(controlRadian) * lengthControlPoint, ptEnd.y + Math.sin(controlRadian) * lengthControlPoint);

            ltLineInfo.add(new BezierInfo(ptStart, ptEnd, ptControlStart, ptControlEnd));
        }

        // Now Place the information into the Design at the center position.
        TransformGraph tGraph = new TransformGraph();
        tGraph.addAll(ltLineInfo);
        return tGraph;
    }
    // </editor-fold>
}
