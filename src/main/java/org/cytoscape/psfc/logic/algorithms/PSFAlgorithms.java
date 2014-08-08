package org.cytoscape.psfc.logic.algorithms;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import org.apache.log4j.Logger;
import org.cytoscape.model.CyEdge;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.enums.EMultiSignalProps;
import org.cytoscape.psfc.gui.enums.ENodeDataProps;
import org.cytoscape.psfc.logic.parsers.RuleFilesParser;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Created by User on 5/24/2014.
 */
public class PSFAlgorithms {

    private static HashMap<String, String> edgeTypeRuleMap;
    private static final String SOURCE = "source";
    private static final String TARGET = "target";


    /**
     * Calculates and returns a map of Nodes and updated flow score values at each level.
     *
     * @param graph                      Graph containing the nodes
     * @param nodeScoreMap               <code>node</code> : <code>Double</code> map of nodes' initial scores
     * @param levelNodesMap              <code>Integer</code> : <code>Node</code> map keeping the nodes at each sorted level
     * @param edgeTypeRuleNameConfigFile the file containing edgeType : ruleName mapping
     * @param ruleConfigFile             the file containing ruleName : rule mapping
     * @param nodeDataProps
     * @param multipleSignalProps        properties containing multiple signal processing rules.
     * @return Level : scoreMap map, where scoreMap is Node : score map.
     * The returned map keeps the updated scores of Nodes at each level.
     * @throws Exception
     */
    public static HashMap<Integer, HashMap<Node, Double>> calculateFlow(Graph graph,
                                                                        HashMap<Node, Double> nodeScoreMap,
                                                                        HashMap<Integer, ArrayList<Node>> levelNodesMap,
                                                                        File edgeTypeRuleNameConfigFile,
                                                                        File ruleConfigFile, Properties nodeDataProps,
                                                                        Properties multipleSignalProps,
                                                                        Logger logger) throws Exception {

        assignNodeLevels(graph, levelNodesMap);
        logger.debug("Node levels assigned");
        assingNodeDataValues(graph, nodeScoreMap);
        logger.debug("Node values assigned");
        //Keeps the recalculated scores for nodes at each level
        HashMap<Integer, HashMap<Node, Double>> levelNodeScoreMap = new HashMap<Integer, HashMap<Node, Double>>();

        //Keeps the recalculated scores for edges at each level:
        //the edge score reflects the input signal to a node of the current level - how - TBD
        //no edge values are applicable for nodes at first level
        HashMap<Integer, HashMap<CyEdge, Double>> levelCyEdgeScoreMap = new HashMap<Integer, HashMap<CyEdge, Double>>();

        //Keeps the rule as a String for each edgeType
        try {
            edgeTypeRuleMap = RuleFilesParser.parseSimpleRules(edgeTypeRuleNameConfigFile, ruleConfigFile);
        } catch (Exception e) {
            throw new Exception("Problem occured while parsing simple rules. Reason: "
                    + e.getMessage(), e);
        }

        assignEdgeWeigths(graph, multipleSignalProps, levelNodesMap);
        logger.debug("Edge weigths assigned");
        logger.debug("Preprocessed graph");
        logger.debug(graph.toString());

        logger.debug("PSF calculation started");
        logger.debug("Node data properties:");
        logger.debug(nodeDataProps.toString());
        logger.debug("Multiple signal processing rules");
        logger.debug(multipleSignalProps.toString());
        boolean isFirstLevel = true;
        int multiRule = (Integer)multipleSignalProps.get(EMultiSignalProps.MultipleSignalProcessingRule.getName());


        processFirstLevel(graph, levelNodesMap);


        for (int level : levelNodesMap.keySet()) {
            ArrayList<Node> nodesAtLevel = levelNodesMap.get(level);
            HashMap<Node, Double> nodeNewScoreMap = new HashMap<Node, Double>();
            if (isFirstLevel) {
                for (Node node : nodesAtLevel) {
                    nodeNewScoreMap.put(node, node.getValue());
                    ArrayList<Node> childNodes = graph.getChildNodes(node);
                    for (Node childNode : childNodes){
                        if (childNode.getLevel() > node.getLevel()) {
                            Edge edge = graph.getEdge(node, childNode);
                            edge.setSignal(edge.getWeight()*node.getValue());
                        }
                    }
                }
                isFirstLevel = false;
            } else {
                ArrayList<Node> nodesAtPrevLevel = levelNodesMap.get(level - 1);
                for (Node node : nodesAtLevel) {
                    ArrayList<Edge> prevEdges = new ArrayList<Edge>();
                    for (Node prevNode : nodesAtPrevLevel) {
                        Edge edge = graph.getEdge(prevNode, node);
                        if (edge != null) {
                            prevEdges.add(edge);
                            double nodeScore = node.getValue();
                            double prevNodeScore = prevNode.getValue();
                            String edgeType = edge.getEdgeType();
                            double updatedScore = nodeScore;
                            if (edgeType != null)
                                try {
                                    updatedScore = updateScoreBySimpleRule(edge.getSignal(), node.getValue(), edgeType);
                                    if (multiRule == EMultiSignalProps.UPDATE_NODE_SCORES)
                                        node.setValue(updatedScore);
                                } catch (Exception e) {
                                    PSFCActivator.getLogger().error("Exception " + e.getMessage()
                                            + " caught when trying to updateScoreBySimpleRule for node "
                                            + node.getName() + " with values: "
                                            + prevNodeScore + " , " + nodeScore + " , " + edgeType);
                                }
                            else
                                PSFCActivator.getLogger().warn("EdgeType of edge " + edge.toString() + " was null");
                            nodeNewScoreMap.put(node, updatedScore);
                        }
                    }
                }
            }
            levelNodeScoreMap.put(level, nodeNewScoreMap);
        }
        return levelNodeScoreMap;
    }

    private static void processFirstLevel(Graph graph, HashMap<Integer, ArrayList<Node>> levelNodesMap) {
        Set<Integer> levels = levelNodesMap.keySet();
        ArrayList<Node> nodesAtLevel = levelNodesMap.get(levels.iterator().next());
        for (Node node : nodesAtLevel) {
            ArrayList<Node> childNodes = graph.getChildNodes(node);
            for (Node childNode : childNodes){
                if (childNode.getLevel() > node.getLevel()) {
                    Edge edge = graph.getEdge(node, childNode);
                    edge.setSignal(edge.getWeight()*node.getValue());
                }
            }
        }
    }

    private static HashMap<Integer, HashMap<Node, Double>> psfUpdateNodeScores() {
        return null;
    }

    private static void assingNodeDataValues(Graph graph, HashMap<Node, Double> nodeScoreMap) {
        for (Node node : graph.getNodes()) {
            node.setValue(nodeScoreMap.get(node));
        }
    }

    private static void assignNodeLevels(Graph graph, HashMap<Integer, ArrayList<Node>> levelNodesMap) {
        for (int level : levelNodesMap.keySet()) {
            for (Node node : levelNodesMap.get((level))) {
                node.setLevel(level);
            }
        }
    }

    private static HashMap<Edge, Double> assignEdgeWeigths(Graph graph, Properties multipleSignalProps, HashMap<Integer, ArrayList<Node>> levelNodesMap)
            throws Exception {
        //Assign weights to all edges
        HashMap<Edge, Double> edgeWeigthMap = new HashMap<Edge, Double>();
        int splitRule = -1;
        try {
            Object property = multipleSignalProps.get(EMultiSignalProps.SplitSignalRule.getName());
            if (property == null) {
                throw new Exception("Could not find property " + EMultiSignalProps.SplitSignalRule.getName());
            }
            splitRule = (Integer) property;
        } catch (ClassCastException e) {
            throw new Exception("The property " + EMultiSignalProps.SplitSignalRule.getName()
                    + " was not of type int." + e.getMessage(), e);
        }


        if (splitRule == EMultiSignalProps.SPLIT_NONE) {
            for (Edge edge : graph.getEdges()) {
                edgeWeigthMap.put(edge, 1.0);
            }
        } else if (splitRule == EMultiSignalProps.SPLIT_WEIGHTS) {
            Object prop = multipleSignalProps.get(EMultiSignalProps.EDGE_WEIGHT_MAP);
            if (prop == null)
                throw new Exception("Could not find property " + EMultiSignalProps.EDGE_WEIGHT_MAP);
            HashMap<Edge, Double> weigthsMap;
            try {
                weigthsMap = (HashMap<Edge, Double>) prop;
            } catch (ClassCastException e) {
                throw new Exception("ClassCastException for proprty " + EMultiSignalProps.EDGE_WEIGHT_MAP);
            }
            for (Edge edge : weigthsMap.keySet())
                edgeWeigthMap.put(edge, weigthsMap.get(edge));
        } else {
            boolean equal = multipleSignalProps.get(EMultiSignalProps.SplitSignalRule.getName())
                    == EMultiSignalProps.SPLIT_EQUAL;
            boolean incomming = (multipleSignalProps.get
                    (EMultiSignalProps.SplitSignalOn.getName()) == EMultiSignalProps.SPLIT_INCOMING);
            if (incomming) {
                for (Node node : graph.getNodes()) {
                    ArrayList<Node> parentNodes = graph.getParentNodes(node);
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    for (Node parentNode : parentNodes) {
                        if (parentNode.getLevel() < node.getLevel())
                            edges.add(graph.getEdge(parentNode, node));
                    }
                    if (equal)
                        for (Edge edge : edges) {
                            edgeWeigthMap.put(edge, 1. / edges.size());
                        }
                    else {
                        double nodeScoreSum = 0;
                        for (Edge edge : edges)
                            nodeScoreSum += edge.getSource().getValue();
                        if (nodeScoreSum == 0)
                            for (Edge edge : edges)
                                edgeWeigthMap.put(edge, 1.);
                        for (Edge edge : edges)
                            edgeWeigthMap.put(edge, edge.getSource().getValue() / nodeScoreSum);
                    }
                }
            } else {
                for (Node node : graph.getNodes()) {
                    ArrayList<Node> childNodes = graph.getChildNodes(node);
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    for (Node childNode : childNodes) {
                        if (childNode.getLevel() > node.getLevel())
                            edges.add(graph.getEdge(node, childNode));
                    }
                    if (!edges.isEmpty())
                        if (equal)
                            for (Edge edge : edges) {
                                edgeWeigthMap.put(edge, 1. / edges.size());
                            }
                        else {
                            double nodeScoreSum = 0;
                            for (Edge edge : edges)
                                nodeScoreSum += edge.getTarget().getValue();
                            if (nodeScoreSum == 0)
                                for (Edge edge : edges)
                                    edgeWeigthMap.put(edge, 1.);
                            for (Edge edge : edges)
                                edgeWeigthMap.put(edge, edge.getTarget().getValue() / nodeScoreSum);
                        }
                }
            }
        }
        for (Edge edge : edgeWeigthMap.keySet()) {
            edge.setWeight(edgeWeigthMap.get(edge));
        }
        return edgeWeigthMap;

    }

    private static double getNodeScore(HashMap<Node, Double> nodeScoreMap, Node node, Properties nodeDataProps) {
        if (nodeScoreMap.containsKey(node))
            return nodeScoreMap.get(node);
        if (nodeDataProps.containsKey(ENodeDataProps.NODE_DEFAULT_VALUE.getName()))
            try {
                return Double.parseDouble(ENodeDataProps.NODE_DEFAULT_VALUE.getName());
            } catch (Exception e) {
                PSFCActivator.getLogger().error("Could not parse to double node default value: " + Node.getDefaultValue() + " will be used");
                return Double.parseDouble(Node.getDefaultValue());
            }
        return Double.parseDouble(Node.getDefaultValue());
    }

    private static double updateScoreBySimpleRule(double prevNodeScore, double nodeScore, String edgeType) throws ScriptException, UnparsableExpressionException, UnknownFunctionException {
        if (!edgeTypeRuleMap.containsKey(edgeType)) {
            PSFCActivator.getLogger().debug("No rule found for edge type " + edgeType);
            return 0;
        }
        String rule = edgeTypeRuleMap.get(edgeType);
        Calculable calculable = new ExpressionBuilder(rule)
                .withVariable(SOURCE, prevNodeScore)
                .withVariable(TARGET, nodeScore)
                .build();
        double result = calculable.calculate();
        System.out.println(rule + " " + prevNodeScore + ":" + nodeScore + " = " + result);
        return result;
    }


    public static void main(String[] args) {

        try {
            String exp = "exp(target)";
            Calculable calculable = new ExpressionBuilder(exp)
                    .withVariable("source", 2)
                    .withVariable("target", 1)
                    .build();
            System.out.println(calculable.calculate());
        } catch (UnparsableExpressionException e) {
            e.printStackTrace();
        } catch (UnknownFunctionException e) {
            e.printStackTrace();
        }


    }
}
