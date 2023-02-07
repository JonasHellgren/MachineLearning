package mcts_classes;

import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeNotTerminal;
import monte_carlo_tree_search.node_models.NodeTerminalFail;
import monte_carlo_tree_search.node_models.NodeTerminalNotFail;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.StateShip;
import org.junit.Assert;
import org.junit.Test;

public class TestNodeInterfaceCopy {


    private static final double DELTA = 0.1;

    @Test
    public void testNodeNotTerminal() {
        NodeNotTerminal<ShipVariables, ShipActionSet> node=new NodeNotTerminal<>(StateShip.newStateFromXY(0,0), ActionShip.newStill());
        node.increaseNofVisits();
        node.increaseNofActionSelections(ActionShip.newUp());
        node.saveRewardForAction(ActionShip.newUp(),10);
        node.addChildNode(new NodeTerminalNotFail<>(StateShip.newStateFromXY(1, 0), ActionShip.newStill()));

        NodeNotTerminal<ShipVariables, ShipActionSet> clone= (NodeNotTerminal<ShipVariables, ShipActionSet>) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);


        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getNofActionSelections(ActionShip.newUp()),clone.getNofActionSelections(ActionShip.newUp()));
        Assert.assertEquals(node.restoreRewardForAction(ActionShip.newUp()),clone.restoreRewardForAction(ActionShip.newUp()), DELTA);
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

    @Test
    public void testNodeTerminalFail() {
        NodeTerminalFail<ShipVariables, ShipActionSet> node=new NodeTerminalFail<>(StateShip.newStateFromXY(0,0), ActionShip.newStill());
        node.setDepth(5);
      //  node.increaseNofVisits();
      //  node.increaseNofActionSelections(ActionShip.newUp());
      //  node.saveRewardForAction(ActionShip.newUp(),10);
        NodeTerminalFail<ShipVariables, ShipActionSet> clone= (NodeTerminalFail<ShipVariables, ShipActionSet>) NodeInterface.copy(node);

        System.out.println("node = " + node);
        System.out.println("clone = " + clone);

        Assert.assertEquals(node.getNofVisits(),clone.getNofVisits());
        Assert.assertEquals(node.getAction(),clone.getAction());
        Assert.assertEquals(node.getDepth(),clone.getDepth());
        Assert.assertTrue(node.getState().equals(clone.getState()));
    }

}
