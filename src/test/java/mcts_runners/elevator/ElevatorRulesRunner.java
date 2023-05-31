package mcts_runners.elevator;

import black_jack.result_drawer.GridPanel;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Arrays;

@Log
public class ElevatorRulesRunner {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int NOF_STEPS_HALF_RANDOM_POLICY = 50;
    private static final int NSTEPS_BETWEEN = 50;
    private static final int NOF_CYCLES = 5;
    private static final int SLEEP_TIME = 300;

    static EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    static StateInterface<VariablesElevator> state;
    static GridPanel panel;
    static ElevatorPanelUpdater panelUpdater;

    public static void main(String[] args) throws InterruptedException {
        environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN));
        state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        panel=FrameAndPanelCreatorElevator.createPanel("","","");
        panelUpdater =new ElevatorPanelUpdater(state,panel);

        for (int i = 0; i < NOF_CYCLES; i++) {
        System.out.println("variables start = " + state.getVariables());
        runHalfRandomPolicySimulation();
        runChargeSimulation();
        System.out.println("variables end = " + state.getVariables());
        }
    }

    private static void runHalfRandomPolicySimulation() throws InterruptedException {
        SimulationPolicyInterface<VariablesElevator, Integer> policy =
                new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();

        for (int i = 0; i < NOF_STEPS_HALF_RANDOM_POLICY; i++) {
            VariablesElevator variables = stepAndUpdateState(policy);
            doPrintingIfAtFloor(variables);
            updatePanelAndSleep100Millis();
        }
    }

    private static void doPrintingIfAtFloor(VariablesElevator variables) {
        if (EnvironmentElevator.isAtFloor.test(variables.speed, variables.pos)) {
            System.out.println("variables = " + variables);
        }
    }

    private static void runChargeSimulation() throws InterruptedException {
        log.info("Charge simulation starting");
        SimulationPolicyInterface<VariablesElevator, Integer> policy = new PolicyMoveDownStop();
        VariablesElevator variables;
        do {
            variables = stepAndUpdateState(policy);
            updatePanelAndSleep100Millis();
        } while (variables.SoE < SOE_FULL);
    }

    private static void updatePanelAndSleep100Millis() throws InterruptedException {
        panelUpdater.insertStates();
        Thread.sleep(SLEEP_TIME);
    }

    private static VariablesElevator stepAndUpdateState(SimulationPolicyInterface<VariablesElevator, Integer> policy) {
        ActionInterface<Integer> action = policy.chooseAction(state);
        StepReturnGeneric<VariablesElevator> stepReturn = environment.step(action, state);
        state.setFromReturn(stepReturn);
        return stepReturn.newState.getVariables();
    }

}
