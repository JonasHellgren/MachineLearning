package mcts_spacegame;

import common.MathUtils;
import lombok.SneakyThrows;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.model_mcts.NodeValueMemory;
import mcts_spacegame.model_mcts.SimulationResults;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
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

    MonteCarloTreeCreator monteCarloTreeCreator;
    EnvironmentShip environment;
    MonteCarloSettings settings;
    NodeValueMemory memory;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15GridCorridor();
        environment = new EnvironmentShip(spaceGrid);
        settings= MonteCarloSettings.builder()
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

        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,2))
                .monteCarloSettings(settings)
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
        boolean any6 = anySimulationHasValue6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    private boolean anySimulationHasValue6(SimulationResults simulationResults) {
        return simulationResults.getResults().stream().map(r -> r.valueInTerminalState).anyMatch(v -> MathUtils.isZero(v - VALUE_6));
    }

    @Test public void moveFromX9Y4IntoGoalWithHighValue() {
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(StateShip.newStateFromXY(5,4));
        boolean any6 = anySimulationHasValue6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2() {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2ManyTimes() {
        for (int i = 0; i < 10 ; i++) {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,4));
        }
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2MemoryFavorsSouthRoute() {
        memory=NodeValueMemory.newEmpty();
        memory.write(StateShip.newStateFromXY(14,0),VALUE_6);
        memory.write(StateShip.newStateFromXY(14,2), VALUE_3);
        memory.write(StateShip.newStateFromXY(14,4), VALUE_0);
        monteCarloTreeCreator.setMemory(memory);


        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,0));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(5,0));
    }

    @SneakyThrows
    @Test
    public void iterateFromX10Y4WithNoSimulations() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(10,4));

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,4));

        Optional<NodeInterface> node=tih.getNodeReachedForActions(Collections.singletonList(ShipAction.still));
        System.out.println("node = " + node);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));
        Assert.assertEquals(VALUE_6,node.orElseThrow().getActionValue(ShipAction.still), DELTA);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithNoSimulations() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,4));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));


        Optional<NodeInterface> node=tih.getNodeReachedForActions(Arrays.asList(ShipAction.up, ShipAction.up));
        System.out.println("node = " + node);
        Assert.assertEquals(VALUE_6,node.orElseThrow().getActionValue(ShipAction.still), DELTA);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithNoSimulationsHalveMemoryValueWeight() {

        settings=settingsForNoSimulations();
        settings.setWeightMemoryValue(0.5);
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,2));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,2));


        Optional<NodeInterface> node=tih.getNodeReachedForActions(Arrays.asList(ShipAction.still, ShipAction.still));
        System.out.println("node = " + node);
        Assert.assertEquals(VALUE_3/2,node.orElseThrow().getActionValue(ShipAction.still), DELTA);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithNoSimulationsAndLowExplorationGivesSubOptimalPath() {

        settings=settingsForNoSimulations();
        settings.setCoefficientExploitationExploration(1);
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,2));
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,2));


        Optional<NodeInterface> node=tih.getNodeReachedForActions(Arrays.asList(ShipAction.still, ShipAction.still));
        System.out.println("node = " + node);
        Assert.assertEquals(VALUE_3,node.orElseThrow().getActionValue(ShipAction.still), DELTA);
    }



    private MonteCarloTreeCreator treeCreator(StateShip state) {
        return MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(memory)
                .build();
    }

    private MonteCarloSettings settingsForNoSimulations() {
        return MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(1000)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
    }

    private void assertStateIsOnBestPath(TreeInfoHelper tih, StateShip state) {
        Optional<NodeInterface> node= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeInterface nodeRoot) {
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
