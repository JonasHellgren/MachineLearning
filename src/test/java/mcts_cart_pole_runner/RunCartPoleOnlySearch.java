package mcts_cart_pole_runner;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.swing.CartPoleGraphics;
import org.jcodec.common.Assert;

@Log
public class RunCartPoleOnlySearch {

    private static final int NOF_STEPS_IN_TEST = 600;

    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator= createTreeCreator();
        CartPoleGraphics graphics=new CartPoleGraphics();
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited =
                EnvironmentCartPole.builder().maxNofSteps(Integer.MAX_VALUE).build();
        StateInterface<CartPoleVariables> state=StateCartPole.newAllStatesAsZero();

        for (int i = 0; i < NOF_STEPS_IN_TEST; i++) {
            state.getVariables().nofSteps=0;  //reset nof steps
            monteCarloTreeCreator.setStartState(state);
            monteCarloTreeCreator.run();
            ActionInterface<Integer> actionCartPole=monteCarloTreeCreator.getFirstAction();
            StepReturnGeneric<CartPoleVariables> sr=environmentNotStepLimited.step(actionCartPole,state);
            state.setFromReturn(sr);
            graphics.render(state,i,0,actionCartPole.getValue());

            System.out.println("i = "+i+", state = " + state);
            if (sr.isFail) {
                log.warning("Fail state");
                break;
            }
        }
        System.out.println("state.getVariables().nofSteps = " + state.getVariables().nofSteps);

        Assert.assertEquals(state.getVariables().nofSteps,NOF_STEPS_IN_TEST-1);

    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreator() {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();
        final int VALUE_LEFT = 0;
        final int MAX_NOF_ITERATIONS = 10_000;
        final int NOF_SIMULATIONS_PER_NODE = 100;
        final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.1;
        final int MAX_TREE_DEPTH=100;
        final int TIME_BUDGET_MILLI_SECONDS = 100;
        ActionInterface<Integer> actionTemplate=  ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }



}
