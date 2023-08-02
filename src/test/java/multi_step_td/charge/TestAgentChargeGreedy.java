package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedy;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


public class TestAgentChargeGreedy {
    public static final double SOC_INIT = 0.9;
    public static final int PROB_RANDOM = 0;
    public static final int TIME = 10;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    StepReturn<ChargeVariables> stepReturn;

    @Builder
    record ArgumentReader(int posA, int posB, double socA, double socB,
                          boolean isFailState, int posANew, int posBNew) {

        public static ArgumentReader of(ArgumentsAccessor args) {
            return  ArgumentReader.builder()
                    .posA(args.getInteger(0)).posB(args.getInteger(1))
                    .socA(args.getDouble(2)).socB(args.getDouble(3))
                    .isFailState(args.getBoolean(4)).posANew(args.getInteger(5)).posBNew(args.getInteger(6))
                    .build();
        }
    }

    @BeforeEach
    public void init() {
        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        stateCasted = (ChargeState) state;
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,0.9,0.9, false,1,2",
            "20,30,0.22,0.22, false,20,40",
            "20,30,0.22,0.52, false,20,40",    //A blocked by B, B should not have decided to charge
    })
    public void whenNoObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        environmentCasted.setObstacle(false);
        ArgumentReader reader= ArgumentReader.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        int action = createAgentAndGetAction(state);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
        assertAction(reader, stepReturn);
    }

    @Test
    public void when30steps_thenOkState() {
        StateInterface<ChargeVariables> state = setState(ArgumentReader.builder()
                .posA(0).posB(1).socA(0.5).socB(0.5).build());
        for (int i = 0; i < 30 ; i++) {
            int action = createAgentAndGetAction(state);
            System.out.println("state = " + state);
            stepReturn=environment.step(state,action);
            state.setFromReturn(stepReturn);
            if (stepReturn.isNewStateTerminal) {
                break;
            }
        }
        System.out.println("stepReturn.newState = " + stepReturn.newState);
        assertFalse(stepReturn.isNewStateFail);

    }


    private int createAgentAndGetAction(StateInterface<ChargeVariables> state) {
        AgentInterface<ChargeVariables>  agent = new AgentChargeGreedy(environment, state);
        return agent.chooseAction(PROB_RANDOM);
    }


    private static void assertAction(ArgumentReader reader, StepReturn<ChargeVariables> stepReturn) {
        assertEquals(reader.isFailState, stepReturn.isNewStateFail);
        assertEquals(reader.posANew, stepReturn.newState.getVariables().posA);
        assertEquals(reader.posBNew, stepReturn.newState.getVariables().posB);

    }

    @NotNull
    private static StateInterface<ChargeVariables> setState(ArgumentReader reader) {
        return new ChargeState(ChargeVariables.builder()
                .posA(reader.posA).posB(reader.posB)
                .socA(reader.socA).socB(reader.socB)
                .time(TIME)
                .build());
    }

    /*


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





    private int getActionStepAndPrint() {
        int action= agent.chooseAction(PROB_RANDOM);
        stepReturn = environment.step(state, action);
        TestChargeHelper.printStepReturn(stepReturn);
        TestChargeHelper.printAction(action);
        return action;
    }


     */

}
