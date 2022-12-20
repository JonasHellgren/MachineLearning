package mcts_classes;

import lombok.SneakyThrows;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;
import mcts_spacegame.models_space.ShipPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeSelector {

    private static final double DELTA = 0.1;
    NodeInterface<ShipVariables, ShipActionSet> nodeRoot;
    NodeInterface<ShipVariables, ShipActionSet> chUp ;
    NodeInterface<ShipVariables, ShipActionSet> chStill;
    NodeInterface<ShipVariables, ShipActionSet> chDown;
    private static final double SIM_RES = -1d;

    MonteCarloSettings<ShipVariables, ShipActionSet> settings;

    @Before
    public void init() {
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();

        nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0), ActionShip.newNA());
        chUp = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 1), ActionShip.newUp());
        chStill = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0), ActionShip.newStill());
        chDown = NodeInterface.newTerminalFail(StateShip.newStateFromXY(1, 0), ActionShip.newDown()); //terminal
        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
    }

    @Test public void sameValueFewVisitsGivesBetterUct() {
        double v=1;
        int nParent=10;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        double uctFewVisits=ns.calcUct(v,nParent,1);
        double uctManyVisits=ns.calcUct(v,nParent,5);
        System.out.println("uctFewVisits = " + uctFewVisits);
        System.out.println("uctManyVisits = " + uctManyVisits);
        Assert.assertTrue(uctFewVisits>uctManyVisits);
    }

    @Test public void sameNofVisitsHighValueGivesBetterUct() {
        int nParent=10, nofVisits=5;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        double uctLowValue=ns.calcUct(1,nParent,nofVisits);
        double uctHighValue=ns.calcUct(5,nParent,nofVisits);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue);
    }

    @Test public void nofVisitsDoesNotAffectZeroC() {
        int nParent=10, nofVisitsSmall=1, nofVisitsBig= (int) 1e5, C=0;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings,C,false);
        double uctLowValue=ns.calcUct(1.0,nParent,nofVisitsSmall); //C=0 gives nofVisitsSmall does not matter
        double uctHighValue=ns.calcUct(1.1,nParent,nofVisitsBig);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue); //
    }

    @Test public void uctZeroVisitsGivesUCTMax() {
        int nParent=10, nofVisits=0;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        double uctZeroVisits=ns.calcUct(1,nParent,nofVisits);
        System.out.println("uctZeroVisits = " + uctZeroVisits);
        Assert.assertEquals(NodeSelector.UCT_MAX,uctZeroVisits, DELTA);
    }


    @SneakyThrows
    @Test
    public void upCostsStillFreeDownBad() {
        addExperience(nodeRoot, ActionShip.newUp(),-EnvironmentShip.MOVE_COST+SIM_RES);
        addExperience(nodeRoot, ActionShip.newStill(),-EnvironmentShip.STILL_COST+SIM_RES);
        addExperience(nodeRoot, ActionShip.newDown(),-EnvironmentShip.CRASH_COST+SIM_RES);

        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        NodeInterface<ShipVariables, ShipActionSet> nodeFound=ns.select();

        System.out.println("nodeFound = " + nodeFound);
        ns.getNodesFromRootToSelected().forEach(System.out::println);

        Assert.assertEquals(2,ns.getNodesFromRootToSelected().size());
        Assert.assertTrue(nodeFound.equals(chStill));

        nodeRoot.printTree();

    }

    private void addExperience(NodeInterface<ShipVariables, ShipActionSet> node,
                               ActionInterface<ShipActionSet> action, double G) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G,action,1);
    }

}
