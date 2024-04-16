package mcts_runners.energy_trading;

import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.*;
import monte_carlo_tree_search.network_training.ReplayBuffer;

import java.util.List;

/*** memory guides search, if weightMemoryValue is zero, higher prob sub optimal solution
  *  sumOfRewards shall be 3
 *  mostly finds best value
 */

@Log
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
        trainer=new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        memory=new EnergyTraderValueMemoryNetwork<>();
        buffer=trainer.createExperienceBuffer(simulator, BUFFER_SIZE);
        trainer.trainMemory(memory,buffer);

        StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
        monteCarloTreeCreator=createTreeCreator(stateStart,memory);
        monteCarloTreeCreator.run();

        warnIfBadSolution();
        doPrinting(stateStart);
    }

    private static void warnIfBadSolution() {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSearchSettings());
        if (tih.getActionsOnBestPath().size()!=EnvironmentEnergyTrading.AFTER_MAX_TIME) {
            log.warning("No adequate solution found, tree not deep enough");
        }
    }

    private static void doPrinting(StateInterface<VariablesEnergyTrading> stateStart) {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSearchSettings());
        List<ActionInterface<Integer>> actions= tih.getActionsOnBestPath();
        List<Double> rewards=simulator.stepWithPresetActions(stateStart,actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        double avgError=memory.getAverageValueError(buffer.getBuffer());
        System.out.println("avgError = " + avgError);
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
                .alphaBackupDefensiveStep(0.1)
                .discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(8)
                .maxNofIterations(100_000)  //100_000
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(0.0)
                .weightMemoryValue(0.5)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(1e-2)  //0.1
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
