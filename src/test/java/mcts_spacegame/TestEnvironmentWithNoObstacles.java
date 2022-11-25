package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironmentWithNoObstacles {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    Environment environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.newWithNoObstacles(3,3);
        environment=new Environment(spaceGrid);
    }

    @Test
    public void moveStillFromx0y0() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(0,stepReturn.newPosition.y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(0,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveDownFromXis0Yis0() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.down,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(0,stepReturn.newPosition.y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-Environment.CRASH_COST-Environment.MOVE_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveStillFromXis0Yis0() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(0,stepReturn.newPosition.y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-Environment.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis1Yis1() {
        State pos=new State(1,1);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(1,stepReturn.newPosition.y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-Environment.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveUpFromXis1Yis1() {
        State pos=new State(1,1);
        StepReturn stepReturn= environment.step(Action.up,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(2,stepReturn.newPosition.y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-Environment.MOVE_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis2Yis1() {
        State pos=new State(2,1);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(2,stepReturn.newPosition.x, DELTA),  //new position is outside grid => equal old position
                () -> assertEquals(1,stepReturn.newPosition.y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-Environment.STILL_COST,stepReturn.reward,DELTA)
        );
    }

    @Test
    public void moveStillFromXis3Yis1IsNonValidPosition() {
        State pos=new State(3,1);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(3,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(1,stepReturn.newPosition.y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-Environment.CRASH_COST,stepReturn.reward,DELTA)
        );
    }


}
