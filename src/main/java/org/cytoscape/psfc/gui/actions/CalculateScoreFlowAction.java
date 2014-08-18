package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.enums.EMultiSignalProps;
import org.cytoscape.psfc.logic.algorithms.GraphManager;
import org.cytoscape.psfc.logic.algorithms.PSF;
import org.cytoscape.psfc.logic.parsers.RuleFilesParser;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.psfc.net.NetworkGraphMapper;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * PUBLIC CLASS CalculateScoreFlowAction
 * <p/>
 * Processes given network and its attributes, instantiates a new PSF object and
 * initiates the task for calculating score flows of the network based on available scores,
 * rules and topology.
 */
public class CalculateScoreFlowAction extends AbstractCyAction {

    private final Properties nodeDataProps;
    private final Properties multiSignalProps;
    private final PSFCPanel psfcPanel;
    private CyNetwork network;
    private CyColumn edgeTypeColumn;
    private CyColumn nodeDataColumn;
    private CyColumn nodeLevelColumn;
    private File edgeTypeRuleNameConfigFile;
    private File ruleConfigFile;
    private File scoreBackupFile;
    private String networkName;

    public static final String PSFC_SIGNAL = "psfc.signal_";

    public CalculateScoreFlowAction(CyNetwork network,
                                    CyColumn edgeTypeColumn,
                                    CyColumn nodeDataColumn,
                                    CyColumn nodeLevelColumn,
                                    File edgeTypeRuleNameConfigFile,
                                    File ruleConfigFile,
                                    Properties nodeDataProperties,
                                    Properties multiSignalProps,
                                    PSFCPanel psfcPanel) {
        super("Calculate score flow");

        this.network = network;
        this.edgeTypeColumn = edgeTypeColumn;
        this.nodeDataColumn = nodeDataColumn;
        this.nodeLevelColumn = nodeLevelColumn;
        this.edgeTypeRuleNameConfigFile = edgeTypeRuleNameConfigFile;
        this.ruleConfigFile = ruleConfigFile;
        this.nodeDataProps = nodeDataProperties;
        this.multiSignalProps = multiSignalProps;
        this.psfcPanel = psfcPanel;

        this.networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
        this.scoreBackupFile = new File(PSFCActivator.getPSFCDir(), networkName + ".xls");
        try {
            boolean success = scoreBackupFile.createNewFile();
            if (!success){
                PSFCActivator.getLogger().error("Could not create new file for " + scoreBackupFile.getAbsolutePath());
            }
        } catch (IOException e) {
            PSFCActivator.getLogger().error("Could not create new file for " + scoreBackupFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CalculateScoreFlowTask task = new CalculateScoreFlowTask();
        PSFCActivator.taskManager.execute(new TaskIterator(task));
    }

    private class CalculateScoreFlowTask extends AbstractTask {

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC.CalculateFlowTask");

            //Converting network to Graph
            taskMonitor.setStatusMessage("Converting network to PSFC Graph");
            Graph graph;
            try {
                graph = NetworkGraphMapper.graphFromNetwork(network, edgeTypeColumn);
            } catch (Exception e) {
                throw new Exception("Could not convert network to graph. Reason: " + e.getMessage(), e);
            }
            taskMonitor.setProgress(0.1);

            //Retrieving node levels from CyTable and keeping them in graph nodes
            taskMonitor.setStatusMessage("Retrieving node levels");
            HashMap<CyNode, Integer> cyNodeLevelMap;
            try {
                cyNodeLevelMap = getCyNodeLevelMap();
            } catch (Exception e) {
                throw new Exception("Node levels could not be retrieved from the column " + nodeLevelColumn.getName());
            }
            try {
                GraphManager.assignNodeLevels(graph, cyNodeLevelMap);
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }

            // Retrieving node scores from CyTable and keeping them in graph nodes
            taskMonitor.setStatusMessage("Retrieving node scores");
            HashMap<CyNode, Double> cyNodeDataMap;
            try {
                cyNodeDataMap = getCyNodeDataMap();
            } catch (Exception e) {
                throw new Exception("Node scores could not be retrieved from the column " + nodeDataColumn.getName());
            }
            try {
                GraphManager.assignNodeValues(graph, cyNodeDataMap);
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }
            taskMonitor.setProgress(0.2);

            //If edge weights are user supplied, take them from CyTable and keep in graph edges
            if (multiSignalProps.get(EMultiSignalProps.SplitSignalRule.getName()) == EMultiSignalProps.SPLIT_WEIGHTS) {
                Object obj = multiSignalProps.get(EMultiSignalProps.EdgeWeightsAttribute.getName());
                if (obj == null)
                    throw new Exception("Could not find " + EMultiSignalProps.EdgeWeightsAttribute.getName() + " property");
                CyColumn cyEdgeWeightColumn = (CyColumn) obj;
                HashMap<CyEdge, Double> cyEdgeWeightMap = getCyEdgeWeightMap(cyEdgeWeightColumn);
                try {
                    GraphManager.assignEdgeWeights(graph, cyEdgeWeightMap);
                } catch (Exception e) {
                    throw new Exception(e.getMessage(), e);
                }
            }

            //If edge order is given by ranks, take them from CyTable and keep in graph edges
            if (multiSignalProps.get(EMultiSignalProps.SignalProcessingOrder.getName()) == EMultiSignalProps.ORDER_RANKS) {
                Object obj = multiSignalProps.get(EMultiSignalProps.EdgeRankAttribute.getName());
                if (obj == null)
                    throw new Exception("Could not find " + EMultiSignalProps.EdgeRankAttribute.getName() + " property");
                CyColumn cyEdgeRankColumn = (CyColumn) obj;
                HashMap<CyEdge, Integer> cyEdgeRankMap = getCyEdgeRankMap(cyEdgeRankColumn);
                try {
                    GraphManager.assignEdgeRanks(graph, cyEdgeRankMap);
                } catch (Exception e) {
                    throw new Exception(e.getMessage(), e);
                }
            }

            //Instantiate new PSF class with generated graph, and perform pathway flow calculation
            try {
                PSF psf = new PSF(graph, RuleFilesParser.parseSimpleRules(edgeTypeRuleNameConfigFile, ruleConfigFile),
                        PSFCActivator.getLogger());
                psf.setNodeDataProps(nodeDataProps);
                psf.setMultiSignalProps(multiSignalProps);

                PSFCActivator.getLogger().info("\n################\n################");
                PSFCActivator.getLogger().info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
                PSFCActivator.getLogger().info("Action: score flow calculation");
                PSFCActivator.getLogger().info("Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));
                PSFCActivator.getLogger().info("Graph summary: " + graph.getSummary());
                PSFCActivator.getLogger().info("Score file: " + scoreBackupFile.getAbsolutePath());
                PSFCActivator.getLogger().info("edgeTypeRuleNameConfigFile: " + edgeTypeRuleNameConfigFile.toString());
                PSFCActivator.getLogger().info("ruleConfigFile: " + ruleConfigFile.toString());

                taskMonitor.setStatusMessage("Calculating flow scores");

                try {
                    psf.calculateFlow();
                } catch (Exception e) {
                    PSFCActivator.getLogger().error("Error occurred while flow score calculation. Reason: "
                            + e.getMessage(), e);
                    throw new Exception(e.getMessage(), e);
                }
                taskMonitor.setProgress(0.7);
                PSFCActivator.getLogger().debug("Pathway flow signals successfully updated");

                //Process output signals for exporting

                //Process node signals
                HashMap<Integer, HashMap<Node, Double>> levelNodeSignalMap = psf.getLevelNodeSignalMap(0);
                //A separate column of score attributes for each level will be kept in this map

                taskMonitor.setStatusMessage("Exporting updated signals to file " + scoreBackupFile.getAbsolutePath());
                HashMap<Integer, HashMap<CyNode, Double>> levelCyNodeScoreMap = new HashMap<Integer, HashMap<CyNode, Double>>();
                try {
                    PrintWriter writer = new PrintWriter(scoreBackupFile);
                    writer.append("SUID\tName\tLevel\tScore0\tScore1\n");

                    HashMap<CyNode, Double> prevNodeMap = new HashMap<CyNode, Double>();
                    for (Node node : graph.getNodes()) {
                        prevNodeMap.put(graph.getCyNode(node), node.getValue());
                    }
                    for (int level : levelNodeSignalMap.keySet()) {
                        HashMap<CyNode, Double> currentLevelCyNodeScoreMap = new HashMap<CyNode, Double>();
                        levelCyNodeScoreMap.put(level, currentLevelCyNodeScoreMap);
                        currentLevelCyNodeScoreMap.putAll(prevNodeMap);
                        for (Node node : levelNodeSignalMap.get(level).keySet()) {
                            CyNode cyNode = graph.getCyNode(node);
                            double score = levelNodeSignalMap.get(level).get(node);
                            currentLevelCyNodeScoreMap.remove(cyNode);
                            currentLevelCyNodeScoreMap.put(cyNode, score);
                            writer.append(cyNode.getSUID() + "\t"
                                    + node.getName() + "\t"
                                    + level + "\t"
                                    + node.getValue() + "\t"
                                    + score + "\n");
                        }
                        prevNodeMap = currentLevelCyNodeScoreMap;
                    }
                    writer.close();
                    PSFCActivator.getLogger().debug("Written flow scores to file "
                            + scoreBackupFile.getAbsolutePath());

                } catch (FileNotFoundException e) {
                    throw new Exception("Problem writing node scores to file "
                            + scoreBackupFile.getAbsolutePath()
                            + ". Reason: " + e.getMessage(), e);
                }


                //Process edge signals
                HashMap<Integer, HashMap<Edge, Double>> levelEdgeSignalMap = psf.getLevelEdgeSignalMap(0);
                //A separate column of score attributes for each level will be kept in this map
                HashMap<Integer, HashMap<CyEdge, Double>> levelCyEdgeScoreMap = new HashMap<Integer, HashMap<CyEdge, Double>>();
                HashMap<CyEdge, Double> prevEdgeMap = new HashMap<CyEdge, Double>();
                for (Edge edge : graph.getEdges()) {
                    CyEdge cyEdge = NetworkCyManager.getCyEdge(network, graph.getCyNode(edge.getSource()),
                            graph.getCyNode(edge.getTarget()));
                    prevEdgeMap.put(cyEdge, 1.);
                }
                for (int level : levelEdgeSignalMap.keySet()) {
                    HashMap<CyEdge, Double> currentLevelCyEdgeSignalMap = new HashMap<CyEdge, Double>();
                    levelCyEdgeScoreMap.put(level, currentLevelCyEdgeSignalMap);
                    currentLevelCyEdgeSignalMap.putAll(prevEdgeMap);
                    for (Edge edge : levelEdgeSignalMap.get(level).keySet()) {
                        CyEdge cyEdge = NetworkCyManager.getCyEdge(network, graph.getCyNode(edge.getSource()),
                                graph.getCyNode(edge.getTarget()));
                        double signal = levelEdgeSignalMap.get(level).get(edge);
                        currentLevelCyEdgeSignalMap.remove(cyEdge);
                        currentLevelCyEdgeSignalMap.put(cyEdge, signal);
                    }
                    prevEdgeMap = currentLevelCyEdgeSignalMap;
                }

                taskMonitor.setProgress(0.9);

                //Importing updated signals to Cytoscape

                taskMonitor.setStatusMessage("Importing updated signals to Cytoscape");
                //nodes
                try {
                    for (int level : levelCyNodeScoreMap.keySet()) {
                        NetworkCyManager.setNodeAttributesFromMap(network,
                                levelCyNodeScoreMap.get(level), PSFC_SIGNAL + level, Double.class);
                    }
                    PSFCActivator.getLogger().debug("Mapped CyNode score values to Cytoscape attributes");
                } catch (Exception e) {
                    throw new Exception("Error occured while mapping CyNode scores " +
                            "to Cytoscape attributes. Reason : "
                            + e.getMessage(), e);
                }

                //edges
                try {
                    for (int level : levelCyEdgeScoreMap.keySet()) {
                        NetworkCyManager.setEdgeAttributesFromMap(network,
                                levelCyEdgeScoreMap.get(level), "psfc.signal_" + level, Double.class);
                    }
                    PSFCActivator.getLogger().debug("Mapped CyEdge signal values to Cytoscape attributes");
                } catch (Exception e) {
                    throw new Exception("Error occured while mapping CyEdge scores " +
                            "to Cytoscape attributes. Reason : "
                            + e.getMessage(), e);
                }

                // Trigger psfcPanel to update Flow Visualization Panel
                psfcPanel.setVisualizationComoponents(network, levelCyNodeScoreMap);

                taskMonitor.setStatusMessage("Flow calculation task complete");
                taskMonitor.setProgress(1);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            } finally {
                PSFCActivator.getLogger().debug("PSFC Score Flow calculation finished");
                System.gc();
            }
        }

        private HashMap<CyNode, Integer> getCyNodeLevelMap() {
            HashMap<CyNode, Integer> cyNodeLevelMap = new HashMap<CyNode, Integer>();

            for (Object cyNodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) cyNodeObj;
                CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                int level = row.get(nodeLevelColumn.getName(), Integer.class);
                cyNodeLevelMap.put(cyNode, level);
            }
            return cyNodeLevelMap;
        }

        private HashMap<CyNode, Double> getCyNodeDataMap() throws Exception {
            HashMap<CyNode, Double> map = new HashMap<CyNode, Double>();
            String attr = nodeDataColumn.getName();
            for (Object cyNodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) cyNodeObj;
                CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                Object obj;
                try {
                    obj = row.get(attr, nodeDataColumn.getType());
                } catch (Exception e) {
                    throw new Exception("Error while retrieving node data from row " + row.toString()
                            + ".Reason: " + e.getMessage());
                }
                Double data = null;
                if (obj instanceof Double)
                    data = (Double) obj;
                else if (obj instanceof Integer)
                    data = (double) (Integer) obj;
                else if (obj instanceof String)
                    try {
                        data = Double.valueOf((String) obj);
                    } catch (NumberFormatException e) {
                        throw e;
                    }
                if (data != null)
                    map.put(cyNode, data);
            }
            return map;
        }

    }

    private HashMap<CyEdge, Integer> getCyEdgeRankMap(CyColumn cyEdgeRankColumn) throws Exception {
        HashMap<CyEdge, Integer> map = new HashMap<CyEdge, Integer>();
        String attr = cyEdgeRankColumn.getName();
        for (CyEdge cyEdge : network.getEdgeList()) {
            CyRow row = network.getDefaultEdgeTable().getRow(cyEdge.getSUID());
            Object obj;
            try {
                obj = row.get(attr, cyEdgeRankColumn.getType());
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }
            Integer data = null;
            if (obj instanceof Integer)
                data = (Integer) obj;
            else if (obj instanceof String)
                try {
                    data = Integer.valueOf((String) obj);
                } catch (NumberFormatException e) {
                    throw new Exception(e.getMessage(), e);
                }
            if (data != null)
                map.put(cyEdge, data);
        }
        return map;
    }


    private HashMap<CyEdge, Double> getCyEdgeWeightMap(CyColumn cyEdgeWeightColumn) throws Exception {
        HashMap<CyEdge, Double> map = new HashMap<CyEdge, Double>();
        String attr = cyEdgeWeightColumn.getName();
        for (CyEdge cyEdge : network.getEdgeList()) {
            CyRow row = network.getDefaultEdgeTable().getRow(cyEdge.getSUID());
            Object obj;
            try {
                obj = row.get(attr, cyEdgeWeightColumn.getType());
            } catch (Exception e) {
                throw e;
            }
            Double data = null;
            if (obj instanceof Double)
                data = (Double) obj;
            else if (obj instanceof Integer)
                data = (double) (Integer) obj;
            else if (obj instanceof String)
                try {
                    data = Double.valueOf((String) obj);
                } catch (NumberFormatException e) {
                    throw e;
                }
            if (data != null)
                map.put(cyEdge, data);
        }
        return map;
    }

}
