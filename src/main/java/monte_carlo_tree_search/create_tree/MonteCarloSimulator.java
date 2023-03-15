package monte_carlo_tree_search.create_tree;

import common.ListUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Log
public class MonteCarloSimulator<S, A> {

    EnvironmentGenericInterface<S, A> environment;
    MonteCarloSettings<S, A> settings;

    public MonteCarloSimulator(EnvironmentGenericInterface<S, A> environment, MonteCarloSettings<S, A> settings) {
        this.environment = environment;
        this.settings = settings;
    }

    public SimulationResults simulate(StateInterface<S> stateAfterApplyingActionInSelectedNode) {
        return simulate(stateAfterApplyingActionInSelectedNode,false, 0);
    }

    public SimulationResults simulate(StateInterface<S> stateAfterApplyingActionInSelectedNode,
                                      boolean isTerminal,
                                      int startDepth) {

        if (isTerminal) {
            return SimulationResults.newEmpty();
        }

        SimulationResults simulationResults = SimulationResults.newEmpty();

        for (int i = 0; i < settings.nofSimulationsPerNode; i++) {
            List<StepReturnGeneric<S>> stepResults =
                    stepToTerminal(stateAfterApplyingActionInSelectedNode.copy(), startDepth);
            StepReturnGeneric<S> endReturn = stepResults.get(stepResults.size() - 1);
            double sumOfRewards = ListUtils.discountedSum(
                    stepResults.stream().map(r -> r.reward).collect(Collectors.toList()),
                    settings.discountFactorSimulation);
            boolean isEndingInFail = endReturn.isFail;
            simulationResults.add(sumOfRewards, isEndingInFail);
        }
        return simulationResults;
    }

    public List<StepReturnGeneric<S>> stepToTerminal(StateInterface<S> state,
                                                      int startDepth) {
        List<StepReturnGeneric<S>> returns = new ArrayList<>();
        SimulationPolicyInterface<S, A> policy = settings.simulationPolicy;
        StepReturnGeneric<S> stepReturn;
        int depth = startDepth;
        do {
            ActionInterface<A> action = policy.chooseAction(state);
            stepReturn = environment.step(action, state);
            state.setFromReturn(stepReturn);
            returns.add(stepReturn);
            depth++;
        } while (!stepReturn.isTerminal && depth < settings.maxSimulationDepth);
        return returns;
    }


    public List<Double> stepWithActions(StateInterface<S> state,List<ActionInterface<A>> actions) {

        List<Double> rewards=new ArrayList<>();
        for (ActionInterface<A> action: actions) {
            StepReturnGeneric<S> stepReturn = environment.step(action, state);
            rewards.add(stepReturn.reward);

            if (stepReturn.isFail) {
                log.warning("Stepped into fail state");
                break;
            }

            if (stepReturn.isTerminal) {
                log.info("Stepped into terminal state");
                break;
            }

            state.setFromReturn(stepReturn);
        }
        return rewards;
    }

}
