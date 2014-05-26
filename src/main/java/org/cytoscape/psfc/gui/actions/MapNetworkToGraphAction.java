package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.actions.net.NetworkGraphMapper;
import org.cytoscape.psfc.logic.structures.Graph;

import java.awt.event.ActionEvent;

/**
 * Created by User on 5/25/2014.
 */
public class MapNetworkToGraphAction extends AbstractCyAction {
    public MapNetworkToGraphAction(){
        super("Map network to PSF Graph");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Graph graph = NetworkGraphMapper.graphFromNetwork(PSFCActivator.cyApplicationManager.getCurrentNetwork());
        System.out.println(graph.toString());
    }
}
