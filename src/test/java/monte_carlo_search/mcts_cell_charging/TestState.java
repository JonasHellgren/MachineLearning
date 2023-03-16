package monte_carlo_search.mcts_cell_charging;

import monte_carlo_tree_search.domains.battery_cell.CellVariables;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.domains.battery_cell.StateCell;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class TestState {

    static class GenericClass<T> {
        private int functionUser(Function<T,Integer> fcn, T state) {
            return fcn.apply(state);
        }
    }

    @Test
    public void testStatCell() {
        StateInterface<CellVariables> cellState=new StateCell(CellVariables.builder()
                .SoC(0.9).temperature(30).time(0)
                .build());
        System.out.println("cellState = " + cellState);
    }

    @Test public void testFunction() {
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        Function<CellVariables,Integer> fcn2 = ( a) -> (a.SoC<=0.5) ? actionTemplate.applicableActions().size():1;
        GenericClass<CellVariables> gc= new GenericClass<>();
        CellVariables stateCell=CellVariables.builder()
                .SoC(0.9).temperature(30).time(0)
                .build();
        int nofActions=gc.functionUser(fcn2,stateCell);

        System.out.println("nofActions = " + nofActions);

        Assert.assertEquals(1,nofActions);
    }




}
