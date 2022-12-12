package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.model_mcts.SelectedToTerminalFailConverter;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeTerminalFail;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestSelectedToTerminalFailConverter {

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
    public void testMakeSelectedTerminal() {
        State rootState=new State(0,0);
        List<Action> actionsToSelected= Arrays.asList(Action.still,Action.still);
        Action actionInSelected=Action.down;
        List<Action> actions = Action.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        Optional<NodeInterface> nodeSelected=tih.getNodeReachedForActions(actionsToSelected);

        bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .build();

        nodeRoot.printTree();
        tih.getNodesOnPathForActions(actions).get().forEach(System.out::println);

        SelectedToTerminalFailConverter stc=new SelectedToTerminalFailConverter(nodeRoot,actionsToSelected);
        //NodeInterface nodeSelected = tih.getNodeReachedForActions(actionsToSelected).orElseThrow();
        stc.makeSelectedTerminal(nodeSelected.orElseThrow());

        nodeRoot.printTree();
        tih.getNodesOnPathForActions(actionsToSelected).get().forEach(System.out::println);

        Optional<NodeInterface> nodeSelected2=tih.getNodeReachedForActions(actionsToSelected);

        System.out.println("nodeSelected.orElseThrow() = " + nodeSelected.orElseThrow());
        System.out.println("nodeSelected2.orElseThrow() = " + nodeSelected2.orElseThrow());

        Assert.assertFalse(nodeSelected.orElseThrow() instanceof NodeTerminalFail);
        Assert.assertTrue(nodeSelected2.orElseThrow() instanceof NodeTerminalFail);

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
