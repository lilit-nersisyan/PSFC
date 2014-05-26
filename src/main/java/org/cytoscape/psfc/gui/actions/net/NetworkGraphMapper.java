package org.cytoscape.psfc.gui.actions.net;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 5/26/2014.
 */
public class NetworkGraphMapper {

    public static Graph graphFromNetwork(CyNetwork network){
        HashMap<Integer, Node> nodeHashMap = new HashMap<Integer, Node>();
        HashMap<CyNode, Node> cyNodePsfNodeMap = new HashMap<CyNode, Node>();

        int nodeIndex = 0;
        for (Object nodeObj : network.getNodeList()) {
            CyNode cyNode = (CyNode) nodeObj;
            Node psfNode = new Node(nodeIndex);
            nodeHashMap.put(nodeIndex,psfNode);
            cyNodePsfNodeMap.put(cyNode, psfNode);
            nodeIndex++;
        }

        ArrayList<Edge> edges = new ArrayList<Edge>();
        HashMap<CyEdge, Edge> cyEdgePsfEdgeMap = new HashMap<CyEdge, Edge>();
        for(Object edgeObj : network.getEdgeList()){
            CyEdge cyEdge = (CyEdge) edgeObj;
            Node psfSource = cyNodePsfNodeMap.get(cyEdge.getSource());
            Node psfTarget = cyNodePsfNodeMap.get(cyEdge.getTarget());
            Edge edge = new Edge(psfSource, psfTarget);
            edges.add(edge);
            cyEdgePsfEdgeMap.put(cyEdge, edge);
        }

        Graph graph = new Graph(nodeHashMap,edges);
        return graph;
    }

}
