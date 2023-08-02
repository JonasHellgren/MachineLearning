package multi_step_td.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.SiteStateRules;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/***
 * action=0 -> command A = 0, action=2 -> command A = 1
 */

public class TestChargeEnvironmentBTrapped {

    public static final double SOC_INIT = 0.9;
    public static final double SOC_DELTA = 0.0001;
    public static final double DELTA_REWARD = 0.001;
    public static final int TRAP_POS = 29;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    StepReturn<ChargeVariables> stepReturn;
    ChargeEnvironmentSettings settings;

    @BeforeEach
    public void init() {
        settings=ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment();
        environmentCasted=(ChargeEnvironment) environment;
        state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(TRAP_POS)
                .socA(SOC_INIT).socB(SOC_INIT)
                .build());
        stateCasted=(ChargeState) state;
    }

    @Test
    public void given0_thenAction0_thenBNotChangedAndTimeIncreased() {
        stepReturn = environment.step(state, 0);
        TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(TRAP_POS, (int) TestChargeHelper.posNewB.apply(stepReturn));
        assertEquals(SOC_INIT,TestChargeHelper.socNewB.apply(stepReturn),SOC_DELTA);
        assertFalse(stepReturn.isNewStateTerminal);
        assertEquals(1,(int) TestChargeHelper.time.apply(stepReturn));
    }

    @Test
    public void givenAPos0_thenAction0_thenNewPos1AndSoCDecreasedAndNotTerminal() {
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(1, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
        assertFalse(stepReturn.isNewStateTerminal);
    }

    @Test
    public void givenAPos9_thenAction0_thenNewPos9AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,9);
        System.out.println("state = " + state);
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(10, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos9_thenAction2_thenNewPos10AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,9);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(10, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos10_thenAction0_thenNewPos11AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,10);
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(11, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos10_thenAction2_thenNewPos20AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,10);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(20, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }


    @Test
    public void givenAPos20_thenAction0_thenNewPos20AndSoCNoChangeAndSoCEqCostQue() {
        TestChargeHelper.setPosA.accept(state,20);
        stepReturn = environment.step(state, 0);
        TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(20, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertEquals(SOC_INIT,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
        assertEquals(-settings.costQue(),stepReturn.reward, DELTA_REWARD);

    }

    @Test
    public void givenAPos20_thenAction2_thenNewPos21AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,20);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(21, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos19_thenAction2_thenNewPos0AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,19);
        stepReturn = environment.step(state, 2);
        TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(0, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos25_thenAction0Or1_thenNewPos26AndSoCIncreased() {
        TestChargeHelper.setPosA.accept(state,25);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(26, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
    }

    @Test
    public void givenAPos15_thenAction0Or1_thenNewPos16AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,15);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(16, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }


    @Test
    public void givenAPos18AndNoObstacle_thenAction2_thenNewPos19AndSoCDecreased() {
        environmentCasted.setObstacle(false);
        TestChargeHelper.setPosA.accept(state,18);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(19, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos18AndObstacle_thenAction01Or2_thenNewPos18AndSoCSame() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosA.accept(state,18);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(18, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertEquals(SOC_INIT,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
    }

    @Test
    public void givenAPos29AndNoObstacle_thenAction2_thenNewPos19AndSoCIncreasedRewardIsCostCharge() {
        environmentCasted.setObstacle(false);
        TestChargeHelper.setPosA.accept(state,29);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(19, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
        assertEquals(-settings.costCharge(),stepReturn.reward,DELTA_REWARD);
    }

    @Test
    public void givenAPos29AndObstacle_thenAction01Or2_thenNewPos29AndSoCIncreased() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosA.accept(state,29);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(29, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
    }


    @Test
    public void givenAPos25SoCFull_thenAction0Or1_thenNewPos26AndSoCSame() {
        TestChargeHelper.setPosA.accept(state,25);
        double socInit = 1;
        TestChargeHelper.setSocA.accept(state, socInit);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertEquals(26, (int) TestChargeHelper.posNewA.apply(stepReturn));
        assertEquals(socInit,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
    }

    @Test
    public void given0PoorSoC_thenTwoSteps_thenFailState() {
        TestChargeHelper.setSocA.accept(state, settings.socBad()+SOC_DELTA);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
        state.setFromReturn(stepReturn);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        assertTrue(stepReturn.isNewStateTerminal);
        assertEquals(settings.rewardBad(),stepReturn.reward,DELTA_REWARD);
    }

  

}
