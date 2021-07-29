package qlearning_objoriented;


import org.junit.Assert;
import org.junit.Test;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRoomsAgentTabular;

import java.util.Arrays;
import java.util.List;

public class TestAgent {

    State state = new State();
    SixRooms env=new SixRooms();
    SixRoomsAgentTabular agent=new SixRoomsAgentTabular(env.parameters);
    public final double SMALL = 0.001;  //move transition

    @Test
    public void StateValue() {
        Assert.assertEquals(1,agent.state.getDiscreteVariable("roomNumber"),SMALL);
    }

    @Test
    public void PrintQsa() {
        agent.PrintQsa();
    }

    @Test
    public void chooseBestAction() {
        agent.state.setVariable("roomNumber",1);
        state.copyState(agent.state);
        agent.writeMemory(state,2,1d);
        Integer aBest=agent.chooseBestAction(agent.state);
        Assert.assertEquals(2,aBest,SMALL);
    }

    @Test
    public void findMaxQ() {
        agent.state.setVariable("roomNumber",1);
        state.copyState(agent.state);
        agent.writeMemory(state,2,1d);
        Double qMax=agent.findMaxQ(agent.state);
        Assert.assertEquals(1,qMax,SMALL);
    }

    @Test
    public void feasibleActionsInRoom1Are3and5() {
        System.out.println("agent.state:"+agent.state);
        agent.state.setVariable("roomNumber",1);
        List<Integer> feasibleActions=env.getFeasibleActions(agent.state);
        Assert.assertEquals(Arrays.asList( 3,  5),feasibleActions);
    }

    @Test
    public void stepRoom1Action2ShallGiveFailReward() {
        agent.state.setVariable("roomNumber",1);
        SixRooms.StepReturn stepReturn=env.step(2,agent.state);
        Assert.assertEquals(1,agent.state.getDiscreteVariable("roomNumber"),SMALL);
        Assert.assertEquals(env.parameters.R_FAIL,stepReturn.reward,SMALL);
        agent.state.copyState(stepReturn.state);
        Assert.assertEquals(2,agent.state.getDiscreteVariable("roomNumber"),SMALL);
    }

    @Test
    public void stepRoom2Action2ShallGiveFailReward() {
        agent.state.setVariable("roomNumber",2);
        SixRooms.StepReturn stepReturn=env.step(3,agent.state);
        Assert.assertEquals(env.parameters.R_MOVE,stepReturn.reward,SMALL);
        agent.state.copyState(stepReturn.state);
        Assert.assertEquals(3,agent.state.getDiscreteVariable("roomNumber"),SMALL);
    }

}
