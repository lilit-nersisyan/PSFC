package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.work.TaskMonitor;

import java.util.*;

/**
 * This class performs bootstrap sampling with replacement for calculation of
 * significance for the pathway flow values.
 */
public class Bootstrap {
    public static String NUMOFSAMPLINGSPROP = "numOfSamplings";
    public static String SAMPLINGTYPEPROP = "samplingType";
    public static String EXPMATRIXFILE = "expMatrixFile";

    public static int SAMPLECENTRIC = 0;
    public static int GENECENTRIC = 1;
    public static int minNumOfSamplings = 2;
    public static int defaultNumOfSamplings = 200;
    public static int maxNumOfSamplings = 500;
    private final Logger logger;

    private PSF psf;
    private int numOfSamplings = minNumOfSamplings;
    private int SAMPLINGTYPE = SAMPLECENTRIC;
    protected Graph graph;
    private ArrayList<Node> targetNodes;
    protected HashMap<Node, Double> originalNodeValues = new HashMap<Node, Double>();
    private int cycle = 0;
    private TaskMonitor taskMonitor = null;


    public Bootstrap(PSF psf, Logger logger){
        this.psf = psf;
        this.logger = logger;
        this.graph = psf.getGraph();
    }

    public Bootstrap(PSF psf, int numOfSamplings, Logger logger){
        this.psf = psf;
        this.graph = psf.getGraph();
        this.numOfSamplings = numOfSamplings;

        this.logger = logger;
    }

    //for testing only
    public Bootstrap(int numOfSamplings) {
        this.numOfSamplings = numOfSamplings;
        this.logger = null;
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

    boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HashMap<Node, Double> performBootstrap() throws Exception {
        logger.debug("Performing bootstrap significance test with parameters:\n");
        logger.debug(NUMOFSAMPLINGSPROP + "\t" + numOfSamplings);
        logger.debug(SAMPLINGTYPEPROP + "\t" + SAMPLINGTYPE);

        // keep the original values of the nodes in a map
        for (Node node : graph.getNodes()) {
            originalNodeValues.put(node, node.getValue());
        }
        // get target nodes from the graph
        targetNodes = graph.getTargetNodes();

        // keep target node signals from the PSF computation on the original graph
        // initiate the map of p values for the target nodes with 0s
        // initiate a queue for target nodes based on the signal values after each resampling
        HashMap<Node, Double> targetNodeSignals = new HashMap<Node, Double>();
        HashMap<Node, Double> targetPvalueMap = new HashMap<Node, Double>();
        HashMap<Node, PriorityQueue<Double>> targetSampleValuesMap
                = new HashMap<Node, PriorityQueue<Double>>();
        for (Node target : targetNodes) {
            targetPvalueMap.put(target, 0.);
            targetSampleValuesMap.put(target, new PriorityQueue<Double>());
            targetNodeSignals.put(target, target.getSignal());
        }

        // loop on the number of resamplings
        psf.setSilentMode(true); // restrict PSF logging

        logger.debug("Performing bootstrap for " + numOfSamplings + " cycles:\n");
        for (int sampling = 0; sampling < numOfSamplings; sampling++) {
            if(cancelled) {
                String message = "Bootstrap cancelled at sampling: " + sampling;
                logger.debug(message);
                System.out.println("PSFC:: " + message);
                break;
            }
            //reassign node values according to the resampling type
            this.cycle = sampling;
            logger.debug("Bootstrap cycle " + sampling);
            if (taskMonitor != null) {
                taskMonitor.setProgress(cycle / ((double) numOfSamplings));
                taskMonitor.setStatusMessage("Bootstrap cycle: " + cycle + " (of )" + numOfSamplings);
            }
            resample();
            logger.debug(graph.toString());

            //calculate PSF with the resampled values and keep the
            // target signals in a priority queue per each target node
            try {
                psf.calculateFlow();
                for (Node target : targetNodes) {
                    targetSampleValuesMap.get(target).add(target.getSignal());
//                    logger.debug(sampling + ": " + target.getName() + " - " +  target.getSignal());
                }
            } catch (Exception e) {
                throw new Exception("Exception at psf computation at sampling number "
                        + sampling, e);
            }
        }


        for (Node target : targetNodes) {
            double signal = targetNodeSignals.get(target);
            PriorityQueue<Double> bootstrapValues = targetSampleValuesMap.get(target);
            logger.debug("Bootstrap summary for target node: " + target.getName());
            targetPvalueMap.put(target, getBootstrapPValue(signal, bootstrapValues));
        }
//        System.out.println(targetPvalueMap);
        logger.debug("Bootstrap computation complete\n");

        resetGraphOriginalValues();

        return targetPvalueMap;
    }

    /**
     * Calculates the p value of the given value based on the proportion
     * of bigger bootstrap values.
     * If the given value is less than the mean of the queue, the proportion of lesser values is computed.
     * Otherwise, the proportion of greater or equal values is computed.
     * This takes into consideration the case where all the values in the queue are equal to the given value.
     * In this case the numOfSamplings - lessValues.size() will be equal to the numOfSamplings, and the p value will be 1.
     *
     * @param value           : the value for which the significance should be computed
     * @param bootstrapValues : the values generated with bootstrap resampling
     * @return p value
     */
    public double getBootstrapPValue(double value, PriorityQueue<Double> bootstrapValues) throws Exception {
        if (bootstrapValues.isEmpty()) {
            if (logger != null)
                logger.debug("Empty queue!");
            return 1;
        }
        int size = bootstrapValues.size();
        double mean = 0;
        for (Double bv : bootstrapValues) mean += bv;
        mean /= bootstrapValues.size();
        double bootstrapValue;
        PriorityQueue<Double> lessValues = new PriorityQueue<Double>();
        while (!bootstrapValues.isEmpty() && value > (bootstrapValue = bootstrapValues.poll())) {
            lessValues.add(bootstrapValue);
        }
        double pValue;
        if (logger != null)
            logger.debug(String.format("#Test value: %f\t#Queue mean: %f",value,mean));
        if (value >= mean) {
            int gteq = size - lessValues.size();
            pValue = gteq / ((double) size);
            if (logger != null) {
                logger.debug(String.format("#Number of extreme (gteq) values: %d out of %d", gteq, size));
                logger.debug(String.format("#p value: " + "%d/%d = %f", gteq, size, pValue));
            }
        } else {
            int lt = lessValues.size();
            pValue = lt / ((double) size);
            if (logger != null) {
                logger.debug(String.format("#Number of extreme (lt) values: %d out of %d",lt,size));
                logger.debug(String.format("p value: " + "%d/%d = %f", lessValues.size(), size, pValue));
            }
        }
        return pValue;
    }

    private void resetGraphOriginalValues() {
        for (Node node : graph.getNodes())
            node.setValue(originalNodeValues.get(node));
    }

    /**
     * Resampling is intended to assign nodes in the graph another value
     * - either taken from other nodes in the graph (sample-centric)
     * or from gene expression matrix (gene-centric).
     *
     * This method is implemented in each of respective child classes correspondingly.
     *
     */
    public void resample() {
    }


    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
        queue.add(0);
        queue.add(-2);
        queue.add(5);
        System.out.println(queue);
        System.out.println(queue.peek());
        System.out.println(queue);
    }

}
