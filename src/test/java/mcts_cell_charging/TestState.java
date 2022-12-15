package mcts_cell_charging;

import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.StateInterface;
import mcts_spacegame.models_battery_cell.StateCell;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class TestState {

    static class GenericClass<T extends StateInterface> {
        private int functionUser(Function<T,Integer> fcn, T state) {
            return fcn.apply(state);
        }
    }

    @Test
    public void testStatCell() {
        StateInterface cellState=StateCell.builder()
                .SoC(0.9).temperature(30).time(0)
                .build();
        System.out.println("cellState = " + cellState);
    }

    @Test public void testFunction() {
        Function<StateCell,Integer> fcn2 = ( a) -> (a.SoC<=0.5) ? Action.applicableActions().size():1;
        GenericClass<StateCell> gc= new GenericClass<>();
        StateCell stateCell=StateCell.builder()
                .SoC(0.9).temperature(30).time(0)
                .build();
        int nofActions=gc.functionUser(fcn2,stateCell);

        System.out.println("nofActions = " + nofActions);

        Assert.assertEquals(1,nofActions);
    }




}
