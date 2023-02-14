package mcts_elevator_runner;

import black_jack.result_drawer.GridPanel;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.Arrays;

public class ElevatorRulesRunner {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int NOF_STEPS_HALF_RANDOM_POLICY = 50;
    private static final double DELTA = 0.01;
    private static final int NSTEPS_BETWEEN = 50;
    private static final int NOF_CYCLES = 5;

    static EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    static StateInterface<VariablesElevator> state;


    public static void main(String[] args) {
        environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN));
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());

        GridPanel panel=FrameAndPanelCreatorElevator.createPanel("","","");

        for (int i = 0; i < NOF_CYCLES; i++) {
        System.out.println("variables start = " + state.getVariables());
        runHalfRandomPolicySimulation();
        runChargeSimulation();
        System.out.println("variables end = " + state.getVariables());
        }

        StateShowerElevator shower=new StateShowerElevator(state,panel);
        shower.insertStates();

    }

    private static void runChargeSimulation() {
        SimulationPolicyInterface<VariablesElevator, Integer> policy = new PolicyMoveDownStop();
        VariablesElevator variables;
        do {
            variables = stepAndUpdateState(policy);
        } while (variables.SoE < SOE_FULL);
    }

    private static void runHalfRandomPolicySimulation() {
        VariablesElevator variables = null;
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();

        for (int i = 0; i < NOF_STEPS_HALF_RANDOM_POLICY; i++) {
            variables = stepAndUpdateState(policy);
            EnvironmentElevator environmentCasted = (EnvironmentElevator) environment;
            if (environmentCasted.canPersonLeavingOrEnter(state)) {
                System.out.println("variables = " + variables);
            }
        }
    }

    private static VariablesElevator stepAndUpdateState(SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        ActionInterface<Integer> action = policy.chooseAction(state);
        StepReturnGeneric<VariablesElevator> stepReturn = environment.step(action, state);
        state.setFromReturn(stepReturn);
        return stepReturn.newState.getVariables();
    }

}
