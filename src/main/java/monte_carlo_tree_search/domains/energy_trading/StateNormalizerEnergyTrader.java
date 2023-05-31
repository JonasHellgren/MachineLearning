package monte_carlo_tree_search.domains.energy_trading;

import common.ScalerLinear;
import lombok.Builder;

public class StateNormalizerEnergyTrader<S> {

    @Builder
    static
    class VariablesEnergyTradingDouble {
        double time;
        double SoE;
    }

    private static final double RANGE_MIN = -1.0;
    private static final double RANGE_MAX = 1.0;
    ScalerLinear timeScaler;
    ScalerLinear SoEScaler;

    public StateNormalizerEnergyTrader() {
        timeScaler =new ScalerLinear(0, EnvironmentEnergyTrading.MAX_TIME,RANGE_MIN, RANGE_MAX);
        SoEScaler =new ScalerLinear(EnvironmentEnergyTrading.SOE_MIN, EnvironmentEnergyTrading.SOE_MAX,RANGE_MIN, RANGE_MAX);
    }

    public VariablesEnergyTradingDouble normalize(S variables) {

        VariablesEnergyTrading cv=(VariablesEnergyTrading) variables;
        return VariablesEnergyTradingDouble.builder()
                .time(timeScaler.calcOutDouble(cv.time))
                .SoE(SoEScaler.calcOutDouble(cv.SoE))
                .build();

    }
}
