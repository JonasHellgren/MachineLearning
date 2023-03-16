package mcts_runners.energy_trading;

import common.ListUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.*;
import monte_carlo_tree_search.network_training.ReplayBuffer;

import java.util.List;

public class EnergyTradingSearchWithPresetMemoryRunner {

    private static final int NOF_SIMULATIONS = 100;
    private static final int MAX_SIMULATION_DEPTH = 10;
    private static final int BUFFER_SIZE = 1_000;
    private static final int MINI_BATCH_SIZE = 20;
    private static final double MAX_ERROR = 1e-5;
    private static final int MAX_NOF_EPOCHS = 10_000;

    static EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    static MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;
    static MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer;
    static NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory;
    static ReplayBuffer<VariablesEnergyTrading,Integer> buffer;
    static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> monteCarloTreeCreator;

    @SneakyThrows
    public static void main(String[] args) {

        environment = EnvironmentEnergyTrading.newDefault();
        simulator= new MonteCarloSimulator<>(environment,createSimulatorSettings());
        trainer=new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, BUFFER_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        memory=new EnergyTraderValueMemory<>();
        buffer=trainer.createExperienceBuffer(simulator);
        trainer.trainMemory(memory,buffer);
        double avgError=memory.getAverageValueError(buffer.getBuffer());
        System.out.println("avgError = " + avgError);


       // StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
        //monteCarloTreeCreator=createTreeCreator(stateStart,memory);
        //EnergyTradingRunner runner=new EnergyTradingRunner(monteCarloTreeCreator,environment);
        //runner.run(stateStart);

        StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
        monteCarloTreeCreator=createTreeCreator(stateStart,memory);
        monteCarloTreeCreator.run();
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSearchSettings());

        List<ActionInterface<Integer>> actions=tih.getActionsOnBestPath();
        List<Double> rewards=simulator.stepWithActions(stateStart,actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        System.out.println("actions = " + actions);
        System.out.println("rewards = " + rewards);
        simulator.getStates().forEach(System.out::println);
        System.out.println("sumOfRewards = " + sumOfRewards);

    }

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(
            StateInterface<VariablesEnergyTrading> state,
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings = createSearchSettings();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .memory(memory)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.5)
                .discountFactorBackupSimulationDefensive(0.5)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(8)
                .maxNofIterations(50_000)
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(0.0)
                .weightMemoryValue(0.5)
                .nofSimulationsPerNode(0)
                //.maxSimulationDepth(10)
                .coefficientExploitationExploration(1)
                .isCreatePlotData(false)
                .build();
    }

    public static MonteCarloSettings<VariablesEnergyTrading, Integer> createSimulatorSettings() {

        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(NOF_SIMULATIONS)
                .maxSimulationDepth(MAX_SIMULATION_DEPTH)
                .build();
    }


}
