package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedyRuleForChargeDecisionPoint;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import static java.lang.System.out;

public class RunnerAgentChargeGreedyRuleForChargeDecisionPoint {

    public static final int PROB_RANDOM = 0;
    public static final double SOC_LIMIT = 0.5;
    public static final int NOF_STEPS = 100;

    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironment environmentCasted;
    static StateInterface<ChargeVariables> state;
    static ChargeState stateCasted;

    public static void main(String[] args) {



        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        stateCasted = (ChargeState) state;


        StateInterface<ChargeVariables> state = new ChargeState(ChargeVariables.builder()
                .posA(0).posB(1)
                .build());

        for (int i = 0; i < NOF_STEPS; i++) {
            int action = createAgentAndGetAction(state);

            out.println("state = " + state);
            StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
            state.setFromReturn(stepReturn);
            if (stepReturn.isNewStateTerminal) {
                break;
            }
        }

    }


    private static int createAgentAndGetAction(StateInterface<ChargeVariables> state) {
        ChargeEnvironmentSettings settings = environmentCasted.getSettings();
        AgentInterface<ChargeVariables> agent =
                new AgentChargeGreedyRuleForChargeDecisionPoint(environment, state, SOC_LIMIT, settings);
        return agent.chooseAction(PROB_RANDOM);
    }

}
