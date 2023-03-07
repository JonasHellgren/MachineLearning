package mcts_cart_pole;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

@Log
public class TestMonteCarloControlledCartPole {
    private static final int VALUE_LEFT = 0;
    private static final int VALUE_RIGHT = 1;
    private static final double X_INIT = EnvironmentCartPole.X_TRESHOLD*0.75;
    private static final int THETA_INIT = 0;
    private static final int THETA_DOT_INIT = 0;
    private static final int X_DOT_INIT = 0;

    private static final int MAX_NOF_ITERATIONS = 10_000;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.1;
    private static final int MAX_TREE_DEPTH=100;
    private static final int TIME_BUDGET_MILLI_SECONDS = 100;
    private static final int START_DEPTH = 0;

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    MonteCarloSettings<CartPoleVariables, Integer> settings;
    StateInterface<CartPoleVariables> stateUpRight;
    StateInterface<CartPoleVariables> stateLeaningRight;

    @Before
    public void init() {
        environment = EnvironmentCartPole.newDefault();
        ActionInterface<Integer>  actionTemplate=  ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
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
        stateUpRight = new StateCartPole(CartPoleVariables.builder()
                .theta(THETA_INIT)
                .thetaDot(THETA_DOT_INIT)
                .x(X_INIT)
                .xDot(X_DOT_INIT)
                .build());
        stateLeaningRight = stateUpRight.copy();
        stateLeaningRight.getVariables().theta=EnvironmentCartPole.THETA_THRESHOLD_RADIANS*0.75;
        monteCarloTreeCreator=MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(stateUpRight)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @Test
    public void simulateFromUpRightShallInAverageGiveMoreThan10Steps() {
        SimulationResults simulationResults=
                monteCarloTreeCreator.simulate(stateUpRight.copy(), START_DEPTH);
        List<Double> avgList= new ArrayList<>(simulationResults.getReturnsForFailing());
        double averageReturn=avgList.stream().mapToDouble(val -> val).average().orElse(0.0);
        System.out.println("averageReturn = " + averageReturn);
        Assert.assertTrue(averageReturn>10);
    }

    @SneakyThrows
    @Test public void bestActionWhenLeaningRightIsForceRight() {

        monteCarloTreeCreator.setStartState(stateLeaningRight);
        NodeWithChildrenInterface<CartPoleVariables, Integer> nodeRoot =monteCarloTreeCreator.run();
        TreeInfoHelper<CartPoleVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
        doPrinting(tih);
        Assert.assertTrue(tih.getValueOfFirstBestAction().orElseThrow()==VALUE_RIGHT);

    }

    private void doPrinting(TreeInfoHelper<CartPoleVariables, Integer> tih) {
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());


        List<ActionInterface <Integer>> bestActions=tih.getActionsOnBestPath();
        System.out.println("bestActions = " + bestActions);
        tih.getBestPath().forEach(System.out::println);
    }

}


