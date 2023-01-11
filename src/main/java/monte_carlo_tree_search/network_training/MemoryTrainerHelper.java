package monte_carlo_tree_search.network_training;

import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.List;

/**
 *  Helper class used for training. The method createExperienceBuffer gives buffer later used by
 *  method trainMemory.
 */

//todo, make generic
@Log
public class MemoryTrainerHelper {
    private static final int START_DEPTH = 0;
    int miniBatchSize;
    int bufferSize;
    double maxError;
    int maxNofEpochs;

    public MemoryTrainerHelper(int miniBatchSize, int bufferSize, double maxError, int maxNofEpochs) {
        this.miniBatchSize = miniBatchSize;
        this.bufferSize = bufferSize;
        this.maxError = maxError;
        this.maxNofEpochs = maxNofEpochs;
    }

    public ReplayBuffer<CartPoleVariables,Integer> createExperienceBuffer(
            MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator) {
        ReplayBuffer<CartPoleVariables,Integer>  buffer=new ReplayBuffer<>(bufferSize);

        for (int i = 0; i < bufferSize; i++) {
            StateCartPole stateRandom=StateCartPole.newRandom();
            SimulationResults simulationResults=monteCarloTreeCreator.simulate(stateRandom, START_DEPTH);
            double averageReturn = getAverageReturn(simulationResults);
            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                    .stateVariables(stateRandom.getVariables())
                    .value(averageReturn)
                    .build());
        }
        return buffer;
    }

    public void trainMemory(NetworkMemoryInterface<CartPoleVariables> memory,
                            ReplayBuffer<CartPoleVariables, Integer> buffer) {
        int epoch = 0;
        do {
            List<Experience<CartPoleVariables, Integer>> miniBatch=buffer.getMiniBatch(miniBatchSize);
            memory.learn(miniBatch);
            logProgressSometimes(memory.getLearningRule(), epoch++);
        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);
        logEpoch(memory.getLearningRule(), epoch);
    }

    private void logProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 1000 == 0 || epoch==0) {
            logEpoch(learningRule, epoch);
        }
    }

    private void logEpoch(MomentumBackpropagation learningRule, int epoch) {
        log.info("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
    }

    public double getAverageReturn(SimulationResults simulationResults) {
        List<Double> returns= new ArrayList<>(simulationResults.getReturnListForAll());
        return returns.stream().mapToDouble(val -> val).average().orElse(0.0);
    }



}
