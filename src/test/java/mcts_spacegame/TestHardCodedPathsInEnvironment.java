package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHardCodedPathsInEnvironment {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    Environment environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new Environment(spaceGrid);
    }

    @Test
    public void moveStillFromx0y2GivesZeroReturn() {
        State pos=new State(0,2);
        List<ShipAction> actions= Arrays.asList(ShipAction.still, ShipAction.still, ShipAction.still, ShipAction.still, ShipAction.still, ShipAction.still);
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(Environment.STILL_COST,G, DELTA);
    }


    @Test
    public void moveFromx0y0Tox6y2GivesTwoMoveCost() {
        State pos=new State(0,0);
        List<ShipAction> actions= Arrays.asList(ShipAction.up, ShipAction.up, ShipAction.still, ShipAction.still, ShipAction.still, ShipAction.still);
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(-Environment.MOVE_COST *2,G, DELTA);
    }

    @Test
    public void moveFromx0y0Tox1y0GivesCrashCost() {
        State pos=new State(0,0);
        List<ShipAction> actions= Arrays.asList(ShipAction.still, ShipAction.up, ShipAction.still, ShipAction.still, ShipAction.still, ShipAction.still);
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(-Environment.CRASH_COST *1,G, DELTA);
    }

    private double getReturnForActions(State pos, List<ShipAction> actions) {
        double G=0;
        for (ShipAction action: actions) {
            System.out.println("pos = " + pos);
            StepReturn stepReturn= environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            G=G+stepReturn.reward;

            if (stepReturn.isTerminal) {
                break;
            }
        }
        return G;
    }

}
