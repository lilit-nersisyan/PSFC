package org.cytoscape.psfc.gui;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.*;
import org.cytoscape.psfc.DoubleFormatter;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.*;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.logic.algorithms.Bootstrap;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.*;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * PUBCLI CLASS PSFCPanel
 * <p>
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
    private String flowHomeIconName = "flow_home_icon.png";
    private String flowRvIconName = "flow_rv_icon.png";
    private String flowFwIconName = "flow_fw_icon.png";
    private String flowEndIconName = "flow_end_icon.png";
    private HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>> networkLevelNodeSignalMap = new HashMap<CyNetwork, HashMap<Integer, HashMap<CyNode, Double>>>();
    private HashMap<CyNetwork, HashMap<Integer, HashMap<CyEdge, Double>>> networkLevelEdgeSignalMap = new HashMap<CyNetwork, HashMap<Integer, HashMap<CyEdge, Double>>>();
    private CalculateScoreFlowAction calculateFlowAction = null;
    private int minNodeSignalIndex = 0;
    private int midNodeSignalIndex = 1;
    private int maxNodeSignalIndex = 2;
    private int minEdgeSignalIndex = 3;
    private int midEdgeSignalIndex = 4;
    private int maxEdgeSignalIndex = 5;
    private static Color minNodeColor = Color.decode("#000080");
    private static Color midNodeColor = Color.decode("#FFFFFF");
    private static Color maxNodeColor = Color.decode("#800000");
    private static double minEdgeWidth = 1.;
    private static double midEdgeWidth = 4.;
    private static double maxEdgeWidth = 7.;
    private HashMap<CyNetwork, double[]> networkMinMaxSignalsMap = new HashMap<>(); // contains minNodeSignal, midNodeSignal, maxNodeSignal, minEdgeSignal, midEdgeSignal, maxEdgeSignal
    private HashMap<CyNetwork, double[]> networkMinMaxEdgeWidthMap = new HashMap<>();
    private HashMap<CyNetwork, Color[]> networkMinMaxNodeColorMap = new HashMap<>();
    private ArrayList<CyColumn> selectedNodeDataColumns;
    Properties multiColProperties = new Properties();


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

    // Variables declaration - do not modify
    private javax.swing.JPanel jP_nodeData;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jb_GeneMatrixFile;
    private javax.swing.JButton jb_calculateFlow;
    private javax.swing.JButton jb_checkEdgeTypes;
    private javax.swing.JButton jb_chooseEdgeTypeConfigFile;
    private javax.swing.JButton jb_chooseRuleNameRuleConfigFile;
    private javax.swing.JButton jb_createNodeFunctionColumn;
    private javax.swing.JButton jb_editEdgeTypeConfig;
    private javax.swing.JButton jb_editRuleNameRuleConfig;
    private javax.swing.JButton jb_flow_end;
    private javax.swing.JButton jb_flow_fw;
    private javax.swing.JButton jb_flow_home;
    private javax.swing.JButton jb_flow_rv;
    private javax.swing.JButton jb_multipleColumns;
    private javax.swing.JButton jb_openLogFile;
    private javax.swing.JButton jb_projectWebPage;
    private javax.swing.JButton jb_refreshEdgeRanks;
    private javax.swing.JButton jb_refreshEdgeTypeAttrs;
    private javax.swing.JButton jb_refreshNetworks;
    private javax.swing.JButton jb_refreshNodeDataAttrs;
    private javax.swing.JButton jb_refreshWeigths;
    private javax.swing.JButton jb_rulePresetsGuide;
    private javax.swing.JButton jb_saveSettings;
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
    private javax.swing.JLabel jl_colorScheme;
    private javax.swing.JLabel jl_colorScheme1;
    private javax.swing.JLabel jl_color_max;
    private javax.swing.JLabel jl_color_mid;
    private javax.swing.JLabel jl_color_min;
    private javax.swing.JLabel jl_convergenceThreshold;
    private javax.swing.JLabel jl_currentLevel;
    private javax.swing.JLabel jl_edgeProcesssingSequence;
    private javax.swing.JLabel jl_edgeTypeConfigFile;
    private javax.swing.JLabel jl_edgeTypeConfigFileName;
    private javax.swing.JLabel jl_edgeWidth_max;
    private javax.swing.JLabel jl_edgeWidth_mid;
    private javax.swing.JLabel jl_edgeWidth_min;
    private javax.swing.JLabel jl_exprMatrixFile;
    private javax.swing.JLabel jl_flowVisualization;
    private javax.swing.JLabel jl_level;
    private javax.swing.JLabel jl_maxNumOfIterations;
    private javax.swing.JLabel jl_missingValues;
    private javax.swing.JLabel jl_multiInOutRules;
    private javax.swing.JLabel jl_multiSignalProcessing;
    private javax.swing.JLabel jl_network_and_attrs;
    private javax.swing.JLabel jl_nodeDataOptions;
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
    private javax.swing.JPanel jp_colorChooser_max;
    private javax.swing.JPanel jp_colorChooser_mid;
    private javax.swing.JPanel jp_colorChooser_min;
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
    private javax.swing.JRadioButton jrb_multipleColumns;
    private javax.swing.JRadioButton jrb_multiplication;
    private javax.swing.JRadioButton jrb_noRanks;
    private javax.swing.JRadioButton jrb_noSplitRule;
    private javax.swing.JRadioButton jrb_outgoingEdges;
    private javax.swing.JRadioButton jrb_proportional;
    private javax.swing.JRadioButton jrb_singleColumn;
    private javax.swing.JRadioButton jrb_suppliedWeights;
    private javax.swing.JRadioButton jrb_updatedNodeScores;
    private javax.swing.JScrollPane jsl_iterateUntilConvergence;
    private javax.swing.JSlider jsl_levels;
    private javax.swing.JScrollPane jsp_nodeFunctionText;
    private javax.swing.JScrollPane jsp_precomputeLoops;
    private javax.swing.JTextArea jta_about;
    private javax.swing.JTextArea jta_iterateUntilConvergence;
    private javax.swing.JTextArea jta_nodeFunctionText;
    private javax.swing.JTextArea jta_precomputeLoops;
    private javax.swing.JTabbedPane jtp_psfc;
    private javax.swing.JTextField jtxt_convergenceThreshold;
    private javax.swing.JTextField jtxt_edgeWidth_max;
    private javax.swing.JTextField jtxt_edgeWidth_mid;
    private javax.swing.JTextField jtxt_edgeWidth_min;
    private javax.swing.JTextField jtxt_maxEdgeSignal;
    private javax.swing.JTextField jtxt_maxNodeSignal;
    private javax.swing.JTextField jtxt_maxNumOfIterations;
    private javax.swing.JTextField jtxt_midEdgeSignal;
    private javax.swing.JTextField jtxt_midNodeSignal;
    private javax.swing.JTextField jtxt_minEdgeSignal;
    private javax.swing.JTextField jtxt_minNodeSignal;
    private javax.swing.JTextField jtxt_missingValues;
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
    private ButtonGroup jbg_singleMultipleColumns;


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
        jb_multipleColumns = new javax.swing.JButton();
        jrb_singleColumn = new javax.swing.JRadioButton();
        jrb_multipleColumns = new javax.swing.JRadioButton();
        jp_flowVisualization = new javax.swing.JPanel();
        jsl_levels = new javax.swing.JSlider();
        jl_flowVisualization = new javax.swing.JLabel();
        jl_level = new javax.swing.JLabel();
        jl_colorScheme = new javax.swing.JLabel();
        jl_color_min = new javax.swing.JLabel();
        jl_color_mid = new javax.swing.JLabel();
        jl_color_max = new javax.swing.JLabel();
        jp_colorChooser_min = new javax.swing.JPanel();
        jp_colorChooser_max = new javax.swing.JPanel();
        jp_colorChooser_mid = new javax.swing.JPanel();
        jl_colorScheme1 = new javax.swing.JLabel();
        jl_edgeWidth_min = new javax.swing.JLabel();
        jl_edgeWidth_mid = new javax.swing.JLabel();
        jl_edgeWidth_max = new javax.swing.JLabel();
        jtxt_edgeWidth_min = new javax.swing.JTextField();
        jtxt_edgeWidth_max = new javax.swing.JTextField();
        jtxt_edgeWidth_mid = new javax.swing.JTextField();
        jl_currentLevel = new javax.swing.JLabel();
        jb_flow_rv = new javax.swing.JButton();
        jb_flow_home = new javax.swing.JButton();
        jb_flow_fw = new javax.swing.JButton();
        jb_flow_end = new javax.swing.JButton();
        jtxt_minNodeSignal = new javax.swing.JTextField();
        jtxt_midNodeSignal = new javax.swing.JTextField();
        jtxt_maxNodeSignal = new javax.swing.JTextField();
        jtxt_maxEdgeSignal = new javax.swing.JTextField();
        jtxt_minEdgeSignal = new javax.swing.JTextField();
        jtxt_midEdgeSignal = new javax.swing.JTextField();
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
        jP_nodeData = new javax.swing.JPanel();
        jl_nodeDataOptions = new javax.swing.JLabel();
        jl_missingValues = new javax.swing.JLabel();
        jtxt_missingValues = new javax.swing.JTextField();
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
        jb_editEdgeTypeConfig = new javax.swing.JButton();
        jp_ruleConfigPanel = new javax.swing.JPanel();
        jl_ruleConfigFile = new javax.swing.JLabel();
        jb_chooseRuleNameRuleConfigFile = new javax.swing.JButton();
        jl_ruleNameRuleConfigFileName = new javax.swing.JLabel();
        jb_editRuleNameRuleConfig = new javax.swing.JButton();
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
        jPanel1 = new javax.swing.JPanel();
        jb_createNodeFunctionColumn = new javax.swing.JButton();
        jsp_nodeFunctionText = new javax.swing.JScrollPane();
        jta_nodeFunctionText = new javax.swing.JTextArea();
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
        jb_saveSettings = new javax.swing.JButton();


        jp_General.setAutoscrolls(true);

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

        jb_multipleColumns.setText("Choose columns");

        jrb_singleColumn.setText("single column");

        jrb_multipleColumns.setText("multiple columns");

        javax.swing.GroupLayout jp_network_attrsLayout = new javax.swing.GroupLayout(jp_network_attrs);
        jp_network_attrs.setLayout(jp_network_attrsLayout);
        jp_network_attrsLayout.setHorizontalGroup(
                jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_checkEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                                .addComponent(jrb_singleColumn, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                                                .addGap(57, 57, 57))
                                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                                                .addComponent(jcb_nodeDataAttribute, 0, 0, Short.MAX_VALUE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jb_multipleColumns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jrb_multipleColumns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_chooseNetwork))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_network_attrsLayout.setVerticalGroup(
                jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                .addComponent(jl_network_and_attrs, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jl_chooseNetwork)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jcb_network, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_refreshNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jl_selectEdgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jb_checkEdgeTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jcb_edgeTypeAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jb_refreshEdgeTypeAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(jrb_multipleColumns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_multipleColumns))
                                        .addGroup(jp_network_attrsLayout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(jl_selectNodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(1, 1, 1)
                                                .addComponent(jrb_singleColumn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jp_network_attrsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_refreshNodeDataAttrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jcb_nodeDataAttribute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(39, 39, 39))
        );

        jp_flowVisualization.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_flowVisualization.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_flowVisualization.setForeground(new java.awt.Color(51, 102, 0));
        jl_flowVisualization.setText("Flow visualization");
        jl_flowVisualization.setMaximumSize(new java.awt.Dimension(145, 15));

        jl_level.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_level.setForeground(new java.awt.Color(102, 102, 102));
        jl_level.setText("Level (Timestep):");

        jl_colorScheme.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_colorScheme.setForeground(new java.awt.Color(102, 102, 102));
        jl_colorScheme.setText("Color scheme");

        jl_color_min.setForeground(new java.awt.Color(102, 102, 102));
        jl_color_min.setText("Min");

        jl_color_mid.setForeground(new java.awt.Color(102, 102, 102));
        jl_color_mid.setText("Mid");

        jl_color_max.setForeground(new java.awt.Color(102, 102, 102));
        jl_color_max.setText("Max");

        jp_colorChooser_min.setBackground(new java.awt.Color(0, 0, 102));
        jp_colorChooser_min.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jp_colorChooser_min.setPreferredSize(new java.awt.Dimension(18, 18));

        javax.swing.GroupLayout jp_colorChooser_minLayout = new javax.swing.GroupLayout(jp_colorChooser_min);
        jp_colorChooser_min.setLayout(jp_colorChooser_minLayout);
        jp_colorChooser_minLayout.setHorizontalGroup(
                jp_colorChooser_minLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );
        jp_colorChooser_minLayout.setVerticalGroup(
                jp_colorChooser_minLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );

        jp_colorChooser_max.setBackground(new java.awt.Color(102, 0, 0));
        jp_colorChooser_max.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jp_colorChooser_max.setPreferredSize(new java.awt.Dimension(18, 18));

        javax.swing.GroupLayout jp_colorChooser_maxLayout = new javax.swing.GroupLayout(jp_colorChooser_max);
        jp_colorChooser_max.setLayout(jp_colorChooser_maxLayout);
        jp_colorChooser_maxLayout.setHorizontalGroup(
                jp_colorChooser_maxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );
        jp_colorChooser_maxLayout.setVerticalGroup(
                jp_colorChooser_maxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );

        jp_colorChooser_mid.setBackground(new java.awt.Color(255, 255, 255));
        jp_colorChooser_mid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jp_colorChooser_mid.setPreferredSize(new java.awt.Dimension(18, 18));

        javax.swing.GroupLayout jp_colorChooser_midLayout = new javax.swing.GroupLayout(jp_colorChooser_mid);
        jp_colorChooser_mid.setLayout(jp_colorChooser_midLayout);
        jp_colorChooser_midLayout.setHorizontalGroup(
                jp_colorChooser_midLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );
        jp_colorChooser_midLayout.setVerticalGroup(
                jp_colorChooser_midLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 16, Short.MAX_VALUE)
        );

        jl_colorScheme1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_colorScheme1.setForeground(new java.awt.Color(102, 102, 102));
        jl_colorScheme1.setText("Edge width");

        jl_edgeWidth_min.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeWidth_min.setText("Min");

        jl_edgeWidth_mid.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeWidth_mid.setText("Mid");

        jl_edgeWidth_max.setForeground(new java.awt.Color(102, 102, 102));
        jl_edgeWidth_max.setText("Max");

        jtxt_edgeWidth_min.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_edgeWidth_min.setPreferredSize(new java.awt.Dimension(25, 18));

        jtxt_edgeWidth_max.setMinimumSize(new java.awt.Dimension(18, 18));
        jtxt_edgeWidth_max.setPreferredSize(new java.awt.Dimension(25, 18));

        jtxt_edgeWidth_mid.setMinimumSize(new java.awt.Dimension(18, 18));
        jtxt_edgeWidth_mid.setPreferredSize(new java.awt.Dimension(25, 18));

        jl_currentLevel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_currentLevel.setForeground(new java.awt.Color(102, 102, 102));
        jl_currentLevel.setText("0");

        jtxt_minNodeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_minNodeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

        jtxt_midNodeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_midNodeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

        jtxt_maxNodeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_maxNodeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

        jtxt_maxEdgeSignal.setEditable(false);
        jtxt_maxEdgeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_maxEdgeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

        jtxt_minEdgeSignal.setEditable(false);
        jtxt_minEdgeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_minEdgeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

        jtxt_midEdgeSignal.setMinimumSize(new java.awt.Dimension(14, 14));
        jtxt_midEdgeSignal.setPreferredSize(new java.awt.Dimension(18, 18));

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
                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_colorScheme)
                                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                                .addGap(40, 40, 40)
                                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(jp_colorChooser_mid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jl_color_mid))
                                                                .addGap(22, 22, 22)
                                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jl_color_max)
                                                                        .addComponent(jp_colorChooser_max, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jtxt_edgeWidth_min, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jl_edgeWidth_min))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jtxt_edgeWidth_mid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jl_edgeWidth_mid))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jl_edgeWidth_max)
                                                                        .addComponent(jtxt_edgeWidth_max, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(jl_colorScheme1))
                                                .addGap(56, 56, 56))
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jp_colorChooser_min, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                                .addComponent(jl_level)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jl_currentLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(21, 21, 21)
                                                                .addComponent(jb_flow_home, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jb_flow_rv, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jb_flow_fw, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jb_flow_end, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jsl_levels, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_color_min))
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addComponent(jtxt_minNodeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtxt_midNodeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtxt_maxNodeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jtxt_minEdgeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtxt_midEdgeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jtxt_maxEdgeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(49, 49, 49))))
        );
        jp_flowVisualizationLayout.setVerticalGroup(
                jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                .addComponent(jl_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jb_flow_home, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jl_level)
                                                        .addComponent(jl_currentLevel)))
                                        .addComponent(jb_flow_rv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jb_flow_fw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jb_flow_end, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsl_levels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_colorScheme1)
                                        .addComponent(jl_colorScheme))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                        .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jtxt_edgeWidth_mid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jtxt_edgeWidth_min, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jtxt_edgeWidth_max, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(jp_colorChooser_min, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jl_edgeWidth_min)
                                                                        .addComponent(jl_edgeWidth_mid)
                                                                        .addComponent(jl_edgeWidth_max))
                                                                .addComponent(jl_color_min, javax.swing.GroupLayout.Alignment.TRAILING)))
                                                .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                        .addComponent(jp_colorChooser_max, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jl_color_max)))
                                        .addGroup(jp_flowVisualizationLayout.createSequentialGroup()
                                                .addComponent(jp_colorChooser_mid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jl_color_mid)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jtxt_minNodeSignal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jtxt_midNodeSignal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jtxt_maxNodeSignal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_flowVisualizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jtxt_minEdgeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jtxt_midEdgeSignal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jtxt_maxEdgeSignal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jp_GeneralLayout = new javax.swing.GroupLayout(jp_General);
        jp_General.setLayout(jp_GeneralLayout);
        jp_GeneralLayout.setHorizontalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jp_network_attrs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_flowVisualization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_GeneralLayout.setVerticalGroup(
                jp_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_GeneralLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jp_network_attrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(jp_flowVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(176, Short.MAX_VALUE))
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
                                                                .addComponent(jl_exprMatrixFile, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))))
                                        .addComponent(jchb_CalculateSignificance)
                                        .addComponent(jl_samplingType)
                                        .addComponent(jrb_SampleCentric))
                                .addContainerGap(21, Short.MAX_VALUE))
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

        jP_nodeData.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jl_nodeDataOptions.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jl_nodeDataOptions.setForeground(new java.awt.Color(51, 102, 0));
        jl_nodeDataOptions.setText("Node data options");

        jl_missingValues.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_missingValues.setForeground(new java.awt.Color(102, 102, 102));
        jl_missingValues.setText("Missing values");

        jtxt_missingValues.setText("1.0");

        javax.swing.GroupLayout jP_nodeDataLayout = new javax.swing.GroupLayout(jP_nodeData);
        jP_nodeData.setLayout(jP_nodeDataLayout);
        jP_nodeDataLayout.setHorizontalGroup(
                jP_nodeDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jP_nodeDataLayout.createSequentialGroup()
                                .addGroup(jP_nodeDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_nodeDataOptions)
                                        .addGroup(jP_nodeDataLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jl_missingValues)
                                                .addGap(18, 18, 18)
                                                .addComponent(jtxt_missingValues, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 163, Short.MAX_VALUE))
        );
        jP_nodeDataLayout.setVerticalGroup(
                jP_nodeDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jP_nodeDataLayout.createSequentialGroup()
                                .addComponent(jl_nodeDataOptions)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jP_nodeDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_missingValues)
                                        .addComponent(jtxt_missingValues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(76, Short.MAX_VALUE))
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
                                .addGap(0, 0, Short.MAX_VALUE))
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
                                .addGroup(jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jp_algorithms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_significance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jP_nodeData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(47, Short.MAX_VALUE))
        );
        jp_OptionsLayout.setVerticalGroup(
                jp_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_OptionsLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jp_algorithms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jp_significance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(jP_nodeData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(152, Short.MAX_VALUE))
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

        jb_editEdgeTypeConfig.setBorder(null);

        javax.swing.GroupLayout jp_edgeTypeConfigPanelLayout = new javax.swing.GroupLayout(jp_edgeTypeConfigPanel);
        jp_edgeTypeConfigPanel.setLayout(jp_edgeTypeConfigPanelLayout);
        jp_edgeTypeConfigPanelLayout.setHorizontalGroup(
                jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                                .addGroup(jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_chooseEdgeTypeConfigFile)
                                                        .addComponent(jl_edgeTypeConfigFile, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 8, Short.MAX_VALUE))
                                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                                .addComponent(jl_edgeTypeConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jb_editEdgeTypeConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        jp_edgeTypeConfigPanelLayout.setVerticalGroup(
                jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_edgeTypeConfigPanelLayout.createSequentialGroup()
                                .addComponent(jl_edgeTypeConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_chooseEdgeTypeConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_edgeTypeConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jb_editEdgeTypeConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jl_edgeTypeConfigFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
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

        jb_editRuleNameRuleConfig.setBorder(null);

        javax.swing.GroupLayout jp_ruleConfigPanelLayout = new javax.swing.GroupLayout(jp_ruleConfigPanel);
        jp_ruleConfigPanel.setLayout(jp_ruleConfigPanelLayout);
        jp_ruleConfigPanelLayout.setHorizontalGroup(
                jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                                .addComponent(jl_ruleNameRuleConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jb_editRuleNameRuleConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                                .addGroup(jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_ruleConfigFile)
                                                        .addComponent(jb_chooseRuleNameRuleConfigFile))
                                                .addGap(0, 27, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jp_ruleConfigPanelLayout.setVerticalGroup(
                jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                .addComponent(jl_ruleConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_chooseRuleNameRuleConfigFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_ruleConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_ruleConfigPanelLayout.createSequentialGroup()
                                                .addComponent(jl_ruleNameRuleConfigFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jb_editRuleNameRuleConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
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
                                                .addComponent(jp_edgeTypeConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jp_ruleConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_simpleRulesLayout.setVerticalGroup(
                jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_simpleRulesLayout.createSequentialGroup()
                                .addComponent(jl_simpleRules)
                                .addGap(18, 18, 18)
                                .addGroup(jp_simpleRulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_edgeTypeConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                                        .addGroup(jp_simpleRulesLayout.createSequentialGroup()
                                                .addComponent(jp_ruleConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))))
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
        jcb_edgeWeights.setName(""); // NOI18N
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
                                .addContainerGap(31, Short.MAX_VALUE))
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jb_createNodeFunctionColumn.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jb_createNodeFunctionColumn.setForeground(new java.awt.Color(51, 102, 0));
        jb_createNodeFunctionColumn.setText("Create Node Function Column");
        jb_createNodeFunctionColumn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 153, 0), 2));

        jsp_nodeFunctionText.setBorder(null);

        jta_nodeFunctionText.setBackground(new java.awt.Color(240, 240, 240));
        jta_nodeFunctionText.setColumns(20);
        jta_nodeFunctionText.setFont(new java.awt.Font("Monospaced", 2, 13)); // NOI18N
        jta_nodeFunctionText.setLineWrap(true);
        jta_nodeFunctionText.setRows(5);
        jta_nodeFunctionText.setText("Want to apply functions onto nodes? \nWrite them in the \"psf_function\" node  column(if it's not there, hit the button to create it).\n  \nYou may choose the following functions:\n      min, max, mean, sum, prod ");
        jta_nodeFunctionText.setBorder(null);
        jsp_nodeFunctionText.setViewportView(jta_nodeFunctionText);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jsp_nodeFunctionText)
                                .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jb_createNodeFunctionColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jsp_nodeFunctionText, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jb_createNodeFunctionColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout jp_RulesLayout = new javax.swing.GroupLayout(jp_Rules);
        jp_Rules.setLayout(jp_RulesLayout);
        jp_RulesLayout.setHorizontalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jb_rulePresetsGuide)
                                        .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_simpleRules, javax.swing.GroupLayout.PREFERRED_SIZE, 340, Short.MAX_VALUE))
                                .addContainerGap(25, Short.MAX_VALUE))
        );
        jp_RulesLayout.setVerticalGroup(
                jp_RulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_RulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jb_rulePresetsGuide)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_simpleRules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_multiInOutRulesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addGap(0, 0, Short.MAX_VALUE))
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
                                                .addComponent(jsl_iterateUntilConvergence))
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
                                                .addGap(0, 89, Short.MAX_VALUE)))
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

        jta_precomputeLoops.setEditable(false);
        jta_precomputeLoops.setBackground(new java.awt.Color(240, 240, 240));
        jta_precomputeLoops.setColumns(20);
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
                                .addGap(0, 149, Short.MAX_VALUE))
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
                                .addGroup(jp_LoopsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jp_procomputeLoops, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_iterateUntilConvergence, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_ignoreLoops, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(25, Short.MAX_VALUE))
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
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtp_psfc.addTab("Loops", jp_Loops);

        jl_psfc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jb_projectWebPage.setText("Go to project web page");

        jb_userManual.setText("Open User Manual");

        jta_about.setEditable(false);
        jta_about.setBackground(new java.awt.Color(240, 240, 240));
        jta_about.setColumns(20);
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
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_HelpLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jta_about, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26))
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGap(49, 49, 49)
                                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                                .addGap(71, 71, 71)
                                                .addGroup(jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jb_userManual, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jb_projectWebPage, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(43, Short.MAX_VALUE))
        );
        jp_HelpLayout.setVerticalGroup(
                jp_HelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_HelpLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jl_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                                .addComponent(jta_about, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jb_projectWebPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jb_userManual)
                                .addGap(60, 60, 60))
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
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jtp_psfc, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jb_openLogFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jb_saveSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jb_calculateFlow, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jl_selectedNetwork, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jtp_psfc)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jb_openLogFile)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_saveSettings))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jl_selectedNetwork)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_calculateFlow)))
                                .addContainerGap())
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
        jrb_singleColumn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_singleColumnActionPerformed();
            }
        });
        jrb_multipleColumns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_multipleColumnsActionPerformed();
            }
        });
        jb_multipleColumns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_multipleColumnsActionPerformed();
            }
        });

        //Flow visualization
        jsl_levels.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jl_currentLevel.setText("" + jsl_levels.getValue());
                jb_showStateActionPerformed(e);
            }
        });

        jb_flow_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_flow_homeActionPerformed(e);
            }
        });

        jb_flow_rv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_flow_rvActionPerformed(e);
            }
        });

        jb_flow_fw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_flow_fwActionPerformed(e);
            }
        });

        jb_flow_end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_flow_endActionPerformed(e);
            }
        });

        jp_colorChooser_min.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jp_colorChooser_minActionPerformed(e);
            }
        });

        jp_colorChooser_mid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jp_colorChooser_midActionPerformed(e);
            }
        });

        jp_colorChooser_max.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jp_colorChooser_maxActionPerformed(e);
            }
        });

        jtxt_minNodeSignal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_minNodeSignalActionPerformed();
            }
        });

        jtxt_midNodeSignal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_midNodeSignalActionPerformed();
            }
        });

        jtxt_maxNodeSignal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_maxNodeSignalActionPerformed();
            }
        });

        jtxt_midEdgeSignal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_midEdgeSignalActionPerformed();
            }
        });

        jtxt_edgeWidth_min.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_edgeWidth_minActionPerformed();
            }
        });

        jtxt_edgeWidth_mid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_edgeWidth_midActionPerformed();
            }
        });

        jtxt_edgeWidth_max.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_edgeWidth_maxActionPerformed();
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

        jtxt_missingValues.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_missingValuesActionPerformed();
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

        jb_editEdgeTypeConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_editEdgeTypeConfigActionPerformed(e);
            }
        });


        jb_chooseRuleNameRuleConfigFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_chooseRuleNameRuleConfigFileActionPerformed();
            }
        });

        jb_editRuleNameRuleConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_editRuleNameRuleConfigActionPerformed(e);
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

        jb_createNodeFunctionColumn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_createNodeFunctionColumnActionPerformed();
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

        CyColumn nodeLevelColumn = getNodeColumn(levelAttr);
        CyColumn isOperatorColumn = getNodeColumn(EColumnNames.PSFC_isOperator.getName());
        CyColumn nodeFunctionColumn = getNodeColumn(EColumnNames.PSFC_FUNCTION.getName());
        CyColumn edgeIsBackwardColumn = getEdgeColumn(EColumnNames.PSFC_IS_BACKWARD.getName());
        Properties nodeDataProperties = getNodeDataProperties();
        Properties multiSignalProps = getMultiSignalProperties();
        Properties loopHandlingProps = getLoopHandlingProperties();
        if (multiSignalProps == null)
            return;
        if (jrb_multipleColumns.isSelected()) {
//            CalculateScoreFlowMultipleColumnsTask calculateScoreFlowMultipleColumnsTask =
//                    new CalculateScoreFlowMultipleColumnsTask(e, network,
//                            edgeTypeColumn, nodeLevelColumn,
//                            isOperatorColumn, nodeFunctionColumn, edgeIsBackwardColumn,
//                            nodeDataProperties,
//                            multiSignalProps, loopHandlingProps, this);

//            TaskIterator taskIterator = new TaskIterator();
//            taskIterator.append(calculateScoreFlowMultipleColumnsTask);
//            PSFCActivator.taskManager.execute(taskIterator);
            CalculateScoreFlowMultiColAction calculateScoreFlowMultiColAction =
                    new CalculateScoreFlowMultiColAction(network, edgeTypeColumn,
                            selectedNodeDataColumns, nodeLevelColumn,
                            isOperatorColumn, nodeFunctionColumn, edgeIsBackwardColumn,
                            edgeTypeRuleNameConfigFile, ruleNameRuleConfigFile,
                            nodeDataProperties, multiSignalProps, loopHandlingProps,
                            jchb_CalculateSignificance.isSelected(), this);
            if (jchb_CalculateSignificance.isSelected())
                calculateScoreFlowMultiColAction.setBootstrapProps(getBootstrapProperties());
            calculateScoreFlowMultiColAction.actionPerformed(e);
        } else {
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
            calculateFlowAction = new CalculateScoreFlowAction(
                    network, edgeTypeColumn, nodeDataColumn, nodeLevelColumn, isOperatorColumn,
                    nodeFunctionColumn, edgeIsBackwardColumn,
                    edgeTypeRuleNameConfigFile, ruleNameRuleConfigFile, nodeDataProperties,
                    multiSignalProps, loopHandlingProps, jchb_CalculateSignificance.isSelected(), this);
            if (jchb_CalculateSignificance.isSelected()) {
                calculateFlowAction.setBootstrapProps(getBootstrapProperties());
            }
            calculateFlowAction.actionPerformed(e);
        }
    }

    class CalculateScoreFlowMultipleColumnsTask extends AbstractTask {
        private final ActionEvent e;
        private CyNetwork network;
        private CyColumn edgeTypeColumn;
        private CyColumn nodeLevelColumn;
        private CyColumn isOperatorColumn;
        private Properties nodeDataProperties;
        private Properties multiSignalProps;
        private Properties loopHandlingProps;
        private CyColumn nodeFunctionColumn;
        private CyColumn edgeIsBackwardColumn;
        private PSFCPanel psfcPanel;

        public CalculateScoreFlowMultipleColumnsTask(ActionEvent e, CyNetwork network,
                                                     CyColumn edgeTypeColumn,
                                                     CyColumn nodeLevelColumn,
                                                     CyColumn isOperatorColumn,
                                                     CyColumn nodeFunctionColumn,
                                                     CyColumn edgeIsBackwardColumn,
                                                     Properties nodeDataProperties,
                                                     Properties multiSignalProps,
                                                     Properties loopHandlingProps,
                                                     PSFCPanel psfcPanel) {
            this.network = network;
            this.edgeTypeColumn = edgeTypeColumn;
            this.nodeLevelColumn = nodeLevelColumn;
            this.isOperatorColumn = isOperatorColumn;
            this.nodeFunctionColumn = nodeFunctionColumn;
            this.edgeIsBackwardColumn = edgeIsBackwardColumn;
            this.nodeDataProperties = nodeDataProperties;
            this.multiSignalProps = multiSignalProps;
            this.loopHandlingProps = loopHandlingProps;
            this.e = e;
            this.psfcPanel = psfcPanel;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC.CalculateFlowForMultipleColumnsTask");
            if (selectedNodeDataColumns != null && !selectedNodeDataColumns.isEmpty()) {
                taskMonitor.setStatusMessage("Selected columns: " + selectedNodeDataColumns);
                for (CyColumn nextColumn : selectedNodeDataColumns) {
                    taskMonitor.setStatusMessage("Performing PSF for column " + nextColumn.getName());

                    calculateFlowAction = new CalculateScoreFlowAction(
                            network, edgeTypeColumn, nextColumn, nodeLevelColumn, isOperatorColumn,
                            nodeFunctionColumn, edgeIsBackwardColumn,
                            edgeTypeRuleNameConfigFile, ruleNameRuleConfigFile, nodeDataProperties,
                            multiSignalProps, loopHandlingProps, jchb_CalculateSignificance.isSelected(),
                            psfcPanel);
                    if (jchb_CalculateSignificance.isSelected()) {
                        calculateFlowAction.setBootstrapProps(getBootstrapProperties());
                    }

                    calculateFlowAction.actionPerformed(e);
                    int timeout = 10;
                    int time = 0;

                    while (!calculateFlowAction.done() && time < timeout) {
                        try {
                            Thread.sleep(100);
                            time++;
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (cancelled || calculateFlowAction.isCancelled()) {
                        break;
                    }
                }
                if (!cancelled && !calculateFlowAction.isCancelled()) {

                    //create a summary backup file
                    String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
                    File summaryFile = new File(PSFCActivator.getPSFCDir(), networkName + "_summary.xls");
                    HashMap<CyColumn, ArrayList<Double>> columnScoreMap = new HashMap<>();
                    HashMap<CyColumn, ArrayList<Double>> columnPvalMap = new HashMap<>();
                    ArrayList<String> rownames = new ArrayList<>();
                    boolean firstFile = true;
                    for (CyColumn nextColumn : selectedNodeDataColumns) {
                        File scoreBackupFile = new File(PSFCActivator.getPSFCDir(),
                                networkName + nextColumn.getName() + ".xls");
                        ArrayList<Double> scores = new ArrayList<>();
                        ArrayList<Double> pvals = new ArrayList<>();

                        if (scoreBackupFile.exists()) {
                            BufferedReader reader = new BufferedReader(new FileReader(scoreBackupFile));
                            String line;
                            int ncol = 0, scoreInd = 0, pvalInd = 0;
                            while ((line = reader.readLine()) != null) {
                                String[] tokens = line.split("\t");
                                if (line.startsWith("SUID")) {
                                    ncol = tokens.length;
                                    if (ncol < 2)
                                        throw new Exception("PSFC:: Error while generating summary file. Number of columns in the backup file " + scoreBackupFile + " was less than 2");
                                    scoreInd = ncol - 2;
                                    pvalInd = ncol - 1;
                                } else {
                                    if (firstFile) {
                                        rownames.add(tokens[1]);
                                    }

                                    try {
                                        double score = Double.parseDouble(tokens[scoreInd]);
                                        scores.add(score);
                                    } catch (NumberFormatException e1) {
                                        throw new NumberFormatException("PSFC:: Error while generating summary file. " +
                                                "The value in " + scoreBackupFile.getName() + " at column "
                                                + scoreInd + " and rowname " + tokens[1] +
                                                " was not convertable to double");
                                    }


                                    try {
                                        double pval = Double.parseDouble(tokens[pvalInd]);
                                        pvals.add(pval);
                                    } catch (NumberFormatException e1) {
                                        throw new NumberFormatException("PSFC:: Error while generating summary file. " +
                                                "The value in " + scoreBackupFile.getName() + " at column "
                                                + pvalInd + " and rowname " + tokens[1] +
                                                " was not convertable to double");
                                    }

                                }
                            }
                            reader.close();
                            columnScoreMap.put(nextColumn, scores);
                            columnPvalMap.put(nextColumn, pvals);
                            firstFile = false;
                        } else {
                            throw new Exception("PSFC: Error while generating summary file. The backup file: " + scoreBackupFile.getAbsolutePath() + " does not exist");
                        }
                    }
                    PrintWriter writer = new PrintWriter(summaryFile);
                    String header = "Name";
                    for (CyColumn key : columnScoreMap.keySet()) {
                        header += String.format("\tscore.%s\tpval.%s", key.getName(), key.getName());
                    }
                    writer.write(header);

                    for (int i = 0; i < rownames.size(); i++) {
                        String line = rownames.get(i);
                        for (CyColumn key : columnScoreMap.keySet()) {
                            line += String.format("\t%f\t%f", columnScoreMap.get(key).get(i), columnPvalMap.get(key).get(i));
                        }
                        writer.append("\n" + line);
                    }
                    writer.close();
                    taskMonitor.setStatusMessage("Successfully generated summary backup file at " + summaryFile.getAbsolutePath());
                }
            }
            System.gc();
        }
    }

    private void mapMinMaxSignals(CyNetwork network) {
        if (network == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected network does not exist. \nPlease, refresh the network list and choose a valid network for pathway flow calculation.",
                    "PSFC user message", JOptionPane.OK_OPTION);
            return;
        }
        double minNodeSignal = Double.MAX_VALUE;
        double midNodeSignal = 0;
        double maxNodeSignal = Double.MIN_VALUE;
        double minEdgeSignal = Double.MAX_VALUE;
        double midEdgeSignal = 0;
        double maxEdgeSignal = Double.MIN_VALUE;
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++) {
            HashMap<CyNode, Double> nodeSignalMap = networkLevelNodeSignalMap.get(network).get(level);
            HashMap<CyEdge, Double> edgeSignalMap = networkLevelEdgeSignalMap.get(network).get(level);
            if (nodeSignalMap != null)
                for (CyNode cyNode : nodeSignalMap.keySet()) {
                    double signal = nodeSignalMap.get(cyNode);
                    if (signal < minNodeSignal)
                        minNodeSignal = signal;
                    else if (signal > maxNodeSignal)
                        maxNodeSignal = signal;
                }
            if (edgeSignalMap != null)
                if (edgeSignalMap != null)
                    for (CyEdge cyEdge : edgeSignalMap.keySet()) {
                        double signal = edgeSignalMap.get(cyEdge);
                        if (signal < minEdgeSignal)
                            minEdgeSignal = signal;
                        else if (signal > maxEdgeSignal)
                            maxEdgeSignal = signal;
                    }
        }
        // set the midNodeSignal default to 1
        if (maxNodeSignal >= 1)
            midNodeSignal = 1;
        else
            midNodeSignal = (minNodeSignal + maxNodeSignal) / 2;
        midEdgeSignal = (minEdgeSignal + maxEdgeSignal) / 2;


        networkMinMaxSignalsMap.put(network,
                new double[]{minNodeSignal, midNodeSignal, maxNodeSignal,
                        minEdgeSignal, midEdgeSignal, maxEdgeSignal});
        networkMinMaxEdgeWidthMap.put(network, new double[]{
                minEdgeWidth,
                midEdgeWidth,
                maxEdgeWidth});
        networkMinMaxNodeColorMap.put(network, new Color[]{
                minNodeColor,
                midNodeColor,
                maxNodeColor
        });

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
            PSFCActivator.cyApplicationManager.setCurrentNetwork(selectedNetwork);
//            jl_selectedNetwork.setForeground(new java.awt.Color(153, 0, 0));
//            jl_selectedNetwork.setToolTipText("Selected network is not currently visualized, or refresh network selection");
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

    private void jrb_singleColumnActionPerformed() {
        enableButtons();
    }

    private void jrb_multipleColumnsActionPerformed() {
        enableButtons();
    }

    private void jb_multipleColumnsActionPerformed() {
        CyNetwork network = getSelectedNetwork();
        Collection<CyColumn> columns = network.getDefaultNodeTable().getColumns();
        MultipleColumnSelection multipleColumnSelection = new MultipleColumnSelection(columns);
        multipleColumnSelection.setVisible(true);
    }

    private class MultipleColumnSelection extends JFrame {
        private Collection<CyColumn> columns;
        private javax.swing.JButton jb_select;
        private javax.swing.JList jl_columnList;
        private javax.swing.JScrollPane jsp_columnList;


        MultipleColumnSelection(Collection<CyColumn> columns) {
            this.columns = columns;
            initComponents();
        }

        private void initComponents() {
            jb_select = new javax.swing.JButton();
            jsp_columnList = new javax.swing.JScrollPane();
            jl_columnList = new javax.swing.JList();

            setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            jb_select.setText("Select");
            jb_select.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jb_selectActionPerformed();
                }
            });

            final String[] strings;
            if (columns != null) {
                strings = new String[columns.size()];
                int i = 0;
                for (CyColumn column : columns) {
                    strings[i++] = column.getName();
                }
            } else {
                strings = new String[]{"null columns"};
            }

            jl_columnList.setModel(new javax.swing.AbstractListModel() {
                public int getSize() {
                    return strings.length;
                }

                public Object getElementAt(int i) {
                    return strings[i];
                }
            });
            jsp_columnList.setViewportView(jl_columnList);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jsp_columnList)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                    .addGap(0, 154, Short.MAX_VALUE)
                                                    .addComponent(jb_select)))
                                    .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jsp_columnList, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jb_select)
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            pack();
        }

        private void jb_selectActionPerformed() {
            selectedNodeDataColumns = new ArrayList<CyColumn>();
            List<String> selectedValues = jl_columnList.getSelectedValuesList();
            for (String value : selectedValues) {
                Iterator<CyColumn> iterator = columns.iterator();
                while (iterator.hasNext()) {
                    CyColumn next = iterator.next();
                    if (next.getName().equals(value))
                        selectedNodeDataColumns.add(next);
                }
            }
            setVisible(false);
            enableButtons();
        }

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

    private void visualizeFlowActionPerformed(ActionEvent e, int level) {
        ArrayList<Integer> levels = new ArrayList<>();
        levels.add(level);
        CyNetwork network = getSelectedNetwork();
        if (network == null)
            return;
        double[] signals = networkMinMaxSignalsMap.get(network);
        VisualizeFlowAction visualizeFlowAction = new VisualizeFlowAction(network,
                signals[minNodeSignalIndex], signals[midNodeSignalIndex], signals[maxNodeSignalIndex],
                levels,
                signals[minEdgeSignalIndex], signals[midEdgeSignalIndex], signals[maxEdgeSignalIndex],
                networkMinMaxEdgeWidthMap.get(network)[0], networkMinMaxEdgeWidthMap.get(network)[1], networkMinMaxEdgeWidthMap.get(network)[2],
                networkMinMaxNodeColorMap.get(network)[0], networkMinMaxNodeColorMap.get(network)[1], networkMinMaxNodeColorMap.get(network)[2],
                this);
        visualizeFlowAction.actionPerformed(e);
    }

    private void jb_flow_homeActionPerformed(ActionEvent e) {
        visualizeFlowActionPerformed(e, 0);
    }

    private void jb_flow_rvActionPerformed(ActionEvent e) {
        int currentLevel = Integer.parseInt(jl_currentLevel.getText());
        if (currentLevel == 0)
            return;
        int prevLevel = currentLevel - 1;
        visualizeFlowActionPerformed(e, prevLevel);
    }

    private void jb_flow_fwActionPerformed(ActionEvent e) {
        int currentLevel = Integer.parseInt(jl_currentLevel.getText());
        if (currentLevel == jsl_levels.getMaximum())
            return;
        int nextLevel = currentLevel + 1;
        visualizeFlowActionPerformed(e, nextLevel);
    }

    private void jb_flow_endActionPerformed(ActionEvent e) {
        visualizeFlowActionPerformed(e, jsl_levels.getMaximum());
    }

    private void jb_showStateActionPerformed(ChangeEvent e) {
        visualizeFlowActionPerformed(null, jsl_levels.getValue());
    }

    private void jp_colorChooser_minActionPerformed(MouseEvent e) {
        Color newColor = JColorChooser.showDialog(this, "Min node signal color", jp_colorChooser_min.getBackground());
        if (newColor != null) {
            jp_colorChooser_min.setBackground(newColor);
            networkMinMaxNodeColorMap.get(getSelectedNetwork())[0] = newColor;
        }
    }

    private void jp_colorChooser_midActionPerformed(MouseEvent e) {
        Color newColor = JColorChooser.showDialog(this, "Min node signal color", jp_colorChooser_min.getBackground());
        if (newColor != null) {
            jp_colorChooser_mid.setBackground(newColor);
            networkMinMaxNodeColorMap.get(getSelectedNetwork())[1] = newColor;
        }
    }

    private void jp_colorChooser_maxActionPerformed(MouseEvent e) {
        Color newColor = JColorChooser.showDialog(this, "Min node signal color", jp_colorChooser_min.getBackground());
        if (newColor != null) {
            jp_colorChooser_max.setBackground(newColor);
            networkMinMaxNodeColorMap.get(getSelectedNetwork())[2] = newColor;
        }
    }

    private void jtxt_minNodeSignalActionPerformed() {
        double oldValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[minNodeSignalIndex];
        double maxValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[maxNodeSignalIndex];
        double midValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[midNodeSignalIndex];

        Double newValue;
        try {
            newValue = Double.parseDouble(jtxt_minNodeSignal.getText());
        } catch (NumberFormatException e) {
            jtxt_minNodeSignal.setText(oldValue + "");
            return;
        }

        if (newValue > maxValue) {
            jtxt_minNodeSignal.setText(oldValue + "");
            return;
        }
        networkMinMaxSignalsMap.get(getSelectedNetwork())[minNodeSignalIndex] = newValue;
        jtxt_minNodeSignal.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_midNodeSignalActionPerformed() {
        double minValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[minNodeSignalIndex];
        double maxValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[maxNodeSignalIndex];
        double oldValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[midNodeSignalIndex];

        Double newValue;
        try {
            newValue = Double.parseDouble(jtxt_midNodeSignal.getText());
        } catch (NumberFormatException e) {
            jtxt_midNodeSignal.setText(oldValue + "");
            return;
        }

        if (newValue < minValue || newValue > maxValue) {
            jtxt_midNodeSignal.setText(oldValue + "");
            return;
        }
        networkMinMaxSignalsMap.get(getSelectedNetwork())[midNodeSignalIndex] = newValue;
        jtxt_midNodeSignal.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_maxNodeSignalActionPerformed() {
        double minValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[minNodeSignalIndex];
        double oldValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[maxNodeSignalIndex];
        double midValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[midNodeSignalIndex];

        Double newValue;
        try {
            newValue = Double.parseDouble(jtxt_maxNodeSignal.getText());
        } catch (NumberFormatException e) {
            jtxt_maxNodeSignal.setText(oldValue + "");
            return;
        }

        if (newValue < midValue) {
            jtxt_maxNodeSignal.setText(oldValue + "");
            return;
        }
        networkMinMaxSignalsMap.get(getSelectedNetwork())[maxNodeSignalIndex] = newValue;
        jtxt_maxNodeSignal.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_midEdgeSignalActionPerformed() {
        double minValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[midEdgeSignalIndex];
        double maxValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[maxEdgeSignalIndex];
        double oldValue = networkMinMaxSignalsMap.get(getSelectedNetwork())[midEdgeSignalIndex];

        Double newValue;
        try {
            newValue = Double.parseDouble(jtxt_midEdgeSignal.getText());
        } catch (NumberFormatException e) {
            jtxt_midEdgeSignal.setText(oldValue + "");
            return;
        }

        if (newValue < minValue || newValue > maxValue) {
            jtxt_midEdgeSignal.setText(oldValue + "");
            return;
        }
        networkMinMaxSignalsMap.get(getSelectedNetwork())[midEdgeSignalIndex] = newValue;
        jtxt_midEdgeSignal.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_edgeWidth_minActionPerformed() {
        double oldValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[0];
        double midValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[1];
        double newValue;
        try {
            newValue = Double.parseDouble(jtxt_edgeWidth_min.getText());
        } catch (NumberFormatException e) {
            jtxt_edgeWidth_min.setText(oldValue + "");
            return;
        }
        if (newValue < 0 || newValue > midValue) {
            jtxt_edgeWidth_min.setText(oldValue + "");
            return;
        }
        networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[0] = newValue;
        jtxt_edgeWidth_min.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_edgeWidth_midActionPerformed() {
        double minValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[0];
        double maxValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[2];
        double oldValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[1];
        double newValue;
        try {
            newValue = Double.parseDouble(jtxt_edgeWidth_mid.getText());
        } catch (NumberFormatException e) {
            jtxt_edgeWidth_mid.setText(oldValue + "");
            return;
        }
        if (newValue < minValue || newValue > maxValue) {
            jtxt_edgeWidth_mid.setText(oldValue + "");
            return;
        }
        networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[1] = newValue;
        jtxt_edgeWidth_mid.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jtxt_edgeWidth_maxActionPerformed() {
        double midValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[1];
        double oldValue = networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[2];
        double newValue;
        try {
            newValue = Double.parseDouble(jtxt_edgeWidth_max.getText());
        } catch (NumberFormatException e) {
            jtxt_edgeWidth_max.setText(oldValue + "");
            return;
        }
        if (newValue < midValue) {
            jtxt_edgeWidth_max.setText(oldValue + "");
            return;
        }
        networkMinMaxEdgeWidthMap.get(getSelectedNetwork())[2] = newValue;
        jtxt_edgeWidth_max.setText(DoubleFormatter.formatDouble(newValue) + "");
    }

    private void jchb_changeNetworkLayoutActionPerformed() {

    }

    public void setVisualizationComponents(CyNetwork network,
                                           HashMap<Integer, HashMap<CyNode, Double>> levelNodeSignalMap,
                                           HashMap<Integer, HashMap<CyEdge, Double>> levelCyEdgeScoreMap) {
        networkLevelNodeSignalMap.put(network, levelNodeSignalMap);
        networkLevelEdgeSignalMap.put(network, levelCyEdgeScoreMap);
        mapMinMaxSignals(network);
        setMinMaxSignals(network);
        activateFlowVisualizationPanel(network);
    }

    private void setMinMaxSignals(CyNetwork network) {
        jtxt_minNodeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[minNodeSignalIndex]) + "");
        jtxt_midNodeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[midNodeSignalIndex]) + "");
        jtxt_maxNodeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[maxNodeSignalIndex]) + "");
        jtxt_minEdgeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[minEdgeSignalIndex]) + "");
        jtxt_midEdgeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[midEdgeSignalIndex]) + "");
        jtxt_maxEdgeSignal.setText(DoubleFormatter.formatDouble(networkMinMaxSignalsMap.get(network)[maxEdgeSignalIndex]) + "");
        jtxt_edgeWidth_min.setText(networkMinMaxEdgeWidthMap.get(network)[0] + "");
        jtxt_edgeWidth_mid.setText(networkMinMaxEdgeWidthMap.get(network)[1] + "");
        jtxt_edgeWidth_max.setText(networkMinMaxEdgeWidthMap.get(network)[2] + "");
        jp_colorChooser_min.setBackground(networkMinMaxNodeColorMap.get(network)[0]);
        jp_colorChooser_mid.setBackground(networkMinMaxNodeColorMap.get(network)[1]);
        jp_colorChooser_max.setBackground(networkMinMaxNodeColorMap.get(network)[2]);
    }

    private void activateFlowVisualizationPanel(CyNetwork network) {
        HashMap<Integer, HashMap<CyNode, Double>> levelNodeSignalMap = networkLevelNodeSignalMap.get(network);
        if (levelNodeSignalMap == null || jrb_multipleColumns.isSelected()) {
            jsl_levels.setEnabled(false);
            jb_flow_home.setEnabled(false);
            jb_flow_rv.setEnabled(false);
            jb_flow_fw.setEnabled(false);
            jb_flow_end.setEnabled(false);
            jp_colorChooser_min.setEnabled(false);
            jp_colorChooser_mid.setEnabled(false);
            jp_colorChooser_max.setEnabled(false);
            jtxt_minNodeSignal.setEnabled(false);
            jtxt_midNodeSignal.setEnabled(false);
            jtxt_maxNodeSignal.setEnabled(false);
            jtxt_edgeWidth_min.setEnabled(false);
            jtxt_edgeWidth_mid.setEnabled(false);
            jtxt_edgeWidth_max.setEnabled(false);
            jtxt_minEdgeSignal.setEnabled(false);
            jtxt_midEdgeSignal.setEnabled(false);
            jtxt_maxEdgeSignal.setEnabled(false);
        } else {
            jsl_levels.setMinimum(0);
            jsl_levels.setMaximum(levelNodeSignalMap.size() - 1);
            jsl_levels.setEnabled(true);
            jb_flow_home.setEnabled(true);
            jb_flow_rv.setEnabled(true);
            jb_flow_fw.setEnabled(true);
            jb_flow_end.setEnabled(true);
            jp_colorChooser_min.setEnabled(true);
            jp_colorChooser_mid.setEnabled(true);
            jp_colorChooser_max.setEnabled(true);
            jtxt_minNodeSignal.setEnabled(true);
            jtxt_midNodeSignal.setEnabled(true);
            jtxt_maxNodeSignal.setEnabled(true);
            jtxt_edgeWidth_min.setEnabled(true);
            jtxt_edgeWidth_mid.setEnabled(true);
            jtxt_edgeWidth_max.setEnabled(true);
            jtxt_minEdgeSignal.setEnabled(true);
            jtxt_midEdgeSignal.setEnabled(true);
            jtxt_maxEdgeSignal.setEnabled(true);
            setMinMaxSignals(network);
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

            /*** Visualization ***/
            psfcProps.setProperty(EpsfcProps.NodeColorMin.getName(), String.valueOf(jp_colorChooser_min.getBackground().getRGB()));
            psfcProps.setProperty(EpsfcProps.NodeColorMid.getName(), String.valueOf(jp_colorChooser_mid.getBackground().getRGB()));
            psfcProps.setProperty(EpsfcProps.NodeColorMax.getName(), String.valueOf(jp_colorChooser_max.getBackground().getRGB()));

            psfcProps.setProperty(EpsfcProps.EdgeWidthMin.getName(), jtxt_edgeWidth_min.getText());
            psfcProps.setProperty(EpsfcProps.EdgeWidthMid.getName(), jtxt_edgeWidth_mid.getText());
            psfcProps.setProperty(EpsfcProps.EdgeWidthMax.getName(), jtxt_edgeWidth_max.getText());


            /*** End of Visualization ***/

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

            psfcProps.setProperty(ENodeDataProps.MISSING_DATA_VALUE.getName(),
                    jtxt_missingValues.getText());
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
            SortNetworkAction sortNetworkAction = new SortNetworkAction(selectedNetwork, getSortingAlgorithm(), jchb_changeNetworkLayout.isSelected());
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

    private void jtxt_missingValuesActionPerformed() {
        Double missingValue = Double.parseDouble(String.valueOf(ENodeDataProps.MISSING_DATA_VALUE.getDefaultValue()));

        try {
            missingValue = Double.parseDouble(jtxt_missingValues.getText());
        } catch (NumberFormatException e) {
            String property = (String) PSFCActivator.getPsfcProps().get(ENodeDataProps.MISSING_DATA_VALUE.getName());
            missingValue = Double.parseDouble(property);
        }

        jtxt_missingValues.setText(String.valueOf(missingValue));
    }


    /**
     * ***************
     * Actions: jp_Rules
     * ****************
     */
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

    private void jb_editEdgeTypeConfigActionPerformed(ActionEvent e) {
        if (edgeTypeRuleNameConfigFile != null & edgeTypeRuleNameConfigFile.exists()) {
            try {
                Desktop.getDesktop().edit(edgeTypeRuleNameConfigFile);
            } catch (IOException e1) {
                try {
                    Desktop.getDesktop().open(edgeTypeRuleNameConfigFile.getParentFile());
                } catch (IOException e2) {
                    System.out.println("PSFC:: cannot open the folder: "
                            + edgeTypeRuleNameConfigFile.getParent());
                }
                System.out.println("PSFC:: cannot open the file: "
                        + edgeTypeRuleNameConfigFile + " for editing");
            }
        }
    }

    private void jb_editRuleNameRuleConfigActionPerformed(ActionEvent e) {
        if (ruleNameRuleConfigFile != null & ruleNameRuleConfigFile.exists()) {
            try {
                Desktop.getDesktop().edit(ruleNameRuleConfigFile);
            } catch (IOException e1) {
                try {
                    Desktop.getDesktop().open(ruleNameRuleConfigFile.getParentFile());
                } catch (IOException e2) {
                    System.out.println("PSFC:: cannot open the folder: "
                            + ruleNameRuleConfigFile.getParent());
                }
                System.out.println("PSFC:: cannot open the file: "
                        + ruleNameRuleConfigFile + " for editing");
            }
        }
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

    private void jb_createNodeFunctionColumnActionPerformed() {
        CyColumn nodeFunctionColumn = getNodeColumn(EColumnNames.PSFC_FUNCTION.getName());
        if (nodeFunctionColumn == null) {
            boolean successs = addNodeColumn(EColumnNames.PSFC_FUNCTION.getName(), String.class);
            if (!successs)
                System.out.println("Problem creating the column: " + EColumnNames.PSFC_FUNCTION.getName());
        }
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
        //Temp solution before download and open as local file option implementation
        String[] buttons = new String[]{"Open in Web browser"};
        int rc = JOptionPane.showOptionDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
                "PSFC User Manual is at " + PSFCActivator.getUserManualURL(), "PSFC User Manual",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                buttons, buttons[0]);
        if (rc == 0)
            new WebLoadAction(PSFCActivator.getUserManualURL()).actionPerformed(e);
//        String[] buttons = new String[]{"As local PDF", "In Web browser"};
//        int rc = JOptionPane.showOptionDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
//                "Open PSFC User Manual:", "Open PSFC User Manual",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
//                buttons, buttons[1]);
//        if (rc == 0)
//            new OpenFileAction(PSFCActivator.getUserManualFileName()).actionPerformed(e);
//        else
//            new WebLoadAction(PSFCActivator.getUserManualURL()).actionPerformed(e);

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

        String property = "";
        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.NodeColorMin.getName());
            minNodeColor = Color.decode(property);
            jp_colorChooser_min.setBackground(minNodeColor);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load minNodeColor from property " + property);
        }

        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.NodeColorMid.getName());
            midNodeColor = Color.decode(property);
            jp_colorChooser_mid.setBackground(midNodeColor);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load midNodeColor from property " + property);
        }

        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.NodeColorMax.getName());
            maxNodeColor = Color.decode(property);
            jp_colorChooser_max.setBackground(maxNodeColor);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load minNodeColor from property " + property);
        }

        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.EdgeWidthMin.getName());
            minEdgeWidth = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load minEdgeWidth from property " + EpsfcProps.EdgeWidthMin.getName());
        }

        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.EdgeWidthMid.getName());
            midEdgeWidth = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load midEdgeWidth from property " + EpsfcProps.EdgeWidthMid.getName());
        }

        try {
            property = (String) PSFCActivator.getPsfcProps().get(EpsfcProps.EdgeWidthMax.getName());
            maxEdgeWidth = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            System.out.println("PSFC::Could not load maxEdgeWidth from property " + EpsfcProps.EdgeWidthMax.getName());
        }


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

        jbg_singleMultipleColumns = new ButtonGroup();
        jbg_singleMultipleColumns.add(jrb_singleColumn);
        jbg_singleMultipleColumns.add(jrb_multipleColumns);
        jrb_singleColumn.setSelected(true);
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
            System.out.println("PSFC::" + "Exception while parsing number of samplings property." + Arrays.toString(e.getStackTrace()));
        }

        propValue = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.ChangeNetworkLayout.getName());
        if (propValue != null)
            jchb_changeNetworkLayout.setSelected(propValue.equals("true") ? true : false);

        //EdgeTypeRuleNameConfigFile
        String fileName = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.EdgeTypeRuleNameConfigFile.getName());
        File file = new File(fileName);
        jb_editEdgeTypeConfig.setIcon(getIcon("edit.png"));
        if (file.exists()) {
            setEdgeTypeRuleNameConfigFile(file);
            jb_editEdgeTypeConfig.setEnabled(true);
        } else {
            jb_editEdgeTypeConfig.setEnabled(false);
        }

        //RuleNameRuleConfigFile
        fileName = PSFCActivator.getPsfcProps().getProperty(EpsfcProps.RuleNameRuleConfigFile.getName());
        file = new File(fileName);
        jb_editRuleNameRuleConfig.setIcon(getIcon("edit.png"));
        if (file.exists()) {
            setRuleNameRuleConfigFile(file);
            jb_editRuleNameRuleConfig.setEnabled(true);
        } else {
            jb_editRuleNameRuleConfig.setEnabled(false);
        }

        //Split signal on button group
        jbg_splitSignalOn = new ButtonGroup();
        jbg_splitSignalOn.add(jrb_incomingEdges);
        jbg_splitSignalOn.add(jrb_outgoingEdges);

        //default selection
        jrb_incomingEdges.setSelected(true);

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
        jrb_proportional.setSelected(true);

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
        jrb_addition.setSelected(true);

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

        //Node data
        propValue = PSFCActivator.getPsfcProps()
                .getProperty(ENodeDataProps.MISSING_DATA_VALUE.getName());
        jtxt_missingValues.setText(propValue);

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
//        jtxt_level.setText(jsl_levels.getValue() + "");
        jl_currentLevel.setText(jsl_levels.getValue() + "");
//        jtxt_level.setEnabled(false);
//        jb_playFlow.setEnabled(false);
        jb_flow_home.setIcon(getIcon(flowHomeIconName));
        jb_flow_rv.setIcon(getIcon(flowRvIconName));
        jb_flow_fw.setIcon(getIcon(flowFwIconName));
        jb_flow_end.setIcon(getIcon(flowEndIconName));

        jb_flow_home.setEnabled(false);
        jb_flow_rv.setEnabled(false);
        jb_flow_fw.setEnabled(false);
        jb_flow_end.setEnabled(false);

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
        jl_missingValues.setToolTipText("The value of nodes assigned when no data for the node is present (e.g. usually the missing values are replaced with 1.0)");
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
            setTabIcon(0, getRedFlagIcon());
        } else {
            jb_sortNetwork.setEnabled(true);
            boolean nodeDataColumn;
            if (jrb_singleColumn.isSelected()) {
                nodeDataColumn = getNodeDataColumn() != null;
                jcb_nodeDataAttribute.setEnabled(true);
                jb_multipleColumns.setEnabled(false);
            } else {
                jcb_nodeDataAttribute.setEnabled(false);
                jb_multipleColumns.setEnabled(true);
                nodeDataColumn = (selectedNodeDataColumns != null && !selectedNodeDataColumns.isEmpty());
            }
            boolean edgeTypeColumn = getEdgeTypeColumn() != null;
            boolean config = (edgeTypeRuleNameConfigFile != null &&
                    edgeTypeRuleNameConfigFile.exists());
            if (config)
                jb_editEdgeTypeConfig.setEnabled(true);
            else
                jb_editEdgeTypeConfig.setEnabled(false);
            if (!nodeDataColumn || !edgeTypeColumn)
                setTabIcon(0, getRedFlagIcon());
            else
                setTabIcon(0, getGreenFlagIcon());

            if (ruleNameRuleConfigFile != null && ruleNameRuleConfigFile.exists())
                jb_editRuleNameRuleConfig.setEnabled(true);
            else
                jb_editRuleNameRuleConfig.setEnabled(false);
            config = (config && ruleNameRuleConfigFile != null && ruleNameRuleConfigFile.exists());
            if (!config)
                setTabIcon(2, getRedFlagIcon());
            else
                setTabIcon(2, getGreenFlagIcon());
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
                        setTabIcon(1, getRedFlagIcon());
                        jl_exprMatrixFile.setText("No file chosen");
                        jl_exprMatrixFile.setForeground(new java.awt.Color(153, 0, 0));
                        jb_calculateFlow.setEnabled(false);
                    } else {
                        setTabIcon(1, getGreenFlagIcon());
                        jl_exprMatrixFile.setForeground(Color.black);
                        jl_exprMatrixFile.setText(exprMatrixFile.getName());
                        jl_exprMatrixFile.setToolTipText(exprMatrixFile.getAbsolutePath());
                    }
                } else {
                    setTabIcon(1, getGreenFlagIcon());
                    jl_exprMatrixFile.setEnabled(false);
                }
            } else {
                setTabIcon(1, getGreenFlagIcon());
                jl_exprMatrixFile.setText("");
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

    private void setTabIcon(int index, Icon icon) {
        jtp_psfc.setIconAt(index, icon);
    }


    private Icon getIcon(String iconName) {
        Icon icon = null;
        try {
            ClassLoader cl = PSFCActivator.class.getClassLoader();
            icon = new ImageIcon(cl.getResource(iconName));
        } catch (Exception e) {
            System.out.println("Problem loading component " + iconName + " from resources");
            e.printStackTrace();
        }
        return icon;
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
        properties.setProperty(ENodeDataProps.MISSING_DATA_VALUE.getName(), jtxt_missingValues.getText());
        return properties;
    }

    private Properties getLoopHandlingProperties() {
        Properties properties = new Properties();

        if (jchb_iterateUntilConvergence.isSelected()) {
            properties.setProperty(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.ITERATE_UNTIL_CONVERGENCE);
            properties.setProperty(ELoopHandlingProps.ConvergenceThreshold.getName(), Double.parseDouble(jtxt_convergenceThreshold.getText()) + "");
            properties.setProperty(ELoopHandlingProps.MaxNumOfIterations.getName(), Integer.parseInt(jtxt_maxNumOfIterations.getText()) + "");
        } else {
            if (jchb_precomputeLoops.isSelected())
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
        CyColumn nodeLevelColumn = getNodeColumn(levelAttr);
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

    private CyColumn getNodeColumn(String columnName) {
        try {
            CyNetwork network = getSelectedNetwork();
            return network.getDefaultNodeTable().getColumn(columnName);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean addNodeColumn(String columnName, Class<?> colClass) {
        try {
            CyNetwork network = getSelectedNetwork();
            network.getDefaultNodeTable().createColumn(columnName, colClass, true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private CyColumn getEdgeColumn(String columnName) {
        try {
            CyNetwork network = getSelectedNetwork();
            return network.getDefaultEdgeTable().getColumn(columnName);
        } catch (Exception e) {
            return null;
        }
    }

    private CyColumn getNodeDataColumn() {
        return getNodeColumn(jcb_nodeDataAttribute);
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
