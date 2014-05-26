package org.cytoscape.psfc;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.psfc.gui.actions.CreateSimpleNetworkFile;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

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

    public static CreateSimpleNetworkFile createSimpleNetworkFile;


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

        createSimpleNetworkFile = new CreateSimpleNetworkFile();

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

        registerService(bc, createSimpleNetworkFile, CyAction.class, new Properties());
    }
}
