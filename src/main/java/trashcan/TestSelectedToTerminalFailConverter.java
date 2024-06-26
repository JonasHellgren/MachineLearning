package trashcan;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.classes.BackupModifier;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import trashcan.SelectedToTerminalFailConverter;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeTerminalFail;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
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
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
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
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot= createMCTSTree(actions,rootState,stepReturns);
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

        SelectedToTerminalFailConverter<ShipVariables, ShipActionSet> stc=new SelectedToTerminalFailConverter<>(nodeRoot,actionsToSelected,settings);
        stc.makeSelectedTerminal(nodeSelected.orElseThrow());

        nodeRoot.printTree();
        tih.getNodesOnPathForActions(actionsToSelected).get().forEach(System.out::println);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> nodeSelected2=tih.getNodeReachedForActions(actionsToSelected);

        System.out.println("nodeSelected.orElseThrow() = " + nodeSelected.orElseThrow());
        System.out.println("nodeSelected2.orElseThrow() = " + nodeSelected2.orElseThrow());

        Assert.assertFalse(nodeSelected.orElseThrow() instanceof NodeTerminalFail);
        Assert.assertTrue(nodeSelected2.orElseThrow() instanceof NodeTerminalFail);

    }

    private NodeWithChildrenInterface<ShipVariables, ShipActionSet> createMCTSTree(List<ActionInterface<ShipActionSet>> actions, StateShip rootState, List<StepReturnGeneric<ShipVariables>> stepReturns) {

        stepReturns.clear();
        StateShip state = rootState.copy();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(rootState, ActionShip.newNA());
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ActionInterface<ShipActionSet> a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeWithChildrenInterface<ShipVariables, ShipActionSet> child = NodeInterface.newNotTerminal(sr.newState, a);  //todo StateInterface
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
