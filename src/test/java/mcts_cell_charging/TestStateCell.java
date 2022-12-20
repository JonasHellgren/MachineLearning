package mcts_cell_charging;

import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.domains.models_battery_cell.StateCell;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestStateCell {

    private static final double DELTA = 0.1;
    StateInterface<CellVariables> state;
    StepReturnGeneric<CellVariables> stepReturn;

    @Before
    public void init()  {
        state=new StateCell(CellVariables.builder().SoC(0.5).build());
        stepReturn=StepReturnGeneric.<CellVariables>builder().newState(
                StateCell.newWithVariables(CellVariables.builder().temperature(88).build())).build();
    }

    @Test
    public void copy() {
        System.out.println("state variables = " + state.getVariables());

        Assert.assertEquals(0.5,state.copy().getVariables().SoC,DELTA);
    }

    @Test public void copyFromReturn() {
        state=stepReturn.copyState();
        System.out.println("state = " + state);
        Assert.assertEquals(
                state.getVariables().temperature,
                stepReturn.newState.getVariables().temperature,
                DELTA);
    }

    @Test public void setFromReturn() {
        state.setFromReturn(stepReturn);
        System.out.println("state = " + state);
        Assert.assertEquals(
                state.getVariables().temperature,
                stepReturn.newState.getVariables().temperature,
                DELTA);
    }



}
