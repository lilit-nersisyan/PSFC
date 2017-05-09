package org.cytoscape.psfc.net;

import org.cytoscape.model.*;
import org.cytoscape.psfc.DoubleFormatter;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.enums.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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
     * Deletes the CyColumn from the given CyTable with the given name.
     * If such a column does not exist, the method does nothing.
     *
     * @param table    CyTable where the CyColumn should be
     * @param attrName name of the attribute column
     * @return int 0, if the column is found and deleted; 1, if no such column existed
     */
    public static int deleteAttributeColumn(CyTable table,
                                            String attrName) throws Exception {
        Iterator<CyColumn> iterator = table.getColumns().iterator();

        while (iterator.hasNext()) {
            CyColumn column = iterator.next();
            if (attrName.equals(column.getName())) {
                table.deleteColumn(column.getName());
                return 0;
            }
        }
        return 1;
    }

    /**
     * Deletes the CyColumn from the given CyTable with the given pattern.
     * The pattern is the substring with which the column name begins.
     * If such a column does not exist, the method does nothing.
     *
     * @param table  CyTable where the CyColumn should be
     * @param prefix name of the attribute column
     * @return int 0, if the column is found and deleted; 1, if no such column existed
     */
    public static int deleteAttributeColumnByPrefix(CyTable table,
                                                    String prefix) throws Exception {

        Iterator<CyColumn> iterator = table.getColumns().iterator();

        while (iterator.hasNext()) {
            CyColumn column = iterator.next();
            if (column.getName().startsWith(prefix)) {
                table.deleteColumn(column.getName());
                return 0;
            }
        }
        return 1;
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

        if (attrType.equals(Double.class) || attrType.equals(double.class)) {
            for (Object obj : cyNodeAttributeMap.keySet()) {
                cyNode = (CyNode) obj;
                double value = (Double) cyNodeAttributeMap.get(cyNode);
                value = DoubleFormatter.formatDouble(value);
                CyRow row = nodeTable.getRow(cyNode.getSUID());
                row.set(attrName, value);
            }

        } else {
            for (Object obj : cyNodeAttributeMap.keySet()) {
                cyNode = (CyNode) obj;
                CyRow row = nodeTable.getRow(cyNode.getSUID());
                row.set(attrName, cyNodeAttributeMap.get(cyNode));
            }
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
        if (attrType.equals(Double.class) || attrType.equals(double.class)) {
            for (Object obj : cyEdgeAttributeMap.keySet()) {
                cyEdge = (CyEdge) obj;
                double value = (Double) cyEdgeAttributeMap.get(cyEdge);
                value = DoubleFormatter.formatDouble(value);
                CyRow row = edgeTable.getRow(cyEdge.getSUID());
                row.set(attrName, value);
            }

        } else {
            for (Object obj : cyEdgeAttributeMap.keySet()) {
                cyEdge = (CyEdge) obj;
                CyRow row = edgeTable.getRow(cyEdge.getSUID());
                row.set(attrName, cyEdgeAttributeMap.get(cyEdge));
            }
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

    public static void visualizeNodeSignals(CyNetwork network, CyColumn nodeSignalColumn,
                                            double minSignal, double maxSignal) throws Exception {
        ContinuousMapping<Double, Paint> nodeColorMapping = null;
        try {
            nodeColorMapping = (ContinuousMapping<Double, Paint>) PSFCActivator.vmfFactoryC.createVisualMappingFunction
                    (nodeSignalColumn.getName(), Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
        } catch (Exception e) {
            throw new Exception(nodeSignalColumn.getName() + " should be of type " + Double.class.getName());
        }

        BoundaryRangeValues<Paint> brvMin = new BoundaryRangeValues<Paint>(Color.WHITE, Color.YELLOW, Color.GREEN);
        nodeColorMapping.addPoint(minSignal, brvMin);
        BoundaryRangeValues<Paint> brvMax = new BoundaryRangeValues<Paint>(Color.GREEN, Color.PINK, Color.RED);
        nodeColorMapping.addPoint(maxSignal, brvMax);

        VisualStyle visualStyle = PSFCActivator.visualStyleFactory.createVisualStyle("psfc_vs");
        Set<VisualStyle> visualStyles = PSFCActivator.visualMappingManager.getAllVisualStyles();

        boolean isVisualStylePresent = false;
        for (VisualStyle vs : visualStyles) {
            if (vs.getTitle().equals(visualStyle.getTitle())) {
                isVisualStylePresent = true;
                visualStyle = vs;
            }
        }
        if (!isVisualStylePresent)
            PSFCActivator.visualMappingManager.addVisualStyle(visualStyle);

        CyNetworkView networkView = getNetworkView(network);
        for (View<CyNode> nodeView : networkView.getNodeViews()) {
            nodeView.clearValueLock(BasicVisualLexicon.NODE_FILL_COLOR);
        }

        visualStyle.addVisualMappingFunction(nodeColorMapping);
        visualStyle.apply(getNetworkView(network));
        getNetworkView(network).updateView();
    }

    public static void playFlow(CyNetwork network, final PSFCPanel psfcPanel) {
        TreeMap<Integer, Thread> priorityThreadMap = new TreeMap<Integer, Thread>();

        final JSlider jsl_levels = psfcPanel.getJsl_levels();
        for (int level = jsl_levels.getMinimum(); level <= jsl_levels.getMaximum(); level++) {
            final int levelf = level;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    jsl_levels.setValue(levelf);
                }
            });
            priorityThreadMap.put(level, thread);

        }
        for (int level : priorityThreadMap.keySet()) {
            try {
                priorityThreadMap.get(level).start();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("Visualization level " + level);
        }
    }

    /**
     * Return edge CyColumn from name
     */
    public static CyColumn getEdgeColumnFromName(String columnName, CyNetwork network){
        CyColumn column = network.getDefaultEdgeTable().getColumn(columnName);
        return column;
    }

    /**
     * Return node CyColumn from name
     */
    public static CyColumn getNodeColumnFromName(String columnName, CyNetwork network){
        CyColumn column = network.getDefaultNodeTable().getColumn(columnName);
        return column;
    }
}
