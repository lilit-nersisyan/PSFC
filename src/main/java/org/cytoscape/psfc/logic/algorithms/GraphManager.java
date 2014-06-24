package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * PUBLIC CLASS GraphManager
 *
 * This class provides static methods for manipulation with Graph class entities.
 */
public class GraphManager {

    /**
     * Converts given <code>Integer</code> : <code>CyNode</code> mapping to
     * <code>CyNode</code> : <code>Integer</code> mapping for the given Graph.
     * For each Node in the given map, the respective CyNode is retrieved from
     * the Graph and the Node's value is assigned to its CyNode.
     * If the CyNode (or Node) is not found in the Graph,
     * it is skipped.
     *
     * @param graph Graph containing the Nodes
     * @param intNodesMap the map to be converted
     * @return the converted map
     */
    public static Map<CyNode, Integer> intNodesMapToCyNodeIntMap(Graph graph,
                                                                 TreeMap<Integer, ArrayList<Node>> intNodesMap){
        HashMap<CyNode, Integer> cyNodeIntegerHashMap = new HashMap<CyNode, Integer>();
        for (Map.Entry<Integer, ArrayList<Node>> entry : intNodesMap.entrySet()){
            for (Node node : entry.getValue()){
                CyNode cyNode = graph.getNodeCyNodeMap().get(node);
                if (cyNode != null)
                    cyNodeIntegerHashMap.put(cyNode, entry.getKey());
            }
        }
        return cyNodeIntegerHashMap;
    }

    /**
     * Converts given <code>Integer</code> : <code>ArrayList<Node></code> mapping to
     * <code>Integer</code> : <code>ArrayList<CyNode></code> mapping for the given Graph.
     * For each Node in the given map, the respective CyNode is retrieved from
     * the Graph and put in the newly generated map.
     * If the CyNode (or Node) is not found in the Graph,
     * it is skipped.
     * @param intNodesMap the map to be converted
     * @param graph the graph containing the Node-CyNode mapping
     * @return converted map
     */
    public static  TreeMap<Integer, ArrayList<CyNode>>intNodesMap2IntCyNodeMap(
            TreeMap<Integer, ArrayList<Node>> intNodesMap, Graph graph){
        TreeMap<Integer, ArrayList<CyNode>> intCyNodesMap = new TreeMap<Integer, ArrayList<CyNode>>();
        for (int level : intNodesMap.keySet()){
            ArrayList<Node> nodes = intNodesMap.get(level);
            ArrayList<CyNode> cyNodes = new ArrayList<CyNode>();
            for (Node node : nodes){
                CyNode cyNode = null;
                if (graph.getNodeCyNodeMap().containsKey(node))
                    cyNode = graph.getNodeCyNodeMap().get(node);
                if (cyNode != null)
                    cyNodes.add(cyNode);
            }
            intCyNodesMap.put(level, cyNodes);
        }
        return intCyNodesMap;
    }


}
