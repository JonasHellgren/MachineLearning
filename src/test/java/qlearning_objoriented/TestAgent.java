package qlearning_objoriented;


import org.junit.Assert;
import org.junit.Test;
import udemy_Java_AI_courses.AI4refined.qlearning.Constants;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.FiveRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.FiveRoomsAgentTabular;

public class TestAgent {

    State state = new State();
    FiveRooms env=new FiveRooms();
    FiveRoomsAgentTabular agent=new FiveRoomsAgentTabular(env.parameters);

    @Test
    public void StateValue() {
        Assert.assertEquals(agent.state.getDiscreteVariable("roomNumber"),0,0.01);
    }

    @Test
    public void PrintQsa() {

        for(int state = 0; state< env.parameters.stateSpace.size(); ++state) {
            for(int action=0;action<env.parameters.discreteActionsSpace.size();++action) {
                System.out.printf("%.1f ", agent.Qsa[state][action]);
            }
            System.out.println();
        }
    }

    @Test
    public void chooseBestAction() {

        env.parameters.R_FAIL
        Integer aBest=agent.chooseBestAction(state, env.parameters);
        System.out.println("aBest:"+aBest);

    }


}
