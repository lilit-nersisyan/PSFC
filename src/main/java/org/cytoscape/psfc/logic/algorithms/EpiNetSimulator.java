package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.ELoopHandlingProps;
import org.cytoscape.psfc.properties.EMultiSignalProps;
import org.osgi.framework.BundleContext;

import java.io.*;
import java.util.*;

/**
 * Created by User on 8/6/2015.
 */
public class EpiNetSimulator {
    String activation = "activation";
    String inhibition = "inhibition";
    String methylation = "methylation";
    String demethylation = "demethylation";
    private BundleContext BC;


    private Graph epiNetGraph() {

        Graph graph = new Graph();
        Node DNDMs = graph.addNode();
        DNDMs.setName("DNDM's");
        DNDMs.setLevel(0);

        Node cellDivision = graph.addNode();
        cellDivision.setName("Cell division");
        cellDivision.setLevel(0);

        Node K4DMs = graph.addNode();
        K4DMs.setName("K4DM's");
        K4DMs.setLevel(0);

        Node K9DMs = graph.addNode();
        K9DMs.setName("K9DM's");
        K9DMs.setLevel(0);

        Node K9MTs = graph.addNode();
        K9MTs.setName("K9MT's");
        K9MTs.setLevel(0);

        Node K27DMs = graph.addNode();
        K27DMs.setName("K27DM's");
        K27DMs.setLevel(0);

        Node K27MTs = graph.addNode();
        K27MTs.setName("K27MT's");
        K27MTs.setLevel(0);

        Node CpG = graph.addNode();
        CpG.setName("CpG");
        CpG.setLevel(1);

        Node H3K9 = graph.addNode();
        H3K9.setName("H3K9");
        H3K9.setLevel(1);

        Node H3K27 = graph.addNode();
        H3K27.setName("H3K27");
        H3K27.setLevel(1);

        Node K4MTs = graph.addNode();
        K4MTs.setName("K4MT's");
        K4MTs.setLevel(1);

        Node PcG = graph.addNode();
        PcG.setName("PcG");
        PcG.setLevel(1);

        Node H3K4 = graph.addNode();
        H3K4.setName("H3K4");
        H3K4.setLevel(2);

        Node TrxG = graph.addNode();
        TrxG.setName("TrxG");
        TrxG.setLevel(3);

        Node DNMTs = graph.addNode();
        DNMTs.setName("DNMT's");
        DNMTs.setLevel(3);
        DNMTs.setValue(5);

        Node Pol2 = graph.addNode();
        Pol2.setName("Pol II");
        Pol2.setLevel(4);

        Node Gene = graph.addNode();
        Gene.setName("Gene");
        Gene.setLevel(5);

        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        graph.addEdge(cellDivision, CpG).setEdgeType(demethylation);
        graph.addEdge(K4DMs, H3K4).setEdgeType(demethylation);
        graph.addEdge(K9DMs, H3K9).setEdgeType(demethylation);
        graph.addEdge(K9MTs, H3K9).setEdgeType(methylation);
        graph.addEdge(K27DMs, H3K27).setEdgeType(demethylation);
        graph.addEdge(K27MTs, H3K27).setEdgeType(methylation);
        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        graph.addEdge(CpG, K4MTs).setEdgeType(inhibition);
        graph.addEdge(CpG, K9MTs).setEdgeType(activation);
        Edge edge = graph.addEdge(DNMTs, CpG);
        edge.setEdgeType(methylation);
        edge.setIsBackward(true);
//        graph.addEdge(H3K9, H3K4).setEdgeType(demethylation);
        graph.addEdge(H3K9, DNMTs).setEdgeType(activation);
        graph.addEdge(H3K27, PcG).setEdgeType(activation);
        graph.addEdge(H3K27, DNMTs).setEdgeType(inhibition);
        graph.addEdge(K4MTs, H3K4).setEdgeType(methylation);
        graph.addEdge(PcG, Pol2).setEdgeType(inhibition);
        graph.addEdge(H3K4, DNMTs).setEdgeType(inhibition);
        graph.addEdge(H3K4, TrxG).setEdgeType(activation);
        graph.addEdge(TrxG, Pol2).setEdgeType(activation);
        graph.addEdge(Pol2, Gene).setEdgeType(activation);


        return graph;
    }

    private Graph h3k27Subnet() {

        Graph graph = new Graph();

        Node K27DMs = graph.addNode();
        K27DMs.setName("K27DM's");
        K27DMs.setLevel(0);

        Node K27MTs = graph.addNode();
        K27MTs.setName("K27MT's");
        K27MTs.setLevel(0);

        Node H3K27 = graph.addNode();
        H3K27.setName("H3K27");
        H3K27.setLevel(1);

        Node PcG = graph.addNode();
        PcG.setName("PcG");
        PcG.setLevel(2);

        Node Pol2 = graph.addNode();
        Pol2.setName("Pol II");
        Pol2.setLevel(3);

        Node Gene = graph.addNode();
        Gene.setName("Gene");
        Gene.setLevel(4);

        graph.addEdge(K27DMs, H3K27).setEdgeType(demethylation);
        graph.addEdge(K27MTs, H3K27).setEdgeType(methylation);
        graph.addEdge(H3K27, PcG).setEdgeType(activation);
        graph.addEdge(PcG, Pol2).setEdgeType(inhibition);
        graph.addEdge(Pol2, Gene).setEdgeType(activation);

        return graph;
    }

    private Graph h3k4Subnet() {

        Graph graph = new Graph();

        Node K4DMs = graph.addNode();
        K4DMs.setName("K4DM's");
        K4DMs.setLevel(0);

        Node K4MTs = graph.addNode();
        K4MTs.setName("K4MT's");
        K4MTs.setLevel(0);

        Node H3K4 = graph.addNode();
        H3K4.setName("H3K4");
        H3K4.setLevel(1);

        Node TrxG = graph.addNode();
        TrxG.setName("TrxG");
        TrxG.setLevel(2);

        Node Pol2 = graph.addNode();
        Pol2.setName("Pol II");
        Pol2.setLevel(3);

        Node Gene = graph.addNode();
        Gene.setName("Gene");
        Gene.setLevel(4);

        graph.addEdge(K4DMs, H3K4).setEdgeType(demethylation);
        graph.addEdge(K4MTs, H3K4).setEdgeType(methylation);
        graph.addEdge(H3K4, TrxG).setEdgeType(activation);
        graph.addEdge(TrxG, Pol2).setEdgeType(activation);
        graph.addEdge(Pol2, Gene).setEdgeType(activation);

        return graph;
    }

    private Graph cpgSubnet() {

        Graph graph = new Graph();
        Node DNDMs = graph.addNode();
        DNDMs.setName("DNDM's");
        DNDMs.setLevel(0);
        DNDMs.setValue(1);

        Node cellDivision = graph.addNode();
        cellDivision.setName("Cell division");
        cellDivision.setLevel(0);
        DNDMs.setValue(1);

        Node CpG = graph.addNode();
        CpG.setName("CpG");
        CpG.setLevel(1);
        DNDMs.setValue(1);

        Node DNMT1 = graph.addNode();
        DNMT1.setName("DNMT1");
        DNMT1.setLevel(0);
        DNDMs.setValue(1);

        Node DNMT3 = graph.addNode();
        DNMT3.setName("DNMT's");
        DNMT3.setLevel(0);
        DNDMs.setValue(1);

        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        graph.addEdge(cellDivision, CpG).setEdgeType(demethylation);
        graph.addEdge(DNMT1, CpG).setEdgeType(methylation);
        graph.addEdge(DNMT3, CpG).setEdgeType(methylation);

        return graph;
    }

    private Graph cpgOperatorSubnet() {

        Graph graph = cpgSubnet();


        Node DNDMs = graph.addNode();
        DNDMs.setName("DNDM's");
        DNDMs.setLevel(0);
        DNDMs.setValue(1);

        Node cellDivision = graph.addNode();
        cellDivision.setName("Cell division");
        cellDivision.setLevel(0);
        DNDMs.setValue(1);

        Node CpG = graph.addNode();
        CpG.setName("CpG");
        CpG.setLevel(1);
        DNDMs.setValue(1);

        Node DNMT1 = graph.addNode();
        DNMT1.setName("DNMT1");
        DNMT1.setLevel(0);
        DNDMs.setValue(1);

        Node DNMT3 = graph.addNode();
        DNMT3.setName("DNMT's");
        DNMT3.setLevel(0);
        DNDMs.setValue(1);

        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        graph.addEdge(cellDivision, CpG).setEdgeType(demethylation);
        graph.addEdge(DNMT1, CpG).setEdgeType(methylation);
        graph.addEdge(DNMT3, CpG).setEdgeType(methylation);

        return graph;
    }

    private Graph h3k9Subnet() {
        Graph graph = new Graph();

        Node K9DMs = graph.addNode();
        K9DMs.setName("K9DM's");
        K9DMs.setLevel(0);

        Node K9MTs = graph.addNode();
        K9MTs.setName("K9MT's");
        K9MTs.setLevel(0);

        Node H3K9 = graph.addNode();
        H3K9.setName("H3K9");
        H3K9.setLevel(1);

        graph.addEdge(K9DMs, H3K9).setEdgeType(demethylation);
        graph.addEdge(K9MTs, H3K9).setEdgeType(methylation);

        return graph;
    }

    private Graph simpleNet() {
        Graph graph = new Graph();

        // ------------   h3k9  ------------ //
        Node K9DMs = graph.addNode();
        K9DMs.setName("K9DM's");
        K9DMs.setLevel(0);

        Node K9MTs = graph.addNode();
        K9MTs.setName("K9MT's");
        K9MTs.setLevel(0);

        Node H3K9 = graph.addNode();
        H3K9.setName("H3K9");
        H3K9.setLevel(1);

        graph.addEdge(K9DMs, H3K9).setEdgeType(demethylation);
        graph.addEdge(K9MTs, H3K9).setEdgeType(methylation);

        // ------------   h3k9  ------------ //

        // ------------   cpg  ------------ //
        Node DNDMs = graph.addNode();
        DNDMs.setName("DNDM's");
        DNDMs.setLevel(0);

        Node cellDivision = graph.addNode();
        cellDivision.setName("Cell division");
        cellDivision.setLevel(0);

        Node CpG = graph.addNode();
        CpG.setName("CpG");
        CpG.setLevel(3);

        Node DNMT1 = graph.addNode();
        DNMT1.setName("DNMT1");
        DNMT1.setLevel(2);

        Node DNMT3 = graph.addNode();
        DNMT3.setName("DNMT3");
        DNMT3.setLevel(2);

        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        graph.addEdge(cellDivision, CpG).setEdgeType(demethylation);
        graph.addEdge(DNMT1, CpG).setEdgeType(methylation);
        graph.addEdge(DNMT3, CpG).setEdgeType(methylation);
        // ------------   cpg  ------------ //

        // ------------   h3k9-cpg  ------------ //
        graph.addEdge(H3K9, DNMT1).setEdgeType(activation);
        graph.addEdge(H3K9, DNMT3).setEdgeType(activation);
        // ------------   h3k9-cpg  ------------ //

        // ------------   h3k4  ------------ //
        Node K4DMs = graph.addNode();
        K4DMs.setName("K4DM's");
        K4DMs.setLevel(0);

        Node K4MTs = graph.addNode();
        K4MTs.setName("K4MT's");
        K4MTs.setLevel(0);

        Node H3K4 = graph.addNode();
        H3K4.setName("H3K4");
        H3K4.setLevel(1);

        Node TrxG = graph.addNode();
        TrxG.setName("TrxG");
        TrxG.setLevel(2);

        Node Pol2 = graph.addNode();
        Pol2.setName("Pol II");
        Pol2.setLevel(3);

        Node Gene = graph.addNode();
        Gene.setName("Gene");
        Gene.setLevel(4);

        graph.addEdge(K4DMs, H3K4).setEdgeType(demethylation);
        graph.addEdge(K4MTs, H3K4).setEdgeType(methylation);
        graph.addEdge(H3K4, TrxG).setEdgeType(activation);
        graph.addEdge(TrxG, Pol2).setEdgeType(activation);
        graph.addEdge(Pol2, Gene).setEdgeType(activation);
        // ------------   h3k4  ------------ //

        // ------------   h3k4-cpg  ------------ //
        graph.addEdge(H3K4, DNMT3).setEdgeType(inhibition);
        // ------------   h3k4-cpg  ------------ //

        // ------------   h3k37  ------------ //
        Node K27DMs = graph.addNode();
        K27DMs.setName("K27DM's");
        K27DMs.setLevel(0);

        Node K27MTs = graph.addNode();
        K27MTs.setName("K27MT's");
        K27MTs.setLevel(0);

        Node H3K27 = graph.addNode();
        H3K27.setName("H3K27");
        H3K27.setLevel(1);

        Node PcG = graph.addNode();
        PcG.setName("PcG");
        PcG.setLevel(2);

        /*
        Node Pol2 = graph.addNode();
        Pol2.setName("Pol II");
        Pol2.setLevel(3);


        Node Gene = graph.addNode();
        Gene.setName("Gene");
        Gene.setLevel(4);
        */

        graph.addEdge(K27DMs, H3K27).setEdgeType(demethylation);
        graph.addEdge(K27MTs, H3K27).setEdgeType(methylation);
        graph.addEdge(H3K27, PcG).setEdgeType(activation);
        graph.addEdge(PcG, Pol2).setEdgeType(inhibition);
//        graph.addEdge(Pol2, Gene).setEdgeType(activation);

        // ------------   h3k37  ------------ //

        // ------------ loops   ------------ //
        graph.addEdge(CpG, Gene).setEdgeType(inhibition);
        graph.addEdge(CpG, H3K4).setEdgeType(demethylation);
        // ------------ loops   ------------ //

        return graph;
    }

    private PSF createPSF(Graph graph, boolean split) {
        HashMap<String, String> edgeTypeRuleMap = new HashMap<>();
        edgeTypeRuleMap.put(activation, "source*target");
        edgeTypeRuleMap.put(inhibition, "(1/source)*target");
        edgeTypeRuleMap.put(methylation, "source*target");
        edgeTypeRuleMap.put(demethylation, "(1/source)*target");

        PSF psf = new PSF(graph, edgeTypeRuleMap, Logger.getLogger(""));
        Properties multiSignalProps = new Properties();
        if (split) {
            multiSignalProps.put(EMultiSignalProps.SplitSignalRule.getName(), EMultiSignalProps.SPLIT_PROPORTIONAL);
            multiSignalProps.put(EMultiSignalProps.SplitSignalOn.getName(), EMultiSignalProps.SPLIT_INCOMING);
            multiSignalProps.put(EMultiSignalProps.MultipleSignalProcessingRule.getName(), EMultiSignalProps.ADDITION);
            multiSignalProps.put(EMultiSignalProps.SignalProcessingOrder.getName(), EMultiSignalProps.ORDER_NONE);
        } else {
            multiSignalProps.put(EMultiSignalProps.SplitSignalRule.getName(), EMultiSignalProps.SPLIT_NONE);
            multiSignalProps.put(EMultiSignalProps.MultipleSignalProcessingRule.getName(), EMultiSignalProps.UPDATE_NODE_SCORES);
            multiSignalProps.put(EMultiSignalProps.SignalProcessingOrder.getName(), EMultiSignalProps.ORDER_NONE);
        }
        Properties loopHandlingProps = new Properties();
        loopHandlingProps.put(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.ITERATE_UNTIL_CONVERGENCE);
        loopHandlingProps.put(ELoopHandlingProps.ConvergenceThreshold.getName(), 0.1 + "");
        loopHandlingProps.put(ELoopHandlingProps.MaxNumOfIterations.getName(), 10 + "");

        psf.setMultiSignalProps(multiSignalProps);
        psf.setLoopHandlingProps(loopHandlingProps);
        return psf;
    }

    private void simulate(Double[] inputs, Graph graph, File outfile, boolean split, boolean targetNodesOnly) {
        double[] out = new double[inputs.length];

        try {
            PrintWriter writer = new PrintWriter(outfile);
            for (Node sourceNode : graph.getNodes()) {
                Collection<Node> targetNodes = targetNodesOnly ? graph.getTargetNodes() : graph.getNodes();
                for (Node targetNode : targetNodes) {
                    if (!sourceNode.equals(targetNode)) {
                        int index = 0;
                        for (double input : inputs) {
                            sourceNode.setValue(input);
                            int targetLevel = targetNode.getLevel();
                            PSF psf = createPSF(graph, split);
                            psf.setSilentMode(true);

                            try {
                                psf.calculateFlow();
                                out[index++] = psf.getLevelNodeSignalMap().get(targetLevel).get(targetNode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for (Node node : graph.getNodes()) {
                                node.setValue(1);
                                node.removeNodeSignals();
                            }
                        }


                        String printLines = sourceNode.getName() + ":" + targetNode.getName() + "\n";
                        for (double input : inputs) {
                            printLines += String.format("%.3f\t", input);
                        }
                        printLines += "\n";

                        for (double output : out) {
                            printLines += String.format("%.2f\t", output);
                        }
                        printLines += "\n";
                        writer.write(printLines);

                    }
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void runDependencyTests() {
        double min = 0.01;
        double x = min;
        double max = 100;
        Double[] in = new Double[1000];
        ArrayList<Double> inputs = new ArrayList<>();

        while (x <= max) {
            inputs.add(x);
            if (x < 0.1)
                x += 0.001;
            else if (x < 1)
                x += 0.01;
            else if (x < 10)
                x += 0.1;
            else
                x += 1;

        }
        in = new Double[inputs.size()];
        in = inputs.toArray(in);

        final Double[] inFinal = in;
//        for (int i = 0; i < in.length; i++) {
//            in[i] = x;
//            x *= Math.log10((max-min)/in.length);
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                EpiNetSimulator epiNetSimulator = new EpiNetSimulator();
                Graph graph = epiNetSimulator.simpleNet();
                File outfile = new File("d:\\Dropbox\\Bioinformatics_Group\\Leipzig\\EpiNetwork\\simulations\\simpleNet+cpg-gene+cpg-h3k4.loop.sim.split.xls");
                epiNetSimulator.simulate(inFinal, graph, outfile, true, false);
            }
        }).run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EpiNetSimulator epiNetSimulator = new EpiNetSimulator();
                Graph graph = epiNetSimulator.simpleNet();
                File outfile = new File("d:\\Dropbox\\Bioinformatics_Group\\Leipzig\\EpiNetwork\\simulations\\simpleNet+cpg-gene+cpg-h3k4.loop.sim.nosplit.xls");
                epiNetSimulator.simulate(inFinal, graph, outfile, false, false);
            }
        }).run();


    }

    public static void main(String[] args) {
        EpiNetSimulator epiNetSimulator = new EpiNetSimulator();
        Graph graph = epiNetSimulator.cpgSubnet();
        graph.visualize();
    }
}
