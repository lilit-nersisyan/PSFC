package org.cytoscape.psfc.logic.structures;



import org.apache.commons.math3.stat.inference.TTest;
import org.cytoscape.psfc.properties.EMultiSignalProps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * PUBLIC CLASS GraphInfluence
 * <p>
 * This class serves as a container for storage and retrieval of statistics associated with
 * knock-down of influence nodes in the graph and associated PSF fluctuations on the target nodes.
 */
public class GraphInfluence {
    private final int psfoption;
    private Graph graph;
    private HashMap<Integer, NodeInfluence> nodeInfluenceMap; // influence node -> NodeInfluence map

    public static GraphInfluence getGraphInfluence(Graph graph, int psfoption) {
        return new GraphInfluence(graph, psfoption);
    }

    private GraphInfluence(Graph graph, int psfoptions) {
        this.graph = graph;
        nodeInfluenceMap = new HashMap<>();
        for (Node influenceNode : graph.getNodes()) {
            nodeInfluenceMap.put(influenceNode.getID(),
                    new NodeInfluence(influenceNode.getID()));
        }
        this.psfoption = psfoptions;
    }

    /**
     * Sets original PSF values for the given sample in all the target fluctuation
     * objects associated with the given (target) node
     *
     * @param node
     * @param signal
     * @param sample
     * @throws Exception
     */
    public void setOriginalPSF(Node node, double signal, String sample) throws Exception {
        for (NodeInfluence nodeInfluence : nodeInfluenceMap.values()) {
            nodeInfluence.getTargetFluctuations().get(node.getID()).setOriginalPSF(signal, sample);
        }
    }

    public void setNewPSF(Node influenceNode, Node target, double signal, int knockdirection, String sample) throws Exception {
        nodeInfluenceMap.get(influenceNode.getID()).
                getTargetFluctuations().get(target.getID()).setNewPSF(signal, knockdirection, sample);
    }

    public HashMap<Integer, NodeInfluence> getNodeInfluenceMap() {
        return nodeInfluenceMap;
    }

    public double getDeltaPSF(Node influenceNode, Node targetNode) throws Exception {
        if(!nodeInfluenceMap.containsKey(influenceNode.getID()))
            throw new Exception("Graph influence object does not contain influence node "
                    + influenceNode);
        NodeInfluence influence = nodeInfluenceMap.get(influenceNode.getID());
        if(!influence.getTargetFluctuations().containsKey(targetNode.getID()))
            throw new Exception("Graph influence object does not contain target node "
                    + targetNode);
        return influence.getTargetFluctuations().get(targetNode.getID()).getMeanDeltaPSF();
    }

    public double getDeltaPSFperSample(Node influenceNode, Node targetNode, String sample) throws Exception {
        if(!nodeInfluenceMap.containsKey(influenceNode.getID()))
            throw new Exception("Graph influence object does not contain influence node "
                    + influenceNode);
        NodeInfluence influence = nodeInfluenceMap.get(influenceNode.getID());
        if(!influence.getTargetFluctuations().containsKey(targetNode.getID()))
            throw new Exception("Graph influence object does not contain target node "
                    + targetNode);
        return influence.getTargetFluctuations().get(targetNode.getID()).getDeltaPSFMap().get(sample);
    }

    public double getPSFsd(Node influenceNode, Node targetNode) throws Exception {
        if(!nodeInfluenceMap.containsKey(influenceNode.getID()))
            throw new Exception("Graph influence object does not contain influence node "
                    + influenceNode);
        NodeInfluence influence = nodeInfluenceMap.get(influenceNode.getID());
        if(!influence.getTargetFluctuations().containsKey(targetNode.getID()))
            throw new Exception("Graph influence object does not contain target node "
                    + targetNode);
        return influence.getTargetFluctuations().get(targetNode.getID()).getPSFsd();
    }

    public double getDeltaPSFpval(Node influenceNode, Node targetNode) throws Exception {
        if(!nodeInfluenceMap.containsKey(influenceNode.getID()))
            throw new Exception("Graph influence object does not contain influence node "
                    + influenceNode);
        NodeInfluence influence = nodeInfluenceMap.get(influenceNode.getID());
        if(!influence.getTargetFluctuations().containsKey(targetNode.getID()))
            throw new Exception("Graph influence object does not contain target node "
                    + targetNode);
        return influence.getTargetFluctuations().get(targetNode.getID()).getDeltaPSFpval();
    }

    public double getNewPSF(Node influenceNode, Node targetNode, String sample) {
        return nodeInfluenceMap.get(influenceNode.getID()).getTargetFluctuations().get(targetNode.getID()).getNewPSFMap().get(sample);
    }

    /**
     * PRIVATE CLASS NodeInfluence
     * <p>
     * Keeps the map of PSF fluctuations on the graph target nodes, if this particular influence node is knocked down
     */
    private class NodeInfluence {
        private Integer influenceNodeID;
        private HashMap<Integer, PSFFluctuations> targetFluctuations = new HashMap<>(); // targetNodeID -> PSF fluctuations map

        public NodeInfluence(int influenceNodeID) {
            this.influenceNodeID = influenceNodeID;
            for (Node targetNode : graph.getNodes()) {
                targetFluctuations.put(targetNode.getID(),
                        new PSFFluctuations(targetNode.getID()));
            }
        }

        public HashMap<Integer,PSFFluctuations> getTargetFluctuations() {
            return targetFluctuations;
        }

        public Integer getInfluenceNodeID() {
            return influenceNodeID;
        }
    }


    /**
     * PRIVATE CLASS PSFFluctuations
     * <p>
     * This class keeps original, new and delta PSF values of a (target) node across samples
     * Allows to get statistics, like variance, median and mean difference and p values for these differences.
     */
    private class PSFFluctuations {
        private Integer targetNodeID;
        private HashMap<String, Double> originalPSFMap = new HashMap<>(); // sample -> PSF map
        private HashMap<String, Double> newPSFMap = new HashMap<>(); // sample -> perturbed PSF map
        private HashMap<String, Double> deltaPSFMap = new HashMap<>(); // sample -> deltaPSF map
        private HashMap<String, Integer> knockdirectionMap
                = new HashMap<>(); // sample -> {-1, 1} map indicating if the gene was knocked down or knocked up
                                    // knock-down = -1, knock-up (or knock-nowhere) = 1

        PSFFluctuations(int targetNodeID) {
            this.targetNodeID = targetNodeID;
        }


        void setOriginalPSF(double originalPSF, String sample) {
            this.originalPSFMap.put(sample, originalPSF);
        }

        void setNewPSF(double newPSF, int knockdirection, String sample) throws Exception {
            if(!originalPSFMap.containsKey(sample))
                throw new Exception("Problem setting new PSF values for sample "
                        + sample + " at target node " + targetNodeID
                        + "\nReason: the original PSF values did not contain the sample"
                        + sample);
            this.newPSFMap.put(sample, newPSF);
            this.knockdirectionMap.put(sample, knockdirection);
        }

        public HashMap<String, Double> getOriginalPSFMap() {
            return originalPSFMap;
        }

        public HashMap<String, Double> getNewPSFMap() {
            return newPSFMap;
        }

        public HashMap<String, Double> getDeltaPSFMap() throws Exception {
            ArrayList<String> samples = getSamples();
            double[] deltas = getDeltaPSFValues(samples);
            deltaPSFMap.clear();
            for (int i = 0; i < samples.size(); i++) {
                deltaPSFMap.put(samples.get(i), deltas[i]);
            }
            return deltaPSFMap;
        }

        double getMeanDeltaPSF() {
            double[] deltas = getDeltaPSFValues(getSamples());
            double sum = 0;
            for(int i = 0; i < deltas.length; i++){
                sum += deltas[i];
            }
            return sum/deltas.length;
        }

        double getPSFsd() {
            ArrayList<String> samples = getSamples();
            if (samples.size() < 2) {
                return Double.NaN;
            }
            double[] deltas = getDeltaPSFValues(samples);
            double mean = getMeanDeltaPSF();
            double sum = 0;
            for(int i = 0; i < deltas.length; i++){
                sum += (deltas[i] - mean)*(deltas[i] - mean);
            }
            return Math.sqrt(sum/(deltas.length-1));
        }

        double getDeltaPSFpval() {
            ArrayList<String> samples = getSamples();
            if (samples.size() < 2) {
                return Double.NaN;
            }
            TTest ttest = new TTest();
            return ttest.pairedTTest(getNewPSFValues(samples), getOriginalPSFValues(samples));
        }


        private double[] getNewPSFValues(ArrayList<String> samples){
            double[] newPSFvalues = new double[samples.size()];
            for (int i = 0; i < samples.size(); i++) {
                newPSFvalues[i] = newPSFMap.get(samples.get(i));
            }
            return newPSFvalues;
        }

        private int[] getKnockdirections(ArrayList<String> samples) {
            int[] knockdirections = new int[samples.size()];
            for (int i = 0; i < samples.size(); i++) {
                knockdirections[i] = knockdirectionMap.get(samples.get(i));
            }
            return knockdirections;
        }

        private double[] getOriginalPSFValues(ArrayList<String> samples){
            double[] originalPSFvalues = new double[samples.size()];
            for (int i = 0; i < samples.size(); i++) {
                originalPSFvalues[i] = originalPSFMap.get(samples.get(i));
            }
            return originalPSFvalues;
        }

        private double[] getDeltaPSFValues(ArrayList<String> samples){
            double[] newPSFvalues = getNewPSFValues(samples);
            double[] originalPSFValues = getOriginalPSFValues(samples);
            double[] deltaPSFValues = new double[newPSFvalues.length];
            int[] knockdirections = getKnockdirections(samples);
            for(int i = 0; i < deltaPSFValues.length; i++){
                if(psfoption == EMultiSignalProps.ADDITION)
                    deltaPSFValues[i] = (originalPSFValues[i]-newPSFvalues[i]) * knockdirections[i];
                else {
                    double pseudocount = 0;
                    if (knockdirections[i] < 0) {
                        if (originalPSFValues[i] == 0)
                            pseudocount = 0.0001;
                        deltaPSFValues[i] = (newPSFvalues[i] + pseudocount) / (originalPSFValues[i] + pseudocount);
                    } else {
                        if (newPSFvalues[i] == 0)
                            pseudocount = 0.0001;
                        deltaPSFValues[i] = (originalPSFValues[i] + pseudocount) / (newPSFvalues[i] + pseudocount);
                    }
                }
            }
            return deltaPSFValues;
        }

        private ArrayList<String> getSamples() {
            ArrayList<String> samples = new ArrayList<>(originalPSFMap.keySet());
            Collections.sort(samples);
            return samples;
        }

    }

}
