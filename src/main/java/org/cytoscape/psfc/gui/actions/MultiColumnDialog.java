package org.cytoscape.psfc.gui.actions;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lilit Nersisyan on 3/14/2017.
 */
public class MultiColumnDialog extends JDialog  {
    public MultiColumnDialog(JFrame parentFrame) {
        super(parentFrame);
        setSize(500, 500);
        setContentPane(new MultiPanel());
        setLocation(200, 200);
        repaint();
        pack();
    }



    private class MultiPanel extends JPanel {
        MultiPanel(){
            initComponents();
        }
        // Variables declaration - do not modify
        private javax.swing.JButton jb_cancel;
        private javax.swing.JLabel jl_allCompleteMessage;
        private javax.swing.JLabel jl_columnName;
        private javax.swing.JLabel jl_columnNameTag;
        private javax.swing.JLabel jl_columnNum;
        private javax.swing.JLabel jl_columnNumTag;
        private javax.swing.JLabel jl_currentTask;
        private javax.swing.JLabel jl_currentTaskTag;
        private javax.swing.JLabel jl_timeElapsed;
        private javax.swing.JLabel jl_timeElapsedTag;
        private javax.swing.JLabel jl_title;
        private javax.swing.JLabel jl_totalNum;
        private javax.swing.JLabel jl_totalNumTag;
        private javax.swing.JProgressBar jpb_progressbar;
        // End of variables declaration

        private void initComponents() {
            jl_title = new javax.swing.JLabel();
            jl_columnNameTag = new javax.swing.JLabel();
            jl_columnNumTag = new javax.swing.JLabel();
            jl_totalNumTag = new javax.swing.JLabel();
            jb_cancel = new javax.swing.JButton();
            jl_timeElapsedTag = new javax.swing.JLabel();
            jpb_progressbar = new javax.swing.JProgressBar();
            jl_columnName = new javax.swing.JLabel();
            jl_columnNum = new javax.swing.JLabel();
            jl_totalNum = new javax.swing.JLabel();
            jl_timeElapsed = new javax.swing.JLabel();
            jl_currentTaskTag = new javax.swing.JLabel();
            jl_currentTask = new javax.swing.JLabel();
            jl_allCompleteMessage = new javax.swing.JLabel();

            jl_title.setText("Computing PSFC for multiple columns");

            jl_columnNameTag.setText("Column name: ");

            jl_columnNumTag.setText("Column number:");

            jl_totalNumTag.setText("Out of:");

            jb_cancel.setBackground(new java.awt.Color(51, 102, 0));
            jb_cancel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
            jb_cancel.setForeground(new java.awt.Color(51, 102, 0));
            jb_cancel.setText("Cancel");
            jb_cancel.setBorder(javax.swing.BorderFactory.createCompoundBorder());

            jl_timeElapsedTag.setText("Time elapsed:");

            jl_columnName.setForeground(new java.awt.Color(51, 102, 0));
            jl_columnName.setText("name");

            jl_columnNum.setForeground(new java.awt.Color(51, 102, 0));
            jl_columnNum.setText("num");

            jl_totalNum.setForeground(new java.awt.Color(51, 102, 0));
            jl_totalNum.setText("num");

            jl_timeElapsed.setForeground(new java.awt.Color(51, 102, 0));
            jl_timeElapsed.setText("time");

            jl_currentTaskTag.setText("Current task:");

            jl_currentTask.setForeground(new java.awt.Color(51, 102, 0));
            jl_currentTask.setText("task");

            jl_allCompleteMessage.setForeground(new java.awt.Color(51, 102, 0));
            jl_allCompleteMessage.setText("All complete message");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGap(31, 31, 31)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jl_allCompleteMessage)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                    .addGroup(layout.createSequentialGroup()
                                                                            .addComponent(jl_timeElapsedTag)
                                                                            .addGap(26, 26, 26)
                                                                            .addComponent(jl_timeElapsed)
                                                                            .addGap(152, 152, 152))
                                                                    .addComponent(jpb_progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(jb_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(jl_currentTaskTag))
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(layout.createSequentialGroup()
                                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                            .addComponent(jl_currentTask)
                                                                            .addGroup(layout.createSequentialGroup()
                                                                                    .addComponent(jl_columnNumTag)
                                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                    .addComponent(jl_columnNum)))
                                                                    .addGap(62, 62, 62)
                                                                    .addComponent(jl_totalNumTag)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(jl_totalNum))
                                                            .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(jl_columnNameTag)
                                                                    .addGap(18, 18, 18)
                                                                    .addComponent(jl_columnName))
                                                            .addComponent(jl_title, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jl_title)
                                    .addGap(27, 27, 27)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jl_columnNameTag)
                                            .addComponent(jl_columnName))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jl_columnNumTag)
                                            .addComponent(jl_totalNumTag)
                                            .addComponent(jl_columnNum)
                                            .addComponent(jl_totalNum))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jl_currentTaskTag)
                                            .addComponent(jl_currentTask))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jl_timeElapsedTag)
                                            .addComponent(jl_timeElapsed))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jpb_progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jl_allCompleteMessage)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                                    .addComponent(jb_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
            );

            pack();
        }

    }

}
