package mcts_cell_charging;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.*;
import monte_carlo_tree_search.domains.battery_cell.*;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestMonteCarloControlledCharging {

    private static final double SOC_INIT = 0.1;
    private static final int TEMPERATURE_INIT = 20;
    private static final int TIME_INIT = 0;

    private static final int MAX_NOF_ITERATIONS = 50000;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important

    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.01;
    private static final int DT = 10;
    private static final int NOF_CURRENT_LEVELS = 5;
    private static final int MAX_TIME = 120; //120;
    private static final int MAX_TREE_DEPTH = MAX_TIME/DT;
    private static final int TIME_OUT = 15_000;

    MonteCarloTreeCreator<CellVariables, Integer> monteCarloTreeCreator;
    EnvironmentGenericInterface<CellVariables, Integer> environment;
    CellSettings cellSettings;
    MonteCarloSettings<CellVariables, Integer> settings;
    ActionInterface<Integer> actionTemplate;

    @Before
    public void init() {
        cellSettings=CellSettings.builder()
                .maxTime(MAX_TIME).dt(DT).build();
        environment = new EnvironmentCell(cellSettings);
        actionTemplate= ActionCell.builder()
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();
        settings= MonteCarloSettings.<CellVariables, Integer>builder()
                .actionSelectionPolicy(CellPolicies.newBestFeasible(actionTemplate,environment))
                .simulationPolicy(CellPolicies.newRandomFeasible(actionTemplate,environment))
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();

        monteCarloTreeCreator=MonteCarloTreeCreator.<CellVariables, Integer>builder()
                .environment(environment)
                .startState(StateCell.newStateFromSoCTempAndTime(SOC_INIT,TEMPERATURE_INIT,TIME_INIT))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @Test
    public void whenManySimulation_thenSomeShouldGiveFailingResults() {
        MonteCarloSimulator<CellVariables, Integer> simulator=new MonteCarloSimulator<>(environment,settings);
        SimulationResults simulationResults=
                simulator.simulate(StateCell.newStateFromSoCTempAndTime(SOC_INIT,TEMPERATURE_INIT,TIME_INIT));

        Assert.assertFalse(simulationResults.areAllSimulationsTerminalFail());
    }

    @SneakyThrows
    @Test
    public void doMonteCarloAndPlot() {
        NodeWithChildrenInterface<CellVariables, Integer> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        doPlotting(nodeRoot);
    }

    private void doPrinting(NodeWithChildrenInterface<CellVariables, Integer> nodeRoot) {
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());
        TreeInfoHelper<CellVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);
        System.out.println("tih.getBestPath() = " + tih.getBestPath());

        // nodeRoot.printTree();

    }

    private void doPlotting(NodeWithChildrenInterface<CellVariables, Integer> nodeRoot) {
        TreeInfoHelper<CellVariables, Integer> tih = new TreeInfoHelper<>(nodeRoot,settings);

        List<ActionInterface <Integer>> bestActions=tih.getActionsOnBestPath();

        System.out.println("bestActions = " + bestActions);
        List<Integer> actionValues=bestActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        CellSimulator simulator=new CellSimulator((EnvironmentCell) environment);
        List<EnvironmentCell.CellResults> resultsList=simulator.simulate(actionValues,
                StateCell.newStateFromSoCTempAndTime(SOC_INIT,TEMPERATURE_INIT,TIME_INIT),
                (ActionCell) actionTemplate);
        CellResultsPlotter plotter=new CellResultsPlotter("Hello", TIME_OUT);
        plotter.plot(resultsList);
    }


}
