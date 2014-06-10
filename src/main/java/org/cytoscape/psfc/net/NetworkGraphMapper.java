package org.cytoscape.psfc.net;

import com.google.common.collect.HashBiMap;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

/**
 * PUBLIC CLASS NetworkGraphMapper
 *
 * This class provides static methods for entity and attribute conversion between Cytoscape
 * and PSFC. This is the FacadeController class that establishes relation between PSFC and Cytoscape.
 */
public class NetworkGraphMapper {

    /**
     * Creates a Graph object from the given CyNetwork.
     * A Node object is created for each <code>CyNode</code> and passed to the <code>Graph</code> object.
     * The name attribute of <code>CyNode</code> is passed to Node's name field.
     * A <code>CyNode</code> : <code>Node</code> map is created and kept in the Graph.
     * An Edge object is created for each <code>CyEdge</code>and passed to the <code>Graph</code> object.
     * The <code>Graph</code> keeps the reference of the <code>CyNetwork</code>.
     *
     * @param network CyNetwork to be mapped
     * @return the graph created from the given CyNetwork
     */
    public static Graph graphFromNetwork(CyNetwork network) {
        if (network == null )
            throw new NullPointerException(ExceptionMessages.NullNetwork.getMessage());
        Graph graph = new Graph();
        HashBiMap<CyNode, Node> cyNodePsfNodeMap = HashBiMap.create();


        for (Object nodeObj : network.getNodeList()) {
            CyNode cyNode = (CyNode) nodeObj;
            Node psfNode = graph.addNode();
            graph.setCyNode(psfNode, cyNode);
            psfNode.setName(network.getDefaultNodeTable().getRow(cyNode.getSUID()).get(CyNetwork.NAME, String.class));
            cyNodePsfNodeMap.put(cyNode, psfNode);
        }

        for (Object edgeObj : network.getEdgeList()) {
            CyEdge cyEdge = (CyEdge) edgeObj;
            Node psfSource = cyNodePsfNodeMap.get(cyEdge.getSource());
            Node psfTarget = cyNodePsfNodeMap.get(cyEdge.getTarget());
            graph.addEdge(psfSource, psfTarget);
        }
        graph.setNetwork(network);

        return graph;
    }




}
