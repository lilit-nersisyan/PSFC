package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;

public class EdgeTest {
    private Edge edge;
    private Node source;
    private Node target;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        source = new Node(0);
        target = new Node(1);
        edge = new Edge(source, target);
    }

    @Test
    public void testConstructor(){
        assert edge.getSource().equals(source);
        assert edge.getTarget().equals(target);
        thrown.expect(NullPointerException.class);
        new Edge(null, new Node(0));
        new Edge(new Node(0), null);

    }

    @Test
    public void testGetSource(){
        assertNotNull(edge.getSource());
    }

    @Test
    public void testGetTarget(){
        assertNotNull(edge.getTarget());
    }

    @After
    public void tearDown() throws Exception {
        thrown = ExpectedException.none();
    }

}