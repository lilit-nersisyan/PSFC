package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.logic.structures.Edge;
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

    /**
     * Takes levels from given <code>CyNode</code> : <code>Integer</code> map and keeps them in
     * respective <code>Node</code>s in the graph.
     * @param graph Graph containing the Nodes to be mapped
     * @param cyNodeLevelMap map containing <code>CyNode</code> : level mapping
     * @throws Exception if a <code>Node</code> does not exist for any <code>CyNode</code>
     */
    public static void assignNodeLevels(Graph graph, HashMap<CyNode, Integer> cyNodeLevelMap) throws Exception {
        for (CyNode cyNode : cyNodeLevelMap.keySet()) {
            Node node = graph.getNode(cyNode);
            if (node == null)
                throw new Exception("No node exists for CyNode " + cyNode.getSUID());
            graph.getNode(cyNode).setLevel(cyNodeLevelMap.get(cyNode));
        }

    }

    /**
     * Takes isBackward values from given <code>CyEdge</code> : <code>Boolean</code> map and keeps them in
     * respective <code>Edge</code>s in the graph.
     * @param graph Graph containing the Nodes to be mapped
     * @param cyEdgeIsBackwardMap map containing <code>CyEdge</code> : isBackward mapping
     * @throws Exception if a <code>Edge</code> does not exist for any <code>CyEdge</code>
     */
    public static void assignEdgeIsBackwards(Graph graph, HashMap<CyEdge, Boolean> cyEdgeIsBackwardMap) throws Exception {
        for (CyEdge cyEdge : cyEdgeIsBackwardMap.keySet()) {
            Edge edge = graph.getEdge(cyEdge);
            if (edge == null)
                throw new Exception("No edge exists for CyEdge " + cyEdge.getSUID());
            graph.getEdge(cyEdge).setIsBackward(cyEdgeIsBackwardMap.get(cyEdge));
        }

    }


    /**
     * Takes values from given <code>CyNode</code> : <code>Double</code> map and keeps them in
     * respective <code>Node</code>s in the graph.
     * @param graph Graph containing the Nodes to be mapped
     * @param cyNodeDataMap map containing <code>CyNode</code> : value mapping
     * @throws Exception if a <code>Node</code> does not exist for any <code>CyNode</code>
     */
    public static void assignNodeValues(Graph graph, HashMap<CyNode, Double> cyNodeDataMap) throws Exception{
        for (CyNode cyNode : cyNodeDataMap.keySet()) {
            Node node = graph.getNode(cyNode);
            if (node == null)
                throw new Exception("No node exists for CyNode " + cyNode.getSUID());
            graph.getNode(cyNode).setValue(cyNodeDataMap.get(cyNode));
        }
    }

    /**
     * Takes the operator functions from <code>CyNode</code> : <code>String</code> map and keeps them in
     * respective <code>Node</code>s in the graph.
     * @param graph Graph containing the Nodes to be mapped
     * @param cyNodeFunctionMap map containing <code>CyNode</code> : function mapping
     * @throws Exception if a <code>Node</code> does not exist for any <code>CyNode</code>
     */
    public static void assignNodeFunctions(Graph graph, HashMap<CyNode, String> cyNodeFunctionMap) throws Exception {
        for (CyNode cyNode : cyNodeFunctionMap.keySet()){
            Node node = graph.getNode(cyNode);
            if(node == null)
                throw new Exception("No node exists for CyNode " + cyNode.getSUID());
            graph.getNode(cyNode).setFunction(cyNodeFunctionMap.get(cyNode));
        }
    }
    /**
     * Takes values from given <code>CyEdge</code> : <code>Double</code>-weight map and keeps them in
     * respective <code>Edge</code>s in the graph.
     * @param graph Graph containing the Edges to be mapped
     * @param cyEdgeWeigthMap map containing <code>CyEdge</code> : weight mapping
     * @throws Exception if a <code>Edge</code> does not exist for any <code>CyEdge</code>
     */
    public static void assignEdgeWeights(Graph graph, HashMap<CyEdge, Double> cyEdgeWeigthMap) throws Exception{
        for (CyEdge cyEdge : cyEdgeWeigthMap.keySet()) {
            CyNode cySource = cyEdge.getSource();
            CyNode cyTarget = cyEdge.getTarget();
            Node source = graph.getNode(cySource);
            Node target = graph.getNode(cyTarget);
            if (source == null || target == null)
                throw new Exception("No node exists for edge " + cyEdge.toString());
            Edge edge = graph.getEdge(source, target);
            if (edge == null)
                throw  new Exception("Nod edge exists for nodes " + source.toString() + ", " + target.toString());
            edge.setWeight(cyEdgeWeigthMap.get(cyEdge));
        }
    }

    /**
     * Takes ranks from given <code>CyEdge</code> : <code>Integer</code>-weight map and keeps them in
     * respective <code>Edge</code>s in the graph.
     * @param graph Graph containing the Edges to be mapped
     * @param cyEdgeRankMap map containing <code>CyEdge</code> : rank mapping
     * @throws Exception if a <code>Edge</code> does not exist for any <code>CyEdge</code>
     */
    public static void assignEdgeRanks(Graph graph, HashMap<CyEdge, Integer> cyEdgeRankMap) throws Exception {
        for (CyEdge cyEdge : cyEdgeRankMap.keySet()) {
            CyNode cySource = cyEdge.getSource();
            CyNode cyTarget = cyEdge.getTarget();
            Node source = graph.getNode(cySource);
            Node target = graph.getNode(cyTarget);
            if (source == null || target == null)
                throw new Exception("No node exists for edge " + cyEdge.toString());
            Edge edge = graph.getEdge(source, target);
            if (edge == null)
                throw  new Exception("Nod edge exists for nodes " + source.toString() + ", " + target.toString());
            edge.setRank(cyEdgeRankMap.get(cyEdge));
        }
    }

    /**
     * Returns the list of nodes in the graph that have the given level value.
     *
     * @param graph
     * @param level
     * @return the list of nodes in the graph having specified level value
     */
    public static ArrayList<Node> getNodesAtLevel(Graph graph, int level) {
        ArrayList<Node> nodesAtLevel = new ArrayList<Node>();
        for (Node node : graph.getNodes()){
            if(node.getLevel() == level)
                nodesAtLevel.add(node);
        }
        return nodesAtLevel;
    }

    /**
     * Returns all the edges between the nodes at given level and their parent nodes at lower level in the graph.
     *
     * @param graph
     * @param level
     * @return
     *
     */
    public static ArrayList<Edge> getIncomingEdgesAtLevel(Graph graph, int level) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        ArrayList<Node> nodes = getNodesAtLevel(graph, level);
        for (Node node : nodes){
            ArrayList<Node> parentNodes = graph.getParentNodes(node);
            for (Node parentNode : parentNodes){
//                if (parentNode.getLevel() < level){
                    Edge edge = graph.getEdge(parentNode, node);
                    if (!edges.contains(edge))
                        edges.add(edge);
//                }
            }
        }
        return edges;
    }

    public static Map<CyEdge, Boolean> getCyEdgeIsBackwardMap(Graph graph) {
        Map<CyEdge,Boolean> cyEdgeIsBackwardMap = new HashMap<CyEdge, Boolean>();
        for(Edge edge : graph.getEdges()){
            CyEdge cyEdge = graph.getCyEdge(edge);
            if(cyEdge != null)
                cyEdgeIsBackwardMap.put(cyEdge, edge.isBackward());
        }
        return cyEdgeIsBackwardMap;
    }


}
