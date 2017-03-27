package org.cytoscape.psfc.gui.actions;

import org.cytoscape.work.ObservableTask;

/**
 * Created by Lilit Nersisyan on 3/27/2017.
 */
public interface MyObservableTask extends ObservableTask {
    public void executeSecondPart() throws Exception;
}
