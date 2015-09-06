package org.cytoscape.psfc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by User on 6/13/2015.
 */
public class DoubleFormatter {
    static int precision = 10000;

    public static double formatDouble(Double value){
        DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
        decimalSymbol.setDecimalSeparator('.');
        NumberFormat bigformatter = new DecimalFormat("##E0", decimalSymbol);
        NumberFormat smallformatter = new DecimalFormat("0.##", decimalSymbol);

        bigformatter.setGroupingUsed(false);

        smallformatter.setGroupingUsed(false);

        if (Double.isInfinite(value))
            value = Double.POSITIVE_INFINITY;
        else if (value >= precision || value <= -1*precision)
            value = Double.parseDouble(bigformatter.format(value));
        else
            value = Double.parseDouble(smallformatter.format(value));
        return value;
    }

    public static void main(String[] args) {
        System.out.println(formatDouble(5.83));
    }
}
