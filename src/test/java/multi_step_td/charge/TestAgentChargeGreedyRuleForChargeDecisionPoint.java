package multi_step_td.charge;

import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedyRuleForChargeDecisionPoint;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static multi_step_td.charge.TwoRunningHelper.assertAction;
import static multi_step_td.charge.TwoRunningHelper.setState;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAgentChargeGreedyRuleForChargeDecisionPoint {

    public static final int PROB_RANDOM = 0;
    public static final double SOC_LIMIT = 0.5;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    StateInterface<ChargeVariables> state;
    ChargeState stateCasted;

    @BeforeEach
    public void init() {
        environment = new ChargeEnvironment();
        environmentCasted = (ChargeEnvironment) environment;
        stateCasted = (ChargeState) state;
    }


    @ParameterizedTest
    @CsvSource({
            "0,10,0.9,0.9, false,1,11",   //B at split high soc -> no charge
            "0,10,0.9,0.4, false,1,20",   //B at split low soc ->  charge
            "10,0,0.9,0.9, false,11,1",   //A at split high soc -> no charge
            "10,0,0.4,0.9, false,20,1",   //A at split low soc ->  charge
            "0,1,0.4,0.9, false,1,2",   //none at split ->  move on
            "20,30,0.22,0.52, false,20,40",    //A blocked by B, B should not have decided to charge
            "11,22,0.9,0.9, false,12,22",
            "7,8,0.9,0.9, false,8,9","7,8,0.4,0.3, false,8,9",

    })
    public void whenBAtSplitRandomDestination_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        environmentCasted.setObstacle(false);
        TwoRunningHelper reader= TwoRunningHelper.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        int action = createAgentAndGetAction(state);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,action);
        int posB = stepReturn.newState.getVariables().posB;
        assertAction(reader,stepReturn);
    }


    private int createAgentAndGetAction(StateInterface<ChargeVariables> state) {
        ChargeEnvironmentSettings settings = environmentCasted.getSettings();
        AgentChargeNeuralSettings agentSettings=AgentChargeNeuralSettings.newDefault();
        AgentInterface<ChargeVariables> agent =
                new AgentChargeGreedyRuleForChargeDecisionPoint(environment, state, SOC_LIMIT, settings,agentSettings);
        return agent.chooseAction(PROB_RANDOM);
    }

}
