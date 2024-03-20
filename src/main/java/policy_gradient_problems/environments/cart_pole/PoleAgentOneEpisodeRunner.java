package policy_gradient_problems.environments.cart_pole;

import common.Counter;
import lombok.Builder;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.value_classes.StepReturn;

@Builder
public class PoleAgentOneEpisodeRunner {
    EnvironmentPole environment;
    AgentI<VariablesPole> agent;

    public int runTrainedAgent(StatePole stateStart) {
        agent.setState(stateStart);
        Counter counter=new Counter();
        StepReturn<VariablesPole> stepReturn;
        do {
            stepReturn = environment.step(agent.getState(),agent.chooseAction());
            agent.setState(stepReturn.state().copy());
            counter.increase();
        } while (!stepReturn.isTerminal() );
        return counter.getCount();
    }
}
