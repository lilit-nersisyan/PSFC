package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
        Node node0 = graph.createNode();
        Node node1 = graph.createNode();
        assert node1.getID() != node0.getID();
        assert graph.containsNode(node0);
        assert graph.containsNode(node1);
    }

    @Test
    public void testAddEdge() throws Exception{
        Node node0 = graph.createNode();
        Node node1 = graph.createNode();
        Edge edge = graph.addEdge(node0, node1);

        assert graph.containsEdge(edge);
        assert graph.containsNode(node0);
        assert graph.containsNode(node1);

    }

    @Test
    public void testGetEdge() throws Exception{
        Node node0 = graph.createNode();
        Node node1 = graph.createNode();
        Edge edge = graph.addEdge(node0, node1);
        assert graph.getEdge(node0, node1).equals(edge);
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(graph.toString());
    }

}