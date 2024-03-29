package monte_carlo_tree_search.domains.energy_trading;

import monte_carlo_tree_search.models_and_support_classes.MemoryTrainerHelper;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.interfaces.MemoryTrainerInterface;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;

public class EnergyTraderMemoryTrainer
        implements MemoryTrainerInterface<VariablesEnergyTrading, Integer> {

    private static final int START_DEPTH = 0;
    int miniBatchSize;
    double maxError;
    int maxNofEpochs;
    MemoryTrainerHelper<VariablesEnergyTrading, Integer> helper;

    public EnergyTraderMemoryTrainer(int miniBatchSize, double maxError, int maxNofEpochs) {
        this.miniBatchSize = miniBatchSize;
        this.maxError = maxError;
        this.maxNofEpochs = maxNofEpochs;
        this.helper = new MemoryTrainerHelper<>(miniBatchSize, maxError, maxNofEpochs);
    }

    @Override
    public ReplayBuffer<VariablesEnergyTrading, Integer> createExperienceBuffer(
            MonteCarloSimulator<VariablesEnergyTrading, Integer> simulator,
            int bufferSize) {

        ReplayBuffer<VariablesEnergyTrading, Integer> buffer = new ReplayBuffer<>(bufferSize);
        for (int i = 0; i < bufferSize; i++) {
            StateInterface<VariablesEnergyTrading> stateRandom = StateEnergyTrading.newRandom();
            SimulationResults simulationResults = simulator.simulate(stateRandom, false, START_DEPTH);
            double averageReturn = simulationResults.averageReturnFromNonFailingsOrAnyFailingReturnIfAllFails();
            buffer.addExperience(Experience.<VariablesEnergyTrading, Integer>builder()
                    .stateVariables(stateRandom.getVariables())
                    .value(averageReturn)
                    .build());
        }
        return buffer;
    }

    @Override
    public void trainMemory(NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory,
                            ReplayBuffer<VariablesEnergyTrading, Integer> buffer) {
        helper.trainMemory(memory, buffer);
    }

    public double getAverageReturn(SimulationResults simulationResults) {
        return helper.getAverageReturn(simulationResults);
    }

}


