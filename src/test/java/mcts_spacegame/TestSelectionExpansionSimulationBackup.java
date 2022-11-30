package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.ActionSelector;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.model_mcts.NodeSelector;
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
import java.util.Optional;

public class TestSelectionExpansionSimulationBackup {

    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    Environment environment;
    NodeInterface nodeRoot;
    List<Action> actionsToSelected;
    Action actionInSelected;
    State startState;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);
        startState = new State(0, 0);
        nodeRoot = NodeInterface.newNotTerminal(startState, Action.notApplicable);
    }

    @Test
    public void oneIteration() {
        NodeInterface nodeSelected = select(nodeRoot);
        StepReturn sr = chooseActionAndExpand(nodeSelected);
        //todo simulation
        backPropagate(sr);

        System.out.println("sr = " + sr);
        nodeRoot.printTree();

        double valueUp = nodeRoot.getActionValue(Action.up);
        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueUp);
        Assert.assertEquals(-Environment.MOVE_COST, valueUp, DELTA_BIG);
    }

    @Test
    public void tenIterations() {
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        for (int i = 0; i < 15; i++) {
            NodeInterface nodeSelected = select(nodeRoot);

            StepReturn sr = chooseActionAndExpand(nodeSelected);
            //todo simulation
            backPropagate(sr);
        }

        //System.out.println("xxxxxxxxxxxxxxxxxxxx i = " + i);
        System.out.println("xxxxxxxxxxxxxxxxxxxx ");

        //System.out.println("nodeSelected = " + nodeSelected);
        nodeRoot.printTree();

        List<Action> actions = Arrays.asList(Action.up);
        Optional<NodeInterface> node11=tih.getNodeReachedForActions(actions);
        System.out.println("node11.get() = " + node11.get());

        actions = Arrays.asList(Action.up,Action.down);
        Optional<NodeInterface> node20=tih.getNodeReachedForActions(actions);
        System.out.println("node20.get() = " + node20.get());

        double valueUp = nodeRoot.getActionValue(Action.up);
        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueUp);
        Assert.assertEquals(-Environment.MOVE_COST, valueUp, DELTA_BIG);
    }

    private NodeInterface select(NodeInterface nodeRoot) {
        NodeSelector ns = new NodeSelector(nodeRoot);
        actionsToSelected = ns.getActionsFromRootToSelected();
        return ns.select();
    }

    @NotNull
    private StepReturn chooseActionAndExpand(NodeInterface nodeSelected) {
        State state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        ActionSelector as=new ActionSelector(nodeSelected);
        actionInSelected=as.select();
        StepReturn sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface child = NodeInterface.newNode(sr, actionInSelected);
        if (nodeSelected.isNotTerminal())  {
        nodeSelected.addChildNode(child); }
        return sr;
    }

    private void backPropagate(StepReturn sr) {
        BackupModifier bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .build();
        bum.backup();
    }


}
