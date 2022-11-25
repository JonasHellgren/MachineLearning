package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeSelector {

    private static final double DELTA = 0.1;
    NodeInterface nodeRoot;
    NodeInterface chUp ;
    NodeInterface chStill;
    NodeInterface chDown;
    private static final double SIM_RES = -1d;

    @Before
    public void init() {
        nodeRoot = NodeInterface.newNode(new State(0, 0), Action.notApplicable);
        chUp = NodeInterface.newNode(new State(1, 1),Action.up);
        chStill = NodeInterface.newNode(new State(1, 0),Action.still);
        chDown = NodeInterface.newTerminalFail(new State(1, 0),Action.down); //terminal
        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
    }

    @Test public void sameValueFewVisitsGivesBetterUct() {
        double v=1;
        int nParent=10;
        NodeSelector ns=new NodeSelector(nodeRoot);
        double uctFewVisits=ns.calcUct(v,nParent,1);
        double uctManyVisits=ns.calcUct(v,nParent,5);
        System.out.println("uctFewVisits = " + uctFewVisits);
        System.out.println("uctManyVisits = " + uctManyVisits);
        Assert.assertTrue(uctFewVisits>uctManyVisits);
    }

    @Test public void sameNofVisitsHighValueGivesBetterUct() {
        int nParent=10, nofVisits=5;
        NodeSelector ns=new NodeSelector(nodeRoot);
        double uctLowValue=ns.calcUct(1,nParent,nofVisits);
        double uctHighValue=ns.calcUct(5,nParent,nofVisits);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue);
    }

    @Test public void uctZeroVisitsGivesUCTMax() {
        int nParent=10, nofVisits=0;
        NodeSelector ns=new NodeSelector(nodeRoot);
        double uctZeroVisits=ns.calcUct(1,nParent,nofVisits);
        System.out.println("uctZeroVisits = " + uctZeroVisits);
        Assert.assertEquals(NodeSelector.UCT_MAX,uctZeroVisits, DELTA);
    }


    @Test
    public void upCostsStillFreeDownBad() {
        addExperience(nodeRoot,Action.up,-Environment.MOVE_COST+SIM_RES);
        addExperience(nodeRoot,Action.still,-Environment.STILL_COST+SIM_RES);
        addExperience(nodeRoot,Action.down,-Environment.CRASH_COST+SIM_RES);

        NodeSelector ns=new NodeSelector(nodeRoot);
        NodeInterface nodeFound=ns.findNode();

        System.out.println("nodeFound = " + nodeFound);
        ns.getNodesFromRootToSelected().forEach(System.out::println);

        Assert.assertEquals(2,ns.getNodesFromRootToSelected().size());
        Assert.assertTrue(nodeFound.equals(chStill));

        nodeRoot.printTree();

    }

    private void addExperience(NodeInterface node, Action action, double G) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G,action);
    }

}
