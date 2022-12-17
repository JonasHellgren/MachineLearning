package mcts_cell_charging;

import mcts_spacegame.models_battery_cell.StateInterface;
import mcts_spacegame.models_battery_cell.StateCell;
import mcts_spacegame.models_battery_cell.StepReturnGeneric;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestStateCell {

    private static final double DELTA = 0.1;
    StateInterface<StateCell> state;
    StepReturnGeneric<StateCell> stepReturn;

    @Before
    public void init()  {
        state=StateCell.builder().SoC(0.5).build();
        stepReturn=StepReturnGeneric.<StateCell>builder().newState(
                StateCell.builder().temperature(88).build()
        ).build();
    }

    @Test
    public void copy() {
        System.out.println("state = " + state);

        System.out.println("state.copy() = " + state.copy());
        StateCell castedState=(StateCell) state.copy();
        Assert.assertEquals(0.5,castedState.SoC,DELTA);

    }

    @Test public void copyFromReturn() {

        state=stepReturn.copyState();
        System.out.println("state = " + state);
        StateCell castedState=(StateCell) state;
        Assert.assertEquals(castedState.temperature,stepReturn.newState.temperature, DELTA);

    }

}
