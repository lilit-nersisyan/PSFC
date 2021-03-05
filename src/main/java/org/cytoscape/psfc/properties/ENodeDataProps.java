package org.cytoscape.psfc.properties;

/**
 * Created by User on 7/8/2014.
 */
public enum ENodeDataProps {
    NODE_DEFAULT_VALUE("NodeDefaultValue");

    private String name = "";

    ENodeDataProps(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
