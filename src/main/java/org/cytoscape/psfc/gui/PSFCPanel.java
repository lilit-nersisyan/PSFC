package org.cytoscape.psfc.gui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.*;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.logic.algorithms.Bootstrap;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.*;

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
    private File exprMatrixFile;
    private ImageIcon refreshIcon;
    private ImageIcon warningIcon;
    private ImageIcon redFlagIcon;
    private ImageIcon greenFlagIcon;
    private String refreshIconName = "refresh_button.png";
    private String warningIconName = "warning_icon.png";
    private String redFlagIconName = "red_flag.png";
    private String greenFlagIconName = "green_flag.png";
    private HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>> networkLevelNodeSignalMap = new HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>>();
    private HashMap<CyNetwork, HashMap<Integer, HashMap<CyEdge, Double>>> networkLevelEdgeSignalMap = new HashMap<CyNetwork, HashMap<Integer, HashMap<CyEdge, Double>>>();
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
    private javax.swing.JButton jb_rulePresetsGuide;
    private javax.swing.JButton jb_saveSettings;
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
    private javax.swing.JCheckBox jchb_changeNetworkLayout;
    private javax.swing.JCheckBox jchb_ignoreLoops;
    private javax.swing.JCheckBox jchb_iterateUntilConvergence;
    private javax.swing.JCheckBox jchb_precomputeLoops;
    private javax.swing.JLabel jl_algorithms;
    private javax.swing.JLabel jl_chooseNetwork;
    private javax.swing.JLabel jl_convergenceThreshold;
    private javax.swing.JLabel jl_edgeProcesssingSequence;
    private javax.swing.JLabel jl_edgeTypeConfigFile;
    private javax.swing.JLabel jl_edgeTypeConfigFileName;
    private javax.swing.JLabel jl_exprMatrixFile;
    private javax.swing.JLabel jl_flagGeneral;
    private javax.swing.JLabel jl_flagOptions;
    private javax.swing.JLabel jl_flagRules;
    private javax.swing.JLabel jl_flowVisualization;
    private javax.swing.JLabel jl_level;
    private javax.swing.JLabel jl_maxNumOfIterations;
    private javax.swing.JLabel jl_multiInOutRules;
    private javax.swing.JLabel jl_multiSignalProcessing;
    private javax.swing.JLabel jl_network_and_attrs;
    private javax.swing.JLabel jl_numOfSamplings;
    private javax.swing.JLabel jl_percentLabel;
    private javax.swing.JLabel jl_psfc;
    private javax.swing.JLabel jl_ruleConfigFile;
    private javax.swing.JLabel jl_ruleNameRuleConfigFileName;
    private javax.swing.JLabel jl_samplingType;
    private javax.swing.JLabel jl_selectEdgeTypeAttribute;
    private javax.swing.JLabel jl_selectNodeDataAttribute;
    private javax.swing.JLabel jl_selectedNetwork;
    private javax.swing.JLabel jl_signalSplitOn;
    private javax.swing.JLabel jl_signalSplitRule;
    private javax.swing.JLabel jl_significanceCalculation;
    private javax.swing.JLabel jl_simpleRules;
    private javax.swing.JLabel jl_sortingAlgorithm;
    private javax.swing.JLabel jl_warnNumSamplings;
    private javax.swing.JPanel jp_General;
    private javax.swing.JPanel jp_Help;
    private javax.swing.JPanel jp_Loops;
    private javax.swing.JPanel jp_Options;
    private javax.swing.JPanel jp_Rules;
    private javax.swing.JPanel jp_algorithms;
    private javax.swing.JPanel jp_edgeTypeConfigPanel;
    private javax.swing.JPanel jp_flowVisualization;
    private javax.swing.JPanel jp_ignoreLoops;
    private javax.swing.JPanel jp_iterateUntilConvergence;
    private javax.swing.JPanel jp_multiInOutRulesPanel;
    private javax.swing.JPanel jp_network_attrs;
    private javax.swing.JPanel jp_procomputeLoops;
    private javax.swing.JPanel jp_ruleConfigPanel;
    private javax.swing.JPanel jp_significance;
    private javax.swing.JPanel jp_simpleRules;
    private javax.swing.JRadioButton jrb_GeneCentric;
    private javax.swing.JRadioButton jrb_SampleCentric;
    private javax.swing.JRadioButton jrb_addition;
    private javax.swing.JRadioButton jrb_edgeRanks;
    private javax.swing.JRadioButton jrb_equal;
    private javax.swing.JRadioButton jrb_incomingEdges;
    private javax.swing.JRadioButton jrb_multiplication;
    private javax.swing.JRadioButton jrb_noRanks;
    private javax.swing.JRadioButton jrb_noSplitRule;
    private javax.swing.JRadioButton jrb_outgoingEdges;
    private javax.swing.JRadioButton jrb_proportional;
    private javax.swing.JRadioButton jrb_suppliedWeights;
    private javax.swing.JRadioButton jrb_updatedNodeScores;
    private javax.swing.JScrollPane jsl_iterateUntilConvergence;
    private javax.swing.JSlider jsl_levels;
    private javax.swing.JScrollPane jsp_precomputeLoops;
    private javax.swing.JTextArea jta_about;
    private javax.swing.JTextArea jta_iterateUntilConvergence;
    private javax.swing.JTextArea jta_precomputeLoops;
    private javax.swing.JTabbedPane jtp_psfc;
    private javax.swing.JTextField jtxt_convergenceThreshold;
    private javax.swing.JTextField jtxt_level;
    private javax.swing.JTextField jtxt_maxNumOfIterations;
    private javax.swing.JTextField jtxt_numOfSamplings;
    // End of variables declaration

    private ButtonGroup jbg_dataType;
    private ButtonGroup jbg_multipleDataOption;
    private ButtonGroup jbg_splitSignalOn;
    private ButtonGroup jbg_signalSplitRule;
    private ButtonGroup jbg_multipleSignalProcessingRule;
    private ButtonGroup jbg_signalProcessingOrder;
    private ButtonGroup jbg_loopHandling;
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
        jp_Options = new javax.swing.JPanel();
        jp_significance = new javax.swing.JPanel();
        jl_significanceCalculation = new javax.swing.JLabel();
        jchb_CalculateSignificance = new javax.swing.JCheckBox();
        jl_numOfSamplings = new javax.swing.JLabel();
        jtxt_numOfSamplings = new javax.swing.JTextField();
        jl_samplingType = new javax.swing.JLabel();
        jrb_SampleCentric = new javax.swing.JRadioButton();
        jrb_GeneCentric = new javax.swing.JRadioButton();
        jb_GeneMatrixFile = new javax.swing.JButton();
        jl_warnNumSamplings = new javax.swing.JLabel();
        jl_exprMatrixFile = new javax.swing.JLabel();
        jp_algorithms = new javax.swing.JPanel();
        jl_algorithms = new javax.swing.JLabel();
        jl_sortingAlgorithm = new javax.swing.JLabel();
        jcb_sortingAlgorithm = new javax.swing.JComboBox();
        jb_sortNetwork = new javax.swing.JButton();
        jchb_changeNetworkLayout = new javax.swing.JCheckBox();
        jp_Rules = new javax.swing.JPanel();
        jp_simpleRules = new javax.swing.JPanel();
        jl_simpleRules = new javax.swing.JLabel();
        jp_edgeTypeConfigPanel = new javax.swing.JPanel();
        jb_chooseEdgeTypeConfigFile = new javax.swing.JButton();
        jl_edgeTypeConfigFile = new javax.swing.JLabel();
        jl_edgeTypeConfigFileName = new javax.swing.JLabel();
        jp_ruleConfigPanel = new javax.swing.JPanel();
        jl_ruleConfigFile = new javax.swing.JLabel();
        jb_chooseRuleNameRuleConfigFile = new javax.swing.JButton();
        jl_ruleNameRuleConfigFileName = new javax.swing.JLabel();
        jb_rulePresetsGuide = new javax.swing.JButton();
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
        jp_Loops = new javax.swing.JPanel();
        jp_ignoreLoops = new javax.swing.JPanel();
        jchb_ignoreLoops = new javax.swing.JCheckBox();
        jp_iterateUntilConvergence = new javax.swing.JPanel();
        jchb_iterateUntilConvergence = new javax.swing.JCheckBox();
        jl_convergenceThreshold = new javax.swing.JLabel();
        jl_maxNumOfIterations = new javax.swing.JLabel();
        jtxt_maxNumOfIterations = new javax.swing.JTextField();
        jtxt_convergenceThreshold = new javax.swing.JTextField();
        jsl_iterateUntilConvergence = new javax.swing.JScrollPane();
        jta_iterateUntilConvergence = new javax.swing.JTextArea();
        jl_percentLabel = new javax.swing.JLabel();
        jp_procomputeLoops = new javax.swing.JPanel();
        jchb_precomputeLoops = new javax.swing.JCheckBox();
        jsp_precomputeLoops = new javax.swing.JScrollPane();
        jta_precomputeLoops = new javax.swing.JTextArea();
        jp_Help = new javax.swing.JPanel();
        jl_psfc = new javax.swing.JLabel();
        jb_projectWebPage = new javax.swing.JButton();
        jb_userManual = new javax.swing.JButton();
        jta_about = new javax.swing.JTextArea();
        jb_calculateFlow = new javax.swing.JButton();
        jb_openLogFile = new javax.swing.JButton();
        jl_selectedNetwork = new javax.swing.JLabel();
        jl_flagGeneral = new javax.swing.JLabel();
        jl_flagRules = new javax.swing.JLabel();
        jb_saveSettings = new javax.swing.JButton();
        jl_flagOptions = new javax.swing.JLabel();


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
                                .addComponent(jl_network_and_attrs, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(jl_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
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
                                .addContainerGap(29, Short.MAX_VALUE))
        );
        jp_flowVisualizationLayout.setVerticalGroup(
                jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                .addComponent(jl_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_level)
                                        .addComponent(jtxt_level, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_showState)
                                        .addComponent(jsl_levels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_playFlow)
                                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jp_GeneralLayout = new javax.swing.GroupLayout(jp_General);
        jp_General.setLayout(jp_GeneralLayout);
        jp_GeneralLayout.setHorizontalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_network_attrs, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                                        .addComponent(jp_flowVisualization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jp_GeneralLayout.setVerticalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jp_network_attrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                                .addComponent(jp_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42))
        );

        jtp_psfc.addTab("General", jp_General);

        jp_Options.setPreferredSize(new java.awt.Dimension(400, 500));

        jp_significance.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_significanceCalculation.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_significanceCalculation.setForeground(new java.awt.Color(51, 102, 0));
        jl_significanceCalculation.setText("Significance calculation");

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

        jb_GeneMatrixFile.setText("Expr Matrix");

        jl_warnNumSamplings.setText(" ");

        jl_exprMatrixFile.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jl_exprMatrixFile.setText("       ");

        javax.swing.GroupLayout jp_significanceLayout = new javax.swing.GroupLayout(jp_significance);
        jp_significance.setLayout(jp_significanceLayout);
        jp_significanceLayout.setHorizontalGroup(
                jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addComponent(jl_significanceCalculation)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jrb_GeneCentric, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jl_numOfSamplings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                                                .addComponent(jtxt_numOfSamplings, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jl_warnNumSamplings))
                                                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                                                .addComponent(jb_GeneMatrixFile)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jl_exprMatrixFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                        .addComponent(jchb_CalculateSignificance)
                                        .addComponent(jl_samplingType)
                                        .addComponent(jrb_SampleCentric))
                                .addContainerGap())
        );
        jp_significanceLayout.setVerticalGroup(
                jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_significanceLayout.createSequentialGroup()
                                .addComponent(jl_significanceCalculation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchb_CalculateSignificance)
                                .addGap(13, 13, 13)
                                .addComponent(jl_samplingType)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrb_SampleCentric, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_GeneCentric)
                                        .addComponent(jb_GeneMatrixFile)
                                        .addComponent(jl_exprMatrixFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_significanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_numOfSamplings)
                                        .addComponent(jtxt_numOfSamplings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_warnNumSamplings))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jp_algorithms.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_algorithms.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_algorithms.setForeground(new java.awt.Color(51, 102, 0));
        jl_algorithms.setText("Algorithms");

        jl_sortingAlgorithm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_sortingAlgorithm.setForeground(new java.awt.Color(102, 102, 102));
        jl_sortingAlgorithm.setText("Sorting algorithm");

        jcb_sortingAlgorithm.setPreferredSize(new java.awt.Dimension(201, 20));

        jb_sortNetwork.setText("Sort");
        jb_sortNetwork.setPreferredSize(new java.awt.Dimension(59, 20));

        jchb_changeNetworkLayout.setText("Change network layout after sorting");

        javax.swing.GroupLayout jp_algorithmsLayout = new javax.swing.GroupLayout(jp_algorithms);
        jp_algorithms.setLayout(jp_algorithmsLayout);
        jp_algorithmsLayout.setHorizontalGroup(
                jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_algorithms)
                                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_sortingAlgorithm)
                                                        .addComponent(jchb_changeNetworkLayout)
                                                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                                                .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(0, 33, Short.MAX_VALUE))
        );
        jp_algorithmsLayout.setVerticalGroup(
                jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_algorithmsLayout.createSequentialGroup()
                                .addComponent(jl_algorithms)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jl_sortingAlgorithm)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_algorithmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcb_sortingAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_sortNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchb_changeNetworkLayout)
                                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jp_OptionsLayout = new javax.swing.GroupLayout(jp_Options);
        jp_Options.setLayout(jp_OptionsLayout);
        jp_OptionsLayout.setHorizontalGroup(
                jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jp_algorithms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_significance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(18, Short.MAX_VALUE))
        );
        jp_OptionsLayout.setVerticalGroup(
                jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_algorithms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jp_significance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(100, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Options", jp_Options);

        jp_simpleRules.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jp_simpleRules.setMaximumSize(new java.awt.Dimension(322, 94));

        jl_simpleRules.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_simpleRules.setForeground(new java.awt.Color(51, 102, 0));
        jl_simpleRules.setText("Simple rules");

        jp_edgeTypeConfigPanel.setPreferredSize(new java.awt.Dimension(145, 69));

        jb_chooseEdgeTypeConfigFile.setText("Choose file");

        jl_edgeTypeConfigFile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_edgeTypeConfigFile.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeTypeConfigFile.setText("EdgeTypes config file");

        jl_edgeTypeConfigFileName.setText("n/a");
        jl_edgeTypeConfigFileName.setMaximumSize(new java.awt.Dimension(135, 14));

        javax.swing.GroupLayout jp_edgeTypeConfigPanelLayout = new javax.swing.GroupLayout(jp_edgeTypeConfigPanel);
        jp_edgeTypeConfigPanel.setLayout(jp_edgeTypeConfigPanelLayout);
        jp_edgeTypeConfigPanelLayout.setHorizontalGroup(
                jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                        .addComponent(jl_edgeTypeConfigFile)
                                        .addComponent(jl_edgeTypeConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24))
        );
        jp_edgeTypeConfigPanelLayout.setVerticalGroup(
                jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                .addComponent(jl_edgeTypeConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_chooseEdgeTypeConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jl_edgeTypeConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 6, Short.MAX_VALUE))
        );

        jp_ruleConfigPanel.setAlignmentX(0.0F);
        jp_ruleConfigPanel.setAlignmentY(0.0F);
        jp_ruleConfigPanel.setPreferredSize(new java.awt.Dimension(145, 69));
        jp_ruleConfigPanel.setRequestFocusEnabled(false);

        jl_ruleConfigFile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_ruleConfigFile.setForeground(new java.awt.Color(102, 102, 102));
        jl_ruleConfigFile.setText("Rule config file");

        jb_chooseRuleNameRuleConfigFile.setText("Choose file");

        jl_ruleNameRuleConfigFileName.setText("n/a");
        jl_ruleNameRuleConfigFileName.setMaximumSize(new java.awt.Dimension(135, 14));

        javax.swing.GroupLayout jp_ruleConfigPanelLayout = new javax.swing.GroupLayout(jp_ruleConfigPanel);
        jp_ruleConfigPanel.setLayout(jp_ruleConfigPanelLayout);
        jp_ruleConfigPanelLayout.setHorizontalGroup(
                jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_ruleConfigFile)
                                        .addComponent(jb_chooseRuleNameRuleConfigFile)
                                        .addComponent(jl_ruleNameRuleConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        jp_ruleConfigPanelLayout.setVerticalGroup(
                jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                .addComponent(jl_ruleConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_chooseRuleNameRuleConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jl_ruleNameRuleConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(325, 325, 325))
        );

        javax.swing.GroupLayout jp_simpleRulesLayout = new javax.swing.GroupLayout(jp_simpleRules);
        jp_simpleRules.setLayout(jp_simpleRulesLayout);
        jp_simpleRulesLayout.setHorizontalGroup(
                jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jl_simpleRules))
                                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                                .addComponent(jp_edgeTypeConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jp_ruleConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_simpleRulesLayout.setVerticalGroup(
                jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_simpleRulesLayout.createSequentialGroup()
                                .addComponent(jl_simpleRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_ruleConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jp_edgeTypeConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jb_rulePresetsGuide.setBackground(new java.awt.Color(255, 255, 255));
        jb_rulePresetsGuide.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jb_rulePresetsGuide.setForeground(new java.awt.Color(51, 102, 0));
        jb_rulePresetsGuide.setText("Open Rule Presets Guide");
        jb_rulePresetsGuide.setAlignmentY(0.0F);

        jp_multiInOutRulesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jp_multiInOutRulesPanel.setMaximumSize(new java.awt.Dimension(322, 277));

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
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_noRanks)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jrb_edgeRanks)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jcb_edgeRanks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshEdgeRanks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                                .addComponent(jrb_outgoingEdges))
                                        .addComponent(jl_multiInOutRules)
                                        .addGroup(jp_multiInOutRulesPanelLayout.createSequentialGroup()
                                                .addComponent(jrb_suppliedWeights)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcb_edgeWeights, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshWeigths, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jp_RulesLayout = new javax.swing.GroupLayout(jp_Rules);
        jp_Rules.setLayout(jp_RulesLayout);
        jp_RulesLayout.setHorizontalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                                .addComponent(jb_rulePresetsGuide)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jp_simpleRules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jp_RulesLayout.setVerticalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jb_rulePresetsGuide)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_simpleRules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jtp_psfc.addTab("Rules", jp_Rules);

        jp_ignoreLoops.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jchb_ignoreLoops.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jchb_ignoreLoops.setText("Ignore feedback loops in the pathway");

        javax.swing.GroupLayout jp_ignoreLoopsLayout = new javax.swing.GroupLayout(jp_ignoreLoops);
        jp_ignoreLoops.setLayout(jp_ignoreLoopsLayout);
        jp_ignoreLoopsLayout.setHorizontalGroup(
                jp_ignoreLoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ignoreLoopsLayout.createSequentialGroup()
                                .addComponent(jchb_ignoreLoops)
                                .addGap(0, 78, Short.MAX_VALUE))
        );
        jp_ignoreLoopsLayout.setVerticalGroup(
                jp_ignoreLoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ignoreLoopsLayout.createSequentialGroup()
                                .addComponent(jchb_ignoreLoops)
                                .addGap(0, 4, Short.MAX_VALUE))
        );

        jp_iterateUntilConvergence.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jchb_iterateUntilConvergence.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jchb_iterateUntilConvergence.setText("Iterate until convergence");

        jl_convergenceThreshold.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_convergenceThreshold.setForeground(new java.awt.Color(102, 102, 102));
        jl_convergenceThreshold.setText("Convergence threshold");

        jl_maxNumOfIterations.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_maxNumOfIterations.setForeground(new java.awt.Color(102, 102, 102));
        jl_maxNumOfIterations.setText("Max number of iterations");

        jtxt_maxNumOfIterations.setText("10");

        jtxt_convergenceThreshold.setText("100");

        jsl_iterateUntilConvergence.setBackground(new java.awt.Color(204, 255, 204));
        jsl_iterateUntilConvergence.setBorder(null);

        jta_iterateUntilConvergence.setBackground(new java.awt.Color(240, 240, 240));
        jta_iterateUntilConvergence.setColumns(20);
        jta_iterateUntilConvergence.setEditable(false);
        jta_iterateUntilConvergence.setFont(new java.awt.Font("Monospaced", 2, 11)); // NOI18N
        jta_iterateUntilConvergence.setForeground(new java.awt.Color(51, 102, 0));
        jta_iterateUntilConvergence.setLineWrap(true);
        jta_iterateUntilConvergence.setRows(5);
        jta_iterateUntilConvergence.setText("The algorithm will iterate until reaching the convergence threshold at each node, or iterating the max number of iterations. See the user manual for details.");
        jta_iterateUntilConvergence.setWrapStyleWord(true);
        jta_iterateUntilConvergence.setBorder(null);
        jta_iterateUntilConvergence.setFocusable(false);
        jta_iterateUntilConvergence.setHighlighter(null);
        jsl_iterateUntilConvergence.setViewportView(jta_iterateUntilConvergence);

        jl_percentLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_percentLabel.setForeground(new java.awt.Color(102, 102, 102));
        jl_percentLabel.setText("%");

        javax.swing.GroupLayout jp_iterateUntilConvergenceLayout = new javax.swing.GroupLayout(jp_iterateUntilConvergence);
        jp_iterateUntilConvergence.setLayout(jp_iterateUntilConvergenceLayout);
        jp_iterateUntilConvergenceLayout.setHorizontalGroup(
                jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                .addComponent(jchb_iterateUntilConvergence)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                .addGroup(jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jsl_iterateUntilConvergence, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
                                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addGroup(jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_convergenceThreshold)
                                                        .addComponent(jl_maxNumOfIterations))
                                                .addGroup(jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jtxt_maxNumOfIterations, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                                                .addGap(19, 19, 19)
                                                                .addComponent(jtxt_convergenceThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jl_percentLabel)))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jp_iterateUntilConvergenceLayout.setVerticalGroup(
                jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_iterateUntilConvergenceLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jchb_iterateUntilConvergence)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_convergenceThreshold)
                                        .addComponent(jtxt_convergenceThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_percentLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_iterateUntilConvergenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jtxt_maxNumOfIterations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_maxNumOfIterations))
                                .addGap(18, 18, 18)
                                .addComponent(jsl_iterateUntilConvergence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jp_procomputeLoops.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jchb_precomputeLoops.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jchb_precomputeLoops.setText("Precompute signals at loops");

        jsp_precomputeLoops.setBackground(new java.awt.Color(204, 255, 204));
        jsp_precomputeLoops.setBorder(null);

        jta_precomputeLoops.setBackground(new java.awt.Color(240, 240, 240));
        jta_precomputeLoops.setColumns(20);
        jta_precomputeLoops.setEditable(false);
        jta_precomputeLoops.setFont(new java.awt.Font("Monospaced", 2, 11)); // NOI18N
        jta_precomputeLoops.setForeground(new java.awt.Color(51, 102, 0));
        jta_precomputeLoops.setLineWrap(true);
        jta_precomputeLoops.setRows(5);
        jta_precomputeLoops.setText("The signal at \"Target\" nodes at feedback loops is precomputed and the rest of the algorithm proceeds as there were no loops in the pathway. See the manual for details.");
        jta_precomputeLoops.setWrapStyleWord(true);
        jta_precomputeLoops.setBorder(null);
        jsp_precomputeLoops.setViewportView(jta_precomputeLoops);
        jta_precomputeLoops.getAccessibleContext().setAccessibleParent(jta_precomputeLoops);

        javax.swing.GroupLayout jp_procomputeLoopsLayout = new javax.swing.GroupLayout(jp_procomputeLoops);
        jp_procomputeLoops.setLayout(jp_procomputeLoopsLayout);
        jp_procomputeLoopsLayout.setHorizontalGroup(
                jp_procomputeLoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_procomputeLoopsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jsp_precomputeLoops)
                                .addContainerGap())
                        .addGroup(jp_procomputeLoopsLayout.createSequentialGroup()
                                .addComponent(jchb_precomputeLoops)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jp_procomputeLoopsLayout.setVerticalGroup(
                jp_procomputeLoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_procomputeLoopsLayout.createSequentialGroup()
                                .addComponent(jchb_precomputeLoops)
                                .addGap(14, 14, 14)
                                .addComponent(jsp_precomputeLoops, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jp_LoopsLayout = new javax.swing.GroupLayout(jp_Loops);
        jp_Loops.setLayout(jp_LoopsLayout);
        jp_LoopsLayout.setHorizontalGroup(
                jp_LoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_LoopsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_LoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_LoopsLayout.createSequentialGroup()
                                                .addComponent(jp_ignoreLoops, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(11, 11, 11))
                                        .addGroup(jp_LoopsLayout.createSequentialGroup()
                                                .addComponent(jp_iterateUntilConvergence, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_LoopsLayout.createSequentialGroup()
                                                .addComponent(jp_procomputeLoops, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())))
        );
        jp_LoopsLayout.setVerticalGroup(
                jp_LoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_LoopsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_ignoreLoops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jp_iterateUntilConvergence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jp_procomputeLoops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Loops", jp_Loops);

        jl_psfc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jb_projectWebPage.setText("Go to project web page");

        jb_userManual.setText("Open User Manual");

        jta_about.setBackground(new java.awt.Color(240, 240, 240));
        jta_about.setColumns(20);
        jta_about.setEditable(false);
        jta_about.setFont(new java.awt.Font("Monospaced", 2, 11)); // NOI18N
        jta_about.setRows(5);
        jta_about.setText("PSFC version 1.0.0\nCytoscape app for calculation of pathway\nsignal flow based on gene expression data \nand pathway topology.\n\nCopyright(C) 2015\nLilit Nersisyan, IMB NAS RA\nArsen Arakelyan, IMB NAS RA\nGraham Johnson, UCSF\nMegan Riel-Mehan, UCSF\nAlexander Pico, UCSF\n\nDistributed under\nGNU General Public License version 3");
        jta_about.setAlignmentX(10.0F);
        jta_about.setBorder(null);
        jta_about.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jta_about.setOpaque(false);

        javax.swing.GroupLayout jp_HelpLayout = new javax.swing.GroupLayout(jp_Help);
        jp_Help.setLayout(jp_HelpLayout);
        jp_HelpLayout.setHorizontalGroup(
                jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jta_about))
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                                .addGap(28, 28, 28)
                                                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                                .addGap(69, 69, 69)
                                                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(jb_userManual, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(jb_projectWebPage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGap(0, 19, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jp_HelpLayout.setVerticalGroup(
                jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jta_about, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jb_projectWebPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_userManual)
                                .addContainerGap())
        );

        jtp_psfc.addTab("Help", jp_Help);

        jb_calculateFlow.setBackground(new java.awt.Color(51, 102, 0));
        jb_calculateFlow.setText("Calculate flow");
        jb_calculateFlow.setBorderPainted(false);

        jb_openLogFile.setText("PSFC log");

        jl_selectedNetwork.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jl_selectedNetwork.setForeground(new java.awt.Color(153, 0, 0));
        jl_selectedNetwork.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jl_selectedNetwork.setText("No network chosen ");

        jb_saveSettings.setText("Save settings");

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
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jb_openLogFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jb_saveSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_calculateFlow, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jl_selectedNetwork, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(23, 23, 23))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jl_flagGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jl_flagOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(jl_flagRules, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jl_flagGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE)
                                        .addComponent(jl_flagRules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jl_flagOptions, javax.swing.GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jl_selectedNetwork)
                                                .addGap(10, 10, 10)
                                                .addComponent(jb_calculateFlow))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jb_openLogFile)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_saveSettings)))
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
        /*// Actions: jp_Data
        addActionListeners_jp_Data();*/
        // Actions: jp_Loops
        addActionListeners_jp_Loops();
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
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                    if (-1 < index) {
                        list.setToolTipText(jcb_network.getItemAt(index).toString());
                    }
                } else {
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
        jchb_changeNetworkLayout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jchb_changeNetworkLayoutActionPerformed();
            }
        });

        //Save settings
        jb_saveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_saveSettingsActionPerformed();
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
        jchb_CalculateSignificance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jchb_CalculateSignificanceActionPerformed();
            }
        });
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

    }

    private void addActionListeners_jp_Rules() {
        jb_rulePresetsGuide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_rulePresetsGuideActionPerformed(e);
            }
        });
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

    private void addActionListeners_jp_Loops() {
        jchb_ignoreLoops.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableButtons();
            }
        });
        jchb_precomputeLoops.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableButtons();
            }
        });
        jchb_iterateUntilConvergence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableButtons();
            }
        });
        jtxt_convergenceThreshold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_convergenceThresholdActionPerformed();
            }
        });
        jtxt_maxNumOfIterations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_maxNumOfIterationsActionPerformed();
            }
        });
    }


    private void addActionListeners_jp_Help() {
        jb_projectWebPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_projectWebPageActionPerformed(e);
            }
        });

        jb_userManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_userManualActionPerformed(e);
            }
        });
    }


    /**
     * ***************
     * Actions: shared buttons
     * ****************
     */
    private void jb_openLogFileActionPerformed() {
        final File logFile = PSFCActivator.getPsfcLogFile();
        if (logFile == null || !logFile.exists())
            JOptionPane.showMessageDialog(this, "PSFC uesr message",
                    "Problem loading log file.", JOptionPane.OK_OPTION);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        String cmd = "rundll32 url.dll,FileProtocolHandler "
                                + logFile.getCanonicalPath();
                        Runtime.getRuntime().exec(cmd);
                    } else {
                        Desktop.getDesktop().edit(logFile);
                    }
                } catch (IllegalArgumentException iae) {
                    System.out.println("PSFC:: Log file Not Found");
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
            sortNetworkAction = new SortNetworkAction(network, getSortingAlgorithm(), jchb_changeNetworkLayout.isSelected());
            sortNetworkAction.actionPerformed(e);
            while (!sortNetworkAction.isPerformed()) {
                try {
                    Thread.sleep(50);
//                    System.out.println("Pathway flow calculation waiting for network sorting");
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
        Properties loopHandlingProps = getLoopHandlingProperties();
        if (multiSignalProps == null)
            return;

        calculateFlowAction = new CalculateScoreFlowAction(
                network, edgeTypeColumn, nodeDataColumn, nodeLevelColumn,
                edgeTypeRuleNameConfigFile, ruleNameRuleConfigFile, nodeDataProperties,
                multiSignalProps, loopHandlingProps, jchb_CalculateSignificance.isSelected(), this);
        if (jchb_CalculateSignificance.isSelected()) {
            calculateFlowAction.setBootstrapProps(getBootstrapProperties());
        }
        calculateFlowAction.actionPerformed(e);
    }


    /**
     * ***************
     * Actions: jp_General
     * ****************
     */
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
                if (item.toString().contains(currentNetwork.getRow(currentNetwork).get("Name", String.class))) {
                    jcb_network.setSelectedItem(item);
                    jcb_networkActionPerformed();
                }
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

        CyNetwork currentNetwork = PSFCActivator.cyApplicationManager.getCurrentNetwork();
        Long selectedNetworkSUID = getSelectedNetwork().getSUID();
        Long currentNetworkSUID = currentNetwork.getSUID();

        CyNetwork selectedNetwork = getSelectedNetwork();
        jl_selectedNetwork.setText(selectedNetwork.getRow(selectedNetwork).get("Name", String.class) + suidSplit + selectedNetwork.getSUID());
        if (selectedNetworkSUID.equals(currentNetworkSUID)) {
            jl_selectedNetwork.setForeground(new java.awt.Color(51, 102, 0));
            jl_selectedNetwork.setToolTipText("");
        } else {
            jl_selectedNetwork.setForeground(new java.awt.Color(153, 0, 0));
            jl_selectedNetwork.setToolTipText("Selected network is not currently visualized, or refresh network selection");
        }
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
        double minEdgeSignal = Double.MAX_VALUE;
        double maxSignal = Double.MIN_VALUE;
        double maxEdgeSignal = Double.MIN_VALUE;
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++) {
            HashMap<CyNode, Double> nodeSignalMap = networkLevelNodeSignalMap.get(network).get(level);
            HashMap<CyEdge, Double> edgeSignalMap = networkLevelEdgeSignalMap.get(network).get(level);
            if (nodeSignalMap != null)
                for (CyNode cyNode : nodeSignalMap.keySet()) {
                    double signal = nodeSignalMap.get(cyNode);
                    if (signal < minSignal)
                        minSignal = signal;
                    else if (signal > maxSignal)
                        maxSignal = signal;
                }
            if (edgeSignalMap != null)
                for (CyEdge cyEdge : edgeSignalMap.keySet()) {
                    double signal = edgeSignalMap.get(cyEdge);
                    if (signal < minEdgeSignal)
                        minEdgeSignal = signal;
                    else if (signal > maxEdgeSignal)
                        maxEdgeSignal = signal;
                }
        }

        return new VisualizeFlowAction(network, minSignal, maxSignal, levels, minEdgeSignal, maxEdgeSignal, this);
    }

    private void jb_playFlowActionPerformed() {
        ArrayList<Integer> levels = new ArrayList<Integer>();
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++)
            levels.add(level);
        VisualizeFlowAction visualizeFlowAction = createVisualizeFlowAction(levels);
        visualizeFlowAction.actionPerformed(null);
    }

    private void jchb_changeNetworkLayoutActionPerformed() {

    }

    public void setVisualizationComponents(CyNetwork network,
                                           HashMap<Integer, HashMap<CyNode, Double>> levelNodeSignalMap,
                                           HashMap<Integer, HashMap<CyEdge, Double>> levelCyEdgeScoreMap) {
        networkLevelNodeSignalMap.put(network, levelNodeSignalMap);
        networkLevelEdgeSignalMap.put(network, levelCyEdgeScoreMap);
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

    private void jb_saveSettingsActionPerformed() {
        Properties psfcProps = PSFCActivator.getPsfcProps();

        try {
            /*** General setting ***/

            if (jcb_edgeTypeAttribute.getSelectedItem() != null)
                psfcProps.setProperty(EpsfcProps.EdgeTypeAttribute.getName(), jcb_edgeTypeAttribute.getSelectedItem().toString());
            if (jcb_nodeDataAttribute.getSelectedItem() != null)
                psfcProps.setProperty(EpsfcProps.NodeDataAttribute.getName(), jcb_nodeDataAttribute.getSelectedItem().toString());

            /*** End of General settings ***/

            /*** Options ***/

            psfcProps.setProperty(EpsfcProps.SortingAlgorithm.getName(),
                    jcb_sortingAlgorithm.getSelectedItem().toString());
            psfcProps.setProperty(EpsfcProps.ChangeNetworkLayout.getName(),
                    jchb_changeNetworkLayout.isSelected() ? "true" : "false");

            psfcProps.setProperty(EpsfcProps.CalculateSignificance.getName(),
                    jchb_CalculateSignificance.isSelected() ? "true" : "false");
            psfcProps.setProperty(EpsfcProps.BootstrapMode.getName(),
                    jrb_SampleCentric.isSelected() ? jrb_SampleCentric.getText() : jrb_GeneCentric.getText());
            if (exprMatrixFile != null && exprMatrixFile.exists())
                psfcProps.setProperty(EpsfcProps.BootstrapExpMatrix.getName(), exprMatrixFile.getAbsolutePath());
            psfcProps.setProperty(EpsfcProps.NumOfSamplings.getName(), jtxt_numOfSamplings.getText());

            /*** End of Options ***/

            /*** Rules ***/
            if (edgeTypeRuleNameConfigFile != null)
                psfcProps.setProperty(EpsfcProps.EdgeTypeRuleNameConfigFile.getName(), edgeTypeRuleNameConfigFile.getAbsolutePath());
            if (ruleNameRuleConfigFile != null)
                psfcProps.setProperty(EpsfcProps.RuleNameRuleConfigFile.getName(), ruleNameRuleConfigFile.getAbsolutePath());

            Enumeration<AbstractButton> buttons;
            buttons = jbg_signalSplitRule.getElements();
            while (buttons.hasMoreElements()) {
                JRadioButton button = (JRadioButton) buttons.nextElement();
                if (button.isSelected())
                    psfcProps.setProperty(EpsfcProps.SplitSignalRule.getName(), button.getText());
            }
            if (jcb_edgeWeights.getSelectedItem() != null)
                psfcProps.setProperty(EpsfcProps.EdgeWeigthsAttribute.getName(), jcb_edgeWeights.getSelectedItem().toString());
            buttons = jbg_splitSignalOn.getElements();
            while (buttons.hasMoreElements()) {
                JRadioButton button = (JRadioButton) buttons.nextElement();
                if (button.isSelected())
                    psfcProps.setProperty(EpsfcProps.SplitSignalOn.getName(), button.getText());
            }
            buttons = jbg_multipleSignalProcessingRule.getElements();
            while (buttons.hasMoreElements()) {
                JRadioButton button = (JRadioButton) buttons.nextElement();
                if (button.isSelected())
                    psfcProps.setProperty(EpsfcProps.MultipleSignalProcessingRule.getName(), button.getText());
            }
            buttons = jbg_signalProcessingOrder.getElements();
            while (buttons.hasMoreElements()) {
                JRadioButton button = (JRadioButton) buttons.nextElement();
                if (button.isSelected())
                    psfcProps.setProperty(EpsfcProps.SignalProcessingOrder.getName(), button.getText());
            }
            if (jcb_edgeRanks.getSelectedItem() != null)
                psfcProps.setProperty(EpsfcProps.EdgeRankAttribute.getName(), jcb_edgeRanks.getSelectedItem().toString());

            /*** End of Rules ***/

            /*** Loops ***/

            buttons = jbg_loopHandling.getElements();
            while (buttons.hasMoreElements()) {
                JCheckBox button = (JCheckBox) buttons.nextElement();
                if (button.isSelected())
                    psfcProps.setProperty(EpsfcProps.LoopHandling.getName(), button.getText());
            }

            psfcProps.setProperty(EpsfcProps.ConvergenceThreshold.getName(), jtxt_convergenceThreshold.getText());
            psfcProps.setProperty(EpsfcProps.MaxNumOfIterations.getName(), jtxt_maxNumOfIterations.getText());

            /*** End of Loops ***/

        } catch (Exception e) {
            String message = "Couldn't save the settings. Error: "
                    + e.getMessage() + " Cause: " + e.getCause();
            PSFCActivator.getLogger().warn(message);
            System.out.println("PSFC:: " + message);
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

    /**
     * ***************
     * Actions: jp_Options
     * ****************
     */
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
            SortNetworkAction sortNetworkAction = new SortNetworkAction(selectedNetwork, getSortingAlgorithm(),jchb_changeNetworkLayout.isSelected());
            sortNetworkAction.actionPerformed(e);
        }
    }

    private int getSortingAlgorithm() {
        return ESortingAlgorithms.getNum(jcb_sortingAlgorithm.getSelectedItem().toString());
    }

    //Significance calculation
    private void jchb_CalculateSignificanceActionPerformed() {
        enableButtons();
    }

    private void jrb_SampleCentricActionPerformed() {
        enableButtons();
    }

    private void jrb_GeneCentricActionPerformed() {
        enableButtons();
    }

    private void jb_GeneMatrixFileActionPerformed() {
        JFrame fileLoadFrame = new JFrame("Expression matrix for Bootstrap calculations");
        fileLoadFrame.setLocation(400, 250);
        fileLoadFrame.setSize(400, 200);
        JFileChooser fileChooser = new JFileChooser();
        File recentDirectory = PSFCActivator.getRecentDirectory();
        fileChooser.setCurrentDirectory(recentDirectory);


        fileChooser.setDialogTitle("Select expression matrix file");
        fileChooser.showOpenDialog(fileLoadFrame);
        String selectedFilePath = null;

        if (fileChooser.getSelectedFile() != null) {
            selectedFilePath = fileChooser.getSelectedFile().getPath();
            PSFCActivator.writeRecentDirectory(selectedFilePath);

        }

        if (selectedFilePath != null) {
            setExprMatrixFile(new File(selectedFilePath));
        }

        enableButtons();
    }

    private void jtxt_numOfSamplingsActionPerformed() {
        int numOfSamplings;
        try {
            numOfSamplings = Integer.parseInt(jtxt_numOfSamplings.getText());
            if (numOfSamplings < 1)
                numOfSamplings = Bootstrap.defaultNumOfSamplings;
        } catch (NumberFormatException e) {
            numOfSamplings = Bootstrap.defaultNumOfSamplings;
        }
        jtxt_numOfSamplings.setText(numOfSamplings + "");

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


    /******************
     Actions: jp_Rules
     ******************/
    private void jb_rulePresetsGuideActionPerformed(ActionEvent e) {
        (new OpenFileAction(PSFCActivator.getRulePresetsFileName())).actionPerformed(e);
    }


    /**
     * *Simple rules***
     */
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
            setRuleNameRuleConfigFile(new File(selectedFilePath));
//            name = fileChooser.getSelectedFile().getName();
//            int size = noFile.length();
//            int size = 15;
//            if (name.length() > size)
//                name = name.substring(0, size) + "...";
//            jl_ruleNameRuleConfigFileName.setText(name);
//            this.ruleNameRuleConfigFile = new File(selectedFilePath);
        }
        enableButtons();
    }

    private boolean setRuleNameRuleConfigFile(File file) {
        if (file.exists()) {
            String name = file.getName();
//            int size = jl_ruleNameRuleConfigFileName.getMaximumSize().width-2;
            int size = 15;
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_ruleNameRuleConfigFileName.setText(name);
            jl_ruleNameRuleConfigFileName.setToolTipText(file.getAbsolutePath());
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
//            int size = jl_edgeTypeConfigFileName.getMaximumSize().width-2;
            int size = 15;
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_edgeTypeConfigFileName.setText(name);
            jl_edgeTypeConfigFileName.setToolTipText(file.getAbsolutePath());
            this.edgeTypeRuleNameConfigFile = file;
            enableButtons();
            return true;
        }
        return false;
    }

    private boolean setExprMatrixFile(File file) {
        if (file.exists()) {
            String name = file.getName();
            int size = jl_exprMatrixFile.getSize().width;
            if (size > 4 && name.length() > size)
                name = name.substring(0, size - 4) + "...";
            jl_exprMatrixFile.setText(name);
            jl_exprMatrixFile.setToolTipText(file.getAbsolutePath());
            this.exprMatrixFile = file;
            enableButtons();
            return true;
        }
        return false;
    }


    /**
     * *Multipule input and output rules***
     */
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

    /**
     * ***************
     * Actions: jp_Data
     * ****************
     */
   /* private void jtxt_defaultValueActionPerformed(ActionEvent evt) {
        String text = jtxt_defaultValue.getText();
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            jtxt_defaultValue.setText(Node.getDefaultValue());
        }
    }
*/

    /**
     * ****************
     * Actions: jp_Loops
     * ****************
     */


    private void jtxt_convergenceThresholdActionPerformed() {
        int convThreshold;
        try {
            convThreshold = Integer.parseInt(jtxt_convergenceThreshold.getText());
            if (convThreshold < 0 || convThreshold > 100)
                convThreshold = ELoopHandlingProps.CONVERGENCE_THRESHOLD_DEFAULT;
        } catch (NumberFormatException e) {
            convThreshold = ELoopHandlingProps.CONVERGENCE_THRESHOLD_DEFAULT;
        }

        jtxt_convergenceThreshold.setText(convThreshold + "");
    }

    private void jtxt_maxNumOfIterationsActionPerformed() {
        int maxNumIt;
        try {
            maxNumIt = Integer.parseInt(jtxt_maxNumOfIterations.getText());
            if (maxNumIt < 1)
                maxNumIt = ELoopHandlingProps.MAX_NUM_OF_ITERATION_DEFAULT;
        } catch (NumberFormatException e) {
            maxNumIt = ELoopHandlingProps.MAX_NUM_OF_ITERATION_DEFAULT;
        }

        jtxt_maxNumOfIterations.setText(maxNumIt + "");
    }

    /**
     * ***************
     * Actions: jp_Help
     * ****************
     */

    private void jb_projectWebPageActionPerformed(ActionEvent e) {
        new WebLoadAction(PSFCActivator.getProjectWebpageUrl()).actionPerformed(e);
    }

    private void jb_userManualActionPerformed(ActionEvent e) {
        String[] buttons = new String[]{"As local PDF", "In Web browser"};
        int rc = JOptionPane.showOptionDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
                "Open PSFC User Manual:", "Open PSFC User Manual",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                buttons,buttons[1]);
        if(rc == 0)
            new OpenFileAction(PSFCActivator.getUserManualFileName()).actionPerformed(e);
        else
            new WebLoadAction(PSFCActivator.getUserManualURL()).actionPerformed(e);

    }

    /**
     * ***************************
     * ********Other methods*********
     * ****************************
     */


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

        /*//jbg_dataType
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
        jrb_mean.setEnabled(false); */

        jbg_bootstrapType = new ButtonGroup();
        jbg_bootstrapType.add(jrb_SampleCentric);
        jbg_bootstrapType.add(jrb_GeneCentric);
        String propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.CalculateSignificance.getName());
        if (propValue.equals("true"))
            jchb_CalculateSignificance.setSelected(true);
        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.BootstrapMode.getName());
        jrb_SampleCentric.setSelected(true);
        if (!propValue.equals(jrb_SampleCentric.getText()))
            jrb_GeneCentric.setSelected(true);
        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.BootstrapExpMatrix.getName());
        if (new File(propValue).exists())
            setExprMatrixFile(new File(propValue));
        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.NumOfSamplings.getName());
        try {
            int numOfSamplings = Integer.parseInt(propValue);
            jtxt_numOfSamplings.setText(numOfSamplings + "");
            jtxt_numOfSamplingsActionPerformed();
        } catch (NumberFormatException e) {
        }

        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.ChangeNetworkLayout.getName());
        if(propValue != null)
        jchb_changeNetworkLayout.setSelected(propValue.equals("false")? false : true);

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
        propValue = PSFCActivator.getPsfcProps()
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

        //Loops
        jbg_loopHandling = new ButtonGroup();
        jbg_loopHandling.add(jchb_ignoreLoops);
        jbg_loopHandling.add(jchb_precomputeLoops);
        jbg_loopHandling.add(jchb_iterateUntilConvergence);

        //default
        jchb_ignoreLoops.setSelected(true);

        //selection from properties
        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.LoopHandling.getName());
        buttons = jbg_loopHandling.getElements();
        while (buttons.hasMoreElements()) {
            JCheckBox button = (JCheckBox) buttons.nextElement();
            if (button.getText().equals(propValue))
                button.setSelected(true);
        }
        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.ConvergenceThreshold.getName());
        jtxt_convergenceThreshold.setText(propValue);
        jtxt_convergenceThresholdActionPerformed();

        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.MaxNumOfIterations.getName());
        jtxt_maxNumOfIterations.setText(propValue);
        jtxt_maxNumOfIterationsActionPerformed();


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

    private void setToolTips() {
        jl_selectEdgeTypeAttribute.setToolTipText("String attribute where types of edges are specified.");
        jb_checkEdgeTypes.setToolTipText("Check the unique edge types in selected attribute");
        jl_selectNodeDataAttribute.setToolTipText("Numeric attribute where the node values are present");

        jb_sortNetwork.setToolTipText("See how the network will look like after sorting");
        jchb_changeNetworkLayout.setToolTipText("Does not preserve original layout!");
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
        if (edgeTypeRuleNameConfigFile != null)
            jl_edgeTypeConfigFileName.setToolTipText(edgeTypeRuleNameConfigFile.getAbsolutePath());
        if (ruleNameRuleConfigFile != null)
            jl_ruleNameRuleConfigFileName.setToolTipText(ruleNameRuleConfigFile.getAbsolutePath());
        if (exprMatrixFile != null)
            jl_exprMatrixFile.setToolTipText(exprMatrixFile.getAbsolutePath());

        jtxt_convergenceThreshold.setToolTipText("Percentage of signal change between two iterations");
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
            jl_flagGeneral.setIcon(getRedFlagIcon());
        } else {
            jb_sortNetwork.setEnabled(true);
            boolean nodeDataColumn = getNodeDataColumn() != null;
            boolean edgeTypeColumn = getEdgeTypeColumn() != null;
            boolean config = (edgeTypeRuleNameConfigFile != null &&
                    edgeTypeRuleNameConfigFile.exists());
            if (!nodeDataColumn || !edgeTypeColumn)
                jl_flagGeneral.setIcon(getRedFlagIcon());
            else
                jl_flagGeneral.setIcon(getGreenFlagIcon());


            config = (config && ruleNameRuleConfigFile != null && ruleNameRuleConfigFile.exists());
            if (!config)
                jl_flagRules.setIcon(getRedFlagIcon());
            else
                jl_flagRules.setIcon(getGreenFlagIcon());
            if (nodeDataColumn && edgeTypeColumn && config) {
                jb_calculateFlow.setEnabled(true);
            }

            if (jchb_CalculateSignificance.isSelected()) {
                jrb_SampleCentric.setEnabled(true);
                jrb_GeneCentric.setEnabled(true);
                jl_exprMatrixFile.setEnabled(true);
                jb_GeneMatrixFile.setEnabled(true);
                jtxt_numOfSamplings.setEnabled(true);

                if (jrb_GeneCentric.isSelected()) {
                    if (exprMatrixFile == null) {
                        jl_flagOptions.setIcon(getRedFlagIcon());
                        jl_exprMatrixFile.setText("No file chosen");
                        jl_exprMatrixFile.setForeground(new java.awt.Color(153, 0, 0));
                        jb_calculateFlow.setEnabled(false);
                    } else {
                        jl_flagOptions.setIcon(getGreenFlagIcon());
                        jl_exprMatrixFile.setForeground(Color.black);
                        jl_exprMatrixFile.setText(exprMatrixFile.getName());
                        jl_exprMatrixFile.setToolTipText(exprMatrixFile.getAbsolutePath());
                    }
                } else {
                    jl_flagOptions.setIcon(getGreenFlagIcon());
                    jl_exprMatrixFile.setEnabled(false);
                }
            } else {
                jrb_SampleCentric.setEnabled(false);
                jrb_GeneCentric.setEnabled(false);
                jl_exprMatrixFile.setEnabled(false);
                jb_GeneMatrixFile.setEnabled(false);
                jtxt_numOfSamplings.setEnabled(false);
            }

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

        if (jrb_updatedNodeScores.isSelected()) {
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

        //Loops
        if (jchb_iterateUntilConvergence.isSelected()) {
            jtxt_convergenceThreshold.setEnabled(true);
            jtxt_maxNumOfIterations.setEnabled(true);
        } else {
            jtxt_convergenceThreshold.setEnabled(false);
            jtxt_maxNumOfIterations.setEnabled(false);
        }

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

    private Icon getRedFlagIcon() {
        if (redFlagIcon == null) {
            ClassLoader cl = PSFCActivator.class.getClassLoader();
            redFlagIcon = new ImageIcon(cl.getResource(redFlagIconName));
        }
        return redFlagIcon;
    }

    private Icon getGreenFlagIcon() {
        if (greenFlagIcon == null) {
            ClassLoader cl = PSFCActivator.class.getClassLoader();
            greenFlagIcon = new ImageIcon(cl.getResource(greenFlagIconName));
        }
        return greenFlagIcon;
    }


    private Properties getNodeDataProperties() {
        Properties properties = new Properties();
        properties.setProperty(ENodeDataProps.NODE_DEFAULT_VALUE.getName(), Node.getDefaultValue());
        return properties;
    }

    private Properties getLoopHandlingProperties() {
        Properties properties = new Properties();

        if(jchb_iterateUntilConvergence.isSelected()) {
            properties.setProperty(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.ITERATE_UNTIL_CONVERGENCE);
            properties.setProperty(ELoopHandlingProps.ConvergenceThreshold.getName(), Double.parseDouble(jtxt_convergenceThreshold.getText())+"");
            properties.setProperty(ELoopHandlingProps.MaxNumOfIterations.getName(), Integer.parseInt(jtxt_maxNumOfIterations.getText()) + "");
        } else{
            if(jchb_precomputeLoops.isSelected())
                properties.setProperty(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.PRECOMPUTE_LOOPS);
            else
                properties.setProperty(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.IGNORE_LOOPS); //default
        }

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
                (jrb_SampleCentric.isSelected() ? Bootstrap.SAMPLECENTRIC : Bootstrap.GENECENTRIC) + "");
        if (jrb_GeneCentric.isSelected()) {
            if (exprMatrixFile != null && exprMatrixFile.exists())
                properties.setProperty(Bootstrap.EXPMATRIXFILE, exprMatrixFile.getAbsolutePath());
        }


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
