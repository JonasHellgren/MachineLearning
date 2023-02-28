package mcts_elevator;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSearchStatistics;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.domains.models_space.StateShip;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestSearch {

    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;
    private static final int NSTEPS_BETWEEN = 50;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;

    @Before
    public void init() {
        environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN));
        StateInterface<VariablesElevator> startStateDummy = StateElevator.newFromVariables(VariablesElevator.builder().build());
        monteCarloTreeCreator=createTreeCreator(startStateDummy);

    }

    @SneakyThrows
    @Test
    public void whenAtBottomWaitingFloor_thenMoveToFloor1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot=monteCarloTreeCreator.run();
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath(nodeRoot);
        somePrinting(nodesOnPath,nodeRoot);
        List<Integer> posList= getVisitedPositions(nodesOnPath);
        Assert.assertTrue(posList.contains(POS_FLOOR_1));

    }

    @SneakyThrows
    @Test
    public void whenAtPos5AndBadSoE_thenMoveToFloor0() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(0.21).pos(5).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot=monteCarloTreeCreator.run();
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath(nodeRoot);
        somePrinting(nodesOnPath,nodeRoot);
        List<Integer> posList= getVisitedPositions(nodesOnPath);
        Assert.assertTrue(posList.contains(POS_FLOOR_0));

    }

    private List<NodeInterface<VariablesElevator, Integer>> getNodesOnPath(NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot) {
        TreeInfoHelper<VariablesElevator, Integer> tih=new TreeInfoHelper<>(nodeRoot,settings);
        return tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow();
    }

    private void somePrinting(List<NodeInterface<VariablesElevator, Integer>> nodesOnPath,NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot) {
        List<Integer> posList= getVisitedPositions(nodesOnPath);
        List<Double> soEListList= getSoEList(nodesOnPath);
        System.out.println("posList = " + posList);
        System.out.println("soEListList = " + soEListList);
        MonteCarloSearchStatistics<VariablesElevator,Integer> stats=monteCarloTreeCreator.getStatistics();
        System.out.println("stats = " + stats);
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);
        System.out.println("nodeRoot value = " + NodeInfoHelper.valueNode(actionTemplate,nodeRoot));

    }

    public  MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);
        settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .isDefensiveBackup(true)
            //    .alphaBackupDefensive(0.5)
                .coefficientMaxAverageReturn(0) //0 <=> average, 1<=>max
                .maxTreeDepth(100)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(1000)
                .weightReturnsSteps(0.0)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(0.001)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    List<Integer>  getVisitedPositions(List<NodeInterface <VariablesElevator, Integer>> nodesOnPath) {
        return nodesOnPath.stream().map(n -> n.getState().getVariables().pos).collect(Collectors.toList());
    }


    List<Double>  getSoEList(List<NodeInterface <VariablesElevator, Integer>> nodesOnPath) {
        return nodesOnPath.stream().map(n -> n.getState().getVariables().SoE).collect(Collectors.toList());
    }

}
