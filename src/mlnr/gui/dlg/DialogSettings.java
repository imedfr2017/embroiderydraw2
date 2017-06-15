/*
 * DialogSettings.java
 *
 * Created on January 27, 2007, 3:22 PM
 */

package mlnr.gui.dlg;

import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import mlnr.Measurement;
import mlnr.draw.DrawingLayer;

/**
 *
 * @author  Robert Molnar II
 */
public class DialogSettings extends javax.swing.JDialog {
    ButtonGroup buttonGroup = new ButtonGroup();
    Color newLayerColor = DrawingLayer.getMasterColor();
    boolean currentMetricEnglish = false;
    int originalPenSize = Measurement.getDesignPenSize();
    boolean changedPenSize = false;
    
    /** Creates new form DialogSettings */
    public DialogSettings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        buttonGroup.add(jRadioButtonEnglish);
        buttonGroup.add(jRadioButtonMetric);
        
        jFormattedTextFieldWidth.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextFieldHeight.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        
        setLocationRelativeTo(null);
        
        loadSettings();
    }
    
    /** This will load the settings for the dialog box.
     */
    private void loadSettings() { 
        newLayerColor = DrawingLayer.getMasterColor();
        
        // Load default width and height values.
        jFormattedTextFieldWidth.setValue(Measurement.convertMeasurement(DialogNewFile.getDefaultWidth(), 2));
        jFormattedTextFieldHeight.setValue(Measurement.convertMeasurement(DialogNewFile.getDefaultHeight(), 2));
        
        // Metric or English?
        if (Measurement.isMetric()) {
            currentMetricEnglish = true;
            jRadioButtonMetric.setSelected(true);
            jLabelMeasurement1.setText("mm");
            jLabelMeasurement2.setText("mm");
        } else {
            currentMetricEnglish = false;
            jRadioButtonEnglish.setSelected(true);
            jLabelMeasurement1.setText("in");
            jLabelMeasurement2.setText("in");
        }
        
        // Pen Size.
        DefaultComboBoxModel model;
        jComboBoxPenSize.removeAllItems();
        jComboBoxPenSize.addItem(1);
        jComboBoxPenSize.addItem(2);
        jComboBoxPenSize.addItem(3);
        jComboBoxPenSize.addItem(4);
        jComboBoxPenSize.addItem(5);
        jComboBoxPenSize.addItem(6);
        jComboBoxPenSize.addItem(7);
        jComboBoxPenSize.addItem(8);
        jComboBoxPenSize.addItem(9);
        jComboBoxPenSize.addItem(10);
        jComboBoxPenSize.addItem(11);
        jComboBoxPenSize.addItem(12);
        jComboBoxPenSize.addItem(13);
        jComboBoxPenSize.addItem(14);
        jComboBoxPenSize.addItem(15);
        
        jComboBoxPenSize.setSelectedIndex(Measurement.getDesignPenSize() - 1);
    }

    /** This will save the settings.
     */
    private void saveSettings() {
        Measurement.setMetricEnglish(jRadioButtonMetric.isSelected());
        Measurement.setDesignPenSize(jComboBoxPenSize.getSelectedIndex() + 1);
        Measurement.getSettings().save();
        
        DrawingLayer.setMasterColor(newLayerColor);
        DrawingLayer.getLayerSettings().save();
        
        float width = Measurement.convReal(Float.parseFloat(jFormattedTextFieldWidth.getValue().toString()));
        float height = Measurement.convReal(Float.parseFloat(jFormattedTextFieldHeight.getValue().toString()));
        if (width < 1.0f)
            width = 1.0f;
        if (height < 1.0f)
            height = 1.0f;
        DialogNewFile.setDefaultHeight(height);
        DialogNewFile.setDefaultWidth(width);        
        DialogNewFile.getSettings().save();
    }
    
    /** This is called when a measurement has changed.
     */
    private void onMeasurementChanged() {
        // The width and height the user has entered in.
        float width = Float.parseFloat(jFormattedTextFieldWidth.getValue().toString());
        float height = Float.parseFloat(jFormattedTextFieldHeight.getValue().toString());

        if (jRadioButtonMetric.isSelected()) {
            if (currentMetricEnglish)
                return;
            currentMetricEnglish = true;
            
            // Changed from English to Metric.
            jFormattedTextFieldWidth.setValue(Measurement.convertMeasurementToMM(Measurement.convertInchesToMeasurement(width), 3));
            jFormattedTextFieldHeight.setValue(Measurement.convertMeasurementToMM(Measurement.convertInchesToMeasurement(height), 3));
            
            jLabelMeasurement1.setText("mm");
            jLabelMeasurement2.setText("mm");
        } else {
            if (!currentMetricEnglish)
                return;
            currentMetricEnglish = false;
            
            // Change from Metric to English.
            jFormattedTextFieldWidth.setValue(Measurement.convertMeasurementToInch(Measurement.convertMMToMeasurement(width), 3));
            jFormattedTextFieldHeight.setValue(Measurement.convertMeasurementToInch(Measurement.convertMMToMeasurement(height), 3));
            
            jLabelMeasurement1.setText("in");
            jLabelMeasurement2.setText("in");
        }        
    }
    
    /** @return true if the pen changed size.
     */
    public boolean changePenSize() {
        return changedPenSize;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextFieldWidth = new javax.swing.JFormattedTextField();
        jFormattedTextFieldHeight = new javax.swing.JFormattedTextField();
        jLabelMeasurement1 = new javax.swing.JLabel();
        jLabelMeasurement2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxPenSize = new javax.swing.JComboBox();
        jButtonChooseNewLayerColor = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jRadioButtonMetric = new javax.swing.JRadioButton();
        jRadioButtonEnglish = new javax.swing.JRadioButton();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonRestoreFactorySettings = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Default Drawing Pad Size"));

        jLabel1.setText("Width:");

        jLabel2.setText("Height:");

        jFormattedTextFieldWidth.setText("jFormattedTextField1");

        jFormattedTextFieldHeight.setText("jFormattedTextField2");

        jLabelMeasurement1.setText("jLabel3");

        jLabelMeasurement2.setText("jLabel4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jFormattedTextFieldHeight, 0, 0, Short.MAX_VALUE)
                    .addComponent(jFormattedTextFieldWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelMeasurement2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jLabelMeasurement1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jFormattedTextFieldWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jFormattedTextFieldHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement2))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Pen Size / New Layer Color"));

        jLabel3.setText("Pen Size:");

        jComboBoxPenSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxPenSize.setToolTipText("Using a pen size of 1 will draw faster in Embroidery Draw.");

        jButtonChooseNewLayerColor.setText("Choose New Layer Color");
        jButtonChooseNewLayerColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseNewLayerColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPenSize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButtonChooseNewLayerColor, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxPenSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonChooseNewLayerColor)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Measurement System"));

        jRadioButtonMetric.setText("Metric (Millimeters)");
        jRadioButtonMetric.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonMetric.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonMetric.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMetricActionPerformed(evt);
            }
        });

        jRadioButtonEnglish.setText("English (Inches)");
        jRadioButtonEnglish.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonEnglish.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonEnglishActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRadioButtonMetric)
            .addComponent(jRadioButtonEnglish)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jRadioButtonMetric)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonEnglish)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonRestoreFactorySettings.setText("Restore Factory Settings");
        jButtonRestoreFactorySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRestoreFactorySettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonRestoreFactorySettings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSave)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonRestoreFactorySettings))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonEnglishActionPerformed
// TODO add your handling code here:
        onMeasurementChanged();
    }//GEN-LAST:event_jRadioButtonEnglishActionPerformed

    private void jRadioButtonMetricActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMetricActionPerformed
// TODO add your handling code here:
        onMeasurementChanged();        
    }//GEN-LAST:event_jRadioButtonMetricActionPerformed

    private void jButtonChooseNewLayerColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseNewLayerColorActionPerformed
// TODO add your handling code here:
        Color c = JColorChooser.showDialog(this, "Choose New Layer Color", newLayerColor);
        if (c != null)
            newLayerColor = c;
    }//GEN-LAST:event_jButtonChooseNewLayerColorActionPerformed

    private void jButtonRestoreFactorySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRestoreFactorySettingsActionPerformed
// TODO add your handling code here:
        DrawingLayer.restoreFactorSettings();
        DialogNewFile.restoreFactorSettings();
        Measurement.restorePenFactorySettings();
        Measurement.restoreMetricEnglishFactorySettings();
        
        loadSettings();        
    }//GEN-LAST:event_jButtonRestoreFactorySettingsActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
// TODO add your handling code here:
        
        // Did the pen size change?
        if (originalPenSize != Measurement.getDesignPenSize())
            changedPenSize = true;
        
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
// TODO add your handling code here:
        saveSettings();
        
        // Did the pen size change?
        if (originalPenSize != Measurement.getDesignPenSize())
            changedPenSize = true;
        
        setVisible(false);
        
    }//GEN-LAST:event_jButtonSaveActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DialogSettings(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChooseNewLayerColor;
    private javax.swing.JButton jButtonRestoreFactorySettings;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox jComboBoxPenSize;
    private javax.swing.JFormattedTextField jFormattedTextFieldHeight;
    private javax.swing.JFormattedTextField jFormattedTextFieldWidth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelMeasurement1;
    private javax.swing.JLabel jLabelMeasurement2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButtonEnglish;
    private javax.swing.JRadioButton jRadioButtonMetric;
    // End of variables declaration//GEN-END:variables
    
}
