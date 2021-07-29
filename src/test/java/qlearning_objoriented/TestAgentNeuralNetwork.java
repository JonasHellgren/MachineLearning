package qlearning_objoriented;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRoomsAgentNeuralNetwork;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRoomsAgentTabular;

import java.util.List;

public class TestAgentNeuralNetwork {

    State s = new State();
    //State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;  //move transition

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
        List<SixRoomsAgentNeuralNetwork.Experience> miniBatch=agent.replayBuffer.getMiniBatch(3);
        System.out.println(agent.replayBuffer.bufferAsString(miniBatch));

    }

    @Test
    public void trainNetworkFromMockReplayBuffer() {

        createMockReplayBuffer(agent);
        for( int i=0; i<100; i++ ) {
            List<SixRoomsAgentNeuralNetwork.Experience> miniBatch=agent.replayBuffer.getMiniBatch(6);
            DataSetIterator iterator =agent.createTrainingData(miniBatch);
            agent.network.fit(iterator);
        }

        State s = new State(agent.state);
        s.setVariable("roomNumber", 1);
        agent.printNetworkOutput(s);

    }


    public void createMockReplayBuffer(SixRoomsAgentNeuralNetwork agent) {

        for (int action = 0; action < 6; action++) {
            State s = new State(agent.state);
            s.setVariable("roomNumber", 1);
            SixRooms.StepReturn stepReturn = env.step(action, s);  //new in step method => no need for new here
            SixRoomsAgentNeuralNetwork.Experience experience = agent.new Experience(s, action, stepReturn);
            agent.replayBuffer.addExperience(experience);
        }



    }

}
