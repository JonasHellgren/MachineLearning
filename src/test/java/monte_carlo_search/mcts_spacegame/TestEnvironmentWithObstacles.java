package monte_carlo_search.mcts_spacegame;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironmentWithObstacles {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new EnvironmentShip(spaceGrid);
    }

    @Test
    public void whenMoveStillFromx0y0_thenObstacleCrash() {
        StateShip pos=StateShip.newStateFromXY(0,0);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newStill(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(0,stepReturn.newState.getVariables().y, DELTA),
                () -> assertTrue(stepReturn.isTerminal),
                () -> assertTrue(stepReturn.isFail),
                () -> assertEquals(-EnvironmentShip.CRASH_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void whenMoveUpFromx0y0_thenNoObstacleCrash() {
        StateShip pos=StateShip.newStateFromXY(0,0);
        StepReturnGeneric<ShipVariables> stepReturn= environment.step(ActionShip.newUp(),pos);
        System.out.println("stepReturn = " + stepReturn);
        assertAll(
                () -> assertEquals(1,stepReturn.newState.getVariables().x, DELTA),
                () -> assertEquals(1,stepReturn.newState.getVariables().y, DELTA),
                () -> assertFalse(stepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.MOVE_COST,stepReturn.reward,DELTA)
        );
    }


    @Test
    public void whenMultipleMovesStillFromx0y2G_thenMovingToGoal() {
        StateShip pos=StateShip.newStateFromXY(0,2);

        System.out.println("environment = " + environment);

        StepReturnGeneric<ShipVariables> finalStepReturn = stepToTerminal(pos);
        assertAll(
                () -> assertEquals(6, pos.getX(), DELTA),  //outside grid
                () -> assertEquals(2,pos.getY(), DELTA),
                () -> assertTrue(finalStepReturn.isTerminal),
                () -> assertFalse(finalStepReturn.isFail),
                () -> assertEquals(-EnvironmentShip.STILL_COST,finalStepReturn.reward,DELTA)
        );
    }

    @NotNull
    private StepReturnGeneric<ShipVariables> stepToTerminal(StateShip pos) {
        StepReturnGeneric<ShipVariables> stepReturn;
         do {
            System.out.println("pos = " + pos);
            stepReturn = environment.step(ActionShip.newStill(), pos);
            System.out.println("stepReturn = " + stepReturn);
            pos.setFromReturn(stepReturn);
        } while (!stepReturn.isTerminal);
        return stepReturn;
    }

    @Test
    public void multipleMovesStillFromx0y1GivesMovingToObstacle() {
        StateShip pos=StateShip.newStateFromXY(0,1);

        System.out.println("environment = " + environment);

        StepReturnGeneric<ShipVariables> finalStepReturn = stepToTerminal(pos);
        assertAll(
                () -> assertEquals(2, pos.getX(), DELTA),
                () -> assertEquals(1,pos.getY(), DELTA),
                () -> assertTrue(finalStepReturn.isTerminal),
                () -> assertEquals(-EnvironmentShip.CRASH_COST,finalStepReturn.reward,DELTA)
        );
    }



}
