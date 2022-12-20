package mcts_classes;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.EnvironmentGenericInterface;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.*;
import mcts_spacegame.policies_action.SimulationPolicyInterface;
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
    NodeInterface<ShipVariables, ShipActionSet> nodeRoot;
    List<ActionInterface<ShipActionSet>> actionsToSelected;
    ActionInterface<ShipActionSet> actionInSelected;
    List<ActionInterface<ShipActionSet>> actions;
    TreeInfoHelper<ShipVariables, ShipActionSet> tih;
    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(SimulationPolicyInterface.newAlwaysStill())
                .simulationPolicy(SimulationPolicyInterface.newMostlyStill())
                .build();
        StateShip rootState=StateShip.newStateFromXY(0,0);
        actionsToSelected= Arrays.asList(ActionShip.newUp(), ActionShip.newDown());
        actionInSelected=ActionShip.newStill();
        actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        nodeRoot= createMCTSTree(actions,rootState);
        tih=new TreeInfoHelper<>(nodeRoot, settings);

    }

    @Test
    public void nodeSelectedIsX2Y0() {
        NodeInterface<ShipVariables, ShipActionSet> node=tih.getNodeReachedForActions(actionsToSelected).get();
        System.out.println("node = " + node);
        Assert.assertTrue(node.getName().contains("x=2"));
        Assert.assertTrue(node.getName().contains("y=0"));
    }

    @Test public void rewardOfStillInX2Y0IsBad() {
        NodeInterface<ShipVariables, ShipActionSet> node=tih.getNodeReachedForActions(actionsToSelected).get();
        System.out.println("node = " + node);

        Assert.assertEquals(-EnvironmentShip.CRASH_COST,node.restoreRewardForAction(ActionShip.newStill()), DELTA_BIG);
    }

    @Test public void nofNodesToSelectedIs2() {
        List<NodeInterface<ShipVariables, ShipActionSet>> nodes=tih.getNodesOnPathForActions(actionsToSelected).get();

        nodes.forEach(System.out::println);

        Assert.assertEquals(3,nodes.size());
    }

    @Test public void testNofNodesInTree() {
        int nofNodes=tih.nofNodesInTree();
        nodeRoot.printTree();

        System.out.println("nofNodes = " + nofNodes);
        Assert.assertEquals(actions.size()+1,nofNodes);
    }

    @Test public void testIsStateInAnyNode() {

        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(0,0)));
        Assert.assertFalse(tih.isStateInAnyNode(StateShip.newStateFromXY(10,0)));

    }

    private NodeInterface<ShipVariables, ShipActionSet> createMCTSTree(List<ActionInterface<ShipActionSet>> actions, StateShip rootState) {

        StateShip state = rootState.copy();
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(rootState, ActionShip.newNA());
        NodeInterface<ShipVariables, ShipActionSet> parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ActionInterface<ShipActionSet> a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);

            parent.saveRewardForAction(a, sr.reward);
            NodeInterface<ShipVariables, ShipActionSet> child = NodeInterface.newNotTerminal(sr.newState, a);
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
