package org.cytoscape.psfc.logic.algorithms;

import org.junit.Test;

import java.util.Collections;
import java.util.PriorityQueue;

import static org.junit.Assert.assertTrue;

/**
 * Created by User on 12/3/2014.
 */
public class BootstrapTest {

    @Test
    public void testGetOne2OneCorrespondence() throws Exception {
        int order;
        for (order = 0; order < 5; order++) {
            int[][] one2oneCor = Bootstrap.getOne2OneCorrespondence(order);
            assert one2oneCor.length == order;
            if (order != 0) {
                assert one2oneCor[0].length == 2;
                boolean corExists = false;
                for (int i = 0; i < order; i++) {
                    for (int j = 0; j < order; j++)
                        if (one2oneCor[j][1] == i)
                            corExists = true;
                    assertTrue(corExists);
                }
            }
        }
    }

    /**
     * With a tested value and the queue of resampled values, the p value returned should be
     * - a) 1 if all the values in the queue are equal to the tested value;
     * - b) 0 if all the values in the queue are less than or greater than the tested value;
     * - c) the proportion of values less than the tested value,
     * if the tested value is less than the queue mean
     * - d) the proportion of values greater than or equal than the tested value,
     * if the tested value is greater than or equal to the queue mean
     * - e) 1 if the queue is empty
     *
     * @throws Exception
     */
    @Test
    public void testGetBootstrapPValue() throws Exception {

        // Case a)
        double tValue = 1;
        Double[] qValues = new Double[]{1., 1., 1.};
        PriorityQueue<Double> queue = new PriorityQueue<Double>();
        Collections.addAll(queue, qValues);
        Bootstrap bootstrap = new Bootstrap(queue.size());
        double pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 1;

        // Case b)
        qValues = new Double[]{0., 0.};
        queue = new PriorityQueue<Double>();
        Collections.addAll(queue, qValues);
        bootstrap = new Bootstrap(queue.size());
        pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 0;

        qValues = new Double[]{2., 2.};
        queue = new PriorityQueue<Double>();
        Collections.addAll(queue, qValues);
        bootstrap = new Bootstrap(queue.size());
        pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 0;

        // Case c)
        qValues = new Double[]{-1., 1., 5.};
        queue = new PriorityQueue<Double>();
        Collections.addAll(queue, qValues);
        bootstrap = new Bootstrap(queue.size());
        pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 1/3.;

        // Case d)
        qValues = new Double[]{-1., 0., 1., 0.};
        queue = new PriorityQueue<Double>();
        Collections.addAll(queue, qValues);
        bootstrap = new Bootstrap(queue.size());
        pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 0.25;

        // Case e)
        queue = new PriorityQueue<Double>();
        bootstrap = new Bootstrap(queue.size());
        pValue = bootstrap.getBootstrapPValue(tValue, queue);
        assert pValue == 1;

    }
}
