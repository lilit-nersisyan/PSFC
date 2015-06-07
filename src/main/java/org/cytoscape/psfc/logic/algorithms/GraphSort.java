package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.psfc.gui.enums.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.GraphTestCases;
import org.cytoscape.psfc.logic.structures.Node;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

/**
 * PUBLIC CLASS GraphSort
 * <p/>
 * This class provides a number of static methods for sorting graphs based on different algorithms.
 */
public class GraphSort {

    public static final int SHORTESTPATHSORT = 0;
    public static final int CLOSESTFIRSTSORT = 1;
    public static final int BFSSORT = 2;
    public static final int DFSSORT = 3;
    public static final int TOPOLOGICALSORT = 4;

    private static TreeMap<Integer, ArrayList<Node>> levelNodeMap;
    private static HashMap<Node, Integer> nodeLevelMap;
    private static ArrayList<Integer> algorithms = new ArrayList<Integer>(
            Arrays.asList(SHORTESTPATHSORT, CLOSESTFIRSTSORT, BFSSORT, DFSSORT, TOPOLOGICALSORT));

    /**
     * Apply the chosen method for sorting the
     *
     * @param sortingAlgorithm
     */
    public static TreeMap<Integer, ArrayList<Node>> sort(Graph graph, int sortingAlgorithm) {
        switch (sortingAlgorithm) {
            case SHORTESTPATHSORT:
                return shortestPathIterator(graph);
            case CLOSESTFIRSTSORT:
                closestFirstSort(graph);
                break;
            case BFSSORT:
                bsfIterate(graph);
                break;
            case TOPOLOGICALSORT: {
                ArrayList<Edge> removedEdges = removeLoopEdges(graph);
                TreeMap<Integer, ArrayList<Node>> levelNodeMap = sortByLevelFromStart(graph, topologicalOrderIterator(graph));
                for (Edge edge : removedEdges) {
                    System.out.println("Edge removed for sorting: " + edge);
                    edge.setIsBackward(true);
                    graph.addEdge(edge.getSource(), edge.getTarget());
                }

                return levelNodeMap;
            }
            default:
                throw new IllegalArgumentException(ExceptionMessages.NoSuchAlgorithm.getMessage());
        }
        return null;
    }

    public static GraphIterator topologicalOrderIterator(Graph graph) {
        GraphIterator<Node, Edge> graphIterator =
                new TopologicalOrderIterator<Node, Edge>(graph.getJgraph());
        return graphIterator;
    }


    public static TreeMap<Integer, ArrayList<Node>> getLevelNodeMap() {
        return levelNodeMap;
    }

    public static HashMap<Node, Integer> getNodeLevelMap() {
        return nodeLevelMap;
    }

    /**
     * This assigns levels to each of the node according to its distance from the start node,
     * and returns a map, where keys are levels, and values are lists of Nodes having that level.
     * A start node is a unique node in the graph with in-degree 0.
     * If there are many such nodes, a new node is created and connected to all 0 in-degree nodes.
     *
     * @param graph the Graph to be sorted
     * @return <code>TreeMap</code> containing level : nodes mapping; or <code>null</code> if the graph was empty
     */
    public static TreeMap<Integer, ArrayList<Node>> shortestPathIterator(Graph graph) {
        if (graph.getOrder() == 0)
            return null;
        DijkstraShortestPath<Node, Edge> dijkstraShortestPath;
        Node startVertex = graph.getOrCreateUniqueInputNode();
        TreeMap<Integer, ArrayList<Node>> levelNodeMap = new TreeMap<Integer, ArrayList<Node>>();

        for (Node node : graph.getNodeMap().values()) {
            dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(
                    graph.getJgraph(), startVertex, node);
            int pathLength = (int) dijkstraShortestPath.getPathLength();
            if (!levelNodeMap.containsKey(pathLength))
                levelNodeMap.put(pathLength, new ArrayList<Node>());
            levelNodeMap.get(pathLength).add(node);
        }

//        System.out.println(levelNodeMap);

        return levelNodeMap;
    }

    public static void closestFirstSort(Graph graph) {

        if (graph.getInputNodes().isEmpty())
            throw new IllegalArgumentException(ExceptionMessages.EmptyGraph.getMessage());

        Node uniqueInputNode = graph.getOrCreateUniqueInputNode();
        GraphIterator iterator = new ClosestFirstIterator(graph.getJgraph(), uniqueInputNode);

        GraphIterator<Node, Edge> closestFirstIterator =
                new ClosestFirstIterator<Node, Edge>(graph.getJgraph(), uniqueInputNode);
//        while (closestFirstIterator.hasNext()) {
//            System.out.println(closestFirstIterator.next());
//        }
        TreeMap<Integer, LinkedList<Node>> levelsMap = new TreeMap<Integer, LinkedList<Node>>();
        LinkedList<Node> levelSet = null;
        LinkedList<Node> parentLevelSet = null;
        Node nextNode = null;
        boolean nextLevel = false;
        int level = 0;
        levelSet = new LinkedList<Node>();
        levelSet.add(uniqueInputNode);
        if (!uniqueInputNode.equals(closestFirstIterator.next()))
            System.out.println("*******\nSomething is wrong!!!*****\n");
        levelsMap.put(level, levelSet);
        level++;

        while (closestFirstIterator.hasNext()) {
            if (!levelsMap.containsKey(level)) {
                parentLevelSet = levelSet;
                levelSet = new LinkedList<Node>();
                levelsMap.put(level, levelSet);
                if (nextNode != null)
                    levelSet.add(nextNode);
            }
            nextNode = closestFirstIterator.next();
            System.out.println(nextNode);
            nextLevel = false;
            if (levelSet.isEmpty())
                levelSet.add(nextNode);

            else {
                nextLevel = true;
                for (Node node : parentLevelSet) {
                    if (graph.containsEdge(node, nextNode)) {
                        nextLevel = false;
                        break;
                    }
                }
                if (!nextLevel)
                    levelSet.add(nextNode);
                else {
                    level++;
                }
            }

        }
        if (nextNode != null)
            if (!levelsMap.containsKey(level)) {
                levelSet = new LinkedList<Node>();
                levelsMap.put(level, levelSet);
                if (nextNode != null)
                    levelSet.add(nextNode);
            }
        System.out.println(levelsMap);
    }

    public static void bsfIterate(Graph graph) {
        int level = 0;
        TreeMap<Integer, LinkedList<Node>> levelsMap = new TreeMap<Integer, LinkedList<Node>>();
        GraphIterator<Node, Edge> bsfIterator = new BreadthFirstIterator<Node, Edge>(graph.getJgraph());

        LinkedList<Node> levelSet = null;
        Node nextNode = null;
        boolean nextLevel = false;
        while (bsfIterator.hasNext()) {
            if (!levelsMap.containsKey(level)) {
                levelSet = new LinkedList<Node>();
                levelsMap.put(level, levelSet);
                if (nextNode != null)
                    levelSet.add(nextNode);
            }
            nextNode = bsfIterator.next();
            System.out.println(nextNode);
            nextLevel = false;
            if (levelSet.isEmpty())
                levelSet.add(nextNode);
            else {
                for (Node node : levelSet) {
                    if (graph.containsEdge(nextNode, node)) {
                        nextLevel = true;
                        break;
                    }
                }
                if (!nextLevel)
                    levelSet.add(nextNode);
                else {
                    level++;
                }
            }

        }
        if (nextNode != null)
            if (!levelsMap.containsKey(level)) {
                levelSet = new LinkedList<Node>();
                levelsMap.put(level, levelSet);
                if (nextNode != null)
                    levelSet.add(nextNode);
            }

        System.out.println(levelsMap);
    }

    public static TreeMap<Integer, ArrayList<Node>> sortByLevelFromStart(Graph graph,
                                                                         GraphIterator<Node, Edge> graphIterator) {
        TreeMap<Integer, ArrayList<Node>> levelsMap = new TreeMap<Integer, ArrayList<Node>>();
        ArrayList<Node> levelSet = new ArrayList<Node>();
        Node nextNode = null;
        boolean nextLevel = false;
        int level = 0;

        //Initialization
        if (graphIterator.hasNext()) {
            levelSet.add(graphIterator.next());
            levelsMap.put(level, levelSet);
        }

        while (graphIterator.hasNext()) {
            if (nextLevel) {
                levelSet = new ArrayList<Node>();
                level++;
                levelsMap.put(level, levelSet);
                if (nextNode != null) {
                    levelSet.add(nextNode);
                    nextNode.setLevel(level);
                }
                nextLevel = false;
            }
            nextNode = graphIterator.next();
            if (!levelSet.isEmpty()) {
                for (Node node : levelSet) {
                    if (!node.equals(nextNode))
                        if (graph.containsEdge(node, nextNode)) {
                            nextLevel = true;
                            break;
                        }
                }
            } else {
                nextLevel = false;
            }
            if (!nextLevel) {
                levelSet.add(nextNode);
                nextNode.setLevel(level);
            }
        }
        //Last level
        if (nextLevel && nextNode != null) {
            levelSet = new ArrayList<Node>();
            level++;
            levelsMap.put(level, levelSet);
            levelSet.add(nextNode);
            nextNode.setLevel(level);
        }


        return levelsMap;
    }

    public static boolean cycleExists(Graph graph) {
        CycleDetector cycleDetector = new CycleDetector(graph.getJgraph());
        return cycleDetector.detectCycles();
    }

    /**
     * Returns the first found edge between given node and any other nodes from
     * the set of nodes.
     *
     * @param node  node for which an edge should be found
     * @param set   set of nodes to search
     * @param graph Graph containing the node set
     * @return edge from given node to any node from the set; or null of no such edge exists
     */
    private static Edge findOutgoingEdge(Node node, Set<Node> set, Graph graph) {
        Edge edge = null;
        for (Node setNode : set) {
            if ((edge = graph.getEdge(node, setNode)) != null)
                return edge;
        }
        return null;
    }

    /**
     * Returns the first found edge between given node and any other nodes from
     * the set of nodes, excluding the nodeToExclude node.
     *
     * @param node          node for which an edge should be found
     * @param set           set of nodes to search
     * @param graph         Graph containing the node set
     * @param nodeToExclude node to be excluded from the set
     * @return edge from given node to any node from the set; or null of no such edge exists
     */
    private static Edge findOutgoingEdge(Node node, Set<Node> set, Node nodeToExclude, Graph graph) {
        Edge edge = null;
        for (Node setNode : set) {
            if (!setNode.equals(nodeToExclude))
                if ((edge = graph.getEdge(node, setNode)) != null)
                    return edge;
        }
        return null;
    }

    private static ArrayList<List<Edge>> detectLoops(Graph graph) {
        CycleDetector cycleDetector = new CycleDetector(graph.getJgraph());
        Set<Node> vertexSet = cycleDetector.findCycles();

        ArrayList<Node> nodesToVisit = new ArrayList<Node>();
        nodesToVisit.addAll(vertexSet);
        Iterator<Node> nodeIterator = vertexSet.iterator();

        ArrayList<List<Edge>> loops = new ArrayList<List<Edge>>();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            if (nodesToVisit.contains(node)) {
                Edge edge = findOutgoingEdge(node, vertexSet, graph);
                DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph.getJgraph(),
                        edge.getTarget(), edge.getSource());
                List<Edge> edgeList = dijkstraShortestPath.getPathEdgeList();
                if (edgeList != null) {
                    edgeList.add(edge);
                    loops.add(edgeList);
                    for (Edge edge1 : edgeList) {
                        nodesToVisit.remove(edge1.getSource());
                        nodesToVisit.remove(edge1.getTarget());
                        edge1.incrementLoopCount(1);
                    }
                }
            }
        }
        return loops;
    }

    private static ArrayList<Edge> removeLoopEdges(Graph graph) {
        boolean cycleExists = cycleExists(graph);
        int iteration = 0;
        ArrayList<Edge> removedEdges = new ArrayList<Edge>();

        while (cycleExists) {
            System.out.println("Iteration " + iteration);

            ArrayList<Edge> loopEdges = new ArrayList<Edge>();
            ArrayList<List<Edge>> loops = detectLoops(graph);
            ArrayList<Node> inputNodes = graph.getInputNodes();
            System.out.println("Loops:");
            for (List<Edge> edges : loops) {
                System.out.println("Edges:\n" + edges.toString());
                for (Edge edge : edges) {
                    if (!loopEdges.contains(edge)) {
                        loopEdges.add(edge);
                    }
                }
            }


            //Count distances of loop edges to the input
            final HashMap<Edge, Double> edgeSourceDistanceMap = new HashMap<Edge, Double>();
            for (List<Edge> edges : loops) {
                for (Edge edge : edges) {
                    double shortestPathLength = Double.MAX_VALUE;
                    for (Node iNode : inputNodes) {
                        DijkstraShortestPath<Node, Edge> dsp = new DijkstraShortestPath<Node, Edge>
                                (graph.getJgraph(), iNode, edge.getSource());
                        double pl = dsp.getPathLength();
                        if (pl < shortestPathLength)
                            shortestPathLength = pl;
                    }
                    edgeSourceDistanceMap.put(edge, shortestPathLength);
                }
            }
            System.out.println("edgesourcedistancemap:");
            for (Edge edge : edgeSourceDistanceMap.keySet()) {
                System.out.println(edge.toString() + edgeSourceDistanceMap.get(edge));
            }
            Collections.sort(loopEdges, new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    int ldiff = o2.getLoopCount() - o1.getLoopCount();
                    int ddiff = (int) (edgeSourceDistanceMap.get(o2)-edgeSourceDistanceMap.get(o1));
                    int iddiff = o2.getSource().getID()-o1.getSource().getID();
                    int diff = 10^6*ldiff + 10^4*ddiff + iddiff;
                    if(diff == 0)
                        diff = 1;
                    return diff;
                }
            });


            System.out.println(loopEdges);
            Edge maxLoopEdge = loopEdges.get(0);
            graph.removeEdge(maxLoopEdge);
            removedEdges.add(maxLoopEdge);
            System.out.println("Removed edge: " + maxLoopEdge);
            cycleExists = cycleExists(graph);
            iteration++;
            graph.resetLoopCounts();
        }
        return removedEdges;
    }

    public static void main(String[] args) {
        Graph graph = GraphTestCases.doubleSourceManyLoopsDCG();
        System.out.println(graph);
    }


}
