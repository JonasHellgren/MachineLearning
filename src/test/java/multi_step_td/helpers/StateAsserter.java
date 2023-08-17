package multi_step_td.helpers;

import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.helpers_specific.ForkAndMazeHelper;
import org.junit.Assert;

import java.util.function.Function;

@AllArgsConstructor
public class StateAsserter<S> {

    AgentNeuralInterface<S> agent;
    EnvironmentInterface<S> environment;
    public void assertAllStates(double value, double delta) {
        for (StateInterface<S> state:environment.stateSet()) {
            Assert.assertEquals(value, agent.readValue(state), delta);
        }
    }

    public void assertAllStates(Function<StateInterface<S>,Double> function, double delta) {
        for (StateInterface<S> state:environment.stateSet()) {
            Assert.assertEquals(function.apply(state), agent.readValue(state), delta);
        }
    }
}
