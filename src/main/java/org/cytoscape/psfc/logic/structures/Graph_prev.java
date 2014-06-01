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

public class Graph_prev {
    private TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int freeID = 0;

    /**
     * Empty constructor for Graph.
     */
    public Graph_prev(){}

    /**
     * Create and return new Node with available free ID.
     * @return node: Node
     */
    public Node addNode(){
        Node node = new Node(freeID);
        nodes.put(node.getID(), node);
        freeID++;
        return node;
    }

    /**
     * Returns the number of vertices in the Graph.
     * @return number of vertices : int
     */
    public int getOrder(){
        return nodes.size();
    }

    /**
     * Returns the number of vertices in the Graph
     * @return number of vertices : int
     */
    public int getSize(){
        return edges.size();
    }


    /**
     * Create and return an Edge between source and target Nodes contained in the Graph.
     * If the source and targ
     *
     * @param source Node
     * @param target Node
     * @return newly created edge:Edge
     */
    public Edge addEdge(Node source, Node target){
        if (!nodes.containsValue(source))
            return null;
        if(!nodes.containsValue(target))
            return null;
        Edge existingEdge = getEdge(source, target);
        if(existingEdge != null)
            return existingEdge;
        Edge edge = new Edge(source, target);
        edges.add(edge);
        return edge;
    }

    public boolean containsEdge(Node source, Node target) {
        Edge tempEdge = new Edge(source, target);
        for (Edge edge : edges)
            if(edge.equals(tempEdge))
                return true;
        return false;
    }


    /**
     * Return Edge with specified source and target Nodes.
     * If graph does not contain such an edge, null is returned.
     *
     * @param source Node - source
     * @param target Node - target
     * @return Edge or null
     */
    public Edge getEdge(Node source, Node target) {
        Edge tempEdge = new Edge(source, target);
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

    public int getFreeID() {
        return freeID;
    }
    @Override
    public String toString(){
        return "Graph{" +
                ", \nnodes=" + nodes +
                ", \nedges=" + edges +
                '}';
    }

}
