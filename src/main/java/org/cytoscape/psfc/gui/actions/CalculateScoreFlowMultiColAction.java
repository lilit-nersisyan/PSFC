package org.cytoscape.psfc.gui.actions;


import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.logic.algorithms.*;
import org.cytoscape.psfc.logic.parsers.RuleFilesParser;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.psfc.net.NetworkGraphMapper;
import org.cytoscape.psfc.properties.EMultiSignalProps;
import org.cytoscape.work.*;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * PUBLIC CLASS CalculateScoreFlowMultiColAction
 * <p/>
 * Processes given network and its attributes, instantiates a new PSF object and
 * initiates the task for calculating score flows of the network based on available scores,
 * rules and topology.
 */
public class CalculateScoreFlowMultiColAction extends AbstractCyAction {

    private final Properties nodeDataProps;
    private final Properties multiSignalProps;
    private final PSFCPanel psfcPanel;
    private final Properties loopHandlingProps;
    private final ArrayList<CyColumn> selectedNodeDataColumns;
    private CyNetwork network;
    private CyColumn edgeTypeColumn;
    private CyColumn nodeLevelColumn;
    private CyColumn nodeFunctionColumn;
    private CyColumn edgeIsBackwardColumn;
    private File edgeTypeRuleNameConfigFile;
    private File ruleConfigFile;
    private String networkName;
    private boolean calculateSignificance;
    private HashMap<Integer, HashMap<Node, Double>> levelNodeSignalMap; //to be used by backupscorestask, since the signals may later be updtaed by bootstrap
    MyTaskObserver taskObserver = new MyTaskObserver();


    private Properties bootstrapProps;
    private PSF psf;
    private boolean done = false;
    HashMap<Integer, HashMap<CyNode, Double>> levelCyNodeScoreMap =
            new HashMap<Integer, HashMap<CyNode, Double>>();
    private File backupDir;


    public CalculateScoreFlowMultiColAction(CyNetwork network,
                                            CyColumn edgeTypeColumn,
                                            ArrayList<CyColumn> selectedNodeDataColumns,
                                            CyColumn nodeLevelColumn,
                                            CyColumn isOperatorColumn,
                                            CyColumn nodeFunctionColumn,
                                            CyColumn edgeIsBackwardColumn,
                                            File edgeTypeRuleNameConfigFile,
                                            File ruleConfigFile,
                                            Properties nodeDataProperties,
                                            Properties multiSignalProps,
                                            Properties loopHandlingProps,
                                            boolean calculateSignificance,
                                            PSFCPanel psfcPanel) {
        super("Calculate score flow");

        this.network = network;
        this.edgeTypeColumn = edgeTypeColumn;
        this.selectedNodeDataColumns = selectedNodeDataColumns;
        this.nodeLevelColumn = nodeLevelColumn;
        this.nodeFunctionColumn = nodeFunctionColumn;
        this.edgeIsBackwardColumn = edgeIsBackwardColumn;
        this.edgeIsBackwardColumn = edgeIsBackwardColumn;
        this.edgeTypeRuleNameConfigFile = edgeTypeRuleNameConfigFile;
        this.ruleConfigFile = ruleConfigFile;
        this.nodeDataProps = nodeDataProperties;
        this.multiSignalProps = multiSignalProps;
        this.loopHandlingProps = loopHandlingProps;
        this.psfcPanel = psfcPanel;
        this.calculateSignificance = calculateSignificance;


        this.networkName = network.getRow(network).get(CyNetwork.NAME, String.class);


    }

    public void setBackupDir(File backupDir) {
        this.backupDir = backupDir;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedNodeDataColumns.size() == 1) {
            done = false;
            CyColumn column = selectedNodeDataColumns.get(0);
            if(backupDir == null)
                backupDir = PSFCActivator.getPSFCDir();
            File scoreBackupFile = new File(backupDir, networkName + "_" + column.getName() + ".xls");

            final CalculateScoreFlowTask psfTask = new CalculateScoreFlowTask(column);
            final BackupResultsTask backupResultsTask = new BackupResultsTask(column, scoreBackupFile);
            TaskIterator taskIterator = new TaskIterator();

            taskIterator.append(psfTask);
            if (calculateSignificance) {
                final CalculateSignificanceTask calculateSignificanceTask =
                        new CalculateSignificanceTask(column);
                taskIterator.append(calculateSignificanceTask);
            }
            taskIterator.append(backupResultsTask);
            PSFCActivator.taskManager.execute(taskIterator);

        } else {
            TaskIterator taskIterator = new TaskIterator();
            boolean execute = true;
            for (CyColumn column : selectedNodeDataColumns) {
                if(backupDir == null)
                    backupDir = PSFCActivator.getPSFCDir();
                File scoreBackupFile = new File(backupDir, networkName + "_" + column.getName() + ".xls");

                boolean success = false;
                if (scoreBackupFile.exists()) {
                    success = scoreBackupFile.delete();
                    if (!success) {
                        JOptionPane.showMessageDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
                                "Could not delete the file "
                                        + scoreBackupFile.getAbsolutePath() + ". Probably it's in use.",
                                "PSFC error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                final CalculateScoreFlowTask psfTask = new CalculateScoreFlowTask(column);
                final BackupResultsTask backupResultsTask = new BackupResultsTask(column, scoreBackupFile);

                taskIterator.append(psfTask);
                if (calculateSignificance)

                {
                    final CalculateSignificanceTask calculateSignificanceTask =
                            new CalculateSignificanceTask(column);
                    taskIterator.append(calculateSignificanceTask);
                }
                taskIterator.append(backupResultsTask);

            }
            final GenerateSummaryFileTask generateSummaryFileTask = new GenerateSummaryFileTask();
            taskIterator.append(generateSummaryFileTask);
            if (execute)
                PSFCActivator.taskManager.execute(taskIterator, taskObserver);
        }
    }

    private class GenerateSummaryFileTask extends AbstractTask {
        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            try {
                taskMonitor.setTitle("PSFC Generate summary file task");
                taskMonitor.setStatusMessage("Generating summary file");
                //create a summary backup file
                String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
                File summaryFile = new File(PSFCActivator.getPSFCDir(), networkName + "_summary.xls");
                HashMap<CyColumn, ArrayList<Double>> columnScoreMap = new HashMap<>();
                HashMap<CyColumn, ArrayList<Double>> columnPvalMap = new HashMap<>();
                ArrayList<String> rownames = new ArrayList<>();
                boolean firstFile = true;
                for (CyColumn nextColumn : selectedNodeDataColumns) {
                    if (cancelled)
                        break;
                    if(backupDir == null)
                        backupDir = PSFCActivator.getPSFCDir();
                    File scoreBackupFile = new File(backupDir,
                            networkName + "_" + nextColumn.getName() + ".xls");
                    ArrayList<Double> scores = new ArrayList<>();
                    ArrayList<Double> pvals = new ArrayList<>();

                    if (scoreBackupFile.exists()) {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new FileReader(scoreBackupFile));
                        } catch (FileNotFoundException e) {
                            throw new Exception("Could not find the backup file " + scoreBackupFile.getAbsolutePath()
                                    + " when creating the summary file. Reason: "
                                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                        }
                        String line;
                        int ncol = 0, scoreInd = 0, pvalInd = 0;
                        try {
                            while ((line = reader.readLine()) != null) {
                                String[] tokens = line.split("\t");
                                if (line.startsWith("SUID")) {
                                    ncol = tokens.length;
                                    if (ncol < 2)
                                        throw new Exception("PSFC:: Error while generating summary file. Number of columns in the backup file "
                                                + scoreBackupFile + " was less than 2");
                                    scoreInd = ncol - 2;
                                    pvalInd = ncol - 1;
                                } else {
                                    if (firstFile) {
                                        rownames.add(tokens[0] + "\t" + tokens[1]);
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
                        } catch (Exception e) {
                            throw new Exception("Exception reading the backup file "
                                    + scoreBackupFile.getAbsolutePath() + " when generating the summary file. Reason: "
                                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                        }
                        try {
                            reader.close();
                        } catch (IOException e) {
                            throw new Exception("Exception reading the backup file "
                                    + scoreBackupFile.getAbsolutePath() + " when generating the summary file. Reason: "
                                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                        }
                        columnScoreMap.put(nextColumn, scores);
                        columnPvalMap.put(nextColumn, pvals);
                        firstFile = false;
                    } else {
                        throw new Exception("PSFC: Error while generating summary file. The backup file: "
                                + scoreBackupFile.getAbsolutePath() + " does not exist");
                    }
                }
                if (!cancelled) {
                    PrintWriter writer = new PrintWriter(summaryFile);

                    String header = "SUID\tName";
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
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            } finally {
                System.gc();
            }

        }

        @Override
        public void cancel() {
            cancelled = true;
        }

    }


    public void setBootstrapProps(Properties bootstrapProps) {
        this.bootstrapProps = bootstrapProps;
    }


    private class CalculateScoreFlowTask extends AbstractTask implements ObservableTask {
        private final CyColumn nodeDataColumn;
        boolean flowSuccess = true;
        String errorMessage;
        Graph graph;

        Thread psfThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    psf.calculateFlow();
                } catch (Exception e) {
                    flowSuccess = false;
                    errorMessage = e.getMessage();
                }
            }
        });

        public CalculateScoreFlowTask(CyColumn nodeDataColumn) {
            this.nodeDataColumn = nodeDataColumn;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC.CalculateFlowTask");
            if (nodeDataColumn == null) {
                throw new Exception("Selected Node Data column does not exist. " +
                        "\nPlease, refresh the column list and choose a valid Node Data column for pathway flow calculation.");
            }

            boolean isNumber = true;
            if (!nodeDataColumn.getType().getName().equals(Double.class.getName()))
                if (!nodeDataColumn.getType().getName().equals(Integer.class.getName()))
                    isNumber = false;
            if (!isNumber) {
                throw new Exception("Illegal NodeData column: should be numeric. " +
                        "\nPlease, choose a valid column for pathway flow calculation.");
            }

            //Converting network to Graph
            taskMonitor.setStatusMessage(nodeDataColumn + ": Converting network to PSFC Graph");
            try {
                graph = NetworkGraphMapper.graphFromNetwork(network, edgeTypeColumn);
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "Could not convert network to graph. Reason: " + e.getMessage(), e);
            }
            taskMonitor.setProgress(0.1);

            //Retrieving node levels from CyTable and keeping them in graph nodes
            taskMonitor.setStatusMessage(nodeDataColumn + " :Retrieving node levels");
            HashMap<CyNode, Integer> cyNodeLevelMap;
            try {
                cyNodeLevelMap = getCyNodeLevelMap();
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "Node levels could not be retrieved from the column " + nodeLevelColumn.getName());
            }
            try {
                GraphManager.assignNodeLevels(graph, cyNodeLevelMap);
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + e.getMessage(), e);
            }

            //Retrieving edge isBackward values CyTable and keeping them in graph edges
            taskMonitor.setStatusMessage(nodeDataColumn + " :Retrieving edge isBackward values");
            HashMap<CyEdge, Boolean> cyEdgeIsBackwardMap;
            try {
                cyEdgeIsBackwardMap = getCyEdgeIsBackwardMap();
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "Edge isBackward values could not be retrieved from the column " + edgeIsBackwardColumn.getName());
            }
            try {
                GraphManager.assignEdgeIsBackwards(graph, cyEdgeIsBackwardMap);
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }

            // Retrieving node scores from CyTable and keeping them in graph nodes
            taskMonitor.setStatusMessage(nodeDataColumn + " :Retrieving node scores");
            HashMap<CyNode, Double> cyNodeDataMap;
            try {
                cyNodeDataMap = getCyNodeDataMap();
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "Node scores could not be retrieved from the column " + nodeDataColumn.getName());
            }
            try {
                GraphManager.assignNodeValues(graph, cyNodeDataMap);
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + e.getMessage(), e);
            }

            // Retrieving node scores from CyTable and keeping them in graph nodes
            if(nodeFunctionColumn != null) {
                taskMonitor.setStatusMessage(nodeDataColumn + ": Retrieving node functions");
                HashMap<CyNode, String> cyNodeFunctionMap;
                try {
                    cyNodeFunctionMap = getCyNodeFunctionMap();
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + "Node functions could not be retrieved from the column " + nodeFunctionColumn.getName());
                }
                try {
                    GraphManager.assignNodeFunctions(graph, cyNodeFunctionMap);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + e.getMessage(), e);
                }
                taskMonitor.setProgress(0.2);
            }

            //If edge weights are user supplied, take them from CyTable and keep in graph edges
            if (multiSignalProps.get(EMultiSignalProps.SplitSignalRule.getName()) == EMultiSignalProps.SPLIT_WEIGHTS) {
                Object obj = multiSignalProps.get(EMultiSignalProps.EdgeWeightsAttribute.getName());
                if (obj == null)
                    throw new Exception("PSFC::Exception " + "Could not find " + EMultiSignalProps.EdgeWeightsAttribute.getName() + " property");
                CyColumn cyEdgeWeightColumn = (CyColumn) obj;
                HashMap<CyEdge, Double> cyEdgeWeightMap = getCyEdgeWeightMap(cyEdgeWeightColumn);
                try {
                    GraphManager.assignEdgeWeights(graph, cyEdgeWeightMap);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + e.getMessage(), e);
                }
            }

            //If edge order is given by ranks, take them from CyTable and keep in graph edges
            if (multiSignalProps.get(EMultiSignalProps.SignalProcessingOrder.getName()) == EMultiSignalProps.ORDER_RANKS) {
                Object obj = multiSignalProps.get(EMultiSignalProps.EdgeRankAttribute.getName());
                if (obj == null)
                    throw new Exception("PSFC::Exception " + "Could not find " + EMultiSignalProps.EdgeRankAttribute.getName() + " property");
                CyColumn cyEdgeRankColumn = (CyColumn) obj;
                HashMap<CyEdge, Integer> cyEdgeRankMap = getCyEdgeRankMap(cyEdgeRankColumn);
                try {
                    GraphManager.assignEdgeRanks(graph, cyEdgeRankMap);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + e.getMessage(), e);
                }
            }

            //Delete previous signal and p value columns if present
            taskMonitor.setStatusMessage(nodeDataColumn + ": Deleting PSFC columns, if previously present.");
            int deleted;
            deleted = NetworkCyManager.deleteAttributeColumnByPrefix(network.getDefaultNodeTable(), EColumnNames.PSFC_NODE_SIGNAL.getName());
            deleted = NetworkCyManager.deleteAttributeColumnByPrefix(network.getDefaultEdgeTable(), EColumnNames.PSFC_EDGE_SIGNAL.getName());
            deleted = NetworkCyManager.deleteAttributeColumn(network.getDefaultNodeTable(), EColumnNames.PSFC_PVAL.getName());
            deleted = NetworkCyManager.deleteAttributeColumn(network.getDefaultNodeTable(), EColumnNames.PSFC_FINAL.getName());
            //Instantiate new PSF class with generated graph, and perform pathway flow calculation
            try {
                HashMap<String, String> rulesMap;
                RuleFilesParser ruleFilesParser = new RuleFilesParser();
                try {
                    rulesMap = ruleFilesParser.parseSimpleRules(edgeTypeRuleNameConfigFile, ruleConfigFile);
                } catch (Exception e) {
                    throw e;
                }
                psf = new PSF(graph,
                        rulesMap,
                        PSFCActivator.getLogger());
                psf.setNodeDataProps(nodeDataProps);
                psf.setMultiSignalProps(multiSignalProps);
                psf.setLoopHandlingProps(loopHandlingProps);

                PSFCActivator.getLogger().info("\n################\n################");
                System.out.println("\nPSFC:: Flow calculation on clumn " + nodeDataColumn.getName() + "\n");
                String date = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
                PSFCActivator.getLogger().info(date);
                System.out.println("PSFC:: Date: " + date + "\n");
                PSFCActivator.getLogger().info("Action: score flow calculation");
                String networkTitle = network.getRow(network).get(CyNetwork.NAME, String.class);
                PSFCActivator.getLogger().info("Network: " + networkTitle);
                System.out.println("PSFC:: Network: " + networkTitle + "\n");
                PSFCActivator.getLogger().info("Graph summary: " + graph.getSummary());
                PSFCActivator.getLogger().info("edgeTypeRuleNameConfigFile: " + edgeTypeRuleNameConfigFile.toString());
                PSFCActivator.getLogger().info("ruleConfigFile: " + ruleConfigFile.toString());

                taskMonitor.setStatusMessage(nodeDataColumn + ": Calculating flow scores");


                try {
                    psfThread.run();
                    if (!flowSuccess) {
                        throw new Exception(errorMessage);
                    }
                } catch (Exception e) {
                    PSFCActivator.getLogger().error("Error occurred while flow score calculation. Reason: "
                            + e.getMessage(), e);
                    throw new Exception(e.getMessage().startsWith("PSFC") ? e.getMessage()
                            : "PSFC::" + e.getMessage(), e);
                }
                taskMonitor.setProgress(0.7);
                PSFCActivator.getLogger().debug("Pathway flow signals successfully updated");

                //Process output signals for exporting

                //Process node signals
                //A separate column of score attributes for each level will be kept in this map
                HashMap<CyNode, Double> prevNodeMap = new HashMap<CyNode, Double>();
                for (Node node : psf.getGraph().getNodes()) {
                    prevNodeMap.put(psf.getGraph().getCyNode(node), node.getValue());
                }
                for (int level : psf.getLevelNodeSignalMap().keySet()) {
                    HashMap<CyNode, Double> currentLevelCyNodeScoreMap = new HashMap<CyNode, Double>();
                    levelCyNodeScoreMap.put(level, currentLevelCyNodeScoreMap);
                    currentLevelCyNodeScoreMap.putAll(prevNodeMap);
                    for (Node node : psf.getLevelNodeSignalMap().get(level).keySet()) {
                        CyNode cyNode = psf.getGraph().getCyNode(node);
                        double score = psf.getLevelNodeSignalMap().get(level).get(node);
                        currentLevelCyNodeScoreMap.remove(cyNode);
                        currentLevelCyNodeScoreMap.put(cyNode, score);
                    }
                    prevNodeMap = currentLevelCyNodeScoreMap;
                }


                //Process edge signals
                HashMap<Integer, HashMap<Edge, Double>> levelEdgeSignalMap = psf.getLevelEdgeSignalMap();
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
                taskMonitor.setStatusMessage(nodeDataColumn + ": Importing updated signals to Cytoscape");

                //nodes
                try {
                    for (int level : levelCyNodeScoreMap.keySet()) {
                        NetworkCyManager.setNodeAttributesFromMap(network,
                                levelCyNodeScoreMap.get(level), EColumnNames.PSFC_NODE_SIGNAL.getName() + level, Double.class);
                    }
                    NetworkCyManager.setNodeAttributesFromMap(network,
                            levelCyNodeScoreMap.get(levelCyNodeScoreMap.size() - 1), EColumnNames.PSFC_FINAL.getName(), Double.class);
                    PSFCActivator.getLogger().debug("Mapped CyNode score values to Cytoscape attributes");
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + "Error occurred while mapping CyNode scores " +
                            "to Cytoscape attributes. Reason : "
                            + e.getMessage(), e);
                }

                //edges
                try {
                    for (int level : levelCyEdgeScoreMap.keySet()) {
                        NetworkCyManager.setEdgeAttributesFromMap(network,
                                levelCyEdgeScoreMap.get(level), EColumnNames.PSFC_EDGE_SIGNAL.getName() + level, Double.class);
                    }
                    PSFCActivator.getLogger().debug("Mapped CyEdge signal values to Cytoscape attributes");
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + "Error occurred while mapping CyEdge scores " +
                            "to Cytoscape attributes. Reason : "
                            + e.getMessage(), e);
                }

                // Trigger psfcPanel to update Flow Visualization Panel
                psfcPanel.setVisualizationComponents(network, levelCyNodeScoreMap, levelCyEdgeScoreMap);
                taskMonitor.setStatusMessage(nodeDataColumn + ": Flow calculation task complete");
                taskMonitor.setProgress(1);
                if (psf != null)
                    levelNodeSignalMap = psf.getLevelNodeSignalMap();
                else
                    throw new Exception("PSFC:: PSF object was not created: see the log for details");
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "PSFC::Exception " + e.getMessage());
            } finally {
                PSFCActivator.getLogger().debug("PSFC Score Flow calculation finished");

                System.gc();
            }

        }


        @Override
        public void cancel() {
            psf.setCancelled(true);
            flowSuccess = false;
            System.gc();
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

        private HashMap<CyEdge, Boolean> getCyEdgeIsBackwardMap() {
            HashMap<CyEdge, Boolean> cyEdgeIsBackwardMap = new HashMap<CyEdge, Boolean>();

            for (Object cyEdgeObj : network.getEdgeList()) {
                CyEdge cyEdge = (CyEdge) cyEdgeObj;
                CyRow row = network.getDefaultEdgeTable().getRow(cyEdge.getSUID());
                boolean isBackward = row.get(edgeIsBackwardColumn.getName(), Boolean.class);
                cyEdgeIsBackwardMap.put(cyEdge, isBackward);
            }
            return cyEdgeIsBackwardMap;
        }

        private HashMap<CyNode, String> getCyNodeFunctionMap() throws Exception {
            if(nodeFunctionColumn == null)
                return null;
            HashMap<CyNode, String> map = new HashMap<>();
            String attr = nodeFunctionColumn.getName();
            boolean hasFunctionColumn = false;
            for(CyColumn column : network.getDefaultNodeTable().getColumns()){
                if(column.getName().equals(nodeFunctionColumn.getName()))
                    hasFunctionColumn = true;
            }
            if(!hasFunctionColumn)
                return null;
            for (Object cyNodeObj : network.getNodeList()) {
                CyNode cyNode = (CyNode) cyNodeObj;
                CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                Object obj;
                try {
                    obj = row.get(attr, nodeFunctionColumn.getType());
                } catch (Exception e) {
                    throw new Exception("Error while retrieving node functions from row " +
                            row.toString() + ". Reason: " + e.getMessage());
                }
                String function = null;
                if (obj == null)
                    continue;
                if (obj instanceof String) {
                    String objV = (String) obj;
                    if (!objV.equals(""))
                        function = objV;
                } else {
                    throw new Exception("the function column " + nodeFunctionColumn.getName() +
                            " must be String");
                }
                map.put(cyNode, function);
            }
            return map;
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


        @Override
        public <R> R getResults(Class<? extends R> aClass) {
            return null;
        }


    }

    private class BackupResultsTask extends AbstractTask implements ObservableTask {
        private final CyColumn nodeDataColumn;
        private final File scoreBackupFile;
        boolean cancelled = false;

        public BackupResultsTask(CyColumn nodeDataColumn, File scoreBackupFile) {
            this.nodeDataColumn = nodeDataColumn;
            this.scoreBackupFile = scoreBackupFile;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            if (levelNodeSignalMap == null) {
                throw new Exception("No node signals available for score backup");
            }
            taskMonitor.setStatusMessage("Exporting updated signals to file " + scoreBackupFile.getAbsolutePath());
            try {
                PrintWriter writer = new PrintWriter(scoreBackupFile);
                String columnNames = "SUID\tName\tLevel";
                for (int level : psf.getLevelNodesMap().keySet()) {
                    columnNames += "\tsignal_" + level;
                }


                HashMap<Node, Double> targetPValueMap = null;
                columnNames += "\tpval";
                if (calculateSignificance) {
                    targetPValueMap = psf.getTargetPValueMap();
                } else {
                    targetPValueMap = new HashMap<Node, Double>();
                    for(Node node : psf.getGraph().getNodes()){
                        targetPValueMap.put(node, Double.NaN);
                    }
                }
                columnNames += "\n";
                writer.append(columnNames);
                double score;
                for (Node node : psf.getGraph().getNodes()) {
                    if (cancelled)
                        break;
                    CyNode cyNode = psf.getGraph().getCyNode(node);
                    String line = cyNode.getSUID().toString() + "\t"
                            + node.getName() + "\t"
                            + node.getLevel();

                    for (Integer level : psf.getLevelNodesMap().keySet()) {
                        if (level >= node.getLevel()) {
//                            score = node.getSignal();
                            score = levelNodeSignalMap.get(node.getLevel()).get(node);
                        } else
                            score = node.getValue();

                        line += "\t" + score;
                    }
                    if (targetPValueMap != null && !targetPValueMap.isEmpty())
                        if (targetPValueMap.containsKey(node))
                            line += "\t" + targetPValueMap.get(node);
                    line += "\n";
                    writer.append(line);
                }
                writer.close();
                PSFCActivator.getLogger().debug("Written flow scores to file "
                        + scoreBackupFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                throw new Exception("PSFC::Exception " + "Problem writing node scores to file "
                        + scoreBackupFile.getAbsolutePath()
                        + ". Reason: " + e.getMessage(), e);
            } finally {
                done = true;
                System.gc();
            }
        }


        @Override
        public void cancel() {
            cancelled = true;
            System.gc();
        }

        @Override
        public <R> R getResults(Class<? extends R> aClass) {
            return null;
        }
    }

    private class CalculateSignificanceTask extends AbstractTask implements ObservableTask {
        private final CyColumn nodeDataColumn;
        TaskMonitor taskMonitor;
        Bootstrap bootstrap;

        public CalculateSignificanceTask(CyColumn nodeDataColumn) {
            this.nodeDataColumn = nodeDataColumn;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC.CalculateSignificanceTask");
            taskMonitor.setProgress(0);
            String prop = "";

            int numOfSamplings;
            try {
                prop = bootstrapProps
                        .getProperty(Bootstrap.NUMOFSAMPLINGSPROP);
                numOfSamplings = Integer.parseInt(prop);
            } catch (NumberFormatException e) {

                throw new Exception("PSFC::Exception " + "Unable to parse integer " + prop);
            }
            taskMonitor.setStatusMessage(nodeDataColumn + ": Performing "
                    + numOfSamplings + " bootstrap cycles.");

            if(numOfSamplings != 0) {
                int samplingType;
                File expMatrixFile;

                try {
                    prop = bootstrapProps
                            .getProperty(Bootstrap.SAMPLINGTYPEPROP);
                    samplingType = Integer.parseInt(prop);
                } catch (NumberFormatException e) {
                    throw new Exception("PSFC::Exception " + "Unable to parse integer " + prop);
                }
                if (samplingType == Bootstrap.SAMPLECENTRIC)
                    bootstrap = new BootstrapSampleCentric(psf, numOfSamplings, PSFCActivator.getLogger());
                else {
                    prop = bootstrapProps.getProperty(Bootstrap.EXPMATRIXFILE);
                    expMatrixFile = new File(prop);
                    if (!expMatrixFile.exists()) {
                        throw new Exception("PSFC::Exception " + "Expression matrix file" + prop + "does not exist");
                    }
                    bootstrap = new BootstrapGeneCentric(psf, numOfSamplings, expMatrixFile, PSFCActivator.getLogger());
                }
                bootstrap.setTaskMonitor(taskMonitor);
                try {
                    HashMap<Node, Double> targetPValueMap = bootstrap.performBootstrap();
                    psf.setTargetPValueMap(targetPValueMap);
                    Graph graph = psf.getGraph();
                    HashMap<CyNode, Double> cyNodePValueMap = new HashMap<CyNode, Double>();
                    for (Node node : targetPValueMap.keySet()) {
                        cyNodePValueMap.put(graph.getCyNode(node), targetPValueMap.get(node));
                    }

                    NetworkCyManager.setNodeAttributesFromMap(network, cyNodePValueMap, EColumnNames.PSFC_PVAL.getName(), Double.class);
                    taskMonitor.setStatusMessage(nodeDataColumn + ": Bootstrap significance calculation complete");
                    taskMonitor.setProgress(1);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + "Problem performing bootstrap significance calculation " + e.getMessage());
                } finally {
                    System.gc();
                }
            } else {
                System.gc();
            }
        }

        @Override
        public void cancel() {
            if (bootstrap != null)
                bootstrap.setCancelled(true);
            cancelled = true;
            System.gc();
        }

        @Override
        public <R> R getResults(Class<? extends R> aClass) {
            return null;
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
