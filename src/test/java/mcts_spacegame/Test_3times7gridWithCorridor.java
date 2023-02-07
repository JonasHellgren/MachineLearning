package mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
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

public class Test_3times7gridWithCorridor {

    private static final int MAX_NOF_ITERATIONS = 500;
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7GridWithTrapCorridor();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(10)
                .build();
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52 = NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    @Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() throws StartStateIsTrapException {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2, 0));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(3, 0)));
        Assert.assertFalse(tih.isStateInAnyNode(StateShip.newStateFromXY(4, 0)));

        doPrinting(tih, nodeRoot);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0NoSimulations() {

        settings.setNofSimulationsPerNode(0);
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52 =
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    private void doPrinting(TreeInfoHelper<ShipVariables, ShipActionSet> tih,
                            NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
