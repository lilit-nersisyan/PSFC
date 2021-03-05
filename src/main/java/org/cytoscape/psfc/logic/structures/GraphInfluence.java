package org.cytoscape.psfc.logic.structures;



import org.apache.commons.math3.stat.inference.TTest;

import java.util.HashMap;

/**
 * PUBLIC CLASS GraphInfluence
 * <p>
 * This class serves as a container for storage and retrieval of statistics associated with
 * knock-down of influence nodes in the graph and associated PSF fluctuations on the target nodes.
 */
public class GraphInfluence {
    private Graph graph;
    private HashMap<Integer, NodeInfluence> nodeInfluenceMap; // influence node -> NodeInfluence map

    private static GraphInfluence instance = null;

    public static GraphInfluence getGraphInfluence(Graph graph) {
        if(instance == null){
            instance = new GraphInfluence(graph);
        }
        return instance;
    }

    private GraphInfluence(Graph graph) {
        this.graph = graph;
        nodeInfluenceMap = new HashMap<>();
        for (Node influenceNode : graph.getNodes()) {
            nodeInfluenceMap.put(influenceNode.getID(),
                    new NodeInfluence(influenceNode.getID()));
        }
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
            for(String sample : originalPSFMap.keySet()){
                if(!newPSFMap.containsKey(sample))
                    throw new Exception("Exception when computing deltaPSFMap:\n "
                            +  "The sample " + sample + " is present in original PSF values, "
                            + " but no found in new ones");
                deltaPSFMap.put(sample, originalPSFMap.get(sample) - newPSFMap.get(sample));
            }
            return deltaPSFMap;
        }

        double getMeanDeltaPSF() {
            double[] deltas = getDeltaPSFValues();
            double sum = 0;
            for(int i = 0; i < deltas.length; i++){
                sum += deltas[i];
            }
            return sum/deltas.length;
        }

        double getPSFsd() {
            double[] deltas = getDeltaPSFValues();
            double mean = getMeanDeltaPSF();
            double sum = 0;
            for(int i = 0; i < deltas.length; i++){
                sum += (deltas[i] - mean)*(deltas[i] - mean);
            }
            return Math.sqrt(sum/(deltas.length-1));
        }

        double getDeltaPSFpval() {
            TTest ttest = new TTest();
            return ttest.pairedTTest(getNewPSFValues(), getOriginalPSFValues());
        }


        private double[] getNewPSFValues(){
            double[] newPSFvalues = new double[newPSFMap.size()];
            int i = 0;
            for(double value : newPSFMap.values()){
                newPSFvalues[i++] = value;
            }
            return newPSFvalues;
        }

        private int[] getKnockdirections() {
            int[] knockdirections = new int[knockdirectionMap.size()];
            int i = 0;
            for(int value : knockdirectionMap.values()){
                knockdirections[i++] = value;
            }
            return knockdirections;
        }

        private double[] getOriginalPSFValues(){
            double[] originalPSFvalues = new double[originalPSFMap.size()];
            int i = 0;
            for(double value : originalPSFMap.values()){
                originalPSFvalues[i++] = value;
            }
            return originalPSFvalues;
        }

        private double[] getDeltaPSFValues(){
            double[] newPSFvalues = getNewPSFValues();
            double[] originalPSFValues = getOriginalPSFValues();
            double[] deltaPSFValues = new double[newPSFvalues.length];
            int[] knockdirections = getKnockdirections();
            for(int i = 0; i < deltaPSFValues.length; i++){
                deltaPSFValues[i] = (newPSFvalues[i] - originalPSFValues[i]) * knockdirections[i];
            }
            return deltaPSFValues;
        }

    }

}
