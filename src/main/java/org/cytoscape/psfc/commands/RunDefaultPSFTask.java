package org.cytoscape.psfc.commands;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.actions.CalculateScoreFlowMultiColAction;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.work.*;
import org.cytoscape.work.util.ListSingleSelection;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by Lilit Nersisyan on 3/24/2017.
 */
public class RunDefaultPSFTask extends AbstractTask implements ObservableTask {
    private final ActionEvent e;
    private File lockFile;
    PSFCPanel psfcPanel = PSFCActivator.psfcPanel;

    @Tunable(description = "Edge type column name")
    public ListSingleSelection<String> edgeTypeColumnName =
            new ListSingleSelection("edgeType", "typeDefault");
    @Tunable(description = "Comma separated node data column names")
    public ListSingleSelection<String> nodeDataColumnNames =
            new ListSingleSelection<>("nodeDataColumns", "nodeDataDefault1, nodeDataDefault2");

    @Tunable(description = "Number of bootstrap cycles")
    public ListSingleSelection<String> bootCyclesArg =
            new ListSingleSelection<>("bootCycles", "" + 200);

    @Tunable(description = "FC file")
    public File fcFile = new File("FCFile", "");

    @Tunable(description = "Backup directory")
    public File backupDir = new File("backupDir", "");


    @Tunable(description = "psfOption")
    public ListSingleSelection<String> psfOption =
            new ListSingleSelection("psfOption", "optionDefault");

    @Tunable(description = "edgeRulesOption")
    public ListSingleSelection<String> edgeRulesOption =
            new ListSingleSelection("edgeRulesOption", "optionDefault");


    /*
    @Tunable(description = "rankColumn")
    public ListSingleSelection<String> rankColumn =
            new ListSingleSelection("rankColumn", "rankDefault");
*/


    public RunDefaultPSFTask(ActionEvent e) {
        this.e = e;
    }


    //
//    @Tunable(description = "Names of NodeDataColumns", context = "both")
//    ArrayList<String> nodeDataColumns;

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        System.out.println("RunDefaultPSFTask started");
        taskMonitor.setTitle("Default PSF Task");

        lockFile = new File(PSFCActivator.getPSFCDir(), "psfLock");
        while(lockFile.exists()){
            boolean locksuccess = lockFile.delete();
            if(!locksuccess)
                lockFile = new File(lockFile.getAbsolutePath() + "+");
        }
        lockFile.createNewFile();


        CyNetwork currentNetwork = PSFCActivator.cyApplicationManager.getCurrentNetwork();
        if (currentNetwork == null) {
            throw new Exception("PSFC:: no network loaded!");
        } else {
            System.out.println("Network name: " + currentNetwork.toString());
        }
        taskMonitor.setStatusMessage("Network " + currentNetwork.toString() + " identified");
        CyColumn edgeTypeColumn = null;
        if (edgeTypeColumnName == null) {
            System.out.println("Edgetype is null");
            throw new NullPointerException("PSFC:: Edge type Column should be selected");
        } else {
            System.out.println("selected edge type column name" + edgeTypeColumnName.getSelectedValue());
            edgeTypeColumn = NetworkCyManager.getEdgeColumnFromName(
                    edgeTypeColumnName.getSelectedValue(), currentNetwork);
            if (edgeTypeColumn == null)
                throw new Exception("PSFC:: No column with name " + edgeTypeColumnName +
                        " exists for network " + currentNetwork);
        }
        taskMonitor.setStatusMessage("Edge type column found: " + edgeTypeColumn.getName());

        ArrayList<CyColumn> nodeDataColumns = new ArrayList<>();

        if (nodeDataColumnNames == null) {
            System.out.println("node data is null");
            throw new NullPointerException("PSFC:: Node data columns should be provided ");
        } else {
            String[] nodeDataColumnNamesArray = nodeDataColumnNames.getSelectedValue().split(",");
            System.out.println(nodeDataColumnNamesArray.toString());

            for (String nodeDataCName : nodeDataColumnNamesArray) {
                CyColumn nodeDataColumn = NetworkCyManager.getNodeColumnFromName(nodeDataCName, currentNetwork);
                if (nodeDataColumn == null) {
                    throw new Exception("PSFC:: No column with name " + nodeDataCName +
                            " exists for network " + currentNetwork);
                }
                nodeDataColumns.add(nodeDataColumn);
            }
        }

        if(bootCyclesArg == null)
            throw new Exception("No boot cycles arg specified");
        int bootCycles;
        try {
            bootCycles = Integer.parseInt(bootCyclesArg.getSelectedValue());
        } catch (NumberFormatException e1) {
            throw new Exception("Nonnumeric argument to boot cycles: "
                    + (e1.getCause() != null ? e1.getCause().getMessage() : e1.getMessage()));
        }

        if(fcFile == null)
            throw new Exception("FC file was null");

        taskMonitor.setStatusMessage(nodeDataColumns.size() + " nodeDataColumns selected");


        try {
            CalculateScoreFlowMultiColAction calculateScoreFlowMultiColAction =
                    psfcPanel.createCalculateScoreFlowMultiColAction(
                            currentNetwork, edgeTypeColumn, nodeDataColumns,
                            getEdgeTypeRuleNameConfigFile(edgeRulesOption.getSelectedValue()),
                            getRuleNameRuleConfigFile(),
                            fcFile, bootCycles,
                            psfOption.getSelectedValue(), e);
            if(backupDir != null)
                calculateScoreFlowMultiColAction.setBackupDir(backupDir);
            calculateScoreFlowMultiColAction.setDetectNodeInfluence(true);
            calculateScoreFlowMultiColAction.actionPerformed(e);
        } catch (Exception e) {
            String message;
            if (e.getCause() != null)
                message = e.getCause().getMessage();
            else
                message = e.getMessage();
            throw new Exception("Exception initiating or running the task, reason: " + message);

        } finally {
            boolean unlock = lockFile.delete();
            if(unlock)
                System.out.println("PSF unlocked");
            else
                System.out.println("Could not delete PSF lock file");

            System.gc();
        }

    }

    @Override
    public <R> R getResults(Class<? extends R> aClass) {
        return null;
    }

    private String ADDSUB = "ADDSUB";
    private String MULDIV = "MULDIV";

    private File getEdgeTypeRuleNameConfigFile(String edgeRulesOption) throws Exception {
        File psfcDir = PSFCActivator.getPSFCDir();
        File file = new File(psfcDir, "defaultEdgeTypeRuleName.config");
        if(edgeRulesOption.equals(ADDSUB))
            file = new File(psfcDir, "ADDSUB_EdgeTypeRuleName.config");
        else if (edgeRulesOption.equals(MULDIV))
            file = new File(psfcDir, "MULDIV_EdgeTypeRuleName.config");

        // why am I creating a new default file?
/*        if (file.exists()) {
            boolean success = file.delete();
            if (!success)
                throw new Exception("PSFC:: could not remove file " + file.getAbsolutePath());
        }
*/
        if(! file.getAbsoluteFile().exists()) { // only do this if file does not exist
            file = new File(psfcDir, "defaultEdgeTypeRuleName.config");
            boolean success = file.createNewFile();
            if (!success) {
                System.out.println("WARNING: file either not recognized or not created: " + file.getAbsolutePath());
                //throw new Exception("Could not create file " + file.getAbsolutePath());
            }
            PrintWriter writer = new PrintWriter(file);
            writer.append("activation\t*\n");
            writer.append("inhibition\t/\n");
            writer.close();
        }

        return file;
    }





    private File getRuleNameRuleConfigFile() throws Exception {
        File psfcDir = PSFCActivator.getPSFCDir();
        File file = new File(psfcDir, "defaultRuleNameRule.config");

/*        if (file.exists()) {
            boolean success = file.delete();
            if (!success)
                throw new Exception("PSFC:: could not remove file " + file.getAbsolutePath());
        }
*/
        if(! file.getAbsoluteFile().exists()) {
            boolean success = file.createNewFile();
            if (!success) {
                 PSFCActivator.getLogger().warn("WARNING: file either not recognized or not created: " + file.getAbsolutePath());
                //throw new Exception("Could not create file " + file.getAbsolutePath());
            }

            PrintWriter writer = new PrintWriter(file);
            writer.append("*\tsource * target\n");
            writer.append("/\t1/source * target\n");
            writer.append("+\tsource + target\n");
            writer.append("-\ttarget - source\n");
            writer.close();
        }
        return file;
    }
}
