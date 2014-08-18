package org.cytoscape.psfc.logic.structures;

import org.cytoscape.psfc.gui.enums.ExceptionMessages;

/**
 * PUBLIC CLASS Edge
 * Edge represents a directed relation between two Nodes: source and target.
 * There can be only one directed edge between two nodes.
 * <p/>
 * Unresolved: Should  an Edge keeps an integer index as an identifier?
 * Unresolved: Are self-loops allowed?
 * Unresolved: Should it keep the reference of the Graph it belongs to?
 */
public class Edge {
    private Node source;
    private Node target;
    private String edgeType = "";
    private double weight = 1;
    private double signal = 0;
    private Integer rank = 0;
    private int loopCount = 0;
    private boolean isBackward = false;

    Edge(Node source, Node target) {
        if (target == null || source == null)
            throw new NullPointerException(ExceptionMessages.EdgeWithNullNode.getMessage());
        this.target = target;
        this.source = source;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public String getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source: ID=" + source.getID() + "; SUID=" + source.getID() +
                "target: ID=" + + target.getID() + "; name=" + target.getName() +
                "type='" + edgeType + '\'' + "," +
                "weight='" + weight + '\'' + "," +
                "rank='" + rank + '\'' + "," +
                "loopCount='"  + loopCount + '\'' +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge) {
            Edge that = (Edge) o;
            return this.source == that.source && this.target == that.target;
        }
        return false;
    }

    public void setWeight(double weight) {
        if (!Double.isNaN(weight))
            if (weight >= 0)
                this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setSignal(double signal) {
        if (!Double.isNaN(signal))
            this.signal = signal;
    }

    public double getSignal() {
        return signal;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getRank() {
        return rank;
    }

    public void incrementLoopCount(int i) {
        loopCount = loopCount + i;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void setIsBackward(boolean isBackward) {
        this.isBackward = isBackward;
    }

    public boolean isBackward() {
        return isBackward;
    }
}
