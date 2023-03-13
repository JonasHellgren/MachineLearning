package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.classes.MemoryTrainerHelper;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.generic_interfaces.MemoryTrainerInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import java.util.List;

/**
 *  Helper class used for training. The method createExperienceBuffer gives buffer later used by
 *  method trainMemory.
 */


public class CartPoleMemoryTrainer
        implements MemoryTrainerInterface<CartPoleVariables, Integer> {
    private static final int START_DEPTH = 0;
    int miniBatchSize;
    int bufferSize;
    double maxError;
    int maxNofEpochs;
    MemoryTrainerHelper<CartPoleVariables, Integer> helper;

    public CartPoleMemoryTrainer(int miniBatchSize, int bufferSize, double maxError, int maxNofEpochs) {
        this.miniBatchSize = miniBatchSize;
        this.bufferSize = bufferSize;
        this.maxError = maxError;
        this.maxNofEpochs = maxNofEpochs;
        this.helper=new MemoryTrainerHelper<>(miniBatchSize,maxError,maxNofEpochs);
    }

    @Override
    public ReplayBuffer<CartPoleVariables,Integer> createExperienceBuffer(
            MonteCarloTreeCreator<CartPoleVariables, Integer> monteCarloTreeCreator) {
        ReplayBuffer<CartPoleVariables,Integer>  buffer=new ReplayBuffer<>(bufferSize);

        for (int i = 0; i < bufferSize; i++) {
            StateCartPole stateRandom=StateCartPole.newRandom();
            SimulationResults simulationResults=monteCarloTreeCreator.simulate(stateRandom, START_DEPTH);
            double averageReturn = helper.getAverageReturn(simulationResults);
            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                    .stateVariables(stateRandom.getVariables())
                    .value(averageReturn)
                    .build());
        }
        return buffer;
    }

    @Override
    public void trainMemory(NetworkMemoryInterface<CartPoleVariables,Integer> memory,
                            ReplayBuffer<CartPoleVariables, Integer> buffer) {
        helper.trainMemory(memory,buffer);
    }

    public double getAverageReturn(SimulationResults simulationResults) {
        return helper.getAverageReturn(simulationResults);
    }

}
