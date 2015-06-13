package org.cytoscape.psfc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by User on 6/13/2015.
 */
public class DoubleFormatter {
    static int precision = 10000;

    static NumberFormat bigformatter = new DecimalFormat("##E0");
    static NumberFormat smallformatter = new DecimalFormat("0.###");

    public static double formatDouble(Double value){
        if (Double.isInfinite(value))
            value = Double.POSITIVE_INFINITY;
        else if (value >= precision || value <= -1*precision)
            value = Double.parseDouble(bigformatter.format(value));
        else
            value = Double.parseDouble(smallformatter.format(value));
        return value;
    }
}
