package safe_rl.environments.buying;

import common.list_arrays.ListUtils;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.helpers.RandomActionSimulator;

import java.util.ArrayList;
import java.util.List;

@Log
public class TestRandomSearchNew3HoursSamePrice {

    public static final double SOC = 0.2;
    public static final double TOL_POWER = 0.5;
    BuySettings settings3 = BuySettings.new3HoursSamePrice();
    EnvironmentBuying environment;
    SafetyLayer<VariablesBuying> safetyLayer;
    StateBuying state;

    @BeforeEach
    void init() {
        environment = new EnvironmentBuying(settings3);
        state = StateBuying.of(VariablesBuying.newSoc(SOC));
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3));
    }

    @Test
    void whenManySteps_thenHighPowerGood() {
        var simulator = RandomActionSimulator.<VariablesBuying>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(0d, settings3.powerBattMax())).build();

        Pair<Double, List<Double>> bestRes = Pair.of(0d, new ArrayList<>());
        for (int i = 0; i < 1000; i++) {
            var stateStart = StateBuying.of(VariablesBuying.newSoc(SOC));
            var simRes = simulator.simulate(stateStart);
            bestRes = getBestRes(bestRes, simRes);
            Assertions.assertTrue(settings3.timeEnd() < simRes.getLeft().getVariables().time());
        }

        double powerMax = settings3.powerBattMax();
        List<Double> powerList = bestRes.getRight();
        Assertions.assertEquals(powerMax, powerList.get(0), TOL_POWER);
        Assertions.assertEquals(powerMax, powerList.get(1), TOL_POWER);
    }

    Pair<Double, List<Double>> getBestRes(Pair<Double, List<Double>> bestReturnPowerList,
                                          Triple<StateI<VariablesBuying>, List<Double>, List<Double>> simRes
    ) {
        var sumReward = ListUtils.sumList(simRes.getMiddle());
        if (sumReward > bestReturnPowerList.getLeft()) {
            log.info("Better sumReward found, simRes=" + simRes);
            bestReturnPowerList = Pair.of(sumReward, simRes.getRight());
        }
        return bestReturnPowerList;
    }

}
