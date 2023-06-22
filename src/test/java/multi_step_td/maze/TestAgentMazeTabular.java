package multi_step_td.maze;

import multi_step_temp_diff.agents.AgentMazeTabular;
import multi_step_temp_diff.environments.MazeEnvironment;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.interfaces_and_abstract.AgentTabularInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAgentMazeTabular {

    public static final double DELTA = 0.001;
    AgentTabularInterface<MazeVariables> agent;
   // AgentAbstract<MazeVariables> agentCasted;
    EnvironmentInterface<MazeVariables> environment;

    @Before
    public void init() {
        environment=new MazeEnvironment();
        agent= AgentMazeTabular.newDefault(environment);
      //  agentCasted=(AgentAbstract<MazeVariables>) agent;
    }

    @Test
    public void whenNoWriting_thenRead0() {
        agent.clear();
        Assert.assertEquals(AgentMazeTabular.VALUE_IF_NOT_PRESENT,
                agent.readValue(MazeState.newFromXY(0,0)), DELTA);
    }

    @Test
    public void whenWritingValueX0XYAs0_thenRead0() {
        int value = 0;
        agent.writeValue(MazeState.newFromXY(0,0), value);
        Assert.assertEquals(value,agent.readValue(MazeState.newFromXY(0,0)), DELTA);
    }

    @Test
    public void whenWritingValueX0XYAs10_thenRead10() {
        int value = 10;
        agent.writeValue(MazeState.newFromXY(0,0), value);
        Assert.assertEquals(value,agent.readValue(MazeState.newFromXY(0,0)), DELTA);
    }

    @Test
    public void whenWritingValueX0XYAs10AndWritingValueX1XY1As20_thenCorrectRead() {
        int value00 = 10, value11=20;
        agent.writeValue(MazeState.newFromXY(0,0), value00);
        agent.writeValue(MazeState.newFromXY(1,1), value11);

        Assert.assertEquals(value00,agent.readValue(MazeState.newFromXY(0,0)), DELTA);
        Assert.assertEquals(value11,agent.readValue(MazeState.newFromXY(1,1)), DELTA);

    }


}
