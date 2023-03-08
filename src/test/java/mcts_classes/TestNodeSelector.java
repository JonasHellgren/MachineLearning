package mcts_classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.classes.NodeSelector;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeSelector {

    private static final double DELTA = 0.1;
    NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot;
    NodeInterface<ShipVariables, ShipActionSet> chUp ;
    NodeInterface<ShipVariables, ShipActionSet> chStill;
    NodeInterface<ShipVariables, ShipActionSet> chDown;
    private static final double SIM_RES = -1d;

    MonteCarloSettings<ShipVariables, ShipActionSet> settings;

    @Before
    public void init() {
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
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

    @Test public void whenSameValueFewVisits_thenBetterUct() {
        double v=1;
        int nParent=10;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        double uctFewVisits=ns.calcUct(v,nParent,1);
        double uctManyVisits=ns.calcUct(v,nParent,5);
        System.out.println("uctFewVisits = " + uctFewVisits);
        System.out.println("uctManyVisits = " + uctManyVisits);
        Assert.assertTrue(uctFewVisits>uctManyVisits);
    }

    @Test public void whenSameNofVisitsHighValue_thenBetterUct() {
        int nParent=10, nofVisits=5;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings);
        double uctLowValue=ns.calcUct(1,nParent,nofVisits);
        double uctHighValue=ns.calcUct(5,nParent,nofVisits);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue);
    }

    @Test public void whenZeroC_thenNofVisitsNotAffect() {
        int nParent=10, nofVisitsSmall=1, nofVisitsBig= (int) 1e5, C=0;
        NodeSelector<ShipVariables, ShipActionSet> ns=new NodeSelector<>(nodeRoot, settings,C);
        double uctLowValue=ns.calcUct(1.0,nParent,nofVisitsSmall); //C=0 gives nofVisitsSmall does not matter
        double uctHighValue=ns.calcUct(1.1,nParent,nofVisitsBig);
        System.out.println("uctLowValue = " + uctLowValue+", uctHighValue = " + uctHighValue);
        Assert.assertTrue(uctHighValue>uctLowValue); //
    }

    @Test public void whenUctZeroVisits_thenUCTMax() {
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

    private void addExperience(NodeWithChildrenInterface<ShipVariables, ShipActionSet> node,
                               ActionInterface<ShipActionSet> action, double G) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G,action,1,1);
    }

}
