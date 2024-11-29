package mcts_runners.cart_pole;

import common.plotters.PlotterMultiplePanelsTrajectory;
import lombok.SneakyThrows;

/**
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Arrays;

public class RunCartPoleOnlySearchMinusOneRewardAtFail {
    private static final int NOF_STEPS = 6000;
    private static final int FAIL_REWARD = -1;
    private static final int NON_FAIL_REWARD = 0;
    private static final double DISCOUNT_FACTOR = 0.95;  //0.95

    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator= createTreeCreator();
        StateInterface<CartPoleVariables> state= StateCartPole.newAllStatesAsZero();
        state.getVariables().x=EnvironmentCartPole.X_TRESHOLD*0.75;
        state.getVariables().xDot=EnvironmentCartPole.X_DOT_THRESHOLD*0.2;

        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(
                Arrays.asList("root value","nofNodes","maxDepth"),"Iteration");
        CartPoleRunner cpr=new CartPoleRunner(monteCarloTreeCreator,NOF_STEPS,plotter);
        cpr.run(state);

    }


    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreator() {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.builder()
                .nonFailReward(NON_FAIL_REWARD)
                .failReward(FAIL_REWARD)
                .build();
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(10)
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSimulation(1)
                .weightReturnsSteps(0)
                .weightMemoryValue(0)
                .nofSimulationsPerNode(10)
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR)
                .discountFactorSimulation(DISCOUNT_FACTOR)
                .discountFactorBackupSimulationDefensive(0.1)
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