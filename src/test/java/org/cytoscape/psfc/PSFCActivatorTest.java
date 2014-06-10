package org.cytoscape.psfc;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.internal.FakeBundleContext;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;


/**
 * Created by User on 5/24/2014.
 */
public final class PSFCActivatorTest {
    public static PSFCActivator psfcActivator;
    //
    // FakeBundleContext is provided by the service-api test-jar and
    // will create mock services for each Class specified in the
    // constructor.  Other services can be added using the registerService
    // method, like you'd use a normal BundleContext!
    //
    public static BundleContext bundleContext = new FakeBundleContext(
            CySwingApplication.class,
            DialogTaskManager.class,
            CySessionManager.class,
            CyNetworkFactory.class,
            CyNetworkManager.class,
            CyNetworkViewFactory.class,
            CyNetworkViewManager.class,
            VisualMappingManager.class,
            VisualMappingFunctionFactory.class,
            VisualStyleFactory.class,
            CyTableFactory.class,
            CyApplicationConfiguration.class,
            CyEventHelper.class,
            CySwingApplication.class,
            CyApplicationManager.class,
            CyTableManager.class
    );



}
