package multi_step_td.charge;

import multi_step_temp_diff.environments.ChargeEnvironment;
import multi_step_temp_diff.environments.ChargeState;
import multi_step_temp_diff.environments.ChargeVariables;
import multi_step_temp_diff.domain.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestChargeEnvironmentBRunning {
    public static final double SOC_INIT = 0.9;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    StepReturn<ChargeVariables> stepReturn;

    @BeforeEach
    public void init() {
        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(15)
                .socA(SOC_INIT).socB(SOC_INIT)
                .build());
        stateCasted = (ChargeState) state;
    }


    @Test
    public void givenA20B25_thenAction0_thenPosAndSoCsAsExpected() {
        TestChargeHelper.setPosA.accept(state, 20);
        TestChargeHelper.setPosB.accept(state, 25);
        stepReturn = environment.step(state, 0);
        TestChargeHelper.printStepReturn(stepReturn);
        assertTrue(TestChargeHelper.samePosA.test(state, stepReturn.newState));
        assertFalse(TestChargeHelper.samePosB.test(state, stepReturn.newState));
        assertTrue(TestChargeHelper.sameSoCA.test(state, stepReturn.newState));
        assertFalse(TestChargeHelper.sameSoCB.test(state, stepReturn.newState));
    }

    @Test
    public void givenA20B25_thenAction2AndOneMoreStep_thenFailDueToTwoCharging() {
        TestChargeHelper.setPosA.accept(state, 20);
        TestChargeHelper.setPosB.accept(state, 25);
        stepReturn = environment.step(state, 2);
        oneMoreStep();
        TestChargeHelper.printStepReturn(stepReturn);
        assertTrue(stepReturn.isNewStateTerminal);
        assertEquals(ChargeEnvironment.SiteState.isTwoCharging,
                environmentCasted.getSiteStateRules().getSiteState(state));
    }


    @Test
    public void givenA18B29_thenAction3AndOneMoreStep_thenFailDueToSamePos() {
        TestChargeHelper.setPosA.accept(state, 18);
        TestChargeHelper.setPosB.accept(state, 29);
        stepReturn = environment.step(state, 3);
        TestChargeHelper.printStepReturn(stepReturn);
        oneMoreStep();
        TestChargeHelper.printStepReturn(stepReturn);
        assertTrue(stepReturn.isNewStateTerminal);
        assertEquals(ChargeEnvironment.SiteState.isTwoAtSamePos,
                environmentCasted.getSiteStateRules().getSiteState(state));
    }

    @Test
    public void givenBPos18AndObstacleAndAAt12_thenAnyAction_thenBSamePosAndAChanged() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosB.accept(state, 18);
        TestChargeHelper.setPosA.accept(state, 12);
        stepReturn = environment.step(state, 2);

        assertFalse(TestChargeHelper.samePosA.test(state, stepReturn.newState));
        assertTrue(TestChargeHelper.samePosB.test(state, stepReturn.newState));
    }

    private void oneMoreStep() {
        state.setFromReturn(stepReturn);
        stepReturn = environment.step(state, TestChargeHelper.randomAction.get());
    }

}
