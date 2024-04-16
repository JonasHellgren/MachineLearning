package policy_gradient_problems.environments.maze;

import common.other.Counter;
import lombok.Builder;
import policy_gradient_problems.domain.value_classes.StepReturn;

@Builder
public class AgentRunner {

   EnvironmentMaze environment;
   MazeAgentPPO agent;

    public int runTrainedAgent(StateMaze stateStart) {
        agent.setState(stateStart);
        Counter counter=new Counter();
        StepReturn<VariablesMaze> stepReturn;
        do {
            printStep();
            stepReturn = environment.step(agent.getState(),agent.chooseAction());
            agent.setState(stepReturn.state().copy());
            counter.increase();
        } while (!stepReturn.isTerminal() );
        return counter.getCount();
    }

    private void printStep() {
        System.out.println("state = " + agent.getState().getVariables().pos());
        System.out.println("probs = " + agent.actionProbabilitiesInPresentState());
        System.out.println("criticOut = " + agent.criticOut(agent.getState()));
    }

}
