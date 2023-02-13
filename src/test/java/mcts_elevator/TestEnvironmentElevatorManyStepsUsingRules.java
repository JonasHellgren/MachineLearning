package mcts_elevator;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironmentElevatorManyStepsUsingRules {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    StateInterface<VariablesElevator> state;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 1, 0))
                .build());
    }

    @Test
    public void giveRulePolicy_whenNoSteps_thenWaiting() {

        VariablesElevator variables=state.getVariables();
        assertEquals(1,variables.nofWaiting());

    }

        @Test
    public void giveRulePolicy_whenManySteps_thenNoWaiting() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy=
                new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();


            for (int i = 0; i < 20 ; i++) {
                VariablesElevator variables=getVariablesElevatorAfterStep(state,policy);
                System.out.println("variables = " + variables);
            }


    }


    private VariablesElevator getVariablesElevatorAfterStep(StateInterface<VariablesElevator> state,
                                                            SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        ActionInterface<Integer> action = policy.chooseAction(state);
        StepReturnGeneric<VariablesElevator> stepReturn = environment.step(action, state);
        state.setFromReturn(stepReturn);
        return stepReturn.newState.getVariables();
    }

}
