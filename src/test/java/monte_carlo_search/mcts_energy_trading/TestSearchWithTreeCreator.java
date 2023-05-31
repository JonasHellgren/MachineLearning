package monte_carlo_search.mcts_energy_trading;

import common.ListUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/***
 * By a search horizon of 4 (givenEnvWithCheapEnergyAtTime4_whenTimeIs4SoE0d5_thenBestActionIsBuy) the search is feasible
 * approx 5^4=625 nodes needed.
 *
 *  weightReturnsSteps = 0 does not work
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

        extractAndPrint(state);

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

        extractAndPrint(state);

        int actionValue=monteCarloTreeCreator.getFirstAction().getValue();
        System.out.println("actionValue = " + actionValue);
        Assert.assertEquals(-2,actionValue);
    }

    private void extractAndPrint(StateInterface<VariablesEnergyTrading> state) {
        List<ActionInterface<Integer>> actions = getActions();
        List<Double> rewards = getRewards(state, actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        int bestFirstAction=monteCarloTreeCreator.getFirstAction().getValue();
        Map<Integer, Double> actionValueMap = getAVMap();
        doPrinting(actions, rewards, sumOfRewards, bestFirstAction, actionValueMap);
    }

    @SneakyThrows
    @Test
    public void givenEnvWithCheapEnergyAtTime4_whenTimeIs4SoE0d5_thenBestActionIsBuy() {
        environment=EnvironmentEnergyTrading.newFromPrices(Arrays.asList(1d, 1d, 1d, 1d, 0.5, 1d, 1.5, 1d));
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromTimeAndSoE(4,0.50);
        monteCarloTreeCreator.setStartState(state);
        monteCarloTreeCreator.run();

        List<ActionInterface<Integer>> actions = getActions();
        List<Double> rewards = getRewards(state, actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        int bestFirstAction=monteCarloTreeCreator.getFirstAction().getValue();
        Map<Integer, Double> actionValueMap = getAVMap();
        doPrinting(actions, rewards, sumOfRewards, bestFirstAction, actionValueMap);

        Assert.assertEquals(2,bestFirstAction);
        Assert.assertTrue(sumOfRewards>0);
    }


    private void doPrinting(List<ActionInterface<Integer>> actions, List<Double> rewards, double sumOfRewards, int bestFirstAction, Map<Integer, Double> actionValueMap) {
        System.out.println("actions = " + actions);
        System.out.println("rewards = " + rewards);
        System.out.println("sumOfRewards = " + sumOfRewards);
        System.out.println("bestFirstAction = " + bestFirstAction);
        System.out.println("actionValueMap = " + actionValueMap);
    }

    @NotNull
    private Map<Integer, Double> getAVMap() {
        return NodeInfoHelper.actionValueMap(ActionEnergyTrading.newValue(0),monteCarloTreeCreator.getNodeRoot());
    }

    private List<Double> getRewards(StateInterface<VariablesEnergyTrading> state, List<ActionInterface<Integer>> actions) {
        MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator=new MonteCarloSimulator<>(environment, createSettings());
        return simulator.stepWithPresetActions(state, actions);
    }

    private List<ActionInterface<Integer>> getActions() {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSettings());
        return tih.getActionsOnBestPath();
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
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.1)
                .discountFactorSteps(1.0)
                .discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //0 => average
                .maxTreeDepth(8)
                .minNofIterations(1000)
                .maxNofIterations(100_000)
                .timeBudgetMilliSeconds(200)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(1.0)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(1e2)
                .isCreatePlotData(false)
                .build();
    }


}
