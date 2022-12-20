package mcts_classes;

import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestNodeInfoHelper {

    List<NodeInterface<ShipVariables, ShipActionSet>> nodes;

    @Before
    public void init() {
        nodes=new ArrayList<>();
        nodes.add(NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0), ActionShip.newNA()));
        nodes.add(NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,0), ActionShip.newStill()));
        nodes.add(NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,0), ActionShip.newStill()));
        nodes.add(NodeInterface.newNotTerminal(StateShip.newStateFromXY(3,0), ActionShip.newStill()));
    }

    @Test
    public void nodeWithX0Y0IsPresent() {
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingStateVariables(nodes,StateShip.newStateFromXY(0,0)).isPresent());
    }

    @Test
    public void nodeWithX10Y0IsNotPresent() {
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingStateVariables(nodes,StateShip.newStateFromXY(10,0)).isPresent());
    }

    @Test
    public void findNodeMatchingExistingNode() {
        NodeInterface<ShipVariables, ShipActionSet> node=NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0), ActionShip.newNA());
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherClass() {
        NodeInterface<ShipVariables, ShipActionSet> node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0,0), ActionShip.newNA());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherAction() {
        NodeInterface<ShipVariables, ShipActionSet> node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0,0), ActionShip.newStill());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherState() {
        NodeInterface<ShipVariables, ShipActionSet> node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(10,0), ActionShip.newStill());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

}
