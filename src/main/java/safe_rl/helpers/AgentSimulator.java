package safe_rl.helpers;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.SafetyLayerBuying;

import java.util.ArrayList;
import java.util.List;

@Builder
@Log
public class AgentSimulator<V> {
    @NonNull AgentI<V> agent;
    @NonNull SafetyLayerBuying<V> safetyLayer;
    @NonNull BuySettings settings;
    @NonNull EnvironmentI<V> environment;

    public List<SimulationResult<V>> simulate(StateI<V> startState) {
        boolean isTerminalOrFail = false;
        var state = startState.copy();
      //  agent.setState(state);
        List<SimulationResult<V>> simulationResults=new ArrayList<>();

        while (!isTerminalOrFail) {
            var action=agent.chooseAction(state);
            var state0=state.copy();
            log.fine("state = " + state + ", action = " + action);
            var actionCorrected = safetyLayer.correctAction(state, action);
            var sr = environment.step(state, actionCorrected);
            state.setVariables(sr.state().getVariables());
            isTerminalOrFail = sr.isTerminal() || sr.isFail();
        //    agent.setState(state);
            simulationResults.add(
                    new SimulationResult<>(state0, sr.reward(), actionCorrected));
        }
        return simulationResults;
    }

}
