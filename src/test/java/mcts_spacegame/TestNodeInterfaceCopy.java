package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeNotTerminal;
import mcts_spacegame.models_mcts_nodes.NodeTerminalFail;
import mcts_spacegame.models_mcts_nodes.NodeTerminalNotFail;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Test;

public class TestNodeInterfaceCopy {


    private static final double DELTA = 0.1;

    @Test
    public void testNodeNotTerminal() {
        NodeNotTerminal node=new NodeNotTerminal(StateShip.newStateFromXY(0,0), ShipAction.still);
        node.increaseNofVisits();
        node.increaseNofActionSelections(ShipAction.up);
        node.saveRewardForAction(ShipAction.up,10);
        node.addChildNode(new NodeTerminalNotFail(StateShip.newStateFromXY(1,0), ShipAction.still));

        NodeNotTerminal clone= (NodeNotTerminal) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);


        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getNofActionSelections(ShipAction.up),clone.getNofActionSelections(ShipAction.up));
        Assert.assertEquals(node.restoreRewardForAction(ShipAction.up),clone.restoreRewardForAction(ShipAction.up), DELTA);
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

    @Test
    public void testNodeTerminalFail() {
        NodeTerminalFail node=new NodeTerminalFail(StateShip.newStateFromXY(0,0), ShipAction.still);
        node.setDepth(5);
        node.increaseNofVisits();
        node.increaseNofActionSelections(ShipAction.up);
        node.saveRewardForAction(ShipAction.up,10);
        NodeTerminalFail clone= (NodeTerminalFail) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);

        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getAction(),clone.getAction());
        Assert.assertEquals(node.getDepth(),clone.getDepth());
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

}
