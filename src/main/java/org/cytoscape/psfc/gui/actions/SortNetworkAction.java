package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.PSFCActivator;
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
import java.util.*;

/**
 * PUBLIC CLASS SortCurrentNetworkAction
 */
public class SortNetworkAction extends AbstractCyAction {
    private CyNetwork network;

    public SortNetworkAction() {
        super("Sort current network");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (network == null)
            network = PSFCActivator.cyApplicationManager.getCurrentNetwork();
        final SortNetworkTask task = new SortNetworkTask(network, GraphSort.SHORTESTPATHSORT);
        PSFCActivator.taskManager.execute(new TaskIterator(task));
    }

    public void setSelectedNetwork(CyNetwork network) {
        this.network = network;
    }

    private class SortNetworkTask extends AbstractTask {

        private CyNetwork network;
        private Graph graph;
        private int sortingAlgorithm;
        private CyNetworkView networkView;
        private double xGap = 40;
        private double yGap = 20;

        public SortNetworkTask(CyNetwork network, int sortingAlgorithm) {
            this.network = network;
            this.sortingAlgorithm = sortingAlgorithm;
            this.graph = NetworkGraphMapper.graphFromNetwork(network);
            this.networkView = NetworkCyManager.getNetworkView(network);
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("Network sorting task");

//            GraphSort.sort(graph, sortingAlgorithm);
//            GraphSort.topologicalOrderIterator(graph);
            taskMonitor.setStatusMessage("Sorting the graph with algorithm " + sortingAlgorithm);
            try {
                TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.sort(graph, GraphSort.TOPOLOGICALSORT);
                taskMonitor.setProgress(0.5);
                PSFCActivator.getLogger().debug(levelNodeMap.toString());
                System.out.println(levelNodeMap.toString());
                taskMonitor.setStatusMessage("Mapping node levels to CyNodes");
                Map<CyNode, Integer> cyNodeLevelMap = GraphManager.intNodesMapToCyNodeIntMap(graph, levelNodeMap);
                taskMonitor.setProgress(0.5);

                taskMonitor.setStatusMessage("Setting CyNode Level attribute");
                NetworkCyManager.setNodeAttributesFromMap(network, cyNodeLevelMap, "Level", Integer.class);
                taskMonitor.setStatusMessage("Applying level-based layout");
                assignNodeCoordinates(GraphManager.intNodesMap2IntCyNodeMap(levelNodeMap, graph));
                networkView.updateView();
                taskMonitor.setProgress(1);


            } catch (Exception e1) {
                throw new Exception(e1.getMessage());
            }
        }

        private void assignNodeCoordinates(TreeMap<Integer, ArrayList<CyNode>> levelCyNodeMap)
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
                org.cytoscape.view.model.View<CyNode> nodeView = networkView.getNodeView(node);
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
                    org.cytoscape.view.model.View<CyNode> nodeView = networkView.getNodeView(node);
                    if (nodeView == null)
                        throw new Exception("Could not get view for node " + node.getSUID());
                    nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, minX);
                }
                minX += maxWidth + xGap;
            }

            //Assing node Y coordinates

            List sortedEntryList = null;
            for (int level : levelCyNodeMap.descendingKeySet()) {
                System.out.println(nodeYMap);
                ArrayList<CyNode> cyNodes = levelCyNodeMap.get(level);
                HashMap<CyNode, Double> tempYMap = new HashMap<CyNode, Double>();

                for (CyNode cyNode : cyNodes) {
                    Node node = graph.getNode(cyNode);
                    ArrayList<Node> childNodes = graph.getChildNodes(node);

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
                        if (numOfChildren != 0)
                            meanY /= numOfChildren;
                        else
                            meanY = Double.MAX_VALUE;
                        tempYMap.put(cyNode, meanY);
                    } else {
                        tempYMap.put(cyNode, nodeYMap.get(cyNode));
                    }

                }

                System.out.println("TempYMap: " + tempYMap);
                //Remove overlaps and assign Y coordinates to node views
                sortedEntryList = new LinkedList(tempYMap.entrySet());
                Collections.sort(sortedEntryList, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return ((Comparable) ((Map.Entry) (o1)).getValue())
                                .compareTo(((Map.Entry) (o2)).getValue());
                    }
                });
                System.out.println("EntryList: \n" + sortedEntryList);

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
                    View<CyNode> nodeView = networkView.getNodeView(cyNode);
                    if (nodeView == null)
                        throw new Exception("Could not get view for node " + cyNode.getSUID());
                    nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, entry.getValue());
                }
            }
        }
    }


}
