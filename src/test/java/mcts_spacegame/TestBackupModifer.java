package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.model_mcts.BackupModifer;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

public class TestBackupModifer {

    private static final double DELTA = 0.1;
    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    Environment environment;
    BackupModifer bum;
    List<List<StepReturn>> simulationResultsEmpty;

    List<StepReturn> stepReturns;
    StepReturn getStepReturnOfSelected;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new Environment(spaceGrid);

        simulationResultsEmpty=new ArrayList<>(new ArrayList<>());

        stepReturns=new ArrayList<>();

    }

    @Test public void testCreatedTree() {
        State rootState=new State(0,0);
        List<Action> actions= Arrays.asList(Action.up,Action.up,Action.still,Action.still,Action.still,Action.still,Action.still);

        NodeInterface treeRoot= createMCTSTree(actions,rootState,stepReturns);
        treeRoot.printTree();

    }


    @Test
    public void moveDownFromX0Y0ToGetFailState() {
        State rootState=new State(0,0);
        List<Action> actionsToSelected= Collections.emptyList();
        List<Action> actionOnSelected= Collections.singletonList(Action.down);

        List<Action> actions=new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelected);

        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);


        printLists(actions, stepReturns, nodeRoot);

        bum=new BackupModifer(nodeRoot,actionsToSelected,Action.down,getStepReturnOfSelected,simulationResultsEmpty);
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        Optional<NodeInterface> node=nodeRoot.getChild(Action.down);
        double valueDown=nodeRoot.getActionValue(Action.down);

        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueDown);

        Assert.assertEquals(-Environment.CRASH_COST,valueDown, DELTA_BIG);

    }

/*

    @Test
    @Ignore
    public void moveFromX0Y0Tox6y2GivesTwoMoveCost() {
        State rootState=new State(0,0);
        List<Action> actions= Arrays.asList(Action.up,Action.up,Action.still,Action.still,Action.still,Action.still,Action.still);
        List<NodeInterface> nodesFromRootToSelected= createNodesOnPath(actions,rootState,stepReturns);

        printLists(actions, stepReturns, nodesFromRootToSelected);

        bum=new BackupModifer(actions,nodesFromRootToSelected,getStepReturnOfSelected(stepReturns),simulationResultsEmpty);
        bum.backup();

        printLists(actions, stepReturns, nodesFromRootToSelected);
        System.out.println("xxxxxxxxx stepReturns xxxxxxxxxxxxxx");
        stepReturns.forEach(System.out::println);

        double upAtRoot=nodesFromRootToSelected.get(0).getActionValue(Action.up);
        double stillAfterObstacles=nodesFromRootToSelected.get(3).getActionValue(Action.still);
        System.out.println("upAtRoot = " + upAtRoot);
        System.out.println("stillAfterObstacles = " + stillAfterObstacles);

        Assert.assertEquals(-Environment.MOVE_COST *2,upAtRoot, DELTA);
        Assert.assertEquals(-Environment.STILL_COST,stillAfterObstacles, DELTA);

    }


    private StepReturn getStepReturnOfSelected(List<StepReturn> stepReturns) {
        return stepReturns.get(stepReturns.size()-1);
    }

    @Test
    @Ignore
    public void moveFromX0Y0ToX3Y0ToGetFailStateByObstacleCrash() {
        State rootState=new State(0,0);
        List<Action> actions= Arrays.asList(Action.up,Action.down,Action.still);

        List<NodeInterface> nodesFromRootToSelected= createNodesOnPath(actions,rootState,stepReturns);

        printLists(actions, stepReturns, nodesFromRootToSelected);

        bum=new BackupModifer(actions,nodesFromRootToSelected,getStepReturnOfSelected(stepReturns),simulationResultsEmpty);
        bum.backup();
        printLists(actions, stepReturns, nodesFromRootToSelected);

        double valueUpRoot=nodesFromRootToSelected.get(0).getActionValue(Action.up);
        double valueStillSelected=nodesFromRootToSelected.get(2).getActionValue(Action.still);
        System.out.println("valueUpRoot = " + valueUpRoot);
        System.out.println("valueStillSelected = " + valueStillSelected);
        Assert.assertEquals(-0,valueUpRoot, DELTA);
        Assert.assertEquals(-Environment.CRASH_COST,valueStillSelected, DELTA_BIG);

    }

    @Test
    @Ignore
    public void moveFromX0Y0ToX2Y0AndDoAllMovesToShowGetIntoTrap() {
        State rootState=new State(0,0);
        List<Action> actions= Arrays.asList(Action.up,Action.down,Action.still);
        List<NodeInterface> nodesFromRootToSelected= createNodesOnPath(actions,rootState,stepReturns);
        bum=new BackupModifer(actions,nodesFromRootToSelected,getStepReturnOfSelected(stepReturns),simulationResultsEmpty);
        bum.backup();


        State trapState=new State(2,0);
        List<Action> actionsUpInEnd= Arrays.asList(Action.up,Action.down,Action.up);
        nodesFromRootToSelected= createNodesOnPath(actionsUpInEnd,trapState,stepReturns);
        StepReturn srSelected=stepAndUpdateState(trapState, actionsUpInEnd.get(actionsUpInEnd.size()-1));
        bum=new BackupModifer(actions,nodesFromRootToSelected,srSelected,simulationResultsEmpty);
        bum.backup();

        nodesFromRootToSelected.forEach(System.out::println);

        double valueUpRoot=nodesFromRootToSelected.get(0).getActionValue(Action.up);
        double valueStillSelected=nodesFromRootToSelected.get(2).getActionValue(Action.still);
        System.out.println("valueUpRoot = " + valueUpRoot);
        System.out.println("valueStillSelected = " + valueStillSelected);
        Assert.assertEquals(-0,valueUpRoot, DELTA);
        Assert.assertEquals(-Environment.CRASH_COST,valueStillSelected, DELTA_BIG);

    }  */

    private NodeInterface createMCTSTree(List<Action> actions, State rootState, List<StepReturn> stepReturns) {

        stepReturns.clear();
        State state=rootState.copy();
        NodeInterface nodeRoot=NodeInterface.newNotTerminal(rootState,Action.notApplicable);
        NodeInterface parent=nodeRoot;
        int nofAddedChilds=0;
        for (Action a: actions) {
            StepReturn sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeInterface child=NodeInterface.newNotTerminal(sr.newPosition, a);
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent=child;
            nofAddedChilds++;
        }
        getStepReturnOfSelected=stepReturns.get(stepReturns.size()-1);
        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<Action> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    private void printLists(List<Action> actions, List<StepReturn> stepReturns, NodeInterface nodeRoot) {

        System.out.println("-----------------------------");
        actions.forEach(System.out::println);
        nodeRoot.printTree();
       // System.out.println("getStepReturnOfSelected(stepReturns) = " + getStepReturnOfSelected(stepReturns));
        //  stepReturns.forEach(System.out::println);
        System.out.println("-----------------------------");

    }

    @NotNull
    private StepReturn stepAndUpdateState(State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }


}
