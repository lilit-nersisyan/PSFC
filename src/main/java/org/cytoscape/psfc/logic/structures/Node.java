package org.cytoscape.psfc.logic.structures;

import org.cytoscape.psfc.gui.enums.ExceptionMessages;

import java.util.TreeMap;

/**
 * PUBLIC CLASS Node
 * The Node class represents a node in the graph.
 * Node a non-negative Integer ID associated with it.
 * Node may have a double value for gene expression, expression ratios or other metrics.
 * <p/>
 * Unresolved: If not initialized, the value is set to 0.0. Should this be null?
 * Unresolved: Should it keep the reference of the graph it belongs to?
 */

public class Node {

    private int ID = -1;
    private double value = 1;
    private String name = "";
    private int level = 1;
    private static String defaultValue = "1";
    private TreeMap<Integer, Double> signals = new TreeMap<Integer, Double>();

    /**
     * Creates a Node with given ID and 0.0 initial value.
     *
     * @param ID non-negative integer: should be unique identifier for the node in a graph.
     */
    Node(int ID) {
        if (ID < 0)
            throw new IllegalArgumentException(ExceptionMessages.NodeWithNegativeIndex.getMessage());
        this.ID = ID;
    }

    /**
     * Creates a Node with given ID and value.
     *
     * @param ID    non-negative integer: should be unique identifier for the node in a graph.
     * @param value may stand for expression, ratio, rank score, etc.
     */
    Node(int ID, double value) {
        if (ID < 0)
            throw new IllegalArgumentException(ExceptionMessages.NodeWithNegativeIndex.getMessage());
        this.ID = ID;
        setValue(value);
    }

    public static String getDefaultValue() {
        return defaultValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (!Double.isNaN(value))
            this.value = value;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null)
            this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node that = (Node) o;
            return that.getID() == this.ID;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ID=" + ID + "," +
                "name=" + name + "," +
                "value=" + value + "," +
                "level=" + level + "," +
                "signals=" + signals.toString() +
                "}\n";
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level >= 0)
            this.level = level;
    }

    public void setSignal(double signal, int iteration) {
        if(!Double.isNaN(signal))
            signals.put(iteration, signal);
    }

    public double getSignal(int iteration) {
        return signals.get(iteration);
    }

    /**
     * Returns the last signal value in the signals map.
     * @return
     */
    public double getSignal() {
        return signals.lastEntry().getValue();
    }
}
