package mcts_classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.classes.BackupModifier;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestBackupModifier {

    private static final double DELTA = 0.1;
    private static final int DELTA_BIG = 2;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    BackupModifier<ShipVariables, ShipActionSet> bum;
    List<StepReturnGeneric<ShipVariables>> stepReturns;
    StepReturnGeneric<ShipVariables> getStepReturnOfSelected;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();
        stepReturns = new ArrayList<>();
    }

    @Test
    public void testCreatedTree() {
        StateShip rootState = StateShip.newStateFromXY(0, 0);
        List<ActionInterface<ShipActionSet>> actions = Arrays.asList(ActionShip.newUp(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());

        NodeInterface<ShipVariables, ShipActionSet> treeRoot = createMCTSTree(actions, rootState, stepReturns);
        treeRoot.printTree();

    }


    @SneakyThrows
    @Test
    public void moveDownFromX0Y0ToGetFailState() {
        StateShip rootState = StateShip.newStateFromXY(0, 0);

        List<ActionInterface<ShipActionSet>> actionsToSelected = Collections.emptyList();
        ActionInterface<ShipActionSet> actionInSelected = ActionShip.newDown();
        List<ActionInterface<ShipActionSet>> actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = createMCTSTree(actions, rootState, stepReturns);
        printLists(actions, stepReturns, nodeRoot);
        bum = BackupModifier.<ShipVariables, ShipActionSet>builder()
                .rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        double valueDown = nodeRoot.getActionValue(ActionShip.newDown());
        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueDown);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST, valueDown, DELTA_BIG);

    }



    @SneakyThrows
    @Test
    public void moveFromX0Y0Tox6y2GivesTwoMoveCost() {
        StateShip rootState=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actionsToSelected= Arrays.asList(ActionShip.newUp(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        ActionInterface<ShipActionSet> actionInSelected= ActionShip.newStill();

        List<ActionInterface<ShipActionSet>> actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.<ShipVariables, ShipActionSet>builder()
                .rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        double upAtRoot=nodeRoot.getActionValue(ActionShip.newUp());
        double stillAfterObstacles=tih.getNodesOnPathForActions(actionsToSelected).orElseThrow().get(3).getActionValue(ActionShip.newStill());
        System.out.println("upAtRoot = " + upAtRoot);
        System.out.println("stillAfterObstacles = " + stillAfterObstacles);

        Assert.assertEquals(-EnvironmentShip.MOVE_COST *2,upAtRoot, DELTA);
        Assert.assertEquals(-EnvironmentShip.STILL_COST,stillAfterObstacles, DELTA);

    }



    @SneakyThrows
    @Test
    public void moveFromX0Y0ToX3Y0ToGetFailStateByObstacleCrash() {
        StateShip rootState=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actionsToSelected= Arrays.asList(ActionShip.newUp(), ActionShip.newDown());
        ActionInterface<ShipActionSet> actionInSelected= ActionShip.newStill();
        List<ActionInterface<ShipActionSet>> actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.<ShipVariables, ShipActionSet>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();
        bum.backup();

        printLists(actions, stepReturns, nodeRoot);

        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        double valueUpRoot=nodeRoot.getActionValue(ActionShip.newUp());
        double valueStillSelected=tih.getValueForActionInNode(actionsToSelected, ActionShip.newStill()).get();
        System.out.println("valueUpRoot = " + valueUpRoot);
        System.out.println("valueStillSelected = " + valueStillSelected);
        Assert.assertEquals(-0,valueUpRoot, DELTA);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST,valueStillSelected, DELTA_BIG);

    }



    @SneakyThrows
    @Test
    public void moveFromX0Y0ToX2Y0AndDoAllMovesToShowGetIntoTrap() {
        StateShip rootState=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actionsToSelected= Arrays.asList(ActionShip.newUp(), ActionShip.newDown());
        ActionInterface<ShipActionSet> actionInSelected= ActionShip.newStill();
        List<ActionInterface<ShipActionSet>> actions = ActionInterface.mergeActionsWithAction(actionsToSelected, actionInSelected);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot= createMCTSTree(actions,rootState,stepReturns);
        bum = BackupModifier.<ShipVariables, ShipActionSet>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();
        bum.backup();

        StateShip state = getState(rootState, actionsToSelected);
        actionInSelected= ActionShip.newDown();
        updateTreeFromActionInState(actionsToSelected, actionInSelected, nodeRoot, state);

        state = getState(rootState, actionsToSelected);
        actionInSelected= ActionShip.newUp();
        NodeInterface<ShipVariables, ShipActionSet> nodeSelected = updateTreeFromActionInState(actionsToSelected, actionInSelected, nodeRoot, state);


        nodeRoot.printTree();
        System.out.println("nodeSelected = " + nodeSelected);

        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper(nodeRoot,settings);
        Optional<Double> valueUp=tih.getValueForActionInNode(actionsToSelected, ActionShip.newUp());
        Optional<Double> valueStill=tih.getValueForActionInNode(actionsToSelected, ActionShip.newStill());
        Optional<Double> valueDown=tih.getValueForActionInNode(actionsToSelected, ActionShip.newDown());

        Assert.assertEquals(-0,nodeRoot.getActionValue(ActionShip.newUp()), DELTA);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST,valueUp.get(), DELTA_BIG);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST,valueStill.get(), DELTA_BIG);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST,valueDown.get(), DELTA_BIG);

    }


    @SneakyThrows
    @NotNull
    private NodeInterface<ShipVariables, ShipActionSet> updateTreeFromActionInState(List<ActionInterface<ShipActionSet>> actionsToSelected,
                                                      ActionInterface<ShipActionSet> actionInSelected,
                                                      NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot,
                                                      StateShip state) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        getStepReturnOfSelected= environment.step(actionInSelected, state);
        NodeInterface<ShipVariables, ShipActionSet> nodeSelected= tih.getNodeReachedForActions(actionsToSelected).get();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeCasted=(NodeWithChildrenInterface<ShipVariables, ShipActionSet>) nodeSelected;  //casting

        nodeCasted.saveRewardForAction(actionInSelected, getStepReturnOfSelected.reward);
        bum = BackupModifier.<ShipVariables, ShipActionSet>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(getStepReturnOfSelected)
                .settings(settings)
                .build();
        bum.backup();
        return nodeSelected;   //nodeCasted??
    }

    private StateShip getState(StateShip rootState, List<ActionInterface<ShipActionSet>> actionsToSelected) {
        StateShip state = rootState.copy();
        for (ActionInterface<ShipActionSet> a : actionsToSelected) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);
        }
        return state;
    }

    private NodeWithChildrenInterface<ShipVariables, ShipActionSet> createMCTSTree(List<ActionInterface<ShipActionSet>> actions, StateShip rootState, List<StepReturnGeneric<ShipVariables>> stepReturns) {

        stepReturns.clear();
        StateShip state = rootState.copy();
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(rootState, new ActionShip(actionTemplate.nonApplicableAction()));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> parent = nodeRoot;
        int nofAddedChilds = 0;
        for (ActionInterface<ShipActionSet> a : actions) {
            StepReturnGeneric<ShipVariables> sr = stepAndUpdateState(state, a);
            stepReturns.add(sr.copy());
            parent.saveRewardForAction(a, sr.reward);
            NodeWithChildrenInterface<ShipVariables, ShipActionSet> child = NodeInterface.newNotTerminal(sr.newState, a);
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

    private void printLists(List<ActionInterface<ShipActionSet>> actions,
                            List<StepReturnGeneric<ShipVariables>> stepReturns, NodeWithChildrenInterface<ShipVariables,
            ShipActionSet> nodeRoot) {
        System.out.println("-----------------------------");
        nodeRoot.printTree();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        tih.getNodesOnPathForActions(actions).get().forEach(System.out::println);

        System.out.println("-----------------------------");
    }

    @NotNull
    private StepReturnGeneric<ShipVariables> stepAndUpdateState(StateShip pos, ActionInterface<ShipActionSet> a) {
        StepReturnGeneric<ShipVariables> sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }


}
