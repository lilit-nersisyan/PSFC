package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;

import java.awt.event.ActionEvent;

/**
 * Created by User on 5/25/2014.
 */
public class PSFCJustAction extends AbstractCyAction {
    public PSFCJustAction(){
        super("Just action");
        setMenuGravity(0);
        setPreferredMenu("Apps.PSFC");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}