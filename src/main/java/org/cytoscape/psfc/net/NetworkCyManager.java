package org.cytoscape.psfc.net;

import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.enums.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.view.model.CyNetworkView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PUBLIC CLASS NetworkManager
 * <p/>
 * This class provides static methods for copying, and modifying CyNetworks,
 * their nodes and edges and attributes.
 */
public class NetworkCyManager {
    /**
     * Returns a CyColumn from the given CyTable with the given name.
     * If such a column does not exist, it is created.
     * If a CyColumn with given name exists, but does not match the attribute type given,
     * and exception is thrown.
     *
     * @param table    CyTable where the CyColumn should be
     * @param attrName name of the attribute column
     * @param attrType type of the attribute
     * @return CyColumn
     * @throws Exception thrown if the attribute type does not match the existing type in the existing CyColumn.
     */
    public static CyColumn getOrCreateAttributeColumn(CyTable table,
                                                      String attrName, Class attrType) throws Exception {
        Iterator<CyColumn> iterator = table.getColumns().iterator();

        while (iterator.hasNext()) {
            CyColumn column = iterator.next();
            if (attrName.equals(column.getName()))
                if (column.getType().equals(attrType))
                    return column;
                else {
                    throw new Exception(ExceptionMessages.ConflictingAttributeType.getMessage());
                }
        }
        table.createColumn(attrName, attrType, false);
        return table.getColumn(attrName);
    }

    /**
     * Populate the CyTable with attributes of given attribute name from given
     * <code>CyNode</code> : attribute map.
     * <p>
     * If an attribute column with such name
     * does not exist in the <code>CyTable</code>, it will be created. If it exists and its type
     * does not match the attribute type given, an Exception will be returned.
     * If the key type of the map is not <code>CyNode</code>, an Exception is returned.
     * Otherwise, rows for each of the <code>CyNode</code>in the map will be populated with
     * values from the map. If a CyNode does not exist in the given CyNetwork this node will be skipped.
     * </p>
     *
     * @param cyNetwork          CyNetwork containing the CyNodes to be mapped.
     * @param cyNodeAttributeMap Map containing the CyNodes and their attribute values.
     * @param attrName           the name of the attribute column
     * @param attrType           the type of the attribute
     */
    public static void setNodeAttributesFromMap(CyNetwork cyNetwork,
                                                Map cyNodeAttributeMap,
                                                String attrName, Class<?> attrType) throws Exception {
        if (cyNodeAttributeMap.isEmpty())
            throw new Exception(ExceptionMessages.EmptyMap.getMessage());
        CyTable nodeTable = cyNetwork.getDefaultNodeTable();

        NetworkCyManager.getOrCreateAttributeColumn(nodeTable, attrName, attrType);

        for (Object obj : cyNodeAttributeMap.keySet()) {
            if (obj instanceof CyNode)
                break;
            else
                throw new Exception(ExceptionMessages.NotCyNodeKeyType.getMessage());
        }
        CyNode cyNode;
        for (Object obj : cyNodeAttributeMap.keySet()) {
            cyNode = (CyNode) obj;
            CyRow row = nodeTable.getRow(cyNode.getSUID());
            row.set(attrName, cyNodeAttributeMap.get(cyNode));
        }
    }


    /**
     * Populate the CyTable with attributes of given attribute name from given
     * <code>CyEdge</code> : attribute map.
     * <p>
     * If an attribute column with such name
     * does not exist in the <code>CyTable</code>, it will be created. If it exists and its type
     * does not match the attribute type given, an Exception will be returned.
     * If the key type of the map is not <code>CyEdge</code>, an Exception is returned.
     * Otherwise, rows for each of the <code>CyEdge</code>in the map will be populated with
     * values from the map. If a CyEdge does not exist in the given CyNetwork this edge will be skipped.
     * </p>
     *
     * @param cyNetwork          CyNetwork containing the CyEdges to be mapped.
     * @param cyEdgeAttributeMap Map containing the CyEdges and their attribute values.
     * @param attrName           the name of the attribute column
     * @param attrType           the type of the attribute
     */
    public static void setEdgeAttributesFromMap(CyNetwork cyNetwork,
                                                Map cyEdgeAttributeMap,
                                                String attrName, Class<?> attrType) throws Exception {
        if (cyEdgeAttributeMap.isEmpty())
            throw new Exception(ExceptionMessages.EmptyMap.getMessage());
        CyTable edgeTable = cyNetwork.getDefaultEdgeTable();

        NetworkCyManager.getOrCreateAttributeColumn(edgeTable, attrName, attrType);

        for (Object obj : cyEdgeAttributeMap.keySet()) {
            if (obj instanceof CyEdge)
                break;
            else
                throw new Exception(ExceptionMessages.NotCyEdgeKeyType.getMessage());
        }
        CyEdge cyEdge;
        for (Object obj : cyEdgeAttributeMap.keySet()) {
            cyEdge = (CyEdge) obj;
            CyRow row = edgeTable.getRow(cyEdge.getSUID());
            row.set(attrName, cyEdgeAttributeMap.get(cyEdge));
        }
    }

    /**
     * Return first of all the views of the given network, or create one if no view for the network exists.
     *
     * @param network the network
     * @return the view of the network
     */
    public static CyNetworkView getNetworkView(CyNetwork network) {
        CyNetworkView networkView;
        Collection<CyNetworkView> networkViews = PSFCActivator.networkViewManager.getNetworkViews(network);
        if (networkViews.isEmpty()) {
            networkView = PSFCActivator.networkViewFactory.createNetworkView(network);
            PSFCActivator.networkViewManager.addNetworkView(networkView);
        } else
            networkView = networkViews.iterator().next();
        return networkView;
    }

    /**
     * Return first of all the views of the given network, or create one if no view for the network exists.
     *
     * @param network the network
     * @return the Collection of all views of the network
     */
    public static Collection<CyNetworkView> getNetworkViews(CyNetwork network) {
        CyNetworkView networkView;
        Collection<CyNetworkView> networkViews = PSFCActivator.networkViewManager.getNetworkViews(network);
        if (networkViews.isEmpty()) {
            networkView = PSFCActivator.networkViewFactory.createNetworkView(network);
            PSFCActivator.networkViewManager.addNetworkView(networkView);
            networkViews.add(networkView);
        }
        return networkViews;
    }


    /**
     * Converts the give <code>CyNode</code> : <code>Double</code> map to
     * <code>Node</code> : <code>Double</code> map, by readdressing the double value of each CyNode
     * in the given map to referencing Node in the give Graph.
     *
     * @param graph           Graph keeping the nodes
     * @param cyNodeDoubleMap given <code>CyNode</code> : <code>Double</code> map
     * @return converted <code>Node</code> : <code>Double</code> map
     */
    public static HashMap<Node, Double> convertCyNodeDouble2NodeDoubleMap(Graph graph,
                                                                          HashMap<CyNode, Double> cyNodeDoubleMap) {
        HashMap<Node, Double> nodeDoubleMap = new HashMap<Node, Double>();
        for (CyNode cyNode : cyNodeDoubleMap.keySet()) {
            nodeDoubleMap.put(graph.getNode(cyNode), cyNodeDoubleMap.get(cyNode));
        }
        return nodeDoubleMap;
    }

    public static HashMap<Edge, Double> convertCyEdgeDouble2EdgeDoubleMap
            (Graph graph, HashMap<CyEdge, Double> cyEdgeWeightMap) {
        HashMap<Edge, Double> map = new HashMap<Edge, Double>();
        for (CyEdge cyEdge : cyEdgeWeightMap.keySet()) {
            CyNode cySource = cyEdge.getSource();
            CyNode cyTarget = cyEdge.getTarget();
            Node source = graph.getNode(cySource);
            Node target = graph.getNode(cyTarget);
            if (source != null && target != null)
                map.put(graph.getEdge(source, target), cyEdgeWeightMap.get(cyEdge));
        }
        return map;
    }

    public static CyEdge getCyEdge(CyNetwork network, CyNode cyNode, CyNode cyTarget) {
        for (CyEdge cyEdge : network.getEdgeList()) {
            if (cyEdge.getSource().equals(cyNode) && cyEdge.getTarget().equals(cyTarget))
                return cyEdge;
        }
        return null;
    }


}
