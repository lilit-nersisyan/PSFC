package org.cytoscape.psfc.logic.structures;
/**
 * PUBLIC CLASS Graph
 *
 * Graph class is for keeping the topology of the network, and references of its entities -
 * Nodes and Edges.
 * Functions?
 */

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Graph {

    private TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int freeID = 0;
    private DefaultDirectedWeightedGraph<Node, Edge> jgraph;

    //Cytoscape-related fields
    private CyNetwork network;
    private HashMap<CyNode, Node> cyNodePsfNodeMap;

    /**
     * Empty constructor for Graph.
     */
    public Graph(){
        jgraph = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
    }

    /**
     * Create and return new Node with available free ID.
     * @return node: Node
     */
    public Node addNode(){
        Node node = new Node(freeID);
        nodes.put(node.getID(), node);
        freeID++;
        jgraph.addVertex(node);
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
        jgraph.addEdge(source, target, edge);
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

    public DefaultDirectedWeightedGraph<Node, Edge> getJgraph() {
        return jgraph;
    }

    public int getFreeID() {
        return freeID;
    }
    @Override
    public String toString(){
        return "Graph{" +
                "\nnodes=" + nodes +
                "\nedges=" + edges +
                '}';
    }

    /**
     * Returns first encountered node with the given name
     * Returns null if no such node exists
     * @param name String - node name
     * @return Node - first node with the same name
     */
    public Node getNodeByName(String name){
        for (Node node : nodes.values()){
            if(node.getName().equals(name))
                return node;
        }
        return null;

    }

    public Node getNode(int id) {
        if (nodes.containsKey(id))
            return nodes.get(id);
        return null;
    }

    /**
     * Returns nodes with 0 in-degree.
     * If no such nodes exist (i.e. if the graph is empty), an empty set is returned.
     * @return Set<Node> : the set of Nodes with 0 in-degree.
     */
    public ArrayList<Node> getInputNodes(){
        ArrayList<Node> nodeSet = new ArrayList<Node>();
        for (Node node : nodes.values()){
            if(jgraph.inDegreeOf(node) == 0)
                nodeSet.add(node);
        }
        return nodeSet;
    }

    /**
     * If the graph has only one input node (node with 0 in-degree) this node is returned.
     * Otherwise, a new node is created with out-edges to existing input nodes.
     *
     * @return existing or newly created unique input node : Node
     */
    public Node getOrCreateUniqueInputNode(){
        ArrayList<Node> inputNodes = getInputNodes();
        Node uniqueInputNode;
        if (inputNodes.size() > 0) {
            uniqueInputNode = addNode();
            for (Node node : inputNodes) {
                addEdge(uniqueInputNode, node);
            }
        } else
            uniqueInputNode = inputNodes.iterator().next();
        return uniqueInputNode;

    }

    public CyNetwork getNetwork() {
        return network;
    }

    public void setNetwork(CyNetwork network) {
        this.network = network;
    }

    public HashMap<CyNode, Node> getCyNodePsfNodeMap() {
        return cyNodePsfNodeMap;
    }

    public void setCyNodePsfNodeMap(HashMap<CyNode, Node> cyNodePsfNodeMap) {
        this.cyNodePsfNodeMap = cyNodePsfNodeMap;
    }
}
