package qlearning_objoriented;

import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class TestPrioritzedExpReplay {

    State s = new State();
    //State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.1;  //move transition

    @Before
    public void setup() {

        for (int aChosen = 0; aChosen < (5+1) ; aChosen++) {
            StepReturn stepReturn = env.step(aChosen, agent.state);

            for (int i = 0; i < 1; i++) {
                Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
                experience.pExpRep.beError=Math.pow(aChosen,1)*0.1;
                agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_SIZE);
            }

        }
    }

    @Test
    public void printDummyBuffer() {
        System.out.println(agent.replayBuffer);
        List<Experience> miniBatch= agent.replayBuffer.
                getMiniBatchPrioritizedExperienceReplay(1,agent,0);

        for(Experience exp:agent.replayBuffer.buffer)
            System.out.println(exp.pExpRep);


        System.out.println(miniBatch);

    }

    @Test
    public void action5ChoosenMoreOftenThanAction0AndAction3() {

        int nAction0=0;
        int nAction3=0;
        int nAction5=0;
        for (int i = 0; i < 1000 ; i++) {
            List<Experience> miniBatch= agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(1,agent,(double) i/1000);

            if (miniBatch.get(0).action==0)
                nAction0++;

            if (miniBatch.get(0).action==3)
                nAction3++;

            if (miniBatch.get(0).action==5)
                nAction5++;
        }

        System.out.println("nAction0:"+nAction0+", nAction3:"+nAction3+", nAction5:"+nAction5);

        Assert.assertTrue(nAction0<nAction5);
        Assert.assertTrue(nAction3<nAction5);

    }

    @Test
    public void timeTest() {

        long startTime=System.currentTimeMillis();
        List<Experience> miniBatch=new ArrayList<>();
        for (int i = 0; i < 100000 ; i++) {
            miniBatch= agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(10,agent,0);
        }

        System.out.println("buffer.size:"+agent.replayBuffer.buffer.size()+", miniBatch.size:"+miniBatch.size()+", time ms:"+(System.currentTimeMillis()-startTime));

    }

    @Test
    public void createTrainingDataShallChangeBellmanErrorsInRepBuffer() {

        int BATCHLENGTH=agent.replayBuffer.buffer.size();
        System.out.println(agent.replayBuffer);
        List<Experience> miniBatch= agent.replayBuffer.
                getMiniBatchPrioritizedExperienceReplay(BATCHLENGTH,agent,0);

        System.out.println(agent.replayBuffer.pExpRepInfoAsString(miniBatch));

        agent.createTrainingData(miniBatch);

        System.out.println(agent.replayBuffer.pExpRepInfoAsString(agent.replayBuffer.buffer));

        //System.out.println(agent.replayBuffer);

    }

}
