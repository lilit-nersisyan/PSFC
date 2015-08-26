package org.cytoscape.psfc.properties;

/**
 * Created by User on 6/15/2014.
 */
public enum EpsfcProps {
    EdgeTypeAttribute("edgeTypeAttribute"),
//    NodeDataType("nodeDataType"),
//    MultipleDataOption("multipleDataOption"),
    EdgeTypeRuleNameConfigFile("edgeTypeRuleNameConfigFile"),
    RuleNameRuleConfigFile("ruleNameRuleConfigFile"),
    NodeDataAttribute("nodeDataAttribute"),
    SplitSignalOn("splitSignalOn"),
    SplitSignalRule("splitSignalRule"),
    MultipleSignalProcessingRule("multipleSignalProcessingRule"),
    EdgeRankAttribute("edgeRankAttribute"),
    EdgeWeigthsAttribute("edgeWeightsAttribute"),
    SignalProcessingOrder("signalProcessingOrder"),
    SortingAlgorithm("sortingAlgorithm"),
    LoopHandling("loopHandling"),
    ConvergenceThreshold("convergenceThreshold"),
    MaxNumOfIterations("maxNumOfIterations"),
    CalculateSignificance("calculateSignificance"),
    BootstrapMode("bootstrapMode"),
    BootstrapExpMatrix("bootstrapExpMatrix"),
    NumOfSamplings("NumOfSamplings"),
    ChangeNetworkLayout("changeNetworkLayout");

    private boolean oldValue = true;
    private boolean newValue = true;
    private String name = "";

    EpsfcProps(String name){
        this.name = name;
    }

    private boolean initialized = false;

    public void setInitialized(boolean b){
        this.initialized = b;
    }
    public boolean isInitialized(){
        return initialized;
    }


    public String getName(){
        return name;
    }

    public boolean getNewValue() {
        return newValue;
    }

    public void setNewValue(boolean value) {
        this.newValue = value;
    }

    public boolean getOldValue() {
        return oldValue;
    }

    public void setOldValue(boolean oldValue) {
        this.oldValue = oldValue;
    }
}
