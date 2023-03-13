package mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
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
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
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
    public void whenStartingFrommX0Y0_then11And52IsOnBestPath() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        SpaceGameTestHelper.doPrinting(tih,nodeRoot);
        Assert.assertTrue(isOnBestPath(1,1,tih));
        Assert.assertTrue(isOnBestPath(5,2,tih));
    }

    @SneakyThrows
    @Test(expected = StartStateIsTrapException.class)
    public void whenStartingFrommX2Y0_then11And52IsNotOnBestPath() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2,0));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        SpaceGameTestHelper.doPrinting(tih,nodeRoot);
        Assert.assertFalse(isOnBestPath(1,1,tih));
        Assert.assertFalse(isOnBestPath(5,2,tih));
        Assert.assertEquals(1,tih.getBestPath().size());
    }

    @SneakyThrows
    @Test
    public void whenStartingFrommX1Y1_then20IsVisited() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(1,1));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        SpaceGameTestHelper.doPrinting(tih,nodeRoot);

        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(2,0)));
    }

    @SneakyThrows
    @Test public void whenMaxTreeDepth_thenDepth3() {
        settings.setMaxTreeDepth(3);

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        SpaceGameTestHelper.doPrinting(tih,nodeRoot);
        System.out.println("tih.maxDepth() = " + tih.maxDepth());
        Assert.assertEquals(3,tih.maxDepth());
    }


    private boolean isOnBestPath(int x, int y, TreeInfoHelper<ShipVariables, ShipActionSet> tih) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11 =
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(x,y));
        return node11.isPresent();
    }



}
