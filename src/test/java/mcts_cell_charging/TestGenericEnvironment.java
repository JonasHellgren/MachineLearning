package mcts_cell_charging;

import mcts_spacegame.model_mcts.ActionInterface;
import mcts_spacegame.model_mcts.StateInterface;
import mcts_spacegame.models_battery_cell.ActionCell;
import mcts_spacegame.models_battery_cell.StateCell;
import org.junit.Assert;
import org.junit.Test;

public class TestGenericEnvironment {

    public interface EnvironmentInterfaceMock<TS extends StateInterface, TA extends ActionInterface> {
        int step(TA action, TS state);
    }

    static class MockCellEnvironment implements EnvironmentInterfaceMock<StateCell, ActionCell> {
        @Override
        public int step(ActionCell action, StateCell state) {
            return 0;
        }
    }


    @Test
    public void testStep() {
        EnvironmentInterfaceMock<StateCell, ActionCell> env=new MockCellEnvironment();
        int stepReturn= env.step(ActionCell.max, StateCell.builder().build());
        System.out.println("stepReturn = " + stepReturn);
        Assert.assertEquals(0,stepReturn);
    }

}
