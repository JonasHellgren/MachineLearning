package policy_gradient_problems.environments.cart_pole;

import common.other.Counter;
import lombok.AllArgsConstructor;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import common.reinforcment_learning.value_classes.StepReturn;

@AllArgsConstructor
public class PoleAgentOneEpisodeRunner {
    EnvironmentPole environment;
    AgentI<VariablesPole> agent;

    public static PoleAgentOneEpisodeRunner newOf(EnvironmentPole environment,
                                                  AgentI<VariablesPole> agent) {
        return new PoleAgentOneEpisodeRunner(environment, agent);
    }

    public int runTrainedAgent(StatePole stateStart) {
        agent.setState(stateStart);
        Counter counter = new Counter();
        StepReturn<VariablesPole> stepReturn;
        do {
            stepReturn = environment.step(agent.getState(), agent.chooseAction());
            agent.setState(stepReturn.state().copy());
            counter.increase();
        } while (!stepReturn.isTerminal());
        return counter.getCount();
    }
}
