/*
 * DialogFreeStandingLaceGenerator.java
 *
 * Created on February 5, 2007, 5:02 PM
 */

package mlnr.gui.dlg;

import java.text.DecimalFormat;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import mlnr.Measurement;

/**
 *
 * @author  Robert Molnar II
 */
public class DialogFreeStandingLaceGenerator extends javax.swing.JDialog {
    boolean ok = false;
    
    ButtonGroup bgStep1 = new ButtonGroup();
    ButtonGroup bgStep2 = new ButtonGroup();
    ButtonGroup bgStep4 = new ButtonGroup();
    
    /**
     * Creates new form DialogFreeStandingLaceGenerator
     */
    public DialogFreeStandingLaceGenerator(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        bgStep1.add(jRadioButtonBottom4Side);
        bgStep1.add(jRadioButtonBottom5Side);
        bgStep1.add(jRadioButtonBottom6Side);
        bgStep1.add(jRadioButtonBottom7Side);
        bgStep1.add(jRadioButtonBottom8Side);
        jRadioButtonBottom4Side.setSelected(true);
        
        bgStep2.add(jRadioButtonSidePentagon);
        bgStep2.add(jRadioButtonSideHexagon);
        bgStep2.add(jRadioButtonSideQuadagon);
        jRadioButtonSideQuadagon.setSelected(true);
        
        bgStep4.add(jRadioButtonPlacementCurrentLayer);
        bgStep4.add(jRadioButtonPlacementNewDrawing);
        bgStep4.add(jRadioButtonPlacementTwoNewLayers);
        jRadioButtonPlacementCurrentLayer.setSelected(true);
        
        jFormattedTextFieldTopHeight.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextFieldLengthBottomHeight.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextFieldLengthBottom.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextFieldLengthMiddle.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        jFormattedTextFieldLengthTop.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#.##"))));
        
        jLabelMeasurement1.setText(Measurement.getTextualName());
        jLabelMeasurement2.setText(Measurement.getTextualName());
        jLabelMeasurement3.setText(Measurement.getTextualName());
        jLabelMeasurement4.setText(Measurement.getTextualName());
        jLabelMeasurement5.setText(Measurement.getTextualName());
        
        // Make sure parameters are correctly enabled.
        enableParameters();
        
        setLocation(DialogUtil.centerDialog(parent.getBounds(), getBounds()));
    }
    
    /** Only the new drawing is selected.
     */
    public void enableNewDrawingOnly() {
        jRadioButtonPlacementCurrentLayer.setVisible(false);
        jRadioButtonPlacementTwoNewLayers.setVisible(false);
        jRadioButtonPlacementNewDrawing.setSelected(true);
    }
    
    /** This will show/hide certain parameters based on the side selection.
     */
    private void enableParameters() {
        // Middle is not visible for quadagon.
        jLabelMiddle.setEnabled(!jRadioButtonSideQuadagon.isSelected());
        jFormattedTextFieldLengthMiddle.setEnabled(!jRadioButtonSideQuadagon.isSelected());
        
        // Top is not visible for pentagons.
        jLabelTop.setEnabled(!jRadioButtonSidePentagon.isSelected());
        jFormattedTextFieldLengthTop.setEnabled(!jRadioButtonSidePentagon.isSelected());
        
        // Height is renamed for quadagon.
        if (jRadioButtonSideQuadagon.isSelected()) {
            jLabelTopHeight.setText("Height:");
            jLabelBottomHeight.setEnabled(false);
            jFormattedTextFieldLengthBottomHeight.setEditable(false);
        } else {            
            jLabelTopHeight.setText("Top Height:");
            jLabelBottomHeight.setEnabled(true);
            jFormattedTextFieldLengthBottomHeight.setEditable(true);
        }
    }
    
    /** @return true if the user clicked on generate FSL.
     */
    public boolean isOk() {
        return ok;
    }
    
    /** @return true if the parameters are acceptable to creating the Free Standing Lace items.
     */
    private boolean verifyParameters() {
        float heightTop, heightBottom, lengthTop, lengthBottom, lengthMiddle;
        
        // Default is zero.
        heightTop = heightBottom = lengthTop = lengthBottom = lengthMiddle = 0.0f;
        
        try { heightTop = Measurement.convReal(Float.parseFloat(jFormattedTextFieldTopHeight.getValue().toString())); } catch (Exception e) {}
        try { heightBottom = Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthBottomHeight.getValue().toString())); } catch (Exception e) {}
        try { lengthTop = Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthTop.getValue().toString())); } catch (Exception e) {}
        try { lengthBottom = Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthBottom.getValue().toString())); } catch (Exception e) {}
        try { lengthMiddle = Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthMiddle.getValue().toString())); } catch (Exception e) {}
        
        // Verify parameters.
        if (heightTop <= 0.0f) {
            JOptionPane.showMessageDialog(this, "Top height must be greater than 0.");
            return false;
        }
        
        if (!jRadioButtonSideQuadagon.isSelected() && heightBottom <= 0.0f) {
            JOptionPane.showMessageDialog(this, "Bottom height must be greater than 0.");
            return false;
        }
        
        if (!jRadioButtonSidePentagon.isSelected() && lengthTop <= 0.0f) {
            JOptionPane.showMessageDialog(this, "Length Top must be greater than 0.");
            return false;
        }
        
        if (lengthBottom <= 0.0f) {
            JOptionPane.showMessageDialog(this, "Length Bottom must be greater than 0.");
            return false;
        }
        
        if (!jRadioButtonSideQuadagon.isSelected() && lengthMiddle <= 0.0f) {
            JOptionPane.showMessageDialog(this, "Length Middle must be greater than 0.");
            return false;
        }
        
        return true;
    }
    
    /** @return the parameter top height. (If quadapolygon then this is the height).
     */
    public float getLengthTopHeight() {
        return Measurement.convReal(Float.parseFloat(jFormattedTextFieldTopHeight.getValue().toString()));
    }
    
    /** @return the parameter bottom height.
     */
    public float getLengthBottomHeight() {
        return Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthBottomHeight.getValue().toString()));
    }
    
    /** @return the parameter length top.
     */
    public float getLengthTop() {
        return Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthTop.getValue().toString()));
    }
    
    /** @return the parameter length bottom.
     */
    public float getLengthBottom() {
        return Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthBottom.getValue().toString()));
    }
    
    /** @return the parameter length middle.
     */
    public float getLengthMiddle() {
        return Measurement.convReal(Float.parseFloat(jFormattedTextFieldLengthMiddle.getValue().toString()));
    }
    
    /** @return the number of sides for the bottom part. (4, 5, 6, 7, 8)
     */
    public int getBottomSideCount() {
        if (jRadioButtonBottom4Side.isSelected())
            return 4;
        if (jRadioButtonBottom5Side.isSelected())
            return 5;
        if (jRadioButtonBottom6Side.isSelected())
            return 6;
        if (jRadioButtonBottom7Side.isSelected())
            return 7;
        if (jRadioButtonBottom8Side.isSelected())
            return 8;
        
        throw new IllegalStateException("getBottomSideCount() unknown radio button selected. ");
    }
    
    /** @return the number of sides for the side part. (4, 5, 6)
     */
    public int getSideSideCount() {
        if (jRadioButtonSideQuadagon.isSelected())
            return 4;
        if (jRadioButtonSidePentagon.isSelected())
            return 5;
        if (jRadioButtonSideHexagon.isSelected())
            return 6;
        
        throw new IllegalStateException("getSideSideCount() unknown radio button selected. ");
    }
    
    /** @return true if placement should be two new drawings.
     */
    public boolean isPlacementNewDrawings() {
        return jRadioButtonPlacementNewDrawing.isSelected();
    }
    
    /** @return true if placement should be in current layer.
     */
    public boolean isPlacementCurrentLayer() {
        return jRadioButtonPlacementCurrentLayer.isSelected();
    }
    
    /** @return true if placement should be in new layers.
     */
    public boolean isPlacementNewLayers() {
        return jRadioButtonPlacementTwoNewLayers.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jRadioButtonBottom5Side = new javax.swing.JRadioButton();
        jRadioButtonBottom6Side = new javax.swing.JRadioButton();
        jRadioButtonBottom7Side = new javax.swing.JRadioButton();
        jRadioButtonBottom8Side = new javax.swing.JRadioButton();
        jRadioButtonBottom4Side = new javax.swing.JRadioButton();
        jLabel4Side = new javax.swing.JLabel();
        jLabel5Side = new javax.swing.JLabel();
        jLabel6Side = new javax.swing.JLabel();
        jLabel7Side = new javax.swing.JLabel();
        jLabel8Side = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonSideQuadagon = new javax.swing.JRadioButton();
        jRadioButtonSidePentagon = new javax.swing.JRadioButton();
        jRadioButtonSideHexagon = new javax.swing.JRadioButton();
        jLabelPentaSide = new javax.swing.JLabel();
        jLabelHexSide = new javax.swing.JLabel();
        jLabelQuadSide = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jRadioButtonPlacementNewDrawing = new javax.swing.JRadioButton();
        jRadioButtonPlacementCurrentLayer = new javax.swing.JRadioButton();
        jRadioButtonPlacementTwoNewLayers = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jLabelBottom = new javax.swing.JLabel();
        jFormattedTextFieldLengthBottom = new javax.swing.JFormattedTextField();
        jLabelTop = new javax.swing.JLabel();
        jFormattedTextFieldLengthTop = new javax.swing.JFormattedTextField();
        jLabelTopHeight = new javax.swing.JLabel();
        jFormattedTextFieldTopHeight = new javax.swing.JFormattedTextField();
        jLabelMiddle = new javax.swing.JLabel();
        jFormattedTextFieldLengthMiddle = new javax.swing.JFormattedTextField();
        jLabelMeasurement1 = new javax.swing.JLabel();
        jLabelMeasurement2 = new javax.swing.JLabel();
        jLabelMeasurement3 = new javax.swing.JLabel();
        jLabelMeasurement4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelBottomHeight = new javax.swing.JLabel();
        jFormattedTextFieldLengthBottomHeight = new javax.swing.JFormattedTextField();
        jLabelMeasurement5 = new javax.swing.JLabel();
        jButtonGenerateFSLPattern = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Free Standing Lace Generator");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Step 2 - Bottom"));

        jRadioButtonBottom5Side.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonBottom5Side.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonBottom6Side.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonBottom6Side.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonBottom7Side.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonBottom7Side.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonBottom8Side.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonBottom8Side.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonBottom4Side.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonBottom4Side.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel4Side.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/poly4.gif"))); // NOI18N
        jLabel4Side.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4SideMouseClicked(evt);
            }
        });

        jLabel5Side.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/poly5.gif"))); // NOI18N
        jLabel5Side.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5SideMouseClicked(evt);
            }
        });

        jLabel6Side.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/poly6.gif"))); // NOI18N
        jLabel6Side.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6SideMouseClicked(evt);
            }
        });

        jLabel7Side.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/poly7.gif"))); // NOI18N
        jLabel7Side.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7SideMouseClicked(evt);
            }
        });

        jLabel8Side.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/poly8.gif"))); // NOI18N
        jLabel8Side.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8SideMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom4Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4Side))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom5Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5Side))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom6Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6Side))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom7Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7Side))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom8Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8Side)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4Side)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5Side))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBottom4Side)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonBottom5Side)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6Side)
                    .addComponent(jRadioButtonBottom6Side))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonBottom7Side)
                    .addComponent(jLabel7Side))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonBottom8Side)
                    .addComponent(jLabel8Side)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4Side, jRadioButtonBottom4Side});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5Side, jRadioButtonBottom5Side});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6Side, jRadioButtonBottom6Side});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7Side, jRadioButtonBottom7Side});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8Side, jRadioButtonBottom8Side});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Step 1 - Side"));

        jRadioButtonSideQuadagon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonSideQuadagon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSideQuadagon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSideQuadagonActionPerformed(evt);
            }
        });

        jRadioButtonSidePentagon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonSidePentagon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSidePentagon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSidePentagonActionPerformed(evt);
            }
        });

        jRadioButtonSideHexagon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonSideHexagon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSideHexagon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSideHexagonActionPerformed(evt);
            }
        });

        jLabelPentaSide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/side5.gif"))); // NOI18N
        jLabelPentaSide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelPentaSideMouseClicked(evt);
            }
        });

        jLabelHexSide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/side6.gif"))); // NOI18N
        jLabelHexSide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelHexSideMouseClicked(evt);
            }
        });

        jLabelQuadSide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mlnr/img/dlg/fsl/side4.gif"))); // NOI18N
        jLabelQuadSide.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelQuadSide.setMaximumSize(new java.awt.Dimension(140, 100));
        jLabelQuadSide.setMinimumSize(new java.awt.Dimension(140, 100));
        jLabelQuadSide.setPreferredSize(new java.awt.Dimension(140, 100));
        jLabelQuadSide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelQuadSideMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButtonSideQuadagon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelQuadSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButtonSidePentagon, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPentaSide))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButtonSideHexagon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHexSide)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButtonSideQuadagon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelQuadSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButtonSidePentagon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelPentaSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButtonSideHexagon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelHexSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(114, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Step 4 - Placement"));

        jRadioButtonPlacementNewDrawing.setText("New Drawing For Bottom and Side");
        jRadioButtonPlacementNewDrawing.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPlacementNewDrawing.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonPlacementCurrentLayer.setText("Current Layer");
        jRadioButtonPlacementCurrentLayer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPlacementCurrentLayer.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jRadioButtonPlacementTwoNewLayers.setText("Two New Layers");
        jRadioButtonPlacementTwoNewLayers.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPlacementTwoNewLayers.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonPlacementCurrentLayer)
                    .addComponent(jRadioButtonPlacementTwoNewLayers)
                    .addComponent(jRadioButtonPlacementNewDrawing))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jRadioButtonPlacementCurrentLayer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPlacementTwoNewLayers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPlacementNewDrawing)
                .addContainerGap(180, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Step 3 - Parameters"));

        jLabelBottom.setText("Length Bottom:");

        jLabelTop.setText("Length Top:");

        jLabelTopHeight.setText("Top Height:");

        jLabelMiddle.setText("Length Middle:");

        jLabelMeasurement1.setText("Inches");

        jLabelMeasurement2.setText("jLabel4");

        jLabelMeasurement3.setText("jLabel5");

        jLabelMeasurement4.setText("jLabel6");

        jLabel5.setText("Step 2 - uses the value from length bottom");

        jLabel6.setText("parameter for the sides. All sides are equal.");

        jLabelBottomHeight.setText("Bottom Height:");

        jLabelMeasurement5.setText("jLabel4");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMiddle)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabelBottomHeight)
                                .addGap(18, 18, 18)
                                .addComponent(jFormattedTextFieldLengthBottomHeight, 0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelTop)
                                    .addComponent(jLabelBottom)
                                    .addComponent(jLabelTopHeight))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jFormattedTextFieldTopHeight)
                                    .addComponent(jFormattedTextFieldLengthTop, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jFormattedTextFieldLengthMiddle)
                                    .addComponent(jFormattedTextFieldLengthBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMeasurement5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelMeasurement4)
                            .addComponent(jLabelMeasurement3)
                            .addComponent(jLabelMeasurement2)
                            .addComponent(jLabelMeasurement1)))
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBottom)
                    .addComponent(jFormattedTextFieldLengthBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMiddle)
                    .addComponent(jFormattedTextFieldLengthMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTop)
                    .addComponent(jFormattedTextFieldLengthTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTopHeight)
                    .addComponent(jFormattedTextFieldTopHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMeasurement1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelBottomHeight)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextFieldLengthBottomHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelMeasurement5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonGenerateFSLPattern.setText("Generate Patterns");
        jButtonGenerateFSLPattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateFSLPatternActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonGenerateFSLPattern))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGenerateFSLPattern)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelHexSideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHexSideMouseClicked
// TODO add your handling code here:
        jRadioButtonSideHexagon.setSelected(true);
        enableParameters();
        
    }//GEN-LAST:event_jLabelHexSideMouseClicked
        
    private void jLabelPentaSideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPentaSideMouseClicked
// TODO add your handling code here:
        jRadioButtonSidePentagon.setSelected(true);
        enableParameters();
        
    }//GEN-LAST:event_jLabelPentaSideMouseClicked
    
    private void jLabelQuadSideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelQuadSideMouseClicked
// TODO add your handling code here:
        jRadioButtonSideQuadagon.setSelected(true);
        enableParameters();
        
    }//GEN-LAST:event_jLabelQuadSideMouseClicked
    
    private void jLabel8SideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8SideMouseClicked
// TODO add your handling code here:
        jRadioButtonBottom8Side.setSelected(true);
    }//GEN-LAST:event_jLabel8SideMouseClicked
    
    private void jLabel7SideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7SideMouseClicked
// TODO add your handling code here:
        jRadioButtonBottom7Side.setSelected(true);
        
    }//GEN-LAST:event_jLabel7SideMouseClicked
    
    private void jLabel6SideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6SideMouseClicked
// TODO add your handling code here:
        jRadioButtonBottom6Side.setSelected(true);
        
    }//GEN-LAST:event_jLabel6SideMouseClicked
    
    private void jLabel5SideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5SideMouseClicked
// TODO add your handling code here:
        jRadioButtonBottom5Side.setSelected(true);
        
    }//GEN-LAST:event_jLabel5SideMouseClicked
    
    private void jLabel4SideMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4SideMouseClicked
// TODO add your handling code here:
        jRadioButtonBottom4Side.setSelected(true);
        
    }//GEN-LAST:event_jLabel4SideMouseClicked
    
    private void jRadioButtonSideHexagonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSideHexagonActionPerformed
// TODO add your handling code here:
        enableParameters();
    }//GEN-LAST:event_jRadioButtonSideHexagonActionPerformed
    
    private void jRadioButtonSidePentagonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSidePentagonActionPerformed
// TODO add your handling code here:
        enableParameters();
    }//GEN-LAST:event_jRadioButtonSidePentagonActionPerformed
    
    private void jRadioButtonSideQuadagonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSideQuadagonActionPerformed
// TODO add your handling code here:
        enableParameters();
    }//GEN-LAST:event_jRadioButtonSideQuadagonActionPerformed
    
    private void jButtonGenerateFSLPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateFSLPatternActionPerformed
// TODO add your handling code here:
        if (verifyParameters() == false)
            return;
        ok = true;
        setVisible(false);
    }//GEN-LAST:event_jButtonGenerateFSLPatternActionPerformed
    
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
// TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DialogFreeStandingLaceGenerator(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonGenerateFSLPattern;
    private javax.swing.JFormattedTextField jFormattedTextFieldLengthBottom;
    private javax.swing.JFormattedTextField jFormattedTextFieldLengthBottomHeight;
    private javax.swing.JFormattedTextField jFormattedTextFieldLengthMiddle;
    private javax.swing.JFormattedTextField jFormattedTextFieldLengthTop;
    private javax.swing.JFormattedTextField jFormattedTextFieldTopHeight;
    private javax.swing.JLabel jLabel4Side;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel5Side;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel6Side;
    private javax.swing.JLabel jLabel7Side;
    private javax.swing.JLabel jLabel8Side;
    private javax.swing.JLabel jLabelBottom;
    private javax.swing.JLabel jLabelBottomHeight;
    private javax.swing.JLabel jLabelHexSide;
    private javax.swing.JLabel jLabelMeasurement1;
    private javax.swing.JLabel jLabelMeasurement2;
    private javax.swing.JLabel jLabelMeasurement3;
    private javax.swing.JLabel jLabelMeasurement4;
    private javax.swing.JLabel jLabelMeasurement5;
    private javax.swing.JLabel jLabelMiddle;
    private javax.swing.JLabel jLabelPentaSide;
    private javax.swing.JLabel jLabelQuadSide;
    private javax.swing.JLabel jLabelTop;
    private javax.swing.JLabel jLabelTopHeight;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButtonBottom4Side;
    private javax.swing.JRadioButton jRadioButtonBottom5Side;
    private javax.swing.JRadioButton jRadioButtonBottom6Side;
    private javax.swing.JRadioButton jRadioButtonBottom7Side;
    private javax.swing.JRadioButton jRadioButtonBottom8Side;
    private javax.swing.JRadioButton jRadioButtonPlacementCurrentLayer;
    private javax.swing.JRadioButton jRadioButtonPlacementNewDrawing;
    private javax.swing.JRadioButton jRadioButtonPlacementTwoNewLayers;
    private javax.swing.JRadioButton jRadioButtonSideHexagon;
    private javax.swing.JRadioButton jRadioButtonSidePentagon;
    private javax.swing.JRadioButton jRadioButtonSideQuadagon;
    // End of variables declaration//GEN-END:variables
    
}
