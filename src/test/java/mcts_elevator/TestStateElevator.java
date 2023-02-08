package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStateElevator {


    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_thenAllZeroExceptSoE() {

        StateElevator stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        System.out.println("stateElevator = " + stateElevator);
        VariablesElevator variables=stateElevator.getVariables();

        assertAll(
                () -> assertEquals(0,variables.pos),
                () -> assertEquals(0,variables.nPersonsInElevator),
                () -> assertEquals(Arrays.asList(0,0,0),variables.nPersonsWaiting),
                () -> assertEquals(StateElevator.SOE_MAX,variables.SoE)
        );

    }

    @Test(expected = IllegalArgumentException.class)
    public void givenPosIs31_thenThrows()   {
        StateElevator.newFromVariables(
                VariablesElevator.builder().pos(31).build()
        );
    }

    @Test
    public void whenCopy_thenEqual() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        StateInterface<VariablesElevator> stateElevatorCopy=stateElevator.copy();
        Assert.assertEquals(stateElevator.getVariables(), stateElevatorCopy.getVariables());
    }


    @Test
    public void whenCopyAndModifying_thenNotEqual() {
        StateInterface<VariablesElevator> stateElevator=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        StateInterface<VariablesElevator> stateElevatorCopy=stateElevator.copy();
        stateElevatorCopy.getVariables().pos=stateElevatorCopy.getVariables().pos+1;
        System.out.println("stateElevator = " + stateElevator);
        System.out.println("stateElevatorCopy = " + stateElevatorCopy);
        Assert.assertNotEquals(stateElevator.getVariables(), stateElevatorCopy.getVariables());
    }

}
