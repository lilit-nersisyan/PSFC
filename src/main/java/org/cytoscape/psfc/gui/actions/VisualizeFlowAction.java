package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * PUBLIC CLASS VisualizeFlowAction
 * Initializes an iterator of ColorNodesTask,
 * which performs node color visual mapping based on node signals.
 */
public class VisualizeFlowAction extends AbstractCyAction {
    private final double minSignal;
    private final double maxSignal;
    private final PSFCPanel psfcPanel;
    private final double minEdgeSignal;
    private final double maxEdgeSignal;
    private CyNetwork network;
    private ArrayList<Integer> levels;
    private Color borderPaint = Color.black;
    private Color minColor = Color.decode("#40E0D0");
    private Color maxColor = Color.decode("#000000");
    private double minWidth = 2.;
    private double maxWidth = 7.;


    public VisualizeFlowAction(CyNetwork network, double minSignal,
                               double maxSignal, ArrayList<Integer> levels,
                               double minEdgeSignal, double maxEdgeSignal,
                               PSFCPanel psfcPanel) {
        super("VisualizeFlowAction");
        this.network = network;
        this.minSignal = minSignal;
        this.maxSignal = maxSignal;
        this.minEdgeSignal = minEdgeSignal;
        this.maxEdgeSignal = maxEdgeSignal;
        this.levels = levels;
        this.psfcPanel = psfcPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TaskIterator taskIterator = new TaskIterator();
        for (int level : levels) {
            VisualizeFlowTask visualizeFlowTask = new VisualizeFlowTask(level);
            taskIterator.append(visualizeFlowTask);
        }
        PSFCActivator.taskManager.execute(taskIterator);

    }

    private class VisualizeFlowTask extends AbstractTask {
        private final CyColumn nodeSignalColumn;
        private final CyColumn edgeSignalColumn;
        private int level;

        private VisualizeFlowTask(int level) {
            this.level = level;
            String nodeColumnName = EColumnNames.PSFC_NODE_SIGNAL.getName() + level;
            nodeSignalColumn = network.getDefaultNodeTable().getColumn(nodeColumnName);
            String edgeColumnName = EColumnNames.PSFC_EDGE_SIGNAL.getName() + level;
            edgeSignalColumn = network.getDefaultEdgeTable().getColumn(edgeColumnName);
            if (nodeSignalColumn == null) {
                JOptionPane.showMessageDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
                        "CyColumn " + nodeColumnName + " was not found for visualization.",
                        "PSFC user message", JOptionPane.OK_OPTION);
            }
            if (edgeSignalColumn == null) {
                JOptionPane.showMessageDialog(PSFCActivator.cytoscapeDesktopService.getJFrame(),
                        "CyColumn " + nodeColumnName + " was not found for visualization.",
                        "PSFC user message", JOptionPane.OK_OPTION);
            }
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("PSFC:VisualizeFlowTask");
            psfcPanel.getJsl_levels().setValue(level);
            psfcPanel.updateUI();
            if (nodeSignalColumn == null)
                System.out.println(("NodeSignalColumn not found."));
            else if (edgeSignalColumn == null)
                System.out.println(("EdgeSignalColumn not found."));
            else {
                ContinuousMapping<Double, Paint> nodeColorMapping;
                ContinuousMapping<Double, Double> edgeWidthMapping;
                try {
                    nodeColorMapping = (ContinuousMapping<Double, Paint>) PSFCActivator.vmfFactoryC.createVisualMappingFunction
                            (nodeSignalColumn.getName(), Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
                } catch (Exception e) {
                    throw new Exception(nodeSignalColumn.getName() + " should be of type " + Double.class.getName());
                }
                BoundaryRangeValues<Paint> brvMin = new BoundaryRangeValues<Paint>(Color.WHITE, minColor, minColor);
                nodeColorMapping.addPoint(minSignal, brvMin);
                BoundaryRangeValues<Paint> brvMax = new BoundaryRangeValues<Paint>(maxColor, maxColor, Color.BLACK);
                nodeColorMapping.addPoint(maxSignal, brvMax);

                try {
                    edgeWidthMapping = (ContinuousMapping<Double, Double>) PSFCActivator.vmfFactoryC.createVisualMappingFunction
                            (edgeSignalColumn.getName(), Double.class, BasicVisualLexicon.EDGE_WIDTH);
                } catch (Exception e) {
                    throw new Exception(edgeSignalColumn.getName() + " should be of type " + Double.class.getName());
                }
                BoundaryRangeValues<Double> brvWidthMin = new BoundaryRangeValues<Double>(1., minWidth, minWidth);
                edgeWidthMapping.addPoint(minEdgeSignal, brvWidthMin);
                BoundaryRangeValues<Double> brvWidthMax = new BoundaryRangeValues<Double>(maxWidth, maxWidth, 8.);
                edgeWidthMapping.addPoint(maxEdgeSignal, brvWidthMax);


                VisualStyle visualStyle = PSFCActivator.visualMappingManager.getVisualStyle(NetworkCyManager.getNetworkView(network));

                ArrayList<CyNetworkView> networkViews = new ArrayList<CyNetworkView>();
                networkViews.addAll(NetworkCyManager.getNetworkViews(network));
                for (CyNetworkView networkView : networkViews) {
                    for (CyNode cyNode : network.getNodeList()) {
                        View<CyNode> nodeView = networkView.getNodeView(cyNode);
                        nodeView.clearValueLock(BasicVisualLexicon.NODE_FILL_COLOR);
                        nodeView.clearValueLock(BasicVisualLexicon.NODE_BORDER_PAINT);
                    }
                    for (CyEdge cyEdge : network.getEdgeList()) {
                        View<CyEdge> edgeView = networkView.getEdgeView(cyEdge);
                        edgeView.clearValueLock(BasicVisualLexicon.EDGE_WIDTH);
                    }

                    visualStyle.addVisualMappingFunction(nodeColorMapping);
                    visualStyle.addVisualMappingFunction(edgeWidthMapping);
                    networkView.setVisualProperty(BasicVisualLexicon.NODE_BORDER_PAINT, borderPaint);
                    visualStyle.apply(networkView);
                    networkView.updateView();
                    Thread.sleep(500);
                }
            }
        }
        @Override
        public void cancel(){
            System.gc();
        }

    }


}

