package book_rl_explained.lunar_lander.domain.environment.startstate_suppliers;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StartStateSupplierI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.hellgren.utilities.math.MyMathUtils;

@AllArgsConstructor
public class StartStateSupplierRandomAndClipped implements StartStateSupplierI   {

    LunarProperties ep;
    Pair<Double,Double> yMinMax;
    Pair<Double,Double> spdMinMax;

    public static StartStateSupplierI create(LunarProperties ep, Pair<Double,Double> yMinMax, Pair<Double,Double> spdMinMax) {
        return new StartStateSupplierRandomAndClipped(ep, yMinMax,spdMinMax);
    }

    @Override
    public StateLunar getStartState() {
        var state = StateLunar.randomPosAndSpeed(ep);
        double yClipped = MyMathUtils.clip(state.y(), yMinMax.getFirst(), yMinMax.getSecond());
        double spdClipped = MyMathUtils.clip(state.spd(), spdMinMax.getFirst(), spdMinMax.getSecond());
        return StateLunar.of(yClipped, spdClipped);
    }
}
