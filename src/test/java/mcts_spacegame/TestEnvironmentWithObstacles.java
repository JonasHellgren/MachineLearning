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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironmentWithObstacles {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    Environment environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new Environment(spaceGrid);
    }

    @Test
    public void moveStillFromx0y0GivesObstacleCrash() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.still,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(0,stepReturn.newPosition.y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertEquals(-Environment.CRASH_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void moveUpFromx0y0GivesNoObstacleCrash() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.up,pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newPosition.x, DELTA),
                () -> assertEquals(1,stepReturn.newPosition.y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-Environment.MOVE_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void multipleMovesStillFromx0y2GivesMovingToGoal() {
        State pos=new State(0,2);

        System.out.println("environment = " + environment);

        StepReturn stepReturn;
        do {
            System.out.println("pos = " + pos);
            stepReturn = environment.step(Action.still,pos);
            pos.setFromReturn(stepReturn);
        } while (!stepReturn.isTerminal);

        StepReturn finalStepReturn = stepReturn;
        assertAll(
                () -> assertEquals(6, pos.x, DELTA),  //at goal, new position will be equal to present pos
                () -> assertEquals(2,pos.y, DELTA),
                () -> assertTrue(finalStepReturn.isTerminal),
                () -> assertEquals(-Environment.STILL_COST,finalStepReturn.reward,DELTA)
        );
    }

    @Test
    public void multipleMovesStillFromx0y1GivesMovingToObstacle() {
        State pos=new State(0,1);

        System.out.println("environment = " + environment);

        StepReturn stepReturn;
        do {
            System.out.println("pos = " + pos);
            stepReturn = environment.step(Action.still,pos);
            pos.setFromReturn(stepReturn);
        } while (!stepReturn.isTerminal);

        StepReturn finalStepReturn = stepReturn;
        assertAll(
                () -> assertEquals(2, pos.x, DELTA),
                () -> assertEquals(1,pos.y, DELTA),
                () -> assertTrue(finalStepReturn.isTerminal),
                () -> assertEquals(-Environment.CRASH_COST,finalStepReturn.reward,DELTA)
        );
    }



}
