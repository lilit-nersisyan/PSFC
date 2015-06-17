package org.cytoscape.psfc.logic.structures;
/**
 * PUBLIC CLASS Graph
 *
 * Graph class is for keeping the topology of the network, and references of its entities -
 * Nodes and Edges.
 * Functions?
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.gui.enums.ExceptionMessages;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Graph {

    private TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int freeID = 0;
    private DefaultDirectedWeightedGraph<Node, Edge> jgraph;

    //Cytoscape-related fields
    private CyNetwork network;
    private BiMap<Node, CyNode> nodeCyNodeMap;
    private BiMap<Edge, CyEdge> edgeCyEdgeMap;


    /**
     * Empty constructor for Graph.
     */
    public Graph() {
        jgraph = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
        nodeCyNodeMap = HashBiMap.create();
        edgeCyEdgeMap = HashBiMap.create();
    }


    /**
     * Creates  a graph with specifiied number of vertices
     *
     * @param order number of vertices
     */
    public Graph(int order) {
        if (order < 0)
            throw new IllegalArgumentException();
        jgraph = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
        nodeCyNodeMap = HashBiMap.create();
        edgeCyEdgeMap = HashBiMap.create();
        for (int i = 0; i < order; i++) {
            addNode();
        }
    }

    /**
     * Graph constructor with full set of fields.
     *
     * @param nodes
     * @param edges
     * @param freeID
     * @param jgraph
     * @param network
     * @param nodeCyNodeMap
     */
    private Graph(TreeMap<Integer, Node> nodes, ArrayList<Edge> edges, int freeID, DefaultDirectedWeightedGraph<Node, Edge> jgraph, CyNetwork network, BiMap<Node, CyNode> nodeCyNodeMap, BiMap<Edge, CyEdge> edgeCyEdgeBiMap) {
        this.nodes = nodes;
        this.edges = edges;
        this.freeID = freeID;
        this.jgraph = jgraph;
        this.network = network;
        this.nodeCyNodeMap = nodeCyNodeMap;
        this.edgeCyEdgeMap = edgeCyEdgeMap;
    }

    /**
     * Returns the number of vertices in the Graph.
     *
     * @return number of vertices
     */
    public int getOrder() {
        return nodes.size();
    }

    /**
     * Returns the number of vertices in the Graph
     *
     * @return number of vertices
     */
    public int getSize() {
        return edges.size();
    }

    /**
     * Create and return new Node with available free ID.
     *
     * @return created node
     */
    public Node addNode() {
        Node node = new Node(freeID);
        nodes.put(node.getID(), node);
        freeID++;
        jgraph.addVertex(node);
        return node;
    }


    /**
     * Create and return a new <code>Node</code> and assign given <code>CyNode</code>
     * reference ot it.
     *
     * @param cyNode the CyNode reference of the Node
     * @return created Node.
     */
    public Node addNode(CyNode cyNode) {
        if (cyNode == null)
            throw new NullPointerException(ExceptionMessages.NullCyNode.getMessage());
        Node node = addNode();
        setCyNode(node, cyNode);
        return node;
    }

    /**
     * Checks if there the graph contains the specified node.
     *
     * @param node the node to search for
     * @return <code>true</code> if the node is present; <code>false</code> otherwise
     */
    public boolean containsNode(Node node) {
        return nodes.containsValue(node);
    }

    /**
     * Returns the next identifier which has not been assigned to any node.
     *
     * @return free ID
     */
    int getFreeID() {
        return freeID;
    }

    /**
     * Returns first encountered node with the given name
     * Returns null if no such node exists
     *
     * @param name node name
     * @return first node with the same name
     */
    public Node getNode(String name) {
        for (Node node : nodes.values()) {
            if (node.getName().equals(name))
                return node;
        }
        return null;

    }

    /**
     * Returns <code>Node</code> with given CyNode reference, or null if no such node exists.
     *
     * @param cyNode the <code>CyNode</code>reference
     * @return the node with the CyNode reference; null if no such node exists
     */
    public Node getNode(CyNode cyNode) {
        return nodeCyNodeMap.inverse().get(cyNode);
    }

    /**
     * Returns <code>Edge</code> with given CyEdge reference, or null if no such edge exists.
     *
     * @param cyEdge the <code>CyEdge</code>reference
     * @return the edge with the CyEdge reference; null if no such edge exists
     */
    public Edge getEdge(CyEdge cyEdge) {
        return edgeCyEdgeMap.inverse().get(cyEdge);
    }

    /**
     * Returns <code>CyNode</code> which is references by the given <code>Node</code>
     * or null if no such reference exists or if the given node does not exist in the graph.
     *
     * @param node Node containing the CyNode reference
     * @return CyNode referenced by the node; or null if no reference exists
     */
    public CyNode getCyNode(Node node) {
        return nodeCyNodeMap.get(node);
    }

    /**
     * Returns <code>CyEdge</code> which is references by the given <code>Edge</code>
     * or null if no such reference exists or if the given edge does not exist in the graph.
     *
     * @param edge
     * @return CyEdge referenced by the edge; or null if no reference exists
     */
    public CyEdge getCyEdge(Edge edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();
        if (!containsEdge(source, target))
            return null;
        if (!edgeCyEdgeMap.containsKey(edge)) {
            System.out.println(edgeCyEdgeMap);
            System.out.println("does not contain key");
            System.out.println(edge.toString());
            return null;
        }
        return edgeCyEdgeMap.get(edge);
    }


    /**
     * Return the Node with given ID.
     * If there is no node with specified ID, null is returned.
     *
     * @param ID Node identifier
     * @return <code>Node</code> if the Node was found;  <code>null</code> if no node exists with the ID
     */
    public Node getNode(int ID) {
        if (nodes.containsKey(ID))
            return nodes.get(ID);
        return null;
    }

    /**
     * Sets <code>CyNode</code> reference to the given <code>Node</code>.
     * If Node does not exist or either of the input parameters is null,
     * a value of <code>false</code> is returned, otherwise <code>true</code> is returned.
     *
     * @param node   node for which the CyNode reference should be set
     * @param cyNode the actual reference of the node
     * @return boolean success
     */
    public boolean setCyNode(Node node, CyNode cyNode) {
        if (!containsNode(node))
            return false;
        if (nodeCyNodeMap.containsKey(node))
            nodeCyNodeMap.remove(node);
        nodeCyNodeMap.put(node, cyNode);
        return true;
    }


    /**
     * Sets <code>CyEdge</code> reference to the given <code>Edge</code>.
     * If Edge does not exist or either of the input parameters is null,
     * a value of <code>false</code> is returned, otherwise <code>true</code> is returned.
     *
     * @param edge   edge for which the CyEdge reference should be set
     * @param cyEdge the actual reference of the edge
     * @return boolean success
     */
    public boolean setCyEdge(Edge edge, CyEdge cyEdge) {
        if (!containsEdge(edge.getSource(), edge.getTarget()))
            return false;
        if (edgeCyEdgeMap.containsKey(edge))
            edgeCyEdgeMap.remove(edge);
        edgeCyEdgeMap.put(edge, cyEdge);
        return true;
    }

    /**
     * Return the list of Nodes in the graph.
     *
     * @return list of Nodes
     */
    public Collection<Node> getNodes() {
        return nodes.values();
    }

    /**
     * Return <code>Integer</code> : <code>Node</code> map kept in the graph.
     *
     * @return the map
     */
    public TreeMap<Integer, Node> getNodeMap() {
        return nodes;
    }

    public BiMap<Node, CyNode> getNodeCyNodeMap() {
        return nodeCyNodeMap;
    }

    /**
     * Returns nodes with 0 in-degree.
     * If no such nodes exist (i.e. if the graph is empty), an empty set is returned.
     *
     * @return the set of Nodes with 0 in-degree.
     */
    public ArrayList<Node> getInputNodes() {
        ArrayList<Node> nodeSet = new ArrayList<Node>();
        for (Node node : nodes.values()) {
            if (jgraph.inDegreeOf(node) == 0)
                nodeSet.add(node);
        }
        return nodeSet;
    }

    /**
     * Returns the list of all the child nodes to which the input node is connected by out-edges.
     *
     * @param parentNode the Node of interest
     * @return the list of child nodes of the given Node;
     * or null if the given node does not exist in the graph.
     */
    public ArrayList<Node> getChildNodes(Node parentNode) {
        if (!containsNode(parentNode))
            return null;
        ArrayList<Node> childNodes = new ArrayList<Node>();
        for (Node node : nodes.values()) {
            if (containsEdge(parentNode, node))
                childNodes.add(node);
        }
        return childNodes;
    }

    /**
     * Returns the list of all the parent nodes which have in-edges to the input node.
     *
     * @param childNode the Node of interest
     * @return the list of nodes having input edges to the given Node;
     * or null if the given node does not exist in the graph.
     */
    public ArrayList<Node> getParentNodes(Node childNode) {
        if (!containsNode(childNode))
            return null;
        ArrayList<Node> parentNodes = new ArrayList<Node>();
        for (Node node : nodes.values()) {
            if (containsEdge(node, childNode))
                parentNodes.add(node);
        }
        return parentNodes;
    }

    /**
     * If the graph has only one input node (node with 0 in-degree) this node is returned.
     * Otherwise, a new node is created with out-edges to existing input nodes.
     *
     * @return existing or newly created unique input node : Node
     */
    public Node getOrCreateUniqueInputNode() {
        ArrayList<Node> inputNodes = getInputNodes();
        Node uniqueInputNode;
        if (inputNodes.size() > 1) {
            uniqueInputNode = addNode();
            for (Node node : inputNodes) {
                addEdge(uniqueInputNode, node);
            }
        } else if (inputNodes.size() == 1)
            uniqueInputNode = inputNodes.iterator().next();
        else
            uniqueInputNode = addNode();
        return uniqueInputNode;

    }


    /**
     * Create and return an Edge between source and target Nodes contained in the Graph.
     * If the source and targ
     *
     * @param source Node
     * @param target Node
     * @return newly created edge
     */
    public Edge addEdge(Node source, Node target) {
        if (!nodes.containsValue(source))
            return null;
        if (!nodes.containsValue(target))
            return null;
        Edge existingEdge = getEdge(source, target);
        if (existingEdge != null)
            return existingEdge;
        Edge edge = new Edge(source, target);
        edges.add(edge);
        jgraph.addEdge(source, target, edge);
        return edge;
    }

    /**
     * Sets the edgeType attribute of the specified edge equal to the given edgeType.
     * Returns true in case of success and false, if the edge does not exist.
     *
     * @param edge
     * @param edgeType
     */
    public void setEdgeType(Edge edge, String edgeType) {
        edge.setEdgeType(edgeType);
    }

    /**
     * Checks if the graph contains an edge with specified source and target nodes.
     *
     * @param source source node
     * @param target target node
     * @return <code>true</code> if an edge was found; <code>false</code> otherwise
     */
    public boolean containsEdge(Node source, Node target) {
        Edge tempEdge = new Edge(source, target);
        for (Edge edge : edges)
            if (edge.equals(tempEdge))
                return true;
        return false;
    }

    /**
     * Return Edge with specified source and target Nodes.
     * If graph does not contain such an edge, null is returned.
     *
     * @param source source node
     * @param target target node
     * @return <code>Edge</code> if exists; <code>null</code> otherwise
     */
    public Edge getEdge(Node source, Node target) {
        Edge tempEdge = new Edge(source, target);
        for (Edge edge : edges) {
            if (edge.equals(tempEdge))
                return edge;
        }
        return null;
    }

    /**
     * Return the list of edges in the graph.
     *
     * @return Edge list
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Return <code>DefaultDirectedWeightedGraph</code> instance kept in the graph.
     *
     * @return <code>DefaultDirectedWeightedGraph</code>
     */
    public DefaultDirectedWeightedGraph<Node, Edge> getJgraph() {
        return jgraph;
    }

    /**
     * Return <code>CyNetwork</code> to which the graph holds a reference.
     *
     * @return <code>CyNetwork</code>; or <code>null</code> if no CyNetwork reference exists
     */
    public CyNetwork getNetwork() {
        return network;
    }

    /**
     * Set the reference of the <code>CyNetwork</code> from which the graph was created.
     *
     * @param network CyNetwork
     */
    public void setNetwork(CyNetwork network) {
        this.network = network;
    }


    public String getSummary() {
        String summary = "";
        summary += "Number of nodes (order): " + getOrder() + "\n";
        summary += "Number of edges (size): " + getSize();
        return summary;
    }

    /**
     * Sets loopCount field of all the edges to 0.
     */
    public void resetLoopCounts() {
        for (Edge edge : edges)
            edge.setLoopCount(0);
    }

    public void removeEdge(Edge maxLoopEdge) {
        edges.remove(maxLoopEdge);
        jgraph.removeEdge(maxLoopEdge);
    }

    public ArrayList<Edge> getBackwardEdges() {
        ArrayList<Edge> backwardEdges = new ArrayList<Edge>();
        for (Edge edge : edges)
            if (edge.isBackward())
                backwardEdges.add(edge);
        return backwardEdges;
    }

    /**
     * Return nodes that have incoming edges to given node,
     * which were assigned backward orientation as parts of loops during graph sorting.
     *
     * @param node
     * @return list of nodes with incoming backward edges to given node; or empty list if such an edge does not exist.
     */
    public ArrayList<Node> getIncomingBackwardNodes(Node node) {
        ArrayList<Node> incomingBackwardNodes = new ArrayList<Node>();
        for (Node inputNode : getParentNodes(node))
            if (getEdge(inputNode, node).isBackward())
                incomingBackwardNodes.add(node);
        return incomingBackwardNodes;
    }


    public ArrayList<Node> getTargetNodes() {
        ArrayList<Node> targetNodes = new ArrayList<Node>();
        for (Node node : nodes.values()) {
            if (getChildNodes(node).isEmpty())
                targetNodes.add(node);
        }
        return targetNodes;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "\nnodes=" + nodes +
                "\nedges=" + edges +
                '}';
    }



 /*   @Override
    public Object clone(){
        Graph cGraph = new Graph(nodes.size());

        TreeMap<Integer, Node> cNodes = new TreeMap<Integer, Node>();
        for (Integer nodeID : nodes.keySet()){
            Node cNode = (Node) nodes.get(nodeID).clone();
            cNodes.put(nodeID, cNode);
        }
        ArrayList<Edge> cEdges = new ArrayList<Edge>();
        for (Edge edge : edges){
            Edge cEdge = (Edge) edge.clone();
            cEdges.add(cEdge);
        }

        Graph cGraph = new Graph(cNodes, cEdges, freeID,
                (DefaultDirectedWeightedGraph<Node, Edge>) jgraph.clone(),
                network, nodeCyNodeMap);
    }*/
}
