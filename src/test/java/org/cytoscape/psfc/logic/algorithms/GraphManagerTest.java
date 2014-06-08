package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.junit.Before;
import org.junit.Test;

public class GraphManagerTest {
    private Graph graph;

    @Before
    public void setUp() throws Exception {
        graph = new Graph();
        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        Edge edge = graph.addEdge(node1, node2);
    }

    @Test
    public void testIntNodesMapToCyNodeIntMap() throws Exception {

    }
}