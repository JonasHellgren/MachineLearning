package mcts_elevator;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.elevator.EnvironmentElevator;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEnvironmentElevator {

    public static final int ACTION_UP = 1;
    EnvironmentGenericInterface<VariablesElevator,Integer> environment;
    StepReturnGeneric<VariablesElevator> stepReturn;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
    }

    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionUp_thenPosIs1() {
        StateInterface<VariablesElevator> state=StateElevator.newFullyChargedReadyAtBottomFloorNoPassengers();
        ActionInterface<Integer> action= ActionElevator.newValueDefaultRange(ACTION_UP);
        stepReturn=environment.step(action,state);
        VariablesElevator variables=stepReturn.newState.getVariables();
        Assert.assertEquals(1,variables.pos);

    }


    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionStill_thenPosIs0() {

    }


    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionDown_thenPosIs0() {

    }


    @Test
    public void givenHalfChargedReadyAtBottomFloorNoPassengers_whenActionStill_thenPosIs0AndSoEIncreases() {

    }


    @Test
    public void givenReadyAtBottomFloorWithPassengersInElevator_whenActionStill_thenNoPassengersInElevator() {

    }

    @Test
    public void givenHalfChargedAtFloor1NoPassengers_whenActionStill_thenPosIsSameAndSoESame() {

    }


    @Test
    public void givenHalfChargedAtFloor1PassengersWaiting_whenActionStill_thenPassengersInElevator() {

    }


    @Test
    public void givenHalfChargedAtFloor1_whenActionDown_thenPosDecreasedAndSoEIncreased() {

    }


    @Test
    public void givenHalfChargedAtFloor1_whenActionUp_thenPosIncreasedAndSoEDecreased() {

    }




}
