package mcts_runners.energy_trading;

import common.ListUtils;
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

/**
 * Pure search mostly fails to find optimal solution for this domain, high branching factor requires big tree
 */

@Log
public class EnergyTradingSearchRunner {

    private static final int NOF_SIMULATIONS = 100;
    private static final int MAX_SIMULATION_DEPTH = 10;

    static EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    static MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;
    static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> monteCarloTreeCreator;

    @SneakyThrows
    public static void main(String[] args) {
    environment = EnvironmentEnergyTrading.newDefault();
    StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
    monteCarloTreeCreator=createTreeCreator(stateStart);
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

        simulator= new MonteCarloSimulator<>(environment,createSimulatorSettings());

        List<Double> rewards=simulator.stepWithActions(stateStart,actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        System.out.println("actions = " + actions);
        System.out.println("rewards = " + rewards);
        simulator.getStates().forEach(System.out::println);
        System.out.println("sumOfRewards = " + sumOfRewards);
    }

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(
            StateInterface<VariablesEnergyTrading> state) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings = createSearchSettings();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .alphaBackupNormal(1.0)
                .alphaBackupDefensiveStep(0.1)
                .discountFactorBackupSimulationDefensive(0.99)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(8)
                .discountFactorSimulation(0.99)
                .maxNofIterations(100_000)  //100_000
                .timeBudgetMilliSeconds(2000)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(1.0)
                .weightMemoryValue(0.0)
                .nofSimulationsPerNode(5)
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
