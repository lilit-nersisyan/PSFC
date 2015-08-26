package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.psfc.properties.ELoopHandlingProps;
import org.cytoscape.psfc.properties.EMultiSignalProps;

import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by User on 6/29/2015.
 */
public class LoopSimulator {
    String activation = "activation";
    String inhibition = "inhibition";
    String positiveFeedback = "positiveFeedback";
    private double decay = 0;

    private Graph homeostat(){
        Graph graph = new Graph();
        graph.addNode().setLevel(0);
        graph.addNode().setLevel(1);
        graph.addNode().setLevel(2);
        graph.addNode().setLevel(3);
//        graph.getNode(2).setValue(0.5);
        graph.addEdge(graph.getNode(0), graph.getNode(1)).setEdgeType(activation);
        graph.addEdge(graph.getNode(1), graph.getNode(2)).setEdgeType(activation);
        graph.addEdge(graph.getNode(2), graph.getNode(3)).setEdgeType(activation);
        Edge backwardEdge = graph.addEdge(graph.getNode(2), graph.getNode(1));
        backwardEdge.setEdgeType(inhibition);
        backwardEdge.setIsBackward(true);

        return graph;
    }

    private Graph positiveFeedback(){
        Graph graph = new Graph();
        graph.addNode().setLevel(0);
        graph.addNode().setLevel(1);
        graph.addNode().setLevel(2);
        graph.addNode().setLevel(3);
//        graph.getNode(1).setValue(2);
        graph.getNode(1).setValue(1.1);
//        graph.getNode(2).setValue(2);
//        graph.getNode(3).setValue(2);
        graph.addEdge(graph.getNode(0), graph.getNode(1)).setEdgeType(activation);
        graph.addEdge(graph.getNode(1), graph.getNode(2)).setEdgeType(activation);
        graph.addEdge(graph.getNode(2), graph.getNode(3)).setEdgeType(activation);
        Edge backwardEdge = graph.addEdge(graph.getNode(2), graph.getNode(1));
        backwardEdge.setEdgeType(positiveFeedback);
        backwardEdge.setIsBackward(true);

        return graph;
    }

    private Graph positiveFeedback2(){
        Graph graph = new Graph();
        graph.addNode().setLevel(0);
        graph.addNode().setLevel(1);
        graph.addNode().setLevel(2);
        graph.addNode().setLevel(3);
        graph.addNode().setLevel(3);
//        graph.getNode(1).setValue(2);
        graph.getNode(1).setValue(2);
//        graph.getNode(2).setValue(2);
//        graph.getNode(3).setValue(2);
        graph.addEdge(graph.getNode(0), graph.getNode(1)).setEdgeType(activation);
        graph.addEdge(graph.getNode(1), graph.getNode(2)).setEdgeType(activation);
        graph.addEdge(graph.getNode(2), graph.getNode(3)).setEdgeType(activation);
        Edge backwardEdge = graph.addEdge(graph.getNode(3), graph.getNode(1));
        backwardEdge.setEdgeType(positiveFeedback);
        backwardEdge.setIsBackward(true);

        return graph;
    }

    private Graph adaptiveSystem(){
        Graph graph = new Graph();
        graph.addNode().setLevel(0);
        graph.addNode().setLevel(1);
        graph.addNode().setLevel(2);
        graph.addNode().setLevel(3);
        graph.addNode().setLevel(3);

        graph.addEdge(graph.getNode(0), graph.getNode(1)).setEdgeType(activation);
        graph.addEdge(graph.getNode(1), graph.getNode(2)).setEdgeType(activation);
        graph.addEdge(graph.getNode(2), graph.getNode(3)).setEdgeType(activation);
        graph.addEdge(graph.getNode(2), graph.getNode(4)).setEdgeType(activation);
        Edge backwardEdge = graph.addEdge(graph.getNode(4), graph.getNode(1));
        backwardEdge.setEdgeType(inhibition);
        backwardEdge.setIsBackward(true);

        return graph;
    }

    private PSF createPSF(Graph graph){
        HashMap<String, String> edgeTypeRuleMap = new HashMap<>();
        edgeTypeRuleMap.put(activation, "(source*target)");
        edgeTypeRuleMap.put(inhibition, "((1/source))*target");
        edgeTypeRuleMap.put(positiveFeedback, "(source^2/(2*source))*target");
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
        LoopSimulator loopSimulator = new LoopSimulator();


//        double[] in = new double[]{1, 0.8, 1.2, 0.8, 1.2, 0.8, 1.2, 0.8, 1.2, 0.8, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5};
//        double[] in = new double[]{2};
        double[] in = new double[]{1,1.5, 2, 2.5, 3, 3.5};
        double[] out = new double[in.length];
        double[] outT = new double[in.length];
        HashMap<Integer, TreeMap<Integer, Double>> outSignals = new HashMap<>();
        int index = 0;
        for (double input : in){
            Graph graph = loopSimulator.adaptiveSystem();
            graph.getNode(0).setValue(input);
            PSF psf = loopSimulator.createPSF(graph);
            psf.setSilentMode(true);

            try {
                psf.calculateFlow();
//                System.out.println(input);
                outSignals.put(index, graph.getNode(3).getSignals());
                out[index++] = psf.getLevelNodeSignalMap().get(3).get(graph.getNode(3));
                outT[index-1] = psf.getLevelNodeSignalMap().get(2).get(graph.getNode(2));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(double input : in){
            System.out.print(input + "\t");
        }
        System.out.println("");
        for(double output : out){
            System.out.print(output + "\t");
        }

        System.out.println("\noutT");
        for(double output : outT){
            System.out.print(output + "\t");
        }
        double[][] inoutArray = new double[outSignals.size()*50][2];
        System.out.println("");
        int j = 0;
        for(int i =0 ; i < outSignals.size(); i++){

            for(double outsignal : outSignals.get(i).values()){
                inoutArray[j][1] = outsignal;
                inoutArray[j][0] = in[i];
                j++;
            }
        }
        for(int i = 0; i < inoutArray.length; i++){
            System.out.print(inoutArray[i][0] + "\t");
        }
        System.out.println("");
        for(int i = 0; i < inoutArray.length; i++){
            System.out.print(inoutArray[i][1] + "\t");
        }
        System.out.println("");
     }
}
