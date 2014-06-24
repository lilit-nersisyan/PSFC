package org.cytoscape.psfc.logic.parsers;

import org.cytoscape.psfc.ExceptionMessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * PUBLIC CLASS EdgeTypeRuleNameParser
 */
public class EdgeTypeRuleNameParser {
    private File configFile;
    private BufferedReader reader;
    private HashMap<String, String> edgeTypeRuleNameMap;

    public EdgeTypeRuleNameParser(File configFile) throws FileNotFoundException {
        if (configFile == null)
            throw new NullPointerException(ExceptionMessages.NullConfigFile.getMessage());
        this.configFile = configFile;
        reader = new BufferedReader(new FileReader(configFile));
    }

    public void readConfigFile(){

    }
}
