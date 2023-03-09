package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import org.junit.Assert;
import org.junit.Test;

public class TestVariablesElevator {

    private static final int MAX_N_PERSONS_IN_ELEVATOR = 5;
    private static final int MAX_N_PERSONS_WAITING_EACH_FLOOR = 5;

    @Test
    public void whenNewRandom_thenCorrectValues() {

        VariablesElevator variablesElevator=VariablesElevator.newRandom(MAX_N_PERSONS_IN_ELEVATOR, MAX_N_PERSONS_WAITING_EACH_FLOOR);
        System.out.println("variablesElevator = " + variablesElevator);

        Assert.assertTrue(variablesElevator.nPersonsInElevator<MAX_N_PERSONS_IN_ELEVATOR);
        Assert.assertTrue(variablesElevator.nofWaiting()<MAX_N_PERSONS_WAITING_EACH_FLOOR*3);

    }

    @Test public void whenCopying_thenEqual() {
        VariablesElevator variablesElevator=VariablesElevator.newRandom(MAX_N_PERSONS_IN_ELEVATOR, MAX_N_PERSONS_WAITING_EACH_FLOOR);
        VariablesElevator variablesElevatorCopy= variablesElevator.copy();
        System.out.println("variablesElevator = " + variablesElevator);
        System.out.println("variablesElevatorCopy = " + variablesElevatorCopy);

        Assert.assertEquals(variablesElevator, variablesElevatorCopy);
    }


}
