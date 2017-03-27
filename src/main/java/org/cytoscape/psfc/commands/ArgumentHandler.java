package org.cytoscape.psfc.commands;

import org.cytoscape.work.TunableHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * Created by Lilit Nersisyan on 3/25/2017.
 */
public class ArgumentHandler implements TunableHandler {
    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return null;
    }

    @Override
    public void setValue(Object o) throws IllegalAccessException, InvocationTargetException {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean controlsMutuallyExclusiveNestedChildren() {
        return false;
    }

    @Override
    public String getChildKey() {
        return null;
    }

    @Override
    public String dependsOn() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getQualifiedName() {
        return null;
    }

    @Override
    public Properties getParams() {
        return null;
    }

    @Override
    public void handle() {

    }

    @Override
    public String[] listenForChange() {
        return new String[0];
    }

    @Override
    public Class<?> getType() {
        return null;
    }
}
