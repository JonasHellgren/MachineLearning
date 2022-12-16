package mcts_cell_charging;

import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_battery_cell.*;
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
    EnvironmentGenericInterface<StateCell, ActionCell> environment;
    StateCell state;
    ActionCell action;

    @Before
    public void init() {
        environment=new EnvironmentCell(CellSettings.builder().dt(DT).build());
        state= StateCell.builder()
                .time(TIME_INIT).temperature(TEMPERATURE_INIT).SoC(SOC_INIT).build();
        action= ActionCell.builder()
                .minRelativeCurrent(MIN_RELATIVE_CURRENT).maxRelativeCurrent(MAX_RELATIVE_CURRENT).nofCurrentLevels(5).build();
    }

    @Test
    public void action4() {
        action.setLevel(4);
        System.out.println("action.getRelativeCurrent() = " + action.getRelativeCurrent());
        Assert.assertEquals(MAX_RELATIVE_CURRENT,action.getRelativeCurrent(), DELTA);
    }

    @Test
    public void action0() {
        action.setLevel(0);
        System.out.println("action.getRelativeCurrent() = " + action.getRelativeCurrent());
        Assert.assertEquals(MIN_RELATIVE_CURRENT,action.getRelativeCurrent(), DELTA);
    }

    @Test
    public void chargeShallIncreaseAllStateVariables() {
        action.setLevel(4);
        StepReturnGeneric<StateCell> sr=environment.step(action,state);
        System.out.println("sr.newState = " + sr.newState);

        Assert.assertEquals(DT,sr.newState.time,DELTA);
        Assert.assertTrue(sr.newState.SoC>state.SoC);
        Assert.assertTrue(sr.newState.temperature> state.temperature);

    }

    @Test
    public void smallerChargeCurrentShallGiveLessIncreaseInTempAndSoC() {
        action.setLevel(0);
        StepReturnGeneric<StateCell> sr0=environment.step(action,state);
        System.out.println("sr.newState = " + sr0.newState);

        action.setLevel(4);
        StepReturnGeneric<StateCell> sr4=environment.step(action,state);
        System.out.println("sr.newState = " + sr4.newState);

        Assert.assertTrue(sr4.newState.SoC>sr0.newState.SoC);
        Assert.assertTrue(sr4.newState.temperature>sr0.newState.temperature);

    }


}
