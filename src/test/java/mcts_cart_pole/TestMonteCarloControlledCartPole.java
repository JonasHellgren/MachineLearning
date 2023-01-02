package mcts_cart_pole;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.swing.PanelCartPoleAnimation;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import swing.FrameEnvironment;
import swing.ScaleLinear;

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
    private static final int NOF_STEPS_IN_TEST = 600;
    private static final int FRAME_WEIGHT = 600;
    private static final int FRAME_HEIGHT = 300;
    private static final int FRAME_MARGIN = 50;

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    MonteCarloSettings<CartPoleVariables, Integer> settings;
    ActionInterface<Integer> actionTemplate;
    StateInterface<CartPoleVariables> stateUpRight;
    StateInterface<CartPoleVariables> stateLeaningRight;
    PanelCartPoleAnimation animationPanel;

    @Before
    public void init() {
        environment = EnvironmentCartPole.newDefault();
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
        setupFrameAndPanel();
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
        NodeInterface<CartPoleVariables, Integer> nodeRoot =monteCarloTreeCreator.run();
        TreeInfoHelper<CartPoleVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
        doPrinting(tih);
        Assert.assertTrue(tih.getValueOfFirstBestAction().orElseThrow()==VALUE_RIGHT);

    }

    @SneakyThrows
    @Test public void multipleBestActionShallGiveMultipleSteps() {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited =
                EnvironmentCartPole.builder().maxNofSteps(Integer.MAX_VALUE).build();
        StateInterface<CartPoleVariables> state=StateCartPole.newFromState(stateUpRight);


        for (int i = 0; i < NOF_STEPS_IN_TEST; i++) {
            state.getVariables().nofSteps=0;  //reset nof steps
            monteCarloTreeCreator.setStartState(state);
            monteCarloTreeCreator.run();
            ActionInterface<Integer> actionCartPole=monteCarloTreeCreator.getFirstAction();
            StepReturnGeneric<CartPoleVariables> sr=environmentNotStepLimited.step(actionCartPole,state);
            state.setFromReturn(sr);

            state.getVariables().nofSteps=i;  //set for rendering
            render(state,0,actionCartPole.getValue());

            System.out.println("i = "+i+", state = " + state);

            if (sr.isFail) {
                log.warning("Fail state");
                break;
            }
        }
        System.out.println("state.getVariables().nofSteps = " + state.getVariables().nofSteps);

        Assert.assertEquals(state.getVariables().nofSteps,NOF_STEPS_IN_TEST-1);

    }

    private void doPrinting(TreeInfoHelper<CartPoleVariables, Integer> tih) {
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());


        List<ActionInterface <Integer>> bestActions=tih.getActionsOnBestPath();
        System.out.println("bestActions = " + bestActions);
        tih.getBestPath().forEach(System.out::println);
    }


    private void setupFrameAndPanel() {
        FrameEnvironment animationFrame =new FrameEnvironment(FRAME_WEIGHT, FRAME_HEIGHT,"CartPole animation");
        double xMax=EnvironmentCartPole.X_TRESHOLD;
        double yMin=EnvironmentCartPole.Y_MIN;
        double yMax=EnvironmentCartPole.Y_MAX;
        ScaleLinear xScaler=new ScaleLinear(-xMax,xMax,
                FRAME_MARGIN, FRAME_WEIGHT - FRAME_MARGIN,
                false, FRAME_MARGIN);
        ScaleLinear yScaler=new ScaleLinear(yMin,yMax, FRAME_MARGIN,
                FRAME_HEIGHT - FRAME_MARGIN,true, FRAME_MARGIN);

        animationPanel =new PanelCartPoleAnimation(xScaler,yScaler);
        animationPanel.setLayout(null);  //to enable tailor made position
        animationFrame.add(animationPanel);
        animationFrame.setVisible(true);
    }

    public void render(StateInterface<CartPoleVariables> state, double maxQ, int actionValue) {
        animationPanel.setCartPoleStates(state,actionValue,maxQ);
        animationPanel.repaint();
    }

}


