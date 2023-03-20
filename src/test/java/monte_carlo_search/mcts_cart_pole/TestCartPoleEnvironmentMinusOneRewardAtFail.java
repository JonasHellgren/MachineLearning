package monte_carlo_search.mcts_cart_pole;

import monte_carlo_tree_search.models_and_support_classes.*;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCartPoleEnvironmentMinusOneRewardAtFail {

    private static final int FAIL_REWARD = -1;
    private static final int NON_FAIL_REWARD = 0;
    private static final int VALUE_LEFT = 0;
    private static final double DELTA = 0.1;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;


    StateInterface<CartPoleVariables> state;
    ActionCartPole action;
    StepReturnGeneric<CartPoleVariables> stepReturn;

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    MonteCarloSettings<CartPoleVariables, Integer> settings;
    StateInterface<CartPoleVariables> stateUpRight;
    StateInterface<CartPoleVariables> stateLeaningRight;

    @Before
    public void init() {
        environment= EnvironmentCartPole.builder()
        .failReward(FAIL_REWARD).nonFailReward(NON_FAIL_REWARD).build();

        action= ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .discountFactorSimulation(0.9)
                .build();
        stateUpRight = StateCartPole.newAllStatesAsZero();
        state=stateUpRight.copy();
        stateLeaningRight = stateUpRight.copy();
        stateLeaningRight.getVariables().theta=EnvironmentCartPole.THETA_THRESHOLD_RADIANS*0.75;
        monteCarloTreeCreator= MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();

    }

    @Test
    public void forceLeftMovesCartLeftAndRotatesRight() {
        ActionCartPole action= ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        applyActionUntilTermination(action);

        System.out.println("state.getVariables() = " + state.getVariables());

        Assert.assertEquals(-1,stepReturn.reward, DELTA);

        Assert.assertTrue(state.getVariables().x<0);
        Assert.assertTrue(state.getVariables().theta>0);
        Assert.assertTrue(state.getVariables().nofSteps>0);
    }

    @Test
    public void simulateFromUpRightShallGiveBetterReturnThanLeaningRight() {
        double averageReturnAllZero = getAverageReturn(stateUpRight);
        System.out.println("averageReturnAllZero = " + averageReturnAllZero);

        double averageReturnLeaningRight= getAverageReturn(stateLeaningRight);
        System.out.println("averageReturnLeaningRight = " + averageReturnLeaningRight);

        Assert.assertTrue(averageReturnAllZero>averageReturnLeaningRight);

    }

    private double getAverageReturn(StateInterface<CartPoleVariables> state ) {
        MonteCarloSimulator<CartPoleVariables, Integer> simulator=new MonteCarloSimulator<>(
                environment,settings);

        SimulationResults simulationResults=
                simulator.simulate(state.copy());
        List<Double> avgList= new ArrayList<>(simulationResults.getReturnsForFailing());
        return avgList.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    private void applyActionUntilTermination(ActionCartPole action) {
        do {

            stepReturn=environment.step(action, state);
            state.setFromReturn(stepReturn);

            System.out.println("stepReturn.reward = " + stepReturn.reward);
        }  while (!stepReturn.isTerminal);
    }

}
