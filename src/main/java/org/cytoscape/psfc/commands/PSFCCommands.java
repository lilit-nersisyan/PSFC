package org.cytoscape.psfc.commands;

import org.cytoscape.command.AvailableCommands;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lilit Nersisyan on 3/25/2017.
 */
public class PSFCCommands implements AvailableCommands {
    @Override
    public List<String> getNamespaces() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("psfc");
        return list;
    }

    @Override
    public List<String> getCommands(String s) {
        return null;
    }

    @Override
    public String getDescription(String s, String s1) {
        return null;
    }

    @Override
    public List<String> getArguments(String s, String s1) {
        return null;
    }

    @Override
    public boolean getArgRequired(String s, String s1, String s2) {
        return false;
    }

    @Override
    public String getArgTooltip(String s, String s1, String s2) {
        return null;
    }

    @Override
    public String getArgDescription(String s, String s1, String s2) {
        return null;
    }

    @Override
    public Class<?> getArgType(String s, String s1, String s2) {
        return null;
    }

    @Override
    public Object getArgValue(String s, String s1, String s2) {
        return null;
    }

    @Override
    public String getArgTypeString(String s, String s1, String s2) {
        return null;
    }
}
