package org.cytoscape.psfc.logic.algorithms;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.logic.structures.Graph;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;

/**
 * Created by User on 5/24/2014.
 */
public class PSFAlgorithms {
    public static void main(String[] args) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String foo = "40+2";
        try {
            System.out.println(engine.eval(foo));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<CyNode, Double> calculateFlow(Graph graph,
                                                        HashMap<CyNode, Double> cyNodeDataMap,
                                                        HashMap<CyNode, Integer> cyNodeLevelMap,
                                                        HashMap<CyEdge, String> cyEdgeTypeMap,
                                                        File edgeTypeRuleNameConfigFile,
                                                        File ruleConfigFile) {

        HashMap<CyNode, Double> cyNodeFlowScoreMap = new HashMap<CyNode, Double>();

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");


        for (CyNode cyNode : cyNodeDataMap.keySet()){
            double data = cyNodeDataMap.get(cyNode);
            cyNodeFlowScoreMap.put(cyNode, 2*data);
        }


        return cyNodeFlowScoreMap;
    }
}
