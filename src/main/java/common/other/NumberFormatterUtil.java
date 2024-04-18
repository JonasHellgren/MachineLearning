package common.other;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormatterUtil {

    private NumberFormatterUtil() {
    }

    static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US); //US <=> only dots
    public static final DecimalFormat formatterOneDigit = new DecimalFormat("#.#", SYMBOLS);
    public static final DecimalFormat formatterTwoDigits = new DecimalFormat("#.##", SYMBOLS);

}
