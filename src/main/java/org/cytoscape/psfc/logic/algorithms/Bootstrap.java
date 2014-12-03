package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * This class performs bootstrap sampling with replacement for calculation of
 * significance for the pathway flow values.
 */
public class Bootstrap {
    public static int SAMPLECENTRIC = 0;
    public static int GENECENTRIC = 1;
    public static int minNumOfSamplings = 200;

    private PSF psf;
    private int numOfSamplings = minNumOfSamplings;
    private int SAMPLINGTYPE = SAMPLECENTRIC;
    private Graph graph;
    private ArrayList<Node> targetNodes;
    private TreeMap<Node, Double> originalNodeValues = new TreeMap<Node, Double>();


    public Bootstrap(int numOfSamplings, int SAMPLINGTYPE,
                     PSF psf,
                     Logger logger) {
        this.numOfSamplings = numOfSamplings;
        this.SAMPLINGTYPE = SAMPLINGTYPE;
        this.psf = psf;
        this.graph = psf.getGraph();
    }

    public void setNumOfSamplings(int numOfSamplings) {
        if (numOfSamplings > minNumOfSamplings)
            this.numOfSamplings = numOfSamplings;
    }

    public TreeMap<Node, Double> performBootstrapTest() throws Exception {
        for (Node node : graph.getNodes()) {
            originalNodeValues.put(node, node.getValue());
        }
        targetNodes = graph.getTargetNodes();

        if (targetNodes.isEmpty())
            return null;

        TreeMap<Node, Double> targetNodeSignals = new TreeMap<Node, Double>();
        TreeMap<Node, Double> targetPvalueMap = new TreeMap<Node, Double>();
        TreeMap<Node, PriorityQueue<Double>> targetSampleValuesMap
                = new TreeMap<Node, PriorityQueue<Double>>();
        for (Node target : targetNodes) {
            targetPvalueMap.put(target, 0.);
            targetSampleValuesMap.put(target, new PriorityQueue<Double>());
            targetNodeSignals.put(target, target.getSignal());
        }


        for (int sampling = 0; sampling < numOfSamplings; sampling++) {
            resample();
            try {
                psf.calculateFlow();
                for (Node target : targetNodes)
                    targetSampleValuesMap.get(target).add(target.getSignal());
            } catch (Exception e) {
                throw new Exception("Exception at psf computation at sampling number "
                        + sampling, e);
            }
        }
        for (Node target : targetNodes) {
            double signal = targetNodeSignals.get(target);
            PriorityQueue<Double> bootstrapValues = targetSampleValuesMap.get(target);
            PriorityQueue<Double> polledValues = new PriorityQueue<Double>();
            double value;
            while (signal > (value = bootstrapValues.poll()))
                polledValues.add(value);
            if (signal >= 0)
                targetPvalueMap.put(target, (double) ((numOfSamplings-polledValues.size())/numOfSamplings));
            else
                targetPvalueMap.put(target, (double) (polledValues.size()/numOfSamplings));
        }

        resetGraphOriginalValues();
        return targetPvalueMap;
    }

    private void resetGraphOriginalValues() {
        for (Node node : graph.getNodes())
            node.setValue(originalNodeValues.get(node));
    }

    private void initFields() {

    }

    private void resample() {
        ArrayList<Node> nodes = (ArrayList<Node>) graph.getNodes();
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


}
