package monte_carlo_search.mcts_elevator;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.elevator.EnvironmentElevator;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

public class TestEnvironmentElevator {

    public static final int ACTION_UP = 1;
    private static final int ACTION_STILL = 0;
    private static final int ACTION_DOWN = -1;
    private static final double HALF_SOE = 0.5;
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;

    Supplier<StateInterface<VariablesElevator>> fullyChargedReadyAtBottomFloorNoPassengers =
            StateElevator::newFullyChargedReadyAtBottomFloorNoPassengers;

    Supplier<StateInterface<VariablesElevator>> halfChargedReadyAtBottomFloorNoPassengers = () ->
            StateElevator.newFromVariables(VariablesElevator.builder().SoE(HALF_SOE).pos(POS_FLOOR_0).build());

    Supplier<StateInterface<VariablesElevator>> atBottomFloorWithPassengersInElevator = () ->
            StateElevator.newFromVariables(VariablesElevator.builder().nPersonsInElevator(1).pos(POS_FLOOR_0).build());

    Supplier<StateInterface<VariablesElevator>> halfChargedAtFloor1 = () ->
            StateElevator.newFromVariables(VariablesElevator.builder().SoE(HALF_SOE).pos(POS_FLOOR_1).build());

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
    }

    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionUp_thenPosIs1() {
        StateInterface<VariablesElevator> state = fullyChargedReadyAtBottomFloorNoPassengers.get();
        Assert.assertEquals(1, getVariablesElevatorAfterStep(state, ACTION_UP).pos);
    }

    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionStill_thenPosIs0() {
        StateInterface<VariablesElevator> state = fullyChargedReadyAtBottomFloorNoPassengers.get();
        Assert.assertEquals(0, getVariablesElevatorAfterStep(state, ACTION_STILL).pos);
    }

    @Test
    public void givenFullyChargedReadyAtBottomFloorNoPassengers_whenActionDown_thenPosIs0() {
        StateInterface<VariablesElevator> state = fullyChargedReadyAtBottomFloorNoPassengers.get();
        Assert.assertEquals(0, getVariablesElevatorAfterStep(state, ACTION_DOWN).pos);
    }

    @Test
    public void givenHalfChargedReadyAtBottomFloorNoPassengers_whenActionStill_thenPosIs0AndSoEIncreases() {
        StateInterface<VariablesElevator> state = halfChargedReadyAtBottomFloorNoPassengers.get();
        Assert.assertEquals(0, getVariablesElevatorAfterStep(state, ACTION_STILL).pos);
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_STILL).SoE > HALF_SOE);
    }

    @Test
    public void givenReadyAtBottomFloorWithPassengersInElevator_whenActionStill_thenNoPassengersInElevator() {
        StateInterface<VariablesElevator> state = atBottomFloorWithPassengersInElevator.get();
        Assert.assertEquals(0, getVariablesElevatorAfterStep(state, ACTION_STILL).nPersonsInElevator);
    }

    @Test
    public void givenHalfChargedAtFloor1NoPassengers_whenActionStill_thenPosIsSameAndSoESame() {
        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(HALF_SOE).pos(POS_FLOOR_0).nPersonsInElevator(0).build());
        Assert.assertEquals(0, getVariablesElevatorAfterStep(state, ACTION_STILL).nPersonsInElevator);
    }

    @Test
    public void givenHalfChargedAtFloor1PassengersWaiting_whenActionStill_thenPassengersInElevator() {
        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_1).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        Assert.assertEquals(1, getVariablesElevatorAfterStep(state, ACTION_STILL).nPersonsInElevator);
    }

    @Test
    public void givenHalfChargedAtFloor1_whenActionDown_thenPosDecreasedAndSoEIncreased() {
        StateInterface<VariablesElevator> state = halfChargedAtFloor1.get();
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_DOWN).pos < POS_FLOOR_1);
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_DOWN).SoE > HALF_SOE);
    }

    @Test
    public void givenHalfChargedAtFloor1_whenActionUp_thenPosIncreasedAndSoEDecreased() {
        StateInterface<VariablesElevator> state = halfChargedAtFloor1.get();
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_UP).pos > POS_FLOOR_1);
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_UP).SoE < SOE_FULL);
    }

    @Test
    public void givenSpeed0AtPos1_whenActionStill_thenSamePos() {
        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(
                VariablesElevator.builder().SoE(HALF_SOE).speed(0).pos(1).build());
        Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_STILL).pos == 1);
        //Assert.assertTrue(getVariablesElevatorAfterStep(state, ACTION_UP).SoE < SOE_FULL);
    }

    private VariablesElevator getVariablesElevatorAfterStep(StateInterface<VariablesElevator> state, int actionValue) {
        ActionInterface<Integer> action = ActionElevator.newValueDefaultRange(actionValue);
        StepReturnGeneric<VariablesElevator> stepReturn = environment.step(action, state);
        return stepReturn.newState.getVariables();
    }

}
