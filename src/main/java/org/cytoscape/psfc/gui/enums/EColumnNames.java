package org.cytoscape.psfc.gui.enums;

/**
 * Created by User on 7/6/2014.
 */
public enum EColumnNames {
    Level("psfc.level"),
    PSFC_NODE_SIGNAL("psf_l"),
    PSFC_EDGE_SIGNAL("psf_l"),
    PSFC_PVAL("psfc_pval"),
    PSFC_FINAL("psf_final"),
    PSFC_IS_BACKWARD("psf_isbackward"),
    PSFC_isOperator("psf_isOperator"),
    PSFC_FUNCTION("psf_function")
    ;


    private String columnName;
    private EColumnNames(String columnName){
        this.columnName = columnName;
    }

    public String getName() {
        return columnName;
    }
}
