package safe_rl.environments.trading;

import common.list_arrays.ListUtils;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.RandomActionSimulator;
import java.util.ArrayList;
import java.util.List;

@Log
public class TestEnvironmentTrading5hRandomSearch {
    EnvironmentTrading environment;
    public static final double SOC = 0.2;
    public static final double TOL_POWER = 0.5;
    SettingsTrading settingsNonZeroFCR;
    SafetyLayerBuying<VariablesTrading> safetyLayer;
    StateBuying state;


    @BeforeEach
    void init() {
        settingsNonZeroFCR = SettingsTrading.new5HoursIncreasingPrice().withPowerCapacityFcr(0);
        environment = new EnvironmentTrading(settingsNonZeroFCR);
    }


    @Test
    void whenRandomSearch_thenSomePositiveRevenue() {
        var simulator = RandomActionSimulator.<VariablesTrading>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(0d, settingsNonZeroFCR.powerBattMax())).build();

        Pair<Double, List<Double>> bestRes = Pair.of(0d, new ArrayList<>());
        for (int i = 0; i < 1000; i++) {
            var stateStart = StateTrading.of(VariablesTrading.newSoc(SOC));
            var simRes = simulator.simulate(stateStart);
            bestRes = getBestRes(bestRes, simRes);
            Assertions.assertTrue(settingsNonZeroFCR.timeEnd() < simRes.getLeft().getVariables().time());
        }

        double powerMax = settingsNonZeroFCR.powerBattMax();
        List<Double> powerList = bestRes.getRight();
        Assertions.assertEquals(powerMax, powerList.get(0), TOL_POWER);
        Assertions.assertEquals(powerMax, powerList.get(1), TOL_POWER);
    }

    Pair<Double, List<Double>> getBestRes(Pair<Double, List<Double>> bestReturnPowerList,
                                          Triple<StateI<VariablesTrading>, List<Double>, List<Double>> simRes
    ) {
        var sumReward = ListUtils.sumList(simRes.getMiddle());
        if (sumReward > bestReturnPowerList.getLeft()) {
            log.info("Better sumReward found, simRes=" + simRes);
            bestReturnPowerList = Pair.of(sumReward, simRes.getRight());
        }
        return bestReturnPowerList;
    }


}
