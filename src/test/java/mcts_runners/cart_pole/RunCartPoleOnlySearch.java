package mcts_runners.cart_pole;

import common.plotters.PlotterMultiplePanelsTrajectory;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Arrays;

@Log
public class RunCartPoleOnlySearch {

    private static final int NOF_STEPS = 600;

    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator= createTreeCreator();
        StateInterface<CartPoleVariables> state=StateCartPole.newAllStatesAsZero();
        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(Arrays.asList("maxValue","nofNodes","maxDepth"),"Iteration");
        CartPoleRunner cpr=new CartPoleRunner(monteCarloTreeCreator,NOF_STEPS,plotter);
        cpr.run(state);

    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreator() {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(100)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(100)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(100)
                .coefficientExploitationExploration(0.1)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
*/