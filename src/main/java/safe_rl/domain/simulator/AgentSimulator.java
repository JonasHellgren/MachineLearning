package safe_rl.domain.simulator;

import com.beust.jcommander.internal.Lists;
import com.joptimizer.exception.JOptimizerException;
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

@Builder
@AllArgsConstructor
@Log
public class AgentSimulator<V> {
    @NonNull AgentACDiscoI<V> agent;
    @NonNull SafetyLayer<V> safetyLayer;
    @NonNull Supplier<StateI<V>> startStateSupplier;
    @NonNull EnvironmentI<V> environment;

    public List<SimulationResult<V>> simulateWithExploration() throws JOptimizerException {
        return simulate(true, false);
    }

    public List<SimulationResult<V>> simulateWithNoExplorationEndStateNotEnded() throws JOptimizerException {
        return simulate(false, false);
    }

    public List<SimulationResult<V>> simulateWithNoExplorationEndStateEnded() throws JOptimizerException {
        return simulate(false, true);
    }

    public List<SimulationResult<V>> simulateWithExplorationEndStateEnded() throws JOptimizerException {
        return simulate(true, true);
    }

    public double criticValueInStartState() {
        return agent.readCritic(startStateSupplier.get());
    }

    public double sumRewardsFromSimulations(int nSim) throws JOptimizerException {

        List<Double> srList = Lists.newArrayList();
        for (int i = 0; i < nSim; i++) {
            List<SimulationResult<V>> simresList = simulateWithNoExplorationEndStateNotEnded();
            double sumReward = simresList.stream().mapToDouble(sr -> sr.reward()).sum();
            srList.add(sumReward);
        }
        return ListUtils.findAverage(srList).orElseThrow();
    }

    public StateI<V> endStateFromSingleSimulation() throws JOptimizerException {
        List<SimulationResult<V>> simresList = simulateWithNoExplorationEndStateEnded();
        return ListUtils.findEnd(simresList).orElseThrow().state();
    }


    List<SimulationResult<V>> simulate(boolean exploration, boolean isAddEndState) throws JOptimizerException {
        boolean isTerminalOrFail = false;
        var state = startStateSupplier.get().copy();
        List<SimulationResult<V>> simulationResults = Lists.newArrayList();

        StepReturn<V> sr = null;
        while (!isTerminalOrFail) {
            var action = exploration
                    ? agent.chooseAction(state)
                    : agent.chooseActionNoExploration(state);
            var state0 = state.copy();
            var actionCorrected = safetyLayer.correctAction(state, action);
            log.fine("stateNew = " + state + ", action = " + action + ", actionCorr = " + actionCorrected);

            sr = environment.step(state, actionCorrected);
            state.setVariables(sr.state().getVariables());

            simulationResults.add(
                    new SimulationResult<>(state0, sr.reward(), action, actionCorrected));
            isTerminalOrFail = sr.isTerminal() || sr.isFail();

        }
        //the state after the very last step is used by method endStateFromSingleSimulation()
        if (isAddEndState) {
            simulationResults.add(
                    new SimulationResult<>(sr.state(), 0d, Action.ofDouble(0d), Action.ofDouble(0d)));
        }
        return simulationResults;
    }


}
