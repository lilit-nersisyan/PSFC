package org.cytoscape.psfc.gui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.CalculateScoreFlowAction;
import org.cytoscape.psfc.gui.actions.SortNetworkAction;
import org.cytoscape.psfc.gui.actions.VisualizeFlowAction;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.gui.enums.EMultiSignalProps;
import org.cytoscape.psfc.gui.enums.ENodeDataProps;
import org.cytoscape.psfc.gui.enums.ESortingAlgorithms;
import org.cytoscape.psfc.logic.algorithms.Bootstrap;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.EpsfcProps;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * PUBCLI CLASS PSFCPanel
 * <p/>
 * Sets the components of the app panel, located in the WEST CytoPanel group.
 */
public class PSFCPanel extends JPanel implements CytoPanelComponent {
    private String title = "PSFC";
    private String suidSplit = ":SUID";
    private String iconName = "psfc_icon.png";
    private String levelAttr = EColumnNames.Level.getName();
    private File edgeTypeRuleNameConfigFile;
    private File ruleNameRuleConfigFile;
    private ImageIcon refreshIcon;
    private ImageIcon warningIcon;
    private String refreshIconName = "refresh_button.png";
    private String warningIconName = "warning_icon.png";
    private HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>> networkLevelNodeSignalMap = new HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>>();
    private CalculateScoreFlowAction calculateFlowAction = null;



    public PSFCPanel() {
        this.setPreferredSize(new Dimension(380, getHeight()));
        loadProps();
        initComponents();
        setComponentProperties();
        setToolTips();
        setModels();
        addActionListeners();
        enableButtons();
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
        return null;
    }

    public Icon getPsfcIcon() {
        ClassLoader classLoader = PSFCActivator.class.getClassLoader();
        Icon icon = new ImageIcon(classLoader.getResource(iconName));
        return icon;
    }

    private javax.swing.JButton jb_GeneMatrixFile;
    private javax.swing.JButton jb_SaveOptionsSettings;
    private javax.swing.JButton jb_calculateFlow;
    private javax.swing.JButton jb_checkEdgeTypes;
    private javax.swing.JButton jb_chooseEdgeTypeConfigFile;
    private javax.swing.JButton jb_chooseRuleNameRuleConfigFile;
    private javax.swing.JButton jb_openLogFile;
    private javax.swing.JButton jb_playFlow;
    private javax.swing.JButton jb_projectWebPage;
    private javax.swing.JButton jb_refreshEdgeRanks;
    private javax.swing.JButton jb_refreshEdgeTypeAttrs;
    private javax.swing.JButton jb_refreshNetworks;
    private javax.swing.JButton jb_refreshNodeDataAttrs;
    private javax.swing.JButton jb_refreshWeigths;
    private javax.swing.JButton jb_restoreDefaultOptions;
    private javax.swing.JButton jb_saveGeneralSettings;
    private javax.swing.JButton jb_showState;
    private javax.swing.JButton jb_sortNetwork;
    private javax.swing.JButton jb_userManual;
    private javax.swing.JComboBox jcb_edgeRanks;
    private javax.swing.JComboBox jcb_edgeTypeAttribute;
    private javax.swing.JComboBox jcb_edgeWeights;
    private javax.swing.JComboBox jcb_network;
    private javax.swing.JComboBox jcb_nodeDataAttribute;
    private javax.swing.JComboBox jcb_sortingAlgorithm;
    private javax.swing.JCheckBox jchb_CalculateSignificance;
    private javax.swing.JLabel jl_chooseNetwork;
    private javax.swing.JLabel jl_dataMappingRules;
    private javax.swing.JLabel jl_dataType;
    private javax.swing.JLabel jl_defaultValue;
    private javax.swing.JLabel jl_edgeProcesssingSequence;
    private javax.swing.JLabel jl_edgeTypeConfigFile;
    private javax.swing.JLabel jl_edgeTypeConfigFileName;
    private javax.swing.JLabel jl_flowVisualization;
    private javax.swing.JLabel jl_level;
    private javax.swing.JLabel jl_multiInOutRules;
    private javax.swing.JLabel jl_multiSignalProcessing;
    private javax.swing.JLabel jl_multipleDataRule;
    private javax.swing.JLabel jl_network_and_attrs;
    private javax.swing.JLabel jl_network_and_attrs1;
    private javax.swing.JLabel jl_network_and_attrs2;
    private javax.swing.JLabel jl_numOfSamplings;
    private javax.swing.JLabel jl_psfc;
    private javax.swing.JLabel jl_ruleConfigFile;
    private javax.swing.JLabel jl_ruleNameRuleConfigFileName;
    private javax.swing.JLabel jl_samplingType;
    private javax.swing.JLabel jl_selectEdgeTypeAttribute;
    private javax.swing.JLabel jl_selectNodeDataAttribute;
    private javax.swing.JLabel jl_signalSplitOn;
    private javax.swing.JLabel jl_signalSplitRule;
    private javax.swing.JLabel jl_simpleRules;
    private javax.swing.JLabel jl_sortingAlgorithm;
    private javax.swing.JLabel jl_warnNumSamplings;
    private javax.swing.JPanel jp_Data;
    private javax.swing.JPanel jp_General;
    private javax.swing.JPanel jp_Help;
    private javax.swing.JPanel jp_Options;
    private javax.swing.JPanel jp_Rules;
    private javax.swing.JPanel jp_algorithms;
    private javax.swing.JPanel jp_flowVisualization;
    private javax.swing.JPanel jp_multiInOutRulesPanel;
    private javax.swing.JPanel jp_network_attrs;
    private javax.swing.JPanel jp_significance;
    private javax.swing.JPanel jp_simpleRules;
    private javax.swing.JRadioButton jrb_FC;
    private javax.swing.JRadioButton jrb_GeneCentric;
    private javax.swing.JRadioButton jrb_SampleCentric;
    private javax.swing.JRadioButton jrb_addition;
    private javax.swing.JRadioButton jrb_edgeRanks;
    private javax.swing.JRadioButton jrb_equal;
    private javax.swing.JRadioButton jrb_incomingEdges;
    private javax.swing.JRadioButton jrb_linear;
    private javax.swing.JRadioButton jrb_log;
    private javax.swing.JRadioButton jrb_logFC;
    private javax.swing.JRadioButton jrb_max;
    private javax.swing.JRadioButton jrb_mean;
    private javax.swing.JRadioButton jrb_min;
    private javax.swing.JRadioButton jrb_multiplication;
    private javax.swing.JRadioButton jrb_noRanks;
    private javax.swing.JRadioButton jrb_noSplitRule;
    private javax.swing.JRadioButton jrb_outgoingEdges;
    private javax.swing.JRadioButton jrb_proportional;
    private javax.swing.JRadioButton jrb_suppliedWeights;
    private javax.swing.JRadioButton jrb_updatedNodeScores;
    private javax.swing.JSlider jsl_levels;
    private javax.swing.JTextArea jta_about;
    private javax.swing.JTabbedPane jtp_psfc;
    private javax.swing.JTextField jtxt_defaultValue;
    private javax.swing.JTextField jtxt_level;
    private javax.swing.JTextField jtxt_numOfSamplings;

    private ButtonGroup jbg_dataType;
    private ButtonGroup jbg_multipleDataOption;
    private ButtonGroup jbg_splitSignalOn;
    private ButtonGroup jbg_signalSplitRule;
    private ButtonGroup jbg_multipleSignalProcessingRule;
    private ButtonGroup jbg_signalProcessingOrder;
    private ButtonGroup jbg_bootstrapType;


    private void initComponents() {
        jtp_psfc = new javax.swing.JTabbedPane();
        jp_General = new javax.swing.JPanel();
        jp_network_attrs = new javax.swing.JPanel();
        jl_network_and_attrs = new javax.swing.JLabel();
        jcb_network = new javax.swing.JComboBox();
        jl_chooseNetwork = new javax.swing.JLabel();
        jb_refreshNetworks = new javax.swing.JButton();
        jl_selectEdgeTypeAttribute = new javax.swing.JLabel();
        jcb_edgeTypeAttribute = new javax.swing.JComboBox();
        jb_refreshEdgeTypeAttrs = new javax.swing.JButton();
        jb_checkEdgeTypes = new javax.swing.JButton();
        jcb_nodeDataAttribute = new javax.swing.JComboBox();
        jl_selectNodeDataAttribute = new javax.swing.JLabel();
        jb_refreshNodeDataAttrs = new javax.swing.JButton();
        jp_flowVisualization = new javax.swing.JPanel();
        jsl_levels = new javax.swing.JSlider();
        jl_flowVisualization = new javax.swing.JLabel();
        jb_playFlow = new javax.swing.JButton();
        jb_showState = new javax.swing.JButton();
        jl_level = new javax.swing.JLabel();
        jtxt_level = new javax.swing.JTextField();
        jb_saveGeneralSettings = new javax.swing.JButton();
        jp_Options = new javax.swing.JPanel();
        jp_significance = new javax.swing.JPanel();
        jl_network_and_attrs1 = new javax.swing.JLabel();
        jchb_CalculateSignificance = new javax.swing.JCheckBox();
        jl_numOfSamplings = new javax.swing.JLabel();
        jtxt_numOfSamplings = new javax.swing.JTextField();
        jl_samplingType = new javax.swing.JLabel();
        jrb_SampleCentric = new javax.swing.JRadioButton();
        jrb_GeneCentric = new javax.swing.JRadioButton();
        jb_GeneMatrixFile = new javax.swing.JButton();
        jl_warnNumSamplings = new javax.swing.JLabel();
        jp_algorithms = new javax.swing.JPanel();
        jl_network_and_attrs2 = new javax.swing.JLabel();
        jl_sortingAlgorithm = new javax.swing.JLabel();
        jcb_sortingAlgorithm = new javax.swing.JComboBox();
        jb_sortNetwork = new javax.swing.JButton();
        jb_restoreDefaultOptions = new javax.swing.JButton();
        jb_SaveOptionsSettings = new javax.swing.JButton();
        jp_Rules = new javax.swing.JPanel();
        jp_multiInOutRulesPanel = new javax.swing.JPanel();
        jl_multiInOutRules = new javax.swing.JLabel();
        jl_signalSplitRule = new javax.swing.JLabel();
        jrb_equal = new javax.swing.JRadioButton();
        jrb_proportional = new javax.swing.JRadioButton();
        jrb_noSplitRule = new javax.swing.JRadioButton();
        jrb_suppliedWeights = new javax.swing.JRadioButton();
        jcb_edgeWeights = new javax.swing.JComboBox();
        jl_multiSignalProcessing = new javax.swing.JLabel();
        jrb_addition = new javax.swing.JRadioButton();
        jrb_updatedNodeScores = new javax.swing.JRadioButton();
        jl_edgeProcesssingSequence = new javax.swing.JLabel();
        jrb_noRanks = new javax.swing.JRadioButton();
        jrb_edgeRanks = new javax.swing.JRadioButton();
        jcb_edgeRanks = new javax.swing.JComboBox();
        jb_refreshEdgeRanks = new javax.swing.JButton();
        jb_refreshWeigths = new javax.swing.JButton();
        jrb_multiplication = new javax.swing.JRadioButton();
        jl_signalSplitOn = new javax.swing.JLabel();
        jrb_incomingEdges = new javax.swing.JRadioButton();
        jrb_outgoingEdges = new javax.swing.JRadioButton();
        jp_simpleRules = new javax.swing.JPanel();
        jl_simpleRules = new javax.swing.JLabel();
        jl_edgeTypeConfigFile = new javax.swing.JLabel();
        jb_chooseEdgeTypeConfigFile = new javax.swing.JButton();
        jl_edgeTypeConfigFileName = new javax.swing.JLabel();
        jb_chooseRuleNameRuleConfigFile = new javax.swing.JButton();
        jl_ruleConfigFile = new javax.swing.JLabel();
        jl_ruleNameRuleConfigFileName = new javax.swing.JLabel();
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
        jp_Help = new javax.swing.JPanel();
        jl_psfc = new javax.swing.JLabel();
        jb_projectWebPage = new javax.swing.JButton();
        jb_userManual = new javax.swing.JButton();
        jta_about = new javax.swing.JTextArea();
        jb_calculateFlow = new javax.swing.JButton();
        jb_openLogFile = new javax.swing.JButton();

        jp_network_attrs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_network_and_attrs.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_network_and_attrs.setForeground(new java.awt.Color(51, 102, 0));
        jl_network_and_attrs.setText("Network and attributes");

        jcb_network.setToolTipText("");
        jcb_network.setPreferredSize(new java.awt.Dimension(201, 20));

        jl_chooseNetwork.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_chooseNetwork.setForeground(new java.awt.Color(102, 102, 102));
        jl_chooseNetwork.setText("Network");

        jb_refreshNetworks.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshNetworks.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshNetworks.setPreferredSize(new java.awt.Dimension(20, 20));

        jl_selectEdgeTypeAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectEdgeTypeAttribute.setForeground(new java.awt.Color(102, 102, 102));
        jl_selectEdgeTypeAttribute.setText("Edge type attribute");

        jcb_edgeTypeAttribute.setPreferredSize(new java.awt.Dimension(201, 20));

        jb_refreshEdgeTypeAttrs.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeTypeAttrs.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeTypeAttrs.setPreferredSize(new java.awt.Dimension(20, 20));

        jb_checkEdgeTypes.setText("Check");
        jb_checkEdgeTypes.setPreferredSize(new java.awt.Dimension(59, 20));

        jcb_nodeDataAttribute.setPreferredSize(new java.awt.Dimension(201, 20));

        jl_selectNodeDataAttribute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_selectNodeDataAttribute.setForeground(new java.awt.Color(102, 102, 102));
        jl_selectNodeDataAttribute.setText("Node data attribute");

        jb_refreshNodeDataAttrs.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshNodeDataAttrs.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshNodeDataAttrs.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jp_network_attrsLayout = new javax.swing.GroupLayout(jp_network_attrs);
        jp_network_attrs.setLayout(jp_network_attrsLayout);
        jp_network_attrsLayout.setHorizontalGroup(
                jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_chooseNetwork)
                                        .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_checkEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jp_network_attrsLayout.setVerticalGroup(
                jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs)
                                .addGap(12, 12, 12)
                                .addComponent(jl_chooseNetwork)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_checkEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jp_flowVisualization.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_flowVisualization.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_flowVisualization.setForeground(new java.awt.Color(51, 102, 0));
        jl_flowVisualization.setText("Flow visualization");
        jl_flowVisualization.setMaximumSize(new java.awt.Dimension(145, 15));

        jb_playFlow.setText("Play flow");

        jb_showState.setText("Show state");

        jl_level.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_level.setForeground(new java.awt.Color(102, 102, 102));
        jl_level.setText("Level:");

        jtxt_level.setText("0");
        jtxt_level.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxt_levelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jp_flowVisualizationLayout = new javax.swing.GroupLayout(jp_flowVisualization);
        jp_flowVisualization.setLayout(jp_flowVisualizationLayout);
        jp_flowVisualizationLayout.setHorizontalGroup(
                jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addComponent(jl_level)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtxt_level, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jb_playFlow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                                                        .addComponent(jsl_levels, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jb_showState)))
                                .addContainerGap())
                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                .addComponent(jl_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jp_flowVisualizationLayout.setVerticalGroup(
                jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                .addComponent(jl_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_level)
                                        .addComponent(jtxt_level, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_showState)
                                        .addComponent(jsl_levels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_playFlow)
                                .addContainerGap())
        );

        jb_saveGeneralSettings.setText("Save settings");

        javax.swing.GroupLayout jp_GeneralLayout = new javax.swing.GroupLayout(jp_General);
        jp_General.setLayout(jp_GeneralLayout);
        jp_GeneralLayout.setHorizontalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_network_attrs, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                                        .addComponent(jp_flowVisualization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_GeneralLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jb_saveGeneralSettings)))
                                .addContainerGap())
        );
        jp_GeneralLayout.setVerticalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jp_network_attrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                                .addComponent(jp_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addComponent(jb_saveGeneralSettings)
                                .addContainerGap())
        );

        jtp_psfc.addTab("General", jp_General);

        jp_Options.setPreferredSize(new java.awt.Dimension(400, 500));

        jp_significance.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_network_and_attrs1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_network_and_attrs1.setForeground(new java.awt.Color(51, 102, 0));
        jl_network_and_attrs1.setText("Significance calculation");

        jchb_CalculateSignificance.setText("Calculate significance after calculating flow");

        jl_numOfSamplings.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_numOfSamplings.setForeground(new java.awt.Color(102, 102, 102));
        jl_numOfSamplings.setText("No. of samplings");

        jtxt_numOfSamplings.setText("200");

        jl_samplingType.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_samplingType.setForeground(new java.awt.Color(102, 102, 102));
        jl_samplingType.setText("Sampling type");

        jrb_SampleCentric.setText("Sample centric");

        jrb_GeneCentric.setText("Gene centric");

        jb_GeneMatrixFile.setText("Expr Matrix File");

        jl_warnNumSamplings.setText(" ");

        javax.swing.GroupLayout jp_significanceLayout = new javax.swing.GroupLayout(jp_significance);
        jp_significance.setLayout(jp_significanceLayout);
        jp_significanceLayout.setHorizontalGroup(
                jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs1)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jchb_CalculateSignificance)
                                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                                .addComponent(jl_numOfSamplings)
                                                .addGap(18, 18, 18)
                                                .addComponent(jtxt_numOfSamplings, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jl_warnNumSamplings))
                                        .addComponent(jl_samplingType)
                                        .addComponent(jrb_SampleCentric)
                                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                                .addComponent(jrb_GeneCentric)
                                                .addGap(18, 18, 18)
                                                .addComponent(jb_GeneMatrixFile)))
                                .addContainerGap())
        );
        jp_significanceLayout.setVerticalGroup(
                jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchb_CalculateSignificance)
                                .addGap(13, 13, 13)
                                .addComponent(jl_samplingType)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrb_SampleCentric, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_GeneCentric)
                                        .addComponent(jb_GeneMatrixFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_numOfSamplings)
                                        .addComponent(jtxt_numOfSamplings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_warnNumSamplings))
                                .addContainerGap(21, Short.MAX_VALUE))
        );

        jp_algorithms.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_network_and_attrs2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_network_and_attrs2.setForeground(new java.awt.Color(51, 102, 0));
        jl_network_and_attrs2.setText("Algorithms");

        jl_sortingAlgorithm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_sortingAlgorithm.setForeground(new java.awt.Color(102, 102, 102));
        jl_sortingAlgorithm.setText("Sorting algorithm");

        jcb_sortingAlgorithm.setPreferredSize(new java.awt.Dimension(201, 20));

        jb_sortNetwork.setText("Sort");
        jb_sortNetwork.setPreferredSize(new java.awt.Dimension(59, 20));

        javax.swing.GroupLayout jp_algorithmsLayout = new javax.swing.GroupLayout(jp_algorithms);
        jp_algorithms.setLayout(jp_algorithmsLayout);
        jp_algorithmsLayout.setHorizontalGroup(
                jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_network_and_attrs2)
                                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_sortingAlgorithm))
                                                .addGap(17, 17, 17)
                                                .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 20, Short.MAX_VALUE))
        );
        jp_algorithmsLayout.setVerticalGroup(
                jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_sortingAlgorithm)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 17, Short.MAX_VALUE))
        );

        jb_restoreDefaultOptions.setText("Restore defaults");

        jb_SaveOptionsSettings.setText("Save Settings");

        javax.swing.GroupLayout jp_OptionsLayout = new javax.swing.GroupLayout(jp_Options);
        jp_Options.setLayout(jp_OptionsLayout);
        jp_OptionsLayout.setHorizontalGroup(
                jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_algorithms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_significance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                                .addComponent(jb_restoreDefaultOptions)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jb_SaveOptionsSettings)))
                                .addContainerGap())
        );
        jp_OptionsLayout.setVerticalGroup(
                jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_algorithms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(jp_significance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                                .addGroup(jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_restoreDefaultOptions)
                                        .addComponent(jb_SaveOptionsSettings))
                                .addGap(23, 23, 23))
        );

        jtp_psfc.addTab("Options", jp_Options);

        jp_multiInOutRulesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_multiInOutRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_multiInOutRules.setForeground(new java.awt.Color(51, 102, 0));
        jl_multiInOutRules.setText("Multiple input and output edge rules");

        jl_signalSplitRule.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_signalSplitRule.setForeground(new java.awt.Color(102, 102, 102));
        jl_signalSplitRule.setText("Signal split rule");

        jrb_equal.setText("Equal");

        jrb_proportional.setText("Proportional");

        jrb_noSplitRule.setText("None");

        jrb_suppliedWeights.setText("Supplied weights");

        jcb_edgeWeights.setMaximumSize(new java.awt.Dimension(119, 20));
        jcb_edgeWeights.setMinimumSize(new java.awt.Dimension(119, 20));
        jcb_edgeWeights.setName("");
        jcb_edgeWeights.setPreferredSize(new java.awt.Dimension(119, 20));

        jl_multiSignalProcessing.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_multiSignalProcessing.setForeground(new java.awt.Color(102, 102, 102));
        jl_multiSignalProcessing.setText("Multiple signal processing rule");

        jrb_addition.setText("Addition");

        jrb_updatedNodeScores.setText("Updated node scores");

        jl_edgeProcesssingSequence.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_edgeProcesssingSequence.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeProcesssingSequence.setText("Signal processing order");

        jrb_noRanks.setText("None");

        jrb_edgeRanks.setText("Edge ranks");

        jcb_edgeRanks.setMaximumSize(new java.awt.Dimension(115, 20));
        jcb_edgeRanks.setMinimumSize(new java.awt.Dimension(115, 20));
        jcb_edgeRanks.setPreferredSize(new java.awt.Dimension(115, 20));

        jb_refreshEdgeRanks.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeRanks.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshEdgeRanks.setPreferredSize(new java.awt.Dimension(20, 20));

        jb_refreshWeigths.setMaximumSize(new java.awt.Dimension(20, 20));
        jb_refreshWeigths.setMinimumSize(new java.awt.Dimension(20, 20));
        jb_refreshWeigths.setPreferredSize(new java.awt.Dimension(20, 20));

        jrb_multiplication.setText("Multiplication");

        jl_signalSplitOn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_signalSplitOn.setForeground(new java.awt.Color(102, 102, 102));
        jl_signalSplitOn.setText("Split signal on ");

        jrb_incomingEdges.setText("Incoming edges");

        jrb_outgoingEdges.setText("Outgoing edges");

        javax.swing.GroupLayout jp_multiInOutRulesPanelLayout = new javax.swing.GroupLayout(jp_multiInOutRulesPanel);
        jp_multiInOutRulesPanel.setLayout(jp_multiInOutRulesPanelLayout);
        jp_multiInOutRulesPanelLayout.setHorizontalGroup(
                jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_multiInOutRules)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_noRanks)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jrb_edgeRanks)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jcb_edgeRanks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshEdgeRanks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_suppliedWeights)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcb_edgeWeights, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshWeigths, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jl_signalSplitRule)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_noSplitRule)
                                                .addGap(4, 4, 4)
                                                .addComponent(jrb_proportional)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jrb_equal))
                                        .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jl_multiSignalProcessing, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                        .addComponent(jrb_updatedNodeScores)
                                                        .addGap(67, 67, 67)))
                                        .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                        .addComponent(jrb_multiplication)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jrb_addition))
                                                .addComponent(jl_edgeProcesssingSequence, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jl_signalSplitOn)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_incomingEdges)
                                                .addGap(18, 18, 18)
                                                .addComponent(jrb_outgoingEdges)))
                                .addContainerGap(35, Short.MAX_VALUE))
        );
        jp_multiInOutRulesPanelLayout.setVerticalGroup(
                jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jl_multiInOutRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jl_signalSplitRule)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jrb_proportional)
                                                        .addComponent(jrb_noSplitRule)
                                                        .addComponent(jrb_equal))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jrb_suppliedWeights, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jcb_edgeWeights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(jb_refreshWeigths, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_signalSplitOn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_incomingEdges)
                                        .addComponent(jrb_outgoingEdges))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_multiSignalProcessing, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jrb_updatedNodeScores)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_multiplication)
                                        .addComponent(jrb_addition))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jl_edgeProcesssingSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_refreshEdgeRanks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jrb_noRanks, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jrb_edgeRanks, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jcb_edgeRanks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(14, 14, 14))
        );

        jp_simpleRules.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_simpleRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_simpleRules.setForeground(new java.awt.Color(51, 102, 0));
        jl_simpleRules.setText("Simple rules");

        jl_edgeTypeConfigFile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_edgeTypeConfigFile.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeTypeConfigFile.setText("EdgeTypes config file");

        jb_chooseEdgeTypeConfigFile.setText("Choose file");

        jl_edgeTypeConfigFileName.setText("n/a");

        jb_chooseRuleNameRuleConfigFile.setText("Choose file");

        jl_ruleConfigFile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_ruleConfigFile.setForeground(new java.awt.Color(102, 102, 102));
        jl_ruleConfigFile.setText("Rule config file");

        jl_ruleNameRuleConfigFileName.setText("n/a");

        javax.swing.GroupLayout jp_simpleRulesLayout = new javax.swing.GroupLayout(jp_simpleRules);
        jp_simpleRules.setLayout(jp_simpleRulesLayout);
        jp_simpleRulesLayout.setHorizontalGroup(
                jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_simpleRules)
                                        .addComponent(jl_edgeTypeConfigFile)
                                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                                        .addComponent(jb_chooseRuleNameRuleConfigFile)
                                                        .addComponent(jl_ruleConfigFile))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_edgeTypeConfigFileName)
                                                        .addComponent(jl_ruleNameRuleConfigFileName))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_simpleRulesLayout.setVerticalGroup(
                jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                .addComponent(jl_simpleRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jl_edgeTypeConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                        .addComponent(jl_edgeTypeConfigFileName))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_ruleConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_chooseRuleNameRuleConfigFile)
                                        .addComponent(jl_ruleNameRuleConfigFileName))
                                .addGap(0, 16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jp_RulesLayout = new javax.swing.GroupLayout(jp_Rules);
        jp_Rules.setLayout(jp_RulesLayout);
        jp_RulesLayout.setHorizontalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_simpleRules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jp_RulesLayout.setVerticalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_simpleRules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jtp_psfc.addTab("Rules", jp_Rules);

        jp_Data.setForeground(new java.awt.Color(102, 102, 102));

        jl_dataMappingRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_dataMappingRules.setForeground(new java.awt.Color(102, 102, 102));
        jl_dataMappingRules.setText("Data mapping rules");

        jl_dataType.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_dataType.setForeground(new java.awt.Color(102, 102, 102));
        jl_dataType.setText("Data type");

        jrb_linear.setText("Linear");

        jrb_log.setText("Log");

        jrb_logFC.setText("LogFC");

        jrb_FC.setText("FC");

        jl_defaultValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_defaultValue.setForeground(new java.awt.Color(102, 102, 102));
        jl_defaultValue.setText("Default value");

        jtxt_defaultValue.setText("0");

        jl_multipleDataRule.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_multipleDataRule.setForeground(new java.awt.Color(102, 102, 102));
        jl_multipleDataRule.setText("Multiple data rule");

        jrb_min.setText("Min");

        jrb_max.setText("Max");

        jrb_mean.setText("Mean");

        javax.swing.GroupLayout jp_DataLayout = new javax.swing.GroupLayout(jp_Data);
        jp_Data.setLayout(jp_DataLayout);
        jp_DataLayout.setHorizontalGroup(
                jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_DataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_dataMappingRules)
                                        .addComponent(jl_dataType, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jp_DataLayout.createSequentialGroup()
                                                .addComponent(jl_defaultValue)
                                                .addGap(31, 31, 31)
                                                .addComponent(jtxt_defaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jl_multipleDataRule)
                                        .addGroup(jp_DataLayout.createSequentialGroup()
                                                .addComponent(jrb_min)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jrb_max)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jrb_mean))
                                        .addGroup(jp_DataLayout.createSequentialGroup()
                                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jrb_linear, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jrb_log))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jrb_logFC, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jrb_FC))))
                                .addContainerGap(169, Short.MAX_VALUE))
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
                                .addGroup(jp_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_min)
                                        .addComponent(jrb_max)
                                        .addComponent(jrb_mean))
                                .addContainerGap(218, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Data", jp_Data);

        jl_psfc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jb_projectWebPage.setText("Go to project web page");

        jb_userManual.setText("Open User Manual");

        jta_about.setBackground(new java.awt.Color(240, 240, 240));
        jta_about.setColumns(20);
        jta_about.setEditable(false);
        jta_about.setRows(5);
        jta_about.setAutoscrolls(false);
        jta_about.setBorder(null);

        javax.swing.GroupLayout jp_HelpLayout = new javax.swing.GroupLayout(jp_Help);
        jp_Help.setLayout(jp_HelpLayout);
        jp_HelpLayout.setHorizontalGroup(
                jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGap(64, 64, 64)
                                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jb_userManual, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jb_projectWebPage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGap(32, 32, 32)
                                                .addComponent(jta_about, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGap(47, 47, 47)
                                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(45, Short.MAX_VALUE))
        );
        jp_HelpLayout.setVerticalGroup(
                jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jta_about, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jb_projectWebPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jb_userManual)
                                .addContainerGap(22, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Help", jp_Help);

        jb_calculateFlow.setBackground(new java.awt.Color(51, 102, 0));
        jb_calculateFlow.setText("Calculate flow");
        jb_calculateFlow.setBorderPainted(false);

        jb_openLogFile.setText("PSFC log");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jb_openLogFile, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                                                .addComponent(jb_calculateFlow)
                                                .addGap(23, 23, 23))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_calculateFlow)
                                        .addComponent(jb_openLogFile))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void setModels() {
        setjcb_networkModel();
        setjcb_sortingAlgorithmsModel();
        setJcbAttributes(jcb_edgeTypeAttribute, EpsfcProps.EdgeTypeAttribute);
        setjcb_nodeDataAttributes();
        if (jrb_suppliedWeights.isSelected())
            setJcbAttributes(jcb_edgeWeights, EpsfcProps.EdgeWeigthsAttribute);
        if (jrb_edgeRanks.isSelected())
            setJcbAttributes(jcb_edgeRanks, EpsfcProps.EdgeRankAttribute);
    }



    private void addActionListeners() {
        // Actions: shared buttons
        addActionListeners_sharedButtons();
        // Actions: jp_General
        addActionListeners_jp_General();
        // Actions: jp_Options
        addActionListeners_jp_Options();
        // Actions: jp_Rules
        addActionListeners_jp_Rules();
        // Actions: jp_Data
        addActionListeners_jp_Data();
        // Actions: jp_Help
        addActionListeners_jp_Help();
    }

    private void addActionListeners_sharedButtons() {
        jb_openLogFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_openLogFileActionPerformed();
            }
        });

        jb_calculateFlow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_calculateFlowActionPerformed(e);
            }
        });
    }

    private void addActionListeners_jp_General() {
        //Networks and attributes
        jcb_network.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_networkActionPerformed();
            }
        });
        jcb_network.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (isSelected)
                {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                    if (-1 < index)
                    {
                        list.setToolTipText(jcb_network.getItemAt(index).toString());
                    }
                }
                else
                {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setFont(list.getFont());
                setText((value == null) ? "" : value.toString());
                return this;
            }
        });
        jb_refreshNetworks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_refreshNetworksActionPerformed();
            }
        });

        //no action for jcb_edgeTypeAttribute
        jb_refreshEdgeTypeAttrs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_refreshEdgeTypeAttrsActionPerformed();
            }
        });
        jb_checkEdgeTypes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_checkEdgeTypesActionPerformed();
            }
        });

        //no action for jcb_nodeDataAttribute
        jb_refreshNodeDataAttrs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_refreshNodeDataAttrsActionPerformed();
            }
        });


        //Flow visualization
        jsl_levels.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jtxt_level.setText("" + jsl_levels.getValue());
            }
        });
        jb_showState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_showStateActionPerformed();
            }
        });
        jb_playFlow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_playFlowActionPerformed();
            }
        });

        //Save settings
        jb_saveGeneralSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_saveGeneralSettingsActionPerformed();
            }
        });
    }

    private void addActionListeners_jp_Options() {
        //Algorithms
        jcb_sortingAlgorithm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_sortingAlgorithmActionPerformed();
            }
        });
        jb_sortNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_sortNetworkActionPerformed(e);
            }
        });

        //Significance calculation
        //no action for jchb_calculateSignificance
        jrb_SampleCentric.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_SampleCentricActionPerformed();
            }
        });
        //no action for jrb_GeneCentric
        jrb_GeneCentric.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_GeneCentricActionPerformed();
            }
        });
        jb_GeneMatrixFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_GeneMatrixFileActionPerformed();
            }
        });

        jtxt_numOfSamplings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_numOfSamplingsActionPerformed();
            }
        });

        jb_restoreDefaultOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_restoreDefaultOptionsActionPerformed();
            }
        });

        jb_SaveOptionsSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_SaveOptionsSettingsActionPerformed();
            }
        });
    }

    private void addActionListeners_jp_Rules() {
        // Simple rules
        jb_chooseEdgeTypeConfigFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_chooseEdgeTypeConfigFileActionPerformed();
            }
        });

        jb_chooseRuleNameRuleConfigFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_chooseRuleNameRuleConfigFileActionPerformed();
            }
        });

        // Multiple input and output edge rules: Signal split rule
        jrb_noSplitRule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_noSplitRuleActionPerformed();
            }
        });
        jrb_proportional.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_proportionalActionPerformed();
            }
        });
        jrb_equal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_equalActionPerformed();
            }
        });
        jrb_suppliedWeights.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_suppliedWeightsActionPerformed();
            }
        });
        jb_refreshWeigths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_refreshWeigthsActionPerformed();
            }
        });

        // Multiple input and output edge rules: Split signal on
        // No action for jrb_incomingEdges
        // No action for jrb_outgoingEdges

        // Multiple input and output edge rules: Multiple signal processing rule
        jrb_updatedNodeScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_updatedNodeScoresActionPerformed();
            }
        });
        jrb_multiplication.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_multiplicationActionPerformed();
            }
        });
        jrb_addition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_additionActionPerformed();
            }
        });

        // Multiple input and output edge rules: Signal processing order
        jrb_noRanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_noRanksActionPerformed();
            }
        });
        jrb_edgeRanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_edgeRanksActionPerformed();
            }
        });
        jcb_edgeRanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_edgeRanksActionPerformed();
            }
        });
        jb_refreshEdgeRanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_refreshEdgeRanksActionPerformed();
            }
        });
    }

    private void addActionListeners_jp_Data() {
        //jrb_linear
        //jrb_FC
        //jrb_log
        //jrb_logFC
        //jtxt_defaultValue
        //jrb_min
        //jrb_max
        //jrb_mean
    }

    private void addActionListeners_jp_Help() {
        jb_projectWebPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_projectWebPageActionPerformed();
            }
        });

        jb_userManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_userManualActionPerformed();
            }
        });
    }


    /******************
     Actions: shared buttons
     ******************/
    private void jb_openLogFileActionPerformed() {
        final File logFile = PSFCActivator.getPsfcLogFile();
        if (logFile == null || !logFile.exists())
            JOptionPane.showMessageDialog(this, "PSFC uesr message",
                    "Problem loading log file.", JOptionPane.OK_OPTION);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(System.getProperty("os.name").toLowerCase().contains("windows")){
                        String cmd = "rundll32 url.dll,FileProtocolHandler "
                                + logFile.getCanonicalPath();
                        Runtime.getRuntime().exec(cmd);
                    } else {
                        Desktop.getDesktop().edit(logFile);
                    }
                } catch (IllegalArgumentException iae) {
                    System.out.println("File Not Found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }
    private void jb_calculateFlowActionPerformed(ActionEvent e) {
        CyNetwork network = getSelectedNetwork();
        if (network == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected network does not exist. \nPlease, refresh the network list and choose a valid network for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION);
            return;
        }
        if (network.getNodeList().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "The network you have chosen contains no nodes.\n " +
                            "Please, choose a valid network for pathway flow calculation",
                    "PSFC user message", JOptionPane.OK_OPTION
            );
            return;
        }
        CyColumn edgeTypeColumn = getEdgeTypeColumn();
        if (edgeTypeColumn == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected EdgeType column does not exist. \nPlease, refresh the column list and choose a valid EdgeType column for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION);
            return;
        }
        boolean isString = true;
        try {
            if (!(edgeTypeColumn.getType().newInstance() instanceof String))
                isString = false;
        } catch (InstantiationException e1) {
            isString = false;
        } catch (IllegalAccessException e1) {
            isString = false;
        }
        if (!isString) {
            JOptionPane.showMessageDialog(this,
                    "Illegal EdgeType column: should be of type String. " +
                            "\nPlease, choose a valid column for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION
            );
            return;
        }
        CyColumn nodeDataColumn = getNodeDataColumn();
        if (nodeDataColumn == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected Node Data column does not exist. \nPlease, refresh the column list and choose a valid Node Data column for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION);
            return;
        }
        boolean isNumber = true;
        if (!nodeDataColumn.getType().getName().equals(Double.class.getName()))
            if (!nodeDataColumn.getType().getName().equals(Integer.class.getName()))
                isNumber = false;
        if (!isNumber) {
            JOptionPane.showMessageDialog(this,
                    "Illegal NodeData column: should be numeric. " +
                            "\nPlease, choose a valid column for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION
            );
            return;
        }

        boolean sorted = checkSorted(network);
        SortNetworkAction sortNetworkAction;
        PSFCActivator.getLogger().debug("PSFC flow calculation calling network sorting action.");
        if (!sorted) {
            sortNetworkAction = new SortNetworkAction(network, getSortingAlgorithm());
            sortNetworkAction.actionPerformed(e);
            while (!sortNetworkAction.isPerformed()) {
                try {
                    Thread.sleep(50);
                    System.out.println("Pathway flow calculation waiting for network sorting");
                } catch (InterruptedException e1) {
                    PSFCActivator.getLogger().error("Error while sorting the network: " + e1.getMessage(), e1);
                }
            }
            if (!sortNetworkAction.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        "An error occured while sortin the network. \n" +
                                "Please, see the PSFC log file at "
                                + PSFCActivator.getPSFCDir() + " directory for details.",
                        "PSFC error message", JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        CyColumn nodeLevelColumn = getNodeLevelColumn();
        Properties nodeDataProperties = getNodeDataProperties();
        Properties multiSignalProps = getMultiSignalProperties();
        if (multiSignalProps == null)
            return;

        calculateFlowAction = new CalculateScoreFlowAction(
                network, edgeTypeColumn, nodeDataColumn, nodeLevelColumn,
                edgeTypeRuleNameConfigFile, ruleNameRuleConfigFile, nodeDataProperties,
                multiSignalProps, jchb_CalculateSignificance.isSelected(), this);
        if (jchb_CalculateSignificance.isSelected()) {
            calculateFlowAction.setBootstrapProps(getBootstrapProperties());
        }
        calculateFlowAction.actionPerformed(e);
    }

    /******************
     Actions: jp_General
     ******************/
    //Network and attributes
    private void setjcb_networkModel() {
        Set<CyNetwork> networkSet = PSFCActivator.networkManager.getNetworkSet();
        String[] networkTitles = new String[networkSet.size()];
        int index = 0;
        for (CyNetwork network : networkSet) {
            networkTitles[index++] = network.getRow(network).get("Name", String.class) + suidSplit + network.getSUID();
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
    private void jcb_networkActionPerformed() {
        setJcbAttributes(jcb_edgeTypeAttribute, EpsfcProps.EdgeTypeAttribute);
        setjcb_nodeDataAttributes();
        if (jrb_suppliedWeights.isSelected())
            setJcbAttributes(jcb_edgeWeights, EpsfcProps.EdgeWeigthsAttribute);
        if (jrb_edgeRanks.isSelected())
            setJcbAttributes(jcb_edgeRanks, EpsfcProps.EdgeRankAttribute);
        enableButtons();
        activateFlowVisualizationPanel(getSelectedNetwork());
    }
    private void jb_refreshNetworksActionPerformed() {
        setjcb_networkModel();
        enableButtons();
    }

    private void setjcb_nodeDataAttributes() {
        CyNetwork selectedNetwork = getSelectedNetwork();

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

            //Select item from properties, if valid
            String edgeTypeAttr = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.NodeDataAttribute.getName());
            for (i = 0; i < jcb_nodeDataAttribute.getItemCount(); i++) {
                Object item = jcb_nodeDataAttribute.getItemAt(i);
                if (item.toString().equals(edgeTypeAttr))
                    jcb_nodeDataAttribute.setSelectedItem(item);
            }
        }

    }
    private void jb_refreshNodeDataAttrsActionPerformed() {
        setjcb_nodeDataAttributes();
        enableButtons();
    }

    private void jb_refreshEdgeTypeAttrsActionPerformed() {
        setJcbAttributes(jcb_edgeTypeAttribute, EpsfcProps.EdgeTypeAttribute);
        enableButtons();
    }
    private void jb_checkEdgeTypesActionPerformed() {
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
        frame.setLocation(jb_checkEdgeTypes.getLocation());
        frame.pack();
        frame.setVisible(true);
    }

    //Flow visualization
    private void jtxt_levelActionPerformed(ActionEvent evt) {
        int value;
        try {
            value = Integer.parseInt(jtxt_level.getText());
        } catch (NumberFormatException e) {
            value = 0;
        }
        if (value > jsl_levels.getMaximum())
            value = jsl_levels.getMaximum();
        else if (value < 0)
            value = 0;
        jtxt_level.setText(value + "");
        jsl_levels.setValue(value);
    }
    private void jb_showStateActionPerformed() {
        ArrayList<Integer> levels = new ArrayList<Integer>();
        levels.add(jsl_levels.getValue());
        VisualizeFlowAction visualizeFlowAction = createVisualizeFlowAction(levels);
        visualizeFlowAction.actionPerformed(null);
    }
    private VisualizeFlowAction createVisualizeFlowAction(ArrayList<Integer> levels) {
        CyNetwork network = getSelectedNetwork();
        if (network == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected network does not exist. \nPlease, refresh the network list and choose a valid network for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION);
            return null;
        }
        double minSignal = Double.MAX_VALUE;
        double maxSignal = Double.MIN_VALUE;
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++) {
            HashMap<CyNode, Double> nodeSignalMap = networkLevelNodeSignalMap.get(network).get(level);
            if (nodeSignalMap != null)
                for (CyNode cyNode : nodeSignalMap.keySet()) {
                    double signal = nodeSignalMap.get(cyNode);
                    if (signal < minSignal)
                        minSignal = signal;
                    else if (signal > maxSignal)
                        maxSignal = signal;
                }
        }

        return new VisualizeFlowAction(network, minSignal, maxSignal, levels, this);
    }
    private void jb_playFlowActionPerformed() {
        ArrayList<Integer> levels = new ArrayList<Integer>();
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++)
            levels.add(level);
        VisualizeFlowAction visualizeFlowAction = createVisualizeFlowAction(levels);
        visualizeFlowAction.actionPerformed(null);
    }
    public void setVisualizationComponents(CyNetwork network,
                                           HashMap<Integer, HashMap<CyNode, Double>> levelNodeSignalMap) {
        networkLevelNodeSignalMap.put(network, levelNodeSignalMap);
        activateFlowVisualizationPanel(network);
    }
    private void activateFlowVisualizationPanel(CyNetwork network) {
        HashMap<Integer, HashMap<CyNode, Double>> levelNodeSignalMap = networkLevelNodeSignalMap.get(network);
        if (levelNodeSignalMap == null) {
            jsl_levels.setEnabled(false);
            jtxt_level.setEnabled(false);
            jb_showState.setEnabled(false);
            jb_playFlow.setEnabled(false);
        } else {
            jsl_levels.setMinimum(0);
            jsl_levels.setMaximum(levelNodeSignalMap.size() - 1);
            jsl_levels.setEnabled(true);
            jtxt_level.setEnabled(true);
            jb_showState.setEnabled(true);
            jb_playFlow.setEnabled(true);
        }
    }
    public JSlider getJsl_levels() {
        return jsl_levels;
    }

    private void jb_saveGeneralSettingsActionPerformed() {
        Properties psfcProps = PSFCActivator.getPsfcProps();
        //columns
        try {
            psfcProps.setProperty(EpsfcProps.NodeDataAttribute.getName(), jcb_nodeDataAttribute.getSelectedItem().toString());
            psfcProps.setProperty(EpsfcProps.EdgeTypeAttribute.getName(), jcb_edgeTypeAttribute.getSelectedItem().toString());
            psfcProps.setProperty(EpsfcProps.EdgeWeigthsAttribute.getName(), jcb_edgeWeights.getSelectedItem().toString());
            psfcProps.setProperty(EpsfcProps.EdgeRankAttribute.getName(), jcb_edgeRanks.getSelectedItem().toString());
        } catch (NullPointerException e) {
        }


        //Node data type
        Enumeration<AbstractButton> buttons = jbg_dataType.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.NodeDataType.getName(), button.getText());
        }

        //Node default value
        //???????
        //Multiple data rule
        buttons = jbg_multipleDataOption.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.MultipleDataOption.getName(), button.getText());
        }

        //edgeTypeRuleNameConfigFile
        if (edgeTypeRuleNameConfigFile != null)
            psfcProps.setProperty(EpsfcProps.EdgeTypeRuleNameConfigFile.getName(), edgeTypeRuleNameConfigFile.getAbsolutePath());
        //ruleNameRuleConfigFile
        if (ruleNameRuleConfigFile != null)
            psfcProps.setProperty(EpsfcProps.RuleNameRuleConfigFile.getName(), ruleNameRuleConfigFile.getAbsolutePath());


        //MultipleEdgeRules
        buttons = jbg_signalSplitRule.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.SplitSignalRule.getName(), button.getText());
        }


        buttons = jbg_splitSignalOn.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.SplitSignalOn.getName(), button.getText());
        }

        buttons = jbg_signalProcessingOrder.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.SignalProcessingOrder.getName(), button.getText());
        }

        buttons = jbg_multipleSignalProcessingRule.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected())
                psfcProps.setProperty(EpsfcProps.MultipleSignalProcessingRule.getName(), button.getText());
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(PSFCActivator.getPsfcPropsFile());
            PSFCActivator.getPsfcProps().store(outputStream, "PSFC property file");
            outputStream.close();
        } catch (FileNotFoundException e) {
            PSFCActivator.getLogger().error("Could not write to psfc.props file. Reason: " + e.getMessage(), e);
        } catch (IOException e) {
            PSFCActivator.getLogger().error("Could not write to psfc.props file. Reason: " + e.getMessage(), e);
        }
    }
    /******************
     Actions: jp_Options
     ******************/
    //Algorithms
    private void setjcb_sortingAlgorithmsModel() {
//        String[] sortingAlgorithms = ESortingAlgorithms.getAlgorithmNames();
        String[] sortingAlgorithms = new String[]{ESortingAlgorithms.TOPOLOGICALSORT.getName()};
        jcb_sortingAlgorithm.setModel(new DefaultComboBoxModel(sortingAlgorithms));
    }
    private void jcb_sortingAlgorithmActionPerformed() {

    }
    private void jb_sortNetworkActionPerformed(ActionEvent e) {
        CyNetwork selectedNetwork = getSelectedNetwork();
        if (selectedNetwork != null) {
            SortNetworkAction sortNetworkAction = new SortNetworkAction(selectedNetwork, getSortingAlgorithm());
            sortNetworkAction.actionPerformed(e);
        }
    }
    private int getSortingAlgorithm() {
        return ESortingAlgorithms.getNum(jcb_sortingAlgorithm.getSelectedItem().toString());
    }

    //Significance calculation
    private void jrb_SampleCentricActionPerformed() {
        enableButtons();
    }
    private void jrb_GeneCentricActionPerformed() {
        enableButtons();
    }
    private void jb_GeneMatrixFileActionPerformed() {

    }
    private void jtxt_numOfSamplingsActionPerformed() {
        int numOfSamplings;
        try{
            numOfSamplings = Integer.parseInt(jtxt_numOfSamplings.getText());
            if (numOfSamplings < 1)
                numOfSamplings = Bootstrap.defaultNumOfSamplings;
        } catch (NumberFormatException e){
            numOfSamplings = Bootstrap.defaultNumOfSamplings;
            jtxt_numOfSamplings.setText(numOfSamplings + "");
        }

        jl_warnNumSamplings.setIcon(getWarningIcon());

        if (numOfSamplings < Bootstrap.defaultNumOfSamplings)
            jl_warnNumSamplings.setToolTipText("Lower than the recommended 200! May return unreliable results.");
        else if (numOfSamplings > Bootstrap.maxNumOfSamplings)
            jl_warnNumSamplings.setToolTipText("Larger than the recommended 200. May take too long to compute.");
        else {
            jl_warnNumSamplings.setIcon(null);
            jl_warnNumSamplings.setToolTipText("");
        }
    }

    private void jb_restoreDefaultOptionsActionPerformed() {

    }
    private void jb_SaveOptionsSettingsActionPerformed() {

    }

    /******************
     Actions: jp_Rules
     ******************/
    /****Simple rules****/
    private void jb_chooseRuleNameRuleConfigFileActionPerformed() {
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
//            int size = noFile.length();
            int size = 40;
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_ruleNameRuleConfigFileName.setText(name);
            this.ruleNameRuleConfigFile = new File(selectedFilePath);
        }
        enableButtons();
    }
    private boolean setRuleNameRuleConfigFile(File file) {
        if (file.exists()) {
            String name = file.getName();
            int size = 40;
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_ruleNameRuleConfigFileName.setText(name);
            ruleNameRuleConfigFile = file;
            enableButtons();
            return true;
        }
        return false;
    }
    private void jb_chooseEdgeTypeConfigFileActionPerformed() {
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

        if (selectedFilePath != null) {
            setEdgeTypeRuleNameConfigFile(new File(selectedFilePath));
        }

        enableButtons();
    }
    private boolean setEdgeTypeRuleNameConfigFile(File file) {
        if (file.exists()) {
            String name = file.getName();
            int size = 40;
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_edgeTypeConfigFileName.setText(name);
            this.edgeTypeRuleNameConfigFile = file;
            enableButtons();
            return true;
        }
        return false;
    }


    /****Multipule input and output rules****/
    //Signal split rule
    private void jrb_noSplitRuleActionPerformed() {
        enableButtons();
    }
    private void jrb_equalActionPerformed() {
        enableButtons();
    }
    private void jrb_proportionalActionPerformed() {
        enableButtons();
    }
    private void jrb_suppliedWeightsActionPerformed() {
        setJcbAttributes(jcb_edgeWeights, EpsfcProps.EdgeWeigthsAttribute);
        enableButtons();
    }
    private void jb_refreshWeigthsActionPerformed() {
        setJcbAttributes(jcb_edgeWeights, EpsfcProps.EdgeWeigthsAttribute);
    }

    //Split signal on

    //Multiple signal processing rule
    private void jrb_multiplicationActionPerformed() {
        enableButtons();
    }
    private void jrb_updatedNodeScoresActionPerformed() {
        enableButtons();
    }
    private void jrb_additionActionPerformed() {
        enableButtons();
    }


    //Signal processing order
    private void jrb_noRanksActionPerformed() {
        enableButtons();
    }
    private void jcb_edgeRanksActionPerformed() {
        setJcbAttributes(jcb_edgeRanks, EpsfcProps.EdgeRankAttribute);
    }
    private void jrb_edgeRanksActionPerformed() {
        enableButtons();
        setJcbAttributes(jcb_edgeRanks, EpsfcProps.EdgeRankAttribute);
    }
    private void jb_refreshEdgeRanksActionPerformed() {
        setJcbAttributes(jcb_edgeRanks, EpsfcProps.EdgeRankAttribute);
    }

    /******************
     Actions: jp_Data
     ******************/
    private void jtxt_defaultValueActionPerformed(ActionEvent evt) {
        String text = jtxt_defaultValue.getText();
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            jtxt_defaultValue.setText(Node.getDefaultValue());
        }
    }
    /******************
     Actions: jp_Help
     ******************/

    private void jb_projectWebPageActionPerformed() {
    }
    private void jb_userManualActionPerformed() {
    }

    /******************************
    *********Other methods*********
     ******************************/


    private void setComponentProperties() {
        //Refresh buttons
        jb_refreshNodeDataAttrs.setIcon(getRefreshIcon());
        jb_refreshNetworks.setIcon(getRefreshIcon());
        jb_refreshEdgeTypeAttrs.setIcon(getRefreshIcon());
        jb_refreshEdgeRanks.setIcon(getRefreshIcon());
        jb_refreshWeigths.setIcon(getRefreshIcon());

        //Buttons
        jb_calculateFlow.setBackground(new Color(51, 102, 0));
        jb_calculateFlow.setOpaque(true);
        jb_calculateFlow.setBorderPainted(false);

        //Button groups

        //jbg_dataType
        jbg_dataType = new ButtonGroup();
        jbg_dataType.add(jrb_linear);
        jbg_dataType.add(jrb_log);
        jbg_dataType.add(jrb_FC);
        jbg_dataType.add(jrb_logFC);
        //default selection
        jrb_linear.setSelected(true);

        jbg_bootstrapType = new ButtonGroup();
        jbg_bootstrapType.add(jrb_SampleCentric);
        jbg_bootstrapType.add(jrb_GeneCentric);
        jrb_SampleCentric.setSelected(true);

        //Set selectionFromProperties
        String dataType = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.NodeDataType.getName());
        if (dataType != null) {
            setRadioButton(jbg_dataType, dataType);
        }

        //Temporarily disable data components until further implementation
        jrb_linear.setEnabled(false);
        jrb_log.setEnabled(false);
        jrb_FC.setEnabled(false);
        jrb_logFC.setEnabled(false);

        //jbg_multipleDataOption

        jbg_multipleDataOption = new ButtonGroup();
        jbg_multipleDataOption.add(jrb_max);
        jbg_multipleDataOption.add(jrb_min);
        jbg_multipleDataOption.add(jrb_mean);

        //default selection
        jrb_mean.setSelected(true);

        //Set selectionFromProperties
        String multipleDataOption = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.MultipleDataOption.getName());
        if (multipleDataOption != null) {
            setRadioButton(jbg_multipleDataOption, multipleDataOption);
        }
        //Temporarily disable data components until further implementation
        jrb_max.setEnabled(false);
        jrb_min.setEnabled(false);
        jrb_mean.setEnabled(false);

        //EdgeTypeRuleNameConfigFile
        String fileName = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.EdgeTypeRuleNameConfigFile.getName());
        File file = new File(fileName);
        if (file.exists())
            setEdgeTypeRuleNameConfigFile(file);

        //RuleNameRuleConfigFile
        fileName = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.RuleNameRuleConfigFile.getName());
        file = new File(fileName);
        if (file.exists())
            setRuleNameRuleConfigFile(file);

        //Split signal on button group
        jbg_splitSignalOn = new ButtonGroup();
        jbg_splitSignalOn.add(jrb_incomingEdges);
        jbg_splitSignalOn.add(jrb_outgoingEdges);

        //default selection
        jrb_outgoingEdges.setSelected(true);

        //Set selectionFromProperties
        String propValue = PSFCActivator.getPsfcProps()
                .getProperty(EpsfcProps.SplitSignalOn.getName());
        Enumeration<AbstractButton> buttons = jbg_splitSignalOn.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.getText().equals(propValue))
                button.setSelected(true);
        }

        jbg_signalSplitRule = new ButtonGroup();
        jbg_signalSplitRule.add(jrb_noSplitRule);
        jbg_signalSplitRule.add(jrb_equal);
        jbg_signalSplitRule.add(jrb_proportional);
        jbg_signalSplitRule.add(jrb_suppliedWeights);

        //default selection
        jrb_noSplitRule.setSelected(true);

        //Set selectionFromProperties
        propValue = PSFCActivator.getPsfcProps()
                .getProperty(EpsfcProps.SplitSignalRule.getName());
        buttons = jbg_signalSplitRule.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.getText().equals(propValue))
                button.setSelected(true);
        }

        jbg_multipleSignalProcessingRule = new ButtonGroup();
        jbg_multipleSignalProcessingRule.add(jrb_updatedNodeScores);
        jbg_multipleSignalProcessingRule.add(jrb_addition);
        jbg_multipleSignalProcessingRule.add(jrb_multiplication);

        //default selection
        jrb_updatedNodeScores.setSelected(true);

        //Set selectionFromProperties
        propValue = PSFCActivator.getPsfcProps()
                .getProperty(EpsfcProps.MultipleSignalProcessingRule.getName());
        buttons = jbg_multipleSignalProcessingRule.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.getText().equals(propValue))
                button.setSelected(true);
        }

        //jbg_signalProcessingOrder
        jbg_signalProcessingOrder = new ButtonGroup();
        jbg_signalProcessingOrder.add(jrb_noRanks);
        jbg_signalProcessingOrder.add(jrb_edgeRanks);
        //default selection
        jrb_noRanks.setSelected(true);

        //selection from properties
        propValue = PSFCActivator.getPsfcProps()
                .getProperty(EpsfcProps.SignalProcessingOrder.getName());
        buttons = jbg_signalProcessingOrder.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.getText().equals(propValue))
                button.setSelected(true);
        }

        //JSlider jsl_levels
        jp_flowVisualization.setEnabled(false);
        jsl_levels.setEnabled(false);
        jsl_levels.setMajorTickSpacing(1);
        jsl_levels.setPaintLabels(true);
        jsl_levels.setMinorTickSpacing(1);
        jsl_levels.setPaintTicks(true);
        jtxt_level.setText(jsl_levels.getValue() + "");
        jtxt_level.setEnabled(false);
        jb_showState.setEnabled(false);
        jb_playFlow.setEnabled(false);


        //Help components
        jl_psfc.setIcon(getPsfcIcon());
        jta_about.setText(PSFCActivator.getAboutText());

    }

    private void setToolTips(){
        jl_selectEdgeTypeAttribute.setToolTipText("String attribute where types of edges are specified.");
        jb_checkEdgeTypes.setToolTipText("Check the unique edge types in selected attribute");
        jl_selectNodeDataAttribute.setToolTipText("Numeric attribute where the node values are present");


        jb_sortNetwork.setToolTipText("See how the network will look like after sorting");
        jrb_SampleCentric.setToolTipText("Bootstrap resampling will be performed by randomly redistributing node values among all the nodes in the network.");
        jrb_GeneCentric.setToolTipText("Bootstrap resampling will be performed by randomly assigning each gene a value from a set of values provided with the Gene Matrix File");
        jb_GeneMatrixFile.setToolTipText("Tab delimited file where each row contains gene name and a series of its values (usually from sample-series)");
        jl_warnNumSamplings.setToolTipText("Number of bootstrap resamplings. The recommended minimum value is 200.");

        jl_edgeTypeConfigFile.setToolTipText("File where rule names corresponding to each edge type are provided");
        jl_ruleConfigFile.setToolTipText("File where rules corresponding to each rule name are provided");

        jl_signalSplitRule.setToolTipText("Options for splitting node signals by multiple incoming/outgoing edges");
        jrb_proportional.setToolTipText("Split signal among many nodes is proportional to their relative scores");
        jrb_equal.setToolTipText("Splitting is performed by simple division to the number of nodes");
        jrb_suppliedWeights.setToolTipText("Split signal is proportional to each edge weight");

        jl_signalSplitOn.setToolTipText("Specify if signal splitting is performed based on multiple incoming or outgoing edges");
        jl_multiSignalProcessing.setToolTipText("This rule specifies how a node's signal is changed by multiple input signals");
        jrb_updatedNodeScores.setToolTipText("The node value is changed after receiving a signal from a single edge, then the next edge signal is processed");
        jrb_addition.setToolTipText("Signals from multiple incoming edges are processed based on initial node value, and then added together");
        jrb_multiplication.setToolTipText("Signals from multiple incoming edges are processed based on initial node value, and then multiplied");

        jrb_edgeRanks.setToolTipText("Specifies the order in which multiple edge signals should be processed in case of \"Updated node scores\" rule");
        jcb_edgeRanks.setToolTipText("Integer attribute specifying edge ranks. The higher the edge rank the lower its priority");

    }

    private CyNetwork getSelectedNetwork() {
        CyNetwork selectedNetwork = null;

        if (jcb_network.getSelectedItem() == null)
            return null;
        String networkSelection = jcb_network.getSelectedItem().toString();
        Long suid = null;
        if (networkSelection.isEmpty())
            return null;
        if (networkSelection.contains(suidSplit)) {
            String[] tokens = networkSelection.split(suidSplit);
            try {
                suid = Long.decode(tokens[tokens.length - 1]);
            } catch (NumberFormatException e) {
                String message = "Could not convert SUID " + suid + " to java.lang.Long";
                PSFCActivator.getLogger().error(message);
            }
        } else {
            String message = "Network selection " + networkSelection + "does not contain SUID";
            PSFCActivator.getLogger().error(message);
        }

        for (CyNetwork network : PSFCActivator.networkManager.getNetworkSet())
//                if (network.getRow(network).get(CyNetwork.NAME, String.class).
//                        equals(jcb_network.getSelectedItem().toString()))
            if (network.getSUID().equals(suid))
                selectedNetwork = network;
        return selectedNetwork;
    }

    private void setJcbAttributes(JComboBox comboBox) {
        CyNetwork selectedNetwork = getSelectedNetwork();
        if (selectedNetwork == null)
            comboBox.setModel(new DefaultComboBoxModel());
        else {
            Collection<CyColumn> columns = selectedNetwork.getDefaultEdgeTable().getColumns();
            String[] attributes = new String[columns.size()];
            int i = 0;
            for (CyColumn column : columns) {
                attributes[i++] = column.getName();
            }
            comboBox.setModel(new DefaultComboBoxModel(attributes));
        }
        enableButtons();
    }

    private void setJcbAttributes(JComboBox comboBox, EpsfcProps property) {
        setJcbAttributes(comboBox);
        //Select item from properties, if valid
        String edgeTypeAttr = PSFCActivator.getPsfcProps().getProperty(property.getName());
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (item.toString().equals(edgeTypeAttr))
                comboBox.setSelectedItem(item);
        }
        enableButtons();
    }

    private void setRadioButton(ButtonGroup buttonGroup, String buttonName) {
        Enumeration<AbstractButton> buttonEnumeration = buttonGroup.getElements();
        while (buttonEnumeration.hasMoreElements()) {
            JRadioButton rButton = (JRadioButton) buttonEnumeration.nextElement();
            if (rButton.getText().equals(buttonName)) {
                rButton.setSelected(true);
                break;
            }
        }
    }

    private void enableButtons() {
        jb_calculateFlow.setEnabled(false);
        CyNetwork network = getSelectedNetwork();
        if (network == null) {
            jb_sortNetwork.setEnabled(false);
        } else {
            jb_sortNetwork.setEnabled(true);
            boolean nodeDataColumn = getNodeDataColumn() != null;
            boolean edgeTypeColumn = getEdgeTypeColumn() != null;
            boolean config = (edgeTypeRuleNameConfigFile != null &&
                    edgeTypeRuleNameConfigFile.exists());
            config = (config && ruleNameRuleConfigFile != null && ruleNameRuleConfigFile.exists());
            if (nodeDataColumn && edgeTypeColumn && config)
                jb_calculateFlow.setEnabled(true);
        }
        if (jrb_noSplitRule.isSelected() || jrb_suppliedWeights.isSelected()) {
            jl_signalSplitOn.setEnabled(false);
            jrb_incomingEdges.setEnabled(false);
            jrb_outgoingEdges.setEnabled(false);
        } else {
            jl_signalSplitOn.setEnabled(true);
            jrb_incomingEdges.setEnabled(true);
            jrb_outgoingEdges.setEnabled(true);
        }

        if (jrb_suppliedWeights.isSelected()) {
            jcb_edgeWeights.setEnabled(true);
            jb_refreshWeigths.setEnabled(true);
        } else {
            jcb_edgeWeights.setEnabled(false);
            jb_refreshWeigths.setEnabled(false);
        }

        if (jrb_updatedNodeScores.isSelected()){
            jrb_edgeRanks.setEnabled(true);
        } else {
            jrb_noRanks.setSelected(true);
            jrb_edgeRanks.setEnabled(false);
        }

        if (jrb_edgeRanks.isSelected()) {
            jcb_edgeRanks.setEnabled(true);
            jb_refreshEdgeRanks.setEnabled(true);
        } else {
            jcb_edgeRanks.setEnabled(false);
            jb_refreshEdgeRanks.setEnabled(false);
        }

        if (jrb_SampleCentric.isSelected())
            jb_GeneMatrixFile.setEnabled(false);
        else
            jb_GeneMatrixFile.setEnabled(true);

    }

    private ImageIcon getRefreshIcon() {
        if (refreshIcon == null) {
            ClassLoader cl = PSFCActivator.class.getClassLoader();
            refreshIcon = new ImageIcon(cl.getResource(refreshIconName));
        }
        return refreshIcon;
    }

    private Icon getWarningIcon() {
        if (warningIcon == null) {
            ClassLoader cl = PSFCActivator.class.getClassLoader();
            warningIcon = new ImageIcon(cl.getResource(warningIconName));
        }
        return warningIcon;
    }

    private Properties getNodeDataProperties() {
        Properties properties = new Properties();
        properties.setProperty(ENodeDataProps.NODE_DEFAULT_VALUE.getName(), jtxt_defaultValue.getText());
        return properties;
    }

    private Properties getMultiSignalProperties() {
        Properties properties = new Properties();
        int splitRule;
        if (jrb_noSplitRule.isSelected())
            splitRule = EMultiSignalProps.SPLIT_NONE;
        else if (jrb_equal.isSelected())
            splitRule = EMultiSignalProps.SPLIT_EQUAL;
        else if (jrb_proportional.isSelected())
            splitRule = EMultiSignalProps.SPLIT_PROPORTIONAL;
        else {
            splitRule = EMultiSignalProps.SPLIT_WEIGHTS;
        }
        properties.put(EMultiSignalProps.SplitSignalRule.getName(), splitRule);
        if (splitRule == EMultiSignalProps.SPLIT_WEIGHTS) {
            CyColumn edgeWeightColumn = getEdgeWeightColumn();
            if (edgeWeightColumn == null) {
                JOptionPane.showMessageDialog(this,
                        "Selected EdgeWeight column does not exist. " +
                                "\nPlease, refresh the column list and choose a valid EdgeWeight column for pathway flow calculation.",
                        "PSFC user message", JOptionPane.OK_OPTION
                );
                return null;
            }
            boolean isNumber = true;
            if (!edgeWeightColumn.getType().getName().equals(Double.class.getName()))
                if (!edgeWeightColumn.getType().getName().equals(Integer.class.getName()))
                    isNumber = false;
            if (!isNumber) {
                JOptionPane.showMessageDialog(this,
                        "Illegal EdgeWeight column: should be numeric. " +
                                "\nPlease, choose a valid column for pathway flow calculation.",
                        "PSFC user message", JOptionPane.OK_OPTION
                );
                return null;
            }
            properties.put(EMultiSignalProps.EdgeWeightsAttribute.getName(), edgeWeightColumn);
        }

        int splitOn;
        if (jrb_incomingEdges.isSelected())
            splitOn = EMultiSignalProps.SPLIT_INCOMING;
        else
            splitOn = EMultiSignalProps.SPLIT_OUTGOING;

        properties.put(EMultiSignalProps.SplitSignalOn.getName(), splitOn);

        int multiProcessingRule;
        if (jrb_updatedNodeScores.isSelected())
            multiProcessingRule = EMultiSignalProps.UPDATE_NODE_SCORES;
        else if (jrb_multiplication.isSelected())
            multiProcessingRule = EMultiSignalProps.MULTIPLICATION;
        else
            multiProcessingRule = EMultiSignalProps.ADDITION;
        properties.put(EMultiSignalProps.MultipleSignalProcessingRule.getName(), multiProcessingRule);

        int edgeOrder;
        if (jrb_noRanks.isSelected())
            edgeOrder = EMultiSignalProps.ORDER_NONE;
        else
            edgeOrder = EMultiSignalProps.ORDER_RANKS;
        properties.put(EMultiSignalProps.SignalProcessingOrder.getName(), edgeOrder);

        if (edgeOrder == EMultiSignalProps.ORDER_RANKS) {
            CyColumn edgeRankColumn = getEdgeRankColumn();
            if (edgeRankColumn == null) {
                JOptionPane.showMessageDialog(this,
                        "Selected EdgeRank column does not exist. " +
                                "\nPlease, refresh the column list and choose a valid EdgeRank column for pathway flow calculation.",
                        "PSFC user message", JOptionPane.OK_OPTION
                );
                return null;
            }
            boolean isInteger = true;
            if (!edgeRankColumn.getType().getName().equals(Integer.class.getName()))
                isInteger = false;
            if (!isInteger) {
                JOptionPane.showMessageDialog(this,
                        "Illegal EdgeRank column: should be Integer. " +
                                "\nPlease, choose a valid column for pathway flow calculation.",
                        "PSFC user message", JOptionPane.OK_OPTION
                );
                return null;
            }
            properties.put(EMultiSignalProps.EdgeRankAttribute.getName(), edgeRankColumn);
        }
        return properties;
    }

    public Properties getBootstrapProperties() {
        Properties properties = new Properties();
        properties.setProperty(Bootstrap.NUMOFSAMPLINGSPROP, jtxt_numOfSamplings.getText());
        properties.setProperty(Bootstrap.SAMPLINGTYPEPROP,
                (jrb_SampleCentric.isSelected()? Bootstrap.SAMPLECENTRIC : Bootstrap.GENECENTRIC) + "");
        return properties;
    }

    private boolean checkSorted(CyNetwork network) {
        CyColumn nodeLevelColumn = getNodeLevelColumn();
        if (nodeLevelColumn == null)
            return false;
        try {
            if (nodeLevelColumn.getType().newInstance() instanceof Integer)
                return false;
            for (Object nodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) nodeObj;

                if (network.getDefaultNodeTable().getRow(cyNode.getSUID())
                        .get(nodeLevelColumn.getName(), nodeLevelColumn.getType())
                        == null)
                    return false;
            }
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }

    private CyColumn getNodeDataColumn() {
        return getNodeColumn(jcb_nodeDataAttribute);

    }

    private CyColumn getNodeLevelColumn() {
        try {
            CyNetwork network = getSelectedNetwork();
            return network.getDefaultNodeTable().getColumn(levelAttr);
        } catch (Exception e) {
            return null;
        }
    }

    private CyColumn getNodeColumn(JComboBox comboBox) {
        try {
            CyNetwork network = getSelectedNetwork();
            String attr = comboBox.getSelectedItem().toString();
            return network.getDefaultNodeTable().getColumn(attr);
        } catch (Exception e) {
            return null;
        }
    }

    private CyColumn getEdgeTypeColumn() {
        return getEdgeColumn(jcb_edgeTypeAttribute);
    }

    private CyColumn getEdgeWeightColumn() {
        return getEdgeColumn(jcb_edgeWeights);
    }

    private CyColumn getEdgeRankColumn() {
        return getEdgeColumn(jcb_edgeRanks);
    }

    private CyColumn getEdgeColumn(JComboBox comboBox) {
        try {
            CyNetwork network = getSelectedNetwork();
            String attr = comboBox.getSelectedItem().toString();
            return network.getDefaultEdgeTable().getColumn(attr);
        } catch (NullPointerException e) {
            return null;
        }
    }


}
