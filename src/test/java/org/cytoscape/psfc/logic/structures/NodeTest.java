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
    private int index = 0;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        node = new Node(index);
    }

    @Test
    public void testConstructorIndex(){
        assertNotNull(node.getID());
        assert node.getID() == index;
        assertNotNull(node.getValue());
        assert node.getValue() == 0.0;
    }

    @Test
    public void testConstructorIndexValue(){
        double value = 1.0;
        Node node1 = new Node(index, value);
        assertNotNull(node1.getID());
        assert (node1.getID() == index);
        assert (node1.getValue() == value);
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
        assertNotNull(node.getID());
        assertTrue(node.getID() >= 0);

        thrown.expect(IllegalArgumentException.class);
        new Node(-1);

    }

    @After
    public void setExceptionNone(){
        thrown = ExpectedException.none();
    }

}