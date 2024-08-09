package safe_rl.environments.trading;

import com.google.common.collect.Range;
import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.domain.simulator.RandomActionSimulator;

import java.util.ArrayList;
import java.util.List;

@Log
public class TestEnvironmentTrading5hRandomSearch {
    public static final int N_SIMULATIONS = 1000;
    EnvironmentTrading environment;
    public static final double SOC = 0.5;
    SettingsTrading settings;
    SafetyLayer<VariablesTrading> safetyLayer;

    @BeforeEach
    void init() {
        settings = SettingsTradingFactory.new5HoursIncreasingPrice()
                .withPowerCapacityFcrRange(Range.closed(1d,1d))
                //.withSocTerminalMin(SOC)
                .withSocStart(SOC).withSocDelta(0)
                .withPriceBattery(5e3);
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings));
        environment = new EnvironmentTrading(settings);
    }


    @Test
    @SneakyThrows
    void whenRandomSearch_thenSomePositiveRevenue() {
        var simulator = RandomActionSimulator.<VariablesTrading>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(
                        settings.powerChargeMin(),settings.powerChargeMax()))
                .build();

        Pair<Double, List<Double>> bestRes = Pair.of(-Double.MAX_VALUE, new ArrayList<>());
        for (int i = 0; i < N_SIMULATIONS; i++) {
            var stateStart = StateTrading.of(VariablesTrading.newSoc(SOC));
            var simRes = simulator.simulate(stateStart);
            bestRes = getBestRes(bestRes, simRes);
            Assertions.assertTrue(settings.timeEnd() < simRes.getLeft().getVariables().time());
        }

        List<Double> powerList = bestRes.getRight();
        System.out.println("powerList = " + powerList);
        Assertions.assertTrue(powerList.get(0) > 0);  //buy in start
        Assertions.assertTrue(bestRes.getLeft() > 0);  //making revenue
    }

    Pair<Double, List<Double>> getBestRes(Pair<Double, List<Double>> bestReturnPowerList,
                                          Triple<StateI<VariablesTrading>, List<Double>, List<Double>> simRes
    ) {
        var sumReward = ListUtils.sumList(simRes.getMiddle());
        if (sumReward > bestReturnPowerList.getLeft()) {
            log.info("Better sumReward found, sumReward=" + sumReward + ", simRes=" + simRes);
            bestReturnPowerList = Pair.of(sumReward, simRes.getRight());
        }
        return bestReturnPowerList;
    }


}
