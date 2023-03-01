package mcts_elevator;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Atomic;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironmentElevatorManyStepsUsingRules {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_3 = 30;
    private static final int NOF_STEPS_HALF_RANDOM_POLICY = 20;  //100
    private static final double DELTA = 0.01;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    StateInterface<VariablesElevator> state;


    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
    }

    @Test
    public void givenRulePolicy_whenNoSteps_thenWaiting() {
        VariablesElevator variables = state.getVariables();
        assertEquals(1, variables.nofWaiting());
    }

    @Test
    public void givenRulePolicy_whenManyHalfRandomSteps_thenNoWaiting() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        VariablesElevator variables = getVariablesElevatorAfterStep(state, policy);
        System.out.println("variables start = " + variables);
        variables = runHalfRandomPolicySimulation(policy);

        Assert.assertEquals(0, variables.nofWaiting());
    }


    @Test
    public void givenRulePolicy_whenManyHalfRandomStepsAfterMoveToBottomAndStay_thenNoWaitingAndNoInElevatorAndFullSoE() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        runHalfRandomPolicySimulation(policy);
        VariablesElevator variables = runChargeSimulation(new PolicyMoveDownStop());
        System.out.println("variables end = " + variables);

        Assert.assertEquals(0, variables.nofWaiting());
        Assert.assertEquals(0, variables.nPersonsInElevator);
        Assert.assertEquals(1, variables.SoE, DELTA);

    }

    @Test
    public void givenRulePolicyStartAtTop_whenManyHalfRandomSteps_thenNoWaiting() {

        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_3).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 1, 0))
                .build());
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        VariablesElevator variables = runHalfRandomPolicySimulation(policy);

        Assert.assertEquals(0, variables.nofWaiting());
    }

    private VariablesElevator runChargeSimulation(SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        VariablesElevator variables;
        do {
            variables = getVariablesElevatorAfterStep(state, policy);
        } while (variables.SoE < 1);
        return variables;
    }

    private VariablesElevator runHalfRandomPolicySimulation(SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        VariablesElevator variables = state.getVariables();
        for (int i = 0; i < NOF_STEPS_HALF_RANDOM_POLICY; i++) {
          //  if (EnvironmentElevator.isAtFloor.test(variables.speed, variables.pos))  {
                System.out.println("variables = " + variables);  //}


            variables = getVariablesElevatorAfterStep(state, policy);
        }
        return variables;
    }


    private VariablesElevator getVariablesElevatorAfterStep(StateInterface<VariablesElevator> state,
                                                            SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        ActionInterface<Integer> action = policy.chooseAction(state);
        StepReturnGeneric<VariablesElevator> stepReturn = environment.step(action, state);
        state.setFromReturn(stepReturn);
        return stepReturn.newState.getVariables();
    }

}
