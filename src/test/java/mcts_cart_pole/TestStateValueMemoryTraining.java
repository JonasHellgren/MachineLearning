package mcts_cart_pole;

import mcts_cart_pole_runner.MemoryTrainerHelper;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestStateValueMemoryTraining {
    private static final int MAX_NOF_ITERATIONS = 10_000;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.1;
    private static final int MAX_TREE_DEPTH=100;
    private static final int TIME_BUDGET_MILLI_SECONDS = 100;
    private static final int BUFFER_SIZE = 1000;
    private static final int MINI_BATCH_SIZE = 30;
    private static final int DELTA = 5;
    double MAX_ERROR = 5e-5;
    int MAX_NOF_EPOCHS = 50_000;

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    ReplayBuffer<CartPoleVariables,Integer> buffer;
    MemoryTrainerHelper memoryTrainerHelper;
    @Before
    public void init() {
        environment = EnvironmentCartPole.newDefault();
        int VALUE_LEFT = 0;
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

        monteCarloTreeCreator= MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();

        memoryTrainerHelper=new MemoryTrainerHelper(MINI_BATCH_SIZE,BUFFER_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        buffer=memoryTrainerHelper.createExperienceBuffer(monteCarloTreeCreator);
         }

    @Test
    public void checkBufferSize() {
        List<Experience<CartPoleVariables, Integer>> miniBatch=buffer.getMiniBatch(MINI_BATCH_SIZE);
        System.out.println("buffer = " + buffer);
        System.out.println("buffer.bufferAsString(miniBatch) = " + buffer.bufferAsString(miniBatch));
        Assert.assertEquals(BUFFER_SIZE,buffer.size());
        Assert.assertEquals(MINI_BATCH_SIZE,miniBatch.size());
    }

    @Test public void trainNetwork() {
        CartPoleStateValueMemory<CartPoleVariables> memory=new CartPoleStateValueMemory<>();

        memoryTrainerHelper.trainMemory(memory, buffer);

        StateCartPole stateAllZero=StateCartPole.newAllStatesAsZero();
        StateCartPole stateExtreme=StateCartPole.newAllPositiveMax();

        System.out.println("memory.read(stateAllZero.getVariables()) = " + memory.read(stateAllZero));
        System.out.println("memory.read(stateExtreme.getVariables()) = " + memory.read(stateExtreme));

        SimulationResults simulationResultsAllZero=monteCarloTreeCreator.simulate(stateAllZero);
        Assert.assertEquals(
                memoryTrainerHelper.getAverageReturn(simulationResultsAllZero),
                memory.read(stateAllZero),
                DELTA);

        SimulationResults simulationResultsExtreme=monteCarloTreeCreator.simulate(stateExtreme);
        Assert.assertEquals(
                memoryTrainerHelper.getAverageReturn(simulationResultsExtreme),
                memory.read(stateExtreme),
                DELTA);

    }







}
