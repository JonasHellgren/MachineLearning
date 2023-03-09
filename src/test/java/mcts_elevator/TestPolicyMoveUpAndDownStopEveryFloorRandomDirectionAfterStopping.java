package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.arrow.flatbuf.Int;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

public class TestPolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping {
    private static final int MAX_N_PERSONS_IN_ELEVATOR = 5;
    private static final int MAX_N_PERSONS_WAITING_EACH_FLOOR = 5;

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