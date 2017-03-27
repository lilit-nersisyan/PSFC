package org.cytoscape.psfc.commands;

import org.cytoscape.model.CyColumn;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.actions.CalculateScoreFlowMultiColAction;
import org.cytoscape.work.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Lilit Nersisyan on 3/24/2017.
 */
public class RunDefaultPSFTask extends AbstractTask implements ObservableTask {
    PSFCPanel psfcPanel = PSFCActivator.psfcPanel;
    Map<String, Object> map;
    TaskObserver taskObserver;

    public RunDefaultPSFTask(Map<String, Object> map, TaskObserver taskObserver) {
        this.map = map;
        this.taskObserver = taskObserver;
    }

//    @Tunable(description = "Tunable edge type column")
//    String edgeTypeColumn;

//    @Tunable(description = "Tunable nodeDataColumns", context = "nogui")
//    ArrayList<String> nodeDataColumns;
//
//    @Tunable(description = "Tunable actionevent", context = "nogui")
//    ActionEvent actionEvent;

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        System.out.println("running psfc task");
        taskMonitor.setTitle("psfc command");
        for (int i = 0; i < 1000; i++){
            taskMonitor.setProgress(i/10);
        }
        System.out.println(psfcPanel.getSelectedNetwork());
//        CalculateScoreFlowMultiColAction calculateScoreFlowMultiColAction =
//                psfcPanel.createCalculateScoreFlowMultiplCOlAction(edgeTypeColumn,
//                        nodeDataColumns, actionEvent);
//        calculateScoreFlowMultiColAction.actionPerformed(actionEvent);
        System.out.println(map.toString());
//        System.out.println(nodeDataColumns.toArray());

    }

    @Override
    public <R> R getResults(Class<? extends R> aClass) {
        return null;
    }
}
