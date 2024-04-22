package safe_rl.helpers;

import common.other.RandUtils;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import safe_rl.agent_interfaces.AgentI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.SafetyLayerBuying;
import safe_rl.environments.buying_electricity.StepReturn;

import java.util.ArrayList;
import java.util.List;

import static common.other.Conditionals.executeIfTrue;

@Builder
@Log
public class AgentActionSimulator<V> {

    AgentI<V> agent;
    SafetyLayerBuying<V> safetyLayer;
    BuySettings settings;
    EnvironmentI<V> environment;


    public List<SimulationResult<V>> simulate(StateI<V> startState) {
        boolean isTerminalOrFail = false;
        var state = startState.copy();
        agent.setState(state);
        List<SimulationResult<V>> simulationResults=new ArrayList<>();

        while (!isTerminalOrFail) {
            var action=agent.chooseAction();
            var state0=state.copy();
            log.info("state = " + state + ", action = " + action);
            var actionCorrected = safetyLayer.correctAction(state, action);
            var sr = environment.step(state, actionCorrected);
            maybeLog(state, action, actionCorrected, sr);
            state.setVariables(sr.state().getVariables());
            isTerminalOrFail = sr.isTerminal() || sr.isFail();
            agent.setState(state);
            simulationResults.add(new SimulationResult<>(state0, sr.reward(), actionCorrected));
        }
        return simulationResults;
    }

    private void maybeLog(StateI<V> state,
                          Action action,
                          Action actionCorrected,
                          StepReturn<V> sr) {
        executeIfTrue(safetyLayer.isAnyViolation(state, action), () ->
                log.fine("Non safe action - correcting, " +
                        "actionCorrected=" + actionCorrected));
        executeIfTrue(sr.isFail(), () -> {
            log.warning("Failing");
            log.info("sr = " + sr);
        });
    }


}
