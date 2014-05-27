package org.cytoscape.psfc.logic.structures;
/**
 * PUBLIC CLASS Graph
 *
 * Graph class is for keeping the topology of the network, and references of its entities -
 * Nodes and Edges.
 * Functions?
 */

import java.util.ArrayList;
import java.util.TreeMap;

public class Graph {
    private TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int freeID = 0;

    /**
     * Empty constructor for Graph.
     */
    public Graph(){}

    /**
     * Create and return new Node with available free ID.
     * @return node: Node
     */
    public Node createNode(){
        Node node = new Node(freeID);
        nodes.put(node.getID(), node);
        freeID++;
        return node;
    }

    public Edge addEdge(Node source, Node target){
        if (!nodes.containsValue(source))
            return null;
        if(!nodes.containsValue(target))
            return null;
        Edge edge = new Edge(source, target);
        edges.add(edge);
        return edge;
    }

    @Override
    public String toString(){
        return "Graph{" +
                ", \nnodes=" + nodes +
                ", \nedges=" + edges +
                '}';
    }

    /**
     * Return Edge with specified source and target Nodes.
     * If graph does not contain such an edge, null is returned.
     *
     * @param psfSource Node - source
     * @param psfTarget Node - target
     * @return Edge or null
     */
    public Edge getEdge(Node psfSource, Node psfTarget) {
        Edge tempEdge = new Edge(psfSource, psfTarget);
        for(Edge edge : edges){
            if(edge.equals(tempEdge))
                return edge;
        }
        return null;
    }

    public TreeMap<Integer, Node> getNodeMap() {
        return nodes;
    }

    public boolean containsNode(Node node){
        return nodes.containsValue(node);
    }

    public boolean containsEdge(Edge edge){
        return edges.contains(edge);
    }
}
