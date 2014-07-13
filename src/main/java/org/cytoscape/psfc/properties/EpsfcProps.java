package org.cytoscape.psfc.properties;

/**
 * Created by User on 6/15/2014.
 */
public enum EpsfcProps {
    EdgeTypeAttribute("edgeTypeAttribute"),
    NodeDataType("nodeDataType"),
    MultipleDataOption("multipleDataOption"),
    EdgeTypeRuleNameConfigFile("edgeTypeRuleNameConfigFile"),
    RuleNameRuleConfigFile("ruleNameRuleConfigFile")
    ;

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
