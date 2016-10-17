package org.cytoscape.psfc.logic.algorithms;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.ELoopHandlingProps;
import org.cytoscape.psfc.properties.EMultiSignalProps;
import org.cytoscape.psfc.properties.ENodeDataProps;

import java.util.*;

import static com.google.common.primitives.Doubles.max;
import static com.google.common.primitives.Doubles.min;

/**
 * PUBLIC CLASS PSF
 * <p/>
 * Performs PSF calculation for a Graph and keeps the Graph states at different iterations.
 */
public class PSF {
    private static final String SOURCE = "source";
    private static final String TARGET = "target";

    private final HashMap<String, String> edgeTypeRuleMap;
    private final Logger logger;
    private boolean silentMode = false;
    Graph graph;
    HashMap<Integer, State> states;

    Properties multiSignalProps = new Properties();
    Properties nodeDataProps = new Properties();
    Properties loopHandlingProps = new Properties();

    HashMap<Integer, ArrayList<Node>> levelNodesMap = new HashMap<Integer, ArrayList<Node>>();
    boolean converged = false;
    boolean loopMode = false;
    double convergenceThreshold = ELoopHandlingProps.CONVERGENCE_THRESHOLD_DEFAULT;
    int maxNumOfIterations = ELoopHandlingProps.MAX_NUM_OF_ITERATION_DEFAULT;
    private ArrayList<Node> loopTargetNodes = new ArrayList<Node>();
    private HashMap<Node, Double> originalNodeValues = new HashMap<Node, Double>();
    private boolean finished = false;
    private boolean precomputeMode = false;

    public HashMap<Node, Double> getTargetPValueMap() {
        return targetPValueMap;
    }

    private HashMap<Node, Double> targetPValueMap;

    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public PSF(Graph graph, HashMap<String, String> edgeTypeRuleMap, Logger logger) {
        this.graph = graph;
        this.edgeTypeRuleMap = edgeTypeRuleMap;
        this.logger = logger;
        setLevelNodesMap();
        initMultiSignalPropsByDefaults();
        initNodeDataPropsByDefaults();
        initLoopHandlingPropsByDefaults();
    }


    private void setLevelNodesMap() {
        for (Node node : graph.getNodes()) {
            if (!levelNodesMap.containsKey(node.getLevel()))
                levelNodesMap.put(node.getLevel(), new ArrayList<Node>());
            levelNodesMap.get(node.getLevel()).add(node);
        }
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    private void initNodeDataPropsByDefaults() {
        nodeDataProps.put(ENodeDataProps.NODE_DEFAULT_VALUE.getName(), 1);
    }

    private void initMultiSignalPropsByDefaults() {
        multiSignalProps.put(EMultiSignalProps.SplitSignalRule.getName(), EMultiSignalProps.SPLIT_NONE);
        multiSignalProps.put(EMultiSignalProps.SplitSignalOn.getName(), EMultiSignalProps.SPLIT_INCOMING);
        multiSignalProps.put(EMultiSignalProps.MultipleSignalProcessingRule.getName(), EMultiSignalProps.MULTIPLICATION);
        multiSignalProps.put(EMultiSignalProps.SignalProcessingOrder.getName(), EMultiSignalProps.ORDER_NONE);
    }

    private void initLoopHandlingPropsByDefaults() {
        loopHandlingProps.put(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.IGNORE_LOOPS);
        loopHandlingProps.put(ELoopHandlingProps.ConvergenceThreshold.getName(), ELoopHandlingProps.CONVERGENCE_THRESHOLD_DEFAULT);
        loopHandlingProps.put(ELoopHandlingProps.MaxNumOfIterations.getName(), ELoopHandlingProps.MAX_NUM_OF_ITERATION_DEFAULT);
    }


    public void calculateFlow() throws Exception {
        if (!silentMode) {
            logger.debug("PSF calculation started");

            logger.debug("Preprocessed graph");
            logger.debug(graph.toString());

            logger.debug("Node data properties:");
            logger.debug(nodeDataProps.toString());
            logger.debug("Multiple signal processing rules");
            logger.debug(multiSignalProps.toString());
            logger.debug("Loop handling options:");
            logger.debug(loopHandlingProps.toString());
        }


        if (!loopHandlingProps.getProperty(ELoopHandlingProps.LoopHandling.getName()).equals(ELoopHandlingProps.IGNORE_LOOPS)) {
            if (!GraphSort.cycleExists(graph)) {
                logger.debug("\nThe graph contains no loops\n");
            } else
                loopTargetNodes = getLoopTargetNodes();
        }

        //Precompute loops ?

        if (loopHandlingProps.getProperty(ELoopHandlingProps.LoopHandling.getName()).equals(ELoopHandlingProps.PRECOMPUTE_LOOPS)) {
            if (GraphSort.cycleExists(graph)) {
                //find graph.getNode(0).getLevel() levels of nodes. find loop targets.
                //create state. performPSF. update target values by signals.
                //follow by single iteration
                try {
                    precomputeLoops();
                } catch (Exception e) {
                    throw new Exception("Error while precomputing loops. Cause: " + e.getCause() + ". Message: " + e.getMessage());
                }
            } else {
                logger.debug("\nThe graph contains no loops\n");
            }
        }

        //Iterate until convergence?

        if (loopHandlingProps.getProperty(ELoopHandlingProps.LoopHandling.getName()).equals(ELoopHandlingProps.ITERATE_UNTIL_CONVERGENCE)) {
            if (GraphSort.cycleExists(graph)) {
                try {
                    convergenceThreshold = Double.parseDouble(loopHandlingProps.getProperty(ELoopHandlingProps.ConvergenceThreshold.getName()));
                } catch (NumberFormatException e) {
                    throw new Exception("Illegal convergence threshold format");
                } catch (NullPointerException e) {
                    throw new Exception("No convergence threshold supplied for iterations");
                }
                try {
                    maxNumOfIterations = Integer.parseInt(loopHandlingProps.getProperty(ELoopHandlingProps.MaxNumOfIterations.getName()));
                } catch (NumberFormatException e) {
                    throw new Exception("Illegal number of iterations format");
                } catch (NullPointerException e) {
                    throw new Exception("No parameter for max number of iterations supplied");
                }
                loopMode = true;
            } else {
                logger.debug("\nThe graph contains no loops\n");
            }
        }

        converged = false;
        states = new HashMap<Integer, State>();

        while (!converged && !cancelled) {

            if (!silentMode) {
                logger.debug("\nIteration: " + states.size());
            }
            State state = new State(states.size());
            states.put(states.size(), state);
            try {
                state.performPSF();
                if (multiSignalProps.get(EMultiSignalProps.MultipleSignalProcessingRule.getName()).equals(EMultiSignalProps.UPDATE_NODE_SCORES))
                    updateNodeValues();
            } catch (Exception e) {
                throw new Exception(e.getMessage(), e);
            }
            converged = checkForConvergence(states.size());
            if (cancelled)
                System.out.println("PSFC:: PSF computation cancelled at iteration " + (states.size() - 1));
        }
        loopMode = false;
        finished = true;

        if (!silentMode) {
            logger.debug("\nSuccess: psf computation complete!");
            logger.debug("PostProcessed graph:");
            logger.debug(graph.toString());
        }
    }


    /*
    Update node values as node signals of the last iteration for proceeding to the next one.
     */
    private void updateNodeValues() {
        for (Node node : graph.getNodes()) {
            node.setValue(node.getSignal());
        }
    }

    private ArrayList<Node> getLoopTargetNodes() {

        for (Node node : graph.getNodes()) {
            originalNodeValues.put(node, node.getValue());
//            for (Node parent : graph.getParentNodes(node)) {
//                if (node.getLevel() <= parent.getLevel()) {
//                    loopTargetNodes.add(node);
//                    break;
//                }
//            }
        }
        for (Edge edge : graph.getEdges()) {
            if (edge.isBackward())
                loopTargetNodes.add(edge.getTarget());
        }
        return loopTargetNodes;
    }

    private void precomputeLoops() throws Exception {
        if (!silentMode) {
            logger.debug("\nPrecomputing values of target nodes at feedback loops");
            logger.debug("Preprocessed graph");
        }

        if (!silentMode) {
            logger.debug("Loop target nodes to be updated:");
            for (Node node : loopTargetNodes) {
                logger.debug(node.toString());
            }
        }
//        loopMode = true;
        precomputeMode = true; //compute only backward edges
        State state = new State(0);
        try {
            state.performPSF();
        } catch (Exception e) {
            throw new Exception("Exception while precomputing loop PSF valus" + e.getMessage(), e);
        }
        precomputeMode = false;
//        loopMode = false;


        //Override node loop target nodes' values with their signals
        //Restore original values of the rest of the nodes
        for (Node node : graph.getNodes()) {
            if (loopTargetNodes.contains(node)) {
                node.setValue(node.getSignal());
            } else {
                node.setValue(originalNodeValues.get(node));
//                node.removeNodeSignals();
//                node.setSignal(node.getValue(), 0);
            }
            node.removeNodeSignals();
        }

        if (!silentMode) {
            logger.debug("Precomputed signals at loops");
            logger.debug("Updated values of loop target nodes:");
            for (Node tNode : loopTargetNodes) {
                logger.debug(tNode.toString());
            }
        }
    }


    private boolean checkForConvergence(int iteration) {
        if (!loopMode || loopTargetNodes.isEmpty())
            return true; //temporary solution

        if (iteration >= maxNumOfIterations) {
            if (!silentMode) {
                String message = "Reached max number of iterations without convergence!";
                logger.debug(message);
                System.out.println("PSFC::" + message);
            }
            return true;
        }
        if (!silentMode)
            System.out.println("PSFC:: Convergence check: Iteration: " + iteration);
        if (iteration > 1) {
            //Iterate through all nodes.
            // Check if the signal difference between this and the previous state
            // is less than the convergence threshold
            for (Node node : graph.getNodes()) {
                double prevSignal = node.getSignal(iteration - 2);
                double thisSignal = node.getSignal(iteration - 1); //last iteration
                if (prevSignal != 0)
                    if (Math.abs((thisSignal - prevSignal) / prevSignal) > convergenceThreshold / 100)
                        return false;
            }
            if (!silentMode) {
                String message = "Reached convergance at iteration: " + iteration;
                logger.debug(message);
                System.out.println(message);
            }
            return true;
        } else {
            return false;
        }
    }

    public void setNodeDataProps(Properties nodeDataProps) {
        for (Object key : nodeDataProps.keySet())
            this.nodeDataProps.put(key, nodeDataProps.get(key));
    }

    public void setMultiSignalProps(Properties multiSignalProps) {
        for (Object key : multiSignalProps.keySet())
            this.multiSignalProps.put(key, multiSignalProps.get(key));
    }

    public void setLoopHandlingProps(Properties loopHandlingProps) {
        this.loopHandlingProps = loopHandlingProps;
    }

    public HashMap<Integer, HashMap<Node, Double>> getLevelNodeSignalMap() {
        if (states.isEmpty())
            return null;
        return getLevelNodeSignalMap(states.size() - 1);
    }

    public HashMap<Integer, HashMap<Node, Double>> getLevelNodeSignalMap(int iteration) {
        if (!states.containsKey(iteration))
            return null;
        return states.get(iteration).getLevelNodeSignalMap();
    }

    public HashMap<Integer, HashMap<Edge, Double>> getLevelEdgeSignalMap() {
        if (states.isEmpty())
            return null;
        return getLevelEdgeSignalMap(states.size() - 1);
    }

    public HashMap<Integer, HashMap<Edge, Double>> getLevelEdgeSignalMap(int iteration) {
        if (!states.containsKey(iteration))
            return null;
        return states.get(iteration).getLevelEdgeSignalMap();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setTargetPValueMap(HashMap<Node, Double> targetPValueMap) {
        this.targetPValueMap = targetPValueMap;
    }

    public HashMap<Integer, ArrayList<Node>> getLevelNodesMap() {
        return levelNodesMap;
    }

    public boolean isFinished() {
        return finished;
    }


    /**
     * PRIVATE CLASS State
     * <p/>
     * Keeps the node signal values and edge signal values for one iteration and provides basic
     * functions for PSF algorithm.
     */
    private class State {
        int iteration;
        HashMap<Integer, HashMap<Node, Double>> levelNodeSignalMap = new HashMap<Integer, HashMap<Node, Double>>();
        int splitOn;
        int splitRule;
        int multiRule;
        int orderRule;
        private HashMap<Integer, HashMap<Node, Double>> levelEdgeSignalMap;

        public State(int iteration) {
            this.iteration = iteration;
            splitOn = (Integer) multiSignalProps.get(EMultiSignalProps.SplitSignalOn.getName());
            splitRule = (Integer) multiSignalProps.get(EMultiSignalProps.SplitSignalRule.getName());
            multiRule = (Integer) multiSignalProps.get(EMultiSignalProps.MultipleSignalProcessingRule.getName());
            orderRule = (Integer) multiSignalProps.get(EMultiSignalProps.SignalProcessingOrder.getName());
        }


        public void performPSF() throws Exception {
            if (iteration == 0)
                initNodeSignalsFromValues();
            boolean firstLevel = true;
            for (int level : levelNodesMap.keySet()) {
                if (firstLevel) {
                    processFirstLevel();
                    firstLevel = false;
                } else {
                    processLevel(level);
                }
            }
        }

        private void initNodeSignalsFromValues() {
            for (Node node : graph.getNodes())
                node.setSignal(node.getValue(), iteration);
        }

        private void processFirstLevel() {
            for (Node node : levelNodesMap.get(0)) {
                if (iteration == 0)
                    node.setSignal(node.getValue(), iteration);
                else
                    node.setSignal(node.getSignal(iteration - 1), iteration);
            }
        }


        private void processLevel(int level) throws Exception {
            if (splitRule == EMultiSignalProps.SPLIT_EQUAL || splitRule == EMultiSignalProps.SPLIT_PROPORTIONAL) {
                if (splitOn == EMultiSignalProps.SPLIT_INCOMING)
                    assignEdgeWeights_incoming(level);
                else
                    assignEdgeWeigths_outgoing(level);
            }
            if (multiRule != EMultiSignalProps.UPDATE_NODE_SCORES) {
                updateEdgeSignals(level);
                processMultipleSignals(level);
            } else {
                updateSignalsByCascade(level);
            }
        }

        private void updateSignalsByCascade(int nextLevel) throws Exception {
            for (Node node : levelNodesMap.get(nextLevel)) {
                ArrayList<Node> parentNodes = graph.getParentNodes(node);
                ArrayList<Edge> edges = collectEdges(node, parentNodes);


                if (orderRule == EMultiSignalProps.ORDER_RANKS) {
                    Collections.sort(edges, new Comparator<Edge>() {
                        @Override
                        public int compare(Edge edge1, Edge edge2) {
                            return edge2.getRank() - edge1.getRank();
                        }
                    });
                }
                boolean firstEdge = true;

                for (Edge edge : edges) {
                    if (firstEdge) {
                        firstEdge = false;
                        edge.getTarget().setSignal(edge.getTarget().getValue(), iteration);
                    }
                    double source, target, signal;
                    if (splitOn == EMultiSignalProps.SPLIT_INCOMING) {
                        source = edge.getSource().getSignal();
                        target = edge.getWeight() * edge.getTarget().getSignal();
                    } else {
                        source = edge.getWeight() * edge.getSource().getSignal();
                        target = edge.getTarget().getSignal();
                    }
                    try {
                        signal = updateScoreBySimpleRule(source, target, edge.getEdgeType());
                    } catch (Exception e) {
                        String message = "Exception at rule calculation for edge " + edge.toString()
                                + ". Reason: " + e.getMessage();
//                        JOptionPane.showMessageDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),message, "PSFC rule calculation problem", JOptionPane.OK_OPTION);
                        logger.debug(message);
                        throw new Exception(message, e);
                    }
                    edge.setSignal(signal);
//                    if (edge.isBackward() && iteration != 0) {
//                        edge.getTarget().setSignal(edge.getSignal(), iteration);
//                    } else
                    edge.getTarget().setSignal(signal, iteration);
                }
            }
        }

        /**
         * For each node at level, assemble the signals of the edges between that node
         * and its parent nodes from previous levels, either adding or multiplying them together.
         * The node signal is overridden by the assembled signal.
         *
         * @param level
         */
        private void processMultipleSignals(int level) throws Exception {
            for (Node node : levelNodesMap.get(level)) {
                ArrayList<Node> parentNodes = graph.getParentNodes(node);

                ArrayList<Edge> edges = collectEdges(node, parentNodes);
                double signal;
                String function = node.getFunction();
                if (function != null){
                    try {
                        signal = applyFunction(function, edges);
                    } catch (Exception e) {
                        throw new Exception("Problem handling function " + function + " at node " + node.toString());
                    }
                } else {
                    if (multiRule == EMultiSignalProps.ADDITION) {
                        signal = 0;
                        for (Edge edge : edges) {
                            signal += edge.getSignal();
                        }
                    } else {
                        signal = 1;
                        for (Edge edge : edges) {
                            signal *= edge.getSignal();
                        }
                    }
                }
                node.setSignal(signal, iteration);
            }
        }

        /**
         * Functions are from the list:
         *  min
         *  max
         *  sum
         *  prod
         *
         * For now, these function are applied on all of the input edges.
         * Later, the functions will allow for any operation on any subset of the edges.
         *
         * @param function
         * @param edges
         * @return
         */
        private double applyFunction(String function, ArrayList<Edge> edges) throws Exception {
            double[] signals = new double[edges.size()];
            for(int i = 0; i < signals.length; i++){
                signals[i] = edges.get(i).getSignal();
            }
            double signal = Double.NaN;

            switch (function){
                case "min":
                    signal = min(signals);
                    break;
                case "max":
                    signal = max(signals);
                    break;
                case "sum":
                    signal = 0;
                    for(double s : signals){
                        signal += s;
                    }
                    break;
                case "prod":
                    signal = 1;
                    for(double s : signals){
                        signal *= s;
                    }
                    break;
                default:
                    throw new Exception("Function " + function +
                            " is not defined in PSFC: use either of {min, max, sum, prod}");
            }
            return signal;
        }

        /**
         * Creates an ArrayList of Edges between a node and its parentNodes depending on the loopMode.
         * In case of  precomputeMode, only the backward edges are collected.
         * In case of loopMode, all the edges are collected.
         * Otherwise, all the edges except for backwards are collected.
         * @param node
         * @param parentNodes
         * @return
         */
        private ArrayList<Edge> collectEdges(Node node,ArrayList<Node> parentNodes){
            ArrayList<Edge> edges = new ArrayList<Edge>();
            for (Node parentNode : parentNodes) {
                Edge edge = graph.getEdge(parentNode, node);
                if (precomputeMode) {
                    if (edge.isBackward())
                        edges.add(edge);
                } else if (loopMode) {
                    edges.add(edge);
                } else {
                    if (!edge.isBackward())
                        edges.add(edge);
                }
            }
            return edges;
        }

//        private void updateEdgeSignals(int level) throws Exception {
//            if (splitOn == EMultiSignalProps.SPLIT_INCOMING)
//                updateEdgeSignals_incoming(level);
//            else
//                updateEdgeSignals_outgoing(level);
//        }

        private void updateEdgeSignals(int level) throws Exception {
            for (Node node : levelNodesMap.get(level)) {
                ArrayList<Node> parentNodes = graph.getParentNodes(node);
                ArrayList<Edge> edges = collectEdges(node, parentNodes);


                for (Edge edge : edges) {
                    double source;
                    double target;
                    if (splitOn == EMultiSignalProps.SPLIT_OUTGOING) {
                        source = edge.getSource().getSignal() * edge.getWeight();
                        target = edge.getTarget().getValue();
                    } else {
                        source = edge.getSource().getSignal();
                        target = edge.getWeight() * edge.getTarget().getValue();
                    }
                    double signal;
                    try {
                        signal = updateScoreBySimpleRule(source, target, edge.getEdgeType());
                    } catch (Exception e) {
                        String message = "Exception at rule calculation for edge " + edge.toString()
                                + ". Reason: " + e.getMessage();
//                        JOptionPane.showMessageDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),message, "PSFC rule calculation problem", JOptionPane.OK_OPTION);
                        logger.debug(message);
                        throw new Exception(message, e);
                    }
                    edge.setSignal(signal);
                }

            }

        }


        /**
         * Assign edge weights by splitting the edges between each node
         * in the given level and its parent nodes in previous levels or all levels if
         * loopMode or precomputeMode is ON.
         * <p/>
         * If the split rule is "equal", the edge weights are equal to 1/count(edges).
         * If the split rule is "proportional", the weight of each edge is the ratio of
         * the signal at its source node to the sum of signals of all edge sources.
         *
         * @param nextLevel
         */
        private void assignEdgeWeights_incoming(int nextLevel) {
            for (Node node : levelNodesMap.get(nextLevel)) {
                ArrayList<Node> parentNodes = graph.getParentNodes(node);
                ArrayList<Edge> edges = collectEdges(node, parentNodes);

                if (splitRule == EMultiSignalProps.SPLIT_EQUAL)
                    for (Edge edge : edges) {
                        edge.setWeight(1. / edges.size());
                    }
                else {
                    double nodeScoreSum = 0;
                    for (Edge edge : edges)
                        nodeScoreSum += Math.abs(edge.getSource().getSignal());
                    if (nodeScoreSum == 0)
                        for (Edge edge : edges)
                            edge.setWeight(1.);
                    for (Edge edge : edges)
                        edge.setWeight(edge.getSource().getSignal() / nodeScoreSum);
                }
            }
        }

        /**
         * Assign edge weights by splitting the edges between each node
         * in the given level and its child nodes in next levels or all levels
         * if loopMode or precomputeMode is ON.
         * If the split rule is "equal", the edge weights are equal to 1/count(edges).
         * If the split rule is "proportional", the weight of each edge is the ratio of
         * the signal at its target node to the sum of signals of all edge targets.
         *
         * @param level
         */
        private void assignEdgeWeigths_outgoing(int level) {
            // new code
//            int level = prevLevel + 1;
            for (Node node : levelNodesMap.get(level)) {
                ArrayList<Node> parentNodes = graph.getParentNodes(node);
                ArrayList<Node> parentNodesNeeded = new ArrayList<Node>();
                for (Node parentNode : parentNodes) {
                    Edge edge = graph.getEdge(parentNode, node);
                    if (precomputeMode && edge.isBackward()) {
                        parentNodesNeeded.add(parentNode);
                    } else if (loopMode) {
                        parentNodesNeeded.add(parentNode);
                    } else if (!edge.isBackward()) {
                        parentNodesNeeded.add(parentNode);
                    }
                }
                for (Node parentNode : parentNodesNeeded) {
                    ArrayList<Node> childNodes = graph.getChildNodes(parentNode);
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    for (Node childNode : childNodes) {
                        Edge edge = graph.getEdge(parentNode, childNode);
                        edges.add(edge);
                    }
                    if (!edges.isEmpty())
                        if (splitRule == EMultiSignalProps.SPLIT_EQUAL)
                            for (Edge edge : edges) {
                                edge.setWeight(1. / edges.size());
                            }
                        else {
                            double nodeScoreSum = 0;
                            for (Edge edge : edges)
                                nodeScoreSum += Math.abs(edge.getTarget().getSignal());
                            if (nodeScoreSum == 0)
                                for (Edge edge : edges)
                                    edge.setWeight(1.);
                            for (Edge edge : edges)
                                edge.setWeight(edge.getTarget().getSignal() / nodeScoreSum);
                        }
                }
            }
//            // end of new code
//            for (Node node : levelNodesMap.get(prevLevel)) {
//                ArrayList<Node> childNodes = graph.getChildNodes(node);
//                ArrayList<Edge> edges = new ArrayList<Edge>();
//                for (Node childNode : childNodes) {
//                    Edge edge = graph.getEdge(node, childNode);
//                    if(precomputeMode) {
//                        if (edge.isBackward())
//                            edges.add(edge);
//                    } else if(loopMode){
//                        edges.add(edge);
//                    } else{
//                        if(!edge.isBackward())
//                            edges.add(edge);
//                    }
//                }


//            }
        }

        private double updateScoreBySimpleRule(double source, double target, String edgeType)
                throws Exception {
            if (!edgeTypeRuleMap.containsKey(edgeType)) {
                throw new Exception("No rule found for edge type " + edgeType);
            }
            String rule = edgeTypeRuleMap.get(edgeType);
            Calculable calculable = new ExpressionBuilder(rule)
                    .withVariable(SOURCE, source)
                    .withVariable(TARGET, target)
                    .build();
            double result;
            try {
                result = calculable.calculate();
            } catch (ArithmeticException e) {
                if (e.getMessage().equals("Division by zero!")) {
                    result = Double.POSITIVE_INFINITY;
                    logger.debug(e.getMessage() + Double.POSITIVE_INFINITY + " assigned");
                } else {
                    result = Double.NaN;
                    logger.debug(e.getMessage());
                }
            }
//            System.out.println(rule + " " + source + ":" + target + " = " + result);
            return result;
        }

        public HashMap<Integer, HashMap<Node, Double>> getLevelNodeSignalMap() {
            for (int level : levelNodesMap.keySet()) {
                HashMap<Node, Double> nodeSignalMap = new HashMap<Node, Double>();
                for (Node node : levelNodesMap.get(level))
                    nodeSignalMap.put(node, node.getSignal(iteration));
                levelNodeSignalMap.put(level, nodeSignalMap);
            }
            return levelNodeSignalMap;
        }

        public HashMap<Integer, HashMap<Edge, Double>> getLevelEdgeSignalMap() {
            HashMap<Integer, HashMap<Edge, Double>> levelEdgeSignalMap = new HashMap<Integer, HashMap<Edge, Double>>();
            for (int level : levelNodesMap.keySet()) {
                ArrayList<Edge> edgesAtLevel = GraphManager.getIncomingEdgesAtLevel(graph, level);
                HashMap<Edge, Double> edgeSignalMap = new HashMap<Edge, Double>();
                for (Edge edge : edgesAtLevel) {
                    edgeSignalMap.put(edge, edge.getSignal());
                }
                levelEdgeSignalMap.put(level, edgeSignalMap);
            }
            return levelEdgeSignalMap;
        }
    }

    public static void main(String[] args) {


    }
}
