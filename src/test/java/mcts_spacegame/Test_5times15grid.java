package mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_5times15grid {

    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important
    private static final int MAX_TREE_DEPTH = 5;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;


    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentShip environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
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

        createCreator(StateShip.newStateFromXY(0, 0));
    }



    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2NoSimulations() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(10)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        createCreator(StateShip.newStateFromXY(0, 2));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
    }

    @SneakyThrows
    @Test
  //  @Ignore
    public void iterateFromX0Y2WithNoSimulationsAndRestrictedActionSetAfterDepth3() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> (a.x<=3) ? actionTemplate.applicableActions().size():1)
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(100000)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        createCreator(StateShip.newStateFromXY(2, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(4,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX10Y2WithSimulationsAndSteps() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet> builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(100)
                .nofSimulationsPerNode(100)
                .coefficientExploitationExploration(100)
                .build();
        createCreator(StateShip.newStateFromXY(10, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(13,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2WithSimulationsAndSteps() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(10_000)
                .nofSimulationsPerNode(10)
                .coefficientExploitationExploration(1)
                .build();
        createCreator(StateShip.newStateFromXY(0, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(10,4));
    }

    private void createCreator(StateShip state) {
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }


    private void assertStateIsOnBestPath(TreeInfoHelper<ShipVariables, ShipActionSet> tih, StateShip state) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodes());
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
