package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.properties.ESortingAlgorithms;
import org.cytoscape.psfc.logic.algorithms.GraphManager;
import org.cytoscape.psfc.logic.algorithms.GraphSort;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.psfc.net.NetworkGraphMapper;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PUBLIC CLASS SortCurrentNetworkAction
 * Calls SortNetworkTask, which converts given network to a Graph,
 * performs a call to GraphSort.sort(...) method with with given algorithm,
 * and applies sorted network layout in Cytoscape.
 */
public class SortNetworkAction extends AbstractCyAction {
    private CyNetwork network;
    private int sortingAlgorithm;
    private boolean success = false;
    private boolean performed = false;
    private boolean changeNetworkLayout;

    public SortNetworkAction(CyNetwork network, int sortingAlgorithm,  boolean changeNetworkLayout) {
        super("Sort current network");
        this.network = network;
        this.sortingAlgorithm = sortingAlgorithm;
        this.changeNetworkLayout = changeNetworkLayout;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SortNetworkTask task = new SortNetworkTask(network, sortingAlgorithm, changeNetworkLayout);
        PSFCActivator.taskManager.execute(new TaskIterator(task));
    }

    public boolean isPerformed() {
        return performed;
    }

    public boolean isSuccess() {
        return success;
    }

    public class SortNetworkTask extends AbstractTask {

        private CyNetwork network;
        private Graph graph;
        private int sortingAlgorithm;
        private CyNetworkView networkView;
        private Collection<CyNetworkView> networkViews;
        private double xGap = 40;
        private double yGap = 20;
        private boolean changeNetworkLayout = true;

        public SortNetworkTask(CyNetwork network, int sortingAlgorithm, boolean changeNetworkLayout) {
            this.network = network;
            this.sortingAlgorithm = sortingAlgorithm;
            this.graph = NetworkGraphMapper.graphFromNetwork(network);
            this.networkView = NetworkCyManager.getNetworkView(network);
            this.networkViews = NetworkCyManager.getNetworkViews(network);
            this.changeNetworkLayout = changeNetworkLayout;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            if (network == null)
                throw new Exception("Given network was null");
            try {
                //Debugging
                taskMonitor.setTitle("PSFC.SortNetworkTask");
                PSFCActivator.getLogger().info("\n################\n################");
                PSFCActivator.getLogger().info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
                PSFCActivator.getLogger().info("Action: sorting with algorithm " + ESortingAlgorithms.getName(sortingAlgorithm));
                PSFCActivator.getLogger().info("Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));
                PSFCActivator.getLogger().info("Graph summary:\n" + graph.getSummary());
                taskMonitor.setStatusMessage("Sorting the graph with algorithm " + sortingAlgorithm);

                //Graph sorting
                if (GraphSort.cycleExists(graph))
                    taskMonitor.setStatusMessage("The supplied network contains cycles. Sorting will be performed after cycle removal.");

                TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.sort(graph, GraphSort.TOPOLOGICALSORT);

                //Debugging
                taskMonitor.setStatusMessage("Graph sorted");
                taskMonitor.setProgress(0.5);
                PSFCActivator.getLogger().debug("Levels and nodes after sorting: (node SUID : node name):");
                String mapString = "";
                for (int level : levelNodeMap.keySet()) {
                    mapString += "Level " + level + ":\n";
                    for (Node node : levelNodeMap.get(level))
                        mapString += "\t" + graph.getCyNode(node).getSUID() + ": " + node.getName() + "\n";
                }
                PSFCActivator.getLogger().debug(mapString);

                //CyAttribute mapping
                Map<CyNode, Integer> cyNodeLevelMap = GraphManager.intNodesMapToCyNodeIntMap(graph, levelNodeMap);
                NetworkCyManager.setNodeAttributesFromMap(network, cyNodeLevelMap,
                        EColumnNames.Level.getName(), Integer.class);

                //Debugging
                taskMonitor.setStatusMessage(EColumnNames.Level + " attribute values set");
                taskMonitor.setProgress(0.8);
                taskMonitor.setStatusMessage("Applying level-based layout");

                //Peforming Layout
                if(changeNetworkLayout)
                    for (CyNetworkView cyNetworkView : networkViews) {
                        assignNodeCoordinates(GraphManager.intNodesMap2IntCyNodeMap(levelNodeMap, graph), cyNetworkView);
                        cyNetworkView.updateView();
                    }


                //Debugging
                PSFCActivator.getLogger().info("Sorting-based layout applied");
                taskMonitor.setStatusMessage("Sorting task complete");
                PSFCActivator.getLogger().info("Sorting task successfully completed\n");
                taskMonitor.setProgress(1);
                success = true;
            } catch (Exception e1) {
                throw new Exception(e1.getMessage());
            } finally {
                performed = true;
                System.gc();
            }
        }
        @Override
        public void cancel(){
            GraphSort.cancelled = true;
            super.cancel();
            System.gc();
        }

        private void assignNodeCoordinates(TreeMap<Integer, ArrayList<CyNode>> levelCyNodeMap, CyNetworkView cyNetworkView)
                throws Exception {
            HashMap<CyNode, Double> nodeWidthMap = new HashMap<CyNode, Double>();
            HashMap<CyNode, Double> nodeHeightMap = new HashMap<CyNode, Double>();
            HashMap<CyNode, Double> nodeXMap = new HashMap<CyNode, Double>();
            HashMap<CyNode, Double> nodeYMap = new HashMap<CyNode, Double>();

            //Find min X and Y values
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            for (Object nodeObj : network.getNodeList()) {
                CyNode node = (CyNode) nodeObj;
                org.cytoscape.view.model.View<CyNode> nodeView = cyNetworkView.getNodeView(node);
                if (nodeView == null)
                    throw new Exception("Could not get view for node " + node.getSUID());

                double width = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH).doubleValue();
                double height = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT).doubleValue();
                double x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION).doubleValue();
                double y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION).doubleValue();

                nodeWidthMap.put(node, width);
                nodeHeightMap.put(node, height);
                nodeXMap.put(node, x);
                nodeYMap.put(node, y);

                if (x < minX)
                    minX = x;
                if (y < minY)
                    minY = y;
            }

            //Assign node X coordinates
            for (int level : levelCyNodeMap.keySet()) {
                ArrayList<CyNode> nodes = levelCyNodeMap.get(level);
                //Find the node with max width
                double maxWidth = 0;
                for (CyNode node : nodes) {
                    double width = nodeWidthMap.get(node);
                    if (width > maxWidth)
                        maxWidth = width;
                    org.cytoscape.view.model.View<CyNode> nodeView = cyNetworkView.getNodeView(node);
                    if (nodeView == null)
                        throw new Exception("Could not get view for node " + node.getSUID());
                    nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, minX);
                }
                minX += maxWidth + xGap;
            }

            //Assing node Y coordinates
            List sortedEntryList = null;
            for (int level : levelCyNodeMap.descendingKeySet()) {
                ArrayList<CyNode> cyNodes = levelCyNodeMap.get(level);
                HashMap<CyNode, Double> tempYMap = new HashMap<CyNode, Double>();

                for (CyNode cyNode : cyNodes) {
                    Node node = graph.getNode(cyNode);
                    ArrayList<Node> childNodes = graph.getChildNodes(node);
                    ArrayList<Node> incomingBackwardNodes = graph.getIncomingBackwardNodes(node);
                    childNodes.addAll(incomingBackwardNodes);
                    boolean shiftDown = false;
                    //Shift down node if it has two child nodes at the same horizontal level
                    if(childNodes.size() > 1) {
                        for (Node node1 : childNodes)
                            for (Node node2 : childNodes)
                                if (Double.compare(nodeYMap.get(graph.getCyNode(node1)), (nodeYMap.get(graph.getCyNode(node2)))) == 0)
                                    shiftDown = true;
                    }
                    //The new Y coordinate of current node is the average of Y coordinates of its child Nodes.
                    if (level != levelCyNodeMap.lastKey()) {
                        double meanY = 0;
                        int numOfChildren = 0;
                        for (Node childNode : childNodes) {
                            // If childNode is at higher level
                            boolean childAtPreviousLevels = false;
                            for (int prevLevel = level + 1; prevLevel <= levelCyNodeMap.lastKey(); prevLevel++)
                                if (levelCyNodeMap.get(prevLevel).contains(graph.getCyNode(childNode))) {
                                    childAtPreviousLevels = true;
                                    break;
                                }
                            if (childAtPreviousLevels) {
                                meanY += nodeYMap.get(graph.getCyNode(childNode));
                                numOfChildren++;
                            }
                        }
                        if (numOfChildren != 0) {
                            meanY /= numOfChildren;
                            if (shiftDown)
                                meanY += nodeHeightMap.get(graph.getCyNode(node));
                        }
                        else
                            meanY = Double.MAX_VALUE;
                        tempYMap.put(cyNode, meanY);
                    } else {
                        tempYMap.put(cyNode, nodeYMap.get(cyNode));
                    }
                }

                //Remove overlaps and assign Y coordinates to node views
                sortedEntryList = new LinkedList(tempYMap.entrySet());
                Collections.sort(sortedEntryList, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return ((Comparable) ((Map.Entry) (o1)).getValue())
                                .compareTo(((Map.Entry) (o2)).getValue());
                    }
                });

                double levelY = minY;
                CyNode minNode = null;
                if (level == levelCyNodeMap.lastKey()) {
                    Map.Entry<CyNode, Double> entry =
                            (Map.Entry<CyNode, Double>) sortedEntryList.iterator().next();
                    entry.setValue(minY);
                    levelY = minY + nodeHeightMap.get(entry.getKey()) + yGap;
                    minNode = entry.getKey();
                }
                //Sort the rest in descending order and put the nodes into sorted map

                for (Object entryObj : sortedEntryList) {
                    Map.Entry<CyNode, Double> entry = (Map.Entry<CyNode, Double>) entryObj;
                    CyNode cyNode = entry.getKey();
                    if (!cyNode.equals(minNode)) {
                        entry.setValue(levelY);
                        nodeYMap.remove(cyNode);
                        nodeYMap.put(cyNode, levelY);
                        levelY += nodeHeightMap.get(cyNode) + yGap;
                    }
                }

                //Change nodeView Y position on the fly
                for (Object entryObj : sortedEntryList) {
                    Map.Entry<CyNode, Double> entry = (Map.Entry<CyNode, Double>) entryObj;
                    CyNode cyNode = entry.getKey();
                    View<CyNode> nodeView = cyNetworkView.getNodeView(cyNode);
                    if (nodeView == null)
                        throw new Exception("Could not get view for node " + cyNode.getSUID());
                    nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, entry.getValue());
                }
            }
        }
    }
}
