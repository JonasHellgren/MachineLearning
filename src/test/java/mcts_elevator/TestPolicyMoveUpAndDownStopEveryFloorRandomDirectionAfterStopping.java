package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

public class TestPolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping {
    StateInterface<VariablesElevator> state;

    @Before
    public void init() {
        state= StateElevator.newFromVariables(VariablesElevator.newDefault());
    }

    @Test
    public void whenPosIsBetweenFloorAndSpeedIsZero_thenUpOrDown() {
        state.getVariables().speed=0;
        state.getVariables().pos=5;
        SimulationPolicyInterface<VariablesElevator, Integer> policy=new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();
        Set<Integer> availableActions=policy.availableActionValues(state);
        ActionInterface<Integer> action=policy.chooseAction(state);
        Assert.assertEquals(2,availableActions.size());
        Assert.assertTrue(Arrays.asList(-1,1).contains(action.getValue()));
    }

    @Test
    public void whenPosIsBetweenFloorAndSpeedIsUp_thenUp() {
        state.getVariables().speed=1;
        state.getVariables().pos=5;
        SimulationPolicyInterface<VariablesElevator, Integer> policy=new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();
        Set<Integer> availableActions=policy.availableActionValues(state);
        ActionInterface<Integer> action=policy.chooseAction(state);
        Assert.assertEquals(1,availableActions.size());
        Assert.assertEquals(1, (int) action.getValue());
    }



}
