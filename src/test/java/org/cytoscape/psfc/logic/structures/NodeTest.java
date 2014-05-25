package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NodeTest {
    private Node node;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        node = new Node(0);
    }
    @Test
    public void testGetValue() throws Exception {
        assertNotNull(node.getValue());
        System.out.println(node.getValue());
    }

    @Test
    public void testSetValue() throws Exception {

    }

    @Test
    public void testGetIndex() throws Exception {
        assertNotNull(node.getIndex());
        assertTrue(node.getIndex() >= 0);

        thrown.expect(IllegalArgumentException.class);
        new Node(-1);

    }

    @After
    public void setExceptionNone(){
        thrown = ExpectedException.none();
    }

}