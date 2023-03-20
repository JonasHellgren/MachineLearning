package monte_carlo_search.mcts_classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.ActionSelector;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeNotTerminal;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestActionSelector {
    private static final int NSTEPS_BETWEEN = 50;
    private static final double SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int ALPHA = 1;
    private static final int NOF_VISITS_EXPONENT = 1;
    private static final int VALUE_1 = 10;
    private static final int VALUE_MINUS1 = -10;
    private static final int ZERO_SPEED = 0;


    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloSettings<VariablesElevator, Integer> settings;
    ActionSelector<VariablesElevator, Integer> actionSelector;
    NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        settings = MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .build();

        actionSelector = new ActionSelector<>(settings, ActionElevator.newValueDefaultRange(0));
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(ZERO_SPEED).SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        addTwoNodesToRootNodeOneForActionMinusOneAndOneForActionOne(startState);
        System.out.println("nodeRoot = " + nodeRoot);


    }

    private void addTwoNodesToRootNodeOneForActionMinusOneAndOneForActionOne(StateInterface<VariablesElevator> startState) {
        nodeRoot = NodeInterface.newNotTerminal(startState, ActionElevator.newValueDefaultRange(0));
        nodeRoot.increaseNofActionSelections(ActionElevator.newValueDefaultRange(1));
        nodeRoot.addChildNode(getDummyNode(1));
        nodeRoot.updateActionValue(VALUE_1, ActionElevator.newValueDefaultRange(1), ALPHA, NOF_VISITS_EXPONENT);
        nodeRoot.increaseNofActionSelections(ActionElevator.newValueDefaultRange(-1));
        nodeRoot.addChildNode(getDummyNode(-1));
        nodeRoot.updateActionValue(VALUE_MINUS1, ActionElevator.newValueDefaultRange(-1), ALPHA, NOF_VISITS_EXPONENT);
    }

    @NotNull
    private NodeNotTerminal<VariablesElevator, Integer> getDummyNode(int actionValue) {
        return NodeInterface.newNotTerminal(StateElevator.newFromVariables(
                VariablesElevator.newDefault()), ActionElevator.newValueDefaultRange(actionValue));
    }

    @SneakyThrows
    @Test
    public void givenBottomFloorWithAction1AndMinus1Tested_thenRandomNonTestedActionIsZero() {
        Optional<ActionInterface<Integer>> selectedAction = actionSelector.selectRandomNonTestedAction(nodeRoot);
        Assert.assertEquals(0, (int) selectedAction.orElseThrow().getValue());
    }

    @SneakyThrows
    @Test
    public void givenBottomFloorWithAction1AndMinus1Tested_thenBestTestedActionIs1() {
        Optional<ActionInterface<Integer>> selectedAction = actionSelector.selectBestTestedAction(nodeRoot);
        Assert.assertEquals(1, (int) selectedAction.orElseThrow().getValue());
    }

    @SneakyThrows
    @Test
    public void givenBottomFloorWithAction1AndMinus1Tested_thenRandomActionIsMinus1Or0Or1() {
        ActionInterface<Integer> selectedAction = actionSelector.getRandomAction();
        System.out.println("selectedAction = " + selectedAction);
        Assert.assertTrue(-1 == selectedAction.getValue() || 0 == selectedAction.getValue() || 1 == selectedAction.getValue());
    }

    @SneakyThrows
    @Test
    public void givenBottomFloorsStillNoTestedAction_thenNonTestedActionsInPolicyIsAll() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(0).SoE(SOE_FULL).pos(POS_FLOOR_0)
                .build());
        nodeRoot = NodeInterface.newNotTerminal(startState, ActionElevator.newValueDefaultRange(0));
        List<ActionInterface<Integer>> selectedActions = actionSelector.getNonTestedActionsInPolicy(nodeRoot);
        System.out.println("selectedActions = " + selectedActions);
        Assert.assertEquals(3, selectedActions.size());
    }

    @SneakyThrows
    @Test
    public void givenStateBetweenFloorsMovingUpNoTestedAction_thenNonTestedActionsInPolicyIs1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(1).SoE(SOE_FULL).pos(5)
                .build());
        nodeRoot = NodeInterface.newNotTerminal(startState, ActionElevator.newValueDefaultRange(0));
        List<ActionInterface<Integer>> selectedActions = actionSelector.getNonTestedActionsInPolicy(nodeRoot);
        System.out.println("selectedActions = " + selectedActions);
        Assert.assertEquals(1, selectedActions.size());
        Assert.assertEquals(1, (int) selectedActions.get(0).getValue());
    }


}
