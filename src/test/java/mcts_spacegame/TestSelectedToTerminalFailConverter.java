package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.environment.StepReturnREMOVE;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.SelectedToTerminalFailConverter;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeTerminalFail;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
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
    EnvironmentShip environment;
    BackupModifier bum;
    List<StepReturnGeneric<ShipVariables>> stepReturns;
    StepReturnGeneric<ShipVariables> getStepReturnOfSelected;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        stepReturns = new ArrayList<>();
    }

    @Test
    public void testMakeSelectedTerminal() {
        StateShip rootState=StateShip.newStateFromXY(0,0);
        List<ShipAction> actionsToSelected= Arrays.asList(ShipAction.still, ShipAction.still);
        ShipAction actionInSelected= ShipAction.down;
        List<ShipAction> actions = ShipAction.getAllActions(actionsToSelected, actionInSelected);
        NodeInterface nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot, MonteCarloSettings.newDefault());
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

    private NodeInterface createMCTSTree(List<ShipAction> actions, StateShip rootState, List<StepReturnGeneric<ShipVariables>> stepReturns) {

        stepReturns.clear();
        StateShip state = rootState.copy();
        NodeInterface nodeRoot = NodeInterface.newNotTerminal(rootState, ShipAction.notApplicable);
        NodeInterface parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ShipAction a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeInterface child = NodeInterface.newNotTerminal((StateShip) sr.newState, a);  //todo StateInterface
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }
        getStepReturnOfSelected = stepReturns.get(stepReturns.size() - 1);
        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<ShipAction> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    private void printLists(List<ShipAction> actions, List<StepReturnREMOVE> stepReturns, NodeInterface nodeRoot) {
        System.out.println("-----------------------------");
        nodeRoot.printTree();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        tih.getNodesOnPathForActions(actions).get().forEach(System.out::println);

        System.out.println("-----------------------------");
    }

    @NotNull
    private StepReturnGeneric<ShipVariables> stepAndUpdateState(StateShip pos, ShipAction a) {
        StepReturnGeneric<ShipVariables> sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }

}
