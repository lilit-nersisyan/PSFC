package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Node;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class is an extension of Bootstrap for significance calculation
 * for sample-centric re-sampling case
 */
public class BootstrapSampleCentric extends Bootstrap {
    public BootstrapSampleCentric(PSF psf, int numOfSamplings,
                                  Logger logger) {
        super(psf, numOfSamplings, logger);
    }

    @Override
    public void resample() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.addAll(graph.getNodes());
        int[][] one2oneCor = getReshuffledOne2OneCorrespondence(graph.getOrder());
        for (int i = 0; i < graph.getOrder(); i++) {
            nodes.get(i).setValue(originalNodeValues.get(nodes.get(one2oneCor[i][1])));
        }

    }

    /**
     * Uses random function to reshuffle indices from the range [1:order].
     *
     * @param order the number of nodes in the graph
     * @return a one-to-one correspondence array of source indices and their reshuffled pairs.
     */
    public static int[][] getReshuffledOne2OneCorrespondence(int order) {
        int[][] one2oneCor = new int[order][2];
        LinkedList<Integer> sourceQueue = new LinkedList<Integer>();
        for (int i = 0; i < order; i++)
            sourceQueue.add(i);
        for (int i = 0; i < order; i++) {
            int j = (int) Math.floor(Math.random() * (sourceQueue.size()));
            one2oneCor[i][0] = i;
            one2oneCor[i][1] = sourceQueue.get(j);
            sourceQueue.remove(sourceQueue.get(j));
        }
        return one2oneCor;
    }

}
