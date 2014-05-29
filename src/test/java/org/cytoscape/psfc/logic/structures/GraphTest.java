package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphTest {
    private Graph graph;
    @Before
    public void setUp() throws Exception {
        graph = new Graph();
    }

    @After
    public void tearDown() throws Exception {
        graph = null;
    }

    @Test
    public void testCreateNode() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        assert node1.getID() != node0.getID();
        assert graph.containsNode(node0);
        assert graph.containsNode(node1);
    }

    @Test
    public void testAddEdge() throws Exception{
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);

        assert graph.containsEdge(edge);
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
    public void testGetEdge() throws Exception{
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);
        assert graph.getEdge(node0, node1).equals(edge);
    }

    @Test
    public void testContainsEdge() throws Exception {
        Node node0 = graph.addNode();
        Node node1 = graph.addNode();
        Edge edge = graph.addEdge(node0, node1);
        assertTrue(graph.containsEdge(edge));
        assertFalse(graph.containsEdge(node0, new Node(3)));
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
    public void testToString() throws Exception {
        assertNotNull(graph.toString());
    }

}