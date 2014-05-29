package org.cytoscape.psfc.gui.actions.net;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.HashMap;

/**
 * Created by User on 5/26/2014.
 */
public class NetworkGraphMapper {

    public static Graph graphFromNetwork(CyNetwork network){
        Graph graph = new Graph();
        HashMap<CyNode, Node> cyNodePsfNodeMap = new HashMap<CyNode, Node>();

        for (Object nodeObj : network.getNodeList()) {
            CyNode cyNode = (CyNode) nodeObj;
            Node psfNode = graph.addNode();
            psfNode.setName(network.getDefaultNodeTable().getRow(cyNode.getSUID()).get(CyNetwork.NAME,String.class));
            cyNodePsfNodeMap.put(cyNode, psfNode);
        }

        for(Object edgeObj : network.getEdgeList()){
            CyEdge cyEdge = (CyEdge) edgeObj;
            Node psfSource = cyNodePsfNodeMap.get(cyEdge.getSource());
            Node psfTarget = cyNodePsfNodeMap.get(cyEdge.getTarget());
            graph.addEdge(psfSource, psfTarget);
        }

        return graph;
    }

}
