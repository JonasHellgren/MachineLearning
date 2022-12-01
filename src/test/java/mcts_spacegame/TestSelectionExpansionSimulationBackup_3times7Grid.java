package mcts_spacegame;

import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.NodeInfoHelper;
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

@Log
public class TestSelectionExpansionSimulationBackup_3times7Grid {

    private static final int DELTA_BIG = 2;
    private static final int NOF_ITERATIONS = 50;
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
    }

    @Test
    public void oneIteration() {
        initTree(new State(0, 0));
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

    private void initTree(State state) {
        startState = state;
        nodeRoot = NodeInterface.newNotTerminal(startState, Action.notApplicable);
    }

    @Test
    public void iterateFromX0Y0() {
        initTree(new State(0, 0));
        doMCTSIterations();

        nodeRoot.printTree();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        tih.getBestPath().forEach(System.out::println);

        Optional<NodeInterface> node11= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(1,1));
        Assert.assertFalse(node11.isEmpty());
        Optional<NodeInterface> node52= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(5,2));
        Assert.assertFalse(node52.isEmpty());
    }


    @Test
    public void iterateFromX0Y2() {
        initTree(new State(0, 2));
        doMCTSIterations();

        nodeRoot.printTree();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        tih.getBestPath().forEach(System.out::println);

        Optional<NodeInterface> node12= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(1,2));
        Assert.assertFalse(node12.isEmpty());
        Optional<NodeInterface> node52= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(5,2));
        Assert.assertFalse(node52.isEmpty());
    }

    @Test
    public void iterateFromX2Y0() {
        initTree(new State(2,0));
        doMCTSIterations();

        nodeRoot.printTree();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        System.out.println("tih.getBestPath().size() = " + tih.getBestPath().size());
        tih.getBestPath().forEach(System.out::println);

        Optional<NodeInterface> node12= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(2,0));
        Assert.assertTrue(node12.isPresent());
    }

    private void doMCTSIterations() {
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            NodeInterface nodeSelected = select(nodeRoot);
            StepReturn sr = chooseActionAndExpand(nodeSelected);
            //todo simulation
            backPropagate(sr);
        }
    }

    private NodeInterface select(NodeInterface nodeRoot) {
        NodeSelector ns = new NodeSelector(nodeRoot);
        NodeInterface nodeSelected=ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    @NotNull
    private StepReturn chooseActionAndExpand(NodeInterface nodeSelected) {
        State state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        ActionSelector as=new ActionSelector(nodeSelected);
        actionInSelected=as.select();
        StepReturn sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface child = NodeInterface.newNode(sr, actionInSelected);
        child.setDepth(nodeSelected.getDepth()+1);  //easy to forget
        boolean isChildAddedEarlier=NodeInfoHelper.findNodeMatchingNode(nodeSelected.getChildNodes(),child).isPresent();

        if (isChildAddedEarlier) {
            log.warning("Child has been added earlier, child = "+child+", in node = "+nodeSelected);
        }

        if (nodeSelected.isNotTerminal() && !isChildAddedEarlier)  {
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
