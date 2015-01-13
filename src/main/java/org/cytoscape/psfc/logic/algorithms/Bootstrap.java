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

    public static int SAMPLECENTRIC = 0;
    public static int GENECENTRIC = 1;
    public static int minNumOfSamplings = 200;
    private final Logger logger;

    private PSF psf;
    private int numOfSamplings = minNumOfSamplings;
    private int SAMPLINGTYPE = SAMPLECENTRIC;
    private Graph graph;
    private ArrayList<Node> targetNodes;
    private HashMap<Node, Double> originalNodeValues = new HashMap<Node, Double>();
    private int cycle = 0;
    private TaskMonitor taskMonitor = null;


    public Bootstrap(int numOfSamplings, int SAMPLINGTYPE,
                     PSF psf,
                     Logger logger) {
        if (numOfSamplings > minNumOfSamplings)
            this.numOfSamplings = numOfSamplings;
        if (SAMPLINGTYPE == SAMPLECENTRIC || SAMPLINGTYPE == GENECENTRIC)
            this.SAMPLINGTYPE = SAMPLINGTYPE;
        this.psf = psf;
        this.graph = psf.getGraph();
        this.logger = logger;
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

    public void setNumOfSamplings(int numOfSamplings) {
        if (numOfSamplings > minNumOfSamplings)
            this.numOfSamplings = numOfSamplings;
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

        logger.debug("Performing bootstrap cycles:\n");
        for (int sampling = 0; sampling < numOfSamplings; sampling++) {
            //reassign node values according to the resampling type
            this.cycle = sampling;
            logger.debug("Bootstrap cycle " + sampling);
            if(taskMonitor != null) {
                taskMonitor.setProgress(cycle/((double)numOfSamplings));
//                taskMonitor.setStatusMessage("Bootstrap cycle: " + cycle + " (of )" + numOfSamplings);
            }
            resample();

            //calculate PSF with the resampled values and keep the
            // target signals in a priority queue per each target node
            try {
                psf.calculateFlow();
                for (Node target : targetNodes)
                    targetSampleValuesMap.get(target).add(target.getSignal());
            } catch (Exception e) {
                throw new Exception("Exception at psf computation at sampling number "
                        + sampling, e);
            }
        }

        // calculate p values of target signals comparing them to the proportion of signals
        // with higher absolute values
        for (Node target : targetNodes) {
            double signal = targetNodeSignals.get(target);
            PriorityQueue<Double> bootstrapValues = targetSampleValuesMap.get(target);
            double mean = 0;
            for (int i =0; i < bootstrapValues.size(); i++)
                mean += bootstrapValues.peek();
            mean /= bootstrapValues.size();

            PriorityQueue<Double> polledValues = new PriorityQueue<Double>();
            double value;
            while (signal >= (value = bootstrapValues.poll())) {
                polledValues.add(value);
            }
            if (signal >= mean)
                targetPvalueMap.put(target, ((numOfSamplings-polledValues.size())/((double)numOfSamplings)));
            else
                targetPvalueMap.put(target, (polledValues.size()/((double)numOfSamplings)));
        }
        System.out.println(targetPvalueMap);
        logger.debug("Bootstrap computation complete\n");
        logger.debug("p values for target nodes:\n");
        for (Node target : targetNodes) {
            logger.debug(target.getName() + targetPvalueMap.get(target) + "\n");
        }
        resetGraphOriginalValues();

        return targetPvalueMap;
    }

    private void resetGraphOriginalValues() {
        for (Node node : graph.getNodes())
            node.setValue(originalNodeValues.get(node));
    }


    private void resample() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.addAll(graph.getNodes());
        if (SAMPLINGTYPE == SAMPLECENTRIC) {
            int[][] one2oneCor = getOne2OneCorrespondence(graph.getOrder());
            for (int i = 0; i < graph.getOrder(); i++) {
                nodes.get(i).setValue(originalNodeValues.get(nodes.get(one2oneCor[i][1])));
            }
        } else {

        }
    }

    public static int[][] getOne2OneCorrespondence(int order) {
        int[][] one2oneCor = new int[order][2];
        LinkedList<Integer> sourceQueue = new LinkedList<Integer>();
        for (int i = 0; i < order; i++)
            sourceQueue.add(i);
        for (int i = 0; i < order; i++) {
            int j = (int) Math.floor(Math.random() * (sourceQueue.size()));
            one2oneCor[i][0] = i;
            one2oneCor[i][1] = sourceQueue.get(j);
            sourceQueue.remove(sourceQueue.get(j));
        }
        return one2oneCor;
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


    public int getCycle() {
        return cycle;
    }
}
