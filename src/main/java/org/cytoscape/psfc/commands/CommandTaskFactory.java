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
public class CommandTaskFactory implements CommandExecutorTaskFactory {
    public static final String COMMANDWITHARGS = "commandwithargs";

    private String command = "";
/*
    private CyColumn edgeTypeColumn;
    private ArrayList<CyColumn> nodeDataColumn;
    private ActionEvent e;
*/

    public CommandTaskFactory(String command) {
        this.command = command;
    }


    @Override
    public TaskIterator createTaskIterator(File file, TaskObserver taskObserver) {
        return null;
    }

    @Override
    public TaskIterator createTaskIterator(TaskObserver taskObserver, String... strings) {
        return null;
    }

    @Override
    public TaskIterator createTaskIterator(List<String> list, TaskObserver taskObserver) {
        return null;
    }

    @Override
    public TaskIterator createTaskIterator(String namespace, String command,
                                           Map<String, Object> map, TaskObserver taskObserver) {
        if(!namespace.equals("psfc"))
            return null;
        if(command.equals(COMMANDWITHARGS)) {
            return new TaskIterator(new RunDefaultPSFTask(map, taskObserver));
        }
        return null;
    }
}
