package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models.SpaceGrid;
import mcts_spacegame.models.SpaceGridInterface;
import mcts_spacegame.models.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

        Assert.assertEquals(1,stepReturn.newPosition.x, DELTA);
        Assert.assertEquals(0,stepReturn.newPosition.y, DELTA);
        Assert.assertFalse(stepReturn.isTerminal);
        Assert.assertEquals(0,stepReturn.reward,DELTA);
    }


    @Test
    public void moveUpFromx0y0() {
        State pos=new State(0,0);
        StepReturn stepReturn= environment.step(Action.up,pos);

        System.out.println("stepReturn = " + stepReturn);

        Assert.assertEquals(1,stepReturn.newPosition.x, DELTA);
        Assert.assertEquals(0,stepReturn.newPosition.y, DELTA);
        Assert.assertTrue(stepReturn.isTerminal);
        Assert.assertEquals(Environment.CRASH_COST,stepReturn.reward,DELTA);
    }



}
