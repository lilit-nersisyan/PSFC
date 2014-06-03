package org.cytoscape.psfc.gui.actions.net;

import org.cytoscape.model.*;
import org.cytoscape.psfc.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by User on 5/26/2014.
 */
public class NetworkGraphMapper {

    public static Graph graphFromNetwork(CyNetwork network) {
        Graph graph = new Graph();
        HashMap<CyNode, Node> cyNodePsfNodeMap = new HashMap<CyNode, Node>();

        for (Object nodeObj : network.getNodeList()) {
            CyNode cyNode = (CyNode) nodeObj;
            Node psfNode = graph.addNode();
            psfNode.setName(network.getDefaultNodeTable().getRow(cyNode.getSUID()).get(CyNetwork.NAME, String.class));
            cyNodePsfNodeMap.put(cyNode, psfNode);
        }

        for (Object edgeObj : network.getEdgeList()) {
            CyEdge cyEdge = (CyEdge) edgeObj;
            Node psfSource = cyNodePsfNodeMap.get(cyEdge.getSource());
            Node psfTarget = cyNodePsfNodeMap.get(cyEdge.getTarget());
            graph.addEdge(psfSource, psfTarget);
        }
        graph.setCyNodePsfNodeMap(cyNodePsfNodeMap);
        graph.setNetwork(network);

        return graph;
    }

    /**
     * Populate the CyTable with attributes of given attribute name from given
     * Map<CyNode, Class<?>> attribute map. If an attribute column with such name
     * does not exist in the CyTable, it will be created. If it exists and its type
     * does not match the attribute type given, an IllegalArgument exception will be returned.
     * If the key type of the map is not CyNode, an IllegalArgument exception is returned.
     * Otherwise, rows for each of the CyNode in the map will be populated with
     * values from the map. If a CyNode does not exist in the given CyNetwork, an
     * IllegalArgumentException will be returned. ??
     *
     * @param cyNetwork
     * @param cyNodeAttributeMap
     * @param attrName
     * @param attrType
     */
    public static void setNodeAttributesFromMap(CyNetwork cyNetwork,
                                                Map cyNodeAttributeMap,
                                                String attrName, Class<?> attrType) {
        if (cyNodeAttributeMap.isEmpty())
            throw new IllegalArgumentException(ExceptionMessages.EmptyMap);
        CyTable nodeTable = cyNetwork.getDefaultNodeTable();
        CyColumn cyColumn = getOrCreateAttributeColumn(nodeTable, attrName, attrType);



        for (Object obj : cyNodeAttributeMap.keySet()){
            if (obj instanceof CyNode)
                break;
            else
                throw new IllegalArgumentException(ExceptionMessages.notCyNodeKeyType);
        }
        CyNode cyNode;
        for (Object obj : cyNodeAttributeMap.keySet()){
            cyNode = (CyNode) obj;
            CyRow row = nodeTable.getRow(cyNode.getSUID());
            row.set(attrName, cyNodeAttributeMap.get(cyNode));
        }
    }

    private static CyColumn getOrCreateAttributeColumn(CyTable table, String attrName, Class attrType) {
        Iterator<CyColumn> iterator = table.getColumns().iterator();

        while (iterator.hasNext()) {
            CyColumn column = iterator.next();
            if (attrName.equals(column.getName()))
                if (column.getType().equals(attrType))
                    return column;
                else {
                    //should change this : return exception and exit
                    System.out.println("Column with name " + attrName + " exists, but has different type.");
                    table.createColumn(attrName + "_" + attrType.toString(), attrType, false);
                    return table.getColumn(attrName + "_" + attrType.toString());
                }
        }
        table.createColumn(attrName, attrType, false);
        return table.getColumn(attrName);
    }

}
