package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.psfc.ExceptionMessages;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Created by User on 5/26/2014.
 */
public class GraphSort {

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

    public static void closestFirstSort(Graph graph) {

        if (graph.getInputNodes().isEmpty())
            throw new IllegalArgumentException(ExceptionMessages.EmptyGraph);

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
                nextLevel=true;
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

    public static TreeMap<Integer, ArrayList<Node>> shortestPathIterator(Graph graph){
        DijkstraShortestPath<Node, Edge> dijkstraShortestPath;
        Node startVertex = graph.getOrCreateUniqueInputNode();
        TreeMap<Integer, ArrayList<Node>> levelNodeMap = new TreeMap<Integer, ArrayList<Node>>();
        for (Node node : graph.getNodeMap().values()){
            dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(
                    graph.getJgraph(), startVertex, node);
            int pathLength = (int) dijkstraShortestPath.getPathLength();
            if (!levelNodeMap.containsKey(pathLength))
                levelNodeMap.put(pathLength, new ArrayList<Node>());
            levelNodeMap.get(pathLength).add(node);
        }
        System.out.println(levelNodeMap);
        return levelNodeMap;
    }

}
