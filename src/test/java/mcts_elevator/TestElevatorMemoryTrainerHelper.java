package mcts_elevator;

import common.RandUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.MemoryTrainerHelperInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class TestElevatorMemoryTrainerHelper {

    private static final int START_DEPTH = 0;
    private static final int MAX_N_PERSONS_IN_ELEVATOR = 0;
    private static final int MAX_N_PERSONS_WAITING_TOTAL = 1;
    private static final int NOF_RANDOM_STATES = 100;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;
    private static final double SOE_LOW = 0.5;
    private static final double SOE_HIGH = 1.0;
    private static final int MAX_SIMULATION_DEPTH = 1000;
    private static final int BUFFER_SIZE = 10;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;
   // MemoryTrainerHelperInterface<VariablesElevator,Integer> trainer;

    ElevatorMemoryTrainerHelper trainer;


    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        StateInterface<VariablesElevator> startStateDummy = StateElevator.newFromVariables(VariablesElevator.builder().build());
        monteCarloTreeCreator = createTreeCreator(startStateDummy);

    }

    @Test
    @Ignore
    public void givenTrainer_whenCreatedExpBuffer_thenSizeCorrect() {
        final int bufferSize = 10;
        trainer=ElevatorMemoryTrainerHelper.builder()
                .bufferSize(bufferSize)
                .build();
        ReplayBuffer<VariablesElevator, Integer> replayBuffer=trainer.createExperienceBuffer(monteCarloTreeCreator);
        replayBuffer.getBuffer().forEach(System.out::println);
        Assert.assertEquals(bufferSize,replayBuffer.size());
    }

    @Test public void givenTrainedMemory_thenCorrectSoEValueMapping() {
        final int bufferSize = 50;
        trainer=ElevatorMemoryTrainerHelper.builder()
                .bufferSize(bufferSize)
                .build();
        ReplayBuffer<VariablesElevator, Integer> replayBuffer=trainer.createExperienceBuffer(monteCarloTreeCreator);

        log.info("Buffer created");

        NetworkMemoryInterface<VariablesElevator> memory=trainer.createMemory(replayBuffer);

        List<Double> soEList= Arrays.asList(0.3,0.5,0.7,0.9);

        for (double SoE:soEList) {
            System.out.println("SoE = " + SoE+", value= " + memory.read(getState(SoE)));
        }

        Assert.assertTrue(memory.read(getState(soEList.get(1)))<memory.read(getState(soEList.get(0))));
        Assert.assertTrue(memory.read(getState(soEList.get(2)))<memory.read(getState(soEList.get(1))));
        Assert.assertTrue(memory.read(getState(soEList.get(3)))<memory.read(getState(soEList.get(2))));



    }



    private StateInterface<VariablesElevator> getState(double SoE) {
        return StateElevator.newFromVariables(VariablesElevator.builder().SoE(SoE).build());
    }


    public MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate = ActionElevator.newValueDefaultRange(0);

        settings = MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .simulationPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .maxSimulationDepth(MAX_SIMULATION_DEPTH)   //20
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
