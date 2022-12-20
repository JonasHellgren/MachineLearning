package mcts_spacegame;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Test;

public class TestNode {

    private static final double SIM_RES = -1d;
    ActionInterface<ShipActionSet> ACTION_ANY= ActionShip.newStill();

    @Test public void testTypes() {
        NodeInterface<ShipVariables,ShipActionSet> nodeWithChilds=NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> nodeTerminalFail=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> nodeTerminalNoFail=NodeInterface.newTerminalNotFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);

        Assert.assertTrue(nodeWithChilds.isNotTerminal());
        Assert.assertTrue(nodeTerminalFail.isTerminalFail());
        Assert.assertTrue(nodeTerminalNoFail.isTerminalNoFail());
    }

    @Test
    public void oneNoChildNode() {
        NodeInterface<ShipVariables,ShipActionSet> node = NodeInterface.newTerminalFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        node.printTree();

        Assert.assertEquals(0, node.nofChildNodes());
    }

    @Test
    public void rootWithThreeChilds() {
        NodeInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chUp = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 1),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chStill = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chDown = NodeInterface.newTerminalFail(StateShip.newStateFromXY(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
        nodeRoot.printTree();
    }

    @Test
    public void rootWithGrandChilds() {
        NodeInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chUp1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 1),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chStill1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chDown1 = NodeInterface.newTerminalFail(StateShip.newStateFromXY(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp1);
        nodeRoot.addChildNode(chStill1);
        nodeRoot.addChildNode(chDown1);

        NodeInterface<ShipVariables,ShipActionSet> chUp2 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(2, 1),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chStill2 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(2, 0),ACTION_ANY);
        chStill1.addChildNode(chUp2);
        chStill1.addChildNode(chStill2);

        nodeRoot.printTree();
    }

    @Test
    public void imitateStillActionFromRootFollowedByExpansion() {
        NodeInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0), ActionShip.newNA());
//        ShipActionREMOVE action = ShipActionREMOVE.still;
     //   double treeRewards = Environment.STILL_COST;
      //  double simRewards = SIM_RES;
        NodeInterface<ShipVariables,ShipActionSet> chStill1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);

        nodeRoot.addChildNode(chStill1);

        System.out.println("nodeRoot = " + nodeRoot);

    }

}
