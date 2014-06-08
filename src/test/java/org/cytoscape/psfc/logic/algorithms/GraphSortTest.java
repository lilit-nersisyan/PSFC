package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.GraphTestCases;
import org.cytoscape.psfc.logic.structures.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class GraphSortTest {
    private ArrayList<Graph> graphs = new ArrayList<Graph>();

    @Before
    public void setUp() throws Exception {
        graphs.add(GraphTestCases.singleSourceDAG());
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Checks that each node has been assigned a unique level;
     * that the levels are arranged in increasing order, starting from 0, for starting node.
     * Check that each node has at least one in-node from the downstream level,
     * and one out-node from the upstream level.
     *
     * @throws Exception
     */
    @Test
    public void testShortestPathIterator() throws Exception {
        for (Graph graph : graphs) {
            TreeMap<Integer, ArrayList<Node>> levelNodeMap = GraphSort.shortestPathIterator(graph);
            ArrayList<Node> checkedNodes = new ArrayList<Node>();
            for (int level = 0; level < levelNodeMap.size(); level++) {
                ArrayList<Node> nodes = levelNodeMap.get(level);
                assertNotNull(nodes);
                boolean hasUpstreamEdge = false;
                boolean hasDownStreamEdge = false;
                for (Node node : nodes) {
                    assertFalse(checkedNodes.contains(node));
                    if (level > 0) {
                        for (Node upstreamNode : levelNodeMap.get(level - 1))
                            if (graph.containsEdge(upstreamNode, node)) {
                                hasUpstreamEdge = true;
                                break;
                            }
                        assertTrue(hasUpstreamEdge);
                    }
                    if (level < levelNodeMap.size() - 1) {
                        for (Node downStreamNode : levelNodeMap.get(level + 1))
                            if (graph.containsEdge(node, downStreamNode)) {
                                hasDownStreamEdge = true;
                                break;
                            }
                        assertTrue(hasDownStreamEdge);
                    }
                    checkedNodes.add(node);
                }
            }
        }
    }

    @Test
    public void testClosestFirstSort() throws Exception {

    }

    @Test
    public void testBsfIterate() throws Exception {

    }
}