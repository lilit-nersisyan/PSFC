package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.*;
import org.cytoscape.psfc.PSFCActivator;
import org.cytoscape.psfc.gui.PSFCPanel;
import org.cytoscape.psfc.gui.enums.EColumnNames;
import org.cytoscape.psfc.net.NetworkCyManager;
import org.cytoscape.psfc.properties.EpsfcProps;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import sun.print.PSPrinterJob;

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
    private final double minNodeSignal;
    private final double midNodeSignal;
    private final double maxNodeSignal;
    private final PSFCPanel psfcPanel;
    private final double minEdgeSignal;
    private final double midEdgeSignal;
    private final double maxEdgeSignal;
    private CyNetwork network;
    private ArrayList<Integer> levels;
    private Color borderPaint = Color.black;
    private Color minNodeColor ;
    private Color midNodeColor ;
    private Color maxNodeColor ;
    private double minEdgeWidth = 1.;
    private double midEdgeWidth = 4.;
    private double maxEdgeWidth = 7.;



    public VisualizeFlowAction(CyNetwork network, double minNodeSignal, double midNodeSignal,
                               double maxNodeSignal, ArrayList<Integer> levels,
                               double minEdgeSignal, double midEdgeSignal, double maxEdgeSignal,
                               double minEdgeWidth, double midEdgeWidth, double maxEdgeWidth,
                               Color minNodeColor, Color midNodeColor, Color maxNodeColor,
                               PSFCPanel psfcPanel) {
        super("VisualizeFlowAction");
        this.network = network;
        this.minNodeSignal = minNodeSignal;
        this.midNodeSignal = midNodeSignal;
        this.maxNodeSignal = maxNodeSignal;
        this.minEdgeSignal = minEdgeSignal;
        this.midEdgeSignal = midEdgeSignal;
        this.maxEdgeSignal = maxEdgeSignal;
        this.minEdgeWidth = minEdgeWidth;
        this.midEdgeWidth = midEdgeWidth;
        this.maxEdgeWidth = maxEdgeWidth;
        this.minNodeColor = minNodeColor;
        this.midNodeColor = midNodeColor;
        this.maxNodeColor = maxNodeColor;
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
        private boolean isCancelled = false;

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
                System.out.println(("PSFC:: NodeSignalColumn not found."));
            else if (edgeSignalColumn == null)
                System.out.println(("PSFC:: EdgeSignalColumn not found."));
            else {
                ContinuousMapping<Double, Paint> nodeColorMapping;
                ContinuousMapping<Double, Double> edgeWidthMapping;
                try {
                    nodeColorMapping = (ContinuousMapping<Double, Paint>) PSFCActivator.vmfFactoryC.createVisualMappingFunction
                            (nodeSignalColumn.getName(), Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + nodeSignalColumn.getName() + " should be of type " + Double.class.getName());
                }
                for(int i =0 ; i < nodeColorMapping.getPointCount(); i++){
                    nodeColorMapping.removePoint(i);
                }


                BoundaryRangeValues<Paint> brvMin = new BoundaryRangeValues<Paint>(minNodeColor, minNodeColor, minNodeColor);
                nodeColorMapping.addPoint(minNodeSignal, brvMin);

                BoundaryRangeValues<Paint> brvMid = new BoundaryRangeValues<Paint>(midNodeColor, midNodeColor, midNodeColor);
                nodeColorMapping.addPoint(midNodeSignal, brvMid);

                BoundaryRangeValues<Paint> brvMax = new BoundaryRangeValues<Paint>(maxNodeColor, maxNodeColor, maxNodeColor);
                nodeColorMapping.addPoint(maxNodeSignal, brvMax);



                try {
                    edgeWidthMapping = (ContinuousMapping<Double, Double>) PSFCActivator.vmfFactoryC.createVisualMappingFunction
                            (edgeSignalColumn.getName(), Double.class, BasicVisualLexicon.EDGE_WIDTH);
                } catch (Exception e) {
                    throw new Exception("PSFC::Exception " + edgeSignalColumn.getName() + " should be of type " + Double.class.getName());
                }
                BoundaryRangeValues<Double> brvWidthMin = new BoundaryRangeValues<Double>(minEdgeWidth, minEdgeWidth, minEdgeWidth);
                edgeWidthMapping.addPoint(minEdgeSignal, brvWidthMin);
                BoundaryRangeValues<Double> brvWidthMid = new BoundaryRangeValues<Double>(midEdgeWidth, midEdgeWidth, midEdgeWidth);
                edgeWidthMapping.addPoint(midEdgeSignal, brvWidthMid);
                BoundaryRangeValues<Double> brvWidthMax = new BoundaryRangeValues<Double>(maxEdgeWidth, maxEdgeWidth, maxEdgeWidth);
                edgeWidthMapping.addPoint(maxEdgeSignal, brvWidthMax);


                VisualStyle visualStyle = PSFCActivator.visualMappingManager.getVisualStyle(NetworkCyManager.getNetworkView(network));

//                try {
                    ArrayList<CyNetworkView> networkViews = new ArrayList<CyNetworkView>();
                    networkViews.addAll(NetworkCyManager.getNetworkViews(network));

                    for (CyNetworkView networkView : networkViews) {
                        visualStyle.addVisualMappingFunction(nodeColorMapping);
                        visualStyle.addVisualMappingFunction(edgeWidthMapping);
                        visualStyle.apply(networkView);
                        if(isCancelled)
                            break;
                        for (CyNode cyNode : network.getNodeList()) {
                            View<CyNode> nodeView = networkView.getNodeView(cyNode);
                            nodeView.clearValueLock(BasicVisualLexicon.NODE_FILL_COLOR);
                            nodeView.clearValueLock(BasicVisualLexicon.NODE_BORDER_PAINT);
                            networkView.setVisualProperty(BasicVisualLexicon.NODE_BORDER_PAINT, borderPaint);
//                            CyRow cyRow = network.getDefaultNodeTable().getRow(cyNode.getSUID());
//                            nodeColorMapping.apply(cyRow,nodeView);
//                            System.out.println(cyRow.get(nodeSignalColumn.getName(), Double.class) + " - " + nodeColorMapping.getMappedValue(cyRow));
                        }
                        if(isCancelled)
                            break;
                        for (CyEdge cyEdge : network.getEdgeList()) {
                            View<CyEdge> edgeView = networkView.getEdgeView(cyEdge);
                            edgeView.clearValueLock(BasicVisualLexicon.EDGE_WIDTH);
                        }
                        if(isCancelled)
                            break;





                        networkView.updateView();

//                        Thread.sleep(500);
                    }

//                } catch (InterruptedException e) {
//                    throw new Exception("PSFC::Exception " + "problem while updating network view. Reason: " + e.getCause() + "\n" + Arrays.toString(e.getStackTrace()));
//                } finally {
//                    System.gc();
//                }
            }
        }

        @Override
        public void cancel() {
            isCancelled = true;
            System.gc();
        }

    }


}

