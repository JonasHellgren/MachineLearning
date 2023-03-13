package monte_carlo_tree_search.domains.elevator;

import common.RandUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.*;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.generic_interfaces.MemoryTrainerInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;

/***
 * Learning does not start if MAX_MEMORY_OUT is small
 */

@Getter
@Builder
@Log
public class ElevatorMemoryTrainer
    implements MemoryTrainerInterface<VariablesElevator, Integer> {

    private static final int MINI_BATCH_SIZE=10;
    private static final int BUFFER_SIZE=10;
    private static final int MAX_N_NOF_SOE_VALUES = 2;
    private static final double MAX_ERROR=1e-5;
    private static final int MAX_EPOCHS=10_000;
    private static final int MAX_N_PERSONS_IN_ELEVATOR = 0;
    private static final int MAX_N_PERSONS_WAITING_TOTAL = 1;
    private static final int START_DEPTH = 0;
    private static final double MIN_MEMORY_OUT = -10.0; //large (abs) means smaller out reference for net
    private static final double MAX_MEMORY_OUT = 10.0;
    private static final double MIN_NET_OUTPUT = 0.00;  //for clipping
    private static final double MAX_NET_OUTPUT = 1.0;

    @Builder.Default
    int miniBatchSize=MINI_BATCH_SIZE;
    @Builder.Default
    int bufferSize=BUFFER_SIZE;
    @Builder.Default
    int nofSoEValuesPerStateValues = MAX_N_NOF_SOE_VALUES;
    @Builder.Default
    double maxError=MAX_ERROR;
    @Builder.Default
    int maxNofEpochs=MAX_EPOCHS;
    @Builder.Default
    int nPersonsMaxElevator = MAX_N_PERSONS_IN_ELEVATOR;
    @Builder.Default
    int nPersonsMaxTotalWaiting = MAX_N_PERSONS_WAITING_TOTAL;
    @Builder.Default
    double outMemoryMin = MIN_MEMORY_OUT;
    @Builder.Default
    double outMemoryMax = MAX_MEMORY_OUT;



    @Override
    public ReplayBuffer<VariablesElevator, Integer> createExperienceBuffer
            (MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator) {
        ReplayBuffer<VariablesElevator, Integer> buffer = new ReplayBuffer<>(bufferSize);
        Counter counter = new Counter(bufferSize);

        while (!counter.isExceeded()) {
            StateInterface<VariablesElevator> stateRandom = StateElevator.newFromVariables(
                    VariablesElevator.newRandom(nPersonsMaxElevator, nPersonsMaxTotalWaiting));

            for (int j = 0; j < nofSoEValuesPerStateValues; j++) {
                StateInterface<VariablesElevator> stateRandomCopy=stateRandom.copy();
                double value=getAverageReturnPerStep(monteCarloTreeCreator, stateRandomCopy);
                buffer.addExperience(Experience.<VariablesElevator, Integer>builder()
                        .stateVariables(stateRandomCopy.getVariables())
                        .value(value)
                        .build());
                counter.increase();

            }
        }
        return buffer;
    }

    private double getAverageReturnPerStep(MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator, StateInterface<VariablesElevator> stateRandomCopy) {
        stateRandomCopy.getVariables().SoE = RandUtils.getRandomDouble
                (EnvironmentElevator.SOE_LOW, EnvironmentElevator.SoE_HIGH);
        MonteCarloSimulator<VariablesElevator, Integer> simulator=new MonteCarloSimulator<>(monteCarloTreeCreator.getEnvironment(),monteCarloTreeCreator.getSettings());

        SimulationResults simulationResults = simulator.simulate(stateRandomCopy, START_DEPTH);
        return getAverageReturn(simulationResults)/(double)
        monteCarloTreeCreator.getSettings().getMaxSimulationDepth();
    }


    @Override
    public void trainMemory(NetworkMemoryInterface<VariablesElevator,Integer> memory,
                            ReplayBuffer<VariablesElevator, Integer> buffer) {

        MemoryTrainerHelper<VariablesElevator,Integer> helper=new MemoryTrainerHelper<>(miniBatchSize,maxError,maxNofEpochs);
        helper.trainMemory(memory,buffer);
    }


    public double getAverageReturn(SimulationResults simulationResults) {
        MemoryTrainerHelper<VariablesElevator,Integer> helper=new MemoryTrainerHelper<>(miniBatchSize,maxError,maxNofEpochs);
        return  helper.getAverageReturn(simulationResults);
    }

}
