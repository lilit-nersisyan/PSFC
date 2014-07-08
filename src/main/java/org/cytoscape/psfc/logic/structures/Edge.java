package org.cytoscape.psfc.logic.structures;

import org.cytoscape.psfc.gui.enums.ExceptionMessages;

/**
 * PUBLIC CLASS Edge
 * Edge represents a directed relation between two Nodes: source and target.
 * There can be only one directed edge between two nodes.
 *
 * Unresolved: Should  an Edge keeps an integer index as an identifier?
 * Unresolved: Are self-loops allowed?
 * Unresolved: Should it keep the reference of the Graph it belongs to?
 */
public class Edge {
    private int ID;
    public static int ACTIVATION = 1;
    public static int INHIBITION = 0;

    private Node source;
    private Node target;
    private String edgeType = "";


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
    public String toString(){
        return "Edge{" +
                "source=" + source.getID() +
                "target=" + target.getID() + "," +
                "type='" + edgeType + '\'' +
                "}\n";
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Edge) {
            Edge that = (Edge) o;
            return this.source == that.source && this.target == that.target;
        }
        return false;
    }
}
