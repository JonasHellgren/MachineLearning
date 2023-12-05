package policy_gradient_problems.the_problems.cart_pole;

import common.Counter;
import lombok.Builder;

@Builder
public class PoleHelper {
    EnvironmentPole environment;
    AgentPole agent;

    public int runTrainedAgent(StatePole stateStart) {
        agent.setState(stateStart);
        Counter counter=new Counter();
        StepReturnPole stepReturn;
        do {
            stepReturn = environment.step(agent.chooseAction(), agent.getState());
            agent.setState(stepReturn.newState().copy());
            counter.increase();
        } while (!stepReturn.isTerminal() );
        return counter.getCount();
    }
}
