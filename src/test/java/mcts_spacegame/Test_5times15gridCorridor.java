package mcts_spacegame;

import common.MathUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.NodeValueMemory;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.node_models.NodeInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class Test_5times15gridCorridor {
    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;  //important
    private static final int MAX_TREE_DEPTH = 15;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 20;
    private static final double DELTA = 0.5;
    private static final double VALUE_6 = 6;
    private static final double VALUE_3 = 3;
    private static final double VALUE_0 = 0;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;
    NodeValueMemory<ShipVariables> memory;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15GridCorridor();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        memory=NodeValueMemory.newEmpty();
        memory.write(StateShip.newStateFromXY(14,0),VALUE_0);
        memory.write(StateShip.newStateFromXY(14,2),VALUE_3);
        memory.write(StateShip.newStateFromXY(14,4),VALUE_6);

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,2))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .memory(memory)
                .build();
    }

    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
        System.out.println("memory = " + memory);
    }

    @Test public void moveFromX13Y4IntoGoalWithHighValue() {
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(StateShip.newStateFromXY(13,4));
        boolean any6 = anySimulationHasReturn6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    private boolean anySimulationHasReturn6(SimulationResults simulationResults) {
        return simulationResults.getResults().stream().map(r -> r.singleReturn).anyMatch(v -> MathUtils.isZero(v - VALUE_6));
    }

    @Test public void moveFromX9Y4IntoGoalWithHighValue() {
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(StateShip.newStateFromXY(5,4));
        boolean any6 = anySimulationHasReturn6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2GivesMoveNorth() {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2ManyTimes() {
        for (int i = 0; i < 10 ; i++) {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,4));
        }
    }

    @SneakyThrows
    @Test
    public void HighBonusInX14Y0FavorsSouthRoute() {
        EnvironmentShip env=(EnvironmentShip) monteCarloTreeCreator.getEnvironment();
        SpaceGrid spaceGrid=env.getSpaceGrid();
        SpaceCell cell=spaceGrid.getCell(14,0).orElseThrow();
        cell.bonus=10;

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,0));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,0));
    }

    @SneakyThrows
    @Test
    public void iterateFromX10Y4WithNoSimulations() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(10,4));

        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,4));

        Optional<NodeInterface<ShipVariables, ShipActionSet> > node=tih.getNodeReachedForActions(Collections.singletonList(ActionShip.newStill()));
        System.out.println("node = " + node);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));
        Assert.assertEquals(VALUE_6,node.orElseThrow().getActionValue(ActionShip.newStill()), DELTA);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithNoSimulations() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));


        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=tih.getNodeReachedForActions(Arrays.asList(ActionShip.newUp(), ActionShip.newUp()));
        System.out.println("node = " + node);
        Assert.assertEquals(VALUE_6,node.orElseThrow().getActionValue(ActionShip.newStill()), DELTA);
    }


    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithNoSimulationsAndLowExplorationGivesSubOptimalPath() {

        settings=settingsForNoSimulations();
        settings.setCoefficientExploitationExploration(1);
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,2));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,2));


        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                tih.getNodeReachedForActions(Arrays.asList(ActionShip.newStill(),ActionShip.newStill()));
        System.out.println("node = " + node);
        Assert.assertEquals(VALUE_3,node.orElseThrow().getActionValue(ActionShip.newStill()), DELTA);
    }



    private MonteCarloTreeCreator<ShipVariables, ShipActionSet> treeCreator(StateShip state) {
        return MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(memory)
                .actionTemplate(actionTemplate)
                .build();
    }

    private MonteCarloSettings<ShipVariables, ShipActionSet> settingsForNoSimulations() {
        return MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(1000)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
    }

    private void assertStateIsOnBestPath(TreeInfoHelper<ShipVariables, ShipActionSet> tih, StateShip state) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
