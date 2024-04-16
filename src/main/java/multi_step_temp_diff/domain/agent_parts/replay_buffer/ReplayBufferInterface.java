package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import common.other.CpuTimer;

import java.util.List;

public interface ReplayBufferInterface<S> {
    void  addExperience(NstepExperience<S> experience, CpuTimer timer);
    List<NstepExperience<S>> getMiniBatch(int batchLength);
    int size();
}
