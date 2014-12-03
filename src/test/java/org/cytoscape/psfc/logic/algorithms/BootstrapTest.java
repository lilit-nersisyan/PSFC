package org.cytoscape.psfc.logic.algorithms;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by User on 12/3/2014.
 */
public class BootstrapTest {

    @Test
    public void testGetOne2OneCorrespondence() throws Exception{
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
}
