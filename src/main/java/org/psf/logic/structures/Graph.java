package org.psf.logic.structures;
/**
 * Graph class is for keeping the topology of the network, and references of its entities -
 * Nodes and Edges.
 * Functions?
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    private HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();

    public Graph(HashMap<Integer, Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Graph(int[][] adjacency){
        int nrows = adjacency.length;
        int ncols = adjacency[0].length;
        if (nrows != ncols)
            throw new IllegalArgumentException("Adjacency matrix should be square");
        for (int i = 0; i < adjacency.length; i++){
            nodes.put(i, new Node(i));
            for (int j= 0; j < adjacency[0].length; j++){
                if(adjacency[i][j] > 0)
                    edges.add(new Edge(nodes.get(i),nodes.get(j)));
            }
        }
    }


    public static void main(String[] args) {
        int[][] adjacency = new int[][]{
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0}
        };
        Graph graph = new Graph(adjacency);

    }
}
