package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.model_mcts.NodeInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Test;

public class TestNode {

    private static final double SIM_RES = -1d;
    Action ACTION_ANY=Action.still;

    @Test
    public void oneNoChildNode() {
        NodeInterface node = NodeInterface.newNoChildNode(new State(0, 0),ACTION_ANY);
        node.printTree();

        Assert.assertEquals(0, node.nofChildNodes());
    }

    @Test
    public void rootWithThreeChilds() {
        NodeInterface nodeRoot = NodeInterface.newNode(new State(0, 0),ACTION_ANY);
        NodeInterface chUp = NodeInterface.newNode(new State(1, 1),ACTION_ANY);
        NodeInterface chStill = NodeInterface.newNode(new State(1, 0),ACTION_ANY);
        NodeInterface chDown = NodeInterface.newNoChildNode(new State(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
        nodeRoot.printTree();
    }

    @Test
    public void rootWithGrandChilds() {
        NodeInterface nodeRoot = NodeInterface.newNode(new State(0, 0),ACTION_ANY);
        NodeInterface chUp1 = NodeInterface.newNode(new State(1, 1),ACTION_ANY);
        NodeInterface chStill1 = NodeInterface.newNode(new State(1, 0),ACTION_ANY);
        NodeInterface chDown1 = NodeInterface.newNoChildNode(new State(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp1);
        nodeRoot.addChildNode(chStill1);
        nodeRoot.addChildNode(chDown1);

        NodeInterface chUp2 = NodeInterface.newNode(new State(2, 1),ACTION_ANY);
        NodeInterface chStill2 = NodeInterface.newNode(new State(2, 0),ACTION_ANY);
        chStill1.addChildNode(chUp2);
        chStill1.addChildNode(chStill2);

        nodeRoot.printTree();
    }

    @Test
    public void imitateStillActionFromRootFollowedByExpansion() {
        NodeInterface nodeRoot = NodeInterface.newNode(new State(0, 0),Action.notApplicable);
        Action action = Action.still;
     //   double treeRewards = Environment.STILL_COST;
      //  double simRewards = SIM_RES;
        NodeInterface chStill1 = NodeInterface.newNode(new State(1, 0),ACTION_ANY);

        nodeRoot.expand(chStill1,action);

        System.out.println("nodeRoot = " + nodeRoot);

    }

}
