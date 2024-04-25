package safe_rl.helpers;

import common.other.RandUtils;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.value_classes.StepReturn;

import java.util.ArrayList;
import java.util.List;

import static common.other.Conditionals.executeIfTrue;

@Builder
@Log
public class RandomActionSimulator<V> {

    SafetyLayer<V> safetyLayer;
    EnvironmentI<V> environment;
    Pair<Double, Double> minMaxAction;

    public Triple<StateI<V>, List<Double>, List<Double>> simulate(StateI<V> startState) {
        boolean isTerminalOrFail = false;
        var state = startState.copy();
        List<Double> rewardList = new ArrayList<>();
        List<Double> actionList = new ArrayList<>();

        Action action=null;
        Action actionCorrected=null;
        while (!isTerminalOrFail) {
            double power = RandUtils.getRandomDouble(minMaxAction.getLeft(), minMaxAction.getRight());
            action = Action.ofDouble(power);
            logStateAndAction(state, action);
            actionCorrected = safetyLayer.correctAction(state, action);
            var sr = environment.step(state, actionCorrected);
            log.fine("actionCorrected = " + actionCorrected);
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
        log.fine("state = " + state + ", action = " + action);
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
