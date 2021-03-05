package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Graph;
import org.cytoscape.psfc.logic.structures.GraphInfluence;
import org.cytoscape.psfc.logic.structures.Node;
import org.cytoscape.work.TaskMonitor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * PUBLIC CLASS NodeInfluenceDetector
 *
 * This class stores PSF fluctuations in the graph for each nodeDataColumn during the PSF computations
 *
 */
public class NodeInfluenceDetector extends Bootstrap {


    public NodeInfluenceDetector(PSF psf, Logger logger) {
        super(psf, logger);
    }

}
