package org.cytoscape.psfc.gui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.SortNetworkAction;
import org.cytoscape.psfc.logic.algorithms.PSFAlgorithms;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.psfc.net.NetworkGraphMapper;
import org.cytoscape.psfc.properties.EpsfcProps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * PUBCLI CLASS PSFCPanel
 * <p/>
 * Sets the components of the app panel, located in the WEST CytoPanel group.
 */
public class PSFCPanel extends JPanel implements CytoPanelComponent {
    private String title = "PSFC";
    private String iconName = "psfc_icon.png";
    private File edgeTypeRuleNameConfigFile;
    private File ruleConfigFile;

    public PSFCPanel() {
        this.setPreferredSize(new Dimension(400, getHeight()));
        loadProps();
        initComponents();
        setComponentProperties();
        setModels();
        addActionListeners();
    }

    public void loadProps() {
        for (EpsfcProps property : EpsfcProps.values()) {
            property.setOldValue(Boolean.parseBoolean((String) PSFCActivator.getPsfcProps().get(property.getName())));
            property.setNewValue(Boolean.parseBoolean((String) PSFCActivator.getPsfcProps().get(property.getName())));
        }
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

    private javax.swing.JButton jb_calculateFlow;
    private javax.swing.JButton jb_chooseEdgeTypeConfigFile;
    private javax.swing.JButton jb_chooseRuleConfigFile;
    private javax.swing.JButton jb_refreshEdgeTypeAttrs;
    private javax.swing.JButton jb_refreshNetworks;
    private javax.swing.JButton jb_refreshNodeDataAttrs;
    private javax.swing.JButton jb_ruleWizard;
    private javax.swing.JButton jb_showEdgeTypes;
    private javax.swing.JButton jb_showNodeData;
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
    private javax.swing.JLabel jl_edgeTypeConfigFileName;
    private javax.swing.JLabel jl_flowRules;
    private javax.swing.JLabel jl_multipleDataRule;
    private javax.swing.JLabel jl_or;
    private javax.swing.JLabel jl_ruleConfigFile;
    private javax.swing.JLabel jl_ruleConfigFileName;
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

    private ButtonGroup jbg_dataType;
    private ButtonGroup jbg_multipleDataOption;

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
        jb_refreshNetworks = new javax.swing.JButton();
        jb_refreshEdgeTypeAttrs = new javax.swing.JButton();
        jb_refreshNodeDataAttrs = new javax.swing.JButton();
        jb_showNodeData = new javax.swing.JButton();
        jb_calculateFlow = new javax.swing.JButton();
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
        jl_edgeTypeConfigFileName = new javax.swing.JLabel();
        jl_ruleConfigFileName = new javax.swing.JLabel();

        jp_Network.setPreferredSize(new java.awt.Dimension(400, 500));

        jcb_sortingAlgorithm.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hierarchical", "BFS", "DFS", "ShortestPath" }));

        jb_sortNetwork.setText("Sort");
        jb_sortNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_sortNetworkActionPerformed(evt);
            }
        });

        jl_chooseNetwork.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_chooseNetwork.setText("Choose Network");

        jcb_network.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Current Network", "Network1", "Network2", "Network3" }));

        jl_sortingAlgorithm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_sortingAlgorithm.setText("Sorting algorithm");

        jl_selectEdgeTypeAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectEdgeTypeAttribute.setText("Select Edge type attribute");

        jcb_edgeTypeAttribute.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jb_showEdgeTypes.setText("Show");
        jb_showEdgeTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_showEdgeTypesActionPerformed(evt);
            }
        });

        jl_selectNodeDataAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectNodeDataAttribute.setText("Select Node data attribute");

        jcb_nodeDataAttribute.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jb_refreshNetworks.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\Desktop\\refresh-green.png")); // NOI18N
        jb_refreshNetworks.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshNetworks.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshNetworks.setPreferredSize(new java.awt.Dimension(20, 20));
        jb_refreshNetworks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_refreshNetworksActionPerformed(evt);
            }
        });

        jb_refreshEdgeTypeAttrs.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\Desktop\\refresh-green.png")); // NOI18N
        jb_refreshEdgeTypeAttrs.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeTypeAttrs.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeTypeAttrs.setPreferredSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeTypeAttrs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_refreshEdgeTypeAttrsActionPerformed(evt);
            }
        });

        jb_refreshNodeDataAttrs.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\Desktop\\refresh-green.png")); // NOI18N
        jb_refreshNodeDataAttrs.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshNodeDataAttrs.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshNodeDataAttrs.setPreferredSize(new java.awt.Dimension(20, 20));
        jb_refreshNodeDataAttrs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_refreshNodeDataAttrsActionPerformed(evt);
            }
        });

        jb_showNodeData.setText("Show");
        jb_showNodeData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_showNodeDataActionPerformed(evt);
            }
        });

        jb_calculateFlow.setText("Calculate flow");

        javax.swing.GroupLayout jp_NetworkLayout = new javax.swing.GroupLayout(jp_Network);
        jp_Network.setLayout(jp_NetworkLayout);
        jp_NetworkLayout.setHorizontalGroup(
                jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jb_calculateFlow)
                                        .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jl_chooseNetwork)
                                                .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                        .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_showEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(jl_sortingAlgorithm)
                                                .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                        .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_showNodeData, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(52, Short.MAX_VALUE))
        );
        jp_NetworkLayout.setVerticalGroup(
                jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jl_chooseNetwork)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addComponent(jl_sortingAlgorithm)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(jb_showEdgeTypes))
                                .addGap(41, 41, 41)
                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jp_NetworkLayout.createSequentialGroup()
                                                .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jp_NetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(jb_showNodeData))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                                .addComponent(jb_calculateFlow)
                                .addGap(36, 36, 36))
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
                                .addContainerGap(209, Short.MAX_VALUE))
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
                                .addContainerGap(165, Short.MAX_VALUE))
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

        jl_edgeTypeConfigFileName.setText("n/a");

        jl_ruleConfigFileName.setText("n/a");

        javax.swing.GroupLayout jp_RulesLayout = new javax.swing.GroupLayout(jp_Rules);
        jp_Rules.setLayout(jp_RulesLayout);
        jp_RulesLayout.setHorizontalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addComponent(jb_ruleWizard, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_flowRules)
                                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(jl_or)
                                                                        .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jl_ruleConfigFile)
                                                                                .addComponent(jl_edgeTypeConfigFile)))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                                                        .addComponent(jb_chooseRuleConfigFile))
                                                                .addGap(29, 29, 29)
                                                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jl_ruleConfigFileName)
                                                                        .addComponent(jl_edgeTypeConfigFileName))))
                                                .addContainerGap(122, Short.MAX_VALUE))))
        );
        jp_RulesLayout.setVerticalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jl_flowRules)
                                .addGap(27, 27, 27)
                                .addComponent(jb_ruleWizard)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_or)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_edgeTypeConfigFile)
                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                        .addComponent(jl_edgeTypeConfigFileName))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_ruleConfigFile)
                                        .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jb_chooseRuleConfigFile)
                                                .addComponent(jl_ruleConfigFileName)))
                                .addContainerGap(242, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Rules", jp_Rules);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jb_showNodeDataActionPerformed(ActionEvent evt) {

    }

    private void jb_refreshNodeDataAttrsActionPerformed(ActionEvent evt) {
        setjcb_nodeDataAttributes();
    }

    private void jb_refreshEdgeTypeAttrsActionPerformed(ActionEvent evt) {
        setjcb_edgeTypeAttributes();
    }

    private void jb_refreshNetworksActionPerformed(ActionEvent evt) {
        setjcb_networkModel();
    }

    private void jb_ruleWizardActionPerformed(ActionEvent evt) {

    }

    private void jb_chooseRuleConfigFileActionPerformed(ActionEvent evt) {
        JFrame fileLoadFrame = new JFrame("RuleName-Rule configuration");
        fileLoadFrame.setLocation(400, 250);
        fileLoadFrame.setSize(400, 200);
        JFileChooser fileChooser = new JFileChooser();
        File recentDirectory = PSFCActivator.getRecentDirectory();
        fileChooser.setCurrentDirectory(recentDirectory);


        fileChooser.setDialogTitle("Select RuleName-Rule configuration file");
        fileChooser.showOpenDialog(fileLoadFrame);
        String selectedFilePath = null;

        if (fileChooser.getSelectedFile() != null) {
            selectedFilePath = fileChooser.getSelectedFile().getPath();
            PSFCActivator.writeRecentDirectory(selectedFilePath);
        }

        String noFile = "No file selected";
        String name;
        if (selectedFilePath != null) {
            name = fileChooser.getSelectedFile().getName();
            int size = noFile.length();
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_ruleConfigFileName.setText(name);
            this.ruleConfigFile = new File(selectedFilePath);
        }
    }

    private void jb_chooseEdgeTypeConfigFileActionPerformed(ActionEvent evt) {
        JFrame fileLoadFrame = new JFrame("EdgeType-RuleName configuration");
        fileLoadFrame.setLocation(400, 250);
        fileLoadFrame.setSize(400, 200);
        JFileChooser fileChooser = new JFileChooser();
        File recentDirectory = PSFCActivator.getRecentDirectory();
        fileChooser.setCurrentDirectory(recentDirectory);


        fileChooser.setDialogTitle("Select EdgeType-RuleName configuration file");
        fileChooser.showOpenDialog(fileLoadFrame);
        String selectedFilePath = null;

        if (fileChooser.getSelectedFile() != null) {
            selectedFilePath = fileChooser.getSelectedFile().getPath();
            PSFCActivator.writeRecentDirectory(selectedFilePath);

        }

        String noFile = "No file selected";
        String name;
        if (selectedFilePath != null) {
            name = fileChooser.getSelectedFile().getName();
            int size = noFile.length();
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_edgeTypeConfigFileName.setText(name);
            this.edgeTypeRuleNameConfigFile = new File(selectedFilePath);
        }

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
        if (jcb_edgeTypeAttribute.getSelectedItem() == null)
            return;
        String edgeTypeAttr = jcb_edgeTypeAttribute.getSelectedItem().toString();
        CyNetwork selectedNetwork = getSelectedNetwork();
        if (selectedNetwork == null)
            return;
        TreeSet<String> uniqueValues = new TreeSet<String>();
        for (CyRow row : selectedNetwork.getDefaultEdgeTable().getAllRows()) {
            try {
                uniqueValues.add(row.get(edgeTypeAttr, selectedNetwork.getDefaultEdgeTable().
                        getColumn(edgeTypeAttr).getType()).toString());
            } catch (NullPointerException e) {
                return;
            }
        }
        JFrame frame = new JFrame("Unique values of the attribute " + edgeTypeAttr);
        frame.setName(frame.getTitle());
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (String value : uniqueValues) {
            listModel.addElement(value);
        }

        JList<String> list = new JList<String>(listModel);
        JScrollPane panel = new JScrollPane(list);
        frame.setContentPane(panel);
        frame.setLocation(jb_showEdgeTypes.getLocation());
        frame.pack();
        frame.setVisible(true);
    }

    private void jb_sortNetworkActionPerformed(ActionEvent evt) {
        SortNetworkAction sortNetworkAction = new SortNetworkAction();
        sortNetworkAction.setSelectedNetwork(getSelectedNetwork());
        sortNetworkAction.actionPerformed(evt);
    }

    private void setModels() {
        setjcb_networkModel();
        setjcb_sortingAlgorithmsModel();
        setjcb_edgeTypeAttributes();
        setjcb_nodeDataAttributes();
    }

    private void addActionListeners() {
        jcb_network.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_networkActionPerformed();
            }
        });
        jb_calculateFlow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_calculateFlowActionPerformed();
            }
        });
    }

    private void jb_calculateFlowActionPerformed() {
        //Checking statements first!
        HashMap<CyNode, Double> cyNodeDataMap = getCyNodeDataMap();
        HashMap<CyEdge, String> cyEdgeTypeMap = getCyEdgeTypeMap();
        HashMap<CyNode, Integer> cyNodeLevelMap = getCyNodeLevelMap();
        Graph graph = NetworkGraphMapper.graphFromNetwork(getSelectedNetwork());

        PSFCActivator.getLogger().info("Calculating signal flow for Network:");
        PSFCActivator.getLogger().info(getSelectedNetwork().toString());
        PSFCActivator.getLogger().info("cyNodeDataMap:");
        PSFCActivator.getLogger().info(cyNodeDataMap.toString());
        PSFCActivator.getLogger().info("cyEdgeTypeMap:\n" + cyEdgeTypeMap.toString());
        PSFCActivator.getLogger().info("cyNodeLevelMap:\n" + cyNodeLevelMap.toString());
        PSFCActivator.getLogger().info("edgeTypeRuleNameConfigFile:\n" + edgeTypeRuleNameConfigFile.toString());
        PSFCActivator.getLogger().info("ruleConfigFile:\n" + ruleConfigFile.toString());

        HashMap<CyNode, Double> cyNodeFlowScoreMap = PSFAlgorithms.
                calculateFlow(graph, cyNodeDataMap, cyNodeLevelMap,
                        cyEdgeTypeMap, edgeTypeRuleNameConfigFile, ruleConfigFile);
        if (cyNodeFlowScoreMap != null)
            PSFCActivator.getLogger().info("Flow values:\n" + cyNodeFlowScoreMap.toString());
        try {
            NetworkCyManager.setNodeAttributesFromMap(getSelectedNetwork(),
                    cyNodeFlowScoreMap, "FlowScore", Double.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<CyNode, Integer> getCyNodeLevelMap() {
        HashMap<CyNode, Integer> cyNodeLevelMap = new HashMap<CyNode, Integer>();
        CyNetwork network = getSelectedNetwork();
        String levelAttr = "Level";
        for (Object cyNodeObj : network.getNodeList()){
            CyNode cyNode = (CyNode) cyNodeObj;
            CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
            int level = row.get(levelAttr, Integer.class);
            cyNodeLevelMap.put(cyNode, level);
        }
        return cyNodeLevelMap;
    }

    private HashMap<CyEdge, String> getCyEdgeTypeMap() {
        HashMap<CyEdge, String> cyEdgeTypeMap = new HashMap<CyEdge, String>();
        CyNetwork network = getSelectedNetwork();
        String edgeTypeAttr = jcb_edgeTypeAttribute.getSelectedItem().toString();
        for (Object cyEdgeObj : network.getEdgeList()){
            CyEdge cyEdge = (CyEdge) cyEdgeObj;
            CyRow row = network.getDefaultEdgeTable().getRow(cyEdge.getSUID());
            String edgeType = row.get(edgeTypeAttr, String.class);
            cyEdgeTypeMap.put(cyEdge, edgeType);
        }
        return cyEdgeTypeMap;
    }

    private HashMap<CyNode, Double> getCyNodeDataMap() {
        HashMap<CyNode, Double> map = new HashMap<CyNode, Double>();
        CyNetwork network = getSelectedNetwork();
        String attr = jcb_nodeDataAttribute.getSelectedItem().toString();
        for (Object cyNodeObj : network.getNodeList()){
            CyNode cyNode = (CyNode) cyNodeObj;
            CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
            Double data = row.get(attr, Double.class);
            map.put(cyNode, data);
        }
        return map;
    }

    private void jcb_networkActionPerformed() {
        setjcb_edgeTypeAttributes();
        setjcb_nodeDataAttributes();
    }

    private void setjcb_edgeTypeAttributes() {
        CyNetwork selectedNetwork = getSelectedNetwork();
        if (selectedNetwork == null)
            jcb_edgeTypeAttribute.setModel(new DefaultComboBoxModel());
        else {
            Collection<CyColumn> columns = selectedNetwork.getDefaultEdgeTable().getColumns();
            String[] attributes = new String[columns.size()];
            int i = 0;
            for (CyColumn column : columns) {
                attributes[i++] = column.getName();
            }
            jcb_edgeTypeAttribute.setModel(new DefaultComboBoxModel(attributes));

            //Select item from properties, if valid
            String edgeTypeAttr = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.EdgeTypeAttribute.getName());
            for (i = 0; i < jcb_edgeTypeAttribute.getItemCount(); i++){
                Object item = jcb_edgeTypeAttribute.getItemAt(i);
                if (item.toString().equals(edgeTypeAttr))
                    jcb_edgeTypeAttribute.setSelectedItem(item);

            }
        }


    }

    private CyNetwork getSelectedNetwork() {
        CyNetwork selectedNetwork = null;
        if (jcb_network.getSelectedItem() != null)
            for (CyNetwork network : PSFCActivator.networkManager.getNetworkSet())
                if (network.getRow(network).get(CyNetwork.NAME, String.class).
                        equals(jcb_network.getSelectedItem().toString()))
                    selectedNetwork = network;
        return selectedNetwork;
    }

    private void setjcb_nodeDataAttributes() {
        CyNetwork selectedNetwork = null;
        if (jcb_network.getSelectedItem() != null)
            for (CyNetwork network : PSFCActivator.networkManager.getNetworkSet())
                if (network.getRow(network).get(CyNetwork.NAME, String.class).
                        equals(jcb_network.getSelectedItem().toString()))
                    selectedNetwork = network;
        if (selectedNetwork == null)
            jcb_nodeDataAttribute.setModel(new DefaultComboBoxModel());
        else {
            Collection<CyColumn> columns = selectedNetwork.getDefaultNodeTable().getColumns();
            String[] attributes = new String[columns.size()];
            int i = 0;
            for (CyColumn column : columns) {
                attributes[i++] = column.getName();
            }
            jcb_nodeDataAttribute.setModel(new DefaultComboBoxModel(attributes));
        }

    }

    private void setjcb_sortingAlgorithmsModel() {
        String[] sortingAlgorithms = new String[]{"Topological sort"};
        jcb_sortingAlgorithm.setModel(new DefaultComboBoxModel(sortingAlgorithms));
    }

    private void setjcb_networkModel() {
        Set<CyNetwork> networkSet = PSFCActivator.networkManager.getNetworkSet();
        String[] networkTitles = new String[networkSet.size()];
        int index = 0;
        for (CyNetwork network : networkSet) {
            networkTitles[index++] = network.getRow(network).get("Name", String.class);
        }
        jcb_network.setModel(new DefaultComboBoxModel(networkTitles));
        for (int i = 0; i < jcb_network.getItemCount(); i++) {
            Object item = jcb_network.getItemAt(i);
            CyNetwork currentNetwork = PSFCActivator.cyApplicationManager.getCurrentNetwork();
            if (currentNetwork != null)
                if (item.toString().equals(currentNetwork.getRow(currentNetwork).get("Name", String.class)))
                    jcb_network.setSelectedItem(item);
        }
    }

    private void setComponentProperties() {
        //Button groups
        jbg_dataType = new ButtonGroup();
        jbg_dataType.add(jrb_linear);
        jbg_dataType.add(jrb_log);
        jbg_dataType.add(jrb_FC);
        jbg_dataType.add(jrb_logFC);
        //default selection
        jrb_linear.setSelected(true);

        //Set selectionFromProperties
        String dataType = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.NodeDataType.getName());
        if (dataType != null) {
            Enumeration<AbstractButton> buttonEnumeration = jbg_dataType.getElements();
            while (buttonEnumeration.hasMoreElements()) {
                JRadioButton rButton = (JRadioButton) buttonEnumeration.nextElement();
                if (rButton.getText().equals(dataType)) {
                    rButton.setSelected(true);
                    break;
                }
            }
        }

        jbg_multipleDataOption = new ButtonGroup();
        jbg_multipleDataOption.add(jrb_max);
        jbg_multipleDataOption.add(jrb_min);
        jbg_multipleDataOption.add(jrb_mean);

        //default selection
        jrb_mean.setSelected(true);

        //Set selectionFromProperties
        String multipleDataOption = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.MultipleDataOption.getName());
        if (multipleDataOption != null) {
            Enumeration<AbstractButton> buttonEnumeration = jbg_multipleDataOption.getElements();
            while (buttonEnumeration.hasMoreElements()) {
                JRadioButton rButton = (JRadioButton) buttonEnumeration.nextElement();
                if (rButton.getText().equals(multipleDataOption)) {
                    rButton.setSelected(true);
                    break;
                }
            }
        }
    }

}
