package org.cytoscape.psfc.gui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * PUBCLI CLASS PSFCPanel
 *
 * Sets the components of the app panel, located in the WEST CytoPanel group.
 */
public class PSFCPanel extends JPanel implements CytoPanelComponent {
    private String title = "PSFC";
    private String iconName = "psfc_icon.png";

    public PSFCPanel() {
        this.setPreferredSize(new Dimension(400, getHeight()));
        initComponents();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Icon getIcon() {
//        ClassLoader classLoader = PSFCActivator.class.getClassLoader();
//        Icon icon = new ImageIcon(classLoader.getResource(iconName));
        return null;
    }

    private javax.swing.JButton jb_chooseEdgeTypeConfigFile;
    private javax.swing.JButton jb_chooseRuleConfigFile;
    private javax.swing.JButton jb_ruleWizard;
    private javax.swing.JButton jb_showEdgeTypes;
    private javax.swing.JButton jb_sortNetwork;
    private javax.swing.JComboBox jcb_edgeTypeAttribute;
    private javax.swing.JComboBox jcb_network;
    private javax.swing.JComboBox jcb_nodeDataAttribute;
    private javax.swing.JComboBox jcb_sortingAlgorithm;
    private javax.swing.JLabel jl_chooseNetwork;
    private javax.swing.JLabel jl_dataMappingRules;
    private javax.swing.JLabel jl_dataType;
    private javax.swing.JLabel jl_defaultValue;
    private javax.swing.JLabel jl_edgeTypeConfigFile;
    private javax.swing.JLabel jl_flowRules;
    private javax.swing.JLabel jl_multipleDataRule;
    private javax.swing.JLabel jl_or;
    private javax.swing.JLabel jl_ruleConfigFile;
    private javax.swing.JLabel jl_selectEdgeTypeAttribute;
    private javax.swing.JLabel jl_selectNodeDataAttribute;
    private javax.swing.JLabel jl_sortingAlgorithm;
    private javax.swing.JPanel jp_Data;
    private javax.swing.JPanel jp_Network;
    private javax.swing.JPanel jp_Rules;
    private javax.swing.JRadioButton jrb_FC;
    private javax.swing.JRadioButton jrb_linear;
    private javax.swing.JRadioButton jrb_log;
    private javax.swing.JRadioButton jrb_logFC;
    private javax.swing.JRadioButton jrb_max;
    private javax.swing.JRadioButton jrb_mean;
    private javax.swing.JRadioButton jrb_min;
    private javax.swing.JTabbedPane jtp_psfc;
    private javax.swing.JTextField jtxt_defaultValue;

    private void initComponents() {

        jtp_psfc = new javax.swing.JTabbedPane();
        jp_Network = new javax.swing.JPanel();
        jcb_sortingAlgorithm = new javax.swing.JComboBox();
        jb_sortNetwork = new javax.swing.JButton();
        jl_chooseNetwork = new javax.swing.JLabel();
        jcb_network = new javax.swing.JComboBox();
        jl_sortingAlgorithm = new javax.swing.JLabel();
        jl_selectEdgeTypeAttribute = new javax.swing.JLabel();
        jcb_edgeTypeAttribute = new javax.swing.JComboBox();
        jb_showEdgeTypes = new javax.swing.JButton();
        jl_selectNodeDataAttribute = new javax.swing.JLabel();
        jcb_nodeDataAttribute = new javax.swing.JComboBox();
        jp_Data = new javax.swing.JPanel();
        jl_dataMappingRules = new javax.swing.JLabel();
        jl_dataType = new javax.swing.JLabel();
        jrb_linear = new javax.swing.JRadioButton();
        jrb_log = new javax.swing.JRadioButton();
        jrb_logFC = new javax.swing.JRadioButton();
        jrb_FC = new javax.swing.JRadioButton();
        jl_defaultValue = new javax.swing.JLabel();
        jtxt_defaultValue = new javax.swing.JTextField();
        jl_multipleDataRule = new javax.swing.JLabel();
        jrb_min = new javax.swing.JRadioButton();
        jrb_max = new javax.swing.JRadioButton();
        jrb_mean = new javax.swing.JRadioButton();
        jp_Rules = new javax.swing.JPanel();
        jl_flowRules = new javax.swing.JLabel();
        jl_edgeTypeConfigFile = new javax.swing.JLabel();
        jb_chooseEdgeTypeConfigFile = new javax.swing.JButton();
        jl_ruleConfigFile = new javax.swing.JLabel();
        jb_chooseRuleConfigFile = new javax.swing.JButton();
        jb_ruleWizard = new javax.swing.JButton();
        jl_or = new javax.swing.JLabel();



        jp_Network.setPreferredSize(new java.awt.Dimension(400, 500));

        jcb_sortingAlgorithm.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Hierarchical", "BFS", "DFS", "ShortestPath"}));

        jb_sortNetwork.setText("Sort");
        jb_sortNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_sortNetworkActionPerformed(evt);
            }
        });

        jl_chooseNetwork.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_chooseNetwork.setText("Choose Network");

        jcb_network.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Current Network", "Network1", "Network2", "Network3"}));

        jl_sortingAlgorithm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_sortingAlgorithm.setText("Sorting algorithm");

        jl_selectEdgeTypeAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectEdgeTypeAttribute.setText("Select Edge type attribute");

        jcb_edgeTypeAttribute.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        jb_showEdgeTypes.setText("Show");
        jb_showEdgeTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_showEdgeTypesActionPerformed(evt);
            }
        });

        jl_selectNodeDataAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectNodeDataAttribute.setText("Select Node data attribute");

        jcb_nodeDataAttribute.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        javax.swing.GroupLayout jp_NetworkLayout = new javax.swing.GroupLayout(jp_Network);
        jp_Network.setLayout(jp_NetworkLayout);
        jp_NetworkLayout.setHorizontalGroup(
                jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_sortingAlgorithm)
                                                        .addComponent(jl_chooseNetwork)
                                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                                .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jb_showEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap())
                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                                .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        jp_NetworkLayout.setVerticalGroup(
                jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jl_chooseNetwork)
                                .addGap(1, 1, 1)
                                .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_sortingAlgorithm)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_showEdgeTypes))
                                .addGap(36, 36, 36)
                                .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(242, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Network", jp_Network);

        jl_dataMappingRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_dataMappingRules.setText("Data mapping rules");

        jl_dataType.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_dataType.setText("Data type");

        jrb_linear.setText("Linear");

        jrb_log.setText("Log");
        jrb_log.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_logActionPerformed(evt);
            }
        });

        jrb_logFC.setText("LogFC");
        jrb_logFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_logFCActionPerformed(evt);
            }
        });

        jrb_FC.setText("FC");
        jrb_FC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_FCActionPerformed(evt);
            }
        });

        jl_defaultValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_defaultValue.setText("Default value");

        jtxt_defaultValue.setText("0");
        jtxt_defaultValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxt_defaultValueActionPerformed(evt);
            }
        });

        jl_multipleDataRule.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_multipleDataRule.setText("Multiple data rule");

        jrb_min.setText("Min");
        jrb_min.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_minActionPerformed(evt);
            }
        });

        jrb_max.setText("Max");
        jrb_max.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_maxActionPerformed(evt);
            }
        });

        jrb_mean.setText("Mean");
        jrb_mean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_meanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jp_DataLayout = new javax.swing.GroupLayout(jp_Data);
        jp_Data.setLayout(jp_DataLayout);
        jp_DataLayout.setHorizontalGroup(
                jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_DataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jrb_mean)
                                        .addComponent(jrb_max)
                                        .addComponent(jl_dataMappingRules)
                                        .addComponent(jl_dataType, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jp_DataLayout.createSequentialGroup()
                                                .addComponent(jl_defaultValue)
                                                .addGap(31, 31, 31)
                                                .addComponent(jtxt_defaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jl_multipleDataRule)
                                        .addComponent(jrb_min)
                                        .addGroup(jp_DataLayout.createSequentialGroup()
                                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jrb_linear, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jrb_log))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jrb_logFC, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jrb_FC))))
                                .addContainerGap(129, Short.MAX_VALUE))
        );
        jp_DataLayout.setVerticalGroup(
                jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_DataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jl_dataMappingRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_dataType, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_linear)
                                        .addComponent(jrb_FC))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_log)
                                        .addComponent(jrb_logFC))
                                .addGap(18, 18, 18)
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_defaultValue)
                                        .addComponent(jtxt_defaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addComponent(jl_multipleDataRule)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrb_min)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrb_max)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrb_mean)
                                .addContainerGap(230, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Data", jp_Data);

        jl_flowRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_flowRules.setText("Flow rules");

        jl_edgeTypeConfigFile.setText("EdgeTypes config file");

        jb_chooseEdgeTypeConfigFile.setText("Choose file");
        jb_chooseEdgeTypeConfigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_chooseEdgeTypeConfigFileActionPerformed(evt);
            }
        });

        jl_ruleConfigFile.setText("Rule config file");

        jb_chooseRuleConfigFile.setText("Choose file");
        jb_chooseRuleConfigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_chooseRuleConfigFileActionPerformed(evt);
            }
        });

        jb_ruleWizard.setText("Set up rules with wizard");
        jb_ruleWizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_ruleWizardActionPerformed(evt);
            }
        });

        jl_or.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_or.setText("OR");

        javax.swing.GroupLayout jp_RulesLayout = new javax.swing.GroupLayout(jp_Rules);
        jp_Rules.setLayout(jp_RulesLayout);
        jp_RulesLayout.setHorizontalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_flowRules)
                                        .addComponent(jl_or)
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addGap(26, 26, 26)
                                                .addComponent(jb_ruleWizard, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_ruleConfigFile)
                                                        .addComponent(jl_edgeTypeConfigFile))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                                        .addComponent(jb_chooseRuleConfigFile))))
                                .addContainerGap(58, Short.MAX_VALUE))
        );
        jp_RulesLayout.setVerticalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jl_flowRules)
                                .addGap(18, 18, 18)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_edgeTypeConfigFile)
                                        .addComponent(jb_chooseEdgeTypeConfigFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addComponent(jl_ruleConfigFile)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jl_or))
                                        .addComponent(jb_chooseRuleConfigFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_ruleWizard)
                                .addContainerGap(335, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Rules", jp_Rules);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(48, Short.MAX_VALUE))
        );
    }

    private void jb_ruleWizardActionPerformed(ActionEvent evt) {

    }

    private void jb_chooseRuleConfigFileActionPerformed(ActionEvent evt) {

    }

    private void jb_chooseEdgeTypeConfigFileActionPerformed(ActionEvent evt) {

    }

    private void jrb_meanActionPerformed(ActionEvent evt) {

    }

    private void jrb_maxActionPerformed(ActionEvent evt) {

    }

    private void jrb_minActionPerformed(ActionEvent evt) {

    }

    private void jtxt_defaultValueActionPerformed(ActionEvent evt) {

    }

    private void jrb_FCActionPerformed(ActionEvent evt) {

    }

    private void jrb_logFCActionPerformed(ActionEvent evt) {

    }

    private void jrb_logActionPerformed(ActionEvent evt) {

    }

    private void jb_showEdgeTypesActionPerformed(ActionEvent evt) {

    }

    private void jb_sortNetworkActionPerformed(ActionEvent evt) {

    }
}
