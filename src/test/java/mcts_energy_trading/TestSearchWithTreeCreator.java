package mcts_energy_trading;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSearchWithTreeCreator {

    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    MonteCarloTreeCreator<VariablesEnergyTrading, Integer> monteCarloTreeCreator;

    @Before
    public void init() {
        environment=EnvironmentEnergyTrading.newDefault();
        monteCarloTreeCreator=createTreeCreator(StateEnergyTrading.newDefault());
    }

    @SneakyThrows
    @Test
    public void givenDefaultEnv_whenTimeIs6SoE0d7_thenBestActionIsSell() {
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(6,0.70);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        monteCarloTreeCreator.getNodeRoot().printTree();

        int actionValue=monteCarloTreeCreator.getFirstAction().getValue();
        System.out.println("actionValue = " + actionValue);
        Assert.assertEquals(-2,actionValue);
    }

    @SneakyThrows
    @Test
    public void givenDefaultEnv_whenTimeIs7SoE0d7_thenBestActionIsSell() {
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(7,0.70);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        monteCarloTreeCreator.getNodeRoot().printTree();

        int actionValue=monteCarloTreeCreator.getFirstAction().getValue();
        System.out.println("actionValue = " + actionValue);
        Assert.assertEquals(-2,actionValue);

    }




    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(StateInterface<VariablesEnergyTrading> state) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings= MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.5)
                .discountFactorBackupSimulationDefensive(0.5)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(8)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(100)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(0.0)
                .nofSimulationsPerNode(0)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(1e3)
                .isCreatePlotData(false)
                .build();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }



}
