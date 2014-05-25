package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;

public class EdgeTest {
    private Edge edge;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        edge = new Edge(new Node(0), new Node(1));
    }

    @Test
    public void testConstructor(){
        assertNotNull(edge.getSource());
        assertNotNull(edge.getTarget());
        thrown.expect(NullPointerException.class);
        new Edge(null, new Node(0));
        new Edge(new Node(0), null);

    }

    @After
    public void tearDown() throws Exception {
        thrown = ExpectedException.none();
    }

}