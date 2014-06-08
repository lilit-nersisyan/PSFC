package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.net.NetworkCyManager;
import org.cytoscape.psfc.gui.actions.net.NetworkGraphMapper;
import org.cytoscape.psfc.logic.algorithms.GraphManager;
import org.cytoscape.psfc.logic.algorithms.GraphSort;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.work.AbstractTask;
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
        Graph graph = NetworkGraphMapper.graphFromNetwork(network);
        System.out.println(graph.toString());
        TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.shortestPathIterator(graph);
        Map<CyNode, Integer> cyNodeLevelMap = GraphManager.intNodesMapToCyNodeIntMap(graph, levelNodeMap);
        try {
            NetworkCyManager.setNodeAttributesFromMap(network, cyNodeLevelMap, "Level", Integer.class);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private class SortNetworkTask extends AbstractTask {

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {

        }
    }
}
