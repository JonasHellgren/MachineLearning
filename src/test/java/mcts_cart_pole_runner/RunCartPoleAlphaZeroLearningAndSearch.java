package mcts_cart_pole_runner;
import common.MultiplePanelsPlotter;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;

import java.util.Arrays;

public class RunCartPoleAlphaZeroLearningAndSearch {
    private static final int MAX_NOF_STEPS_IN_EVALUATION = Integer.MAX_VALUE;
    private static final int TIME_BUDGET_MILLI_SECONDS_SEARCH = 20;  //small => faster search
    private static final int MAX_TREE_DEPTH_SEARCH = 10;  //small => faster search
    private static final String FILE = "networks/cartPoleStateValue.nnet";

    public static void main(String[] args) {
        CartPoleStateValueMemory<CartPoleVariables> memory = new CartPoleStateValueMemory<>();
        memory.load(FILE);
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch = createTreeCreatorForSearch(memory);

        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Arrays.asList("maxValue","nofNodes","maxDepth"),"Iteration");
        CartPoleRunner cpr = new CartPoleRunner(mcForSearch, memory, MAX_NOF_STEPS_IN_EVALUATION,plotter);
        StateInterface<CartPoleVariables> state = StateCartPole.newAllStatesAsZero();
        cpr.run(state);

    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            CartPoleStateValueMemory<CartPoleVariables> memory) {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();

        ActionInterface<Integer> actionTemplate = ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings = MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(MAX_TREE_DEPTH_SEARCH)
                .alphaBackupNormal(0.9)
                .alphaBackupDefensiveStep(0.1)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS_SEARCH)
                .weightReturnsSteps(0)
                .weightMemoryValue(1.0)
                .weightReturnsSimulation(1.0)
                .nofSimulationsPerNode(100)
                .coefficientExploitationExploration(0.1)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .memory(memory)
                .build();
    }

}
