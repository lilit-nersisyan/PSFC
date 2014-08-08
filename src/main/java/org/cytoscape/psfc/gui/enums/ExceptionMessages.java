
package org.cytoscape.psfc.gui.enums;

/**
 * Created by User on 5/26/2014.
 */
public enum ExceptionMessages {

    EdgeWithNullNode("Edge cannot be initialized with null Node"),
    NodeWithNegativeIndex("Negative indices not allowed"),
    EmptyGraph("The input graph was empty"),
    EmptyMap("The attribute map was empty"),
    NullNetwork("The network was null"),
    NullCyNode("The CyNode was null"),
    NotCyNodeKeyType("The key type should be of class CyNode"),
    NotCyEdgeKeyType("The key type should be of class CyEdge"),
    ConflictingAttributeType("The attribute type does not match existing CyColumn"),
    NoSuchAlgorithm("No algorithm with specified argument exists"),
    NullConfigFile("Null configuration file"),
    NonExistingNode("The node does not exist in the graph")
    ;

    private String message;
    ExceptionMessages(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
