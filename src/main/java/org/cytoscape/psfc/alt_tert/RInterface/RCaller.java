package org.cytoscape.psfc.alt_tert.RInterface;

import java.io.*;

/**
 * Created by Lilit Nersisyan on 12/6/2016.
 */
public class RCaller {
    private File scriptFile = null;
    private String args = "";

    public RCaller(File scriptFile) throws FileNotFoundException{
        if(scriptFile.exists())
            this.scriptFile = scriptFile;
        else
            new FileNotFoundException("Could not find file " + scriptFile.getAbsolutePath());
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void execute() throws IOException, InterruptedException {
        System.out.println("Running the R script " + scriptFile.getAbsolutePath() + " " + args);
        Runtime.getRuntime().exec("Rscript " + scriptFile.getAbsolutePath() + " " + args);
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("Rscript " + scriptFile.getAbsolutePath());
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        System.out.println("<OUTPUT>");
        while ( (line = br.readLine()) != null)
            System.out.println(line);
        System.out.println("</OUTPUT>");
        int exitVal = proc.waitFor();
        System.out.println("Process exitValue: " + exitVal);
    }


    public static void main(String[] args) {
        File scriptFile = new File("reports.R");
        try {
            RCaller rCaller = new RCaller(scriptFile);
            rCaller.execute();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


}
