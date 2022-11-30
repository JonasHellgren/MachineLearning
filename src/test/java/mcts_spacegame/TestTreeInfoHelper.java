package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestTreeInfoHelper {
    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    Environment environment;
    NodeInterface nodeRoot;
    List<Action> actionsToSelected;
    Action actionInSelected;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);

        State rootState=new State(0,0);
        actionsToSelected= Arrays.asList(Action.up,Action.down);
        actionInSelected=Action.still;
        List<Action> actions = Action.getAllActions(actionsToSelected, actionInSelected);
        nodeRoot= createMCTSTree(actions,rootState);
    }

    @Test
    public void nodeSelectedIsX2Y0() {
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        NodeInterface node=tih.getNodeReachedForActions(actionsToSelected).get();
        System.out.println("node = " + node);
        Assert.assertTrue(node.getName().contains("x=2"));
        Assert.assertTrue(node.getName().contains("y=0"));
    }

    @Test public void rewardOfStillInX2Y0IsBad() {
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        NodeInterface node=tih.getNodeReachedForActions(actionsToSelected).get();
        Assert.assertEquals(-Environment.CRASH_COST,node.loadRewardForAction(Action.still), DELTA_BIG);
    }

    @Test public void nofNodesToSelectedIs2() {
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        List<NodeInterface> nodes=tih.getNodesOnPathForActions(actionsToSelected).get();

        nodes.forEach(System.out::println);

        Assert.assertEquals(3,nodes.size());
    }

    private NodeInterface createMCTSTree(List<Action> actions, State rootState) {

        State state = rootState.copy();
        NodeInterface nodeRoot = NodeInterface.newNotTerminal(rootState, Action.notApplicable);
        NodeInterface parent = nodeRoot;
        int nofAddedChilds = 0;
        for (Action a : actions) {
            StepReturn sr = stepAndUpdateState(state, a);

            parent.saveRewardForAction(a, sr.reward);
            NodeInterface child = NodeInterface.newNotTerminal(sr.newPosition, a);
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }
        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<Action> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    @NotNull
    private StepReturn stepAndUpdateState(State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }



}
