package mcts_cell_charging;

import mcts_spacegame.models_battery_cell.ActionCell;
import mcts_spacegame.models_battery_cell.ActionInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

public class TestActionCell {

    private static final double DELTA = 0.1;
    private static final int INTEGER_ACTION_VALUE = 0;
    private static final double MIN_RELATIVE_CURRENT = 0.5;
    ActionInterface<Integer> actionCell;

    @Before
    public void init() {
        actionCell= ActionCell.builder().nofCurrentLevels(3).minRelativeCurrent(MIN_RELATIVE_CURRENT).build();
    }

    @Test
    public void  setAndGetAction() {
        actionCell.setAction(INTEGER_ACTION_VALUE);
        Assert.assertEquals(INTEGER_ACTION_VALUE,actionCell.getAction(), DELTA);
    }

    @Test public void applicableActions() {
        Set<Integer> actionSet=actionCell.applicableActions();
        Assert.assertTrue(actionSet.containsAll(Arrays.asList(0,1,2)));
    }

    @Test public void getRelativeCurrent() {
        ActionCell actionCellCasted=(ActionCell) actionCell;
        Assert.assertEquals(MIN_RELATIVE_CURRENT,actionCellCasted.getRelativeCurrent(),DELTA);
    }


}
