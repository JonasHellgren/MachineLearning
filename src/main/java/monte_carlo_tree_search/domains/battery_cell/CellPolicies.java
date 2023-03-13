package monte_carlo_tree_search.domains.battery_cell;

import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;

public class CellPolicies {

    public static SimulationPolicyInterface<CellVariables, Integer> newEqualProbability(
            ActionInterface<Integer> actionTemplate) {
        return new EqualLevelProbabilityPolicy(actionTemplate);
    }

    public static SimulationPolicyInterface<CellVariables, Integer> newRandomFeasible(
            ActionInterface<Integer> actionTemplate,
            EnvironmentGenericInterface<CellVariables, Integer> environment) {
        return new RandomFeasiblePolicy(actionTemplate,environment);
    }

    public static SimulationPolicyInterface<CellVariables, Integer> newBestFeasible(
            ActionInterface<Integer> actionTemplate,
            EnvironmentGenericInterface<CellVariables, Integer> environment) {
        return new BestFeasiblePolicy(actionTemplate,environment);
    }

}
