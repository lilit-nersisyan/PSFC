package org.cytoscape.psfc.logic.structures;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class GraphTest {
    private Graph graph;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        graph = new Graph();
    }

    @After
    public void tearDown() throws Exception {
        graph = null;
        thrown = ExpectedException.none();
    }

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(graph.getJgraph());
        assertNotNull(graph.getNodeCyNodeMap());
        assertNotNull(graph.getNodeMap());
        assertNotNull(graph.getEdges());
    }

    @Test
    public void testConstructorWithOrder() throws Exception {
        int order = 10;
        graph = new Graph(order);
        assertNotNull(graph.getJgraph());
        assertNotNull(graph.getNodeCyNodeMap());
        assertNotNull(graph.getNodeMap());
        assertNotNull(graph.getEdges());
        assert graph.getOrder() == order;
        order = -1;
        thrown.expect(IllegalArgumentException.class);
        new Graph(order);
    }

    @Test
    public void testGetOrderGetSize() throws Exception {
        assert graph.getOrder() == 0;
        assert graph.getSize() == 0;
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);
        assert graph.getOrder() == 2;
        assert graph.getSize() == 1;
    }

    @Test
    public void testAddNode() throws Exception {
        assert graph.getFreeID() == 0;
        Node node0 = graph.addNode();
        assert graph.getFreeID() == 1;

        assertTrue(graph.containsNode(node0));
        assertFalse(graph.containsNode(new Node(3)));
    }

    @Test
    public void testAddNodeWithCyNode() throws Exception {

    }

    @Test
    public void testcontainsNode() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = new Node(1);
        assertTrue(graph.containsNode(node0));
        assertFalse(graph.containsNode(node1));
        assertFalse(graph.containsNode(null));
    }

    @Test
    public void testGetFreeID() throws Exception {
        int freeID = graph.getFreeID();
        assertFalse(graph.containsNode(graph.getNode(freeID)));
    }

    @Test
    public void testGetNodeByID() throws Exception {
        Node node0 = graph.addNode();
        int id = node0.getID();
        assert node0.equals(graph.getNode(id));

    }

    @Test
    public void testGetNodeByName() throws Exception {
        Node node0 = graph.addNode();
        String name0 = "name0";
        node0.setName(name0);
        assert graph.getNode(name0).equals(node0);
        assertNull(graph.getNode("noSuchNode"));
    }

    @Test
    public void testSetCyNode() throws Exception {

    }

    @Test
    public void testGetNodesList() throws Exception {
        Collection<Node> nodes = graph.getNodesList();
        assert graph.getOrder() == nodes.size();
    }

    @Test
    public void testGetNodeMap() throws Exception {
        assertNotNull(graph.getNodeMap());
    }

    @Test
    public void testGetNodeCyNodeMap() throws Exception {
        assertNotNull(graph.getNodeCyNodeMap());
    }

    @Test
    public void testGetInputNodes() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        graph.addEdge(node1, node2);
        ArrayList<Node> inputNodes = graph.getInputNodes();
        assert inputNodes.contains(node0);
        assert inputNodes.contains(node1);
        assert !inputNodes.contains(node2);
        for (Node node : inputNodes){
            assert graph.getJgraph().inDegreeOf(node) == 0;
        }
    }

    @Test
    public void testGetChildNodes() throws Exception {
        Graph dsDAG = GraphTestCases.doubleSourceDAG();
        for (Node parentNode : dsDAG.getNodesList()){
            ArrayList<Node> childNodes = dsDAG.getChildNodes(parentNode);
            for (Node childNode : childNodes){
                //Assert that there is an edge between parentNode and each child node
                assertTrue(dsDAG.containsEdge(parentNode, childNode));
                for (Node node : dsDAG.getNodesList()){
                    //Assert that there is no other node in the graph to which there is an edge from the parent node
                    if (!childNodes.contains(node))
                        assertFalse(dsDAG.containsEdge(parentNode, node));
                }
            }
        }
    }

    @Test
    public void testGetParentNodes() throws Exception {
        Graph dsDAG = GraphTestCases.doubleSourceDAG();
        for (Node childNode : dsDAG.getNodesList()){
            ArrayList<Node> parentNodes = dsDAG.getParentNodes(childNode);
            for (Node parentNode : parentNodes){
                //Assert that there is an edge between each parent node and the childNode
                assertTrue(dsDAG.containsEdge(parentNode, childNode));
                for (Node node : dsDAG.getNodesList()){
                    //Assert that there is no other node in the graph from which there is an edge to the child node
                    if (!parentNodes.contains(node))
                        assertFalse(dsDAG.containsEdge(node, childNode));
                }
            }
        }
    }



    @Test
    public void testGetOrCreateUniqueInputNode() throws Exception {
        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        graph.addEdge(node1, node2);
        assert node1.equals(graph.getOrCreateUniqueInputNode());
        assert graph.getOrder() == 2;

        graph.addNode();
        int initOrder = graph.getOrder();
        graph.getOrCreateUniqueInputNode();
        assert graph.getOrder() == initOrder + 1;
    }

    @Test
    public void testAddEdge() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);

        assert graph.containsEdge(node0, node1);
        assert graph.containsNode(node0);
        assert graph.containsNode(node1);

        //If the edge already exists in the graph, it should just be returned
        Edge existingEdge = graph.addEdge(node0, node1);
        assert existingEdge.equals(graph.getEdge(node0, node1));

        //If a node does not exist in the graph, null should be returned
        Node notExistingNode = new Node(3);
        Edge nullEdge = graph.addEdge(notExistingNode, node0);
        assertNull(nullEdge);

        nullEdge = graph.addEdge(node0, notExistingNode);
        assertNull(nullEdge);
    }

    @Test
    public void testContainsEdge() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);
        assertFalse(graph.containsEdge(node0, new Node(3)));
    }

    @Test
    public void testGetEdge() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);
        assert graph.getEdge(node0, node1).equals(edge);
    }

    @Test
    public void testGetEdges() throws Exception {
        ArrayList<Edge> edges = graph.getEdges();
        assert edges.size() == graph.getSize();
    }

    @Test
    public void testGetJGraph() throws Exception {
        DefaultDirectedWeightedGraph jgraph = graph.getJgraph();
        assertNotNull(jgraph);
        assert graph.getOrder() == jgraph.vertexSet().size();
        assert graph.getSize() == jgraph.edgeSet().size();
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(graph.toString());
    }

}



