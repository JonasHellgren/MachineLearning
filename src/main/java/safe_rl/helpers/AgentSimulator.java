package safe_rl.helpers;

import com.joptimizer.exception.JOptimizerException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.value_classes.StepReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Builder
@AllArgsConstructor
@Log
public class AgentSimulator<V> {
    @NonNull AgentI<V> agent;
    @NonNull SafetyLayer<V> safetyLayer;
    @NonNull Supplier<StateI<V>> startStateSupplier;
    @NonNull EnvironmentI<V> environment;

    public List<SimulationResult<V>> simulateWithExploration() throws JOptimizerException {
        return simulate(true);
    }

    public List<SimulationResult<V>> simulateWithNoExploration() throws JOptimizerException {
        return simulate(false);
    }

    List<SimulationResult<V>> simulate(boolean exploration) throws JOptimizerException {
        boolean isTerminalOrFail = false;
        var state = startStateSupplier.get().copy();
        List<SimulationResult<V>> simulationResults = new ArrayList<>();

        StepReturn<V> sr=null;
        while (!isTerminalOrFail) {
            var action = exploration
                    ? agent.chooseAction(state)
                    : agent.chooseActionNoExploration(state);
            var state0 = state.copy();
            log.fine("state = " + state + ", action = " + action);
            var actionCorrected = safetyLayer.correctAction(state, action);
            sr = environment.step(state, actionCorrected);
            state.setVariables(sr.state().getVariables());
            isTerminalOrFail = sr.isTerminal() || sr.isFail();
            simulationResults.add(
                    new SimulationResult<>(state0, sr.reward(), actionCorrected));
        }
        simulationResults.add(
                new SimulationResult<>(sr.state(), sr.reward(), Action.ofDouble(0d)));
        return simulationResults;
    }

}
