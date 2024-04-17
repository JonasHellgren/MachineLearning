package safe_rl.domain;

import common.other.RandUtils;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.environments.buying_electricity.*;
import java.util.stream.IntStream;
import static common.other.Conditionals.executeIfTrue;
import static common.other.RandUtils.randomNumberBetweenZeroAndOne;

@Log
class TestSafetyLayer {
    SafetyLayerBuying<VariablesBuying> safetyLayer;
    BuySettings settings;
    EnvironmentBuying environment;

    @BeforeEach
    void init() {
        settings = BuySettings.new5HoursIncreasingPrice();
        safetyLayer = new SafetyLayerBuying<>(settings);
        environment = new EnvironmentBuying(settings);
    }

    @Test
    void whenManySimulations_thenAllSucceeds() {
        IntStream.range(0, 300).forEach((i) -> {
            StateBuying state = simulate();
            log.fine("Simulation finished, state=" + state);
            Assertions.assertTrue(settings.timeEnd() < state.getVariables().time());
        });
    }

    private StateBuying simulate() {
        double soc = randomNumberBetweenZeroAndOne();
        var state = StateBuying.of(VariablesBuying.newSoc(soc));
        boolean isTerminalOrFail = false;
        while (!isTerminalOrFail) {
            double power = RandUtils.getRandomDouble(-5, 5);
            var action = Action.ofDouble(power);
            log.fine("state = " + state + ", action = " + action);
            var actionCorrected = safetyLayer.correctAction(state, action);
            var sr = environment.step(state, actionCorrected);
            maybeLog(state, action, actionCorrected, sr);
            state.setVariables(sr.state().getVariables());
            isTerminalOrFail = sr.isTerminal() || sr.isFail();
        }
        return state;
    }

    private void maybeLog(StateBuying state,
                          Action action,
                          Action actionCorrected,
                          StepReturn<VariablesBuying> sr) {
        executeIfTrue(safetyLayer.isAnyViolation(state, action), () ->
                log.fine("Non safe action - correcting, " +
                        "actionCorrected=" + actionCorrected));
        executeIfTrue(sr.isFail(), () -> {
            log.warning("Failing");
            log.info("sr = " + sr);
        });
    }


}
