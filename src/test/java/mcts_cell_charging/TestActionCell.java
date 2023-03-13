package mcts_cell_charging;

import monte_carlo_tree_search.domains.battery_cell.ActionCell;
import monte_carlo_tree_search.interfaces.ActionInterface;
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
    public void whenSetAction_thenCorrectGetAction() {
        actionCell.setValue(INTEGER_ACTION_VALUE);
        Assert.assertEquals(INTEGER_ACTION_VALUE,actionCell.getValue(), DELTA);
    }

    @Test
    public void whenApplicAtions_then012() {
        Set<Integer> actionSet=actionCell.applicableActions();
        Assert.assertTrue(actionSet.containsAll(Arrays.asList(0,1,2)));
    }

    @Test
    public void whenGetRelativeCurrent_thenGetDefault() {
        ActionCell actionCellCasted=(ActionCell) actionCell;
        Assert.assertEquals(MIN_RELATIVE_CURRENT,actionCellCasted.getRelativeCurrent(),DELTA);
    }

    @Test(expected=IllegalArgumentException.class)
    public void whenSettingNonValidNofCurrentLevels_thenException() {
        actionCell= ActionCell.builder().nofCurrentLevels(1).minRelativeCurrent(MIN_RELATIVE_CURRENT).build();
    }


}
