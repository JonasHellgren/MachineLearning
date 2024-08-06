package safe_rl.domain.simulator;

import com.beust.jcommander.internal.Lists;
import com.joptimizer.exception.JOptimizerException;
import common.other.RandUtils;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.environment.value_objects.StepReturn;

import java.util.List;

import static common.other.Conditionals.executeIfTrue;

@Builder
@Log
public class RandomActionSimulator<V> {

    SafetyLayer<V> safetyLayer;
    EnvironmentI<V> environment;
    Pair<Double, Double> minMaxAction;

    @SneakyThrows
    public Triple<StateI<V>, List<Double>, List<Double>> simulate(StateI<V> startState) throws JOptimizerException {
        boolean isTerminalOrFail = false;
        var state = startState.copy();
        List<Double> rewardList = Lists.newArrayList();
        List<Double> actionList = Lists.newArrayList();

        Action action=null;
        Action actionCorrected=null;
        while (!isTerminalOrFail) {
            double power = RandUtils.getRandomDouble(minMaxAction.getLeft(), minMaxAction.getRight());
            action = Action.ofDouble(power);
            logStateAndAction(state, action);
            actionCorrected = safetyLayer.correctAction(state, action);
            var sr = environment.step(state, actionCorrected);
            log.fine("action="+action+", actionCorrected = " + actionCorrected);
            maybeLogSr(sr);
            rewardList.add(sr.reward());
            actionList.add((actionCorrected.asDouble()));
            state.setVariables(sr.state().getVariables());
            isTerminalOrFail = sr.isTerminal() || sr.isFail();
        }
        logStateAndAction(state, action);

        return Triple.of(state, rewardList, actionList);
    }

    private  void logStateAndAction(StateI<V> state, Action action) {
        log.fine("stateNew = " + state + ", action = " + action);
    }

    private void maybeLog(StateI<V> state,
                          Action action,
                          Action actionCorrected) {
        executeIfTrue(safetyLayer.isAnyViolation(state, action), () ->
                log.fine("Non safe action - correcting, " +
                        "actionCorrected=" + actionCorrected));
    }

    void maybeLogSr(StepReturn<V> sr) {
        executeIfTrue(sr.isFail(), () -> {
            log.warning("Failing");
            log.info("sr = " + sr);
        });
    }


}
