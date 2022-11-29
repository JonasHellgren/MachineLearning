package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestBackupModifier {

    private static final double DELTA = 0.1;
    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    Environment environment;
    BackupModifier bum;
    List<StepReturn> stepReturns;
    StepReturn getStepReturnOfSelected;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);
        stepReturns = new ArrayList<>();
    }

    @Test
    public void testCreatedTree() {
        State rootState = new State(0, 0);
        List<Action> actions = Arrays.asList(Action.up, Action.up, Action.still, Action.still, Action.still, Action.still, Action.still);

        NodeInterface treeRoot = createMCTSTree(actions, rootState, stepReturns);
        treeRoot.printTree();

    }


    @Test
    public void moveDownFromX0Y0ToGetFailState() {
        State rootState = new State(0, 0);

        List<Action> actionsToSelected = Collections.emptyList();
        Action actionInSelected = Action.down;
        List<Action> actions = TreeInfoHelper.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot = createMCTSTree(actions, rootState, stepReturns);
        printLists(actions, stepReturns, nodeRoot);
        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        double valueDown = nodeRoot.getActionValue(Action.down);
        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueDown);
        Assert.assertEquals(-Environment.CRASH_COST, valueDown, DELTA_BIG);

    }



    @Test
    public void moveFromX0Y0Tox6y2GivesTwoMoveCost() {
        State rootState=new State(0,0);
        List<Action> actionsToSelected= Arrays.asList(Action.up,Action.up,Action.still,Action.still,Action.still,Action.still);
        Action actionInSelected=Action.still;

        List<Action> actions = TreeInfoHelper.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        double upAtRoot=nodeRoot.getActionValue(Action.up);
        double stillAfterObstacles=tih.getNodesOnPathForActions(actionsToSelected).orElseThrow().get(3).getActionValue(Action.still);
        System.out.println("upAtRoot = " + upAtRoot);
        System.out.println("stillAfterObstacles = " + stillAfterObstacles);

        Assert.assertEquals(-Environment.MOVE_COST *2,upAtRoot, DELTA);
        Assert.assertEquals(-Environment.STILL_COST,stillAfterObstacles, DELTA);

    }



    @Test
    public void moveFromX0Y0ToX3Y0ToGetFailStateByObstacleCrash() {
        State rootState=new State(0,0);
        List<Action> actionsToSelected= Arrays.asList(Action.up,Action.down);
        Action actionInSelected=Action.still;
        List<Action> actions = TreeInfoHelper.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        double valueUpRoot=nodeRoot.getActionValue(Action.up);
        double valueStillSelected=tih.getValueForActionInNode(actionsToSelected,Action.still).get();
        System.out.println("valueUpRoot = " + valueUpRoot);
        System.out.println("valueStillSelected = " + valueStillSelected);
        Assert.assertEquals(-0,valueUpRoot, DELTA);
        Assert.assertEquals(-Environment.CRASH_COST,valueStillSelected, DELTA_BIG);

    }



    @Test
    public void moveFromX0Y0ToX2Y0AndDoAllMovesToShowGetIntoTrap() {
        State rootState=new State(0,0);
        List<Action> actionsToSelected= Arrays.asList(Action.up,Action.down);
        Action actionInSelected=Action.still;
        List<Action> actions = TreeInfoHelper.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();
        bum.backup();

        State state = getState(rootState, actionsToSelected);
        actionInSelected=Action.down;
        updateTreeFromActionInState(actionsToSelected, actionInSelected, nodeRoot, state);

        state = getState(rootState, actionsToSelected);
        actionInSelected=Action.up;
        NodeInterface nodeSelected = updateTreeFromActionInState(actionsToSelected, actionInSelected, nodeRoot, state);


        nodeRoot.printTree();
        System.out.println("nodeSelected = " + nodeSelected);

        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        Optional<Double> valueUp=tih.getValueForActionInNode(actionsToSelected,Action.up);
        Optional<Double> valueStill=tih.getValueForActionInNode(actionsToSelected,Action.still);
        Optional<Double> valueDown=tih.getValueForActionInNode(actionsToSelected,Action.down);

        Assert.assertEquals(-0,nodeRoot.getActionValue(Action.up), DELTA);
        Assert.assertEquals(-Environment.CRASH_COST,valueUp.get(), DELTA_BIG);
        Assert.assertEquals(-Environment.CRASH_COST,valueStill.get(), DELTA_BIG);
        Assert.assertEquals(-Environment.CRASH_COST,valueDown.get(), DELTA_BIG);

    }


    @NotNull
    private NodeInterface updateTreeFromActionInState(List<Action> actionsToSelected,
                                                      Action actionInSelected,
                                                      NodeInterface nodeRoot,
                                                      State state) {
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        getStepReturnOfSelected= environment.step(actionInSelected, state);
        NodeInterface nodeSelected= tih.getNodeReachedForActions(actionsToSelected).get();
        nodeSelected.saveRewardForAction(actionInSelected, getStepReturnOfSelected.reward);
        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();
        bum.backup();
        return nodeSelected;
    }

    private State getState(State rootState, List<Action> actionsToSelected) {
        State state = rootState.copy();
        for (Action a : actionsToSelected) {
            StepReturn sr = stepAndUpdateState(state, a);
        }
        return state;
    }

    private NodeInterface createMCTSTree(List<Action> actions, State rootState, List<StepReturn> stepReturns) {

        stepReturns.clear();
        State state = rootState.copy();
        NodeInterface nodeRoot = NodeInterface.newNotTerminal(rootState, Action.notApplicable);
        NodeInterface parent = nodeRoot;
        int nofAddedChilds = 0;
        for (Action a : actions) {
            StepReturn sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeInterface child = NodeInterface.newNotTerminal(sr.newPosition, a);
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }
        getStepReturnOfSelected = stepReturns.get(stepReturns.size() - 1);
        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<Action> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    private void printLists(List<Action> actions, List<StepReturn> stepReturns, NodeInterface nodeRoot) {
        System.out.println("-----------------------------");
        nodeRoot.printTree();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        tih.getNodesOnPathForActions(actions).get().forEach(System.out::println);

        System.out.println("-----------------------------");
    }

    @NotNull
    private StepReturn stepAndUpdateState(State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }


}
