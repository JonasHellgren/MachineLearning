package qlearning_objoriented;

import org.junit.Assert;
import org.junit.Test;
import udemy_Java_AI_courses.AI4refined.qlearning.Constants;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.FiveRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.FiveRoomsAgentTabular;

import java.util.List;
import java.util.Random;

public class TestLearning {

    State sNew = new State();
    FiveRooms env=new FiveRooms();
    FiveRoomsAgentTabular agent=new FiveRoomsAgentTabular(env.parameters);
    public final double SMALL = 0.001;
    private final Random random=new Random();

    @Test
    public void SimulateFromRoom1ShallBePositiveQForAction5inRoom1OrRoom4() {
        agent.state.setVariable("roomNumber",0);
        simulate(true);
        agent.PrintQsa();
        State state1 = new State(agent.state);        state1.setVariable("roomNumber",1);
        State state4 = new State(agent.state);        state4.setVariable("roomNumber",4);
        Assert.assertTrue(agent.readMemory(state1,5)>SMALL | agent.readMemory(state4,5)>SMALL);
    }

@Test
    public void runLearning() {
    // episode: a full iteration when the agent starts from a random state and finds the terminal state
    for (int iEpisode = 0; iEpisode < env.parameters.NUM_OF_EPISODES; ++iEpisode) {
        int startState = random.nextInt(env.parameters.nofStates);
        agent.state.setVariable("roomNumber",startState);
        if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
        simulate(false);
    }

    agent.PrintQsa();
    showPolicy();

}

    private void simulate(boolean printFlag) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the terminal state

        FiveRooms.StepReturn stepReturn;
        do {

            List<Integer> aSet = env.getFeasibleActions(agent.state);  // get available actions
            FiveRooms.EnvironmentParameters p = env.parameters;

            int aChosen = agent.chooseRandomAction(aSet);
            stepReturn=env.step(aChosen,agent.state);
            sNew.copyState(stepReturn.state);
            double maxQ = agent.findMaxQ(sNew);
            double qOld = agent.readMemory(agent.state,aChosen);
            double qNew = qOld + p.ALPHA*(stepReturn.reward+p.GAMMA*maxQ-qOld);

            if (printFlag) {
                System.out.println(agent.state);
                System.out.println(aSet);
                System.out.println(stepReturn);
            }

            agent.writeMemory(agent.state,aChosen,stepReturn.termState?stepReturn.reward:qNew);

            agent.state.copyState(sNew);
        } while(!stepReturn.termState);
    }

    public void showPolicy() {
        // we consider every single state as a starting state
        // until we find the terminal state: we walk according to best action

        for(int startState=0; startState<env.parameters.nofStates; startState++) {

            int state=startState;
            FiveRooms.StepReturn stepReturn;
            agent.state.setVariable("roomNumber",state);
            System.out.print("Policy: " + agent.state.getDiscreteVariable("roomNumber"));
            while (!env.isTerminalState(agent.state)) {
                int bestA = agent.chooseBestAction(agent.state);
                stepReturn=env.step(bestA,agent.state);
                agent.state.copyState(stepReturn.state);
                System.out.print(" -> " + agent.state.getDiscreteVariable("roomNumber"));
            }
            System.out.println();
        }
    }

}
