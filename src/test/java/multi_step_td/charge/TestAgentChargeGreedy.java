package multi_step_td.charge;

import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedy;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestAgentChargeGreedy {
    public static final double SOC_INIT = 0.9;
    public static final int PROB_RANDOM = 0;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    AgentInterface<ChargeVariables> agent;

    StepReturn<ChargeVariables> stepReturn;

    @BeforeEach
    public void init() {
        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(15)
                .socA(SOC_INIT).socB(SOC_INIT)
                .build());
        agent = new AgentChargeGreedy(environment,state);
        stateCasted = (ChargeState) state;
    }

    @Test
    public void givenA20B25_whenChooseAction_thenFeasibleActionAWaits() {
        TestChargeHelper.setPosA.accept(state, 20);
        TestChargeHelper.setPosB.accept(state, 25);
        int action = getActionStepAndPrint();
        Range<Integer> actionFeasible = Range.between(0, 1);  //actions not moving A
        assertTrue(actionFeasible.contains(action));
        assertTrue(TestChargeHelper.samePosA.test(state, stepReturn.newState));
    }

    @Test
    public void givenA18B29_whenChooseAction_thenFeasibleAction() {
        TestChargeHelper.setPosA.accept(state, 18);
        TestChargeHelper.setPosB.accept(state, 29);
        int action = getActionStepAndPrint();
        assertTrue(action!=3);
        assertNotEquals(TestChargeHelper.posNewA.apply(stepReturn),TestChargeHelper.posNewB.apply(stepReturn));
    }

    @Test
    public void givenBPos18AndObstacleAndAAt12_thenAnyAction_thenBSamePosAndAChanged() {
        environmentCasted.setObstacle(true);
        TestChargeHelper.setPosB.accept(state, 18);
        TestChargeHelper.setPosA.accept(state, 17);
        int action = getActionStepAndPrint();
        Range<Integer> actionFeasible = Range.between(0, 1);  //actions not moving A
        assertTrue(actionFeasible.contains(action));
        assertTrue(TestChargeHelper.samePosA.test(state,stepReturn.newState));
        assertTrue(TestChargeHelper.samePosB.test(state,stepReturn.newState));
    }

    @Test
    public void givenA20B11_whenChooseAction_thenAvoidQue() {
        TestChargeHelper.setPosA.accept(state, 20);
        TestChargeHelper.setPosB.accept(state, 11);
        int action = getActionStepAndPrint();
        Range<Integer> actionFeasible = Range.between(2, 3);  //actions moving A
        assertTrue(actionFeasible.contains(action));
        assertFalse(TestChargeHelper.samePosA.test(state,stepReturn.newState));
        assertFalse(TestChargeHelper.samePosB.test(state,stepReturn.newState));
    }

    @Test
    public void when100teps_thenOkState() {
        for (int i = 0; i < 100 ; i++) {
            int action = getActionStepAndPrint();
            state.setFromReturn(stepReturn);
            if (stepReturn.isNewStateTerminal) {
                break;
            }
        }
        System.out.println("stepReturn.newState = " + stepReturn.newState);
        assertFalse(stepReturn.isNewStateFail);

    }



    private int getActionStepAndPrint() {
        int action= agent.chooseAction(PROB_RANDOM);
        stepReturn = environment.step(state, action);
        TestChargeHelper.printStepReturn(stepReturn);
        TestChargeHelper.printAction(action);
        return action;
    }


}