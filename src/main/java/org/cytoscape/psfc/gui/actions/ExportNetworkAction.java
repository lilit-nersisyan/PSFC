package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by User on 6/10/2015.
 */
public class ExportNetworkAction extends AbstractCyAction {


    private final String fileName;
    private final CyNetwork network;
    public ExportNetworkAction(CyNetwork network, String fileName) {
        super("PSFC: Exporting network to file " + fileName );
        this.fileName = fileName;
        this.network = network;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TaskIterator taskIterator = PSFCActivator.exportNetworkViewTaskFactory.createTaskIterator(
                NetworkCyManager.getNetworkView(network),
                new File(fileName));

        PSFCActivator.taskManager.execute(taskIterator);
    }

}
