package mcts_cell_charging;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.battery_cell.*;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEnvironmentCell {

    private static final double DELTA = 0.01;
    private static final int MAX_RELATIVE_CURRENT = 1;
    private static final double MIN_RELATIVE_CURRENT = 0.5;
    private static final double SOC_INIT = 0.5;
    private static final int TEMPERATURE_INIT = 20;
    private static final int TIME_INIT = 0;
    private static final int DT = 10;
    private static final int NOF_CURRENT_LEVELS = 5;
    EnvironmentGenericInterface<CellVariables, Integer> environment;
    StateCell state;
    CellVariables variables;
    ActionCell action;

    @Before
    public void init() {
        environment=new EnvironmentCell(CellSettings.builder().dt(DT).build());
        state= new StateCell(CellVariables.builder()
                .time(TIME_INIT).temperature(TEMPERATURE_INIT).SoC(SOC_INIT).build());
        variables=state.getVariables();
        action= ActionCell.builder()
                .minRelativeCurrent(MIN_RELATIVE_CURRENT)
                .maxRelativeCurrent(MAX_RELATIVE_CURRENT)
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();
    }

    @Test
    public void whenAction4_thenMaxRelCurr() {
        action.setValue(NOF_CURRENT_LEVELS-1);
        System.out.println("action.getRelativeCurrent() = " + action.getRelativeCurrent());
        Assert.assertEquals(MAX_RELATIVE_CURRENT,action.getRelativeCurrent(), DELTA);
    }

    @Test
    public void whenAction0_thenMinRelCurr() {
        action.setValue(0);
        System.out.println("action.getRelativeCurrent() = " + action.getRelativeCurrent());
        Assert.assertEquals(MIN_RELATIVE_CURRENT,action.getRelativeCurrent(), DELTA);
    }

    @Test
    public void WHenCharge_thenIncreaseAllStateVariables() {
        action.setValue(4);
        StepReturnGeneric<CellVariables> sr=environment.step(action,state);
        System.out.println("sr.newState = " + sr.newState);
        CellVariables newVariables=sr.newState.getVariables();

        Assert.assertEquals(DT,newVariables.time,DELTA);
        Assert.assertTrue(newVariables.SoC>variables.SoC);
        Assert.assertTrue(newVariables.temperature> variables.temperature);

    }

    @Test
    public void whenSmallerChargeCurrent_thenGiveLessIncreaseInTempAndSoC() {
        action.setValue(0);
        StepReturnGeneric<CellVariables> sr0=environment.step(action,state);
        System.out.println("sr.newState = " + sr0.newState);

        action.setValue(4);
        StepReturnGeneric<CellVariables> sr4=environment.step(action,state);
        System.out.println("sr.newState = " + sr4.newState);
        CellVariables newVariables0=sr0.newState.getVariables();
        CellVariables newVariables4=sr4.newState.getVariables();

        Assert.assertTrue(newVariables4.SoC>newVariables0.SoC);
        Assert.assertTrue(newVariables4.temperature>newVariables0.temperature);

    }


}
