package mcts_elevator;

import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Log
public class TestElevatorMemoryTrainer {

    private static final int NOF_SIMULATIONS_PER_NODE = 10;
    private static final int MAX_SIMULATION_DEPTH = 1000;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;
    ElevatorMemoryTrainer trainer;


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
        trainer= ElevatorMemoryTrainer.builder()
                .bufferSize(bufferSize)
                .build();
        ReplayBuffer<VariablesElevator, Integer> replayBuffer=trainer.createExperienceBuffer(monteCarloTreeCreator);
        replayBuffer.getBuffer().forEach(System.out::println);
        Assert.assertEquals(bufferSize,replayBuffer.size());
    }

    @Test public void givenTrainedMemory_thenCorrectSoEValueMapping() {
        final int bufferSize = 50;
        trainer= ElevatorMemoryTrainer.builder()
                .bufferSize(bufferSize)
                .build();
        ReplayBuffer<VariablesElevator, Integer> replayBuffer=trainer.createExperienceBuffer(monteCarloTreeCreator);
        NetworkMemoryInterface<VariablesElevator> memory=new ElevatorStateValueMemory<>(trainer.getOutMemoryMin(),trainer.getOutMemoryMax());
        trainer.trainMemory(memory,replayBuffer);

        List<Double> soEList= Arrays.asList(0.3,0.5,0.7,0.9);

        for (double SoE:soEList) {
            System.out.println("SoE = " + SoE+", value= " + memory.read(getState(SoE)));
        }

        Assert.assertTrue(memory.read(getState(soEList.get(0)))<memory.read(getState(soEList.get(1))));
        Assert.assertTrue(memory.read(getState(soEList.get(1)))<memory.read(getState(soEList.get(2))));
        Assert.assertTrue(memory.read(getState(soEList.get(2)))<memory.read(getState(soEList.get(3))));

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
