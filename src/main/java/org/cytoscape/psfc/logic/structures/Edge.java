package org.cytoscape.psfc.logic.structures;

/**
 * PUBLIC CLASS Edge
 * Edge represents a directed relation between two Nodes: source and target.
 * There can be only one directed edge between two nodes.
 * Additionally, an Edge keeps an integer index as an identifier.
 *
 * Unresolved: Should it keep the reference of the Graph it belongs to?
 */
public class Edge {
    private Node source;
    private Node target;
    private int index;

    public Edge(Node target, Node source) {
        if (target == null)
            throw new NullPointerException("Edge cannot be initialized with null target Node");
        if (target == null)
            throw new NullPointerException("Edge cannot be initialized with null source Node");
        this.target = target;
        this.source = source;
    }
}
