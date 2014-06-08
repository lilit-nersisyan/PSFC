package org.cytoscape.psfc.logic.structures;

/**
 * PUBLIC CLASS GraphCases
 *
 * Provides a set of graph structures for testing purposes.
 */
public class GraphTestCases {

    public static Graph singleNodeGraph(){
        Graph graph = new Graph();
        graph.addNode();
        return graph;
    }

    public static Graph pairedNodeGraph(){
        Graph graph = singleNodeGraph();
        graph.addNode();
        graph.addEdge(graph.getNode(0), graph.getNode(1));
        return graph;
    }

    public static Graph threeNodeGraph(){
        Graph graph = pairedNodeGraph();
        Node node = graph.addNode();
        graph.addEdge(graph.getNode(0), node);
        return graph;
    }

    public static Graph singleSourceDAG(){
        Graph graph = threeNodeGraph();
        Node node = graph.addNode();
        graph.addEdge(graph.getNode(1), node);
        graph.addEdge(graph.getNode(2), node);
        return graph;
    }
}
