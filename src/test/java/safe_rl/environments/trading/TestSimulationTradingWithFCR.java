package safe_rl.environments.trading;

import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimulationTradingWithFCR {
    public static final int N_SIMULATIONS = 10;
    public static final double POWER = 0d;
    EnvironmentTrading environment;
    public static final double SOC = 0.5;
    public static final StateTrading START_STATE = StateTrading.of(VariablesTrading.newSoc(SOC));
    SettingsTrading settings;
    SafetyLayer<VariablesTrading> safetyLayer;
    RandomActionSimulator<VariablesTrading> simulator;



    @SneakyThrows
    @Test
    void whenFCR_thenHigherRevenueAndSoCEndDeviation() {
        init(0, 1);
        var noFCR = getRevSocAvgPair();
        init(1, 1);
        var FCR = getRevSocAvgPair();
        Assertions.assertTrue(noFCR.getLeft()<FCR.getLeft());
        Assertions.assertTrue(noFCR.getRight()<FCR.getRight());
    }

    @SneakyThrows
    @Test
    void whenFCRAndLowFCRPrice_thenLowerRevenueAndHigherSoCEndDeviation() {
        init(0, 1);
        var noFCR = getRevSocAvgPair();
        init(1, 1e-3);
        var FCR = getRevSocAvgPair();
        Assertions.assertTrue(noFCR.getLeft()>FCR.getLeft());
        Assertions.assertTrue(noFCR.getRight()<FCR.getRight());
    }

    @Test
    void whenToHighFCR_thenException() {
        init(10, 1);
        assertThrows(JOptimizerException.class,() -> getRevSocAvgPair());
    }

    @NotNull
    private Pair<Double, Double> getRevSocAvgPair() throws JOptimizerException {
        List<Double> revList=new ArrayList<>();
        List<Double> socList=new ArrayList<>();
        for (int i = 0; i < N_SIMULATIONS; i++) {
            Triple<StateI<VariablesTrading>, List<Double>, List<Double>> simRes = simulator.simulate(START_STATE.copy());
            List<Double> rewards=simRes.getMiddle();
            revList.add(ListUtils.sumList(rewards));
            socList.add(simRes.getLeft().getVariables().soc());
        }

        var ds= new DescriptiveStatistics();
        socList.forEach(v -> ds.addValue(v));
        return Pair.of(ListUtils.findAverage(revList).orElseThrow(),ds.getStandardDeviation());
    }

    void init(double powerCapacityFcr, double priceFCR) {
        settings = SettingsTradingFactory.new5HoursIncreasingPrice()
                //.withPowerCapacityFcr(powerCapacityFcr)
                .withPowerCapacityFcrRange(Range.closed(0d,powerCapacityFcr))
                .withSocStart(SOC).withSocDelta(0.0)
                //.withSocTerminalMin(SOC)
                .withPriceBattery(5e3)
        .withCapacityPriceTraj(ArrayUtil.createArrayWithSameDoubleNumber(5,priceFCR));
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings));
        environment = new EnvironmentTrading(settings);
        simulator = RandomActionSimulator.<VariablesTrading>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(POWER,POWER)).build();
    }

}
