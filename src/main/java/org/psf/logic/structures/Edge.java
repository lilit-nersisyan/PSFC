package org.psf.logic.structures;

public class Edge {
    private Node source;
    private Node target;

    public Edge(Node target, Node source) {
        this.target = target;
        this.source = source;
    }
}
