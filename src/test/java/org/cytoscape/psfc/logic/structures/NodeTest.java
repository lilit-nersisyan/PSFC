package org.cytoscape.psfc.logic.structures;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

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
    public void testConstructorID(){
        assert node.getID() == index;
        assert node.getValue() == 0.0;
    }

    @Test
    public void testConstructorIDValue(){
        double value = 1.0;
        Node node1 = new Node(index, value);
        assert (node1.getID() == index);
        assert (node1.getValue() == value);
    }

    @Test
    public void testGetValue() throws Exception {

    }

    @Test
    public void testSetValue() throws Exception {

    }

    @Test
    public void testGetID() throws Exception {
        assertTrue(node.getID() >= 0);
        thrown.expect(IllegalArgumentException.class);
        new Node(-1);

    }

    @Test
    public void testGetName() throws Exception {
        assertNotNull(node.getName());
    }

    @Test
    public void testSetName() throws Exception {
        node.setName(null);
        assert node.getName().equals("");
        String newName = "newName";
        node.setName(newName);
        assert node.getName().equals(newName);
        node.setName(null);
        assert node.getName().endsWith(newName);
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(node.toString());
    }

    @After
    public void setExceptionNone(){
        thrown = ExpectedException.none();
    }
}