package monte_carlo_search.mcts_cell_charging;

import monte_carlo_tree_search.domains.battery_cell.CellVariables;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.domains.battery_cell.StateCell;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
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
    public void whenCopyState_thenCorrectSoC() {
        System.out.println("stateNew variables = " + state.getVariables());

        Assert.assertEquals(0.5,state.copy().getVariables().SoC,DELTA);
    }

    @Test public void whenCopyFromReturn_thenCorrectValues() {
        state=stepReturn.copyState();
        System.out.println("stateNew = " + state);
        Assert.assertEquals(
                state.getVariables().temperature,
                stepReturn.newState.getVariables().temperature,
                DELTA);
    }

    @Test public void whenSetFromReturn_thenCorrectValues() {
        state.setFromReturn(stepReturn);
        System.out.println("stateNew = " + state);
        Assert.assertEquals(
                state.getVariables().temperature,
                stepReturn.newState.getVariables().temperature,
                DELTA);
    }



}
