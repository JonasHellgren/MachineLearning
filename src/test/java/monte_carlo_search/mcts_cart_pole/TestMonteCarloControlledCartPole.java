package monte_carlo_search.mcts_cart_pole;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.create_tree.NodeSelector;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
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
        MonteCarloSimulator<CartPoleVariables, Integer> simulator=new MonteCarloSimulator<>(
                environment,settings);
        SimulationResults simulationResults=
                simulator.simulate(stateUpRight.copy(), false, START_DEPTH);
        List<Double> avgList= new ArrayList<>(simulationResults.getReturnsForFailing());
        double averageReturn=avgList.stream().mapToDouble(val -> val).average().orElse(0.0);
        System.out.println("averageReturn = " + averageReturn);
        Assert.assertTrue(averageReturn>10);
    }

    @SneakyThrows
    @Test public void bestActionWhenLeaningRightIsForceRight() {

        monteCarloTreeCreator.setStartState(stateLeaningRight);
        NodeWithChildrenInterface<CartPoleVariables, Integer> nodeRoot =monteCarloTreeCreator.run();

        NodeSelector<CartPoleVariables, Integer> ns = new NodeSelector<>(nodeRoot, settings);
        Optional<NodeInterface<CartPoleVariables, Integer>> bestChild= ns.selectBestNonFailChild(nodeRoot);
        Assert.assertTrue(bestChild.orElseThrow().getAction().getValue()==VALUE_RIGHT);

        TreeInfoHelper<CartPoleVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
        doPrinting(tih);

    }

    private void doPrinting(TreeInfoHelper<CartPoleVariables, Integer> tih) {
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());


        List<ActionInterface <Integer>> bestActions=tih.getActionsOnBestPath();
        System.out.println("bestActions = " + bestActions);
        tih.getBestPath().forEach(System.out::println);
    }

}


