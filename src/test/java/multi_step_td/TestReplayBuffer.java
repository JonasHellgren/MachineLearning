package multi_step_td;

import common.other.CpuTimer;
import common.other.RandUtils;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestReplayBuffer {

    private static final int n = 4;
    public static final CpuTimer CPU_TIMER = CpuTimer.newWithTimeBudgetInMilliSec(0);
    ReplayBufferInterface<ForkVariables> buffer;

    @Before
    public void init() {
        buffer= ReplayBufferNStepUniform.newDefault();
    }

    @Test
    public void whenAddingOneExperience_thenExists() {
        buffer.addExperience(NstepExperience.<ForkVariables>builder().stateToUpdate(ForkState.newFromPos(0)).sumOfRewards(0d)
                .stateToBackupFrom(ForkState.newFromPos(3)).isBackupStatePresent(true).build(),CPU_TIMER);
        System.out.println("buffer = " + buffer);
        Assert.assertEquals(1,buffer.size());
    }

    @Test
    public void whenAddingTenExperiences_thenExists() {

        for (int i = 0; i < 10 ; i++) {
            double sumOfRewards= RandUtils.getRandomDouble(0,10);
            buffer.addExperience(NstepExperience.<ForkVariables>builder()
                    .stateToUpdate(ForkState.newFromPos(i)).sumOfRewards(sumOfRewards)
                    .stateToBackupFrom(ForkState.newFromPos(i + n)).isBackupStatePresent(true)
                    .build(), CPU_TIMER);
        }

        List<NstepExperience<ForkVariables>> miniBuffer=buffer.getMiniBatch(5);
        System.out.println("buffer = " + buffer);
        miniBuffer.forEach(System.out::println);

        Assert.assertEquals(10,buffer.size());
        Assert.assertEquals(5,miniBuffer.size());

    }

}
