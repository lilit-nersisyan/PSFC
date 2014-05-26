package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;

import java.awt.event.ActionEvent;

/**
 * Created by User on 5/25/2014.
 */
public class MapNetworkToGraphAction extends AbstractCyAction {
    public MapNetworkToGraphAction(){
        super("Export network in psf format");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
