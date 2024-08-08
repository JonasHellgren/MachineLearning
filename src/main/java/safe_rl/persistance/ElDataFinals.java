package safe_rl.persistance;

import org.apache.commons.math3.util.Pair;
import safe_rl.persistance.trade_environment.DayId;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ElDataFinals {
    public static final String PATH = "src/main/java/safe_rl/persistance/data/";

    public static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US); //US <=> only dots
    public static final DecimalFormat formatter = new DecimalFormat("#.#", SYMBOLS);


    public static PathAndFile FILE_ENERGY = PathAndFile.xlsxOf(PATH, "Day-ahead-6months-EuroPerMWh");
    public static PathAndFile FILE_FCR = PathAndFile.xlsxOf(PATH, "FCR-N-6months-EuroPerMW");

    public static List<DayId> DAYS = List.of(
            DayId.of(24, 0, 0, "se3"),
            DayId.of(24, 0, 4, "se3"),
            DayId.of(24, 3, 8, "se3")  //high fcr price
    );

    public static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);

    public static final double POWER_MIN = 0d;

    public static final double SOC_START = 0.5;
    public static final double SOC_TERMINAL_MIN = 0.95;
    public static final int N_SIMULATIONS = 5;

    public static final double POOR_VALUE = -100d;

    public static final double TOL_GOLDEN_SEARCH = 1d;
    public static final int N_ITER_MAX_GOLDEN_SEARCH = 100;


}
