package org.cytoscape.psfc.alt_tert.networks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.alt_tert.RInterface.RCaller;
import org.cytoscape.psfc.logic.structures.Edge;
import org.cytoscape.psfc.logic.structures.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Lilit Nersisyan on 12/7/2016.
 */
public class NetIteration {
    public static File parentDir;
    private static File netDir;
    public static String iteration;
    public static File entrezMatFile;
    private static File fcMat;
    private static File processDataR = new File("C:\\Dropbox\\idea_workspace\\psf\\PSFC\\src\\main\\java\\org\\cytoscape\\psfc\\alt_tert\\RInterface\\process.data.R");
    private static File reportsR = new File("C:\\Dropbox\\idea_workspace\\psf\\PSFC\\src\\main\\java\\org\\cytoscape\\psfc\\alt_tert\\RInterface\\reports.R");
    private static Set<String> colnames;

    public static Set<String> getColnames() {
        return colnames;
    }

    public static void setColnames(Set<String> colnames) {
        NetIteration.colnames = colnames;
    }

    public static File getPdfFile() {
        return pdfFile;
    }

    public static void setPdfFile(File pdfFile) {
        NetIteration.pdfFile = pdfFile;
    }

    private static File pdfFile;

    public static File getParentDir() {
        return parentDir;
    }

    public static File getNetDir() {
        return netDir;
    }

    public static void setNetDir(File netDir) {
        NetIteration.netDir = netDir;
    }

    public static void setParentDir(File parentDir) throws Exception {
        if (!parentDir.exists())
            throw new FileNotFoundException("Directory " + parentDir.getAbsolutePath() + " not found.");
        else if (!parentDir.isDirectory())
            throw new IllegalArgumentException("The provided path " + parentDir.getAbsolutePath() + " is not a directory");
        else
            NetIteration.parentDir = parentDir;
    }

    public static File getFcMat() {
        return fcMat;
    }

    public static void setFcMat(File fcMat) {
        NetIteration.fcMat = fcMat;
    }

    public static File getEntrezMatFile() {
        return entrezMatFile;
    }

    public static void setEntrezMatFile(File entrezMatFile) throws FileNotFoundException {
        if (!entrezMatFile.exists())
            throw new FileNotFoundException("Could not find entrez mat file: " + entrezMatFile.getAbsolutePath());
        NetIteration.entrezMatFile = entrezMatFile;
    }

    public static String getIteration() {
        return iteration;
    }

    public static void setIteration(String iteration) {
        NetIteration.iteration = iteration;
    }

    public static void processData() throws IOException, InterruptedException {
        RCaller rCaller = new RCaller(processDataR);
        rCaller.setArgs(parentDir.getAbsolutePath() + " " + iteration);
        rCaller.execute();
    }

    public static void generateReports() throws IOException, InterruptedException {
        pdfFile = new File(netDir, iteration + ".pdf");
        if(pdfFile.exists())
            Files.delete(pdfFile.toPath());
        RCaller rCaller = new RCaller(reportsR);


        rCaller.setArgs(parentDir.getAbsolutePath() +
                " " + iteration + " " + pdfFile.getAbsolutePath());
        rCaller.execute();
    }

    public static void CopyPSFC_summaryFileTask() throws IOException {
        File summaryFile = new File(PSFCActivator.getPSFCDir(), "alt-tert_summary.xls");
        File targetSummaryFile = new File(netDir, "alt-tert_summary.xls");
        if(targetSummaryFile.exists())
            Files.delete(targetSummaryFile.toPath());
        Files.copy(summaryFile.toPath(), targetSummaryFile.toPath());
    }

    public static HashMap<String, Graph> iterateOverEdges(Graph graph){
        HashMap<String,Graph> graphs = new HashMap<>();
        graphs.put("original", graph);
        for(Edge edge : graph.getEdges()){
            Graph or = graph;
            graph.removeEdge(edge);
            graphs.put(edge.getSource() + "-" + edge.getTarget(), graph);
            graph = or;
        }
        return graphs;
    }


    public static void deleteSummaryFile() throws IOException {
        File summaryFile = new File(PSFCActivator.getPSFCDir(), "alt-tert_summary.xls");
        Files.delete(summaryFile.toPath());
    }

    public static boolean summaryFileCreated() {
        File summaryFile = new File(PSFCActivator.getPSFCDir(), "alt-tert_summary.xls");
        return summaryFile.exists() && summaryFile.canRead();
    }

    public static boolean summaryFileCopied() {
        File summaryFile = new File(netDir, "alt-tert_summary.xls");
        return summaryFile.exists() && summaryFile.canRead();
    }
}
