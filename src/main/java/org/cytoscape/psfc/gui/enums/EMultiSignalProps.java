package org.cytoscape.psfc.gui.enums;

/**
 * Created by User on 7/15/2014.
 */
public enum EMultiSignalProps {
    SplitSignalOn("splitSignalOn"),
    SplitSignalRule("splitSignalRule"),
    MultipleSignalProcessingRule("multipleSignalProcessingRule"),
    EdgeRankAttribute("edgeRankAttribute"),
    EdgeWeightsAttribute("edgeWeightsAttribute"),
    SignalProcessingOrder("signalProcessingOrder")
    ;

    public static final Integer SPLIT_NONE= 0;
    public static final Integer SPLIT_PROPORTIONAL = 1;
    public static final Integer SPLIT_EQUAL = 2;
    public static final Integer SPLIT_WEIGHTS = 3;
    public static final String EDGE_WEIGHT_MAP = "edgeWeigthsMap";

    public static final Integer SPLIT_INCOMING = 0;
    public static final Integer SPLIT_OUTGOING = 1;

    public static final Integer UPDATE_NODE_SCORES = 0;
    public static final Integer MULTIPLICATION = 1;
    public static final Integer ADDITION = 2;

    public static final Integer ORDER_NONE = 0;
    public static final Integer ORDER_RANKS = 1;

    private String name;

    EMultiSignalProps(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
