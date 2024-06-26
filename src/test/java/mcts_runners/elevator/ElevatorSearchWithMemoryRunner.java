package mcts_runners.elevator;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;

import java.util.Arrays;
import java.util.List;

public class ElevatorSearchWithMemoryRunner {

    private static final int NOF_SIMULATIONS_PER_NODE = 10;
    private static final int MAX_SIMULATION_DEPTH = 1000;

    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int NSTEPS_BETWEEN = 50;
    private static final int NSTEPS_BETWEEN_LARGER = 100;
    private static final int NOF_STEPS = 1000;
    private static final int BUFFER_SIZE = 100;

    @SneakyThrows
    public static void main(String[] args) throws InterruptedException {

        MonteCarloSettings<VariablesElevator, Integer> settingsMemoryCreation= createSimulatorSettings
                (StateElevator.newFromVariables(VariablesElevator.newDefault()));
        MonteCarloSimulator<VariablesElevator,Integer> simulator=
                new MonteCarloSimulator<>(EnvironmentElevator.newDefault(),settingsMemoryCreation);

        EnvironmentGenericInterface<VariablesElevator, Integer> environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN_LARGER));
        ElevatorMemoryTrainer trainer= ElevatorMemoryTrainer.builder()
                .build();

        ReplayBuffer<VariablesElevator, Integer> replayBuffer=trainer.createExperienceBuffer(simulator,BUFFER_SIZE);
        NetworkMemoryInterface<VariablesElevator,Integer> memory=new ElevatorValueMemoryNetwork<>(
                trainer.getOutMemoryMin(), trainer.getOutMemoryMax());
        trainer.trainMemory(memory,replayBuffer);
        printMemory(memory);

        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 0, 0))
                .build());
        MonteCarloTreeCreator<VariablesElevator, Integer> searchCreator= createSearchTree(state,memory);
        ElevatorRunner er=new ElevatorRunner(searchCreator,environment, NOF_STEPS);
        er.run(state);

    }

    private static void printMemory(NetworkMemoryInterface<VariablesElevator, Integer> memory) {
        List<Double> soEList= Arrays.asList(0.3,0.5,0.7,0.9);
        for (double SoE:soEList) {
            System.out.println("SoE = " + SoE+", value= " + memory.read(getState(SoE)));
        }
    }

    private static StateInterface<VariablesElevator> getState(double SoE) {
        return StateElevator.newFromVariables(VariablesElevator.builder().SoE(SoE).build());
    }

    public static MonteCarloSettings<VariablesElevator, Integer> createSimulatorSettings(StateInterface<VariablesElevator> startState) {

        return MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .simulationPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .maxSimulationDepth(MAX_SIMULATION_DEPTH)   //20
                .build();

    }

    public static MonteCarloTreeCreator<VariablesElevator, Integer> createSearchTree(StateInterface<VariablesElevator> startState,
                                                                                     NetworkMemoryInterface<VariablesElevator,Integer> memory) {
        EnvironmentGenericInterface<VariablesElevator, Integer> environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate = ActionElevator.newValueDefaultRange(0);

        MonteCarloSettings<VariablesElevator, Integer> settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .weightMemoryValue(50)
                .discountFactorSteps(0.9)
                .discountFactorBackupSimulationDefensive(0.99)
                .maxTreeDepth(100)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(300)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(50)   //20
                .coefficientExploitationExploration(1e0)  //1e1
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .memory(memory)
                .build();
    }

}
