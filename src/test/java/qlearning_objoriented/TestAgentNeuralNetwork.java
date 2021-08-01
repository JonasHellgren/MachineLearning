package qlearning_objoriented;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;

import java.util.List;

public class TestAgentNeuralNetwork {

    State s = new State();
    //State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.1;  //move transition

    @Before
    public void setup() {
    }

    @Test
    public void stateValue() {
        Assert.assertEquals(1, agent.state.getDiscreteVariable("roomNumber"), SMALL);
    }


    @Test
    public void addSomeMockTransitionsToReplayBuffer() {
        createMockReplayBuffer(agent);
        System.out.println(agent.replayBuffer);
    }

    @Test
    public void extractMiniBatchFromMockReplayBuffer() {
        createMockReplayBuffer(agent);
        List<Experience> miniBatch=agent.replayBuffer.getMiniBatch(3);
        System.out.println(agent.replayBuffer.bufferAsString(miniBatch));

        Assert.assertEquals(3, miniBatch.size(), SMALL);

    }

    @Test @Ignore
    public void trainAgentForRoom1() {
        trainAgentForRoom1AndGammaZero();
        State s = new State(agent.state);    s.setVariable("roomNumber", 1);
        System.out.println(agent.calcOutFromNetwork(s,agent.network));

        //s.setVariable("roomNumber", 2);  System.out.println(agent.calcOutFromNetwork(s,agent.network));

        env.PrintQsa(agent);
    }


    @Test  @Ignore
    public void bestActionForRoom1ShallBe5() {
        trainAgentForRoom1AndGammaZero();
        State s = new State(agent.state);     s.setVariable("roomNumber", 1);
        System.out.println(agent.chooseBestAction(s));
        Assert.assertEquals(5, agent.chooseBestAction(s), SMALL);
    }

    @Test  @Ignore
    public void maxQForRoom1ShallBe0d9() {
        trainAgentForRoom1AndGammaZero();
        State s = new State(agent.state);     s.setVariable("roomNumber", 1);
        System.out.println(agent.findMaxQ(s));
        Assert.assertEquals(0.9, agent.findMaxQ(s), SMALL);
    }

    @Test
    public void outForRoom1Action5ShallBe0d9() {
        agent.GAMMA=0; //only care about short term reward => easy to test
        trainAgentForRoom1AndGammaZero();
        State s = new State(agent.state);     s.setVariable("roomNumber", 1);
        System.out.println(agent.calcOutFromNetwork(s,agent.network));
        Assert.assertEquals(0.9, agent.readMemory(s,5), SMALL);
    }

    @Test
    public void copiedNetworksParameterShallGiveSameOutput() {
        State s = new State(agent.state);     s.setVariable("roomNumber", 1);
        System.out.println(agent.calcOutFromNetwork(s,agent.network));

        agent.networkTarget.setParams(agent.network.params());
        System.out.println(agent.calcOutFromNetwork(s,agent.networkTarget));

        //all() is true if ll elements within this array are true
        Assert.assertTrue(agent.calcOutFromNetwork(s,agent.networkTarget).eq(agent.calcOutFromNetwork(s,agent.network)).all());
    }


    private void trainAgentForRoom1AndGammaZero() {
        agent.GAMMA=0; //only care about short term reward => easy to test
        createMockReplayBuffer(agent);
        for( int i=0; i<300; i++ ) {
            List<Experience> miniBatch=agent.replayBuffer.getMiniBatch(6);
            DataSetIterator iterator =agent.createTrainingData(miniBatch);
            agent.network.fit(iterator);
        }
    }


    public void createMockReplayBuffer(SixRoomsAgentNeuralNetwork agent) {

        for (int action = 0; action < 6; action++) {
            State s = new State(agent.state);
            s.setVariable("roomNumber", 1);
            StepReturn stepReturn = env.step(action, s);  //new in step method => no need for new here
            Experience experience = new Experience(s, action, stepReturn);
            agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_MAXSIZE);
        }



    }

}
