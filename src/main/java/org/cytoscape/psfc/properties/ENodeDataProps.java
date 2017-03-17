package org.cytoscape.psfc.properties;

/**
 * Created by User on 7/8/2014.
 */
public enum ENodeDataProps {
    NODE_DEFAULT_VALUE("NodeDefaultValue", null),
    MISSING_DATA_VALUE("MissingDataValue", 1);

    private boolean oldValue = true;
    private boolean newValue = true;
    private Object defaultValue = null;
    private String name = "";

    ENodeDataProps(String name, Object defaultValue) {
        this.defaultValue = defaultValue;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
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
