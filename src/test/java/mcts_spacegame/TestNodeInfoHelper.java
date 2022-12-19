package mcts_spacegame;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestNodeInfoHelper {

    List<NodeInterface> nodes;

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
        NodeInterface node=NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0), ActionShip.newNA());
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherClass() {
        NodeInterface node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0,0), ActionShip.newNA());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherAction() {
        NodeInterface node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0,0), ActionShip.newStill());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherState() {
        NodeInterface node=NodeInterface.newTerminalFail(StateShip.newStateFromXY(10,0), ActionShip.newStill());
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

}
