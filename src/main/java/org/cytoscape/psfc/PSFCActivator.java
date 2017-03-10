package org.cytoscape.psfc;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.*;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.properties.EMultiSignalProps;
import org.cytoscape.psfc.properties.ENodeDataProps;
import org.cytoscape.psfc.properties.EpsfcProps;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class PSFCActivator extends AbstractCyActivator {
    public static CySwingApplication cytoscapeDesktopService;
    public static DialogTaskManager taskManager;
    public static TaskManager justTaskManager;
    public static SynchronousTaskManager synchTaskManager;
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
    public static CyNetworkTableManager cyNetworkTableManager;

    public static PSFCPanel psfcPanel;
    private static File PSFCDir;

    private static Logger PSFCLogger;
    private static File logFile;
    private static String logName = "PSFC.log";

    private static Properties psfcProps = null;
    private static File psfcPropsFile = null;
    private static String psfcPropsFileName = "psfc.props";
    private static String aboutText;
    private static String aboutFileName = "about.txt";
    private static String rulePresetsFileName = "rule_presets.pdf";
    private static String userManualFileName = "PSFC_User_Manual.pdf";
    private static String userManualURL = "http://big.sci.am/apps/psfc/PSFC_User_Manual.pdf";
    private static String projectWebpageUrl = "http://apps.cytoscape.org/apps/psfc";

    public static String getAboutText() {
        if (aboutText == null) {
            aboutText = "";
            ClassLoader classLoader = PSFCActivator.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(aboutFileName);
            Reader reader = new InputStreamReader(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            char[] chars = new char[1024];
            try {
                while ((reader.read(chars) > 0)) {
                    stringBuilder.append(chars);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            aboutText = stringBuilder.toString();
        }
        return aboutText;
    }

    public static String getUserManualFileName() {
        return userManualFileName;
    }

    public static String getUserManualURL() {
        return userManualURL;
    }


    @Override
    public void start(BundleContext bc) throws Exception {
        cytoscapeDesktopService = getService(bc, CySwingApplication.class);
        taskManager = getService(bc, DialogTaskManager.class);
        justTaskManager = getService(bc, TaskManager.class);
        synchTaskManager = getService(bc, SynchronousTaskManager.class);
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
        cyNetworkTableManager = getService(bc, CyNetworkTableManager.class);
        cySessionManager = getService(bc, CySessionManager.class);

        psfcPanel = new PSFCPanel();


        registerService(bc, cytoscapeDesktopService, CySwingApplication.class, new Properties());
        registerService(bc, taskManager, DialogTaskManager.class, new Properties());
        registerService(bc, justTaskManager, TaskManager.class, new Properties());
        registerService(bc, synchTaskManager, SynchronousTaskManager.class, new Properties());
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
        registerService(bc, cyNetworkTableManager, CyNetworkTableManager.class, new Properties());

        registerService(bc, psfcPanel, CytoPanelComponent.class, new Properties());

//        EpiNetSimulator epiNetSimulator = new EpiNetSimulator();
//        epiNetSimulator.setBC(bc);

    }


    public static Logger getLogger() {
        if (PSFCLogger != null)
            return PSFCLogger;
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
                if (logFile.length() > (1024 * 1024)) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("HH_mm_dd_MM_yy");
                        boolean success = logFile.renameTo(new File(loggingDir, logFile.getName() + dateFormat.format(new Date())));

                        if (success) {
                            if (!logFile.createNewFile())
                                LoggerFactory.getLogger(PSFCActivator.class).error("Could not create new PSFC log file");
                        } else
                            LoggerFactory.getLogger(PSFCActivator.class).error("Could not rename log file");
                    } catch (IOException e) {
                        LoggerFactory.getLogger(PSFCActivator.class).error(e.getMessage());
                    }
                }
            }
        }
        PSFCLogger = Logger.getLogger(PSFCActivator.class);

        try {
            PSFCLogger.addAppender(new FileAppender(new PatternLayout(), logFile.getAbsolutePath(), true));
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

    private static void initProperties() {
        psfcPropsFile = new File(PSFCActivator.getPSFCDir(), psfcPropsFileName);
        FileInputStream stream = null;
        if (psfcPropsFile.exists())
            try {
                stream = new FileInputStream(PSFCActivator.getPSFCDir().getAbsolutePath() + "/" + psfcPropsFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        boolean isPropsFileValid = true;

        if (stream != null) {
            if (psfcProps == null) {
                psfcProps = new Properties();
                try {
                    psfcProps.load(stream);
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    PSFCLogger.error(e.getMessage());
                }
            }

            for (EpsfcProps epsfcProps : EpsfcProps.values()) {
                if (psfcProps.getProperty(epsfcProps.getName()) == null) {
                    isPropsFileValid = false;
                    break;
                }
            }
            if (isPropsFileValid)
                for (ENodeDataProps props : ENodeDataProps.values()) {
                    if (psfcProps.getProperty(props.getName()) == null) {
                        isPropsFileValid = false;
                        break;
                    }
                }
            if (isPropsFileValid)
                for (EMultiSignalProps props : EMultiSignalProps.values()) {
                    if (psfcProps.getProperty(props.getName()) == null) {
                        isPropsFileValid = false;
                        break;
                    }
                }


        } else
            isPropsFileValid = false;

        if (!isPropsFileValid) {
            try {
                if (psfcPropsFile.exists())
                    psfcPropsFile.delete();
                psfcPropsFile.createNewFile();
                ClassLoader cl = PSFCActivator.class.getClassLoader();
                InputStream in = cl.getResourceAsStream(psfcPropsFileName);
                psfcProps = new Properties();
                psfcProps.load(in);
                psfcProps.store(new PrintWriter(getpsfcPropsFile()), "");
            } catch (IOException e) {
                PSFCLogger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        for (EpsfcProps property : EpsfcProps.values()) {
            property.setOldValue(Boolean.parseBoolean((String) PSFCActivator.getPsfcProps().get(property.getName())));
            property.setNewValue(Boolean.parseBoolean((String) PSFCActivator.getPsfcProps().get(property.getName())));
        }

    }

    public static Properties getPsfcProps() {
        if (psfcProps == null)
            initProperties();
        return psfcProps;
    }

    private static File getpsfcPropsFile() {
        if (psfcPropsFile == null)
            initProperties();
        return psfcPropsFile;
    }


    public static File getRecentDirectory() {
        File recentDirFile = new File(PSFCActivator.getPSFCDir(), "recentDir.txt");
        File recentDirectory = getRecentDirectoryFile();
        try {
            Scanner scanner = new Scanner(recentDirFile);
            if (scanner.hasNextLine())
                recentDirectory = new File(scanner.nextLine());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (recentDirectory == null)
            recentDirectory = PSFCActivator.getPSFCDir();
        return recentDirectory;
    }

    private static File getRecentDirectoryFile() {
        File recentDirFile = new File(PSFCActivator.getPSFCDir(), "recentDir.txt");
        if (!recentDirFile.exists())
            try {
                recentDirFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return recentDirFile;
    }

    public static void writeRecentDirectory(String selectedFilePath) {
        try {
            PrintWriter recentDirWriter = new PrintWriter(getRecentDirectoryFile());
            recentDirWriter.write(selectedFilePath);
            recentDirWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static File getPsfcPropsFile() {
        if (psfcPropsFile == null)
            initProperties();
        return psfcPropsFile;
    }

    public static File getPsfcLogFile() {
        if (getLogger() != null)
            return logFile;
        return null;
    }

    public static String getRulePresetsFileName() {
        return rulePresetsFileName;
    }

    public static String getProjectWebpageUrl() {
        return projectWebpageUrl;
    }


}