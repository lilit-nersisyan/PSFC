package org.cytoscape.psfc.gui.actions;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

/**
 * Created by Lilit Nersisyan on 3/7/2017.
 */
public class MyTaskObserver implements TaskObserver {
    boolean allComplete = false;
    @Override
    public void taskFinished(ObservableTask observableTask) {
        System.out.println("Task finished: " + observableTask.toString());
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        System.out.println("all complete");
        allComplete = true;
    }

    public boolean allComplete(){
        return allComplete;
    }

    public void reset() {
        allComplete = false;
    }
}
