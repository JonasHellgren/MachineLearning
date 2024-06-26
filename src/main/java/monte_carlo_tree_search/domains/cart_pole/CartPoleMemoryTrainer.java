package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.models_and_support_classes.MemoryTrainerHelper;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.interfaces.MemoryTrainerInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;

/**
 *  Helper class used for training. The method createExperienceBuffer gives buffer later used by
 *  method trainMemory.
 */


public class CartPoleMemoryTrainer
        implements MemoryTrainerInterface<CartPoleVariables, Integer> {
    private static final int START_DEPTH = 0;
    int miniBatchSize;
    double maxError;
    int maxNofEpochs;
    MemoryTrainerHelper<CartPoleVariables, Integer> helper;

    public CartPoleMemoryTrainer(int miniBatchSize, double maxError, int maxNofEpochs) {
        this.miniBatchSize = miniBatchSize;
        this.maxError = maxError;
        this.maxNofEpochs = maxNofEpochs;
        this.helper=new MemoryTrainerHelper<>(miniBatchSize,maxError,maxNofEpochs);
    }

    @Override
    public ReplayBuffer<CartPoleVariables,Integer> createExperienceBuffer(
            MonteCarloSimulator<CartPoleVariables, Integer> simulator,
            int bufferSize) {
        ReplayBuffer<CartPoleVariables,Integer>  buffer=new ReplayBuffer<>(bufferSize);

        for (int i = 0; i < bufferSize; i++) {
            StateInterface<CartPoleVariables> stateRandom=StateCartPole.newRandom();
            SimulationResults simulationResults=simulator.simulate(stateRandom, false, START_DEPTH);
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
