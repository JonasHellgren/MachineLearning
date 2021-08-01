package qlearning_objoriented;

import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;

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

            for (int i = 0; i < 100; i++) {
                Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
                experience.pExpRep.beError=Math.pow(aChosen,1)*0.1;
                agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_MAXSIZE);
            }

        }
    }

    @Test
    public void printDummyBuffer() {
        System.out.println(agent.replayBuffer);
        agent.replayBuffer.calcPrioInReplayBufferFromBellmanError();
        agent.replayBuffer.calcSortCriteriaInReplayBuffer();
        agent.replayBuffer.calcPrioInReplayBufferFromBellmanError();

        for(Experience exp:agent.replayBuffer.buffer)
            System.out.println(exp.pExpRep);

        List<Experience> miniBatch= agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(1);
        System.out.println(miniBatch);

    }

    @Test
    public void action5ChoosenMoreOftenThanAction0AndAction3() {

        int nAction0=0;
        int nAction3=0;
        int nAction5=0;
        for (int i = 0; i < 1000 ; i++) {
            List<Experience> miniBatch= agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(1);

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
        for (int i = 0; i < 100000 ; i++) {
            List<Experience> miniBatch= agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(10);

        }

        System.out.println("time ms:"+(System.currentTimeMillis()-startTime));

    }

}
