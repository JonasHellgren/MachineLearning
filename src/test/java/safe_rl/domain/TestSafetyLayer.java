package safe_rl.domain;

import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.helpers.RandomActionSimulator;

import java.util.stream.IntStream;

import static common.other.RandUtils.randomNumberBetweenZeroAndOne;

@Log
class TestSafetyLayer {
    SafetyLayer<VariablesBuying> safetyLayer;
    BuySettings settings;
    EnvironmentI<VariablesBuying> environment;

    @BeforeEach
    void init() {
        settings = BuySettings.new5HoursIncreasingPrice();
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings));
        environment = new EnvironmentBuying(settings);
    }

    @Test
    void whenManySimulations_thenAllSucceeds() {
        double soc = randomNumberBetweenZeroAndOne();
        var simulator = RandomActionSimulator.<VariablesBuying>builder()
                .environment(environment).safetyLayer(safetyLayer)
                .minMaxAction(Pair.of(-5d, -5d)).build();

        IntStream.range(0, 300).forEach((i) -> {
            var stateStart = StateBuying.of(VariablesBuying.newSoc(soc));
            var simRes = simulator.simulate(stateStart.copy());
            log.fine("Simulation finished, simRes=" + simRes);
            Assertions.assertTrue(settings.timeEnd() < simRes.getLeft().getVariables().time());
        });
    }


}
