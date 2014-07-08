package org.cytoscape.psfc.gui.enums;

/**
 * Created by User on 7/6/2014.
 */
public enum EColumnNames {
    Level("psfc.level")
    ;


    private String columnName;
    private EColumnNames(String columnName){
        this.columnName = columnName;
    }

    public String getName() {
        return columnName;
    }
}
