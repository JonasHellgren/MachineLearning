package mcts_cart_pole;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.domains.models_battery_cell.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class TestMonteCarloControlledCartPole {
    private static final int VALUE_LEFT = 0;
    private static final int VALUE_RIGHT = 1;
    private static final int X_INIT = 0;
    private static final int THETA_INIT = 0;
    private static final int THETA_DOT_INIT = 0;
    private static final int X_DOT_INIT = 0;

    private static final int MAX_NOF_ITERATIONS = 1000;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 10.1;
    private static final int MAX_TREE_DEPTH=10;
    private static final int TIME_BUDGET_MILLI_SECONDS = 100;
    private static final int NOF_STEPS_IN_TEST = 100;

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    MonteCarloSettings<CartPoleVariables, Integer> settings;
    ActionInterface<Integer> actionTemplate;
    StateInterface<CartPoleVariables> stateUpRight;
    StateInterface<CartPoleVariables> stateLeaningRight;

    @Before
    public void init() {
        environment = new EnvironmentCartPole();
        actionTemplate=  ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
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
        stateUpRight = new StateCartPole(CartPoleVariables.builder()
                .theta(THETA_INIT)
                .thetaDot(THETA_DOT_INIT)
                .x(X_INIT)
                .xDot(X_DOT_INIT)
                .build());
        stateLeaningRight = new StateCartPole(CartPoleVariables.builder()
                .theta(EnvironmentCartPole.THETA_THRESHOLD_RADIANS*0.75)
                .thetaDot(THETA_DOT_INIT)
                .x(X_INIT)
                .xDot(X_DOT_INIT)
                .build());
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
                monteCarloTreeCreator.simulate(stateUpRight.copy());
        List<Double> avgList= new ArrayList<>(simulationResults.getReturnsForFailing());
        double averageReturn=avgList.stream().mapToDouble(val -> val).average().orElse(0.0);
        System.out.println("averageReturn = " + averageReturn);
        Assert.assertTrue(averageReturn>10);
    }

    @SneakyThrows
    @Test public void bestActionWhenLeaningRightIsForceRight() {

        monteCarloTreeCreator.setStartState(stateLeaningRight);
      //  monteCarloTreeCreator.getSettings().setMaxTreeDepth(2);
        NodeInterface<CartPoleVariables, Integer> nodeRoot =monteCarloTreeCreator.runIterations();
        TreeInfoHelper<CartPoleVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
        doPrinting(tih);
        Assert.assertTrue(tih.getValueOfFirstBestAction().orElseThrow()==VALUE_RIGHT);

    }

    @SneakyThrows
    @Test public void multipleBestActionShallGiveMultipleSteps() {
        StateInterface<CartPoleVariables> state=StateCartPole.newFromState(stateUpRight);
        monteCarloTreeCreator.setStartState(state);

        for (int i = 0; i < NOF_STEPS_IN_TEST; i++) {
            NodeInterface<CartPoleVariables, Integer> nodeRoot =monteCarloTreeCreator.runIterations();
            TreeInfoHelper<CartPoleVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
            ActionCartPole actionCartPole=ActionCartPole.builder()
                    .rawValue(tih.getValueOfFirstBestAction().orElseThrow())
                    .build();
            StepReturnGeneric<CartPoleVariables> sr=environment.step(actionCartPole,state);
            state.setFromReturn(sr);

            System.out.println("state = " + state);

            if (sr.isFail) {
                log.warning("Fail state");
                break;
            }
        }
        System.out.println("state.getVariables().nofSteps = " + state.getVariables().nofSteps);

        Assert.assertEquals(state.getVariables().nofSteps,NOF_STEPS_IN_TEST);

    }

    private void doPrinting(TreeInfoHelper<CartPoleVariables, Integer> tih) {
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());


        List<ActionInterface <Integer>> bestActions=tih.getActionsOnBestPath();
        System.out.println("bestActions = " + bestActions);
        tih.getBestPath().forEach(System.out::println);
        //nodeRoot.printTree();
    }

}


