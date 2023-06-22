package multi_step_td.charge;

import common.RandUtils;
import multi_step_temp_diff.environment_helpers.SiteStateRules;
import multi_step_temp_diff.environments.ChargeEnvironment;
import multi_step_temp_diff.environments.ChargeState;
import multi_step_temp_diff.environments.ChargeVariables;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/***
 * action=0 -> command A = 0, action=2 -> command A = 1
 */

public class TestChargeEnvironmentBTrapped {

    public static final double SOC_INIT = 0.9;
    public static final int POS_TRAP = ChargeEnvironment.POS_MAX;
    public static final double SOC_DELTA = 0.0001;
    public static final double DELTA_REWARD = 0.001;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    StepReturn<ChargeVariables> stepReturn;

    @Before
    public void init() {
        environment = new ChargeEnvironment();
        environmentCasted=(ChargeEnvironment) environment;
        state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(POS_TRAP)
                .socA(SOC_INIT).socB(SOC_INIT)
                .build());
        stateCasted=(ChargeState) state;
    }

    @Test
    public void given0_thenAction0_thenBNotChangedAndTimeIncreased() {
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(POS_TRAP, (int) TestChargeHelper.posNewB.apply(stepReturn));
        Assert.assertEquals(SOC_INIT,TestChargeHelper.socNewB.apply(stepReturn),SOC_DELTA);
        Assert.assertFalse(stepReturn.isNewStateTerminal);
        Assert.assertEquals(1,(int) TestChargeHelper.time.apply(stepReturn));
    }

    @Test
    public void givenAPos0_thenAction0_thenNewPos1AndSoCDecreasedAndNotTerminal() {
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(1, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
        Assert.assertFalse(stepReturn.isNewStateTerminal);
    }

    @Test
    public void givenAPos9_thenAction0_thenNewPos9AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,9);
        System.out.println("state = " + state);
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(10, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos9_thenAction2_thenNewPos10AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,9);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(10, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos10_thenAction0_thenNewPos11AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,10);
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(11, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos10_thenAction2_thenNewPos20AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,10);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(20, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }


    @Test
    public void givenAPos20_thenAction0_thenNewPos20AndSoCNoChangeAndSoCEqCostQue() {
        TestChargeHelper.setPosA.accept(state,20);
        stepReturn = environment.step(state, 0);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(20, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertEquals(SOC_INIT,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
        Assert.assertEquals(-ChargeEnvironment.COST_QUE,stepReturn.reward, DELTA_REWARD);

    }

    @Test
    public void givenAPos20_thenAction2_thenNewPos21AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,20);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(21, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos19_thenAction2_thenNewPos0AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,19);
        stepReturn = environment.step(state, 2);
        TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(0, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos25_thenAction0Or1_thenNewPos26AndSoCIncreased() {
        TestChargeHelper.setPosA.accept(state,25);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(26, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
    }

    @Test
    public void givenAPos15_thenAction0Or1_thenNewPos16AndSoCDecreased() {
        TestChargeHelper.setPosA.accept(state,15);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(16, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }


    @Test
    public void givenAPos18AndNoObstacle_thenAction2_thenNewPos19AndSoCDecreased() {
        environmentCasted.setObstacle(false);
        TestChargeHelper.setPosA.accept(state,18);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(19, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) < SOC_INIT);
    }

    @Test
    public void givenAPos18AndObstacle_thenAction01Or2_thenNewPos18AndSoCSame() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosA.accept(state,18);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(18, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertEquals(SOC_INIT,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
    }

    @Test
    public void givenAPos29AndNoObstacle_thenAction2_thenNewPos19AndSoCIncreasedRewardIsCostCharge() {
        environmentCasted.setObstacle(false);
        TestChargeHelper.setPosA.accept(state,29);
        stepReturn = environment.step(state, 2);
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(19, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
        Assert.assertEquals(-ChargeEnvironment.COST_CHARGE,stepReturn.reward,DELTA_REWARD);
    }

    @Test
    public void givenAPos29AndObstacle_thenAction01Or2_thenNewPos29AndSoCIncreased() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosA.accept(state,29);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(29, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertTrue(TestChargeHelper.socNewA.apply(stepReturn) > SOC_INIT);
    }


    @Test
    public void givenAPos25SoCFull_thenAction0Or1_thenNewPos26AndSoCSame() {
        TestChargeHelper.setPosA.accept(state,25);
        double socInit = 1;
        TestChargeHelper.setSocA.accept(state, socInit);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertEquals(26, (int) TestChargeHelper.posNewA.apply(stepReturn));
        Assert.assertEquals(socInit,TestChargeHelper.socNewA.apply(stepReturn),SOC_DELTA);
    }

    @Test
    public void given0PoorSoC_thenTwoSteps_thenFailState() {
        TestChargeHelper.setSocA.accept(state, SiteStateRules.SOC_BAD+SOC_DELTA);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
        state.setFromReturn(stepReturn);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
         TestChargeHelper.printStepReturn(stepReturn);
        Assert.assertTrue(stepReturn.isNewStateTerminal);
        Assert.assertEquals(ChargeEnvironment.R_BAD,stepReturn.reward,DELTA_REWARD);
    }

  

}
