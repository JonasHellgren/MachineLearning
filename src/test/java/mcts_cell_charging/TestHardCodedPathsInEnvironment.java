package mcts_cell_charging;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

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
    public void whenMoveStillFromx0y2_thenZeroReturn() {
        StateShip pos=StateShip.newStateFromXY(0,2);
        List<ActionInterface<ShipActionSet>> actions= Arrays.asList(ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(EnvironmentShip.STILL_COST,G, DELTA);
    }


    @Test
    public void whenMoveFromx0y0Tox6y2_thenTwoMoveCost() {
        StateShip pos=StateShip.newStateFromXY(0,0);
        List<ActionInterface<ShipActionSet>> actions= Arrays.asList(ActionShip.newUp(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());
        double G = getReturnForActions(pos, actions);
        System.out.println("G = " + G);
        Assert.assertEquals(-EnvironmentShip.MOVE_COST *2,G, DELTA);
    }

    @Test
    public void whenMoveFromx0y0Tox1y0_thenCrashCost() {
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
