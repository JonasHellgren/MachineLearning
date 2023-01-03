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
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

public class TestStateValueMemoryTraining {
    private static final int MAX_NOF_ITERATIONS = 10_000;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.1;
    private static final int MAX_TREE_DEPTH=100;
    private static final int TIME_BUDGET_MILLI_SECONDS = 100;
    private static final int MAX_SIZE = 100;
    private static final int NOF_SAMPLES = 100;
    private static final int MINI_BATCH_SIZE = 10;

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
        Assert.assertEquals(NOF_SAMPLES,buffer.size());
        Assert.assertEquals(MINI_BATCH_SIZE,miniBatch.size());
    }

    @Test public void trainNetwork() {
        CartPoleStateValueMemory memory=new CartPoleStateValueMemory();
        double maxError = 1e-10;
        int  maxNofEpochs = 10_000;
        int epoch = 1;
        do {
            List<Experience<CartPoleVariables, Integer>> miniBatch=buffer.getMiniBatch(MINI_BATCH_SIZE);
            memory.doOneLearningIteration(miniBatch);
            printProgressSometimes(memory.getLearningRule(), epoch);
            epoch++;

        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);


        StateCartPole state=StateCartPole.newAllStatesAsZero();
        System.out.println("memory.read(state.getVariables()) = " + memory.read(state.getVariables()));
        StateCartPole stateRandom=StateCartPole.newRandom();
        System.out.println("memory.read(stateRandom.getVariables()) = " + memory.read(stateRandom.getVariables()));

    }

    private ReplayBuffer<CartPoleVariables,Integer>  createExperienceBuffer() {
        ReplayBuffer<CartPoleVariables,Integer>  buffer=new ReplayBuffer<>(MAX_SIZE);

        for (int i = 0; i < NOF_SAMPLES; i++) {
        StateCartPole stateRandom=StateCartPole.newRandom();
        SimulationResults simulationResults=
                monteCarloTreeCreator.simulate(stateRandom);
        List<Double> returns= new ArrayList<>(simulationResults.getReturnListForAll());
        double averageReturn=returns.stream().mapToDouble(val -> val).average().orElse(0.0);
         //   System.out.println("stateRandom = " + stateRandom);
         //   System.out.println("stateNormalizer.normalize(stateRandom) = " + stateNormalizer.normalize(stateRandom));
          //  System.out.println("averageReturn = " + averageReturn);

            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                    .stateVariables(stateRandom.getVariables())
                    .action(0)
                    .stateVariableNew(stateRandom.getVariables())
                    .reward(0)
                    .value(averageReturn/100)
                    .build());
        }
        return buffer;
    }

    private void printProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 1000 == 0) {
            System.out.println("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
        }
    }

}
