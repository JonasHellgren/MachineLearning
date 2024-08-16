package safe_rl.environments.buying;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.domain.simulator.RandomActionSimulator;

import static common.other.RandUtilsML.randomNumberBetweenZeroAndOne;

@Log
class TestSafetyLayerBuying {
    SafetyLayer<VariablesBuying> safetyLayer;
    SettingsBuying settings;
    EnvironmentI<VariablesBuying> environment;

    @BeforeEach
    void init() {
        settings = SettingsBuying.new5HoursIncreasingPrice();
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings));
        environment = new EnvironmentBuying(settings);
    }

    @Test
    @SneakyThrows
    void whenManySimulations_thenAllSucceeds() {
        double soc = randomNumberBetweenZeroAndOne();
        var simulator = RandomActionSimulator.<VariablesBuying>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(-5d, -5d)).build();

        for (int i = 0; i < 100 ; i++) {
            var stateStart = StateBuying.of(VariablesBuying.newSoc(soc));
            var simRes = simulator.simulate(stateStart.copy());
            log.fine("Simulation finished, simRes=" + simRes);
            Assertions.assertTrue(settings.timeEnd() < simRes.getLeft().getVariables().time());
        };
    }


}
