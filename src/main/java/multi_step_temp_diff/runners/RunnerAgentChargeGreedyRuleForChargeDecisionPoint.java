package multi_step_temp_diff.runners;

import common.Counter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedyRuleForChargeDecisionPoint;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import static java.lang.System.out;

@Log
public class RunnerAgentChargeGreedyRuleForChargeDecisionPoint {

    public static final int PROB_RANDOM = 0;
    public static final double SOC_LIMIT = 0.5;
    public static final int NOF_STEPS = 100;

    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentLambdas lambdas;

    public static void main(String[] args) {

        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        StateInterface<ChargeVariables> state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(1)
                .build());
        lambdas = new ChargeEnvironmentLambdas(environmentCasted.getSettings());

        Counter counterNofStepsChargeQue = new Counter(0, Integer.MAX_VALUE);
        for (int i = 0; i < NOF_STEPS; i++) {
            int action = createAgentAndGetAction(state);
            somePrinting(state);
            StepReturn<ChargeVariables> stepReturn = stepAndSetState(state, action);
            maybeIncreaseCounter(state, counterNofStepsChargeQue, stepReturn);
            if (stepReturn.isNewStateTerminal) {
                break;
            }
        }

        out.println("nofStepsChargeQue = " + counterNofStepsChargeQue.getCount());

    }

    private static StepReturn<ChargeVariables> stepAndSetState(StateInterface<ChargeVariables> state, int action) {
        var stepReturn = environment.step(state, action);
        state.setFromReturn(stepReturn);
        return stepReturn;
    }


    private static void maybeIncreaseCounter(StateInterface<ChargeVariables> state,
                                             Counter counterNofStepsChargeQue,
                                             StepReturn<ChargeVariables> stepReturn) {
        if (lambdas.isStillAtChargeQuePosFromStates.test(state, stepReturn.newState)) {
            counterNofStepsChargeQue.increase();
        }
    }

    private static void somePrinting(StateInterface<ChargeVariables> state) {
        if (lambdas.isAnyAtChargeDecisionPos.test(state)) {
            out.println("---- Some vehicle at charge decision pos, time = " + state.getVariables().time);
        }
        out.println("state = " + state);
    }


    private static int createAgentAndGetAction(StateInterface<ChargeVariables> state) {
        ChargeEnvironmentSettings settings = environmentCasted.getSettings();
        AgentChargeNeuralSettings agentSettings=AgentChargeNeuralSettings.newDefault();
        AgentInterface<ChargeVariables> agent =
                new AgentChargeGreedyRuleForChargeDecisionPoint(environment, state, SOC_LIMIT, settings,agentSettings);
        return agent.chooseAction(PROB_RANDOM);
    }

}
