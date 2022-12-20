package mcts_spacegame;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironmentWithNoObstacles {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.newWithNoObstacles(3,3);
        environment=new EnvironmentShip(spaceGrid);
    }

    @Test
    public void moveStillFromx0y0() {
        StateShip pos= StateShip.newStateFromXY(0,0);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(0,stepReturn.newState.getVariables().y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(0,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveDownFromXis0Yis0() {
        StateShip pos= StateShip.newStateFromXY(0,0);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newDown(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(-1,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.CRASH_COST- EnvironmentShip.MOVE_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveStillFromXis0Yis0() {
        StateShip pos= StateShip.newStateFromXY(0,0);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(0,stepReturn.newState.getVariables().y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis1YisCrash() {
        StateShip pos= StateShip.newStateFromXY(1,1);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(1,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveUpFromXis1Yis1() {
        StateShip pos= StateShip.newStateFromXY(1,1);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newUp(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(2,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.MOVE_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis2Yis1() {
        StateShip pos= StateShip.newStateFromXY(2,1);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(3,stepReturn.newState.getVariables().x, DELTA),  //new position is outside grid
                () -> assertEquals(1,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis3Yis1IsNonValidPosition() {
        StateShip pos= StateShip.newStateFromXY(3,1);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(3,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(1,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.CRASH_COST,stepReturn.reward,DELTA)
        );
    }


}
