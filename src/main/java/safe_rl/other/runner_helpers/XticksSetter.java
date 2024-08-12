package safe_rl.other.runner_helpers;

import common.list_arrays.ListUtils;
import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.XYChart;
import safe_rl.environments.trading_electricity.SettingsTrading;

import java.util.List;
import java.util.function.Function;

public class XticksSetter {

    public static void setXTicks(XYChart chart, Pair<Integer, Integer> fromToHour1) {
        double timeMax = 24;
        Function<Double, String> xTickFormatter = time -> {
            double timeAdded=time+fromToHour1.getFirst();
            double timeShowed=(timeAdded< timeMax)?timeAdded:(timeAdded- timeMax);
            return String.format("%.1f", timeShowed);
        };
        chart.getStyler().setxAxisTickLabelsFormattingFunction(xTickFormatter);
    }


    public static List<Double> getXTicks(List<Double> yList, SettingsTrading settings1) {
        return ListUtils.doublesStartEndStep(0, settings1.dt() * (yList.size() - 1), settings1.dt());
    }


}
