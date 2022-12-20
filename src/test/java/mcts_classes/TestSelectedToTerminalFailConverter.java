package mcts_classes;

import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.SelectedToTerminalFailConverter;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeTerminalFail;
import mcts_spacegame.models_space.*;
import mcts_spacegame.models_space.ShipPolicies;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestSelectedToTerminalFailConverter {
    SpaceGrid spaceGrid;
    EnvironmentShip environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    BackupModifier<ShipVariables, ShipActionSet> bum;
    List<StepReturnGeneric<ShipVariables>> stepReturns;
    StepReturnGeneric<ShipVariables> getStepReturnOfSelected;

    @Before
    public void init() {
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        stepReturns = new ArrayList<>();
    }

    @Test
    public void testMakeSelectedTerminal() {
        StateShip rootState=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actionsToSelected= Arrays.asList(ActionShip.newStill(), ActionShip.newStill());
        ActionInterface<ShipActionSet> actionInSelected= ActionShip.newDown();
        List<ActionInterface<ShipActionSet>> actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Optional<NodeInterface<ShipVariables, ShipActionSet>> nodeSelected=tih.getNodeReachedForActions(actionsToSelected);

        bum = BackupModifier.<ShipVariables, ShipActionSet>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();

        nodeRoot.printTree();
        tih.getNodesOnPathForActions(actions).get().forEach(System.out::println);

        SelectedToTerminalFailConverter<ShipVariables, ShipActionSet> stc=new SelectedToTerminalFailConverter<>(nodeRoot,actionsToSelected);
        stc.makeSelectedTerminal(nodeSelected.orElseThrow());

        nodeRoot.printTree();
        tih.getNodesOnPathForActions(actionsToSelected).get().forEach(System.out::println);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> nodeSelected2=tih.getNodeReachedForActions(actionsToSelected);

        System.out.println("nodeSelected.orElseThrow() = " + nodeSelected.orElseThrow());
        System.out.println("nodeSelected2.orElseThrow() = " + nodeSelected2.orElseThrow());

        Assert.assertFalse(nodeSelected.orElseThrow() instanceof NodeTerminalFail);
        Assert.assertTrue(nodeSelected2.orElseThrow() instanceof NodeTerminalFail);

    }

    private NodeInterface<ShipVariables, ShipActionSet> createMCTSTree(List<ActionInterface<ShipActionSet>> actions, StateShip rootState, List<StepReturnGeneric<ShipVariables>> stepReturns) {

        stepReturns.clear();
        StateShip state = rootState.copy();
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(rootState, ActionShip.newNA());
        NodeInterface<ShipVariables, ShipActionSet> parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ActionInterface<ShipActionSet> a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeInterface<ShipVariables, ShipActionSet> child = NodeInterface.newNotTerminal((StateShip) sr.newState, a);  //todo StateInterface
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }
        getStepReturnOfSelected = stepReturns.get(stepReturns.size() - 1);
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
