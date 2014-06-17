package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.psfc.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
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
            case TOPOLOGICALSORT:
                return sortByLevelFromStart(graph, topologicalOrderIterator(graph));
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
                if (nextNode != null)
                    levelSet.add(nextNode);
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
            }
        }
        //Last level
        if(nextLevel && nextNode != null){
            levelSet = new ArrayList<Node>();
            level++;
            levelsMap.put(level, levelSet);
            levelSet.add(nextNode);
        }


        return levelsMap;
    }

}
