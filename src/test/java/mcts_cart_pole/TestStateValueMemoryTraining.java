package mcts_cart_pole;

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
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
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

    MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environment;
    StateNormalizerCartPole stateNormalizer;
    ReplayBuffer<CartPoleVariables,Integer> buffer;
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

        stateNormalizer=new StateNormalizerCartPole();
        buffer=createExperienceBuffer();
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
        CartPoleStateValueMemory memory=new CartPoleStateValueMemory();
        double maxError = 1e-5;
        int  maxNofEpochs = 50_000;
        trainMemory(memory, maxError, maxNofEpochs);

        StateCartPole stateAllZero=StateCartPole.newAllStatesAsZero();
        StateCartPole stateExtreme=StateCartPole.newAllPositiveMax();

        System.out.println("memory.read(stateAllZero.getVariables()) = " + memory.read(stateAllZero.getVariables()));
        System.out.println("memory.read(stateExtreme.getVariables()) = " + memory.read(stateExtreme.getVariables()));

        SimulationResults simulationResultsAllZero=monteCarloTreeCreator.simulate(stateAllZero);
        Assert.assertEquals(getAverageReturn(simulationResultsAllZero), memory.read(stateAllZero.getVariables()), DELTA);

        SimulationResults simulationResultsExtreme=monteCarloTreeCreator.simulate(stateExtreme);
        Assert.assertEquals(getAverageReturn(simulationResultsExtreme), memory.read(stateExtreme.getVariables()), DELTA);

    }

    private void trainMemory(CartPoleStateValueMemory memory, double maxError, int maxNofEpochs) {
        int epoch = 0;
        do {
            List<Experience<CartPoleVariables, Integer>> miniBatch=buffer.getMiniBatch(MINI_BATCH_SIZE);
            memory.doOneLearningIteration(miniBatch);
            printProgressSometimes(memory.getLearningRule(), epoch++);
        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);
    }

    private ReplayBuffer<CartPoleVariables,Integer>  createExperienceBuffer() {
        ReplayBuffer<CartPoleVariables,Integer>  buffer=new ReplayBuffer<>(BUFFER_SIZE);

        for (int i = 0; i < BUFFER_SIZE; i++) {
        StateCartPole stateRandom=StateCartPole.newRandom();
        SimulationResults simulationResults=monteCarloTreeCreator.simulate(stateRandom);
        double averageReturn = getAverageReturn(simulationResults);
            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                    .stateVariables(stateRandom.getVariables())
                    .value(CartPoleStateValueMemory.normalizeOutput(averageReturn))
                    .build());
        }
        return buffer;
    }

    private double getAverageReturn(SimulationResults simulationResults) {
        List<Double> returns= new ArrayList<>(simulationResults.getReturnListForAll());
        return returns.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    private void printProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 1000 == 0 || epoch==0) {
            System.out.println("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
        }
    }

}
