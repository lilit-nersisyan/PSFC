package org.cytoscape.psfc.logic.algorithms;

import com.sun.java.util.jar.pack.*;
import org.apache.log4j.Logger;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.net.NetworkGraphMapper;
import org.cytoscape.psfc.properties.ELoopHandlingProps;
import org.cytoscape.psfc.properties.EMultiSignalProps;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by User on 8/6/2015.
 */
public class EpiNetSimulator {
    String activation = "activation";
    String inhibition = "inhibition";
    String methylation = "methylation";
    String demethylation = "demethylation";
    private BundleContext BC;



    private Graph epiNetGraph(){

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
    private Graph epiNetGraph2(){

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

    private Graph epiNetGraphSimple(){

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

        graph.addEdge(K9DMs, H3K9).setEdgeType(demethylation);
        graph.addEdge(K9MTs, H3K9).setEdgeType(methylation);
        graph.addEdge(H3K9, DNMTs).setEdgeType(activation);

        graph.addEdge(DNDMs, CpG).setEdgeType(demethylation);
        Edge edge = graph.addEdge(DNMTs, CpG);
        edge.setEdgeType(methylation); // check
        edge.setIsBackward(true); // check
        graph.addEdge(cellDivision, CpG).setEdgeType(demethylation);

        graph.addEdge(K4DMs, H3K4).setEdgeType(demethylation);
        graph.addEdge(K4MTs, H3K4).setEdgeType(methylation);


        graph.addEdge(K27DMs, H3K27).setEdgeType(demethylation);
        graph.addEdge(K27MTs, H3K27).setEdgeType(methylation);

        graph.addEdge(CpG, K4MTs).setEdgeType(inhibition);
        graph.addEdge(CpG, K9MTs).setEdgeType(activation);

//        graph.addEdge(H3K9, H3K4).setEdgeType(demethylation);

        graph.addEdge(H3K27, PcG).setEdgeType(activation);
        graph.addEdge(H3K27, DNMTs).setEdgeType(inhibition);

        graph.addEdge(PcG, Pol2).setEdgeType(inhibition);
        graph.addEdge(H3K4, DNMTs).setEdgeType(inhibition);
        graph.addEdge(H3K4, TrxG).setEdgeType(activation);
        graph.addEdge(TrxG, Pol2).setEdgeType(activation);
        graph.addEdge(Pol2, Gene).setEdgeType(activation);


        return graph;
    }

    private PSF createPSF(Graph graph){
        HashMap<String, String> edgeTypeRuleMap = new HashMap<>();
        edgeTypeRuleMap.put(activation, "(source*target)");
        edgeTypeRuleMap.put(inhibition, "(1/source)*target");
        edgeTypeRuleMap.put(methylation, "source*target");
        edgeTypeRuleMap.put(demethylation, "(1/source)*target");

        PSF psf = new PSF(graph, edgeTypeRuleMap, Logger.getLogger(""));
        Properties multiSignalProps = new Properties();
        multiSignalProps.put(EMultiSignalProps.SplitSignalRule.getName(), EMultiSignalProps.SPLIT_PROPORTIONAL);
        multiSignalProps.put(EMultiSignalProps.SplitSignalOn.getName(), EMultiSignalProps.SPLIT_INCOMING);
        multiSignalProps.put(EMultiSignalProps.MultipleSignalProcessingRule.getName(), EMultiSignalProps.ADDITION);
        multiSignalProps.put(EMultiSignalProps.SignalProcessingOrder.getName(), EMultiSignalProps.ORDER_NONE);

        Properties loopHandlingProps = new Properties();
        loopHandlingProps.put(ELoopHandlingProps.LoopHandling.getName(), ELoopHandlingProps.ITERATE_UNTIL_CONVERGENCE);
        loopHandlingProps.put(ELoopHandlingProps.ConvergenceThreshold.getName(), 1 + "");
        loopHandlingProps.put(ELoopHandlingProps.MaxNumOfIterations.getName(), 50 + "");

        psf.setMultiSignalProps(multiSignalProps);
        psf.setLoopHandlingProps(loopHandlingProps);
        return psf;
    }

    public static void main(String[] args) {
        EpiNetSimulator epiNetSimulator = new EpiNetSimulator();
        Graph graph = epiNetSimulator.epiNetGraph();
        double[] in = new double[1000];
        double x = 0.01;
        for (int i = 0; i < in.length; i++) {
            in[i] = x;
            x += 0.01;
        }
        double[] out = new double[in.length];
        File outfile = new File("d:\\Dropbox\\Bioinformatics_Group\\Leipzig\\EpiNetwork\\epinet.sim3.xls");
        try {
            PrintWriter writer = new PrintWriter(outfile);

            for (Node sourceNode : graph.getNodes()) {
                Node[] targetNodes = new Node[]{graph.getNode("CpG"), graph.getNode("Gene")};
                for (Node targetNode : targetNodes) {
                    int index = 0;
                    for (double input : in) {
                        sourceNode.setValue(input);
                        int targetLevel = targetNode.getLevel();
                        PSF psf = epiNetSimulator.createPSF(graph);
                        psf.setSilentMode(true);

                        try {
                            psf.calculateFlow();
                            out[index++] = psf.getLevelNodeSignalMap().get(targetLevel).get(targetNode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for(Node node : graph.getNodes()){
                            node.setValue(1);
                            node.removeNodeSignals();
                        }
                    }


                    String printLines = sourceNode.getName() + ":" + targetNode.getName() + "\n";
                    for (double input : in) {
                        printLines += String.format("%.1f\t", input);
                    }
                    printLines += "\n";

                    for (double output : out) {
                        printLines += String.format("%.2f\t", output);
                    }
                    printLines += "\n";
                    writer.write(printLines);

                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
