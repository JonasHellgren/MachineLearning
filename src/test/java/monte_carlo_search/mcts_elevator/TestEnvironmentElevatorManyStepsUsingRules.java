package monte_carlo_search.mcts_elevator;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironmentElevatorManyStepsUsingRules {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;
    private static final int POS_FLOOR_2 = 20;
    private static final int POS_FLOOR_3 = 30;
    private static final int NOF_STEPS_HALF_RANDOM_POLICY = 20;  //100
    private static final double DELTA = 0.01;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    StateInterface<VariablesElevator> state;
    List<Integer> visitedPositions;


    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        visitedPositions=new ArrayList<>();
    }

    @Test
    public void givenRulePolicy_whenNoSteps_thenWaiting() {
        VariablesElevator variables = state.getVariables();
        assertEquals(1, variables.nofWaiting());
    }

    @Test
    public void givenRulePolicy_whenManyHalfRandomSteps_thenMaybeWaitin() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        VariablesElevator variables = getVariablesElevatorAfterStep(state, policy);
        System.out.println("variables start = " + variables);
        variables = runHalfRandomPolicySimulation(policy);

        Assert.assertTrue(variables.nofWaiting()==0 || variables.nofWaiting()==1);
        Assert.assertTrue(visitedPositions.contains(POS_FLOOR_1));
    }

    @Test
    public void givenRulePolicy_whenManyHalfRandomStepsAfterMoveToBottomAndStay_thenNoWaitingAndNoInElevatorAndFullSoE() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        runHalfRandomPolicySimulation(policy);
        VariablesElevator variables = runChargeSimulation(new PolicyMoveDownStop());
        System.out.println("variables end = " + variables);

        Assert.assertTrue(variables.nofWaiting()==0 || variables.nofWaiting()==1);
        Assert.assertEquals(0, variables.nPersonsInElevator);
        Assert.assertEquals(1, variables.SoE, DELTA);
        Assert.assertTrue(visitedPositions.contains(10));

    }

    @Test
    public void givenRulePolicyStartAtTop_whenManyHalfRandomSteps_thenMaybeWaiting() {

        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_3).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 1, 0))
                .build());
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                ElevatorPolicies.newRandomDirectionAfterStopping();

        VariablesElevator variables = runHalfRandomPolicySimulation(policy);

        Assert.assertTrue(variables.nofWaiting()==0 || variables.nofWaiting()==1);

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
        visitedPositions.clear();
        for (int i = 0; i < NOF_STEPS_HALF_RANDOM_POLICY; i++) {
          //  if (EnvironmentElevator.isAtFloor.test(variables.speed, variables.posReal))  {
                System.out.println("variables = " + variables);  //}
            visitedPositions.add(variables.pos);
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
