package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.net.NetworkGraphMapper;
import org.cytoscape.psfc.logic.algorithms.GraphSort;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by User on 5/25/2014.
 */
public class SortCurrentNetworkAction extends AbstractCyAction {
    public SortCurrentNetworkAction(){
        super("Sort current network");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Graph graph = NetworkGraphMapper.graphFromNetwork(PSFCActivator.cyApplicationManager.getCurrentNetwork());
        System.out.println(graph.toString());
//        GraphSort.bsfIterate(graph);
//        GraphSort.closestFirstSort(graph);
        TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.shortestPathIterator(graph);

    }
}
