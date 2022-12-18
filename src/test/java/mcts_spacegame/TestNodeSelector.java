package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.model_mcts.MonteCarloSettings;
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
        nodeRoot = NodeInterface.newNotTerminal(new State(0, 0), ShipAction.notApplicable);
        chUp = NodeInterface.newNotTerminal(new State(1, 1), ShipAction.up);
        chStill = NodeInterface.newNotTerminal(new State(1, 0), ShipAction.still);
        chDown = NodeInterface.newTerminalFail(new State(1, 0), ShipAction.down); //terminal
        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
    }

    @Test public void sameValueFewVisitsGivesBetterUct() {
        double v=1;
        int nParent=10;
        NodeSelector ns=new NodeSelector(nodeRoot, MonteCarloSettings.builder().build());
        double uctFewVisits=ns.calcUct(v,nParent,1);
        double uctManyVisits=ns.calcUct(v,nParent,5);
        System.out.println("uctFewVisits = " + uctFewVisits);
        System.out.println("uctManyVisits = " + uctManyVisits);
        Assert.assertTrue(uctFewVisits>uctManyVisits);
    }

    @Test public void sameNofVisitsHighValueGivesBetterUct() {
        int nParent=10, nofVisits=5;
        NodeSelector ns=new NodeSelector(nodeRoot, MonteCarloSettings.builder().build());
        double uctLowValue=ns.calcUct(1,nParent,nofVisits);
        double uctHighValue=ns.calcUct(5,nParent,nofVisits);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue);
    }

    @Test public void nofVisitsDoesNotAffectZeroC() {
        int nParent=10, nofVisitsSmall=1, nofVisitsBig= (int) 1e5, C=0;
        NodeSelector ns=new NodeSelector(nodeRoot, MonteCarloSettings.builder().build(),C,false);
        double uctLowValue=ns.calcUct(1.0,nParent,nofVisitsSmall); //C=0 gives nofVisitsSmall does not matter
        double uctHighValue=ns.calcUct(1.1,nParent,nofVisitsBig);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue); //
    }

    @Test public void uctZeroVisitsGivesUCTMax() {
        int nParent=10, nofVisits=0;
        NodeSelector ns=new NodeSelector(nodeRoot, MonteCarloSettings.builder().build());
        double uctZeroVisits=ns.calcUct(1,nParent,nofVisits);
        System.out.println("uctZeroVisits = " + uctZeroVisits);
        Assert.assertEquals(NodeSelector.UCT_MAX,uctZeroVisits, DELTA);
    }


    @SneakyThrows
    @Test
    public void upCostsStillFreeDownBad() {
        addExperience(nodeRoot, ShipAction.up,-Environment.MOVE_COST+SIM_RES);
        addExperience(nodeRoot, ShipAction.still,-Environment.STILL_COST+SIM_RES);
        addExperience(nodeRoot, ShipAction.down,-Environment.CRASH_COST+SIM_RES);

        NodeSelector ns=new NodeSelector(nodeRoot, MonteCarloSettings.builder().build());
        NodeInterface nodeFound=ns.select();

        System.out.println("nodeFound = " + nodeFound);
        ns.getNodesFromRootToSelected().forEach(System.out::println);

        Assert.assertEquals(2,ns.getNodesFromRootToSelected().size());
        Assert.assertTrue(nodeFound.equals(chStill));

        nodeRoot.printTree();

    }

    private void addExperience(NodeInterface node, ShipAction action, double G) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G,action,1);
    }

}
