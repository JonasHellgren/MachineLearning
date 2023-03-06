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

public class Test_3times7grid {
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();
        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("monteCarloTreeCreator.getActionsToSelected() = " + monteCarloTreeCreator.getActionsToSelected());

        System.out.println("tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow() =");
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertTrue(node11.isPresent());
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertTrue(node52.isPresent());

    }

    @SneakyThrows
    @Test
    //@Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2,0));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertFalse(node11.isPresent());
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertFalse(node52.isPresent());
    }

    @SneakyThrows
    @Test
    public void iterateFromX1Y1() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(1,1));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);

        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(2,0)));
    }

    @SneakyThrows
    @Test public void maxTreeDepth() {
        settings.setMaxTreeDepth(3);

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);
        System.out.println("tih.maxDepth() = " + tih.maxDepth());

        Assert.assertEquals(3,tih.maxDepth());
    }


    private void doPrinting(TreeInfoHelper<ShipVariables, ShipActionSet> tih,NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
