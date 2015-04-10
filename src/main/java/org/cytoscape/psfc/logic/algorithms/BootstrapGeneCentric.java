package org.cytoscape.psfc.logic.algorithms;

import org.apache.log4j.Logger;
import org.cytoscape.psfc.logic.structures.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is an extension of Bootstrap for significance calculation
 * for gene-centric re-sampling case
 */
public class BootstrapGeneCentric extends Bootstrap {
    private HashMap<Node, Double[]> expMat;

    public BootstrapGeneCentric(PSF psf, int numOfSamplings, File expMatFile,
                                Logger logger) throws Exception {
        super(psf, numOfSamplings, logger);
        expMat = parseExpMatFile(expMatFile);
        if(expMat.size() < graph.getOrder())
            logger.warn(String.format("Only %d nodes (of %d graph nodes) found in the expression matrix file", expMat.size(), graph.getOrder()));
    }

    private HashMap<Node, Double[]> parseExpMatFile(File expMatFile) throws Exception {
        if (!expMatFile.exists())
            throw new FileNotFoundException("File " + expMatFile + " doesn't exist");
        HashMap<Node, Double[]> matrix = new HashMap<Node, Double[]>();
        BufferedReader reader = new BufferedReader(new FileReader(expMatFile));
        String line;
        Node node;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\t");
            if (tokens.length < 2) {
                throw new Exception("Less than 2 columns found in expression matrix file " + expMatFile.getName());
            }
            String gene = tokens[0];
            if ((node = graph.getNode(gene)) != null) {
                Double[] exps = new Double[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    try {
                        exps[i-1] = Double.parseDouble(tokens[i]);
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("Non-numeric expression value \"" + tokens[i] + "\" in expression matrix file");
                    }
                }
                matrix.put(node, exps);
            }
        }
        return matrix;
    }

    @Override
    public void resample() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.addAll(graph.getNodes());
        Double[] exps;
        for (int i = 0; i < graph.getOrder(); i++) {
            Node node = nodes.get(i);
            if(expMat.containsKey(node))
                exps = expMat.get(node);
            else {
                exps = new Double[]{originalNodeValues.get(node)};
            }
            int j = (int) Math.floor(Math.random() * (exps.length));
            nodes.get(i).setValue(exps[j]);
        }
    }

}
