package safe_rl.persistance;

import org.apache.commons.math3.util.Pair;
import safe_rl.persistance.trade_environment.DayId;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ElDataFinals {
    public static final String PATH_DATA = "src/main/java/safe_rl/persistance/data/";
    public static final String RES_PATH= "src/main/java/safe_rl/runners/trading/results/";
    public  static final String PICS_FOLDER ="src/main/java/safe_rl/runners/pics";


    public static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US); //US <=> only dots
    public static final DecimalFormat formatter = new DecimalFormat("#.#", SYMBOLS);


    public static PathAndFile FILE_ENERGY = PathAndFile.xlsxOf(PATH_DATA, "Day-ahead-6months-EuroPerMWh");
    public static PathAndFile FILE_FCR = PathAndFile.xlsxOf(PATH_DATA, "FCR-N-6months-EuroPerMW");

    public static List<DayId> DAYS = List.of(
            DayId.of(24, 0, 0, "se3"),
            DayId.of(24, 0, 4, "se3"),
            DayId.of(24, 3, 8, "se3")  //high fcr price
    );

    public static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);

    public static final double POWER_MIN = 0d;
    public static final double POWER_CHARGE_MAX = 22d;
    public static final double PRICE_BATTERY = 30e3;

    public static final double SOC_START = 0.55;
    public static final double SOC_DELTA = 0.10;

//    public static final double SOC_TERMINAL_MIN = 0.95;
    public static final int N_SIMULATIONS_PLOTTING = 5;
    public static final int N_SIM_START_STATE_EVAL = 10;

    public static final double POOR_VALUE = -100d;
    public static final double POWER_TOL = 0.25;
    public static final double TOL_GOLDEN_SEARCH = 1d;
    public static final int N_ITER_MAX_GOLDEN_SEARCH = 100;
    public static final int NOF_EPISODES = 500;


    public static final double DUMMY_CAP = 0d;
    public static final double DUMMY_CAP_NON_ZERO = 10d;
    public static final int DYMMY_N_SIMULATIONS = 1;

}
