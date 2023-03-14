package mcts_runners.energy_trading;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.elevator.ElevatorPolicies;
import monte_carlo_tree_search.domains.elevator.EnvironmentElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

public class EnergyTradingSearchRunner {


    @SneakyThrows
    public static void main(String[] args) {

        StateInterface<VariablesEnergyTrading> state= StateEnergyTrading.newDefault();
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment= EnvironmentEnergyTrading.newDefault();
        MonteCarloTreeCreator<VariablesEnergyTrading, Integer> monteCarloTreeCreator= createTreeCreator(state);

        EnergyTradingRunner runner=new EnergyTradingRunner(monteCarloTreeCreator,environment);
        runner.run(state);


    }

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(StateInterface<VariablesEnergyTrading> state) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings= MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(7)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(0.01)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }


}
