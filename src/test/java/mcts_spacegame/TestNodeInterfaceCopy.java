package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeNotTerminal;
import mcts_spacegame.models_mcts_nodes.NodeTerminalFail;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Test;

public class TestNodeInterfaceCopy {


    private static final double DELTA = 0.1;

    @Test
    public void testNodeNotTerminal() {
        NodeNotTerminal node=new NodeNotTerminal(State.newState(0,0), Action.still);
        node.increaseNofVisits();
        node.increaseNofActionSelections(Action.up);
        node.saveRewardForAction(Action.up,10);
        NodeNotTerminal clone= (NodeNotTerminal) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);

        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getNofActionSelections(Action.up),clone.getNofActionSelections(Action.up));
        Assert.assertEquals(node.restoreRewardForAction(Action.up),clone.restoreRewardForAction(Action.up), DELTA);
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

    @Test
    public void testNodeTerminalFail() {
        NodeTerminalFail node=new NodeTerminalFail(State.newState(0,0), Action.still);
        node.setDepth(5);
        node.increaseNofVisits();
        node.increaseNofActionSelections(Action.up);
        node.saveRewardForAction(Action.up,10);
        NodeTerminalFail clone= (NodeTerminalFail) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);

        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getAction(),clone.getAction());
        Assert.assertEquals(node.getDepth(),clone.getDepth());
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

}
