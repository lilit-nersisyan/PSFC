package org.cytoscape.psfc.logic.algorithms;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import org.cytoscape.model.CyEdge;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.enums.ENodeDataProps;
import org.cytoscape.psfc.logic.parsers.RuleFilesParser;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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
     * @return Level : scoreMap map, where scoreMap is Node : score map.
     * The returned map keeps the updated scores of Nodes at each level.
     * @throws Exception
     */
    public static HashMap<Integer, HashMap<Node, Double>> calculateFlow(Graph graph,
                                                                        HashMap<Node, Double> nodeScoreMap,
                                                                        HashMap<Integer, ArrayList<Node>> levelNodesMap,
                                                                        File edgeTypeRuleNameConfigFile,
                                                                        File ruleConfigFile, Properties nodeDataProps) throws Exception {


        //Keeps the recalculated scores for nodes at each level
        HashMap<Integer, HashMap<Node, Double>> levelNodeScoreMap = new HashMap<Integer, HashMap<Node, Double>>();

        //Keeps the recalculated scores for edges at each level:
        //the edge score reflects the input signal to a node of the current level - how - TBD
        //no edge values are applicable for nodes at first level
        HashMap<Integer, HashMap<CyEdge, Double>> levelCyEdgeScoreMap = new HashMap<Integer, HashMap<CyEdge, Double>>();

        //Keeps the rule as a String for each edgeType
        edgeTypeRuleMap = RuleFilesParser.parseSimpleRules(edgeTypeRuleNameConfigFile, ruleConfigFile);

        boolean isFirstLevel = true;
        for (int level : levelNodesMap.keySet()) {
            ArrayList<Node> nodesAtLevel = levelNodesMap.get(level);
            HashMap<Node, Double> nodeNewScoreMap = new HashMap<Node, Double>();
            if (isFirstLevel) {
                for (Node node : nodesAtLevel) {
                    double data = getNodeScore(nodeScoreMap, node, nodeDataProps);
                    nodeNewScoreMap.put(node, data);
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
                            double nodeScore = getNodeScore(nodeScoreMap, node, nodeDataProps);
                            double prevNodeScore = getNodeScore(levelNodeScoreMap.get(level-1), prevNode, nodeDataProps);
                            String edgeType = edge.getEdgeType();
                            double updatedNodeScore = nodeScore;
                            if (edgeType != null)
                                try {
                                    updatedNodeScore = updateScoreBySimpleRule(prevNodeScore, nodeScore, edgeType);
                                } catch (Exception e) {
                                    PSFCActivator.getLogger().error("Exception " + e.getMessage()
                                            + " caught when trying to updateScoreBySimpleRule for node "
                                            + node.getName() + " with values: "
                                            + prevNodeScore + " , " + nodeScore + " , " + edgeType);
                                }
                            else
                                PSFCActivator.getLogger().warn("EdgeType of edge " + edge.toString() + " was null");
                            nodeNewScoreMap.put(node, updatedNodeScore);
                        }
                    }
                }
            }
            levelNodeScoreMap.put(level, nodeNewScoreMap);
        }
        return levelNodeScoreMap;
    }

    private static double getNodeScore(HashMap<Node, Double> nodeScoreMap, Node node, Properties nodeDataProps) {
        if (nodeScoreMap.containsKey(node))
            return nodeScoreMap.get(node);
        if (nodeDataProps.containsKey(ENodeDataProps.NODE_DEFAULT_VALUE.getName()))
            try {
                double data = Double.parseDouble(ENodeDataProps.NODE_DEFAULT_VALUE.getName());
                return data;
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
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
//        String foo = "Math.exp(2,3)";
//        try {
//            System.out.println(engine.eval(foo));
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
        net.sourceforge.jeval.Evaluator evaluator = new net.sourceforge.jeval.Evaluator();
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
