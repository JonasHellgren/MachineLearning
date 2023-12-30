package policy_gradient_problems.the_problems.cart_pole;

import common.Counter;
import lombok.Builder;
import policy_gradient_problems.common_generic.StepReturn;

@Builder
public class PoleHelper {
    EnvironmentPole environment;
    AgentPole agent;

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
