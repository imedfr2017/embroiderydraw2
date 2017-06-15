/*
 * TranslatePanel.java
 *
 * Created on August 18, 2005, 11:16 PM
 */

package mlnr.gui.tool.opt;

import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import mlnr.gui.tool.dlg.DialogMoveGetLocation;
import mlnr.gui.tool.ToolMove;
import mlnr.type.FPointType;

/**
 *
 * @author  Robert Molnar II
 */
public class TranslatePanel extends javax.swing.JPanel {
    ToolMove tMove;
    ButtonGroup buttonGroup = new ButtonGroup();
    JFrame frame;
    
    /** Creates new form TranslatePanel */
    public TranslatePanel(JFrame frame) {
        initComponents();
        
        this.frame = frame;
        
        buttonGroup.add(jRadioButtonAnyDirection);
        buttonGroup.add(jRadioButtonDegree);
        buttonGroup.add(jRadioButtonHorizontal);
        buttonGroup.add(jRadioButtonVertical);
        
        jFormattedTextField1.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextField1.setValue(0);
        jRadioButtonAnyDirection.setSelected(true);
        jButtonCenter.setToolTipText("This will center the object to the center of the design.");
        jButtonUseMovementMethod.setToolTipText("This will center the object by using the movement method that was selected above.");
    }
    
    /** @return true if movement is any direction.
     */
    public boolean isMovementAnyDirection() {
        return jRadioButtonAnyDirection.isSelected();
    }
    
    /** @return true if movement is to use degree.
     */
    public boolean isMovementDegree() {
        return jRadioButtonDegree.isSelected();
    }
    
    /** @return true if movement is horizontal.
     */
    public boolean isMovementHorizontal() {
        return jRadioButtonHorizontal.isSelected();
    }
    
    /** @return true if movement is vertical.
     */
    public boolean isMovementVertical() {
        return jRadioButtonVertical.isSelected();
    }
    
    /** This will set the degree.
     * @param degree is the degree to set it to.
     */
    public void setDegree(float degree) {
        jFormattedTextField1.setValue(degree);
    }
    
    /** @return degree.
     */
    public float getDegree() {
        return Float.parseFloat(jFormattedTextField1.getValue().toString());
    }
    
    /** This will set the move tool.
     * @param tMove is the move tool.
     */
    public void setMoveTool(ToolMove tMove) {
        this.tMove = tMove;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonAnyDirection = new javax.swing.JRadioButton();
        jRadioButtonHorizontal = new javax.swing.JRadioButton();
        jRadioButtonVertical = new javax.swing.JRadioButton();
        jRadioButtonDegree = new javax.swing.JRadioButton();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonCenter = new javax.swing.JButton();
        jButtonUseMovementMethod = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Translate Options"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Movement Method"));

        jRadioButtonAnyDirection.setText("Any Direction");

        jRadioButtonHorizontal.setText("Horizontal");

        jRadioButtonVertical.setText("Vertical");

        jRadioButtonDegree.setText("Degree:");

        jFormattedTextField1.setText("jFormattedTextField1");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jRadioButtonDegree)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jRadioButtonVertical)
                    .add(jRadioButtonHorizontal)
                    .add(jRadioButtonAnyDirection))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jRadioButtonAnyDirection)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButtonHorizontal)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButtonVertical)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonDegree)
                    .add(jFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Center Operations"));

        jButtonCenter.setText("To Center Of Design");
        jButtonCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCenterActionPerformed(evt);
            }
        });

        jButtonUseMovementMethod.setText("Use Movement Method");
        jButtonUseMovementMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUseMovementMethodActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonUseMovementMethod)
                    .add(jButtonCenter))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {jButtonCenter, jButtonUseMovementMethod}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jButtonCenter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonUseMovementMethod)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void integerTextFieldDegreeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_integerTextFieldDegreeKeyReleased
    // TODO add your handling code here:
    // NOT IN USE.
    }//GEN-LAST:event_integerTextFieldDegreeKeyReleased
    
    private void jButtonUseMovementMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUseMovementMethodActionPerformed
        FPointType fptCenterOfSelectedItems = tMove.getCenterOfSelectedItems();
        DialogMoveGetLocation dialog = new DialogMoveGetLocation(frame, true, fptCenterOfSelectedItems.x, fptCenterOfSelectedItems.y);
        dialog.setVisible(true);
        if (dialog.isOk())
            tMove.translateToLocation(dialog.getXPosition(), dialog.getYPosition());        
    }//GEN-LAST:event_jButtonUseMovementMethodActionPerformed
    
    private void jButtonCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCenterActionPerformed
// TODO add your handling code here:
        tMove.onMoveToCenterOfDesign();
    }//GEN-LAST:event_jButtonCenterActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCenter;
    private javax.swing.JButton jButtonUseMovementMethod;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButtonAnyDirection;
    private javax.swing.JRadioButton jRadioButtonDegree;
    private javax.swing.JRadioButton jRadioButtonHorizontal;
    private javax.swing.JRadioButton jRadioButtonVertical;
    // End of variables declaration//GEN-END:variables
    
}