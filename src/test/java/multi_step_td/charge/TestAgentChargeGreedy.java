package multi_step_td.charge;

import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedy;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static java.lang.System.out;
import static multi_step_td.charge.TwoRunningHelper.assertAction;
import static multi_step_td.charge.TwoRunningHelper.setState;
import static org.junit.jupiter.api.Assertions.*;


public class TestAgentChargeGreedy {

    public static final int PROB_RANDOM = 0;
    public static final int TIME = 10;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;
    StepReturn<ChargeVariables> stepReturn;



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
            "11,22,0.9,0.9, false,12,22",
    })
    public void whenNoObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        environmentCasted.setObstacle(false);
        TwoRunningHelper reader= TwoRunningHelper.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        int action = createAgentAndGetAction(state);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
        assertAction(reader, stepReturn);
    }

    @ParameterizedTest
    @CsvSource({
            "0,10,0.9,0.9, false,1,11",   //B at split, no short term difference between destionations
    })
    public void whenBAtSplitRandomDestination_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        environmentCasted.setObstacle(false);
        TwoRunningHelper reader= TwoRunningHelper.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        int action = createAgentAndGetAction(state);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
        int posB = stepReturn.newState.getVariables().posB;
        assertTrue(posB==11 || posB==20);
    }


    @ParameterizedTest
    @CsvSource({
            "6,8,0.9,0.9, false,7,8",   //gap decreases due to obstacle
            "7,8,0.9,0.9, false,7,8"    //A blocked by B, B blocked by obstacle
    })
    public void whenObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        environmentCasted.setObstacle(true);
        TwoRunningHelper reader= TwoRunningHelper.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        int action = createAgentAndGetAction(state);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
        assertAction(reader, stepReturn);
    }

    @Test
    @Disabled  //flytta till AgentChargeGreedyRuleForChargeDecisionPoint
    public void when30steps_thenFailState() {
        StateInterface<ChargeVariables> state = setState(TwoRunningHelper.builder()
                .posA(0).posB(1).socA(0.5).socB(0.5).build());
        for (int i = 0; i < 30 ; i++) {
            int action = createAgentAndGetAction(state);
            out.println("state = " + state);
            stepReturn=environment.step(state,action);
            state.setFromReturn(stepReturn);
            if (stepReturn.isNewStateTerminal) {
                break;
            }
        }
        out.println("stepReturn.newState = " + stepReturn.newState);
        assertTrue(stepReturn.isNewStateFail);

    }


    private int createAgentAndGetAction(StateInterface<ChargeVariables> state) {
        AgentInterface<ChargeVariables>  agent = new AgentChargeGreedy(environment, state);
        return agent.chooseAction(PROB_RANDOM);
    }





}
