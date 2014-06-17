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
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * PUBLIC CLASS SortCurrentNetworkAction
 */
public class SortCurrentNetworkAction extends AbstractCyAction {
    public SortCurrentNetworkAction(){
        super("Sort current network");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CyNetwork network = PSFCActivator.cyApplicationManager.getCurrentNetwork();
        final SortNetworkTask task = new SortNetworkTask(network, GraphSort.SHORTESTPATHSORT);
        PSFCActivator.taskManager.execute(new TaskIterator(task));
    }

    private class SortNetworkTask extends AbstractTask {

        private CyNetwork network;
        private Graph graph;
        private int sortingAlgorithm;

        public SortNetworkTask(CyNetwork network, int sortingAlgorithm){
            this.network = network;
            this.sortingAlgorithm = sortingAlgorithm;
            this.graph = NetworkGraphMapper.graphFromNetwork(network);
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("Network sorting task");

//            GraphSort.sort(graph, sortingAlgorithm);
//            GraphSort.topologicalOrderIterator(graph);
            taskMonitor.setStatusMessage("Sorting the graph with algorithm " + sortingAlgorithm);
            TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.sort(graph,GraphSort.TOPOLOGICALSORT);
            taskMonitor.setProgress(0.5);
            PSFCActivator.getLogger().debug(levelNodeMap.toString());
            System.out.println(levelNodeMap.toString());
            taskMonitor.setStatusMessage("Mapping node levels to CyNodes");
            Map<CyNode, Integer> cyNodeLevelMap = GraphManager.intNodesMapToCyNodeIntMap(graph, levelNodeMap);
            taskMonitor.setProgress(0.7);
            try {
                taskMonitor.setStatusMessage("Setting CyNode Level attribute");
                NetworkCyManager.setNodeAttributesFromMap(network, cyNodeLevelMap, "Level", Integer.class);
                taskMonitor.setProgress(1);
            } catch (Exception e1) {
                throw new Exception(e1.getMessage());
            }
        }
    }
}
