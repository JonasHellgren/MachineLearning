package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.*;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/***
 * action=0 -> command A = 0, action=2 -> command A = 1
 */

public class TestChargeEnvironmentBTrapped {

    public static final double SOC_B = 0.9;
    public static final int TRAP_POS = 29, TIME = 10;

    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings settings;

    @Builder
    record ArgumentReader(int posA, double socA, int action,boolean isFailState, int posNew, boolean isSoCIncreased) {

        public ArgumentReader of(ArgumentsAccessor args) {
            return  ArgumentReader.builder()
                    .posA(args.getInteger(0)).socA(args.getDouble(1)).action(args.getInteger(2))
                    .isFailState(args.getBoolean(3)).posNew(args.getInteger(4)).isSoCIncreased(args.getBoolean(5))
                    .build();
        }
    }


    @BeforeEach
    public void init() {
        settings=ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(settings);
        environmentCasted=(ChargeEnvironment) environment;
    }


    @ParameterizedTest
    @CsvSource({
            "0,0.9,0, false,1,false","0,0.9,2, false,1,false",
            "7,0.9,0, false,7,false","7,0.9,2, false,8,false",
            "10,0.9,0, false,11,false","10,0.9,2, false,20,false","10,0.2,1, true,11,false",
            "20,0.9,0, false,20,false","20,0.9,2, false,30,false",
            "30,0.9,0, false,40,true","30,0.9,2, false,40,true",
            "32,0.9,0, false,22,true","32,0.9,2, false,22,true",
            "22,0.9,0, false,22,true", "22,0.9,2, false,12,true"
    })

    public void whenNoObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        int posA = arguments.getInteger(0);
        double socA = arguments.getDouble(1);
        int action = arguments.getInteger(2);
        boolean isFailState = arguments.getBoolean(3);
        int posANew = arguments.getInteger(4);
        boolean isSoCIncreased = arguments.getBoolean(5);

        StateInterface<ChargeVariables> state = new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(TRAP_POS)
                .socA(socA).socB(SOC_B)
                .time(TIME)
                .build());

        environmentCasted.setObstacle(false);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);

        System.out.println("stepReturn.newState = " + stepReturn.newState);

        assertEquals(isFailState,stepReturn.isNewStateFail);
        assertEquals(posANew,stepReturn.newState.getVariables().posA);
        boolean isNewSoCALarger = stepReturn.newState.getVariables().socA > socA;
        assertEquals(isSoCIncreased, isNewSoCALarger);

    }

    /*

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

  */

}
