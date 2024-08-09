package safe_rl.domain.simulator;

import com.beust.jcommander.internal.Lists;
import com.joptimizer.exception.JOptimizerException;
import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.environment.value_objects.StepReturn;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Builder
@AllArgsConstructor
@Log
public class AgentSimulator<V> {
    @NonNull AgentACDiscoI<V> agent;
    @NonNull SafetyLayer<V> safetyLayer;
    @NonNull Supplier<StateI<V>> startStateSupplier;
    @NonNull EnvironmentI<V> environment;

    public List<SimulationResult<V>> simulateWithExploration() throws JOptimizerException {
        return simulate(true);
    }

    public List<SimulationResult<V>> simulateWithNoExploration() throws JOptimizerException {
        return simulate(false);
    }

    public double criticValueInStartState() {
        return agent.readCritic(startStateSupplier.get());
    }

    public double simulationValueInStartState(int nSim) throws JOptimizerException {

        List<Double> srList=Lists.newArrayList();
        for (int i = 0; i < nSim ; i++) {
            var simresList=simulate(false);
            double sumReward=simresList.stream().mapToDouble(sr -> sr.reward()).sum();
            srList.add(sumReward);
        }
        return ListUtils.findAverage(srList).orElseThrow();
    }

    List<SimulationResult<V>> simulate(boolean exploration) throws JOptimizerException {
        boolean isTerminalOrFail = false;
        var state = startStateSupplier.get().copy();
        List<SimulationResult<V>> simulationResults = Lists.newArrayList();

        StepReturn<V> sr=null;
        while (!isTerminalOrFail) {
            var action = exploration
                    ? agent.chooseAction(state)
                    : agent.chooseActionNoExploration(state);
            var state0 = state.copy();
            var actionCorrected = safetyLayer.correctAction(state, action);
            log.fine("stateNew = " + state + ", action = " + action+ ", actionCorr = " + actionCorrected);

            sr = environment.step(state, actionCorrected);
            state.setVariables(sr.state().getVariables());

            simulationResults.add(
                    new SimulationResult<>(state0, sr.reward(), action, actionCorrected));
            isTerminalOrFail = sr.isTerminal() || sr.isFail();

        }
        simulationResults.add(
                new SimulationResult<>(sr.state(), sr.reward(), Action.ofDouble(0d),Action.ofDouble(0d)));
        return simulationResults;
    }


}
