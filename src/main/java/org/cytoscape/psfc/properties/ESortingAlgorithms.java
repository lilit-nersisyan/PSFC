package org.cytoscape.psfc.properties;

import org.cytoscape.psfc.logic.algorithms.GraphSort;

/**
 * Created by User on 7/6/2014.
 */
public enum ESortingAlgorithms {
    TOPOLOGICALSORT(GraphSort.TOPOLOGICALSORT, "Topological sort"),
    SHORTESTPATHSORT(GraphSort.SHORTESTPATHSORT, "Shortest path sort"),
    BFSSORT(GraphSort.BFSSORT, "Breadth first sort"),
    DFSSORT(GraphSort.DFSSORT, "Depth first sort")
    ;

    private int num;
    private String name;
    private ESortingAlgorithms(int num, String name){
        this.num = num;
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public static String getName(int num){
        for (ESortingAlgorithms sortingAlgorithm : ESortingAlgorithms.values()){
            if (sortingAlgorithm.getNum() == num)
                return sortingAlgorithm.getName();
        }
        throw new IllegalArgumentException("No sorting algorithm with num " + num);
    }

    public static int getNum(String name){
        for (ESortingAlgorithms sortingAlgorithm : ESortingAlgorithms.values()){
            if (sortingAlgorithm.getName().equals(name))
                return sortingAlgorithm.getNum();
        }
        throw new IllegalArgumentException("No sorting algorithm with name " + name);
    }

    public static String[] getAlgorithmNames() {
        String[] names = new String[ESortingAlgorithms.values().length];
        int i = 0;
        for (ESortingAlgorithms sortingAlgorithm : ESortingAlgorithms.values()){
            names[i++] = sortingAlgorithm.getName();
        }
        return names;
    }

}
