package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;
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
        nodes.add(NodeInterface.newNotTerminal(new State(0,0), ShipAction.notApplicable));
        nodes.add(NodeInterface.newNotTerminal(new State(1,0), ShipAction.still));
        nodes.add(NodeInterface.newNotTerminal(new State(2,0), ShipAction.still));
        nodes.add(NodeInterface.newNotTerminal(new State(3,0), ShipAction.still));
    }

    @Test
    public void nodeWithX0Y0IsPresent() {
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingState(nodes,new State(0,0)).isPresent());
    }

    @Test
    public void nodeWithX10Y0IsNotPresent() {
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingState(nodes,new State(10,0)).isPresent());
    }

    @Test
    public void findNodeMatchingExistingNode() {
        NodeInterface node=NodeInterface.newNotTerminal(new State(0,0), ShipAction.notApplicable);
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherClass() {
        NodeInterface node=NodeInterface.newTerminalFail(new State(0,0), ShipAction.notApplicable);
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherAction() {
        NodeInterface node=NodeInterface.newTerminalFail(new State(0,0), ShipAction.still);
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

    @Test
    public void findNodeMatchingNodeOfOtherState() {
        NodeInterface node=NodeInterface.newTerminalFail(new State(10,0), ShipAction.still);
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingNode(nodes,node).isPresent());
    }

}
