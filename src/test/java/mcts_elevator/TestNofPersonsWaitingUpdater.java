package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.NofPersonsWaitingUpdater;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNofPersonsWaitingUpdater {

    private static final int STEPS_1 = 10;
    private static final int STEPS_2 = 10;
    private static final int STEPS_3 = 10;
    NofPersonsWaitingUpdater nofPersonsWaitingUpdater;

    @Before
    public void init() {
        nofPersonsWaitingUpdater=new NofPersonsWaitingUpdater(Arrays.asList(STEPS_1,STEPS_2,STEPS_3));
    }


    @Test public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenNoUpdate_thenNoWaiting() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        VariablesElevator variables=stateElevator.getVariables();
        assertEquals(Arrays.asList(0,0,0),variables.nPersonsWaiting);
    }


    @Test public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenUpdate_thenNoWaiting() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        VariablesElevator variables=stateElevator.getVariables();
        nofPersonsWaitingUpdater.update(stateElevator);
        printStates(stateElevator);
        assertEquals(Arrays.asList(0,0,0),variables.nPersonsWaiting);
    }


    @Test public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenUpdate10Times_thenOneWaiting() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        VariablesElevator variables=stateElevator.getVariables();

        for (int i = 0; i <10 ; i++) {
            nofPersonsWaitingUpdater.update(stateElevator);
            printStates(stateElevator);
        }

        assertEquals(Arrays.asList(1,1,1),variables.nPersonsWaiting);
    }

    @Test public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenUpdate15Times_thenOneWaiting() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        VariablesElevator variables=stateElevator.getVariables();

        for (int i = 0; i <15 ; i++) {
            nofPersonsWaitingUpdater.update(stateElevator);
            printStates(stateElevator);
        }

        assertEquals(Arrays.asList(1,1,1),variables.nPersonsWaiting);
    }


    private void printStates(StateInterface<VariablesElevator> stateElevator) {
        System.out.println("stateElevator = " + stateElevator);
        System.out.println("nofPersonsWaitingUpdater = " + nofPersonsWaitingUpdater);
    }

}
