package mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.NodeValueMemoryHashMap;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.MemoryInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TestMockedMemoryFor5times15Grid {

    private static final int MAX_NOF_ITERATIONS = 20;
    private static final int NOF_SIMULATIONS_PER_NODE = 0;  //important
    private static final int MAX_TREE_DEPTH = 3;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;
    private static final double BONUS_IN_Y4 = 6d;
    private static final double BONUS_IN_Y2 = 3d;
    private static final double BONUS_IN_Y0 = 0d;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentShip environment;
    MemoryInterface<ShipVariables> nodeValueMemory;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new EnvironmentShip(spaceGrid);
        nodeValueMemory = NodeValueMemoryHashMap.newEmpty();
        nodeValueMemory.write(StateShip.newStateFromXY(2, 4), BONUS_IN_Y4);
        nodeValueMemory.write(StateShip.newStateFromXY(2, 2), BONUS_IN_Y2);
        nodeValueMemory.write(StateShip.newStateFromXY(2, 0), BONUS_IN_Y0);

        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())  //not relevant but needs to be defined
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .weightReturnsSteps(0)
                .weightMemoryValue(1)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        actionTemplate = new ActionShip(ShipActionSet.notApplicable); //whatever action

        createCreator(StateShip.newStateFromXY(0, 2));
    }

    @Test
    public void nodeValueMemory() {
        System.out.println("nodeValueMemory = " + nodeValueMemory);
    }

    @SneakyThrows
    @Test public void shallGoNorthDueToHighMemoryValueInX2Y4() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        assertStateIsOnBestPath(nodeRoot, StateShip.newStateFromXY(2,4));
    }

    private void createCreator(StateShip state) {
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(nodeValueMemory)
                .actionTemplate(actionTemplate)
                .build();
    }

    private void assertStateIsOnBestPath(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot, StateShip state) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
