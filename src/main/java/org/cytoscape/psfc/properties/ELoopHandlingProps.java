package org.cytoscape.psfc.properties;

/**
 * Created by User on 6/5/2015.
 */
public enum ELoopHandlingProps {
    LoopHandling("loopHandling"),
    ConvergenceThreshold("convergenceThreshold"),
    MaxNumOfIterations("maxNumOfIterations");

    public static final String IGNORE_LOOPS = "ignore_loops";
    public static final String PRECOMPUTE_LOOPS = "precompute_loops";
    public static final String ITERATE_UNTIL_CONVERGENCE = "iterate_until_convergence";

    public static final Double CONVERGENCE_THRESHOLD_DEFAULT = 1.; //percentage
    public static final Integer MAX_NUM_OF_ITERATION_DEFAULT = 10;



    private String name;
    ELoopHandlingProps(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
