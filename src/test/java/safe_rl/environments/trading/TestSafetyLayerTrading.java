package safe_rl.environments.trading;

import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.RandomActionSimulator;

import java.util.stream.IntStream;

import static common.other.RandUtils.randomNumberBetweenZeroAndOne;

@Log
class TestSafetyLayerTrading {
    public static final int N_SIMULATIONS = 1_000;
    public static final double TOL_SOC = 0.05;
    SafetyLayer<VariablesTrading> safetyLayer;
    SettingsTrading settings;
    EnvironmentI<VariablesTrading> environment;

    @BeforeEach
    void init() {
        settings = SettingsTrading.new5HoursIncreasingPrice().withSocTerminalMin(0.8d);
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings));
        environment = new EnvironmentTrading(settings);
    }

    @Test
    void whenManySimulations_thenAllSucceeds() {
        var simulator = RandomActionSimulator.<VariablesTrading>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(-5d, 5d)).build();

        IntStream.range(0, N_SIMULATIONS).forEach((i) -> {
            double soc = randomNumberBetweenZeroAndOne();
            var stateStart = StateTrading.of(VariablesTrading.newSoc(soc));
            System.out.println("stateStart = " + stateStart);
            var simRes = simulator.simulate(stateStart.copy());
            log.info("Simulation finished, simRes=" + simRes);
            Assertions.assertTrue(settings.timeEnd() < simRes.getLeft().getVariables().time());
            Assertions.assertTrue(simRes.getLeft().getVariables().soc()>settings.socTerminalMin()- TOL_SOC);
        });
    }


}