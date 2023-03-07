package mcts_cell_charging;

import monte_carlo_tree_search.domains.battery_cell.ActionCell;
import monte_carlo_tree_search.domains.battery_cell.StateCell;
import org.junit.Assert;
import org.junit.Test;

public class TestGenericEnvironment {

    public interface EnvironmentInterfaceMock<TS, TA> {
        int step(TA action, TS state);
    }

    static class MockCellEnvironment implements EnvironmentInterfaceMock<StateCell, ActionCell> {
        @Override
        public int step(ActionCell action, StateCell state) {
            return 0;
        }
    }


    @Test
    public void whenMockedCellEnvAndStep_then0StepReturn() {
        EnvironmentInterfaceMock<StateCell, ActionCell> env=new MockCellEnvironment();
        int stepReturn= env.step(ActionCell.newDefault(), StateCell.newDefault());
        System.out.println("stepReturn = " + stepReturn);
        Assert.assertEquals(0,stepReturn);
    }

}
