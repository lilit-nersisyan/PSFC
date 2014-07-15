package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.logic.algorithms.PSFAlgorithms;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * PUBLIC CLASS CalculateScoreFlowAction
 * <p/>
 * Initiates the task for calculating score flows of the network based on available scores,
 * rules and topology.
 */
public class CalculateScoreFlowAction extends AbstractCyAction {

    private final int sortingAlgorithm;
    private final Properties nodeDataProps;
    private CyNetwork network;
    private CyColumn edgeTypeColumn;
    private CyColumn nodeDataColumn;
    private CyColumn nodeLevelColumn;
    private File edgeTypeRuleNameConfigFile;
    private File ruleConfigFile;
    private File scoreBackupFile;
    private String networkName;

    public CalculateScoreFlowAction(CyNetwork network,
                                    int sortingAlgorithm,
                                    CyColumn edgeTypeColumn,
                                    CyColumn nodeDataColumn,
                                    CyColumn nodeLevelColumn,
                                    File edgeTypeRuleNameConfigFile,
                                    File ruleConfigFile, Properties nodeDataProperties) {
        super("Calculate score flow");

        this.network = network;
        this.edgeTypeColumn = edgeTypeColumn;
        this.nodeDataColumn = nodeDataColumn;
        this.nodeLevelColumn = nodeLevelColumn;
        this.sortingAlgorithm = sortingAlgorithm;
        this.edgeTypeRuleNameConfigFile = edgeTypeRuleNameConfigFile;
        this.ruleConfigFile = ruleConfigFile;
        this.nodeDataProps = nodeDataProperties;

        this.networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
        this.scoreBackupFile = new File(PSFCActivator.getPSFCDir(), networkName + ".xls");
        try {
            scoreBackupFile.createNewFile();
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
            taskMonitor.setStatusMessage("Retrieving node scores");
            HashMap<CyNode, Double> cyNodeDataMap;
            try {
                cyNodeDataMap = getCyNodeDataMap();
            } catch (Exception e) {
                throw new Exception("Node scores could not be retrieved from the column " + nodeDataColumn.getName());
            }
            taskMonitor.setProgress(0.1);

            taskMonitor.setStatusMessage("Converting network to PSFC Graph");
            Graph graph;
            try {
                graph = NetworkGraphMapper.graphFromNetwork(network, edgeTypeColumn);
            } catch (Exception e) {
                throw new Exception("Could not convert network to graph. Reason: " + e.getMessage(), e);
            }
            taskMonitor.setProgress(0.2);


            try {
                taskMonitor.setStatusMessage("Retrieving node levels");
                HashMap<Integer, ArrayList<Node>> levelNodesMap = null;
                try {
                    levelNodesMap = getLevelNodesMap(graph);
                } catch (Exception e) {
                    throw new Exception("Exception while retrieving level node map. Reason: "
                            + e.getMessage(), e);
                }
                taskMonitor.setProgress(0.3);
                HashMap<Node, Double> nodeDataMap = NetworkCyManager.convertCyNodeDouble2NodeDoubleMap(graph, cyNodeDataMap);


                PSFCActivator.getLogger().info("\n################\n################");
                PSFCActivator.getLogger().info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
                PSFCActivator.getLogger().info("Action: score flow calculation");
                PSFCActivator.getLogger().info("Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));
                PSFCActivator.getLogger().info("Graph summary: " + graph.getSummary());
                PSFCActivator.getLogger().info("Score file: " + scoreBackupFile.getAbsolutePath());
                PSFCActivator.getLogger().info("edgeTypeRuleNameConfigFile: " + edgeTypeRuleNameConfigFile.toString());
                PSFCActivator.getLogger().info("ruleConfigFile: " + ruleConfigFile.toString());

                taskMonitor.setStatusMessage("Calculating flow scores");
                HashMap<Integer, HashMap<Node, Double>> nodeFlowScoreMap = null;
                try {
                    nodeFlowScoreMap = PSFAlgorithms.
                            calculateFlow(graph, nodeDataMap, levelNodesMap,
                                    edgeTypeRuleNameConfigFile, ruleConfigFile, nodeDataProps);
                } catch (Exception e) {
                    PSFCActivator.getLogger().error("Error occurred while flow score calculation. Reason: "
                            + e.getMessage(), e);
                    throw new Exception("Error occurred while flow score calculation. See PSFC log file at " +
                            PSFCActivator.getPSFCDir() + " for details");
                }
                taskMonitor.setProgress(0.7);
                PSFCActivator.getLogger().debug("Node scores successfully updated");


                taskMonitor.setStatusMessage("Exporting updated node scores to file " + scoreBackupFile.getAbsolutePath());
                //A separate column of score attributes for each level will be kept in this map
                HashMap<Integer, HashMap<CyNode, Double>> levelCyNodeScoreMap =
                        new HashMap<Integer, HashMap<CyNode, Double>>();

                HashMap<CyNode, Double> prevNodeMap = new HashMap<CyNode, Double>();
                for (Node node : nodeDataMap.keySet()) {
                    prevNodeMap.put(graph.getCyNode(node), nodeDataMap.get(node));
                }

                try {
                    PrintWriter writer = new PrintWriter(scoreBackupFile);
                    writer.append("SUID\tName\tLevel\tScore0\tScore1\n");
                    for (int level : nodeFlowScoreMap.keySet()) {
                        HashMap<CyNode, Double> currentLevelCyNodeScoreMap = new HashMap<CyNode, Double>();
                        levelCyNodeScoreMap.put(level, currentLevelCyNodeScoreMap);
                        currentLevelCyNodeScoreMap.putAll(prevNodeMap);
                        for (Node node : nodeFlowScoreMap.get(level).keySet()) {
                            CyNode cyNode = graph.getCyNode(node);
                            double score = nodeFlowScoreMap.get(level).get(node);
                            currentLevelCyNodeScoreMap.remove(cyNode);
                            currentLevelCyNodeScoreMap.put(cyNode, score);
                            writer.append(cyNode.getSUID() + "\t"
                                    + node.getName() + "\t"
                                    + level + "\t"
                                    + nodeDataMap.get(node) + "\t"
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
                taskMonitor.setProgress(0.9);
                taskMonitor.setStatusMessage("Importing updated node scores to Cytoscape");
                try {
                    for (int level : levelCyNodeScoreMap.keySet()) {
                        NetworkCyManager.setNodeAttributesFromMap(network,
                                levelCyNodeScoreMap.get(level), "psfc.score_" + level, Double.class);
                    }
                    PSFCActivator.getLogger().debug("Mapped CyNode score values to Cytoscape attributes");
                } catch (Exception e) {
                    throw new Exception("Error occured while mapping CyNode scores " +
                            "to Cytoscape attributes. Reason : "
                            + e.getMessage(), e);
                }
                taskMonitor.setStatusMessage("Flow calculation task complete");
                taskMonitor.setProgress(1);
            } catch (Exception e) {
                throw new Exception(e.getCause());
            } finally {
                PSFCActivator.getLogger().debug("PSFC Score Flow calculation finished");
                System.gc();
            }
        }

        @Override
        public void cancel() {
            super.cancel();
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

        private HashMap<Integer, ArrayList<CyNode>> getLevelCyNodesMap() {
            HashMap<Integer, ArrayList<CyNode>> levelCyNodesMap = new HashMap<Integer, ArrayList<CyNode>>();
            String levelAttr = "Level";
            for (Object cyNodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) cyNodeObj;
                CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                int level = row.get(levelAttr, Integer.class);
                if (!levelCyNodesMap.containsKey(level))
                    levelCyNodesMap.put(level, new ArrayList<CyNode>());
                levelCyNodesMap.get(level).add(cyNode);
            }
            return levelCyNodesMap;
        }

        private HashMap<Integer, ArrayList<Node>> getLevelNodesMap(Graph graph) throws Exception {
            HashMap<Integer, ArrayList<Node>> levelNodesMap = new HashMap<Integer, ArrayList<Node>>();
            String levelAttr = EColumnNames.Level.getName();
            for (Object cyNodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) cyNodeObj;
                CyRow row = null;
                try {
                    row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                } catch (Exception e) {
                    throw new Exception("Exception while retrieving CyRow for node "
                            + cyNode.getSUID() + ". Reason: "
                            + e.getMessage(), e);
                }
                int level = row.get(levelAttr, Integer.class);
                if (!levelNodesMap.containsKey(level))
                    levelNodesMap.put(level, new ArrayList<Node>());
                levelNodesMap.get(level).add(graph.getNode(cyNode));
            }
            return levelNodesMap;
        }

        private HashMap<CyEdge, String> getCyEdgeTypeMap() {
            HashMap<CyEdge, String> cyEdgeTypeMap = new HashMap<CyEdge, String>();
            String edgeTypeAttr = edgeTypeColumn.getName();
            for (Object cyEdgeObj : network.getEdgeList()) {
                CyEdge cyEdge = (CyEdge) cyEdgeObj;
                CyRow row = network.getDefaultEdgeTable().getRow(cyEdge.getSUID());
                String edgeType = row.get(edgeTypeAttr, String.class);
                cyEdgeTypeMap.put(cyEdge, edgeType);
            }
            return cyEdgeTypeMap;
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
                    throw e;
                }
                Double data = null;
                if (obj instanceof Double)
                    data = (Double) obj;
                else if (obj instanceof Integer)
                    data = new Double((Integer) obj);
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

}
