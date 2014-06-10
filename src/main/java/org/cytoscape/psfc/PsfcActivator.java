package org.cytoscape.psfc;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.actions.SortCurrentNetworkAction;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class PSFCActivator extends AbstractCyActivator {
    public static CySwingApplication cytoscapeDesktopService;
    public static DialogTaskManager taskManager;
    public static CySessionManager cySessionManager;
    public static CyNetworkFactory networkFactory;
    public static CyNetworkViewFactory networkViewFactory;
    public static CyNetworkManager networkManager;
    public static CyNetworkViewManager networkViewManager;
    public static VisualMappingManager visualMappingManager;
    public static VisualMappingFunctionFactory vmfFactoryC;
    public static VisualMappingFunctionFactory vmfFactoryD;
    public static VisualMappingFunctionFactory vmfFactoryP;
    public static VisualStyleFactory visualStyleFactory;
    public static CyTableFactory tableFactory;
    public static CyApplicationConfiguration cyAppConfig;
    public static CyEventHelper cyEventHelper;
    public static CyApplicationManager cyApplicationManager;
    public static CyTableManager cyTableManager;

    public static SortCurrentNetworkAction sortCurrentNetworkAction;
    public static PSFCPanel psfcPanel;
    private static File PSFCDir;

    private static Logger PSFCLogger;
    private static File logFile;
    private static String logName = "PSFC.log";


    @Override
    public void start(BundleContext bc) throws Exception {
        cytoscapeDesktopService = getService(bc, CySwingApplication.class);
        taskManager = getService(bc, DialogTaskManager.class);
        cySessionManager = getService(bc, CySessionManager.class);
        networkFactory = getService(bc, CyNetworkFactory.class);
        networkViewFactory = getService(bc, CyNetworkViewFactory.class);
        networkManager = getService(bc, CyNetworkManager.class);
        networkViewManager = getService(bc, CyNetworkViewManager.class);
        visualMappingManager = getService(bc, VisualMappingManager.class);
        vmfFactoryC = getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
        vmfFactoryD = getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        vmfFactoryP = getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
        visualStyleFactory = getService(bc, VisualStyleFactory.class);
        tableFactory = getService(bc, CyTableFactory.class);
        cyAppConfig = getService(bc, CyApplicationConfiguration.class);
        cyEventHelper = getService(bc, CyEventHelper.class);
        cyApplicationManager = getService(bc, CyApplicationManager.class);
        cyTableManager = getService(bc, CyTableManager.class);
        cySessionManager = getService(bc, CySessionManager.class);

        sortCurrentNetworkAction = new SortCurrentNetworkAction();
        psfcPanel = new PSFCPanel();


        registerService(bc, cytoscapeDesktopService, CySwingApplication.class, new Properties());
        registerService(bc, taskManager, DialogTaskManager.class, new Properties());
        registerService(bc, cySessionManager, CySessionManager.class, new Properties());
        registerService(bc, networkFactory, CyNetworkFactory.class, new Properties());
        registerService(bc, networkViewFactory, CyNetworkViewFactory.class, new Properties());
        registerService(bc, networkViewManager, CyNetworkViewManager.class, new Properties());
        registerService(bc, networkManager, CyNetworkManager.class, new Properties());
        registerService(bc, visualMappingManager, VisualMappingManager.class, new Properties());
        registerService(bc, vmfFactoryC, VisualMappingFunctionFactory.class, new Properties());
        registerService(bc, vmfFactoryD, VisualMappingFunctionFactory.class, new Properties());
        registerService(bc, vmfFactoryP, VisualMappingFunctionFactory.class, new Properties());
        registerService(bc, visualStyleFactory, VisualStyleFactory.class, new Properties());
        registerService(bc, tableFactory, CyTableFactory.class, new Properties());
        registerService(bc, cyAppConfig, CyApplicationConfiguration.class, new Properties());
        registerService(bc, cyEventHelper, CyEventHelper.class, new Properties());
        registerService(bc, cyApplicationManager, CyApplicationManager.class, new Properties());
        registerService(bc, cyTableManager, CyTableManager.class, new Properties());

        registerService(bc, sortCurrentNetworkAction, CyAction.class, new Properties());
        registerService(bc, psfcPanel, CytoPanelComponent.class, new Properties());
    }

    public static Logger getLogger() {
        File loggingDir = null;

        if (logFile == null)
            loggingDir = setLoggingDirectory();
        if (loggingDir != null && loggingDir.exists()) {
            logFile = new File(loggingDir, logName);
            if (!logFile.exists())
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    LoggerFactory.getLogger(PSFCActivator.class).error(e.getMessage());
                }
            else {
                if (logFile.length() > (1024 * 1024))
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        LoggerFactory.getLogger(PSFCActivator.class).error(e.getMessage());
                    }
            }
        }
        PSFCLogger = Logger.getLogger(PSFCActivator.class);
        try {
            PSFCLogger.addAppender(new FileAppender(new PatternLayout(), logFile.getAbsolutePath(),true));
        } catch (IOException e) {
            LoggerFactory.getLogger(PSFCActivator.class).error(e.getMessage());
        }
        return PSFCLogger;
    }

    private static File setLoggingDirectory() {
        File loggingDir = new File(getPSFCDir(), "logs");
        boolean dirValid = true;
        if (!loggingDir.exists())
            dirValid = loggingDir.mkdir();
        if (dirValid)
            return loggingDir;
        return null;
    }

    public static File getPSFCDir() {
        if (PSFCDir == null) {
            createPluginDirectory();
        }
        return PSFCDir;
    }

    private static void createPluginDirectory() {
        File appConfigDir = cyAppConfig.getConfigurationDirectoryLocation();
        File appData = new File(appConfigDir, "app-data");
        if (!appData.exists())
            appData.mkdir();

        PSFCDir = new File(appData, "PSFC");
        if (!PSFCDir.exists())
            if (!PSFCDir.mkdir())
                LoggerFactory.getLogger(PSFCActivator.class).
                        error("Failed to create directory " + PSFCDir.getAbsolutePath());

    }




}
