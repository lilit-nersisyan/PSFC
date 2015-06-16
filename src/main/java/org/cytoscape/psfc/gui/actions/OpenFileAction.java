package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.psfc.PSFCActivator;
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
public class OpenFileAction extends AbstractCyAction {


    private final String fileName;

    public OpenFileAction(String fileName) {
        super("PSFC: Open " + fileName + " action");
        this.fileName = fileName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileActionTask task = new OpenFileActionTask();
        PSFCActivator.taskManager.execute(new TaskIterator(task));

    }

    private class OpenFileActionTask extends AbstractTask{
        File file;

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC: Opening " + fileName);

            taskMonitor.setProgress(0.1);

            try {
                file = new File(PSFCActivator.getPSFCDir(), fileName);
                if (!file.exists()) {
                    ClassLoader cl = this.getClass().getClassLoader();
                    java.io.InputStream in = cl.getResourceAsStream(fileName);
                    if (in == null) {
                        throw  new Exception("PSFC::Exception " + "Null InputStream: " + fileName + "could not be loaded from the plugin jar file.");
                    }

                    FileOutputStream out = new FileOutputStream(file);
                    byte[] bytes = new byte[1024];
                    int read;
                    while ((read = in.read(bytes)) != -1 && !cancelled) {
                        out.write(bytes, 0, read);
                    }
                    in.close();
                    out.close();
                }
                taskMonitor.setProgress(0.8);
                taskMonitor.setStatusMessage("Opening File " + file.getAbsolutePath());
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(file);
                else{
                    throw new Exception("PSFC::Exception " + "Desktop is not supported!");
                }
            } catch (Exception e) {
                throw new Exception("PSFC::Exception " + "Problems opening " + file.getAbsolutePath() +
                        "\n" + e.getMessage()+
                        "\n Try opening it manually.");
            } finally {
                taskMonitor.setProgress(1);
                System.gc();
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
            super.cancel();
        }
    }
}
