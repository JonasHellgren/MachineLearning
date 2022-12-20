package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.domains.models_space.EnvironmentShip;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.classes.MonteCarloSettings;
import mcts_spacegame.classes.MonteCarloTreeCreator;
import mcts_spacegame.classes.NodeValueMemory;
import mcts_spacegame.nodes.NodeInterface;
import mcts_spacegame.domains.models_space.*;
import mcts_spacegame.domains.models_space.ShipPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

public class Test_5times15grid {

    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important
    private static final int MAX_TREE_DEPTH = 5;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;
    private static final double DELTA = 0.5;
    private static final double VALUE_6 = 6;
    private static final double VALUE_3 = 3;
    private static final double VALUE_0 = 0;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentShip environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    NodeValueMemory<ShipVariables> memory;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .weightReturnsSteps(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        memory=NodeValueMemory.newEmpty();
        memory.write(StateShip.newStateFromXY(14,0),VALUE_0);
        memory.write(StateShip.newStateFromXY(14,2),VALUE_3);
        memory.write(StateShip.newStateFromXY(14,4),VALUE_6);

        createCreator(StateShip.newStateFromXY(0, 0));
    }



    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
        System.out.println("memory = " + memory);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2() {
        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2NoSimulations() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(10)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        System.out.println("memory = " + memory);
        createCreator(StateShip.newStateFromXY(0, 2));
       // createCreator(State.newState(14, 4));

        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
      //  TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
      //  assertStateIsOnBestPath(tih,State.newState(13,4));
    }

    @SneakyThrows
    @Test
    @Ignore
    public void iterateFromX0Y2WithNoSimulationsAndRestrictedActionSetAfterDepth3() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> (a.x<=3) ? ShipActionSet.applicableActions().size():1)
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(100000)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        System.out.println("memory = " + memory);
        createCreator(StateShip.newStateFromXY(2, 2));

        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX10Y2WithSimulationsAndSteps() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(100)
                .nofSimulationsPerNode(100)
                .coefficientExploitationExploration(100)
                .build();
        System.out.println("memory = " + memory);
        createCreator(StateShip.newStateFromXY(10, 2));

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithSimulationsAndSteps() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(10_000)
                .nofSimulationsPerNode(10)
                .coefficientExploitationExploration(1)
                .build();
        System.out.println("memory = " + memory);
        createCreator(StateShip.newStateFromXY(0, 2));

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(10,4));
    }

    private void createCreator(StateShip state) {
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(memory)
                .actionTemplate(actionTemplate)
                .build();
    }


    private void assertStateIsOnBestPath(TreeInfoHelper<ShipVariables, ShipActionSet> tih, StateShip state) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
