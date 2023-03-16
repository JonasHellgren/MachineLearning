package monte_carlo_search.mcts_energy_trading;

import common.ListUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/***
 * By a search horizon of 4 (givenEnvWithCheapEnergyAtTime4_whenTimeIs4SoE0d5_thenBestActionIsBuy) the search is feasible
 * approx 5^4=625 nodes needed.
 */

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
    public void givenDefaultEnv_whenTimeIs7SoE0d7_thenBestActionIsSellLittle() {
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(7,0.70);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        monteCarloTreeCreator.getNodeRoot().printTree();

        int actionValue=monteCarloTreeCreator.getFirstAction().getValue();
        System.out.println("actionValue = " + actionValue);
        Assert.assertEquals(-1,actionValue);
    }

    @SneakyThrows
    @Test
    public void givenDefaultEnv_whenTimeIs7SoE0d9_thenBestActionIsSellMuch() {
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(7,0.90);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        monteCarloTreeCreator.getNodeRoot().printTree();

        int actionValue=monteCarloTreeCreator.getFirstAction().getValue();
        System.out.println("actionValue = " + actionValue);
        Assert.assertEquals(-2,actionValue);
    }

    @SneakyThrows
    @Test
    public void givenEnvWithCheapEnergyAtTime4_whenTimeIs4SoE0d5_thenBestActionIsBuy() {
        environment=EnvironmentEnergyTrading.newFromPrices(Arrays.asList(1d, 1d, 1d, 1d, 0.5, 1d, 1.5, 1d));
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(4,0.50);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSettings());

        List<ActionInterface<Integer>> actions=tih.getActionsOnBestPath();
        MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator=new MonteCarloSimulator<>(environment, createSettings());
        List<Double> rewards=simulator.stepWithActions(state,actions);
        int actionFirstValue=monteCarloTreeCreator.getFirstAction().getValue();
        double sumOfRewards= ListUtils.sumDoubleList(rewards);


        System.out.println("actions = " + actions);
        System.out.println("rewards = " + rewards);
        System.out.println("actionValue = " + actionFirstValue);
        System.out.println("sumOfRewards = " + sumOfRewards);

        Assert.assertEquals(2,actionFirstValue);
        Assert.assertTrue(sumOfRewards>0);
    }


    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(StateInterface<VariablesEnergyTrading> state) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings = createSettings();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
             //   .isDefensiveBackup(false)
              //  .alphaBackupDefensiveStep(0.5)
               // .discountFactorBackupSimulationDefensive(0.5)
                .coefficientMaxAverageReturn(0) //0 => average
                .maxTreeDepth(8)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(100)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(1.0)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(1e2)
                .isCreatePlotData(false)
                .build();
    }


}
