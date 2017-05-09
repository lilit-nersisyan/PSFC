package org.cytoscape.psfc.commands;

import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.psfc.gui.enums.ExceptionMessages;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lilit Nersisyan on 3/24/2017.
 */
public class CommandTaskFactory implements TaskFactory{
    public static final String RUNDEFAULTPSF = "run psf";
    private String command;
    Map<String, Object> map;
    RunDefaultPSFTask runDefaultPSFTask;

/*
    private CyColumn edgeTypeColumnName;
    private ArrayList<CyColumn> nodeDataColumn;
    private ActionEvent e;
*/

    public CommandTaskFactory(String command) {
        System.out.println(command);
        this.command = command;
        if(command.equals(RUNDEFAULTPSF)){
            runDefaultPSFTask = new RunDefaultPSFTask(new ActionEvent(this, 0, RUNDEFAULTPSF));
        }
    }

    @Override
    public TaskIterator createTaskIterator() {
        TaskIterator taskIterator = new TaskIterator();
        taskIterator.append(runDefaultPSFTask);
        return taskIterator;
    }

    @Override
    public boolean isReady() {
        return false;
    }
}
