package multi_step_td;

import common.RandUtils;
import multi_step_temp_diff.interfaces_and_abstract.ReplayBufferInterface;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestReplayBuffer {

    private static final int n = 4;
    ReplayBufferInterface buffer;

    @Before
    public void init() {
        buffer=ReplayBufferNStep.newDefault();
    }

    @Test
    public void whenAddingOneExperience_thenExists() {
        buffer.addExperience(NstepExperience.builder().stateToUpdate(0).sumOfRewards(0d)
                .stateToBackupFrom(3).isBackupStatePresent(true).build());
        System.out.println("buffer = " + buffer);
        Assert.assertEquals(1,buffer.size());
    }

    @Test
    public void whenAddingTenExperiences_thenExists() {

        for (int i = 0; i < 10 ; i++) {
            double sumOfRewards= RandUtils.getRandomDouble(0,10);
            buffer.addExperience(NstepExperience.builder()
                    .stateToUpdate(i).sumOfRewards(sumOfRewards).stateToBackupFrom(i + n).isBackupStatePresent(true)
                    .build());
        }

        List<NstepExperience> miniBuffer=buffer.getMiniBatch(5);
        System.out.println("buffer = " + buffer);
        miniBuffer.forEach(System.out::println);

        Assert.assertEquals(10,buffer.size());
        Assert.assertEquals(5,miniBuffer.size());

    }

}
