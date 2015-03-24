package org.cytoscape.psfc.gui.enums;

/**
 * Created by User on 7/6/2014.
 */
public enum EColumnNames {
    Level("psfc.level"),
    PSFC_NODE_SIGNAL("signal_"),
    PSFC_EDGE_SIGNAL("signal_"),
    PSFC_PVAL("psfc_pVal"),
    PSFC_FINAL("psf_final")
    ;


    private String columnName;
    private EColumnNames(String columnName){
        this.columnName = columnName;
    }

    public String getName() {
        return columnName;
    }
}
