package mcts_cart_pole_runner;

import common.MultiplePanelsPlotter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.lang.reflect.Array;
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

        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Arrays.asList("Value","Not used"),"Step");
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
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(10)
               // .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSimulation(1)
                .weightReturnsSteps(0)
                .weightMemoryValue(0)
                .nofSimulationsPerNode(10)
                .discountFactorSimulationNormal(DISCOUNT_FACTOR)
                .discountFactorSimulation(DISCOUNT_FACTOR)
                .discountFactorSimulationDefensive(0.1)
                .coefficientExploitationExploration(0.1)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
