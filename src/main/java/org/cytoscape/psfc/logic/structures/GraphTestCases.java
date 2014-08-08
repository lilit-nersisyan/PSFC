package org.cytoscape.psfc.logic.structures;

import org.jgrapht.alg.CycleDetector;

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
        Node node3 = graph.addNode();
        graph.addEdge(graph.getNode(1), node3);
        graph.addEdge(graph.getNode(2), node3);
        Node node4 = graph.addNode();
        graph.addEdge(graph.getNode(2),node4);
        return graph;
    }

    public static Graph doubleSourceDAG(){
        Graph graph = new Graph(13);
        graph.addEdge(graph.getNode(0), graph.getNode(1));
        graph.addEdge(graph.getNode(1), graph.getNode(2));
        graph.addEdge(graph.getNode(3), graph.getNode(4));
        graph.addEdge(graph.getNode(2), graph.getNode(5));
        graph.addEdge(graph.getNode(4), graph.getNode(5));
        graph.addEdge(graph.getNode(5), graph.getNode(6));
        graph.addEdge(graph.getNode(5), graph.getNode(7));
        graph.addEdge(graph.getNode(6), graph.getNode(9));
//        graph.addEdge(graph.getNode(7), graph.getNode(9));
        graph.addEdge(graph.getNode(9), graph.getNode(10));
//        graph.addEdge(graph.getNode(9), graph.getNode(11));
        graph.addEdge(graph.getNode(7), graph.getNode(11));
        graph.addEdge(graph.getNode(4), graph.getNode(8));
        graph.addEdge(graph.getNode(8), graph.getNode(12));
        return graph;
    }


    public static Graph doubleSourceDCG(){
        Graph graph = doubleSourceDAG();
        graph.addEdge(graph.getNode(9), graph.getNode(5));
        return graph;
    }

    public static Graph doubleSourceManyLoopsDCG(){
        Graph graph = doubleSourceDAG();
        graph.addNode();
        graph.addEdge(graph.getNode(8), graph.getNode(13));
        graph.addEdge(graph.getNode(13), graph.getNode(12));
        graph.addEdge(graph.getNode(13), graph.getNode(4));
        graph.addEdge(graph.getNode(9), graph.getNode(5));
        graph.addEdge(graph.getNode(10), graph.getNode(6));
        return graph;
    }

    public static void main(String[] args) {
        CycleDetector cycleDetector = new CycleDetector(doubleSourceDCG().getJgraph());
        System.out.println(cycleDetector.findCycles());
//        GraphSort.sort(doubleSourceDCG(), GraphSort.TOPOLOGICALSORT);

    }
}
