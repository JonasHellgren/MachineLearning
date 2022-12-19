package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnREMOVE;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
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
        StepReturnREMOVE stepReturn= environment.step(ShipAction.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(0,stepReturn.newPosition.getY(), DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(0,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveDownFromXis0Yis0() {
        StateShip pos= StateShip.newStateFromXY(0,0);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.down,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(-1,stepReturn.newPosition.getY(), DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.CRASH_COST- EnvironmentShip.MOVE_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveStillFromXis0Yis0() {
        StateShip pos= StateShip.newStateFromXY(0,0);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(0,stepReturn.newPosition.getY(), DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis1YisCrash() {
        StateShip pos= StateShip.newStateFromXY(1,1);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(1,stepReturn.newPosition.getY(), DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveUpFromXis1Yis1() {
        StateShip pos= StateShip.newStateFromXY(1,1);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.up,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(2,stepReturn.newPosition.getY(), DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.MOVE_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis2Yis1() {
        StateShip pos= StateShip.newStateFromXY(2,1);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(3,stepReturn.newPosition.getX(), DELTA),  //new position is outside grid
                () -> assertEquals(1,stepReturn.newPosition.getY(), DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis3Yis1IsNonValidPosition() {
        StateShip pos= StateShip.newStateFromXY(3,1);
        StepReturnREMOVE stepReturn= environment.step(ShipAction.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(3,stepReturn.newPosition.getX(), DELTA),
                () -> assertEquals(1,stepReturn.newPosition.getY(), DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.CRASH_COST,stepReturn.reward,DELTA)
        );
    }


}
