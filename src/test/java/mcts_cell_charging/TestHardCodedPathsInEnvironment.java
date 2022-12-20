package mcts_cell_charging;

import mcts_spacegame.domains.models_space.EnvironmentShip;
import mcts_spacegame.classes.StepReturnGeneric;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.domains.models_space.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHardCodedPathsInEnvironment {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new EnvironmentShip(spaceGrid);
    }

    @Test
    public void moveStillFromx0y2GivesZeroReturn() {
        StateShip pos=StateShip.newStateFromXY(0,2);
        List<ActionInterface<ShipActionSet>> actions= Arrays.asList(ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(EnvironmentShip.STILL_COST,G, DELTA);
    }


    @Test
    public void moveFromx0y0Tox6y2GivesTwoMoveCost() {
        StateShip pos=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actions= Arrays.asList(ActionShip.newUp(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(-EnvironmentShip.MOVE_COST *2,G, DELTA);
    }

    @Test
    public void moveFromx0y0Tox1y0GivesCrashCost() {
        StateShip pos=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actions= Arrays.asList(ActionShip.newStill(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(-EnvironmentShip.CRASH_COST *1,G, DELTA);
    }

    private double getReturnForActions(StateShip pos, List<ActionInterface<ShipActionSet>> actions) {
        double G=0;
        for (ActionInterface<ShipActionSet> action: actions) {
            System.out.println("pos = " + pos);
            StepReturnGeneric<ShipVariables> stepReturn= environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            G=G+stepReturn.reward;

            if (stepReturn.isTerminal) {
                break;
            }
        }
        return G;
    }

}
