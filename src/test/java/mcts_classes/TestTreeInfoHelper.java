package mcts_classes;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestTreeInfoHelper {
    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot;
    List<ActionInterface<ShipActionSet>> actionsToSelected;
    ActionInterface<ShipActionSet> actionInSelected;
    List<ActionInterface<ShipActionSet>> actions;
    TreeInfoHelper<ShipVariables, ShipActionSet> tih;
    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();
        StateShip rootState=StateShip.newStateFromXY(0,0);
        actionsToSelected= Arrays.asList(ActionShip.newUp(), ActionShip.newDown());
        actionInSelected=ActionShip.newStill();
        actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        nodeRoot= createMCTSTree(actions,rootState);
        tih=new TreeInfoHelper<>(nodeRoot, settings);

    }

    @Test
    public void whenActionsUpAndDown_thenNodeSelectedIsX2Y0() {
        NodeInterface<ShipVariables, ShipActionSet> node=tih.getNodeReachedForActions(actionsToSelected).get();
        System.out.println("node = " + node);
        Assert.assertTrue(node.getName().contains("x=2"));
        Assert.assertTrue(node.getName().contains("y=0"));
    }

    @Test public void whenRestoreRewardOfStillInX2Y0_thenIsBad() {
        NodeInterface<ShipVariables, ShipActionSet> node=tih.getNodeReachedForActions(actionsToSelected).get();
        System.out.println("node = " + node);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeCasted=(NodeWithChildrenInterface<ShipVariables, ShipActionSet>) node;

        Assert.assertEquals(-EnvironmentShip.CRASH_COST,nodeCasted.restoreRewardForAction(ActionShip.newStill()), DELTA_BIG);
    }

    @Test public void whenNofNodesToSelected_thenIs3() {
        List<NodeInterface<ShipVariables, ShipActionSet>> nodes=tih.getNodesOnPathForActions(actionsToSelected).get();

        nodes.forEach(System.out::println);

        Assert.assertEquals(3,nodes.size());
    }

    @Test public void whenNofNodesInTree_thenNofActionsPlus1() {
        int nofNodes=tih.nofNodes();
        nodeRoot.printTree();

        System.out.println("nofNodes = " + nofNodes);
        Assert.assertEquals(actions.size()+1,nofNodes);
    }

    @Test public void whenTestIsStateInAnyNode_thenCorrect() {

        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(0,0)));
        Assert.assertFalse(tih.isStateInAnyNode(StateShip.newStateFromXY(10,0)));

    }

    private NodeWithChildrenInterface<ShipVariables, ShipActionSet> createMCTSTree(List<ActionInterface<ShipActionSet>> actions, StateShip rootState) {

        StateShip state = rootState.copy();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(rootState, ActionShip.newNA());
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ActionInterface<ShipActionSet> a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);

            parent.saveRewardForAction(a, sr.reward);
            NodeWithChildrenInterface<ShipVariables, ShipActionSet> child = NodeInterface.newNotTerminal(sr.newState, a);
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }
        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<ActionInterface<ShipActionSet>> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    @NotNull
    private StepReturnGeneric<ShipVariables> stepAndUpdateState(StateShip pos, ActionInterface<ShipActionSet> a) {
        StepReturnGeneric<ShipVariables> sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }



}
