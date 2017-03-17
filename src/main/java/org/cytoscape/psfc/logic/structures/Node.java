package org.cytoscape.psfc.logic.structures;

import org.cytoscape.psfc.DoubleFormatter;
import org.cytoscape.psfc.gui.enums.ExceptionMessages;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
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
    private TreeMap<Integer, Double> signals = new TreeMap<>();
    private String function = null;
    public static double missingDataDefault = 1;


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

    /**
     * Node constructor for complete set of fields.
     *
     * @param ID      non-negative integer: should be unique identifier for the node in a graph.
     * @param value   may stand for expression, ratio, rank score, etc.
     * @param name
     * @param level
     * @param signals
     */
    private Node(int ID, double value, String name, int level, TreeMap<Integer, Double> signals) {
        this.ID = ID;
        this.value = value;
        this.name = name;
        this.level = level;
        this.signals = signals;
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


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level >= 0)
            this.level = level;
    }

    public void setSignal(double signal, int iteration) {
        if (!Double.isNaN(signal)) {
            signals.put(iteration, signal);
        }
    }

    public TreeMap<Integer, Double> getSignals() {
        return signals;
    }

    public double getSignal(int iteration) {
        return signals.get(iteration);
    }

    /**
     * Returns the last signal value in the signals map.
     *
     * @return last signal value of the node or node value if no signals are computed
     */
    public double getSignal() {
        if (signals.isEmpty())
            return value;
        return signals.lastEntry().getValue();
    }

    /**
     * Empty the set of signals associated with the node
     */
    public void removeNodeSignals() {
        signals = new TreeMap<Integer, Double>();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node that = (Node) o;
            return that.getID() == this.ID;
        }
        return false;
    }


    private double[] fSignals() {
        if (signals.isEmpty())
            return new double[0];

        double[] fSignals = new double[signals.size()];
        int i = 0;
        for (double signal : signals.values()) {
            fSignals[i++] = DoubleFormatter.formatDouble(signal);
        }
        return  fSignals;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ID=" + ID + "," +
                "name=" + name + "," +
                "value=" + value + "," +
                "level=" + level + "," +
                "signals=" + Arrays.toString(fSignals()) +
                "\n";
    }

    @Override
    public Object clone() {
        return new Node(ID, value, name, level, signals);
    }

    public static void main(String[] args) {
        NumberFormat bigformatter = new DecimalFormat("####E0");
        double value = 1.2112026993103345E30;
        double fValue = Double.parseDouble(bigformatter.format(value));
        System.out.println(value);
        System.out.println(fValue);
    }

    public void setOperatorFunction(String function) {
        if(function == null)
            throw new IllegalArgumentException("Function cannot be null for operator nodes");
    }

    public boolean isOperator(){
        return function != null;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        if(!function.equals(""))
            this.function = function;
    }
}
