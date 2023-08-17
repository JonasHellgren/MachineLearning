package multi_step_temp_diff.domain.agent_abstract;

import common.CpuTimer;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;

import java.util.List;

public interface ReplayBufferInterface<S> {
    void  addExperience(NstepExperience<S> experience, CpuTimer timer);
    List<NstepExperience<S>> getMiniBatch(int batchLength);
    int size();
}
