package org.cytoscape.psfc.logic.structures;

/**
 * PUBLIC CLASS Node
 * The Node class represents a node in the graph.
 * Node a non-negative int index associated with it. The index of Node cannot be changed after it is created.
 * Node may have a double value for gene expression, expression ratios or other metrics.
 *
 * Unresolved: If not initialized, the value is set to 0.0. Should this be null?
 * Unresolved: Should it keep the reference of the graph it belongs to?
 */

public class Node {
    private int index;
    private double value;

    /**
     *
     * @param index int
     */
    public Node(int index){
        if (index < 0)
            throw new IllegalArgumentException("Negative indices not allowed");
        this.index = index;
    }

    public Node(int index, double value) {
        new Node(index);
        setValue(value);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }
}
