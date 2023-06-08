package multi_step_td;

import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.models.StepReturn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTestAgentTabular {

    private static final double DELTA = 0.1;
    private static final int PROB_RANDOM = 0;
    private static final double PROB_RANDOM1 = 0.1;
    EnvironmentInterface environment;
    AgentInterface agent;
    AgentForkTabular agentCasted;

    @Before
    public void init() {
        environment=new ForkEnvironment();
        agent= AgentForkTabular.newDefault();
        agentCasted=(AgentForkTabular) agent;       //to access class specific methods
    }

    @Test
    public void givenDefaultAgent_whenStartState_then0() {
        Assert.assertEquals(0,agent.getState());
    }

    @Test
    public void givenDefaultAgent_whenReadValueStartSate_then0() {
        Assert.assertEquals(0,agent.readValue(agent.getState()),DELTA);
    }

    @Test
    public void givenDefaultAgent_whenRandomAction_then0or1() {
        final int action = agent.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState14_whenBestAction_then0or1() {
        agent= AgentForkTabular.builder().environment(environment).state(14).build();
        final int action = agent.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState9_whenBestAction_then1() {
        agent= AgentForkTabular.builder().environment(environment).state(9).build();
        final int action = agent.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState5ValueIn7Is10_whenBestAction_then1() {
        agent= AgentForkTabular.builder().environment(environment).state(5).build();
        AgentForkTabular agentCasted=(AgentForkTabular) agent;
        agentCasted.writeValue(7,1d);
        Assert.assertEquals(1,agent.chooseBestAction(agent.getState()));
    }

    @Test
    public void givenState5ValueIn7Is10_whenActionZeroRandProb_then1() {
        agent= AgentForkTabular.builder().environment(environment).state(5).build();
        agentCasted.writeValue(7,1d);
        Assert.assertEquals(1,agent.chooseAction(PROB_RANDOM));
    }

    @Test
    public void givenDefaultAgent_whenStep_thenNewStateIs1() {
        StepReturn sr=environment.step(agent.getState(), agent.chooseAction(PROB_RANDOM1));
        Assert.assertEquals(1,sr.newState);
    }

    @Test
    public void givenDefaultAgent_whenWriting10inState9_thenCorrectRead() {
        agentCasted.writeValue(9,10d);
        Assert.assertEquals(10d,agent.readValue(9),DELTA);
    }

}
