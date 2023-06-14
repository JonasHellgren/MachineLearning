package multi_step_td;

import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.models.StepReturn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

public class TestTestAgentTabular {

    private static final double DELTA = 0.1, PROB_RANDOM = 0, PROB_RANDOM1 = 0.1;
    EnvironmentInterface<ForkVariables> environment;
    AgentInterface<ForkVariables> agent;
    AgentForkTabular agentCasted;
    AgentInfo<ForkVariables> agentInfo;

    Function<StateInterface<ForkVariables>,Integer> getPos=(s) -> s.getVariables().position;


    @Before
    public void init() {
        environment=new ForkEnvironment();
        agent= AgentForkTabular.newDefault(environment);
        agentCasted=(AgentForkTabular) agent;       //to access class specific methods
        agentInfo=new AgentInfo<>(agent);
    }

    @Test
    public void givenDefaultAgent_whenStartState_then0() {
        Assert.assertEquals(0,(int) getPos.apply(agent.getState()));
    }

    @Test
    public void givenDefaultAgent_whenReadValueStartSate_then0() {
        Assert.assertEquals(0,agent.readValue(agent.getState()),DELTA);
    }

    @Test
    public void givenDefaultAgent_whenRandomAction_then0or1() {
        final int action = agentCasted.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState14_whenBestAction_then0or1() {
        agent= AgentForkTabular.newWithStartState(environment, ForkState.newFromPos(14));
        final int action = agentCasted.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState9_whenBestAction_then1() {
       agent= AgentForkTabular.newWithStartState(environment,ForkState.newFromPos(9));
        final int action = agentCasted.chooseRandomAction();
        Assert.assertTrue(action ==0 || action ==1);
    }

    @Test
    public void givenState5ValueIn7Is10_whenBestAction_then1() {
        agent= AgentForkTabular.newWithStartState(environment,ForkState.newFromPos(5));
        agentCasted.writeValue(ForkState.newFromPos(7),1d);
        Assert.assertEquals(1,agentCasted.chooseBestAction(agent.getState()));
    }

    @Test
    public void givenState5ValueIn7Is10_whenActionZeroRandProb_then1() {
        agent= AgentForkTabular.newWithStartState(environment,ForkState.newFromPos(5));
        agentCasted.writeValue(ForkState.newFromPos(7),1d);
        Assert.assertEquals(1,agent.chooseAction(PROB_RANDOM));
    }

    @Test
    public void givenDefaultAgent_whenStep_thenNewStateIs1() {
        StepReturn<ForkVariables> sr=environment.step(agent.getState(), agent.chooseAction(PROB_RANDOM1));
        Assert.assertEquals(1,(int) getPos.apply(sr.newState));
    }

    @Test
    public void givenDefaultAgent_whenWriting10inState9_thenCorrectRead() {
        agentCasted.writeValue(ForkState.newFromPos(9),10d);
        Assert.assertEquals(10d,agent.readValue(ForkState.newFromPos(9)),DELTA);
    }

    @Test
    public void givenState_whenAction_thenNofStepsIsIncreased() {
        Assert.assertEquals(0,agentInfo.getNofSteps());
        agent.chooseAction(PROB_RANDOM);
        Assert.assertEquals(1,agentInfo.getNofSteps());

    }

}
