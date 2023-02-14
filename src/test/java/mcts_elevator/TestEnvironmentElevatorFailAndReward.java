package mcts_elevator;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.elevator.EnvironmentElevator;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentElevatorFailAndReward {

    private static final int SOE_FULL = 1;
    private static final int ACTION_STILL = 0;
    private static final int SOE_EMPTY = 0;
    private static final double SOE_SMALL = 0.21;
    private static final int ACTION_UP = 1;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    StateInterface<VariablesElevator> state;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
    }

    @Test
    public void givenFullSoEOneWaiting_whenStill_thenNotFailMinusOneReward() {
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).nPersonsWaiting(Arrays.asList(1, 0, 0)).build());
         StepReturnGeneric<VariablesElevator> sr= getStepReturnAfterStep(state, ACTION_STILL);
         assertFalse(sr.isFail);
         assertEquals(-1,sr.reward);
    }

    @Test
    public void givenEmptySoEOneWaiting_whenStill_thenNotFailMinusOneReward() {
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_EMPTY).nPersonsWaiting(Arrays.asList(1, 0, 0)).build());
        StepReturnGeneric<VariablesElevator> sr= getStepReturnAfterStep(state, ACTION_STILL);
        assertTrue(sr.isFail);
        assertTrue(sr.reward<-1);
    }

    @Test
    public void givenFullSoETenWaiting_whenStill_thenNotFailMinusOneReward() {
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).nPersonsWaiting(Arrays.asList(1, 5, 4)).build());
        StepReturnGeneric<VariablesElevator> sr= getStepReturnAfterStep(state, ACTION_STILL);
        assertFalse(sr.isFail);
        assertEquals(-10,sr.reward);
    }

    @Test
    public void givenSmallSoETenWaiting_whenUp_thenFail() {
        state = StateElevator.newFromVariables(VariablesElevator.builder().SoE(SOE_SMALL).build());
        StepReturnGeneric<VariablesElevator> sr= getStepReturnAfterStep(state, ACTION_UP);
        assertTrue(sr.isFail);
    }


    @Test
    public void givenSmallSoETenWaiting_whenStill_thenNotFail() {
        state = StateElevator.newFromVariables(VariablesElevator.builder().build());
        StepReturnGeneric<VariablesElevator> sr= getStepReturnAfterStep(state, ACTION_STILL);
        assertFalse(sr.isFail);
    }

    private StepReturnGeneric<VariablesElevator> getStepReturnAfterStep(
            StateInterface<VariablesElevator> state,
            int actionValue) {

        ActionInterface<Integer> action = ActionElevator.newValueDefaultRange(actionValue);
        return environment.step(action, state);
    }

}
