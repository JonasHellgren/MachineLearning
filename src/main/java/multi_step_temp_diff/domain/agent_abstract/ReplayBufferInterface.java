package multi_step_temp_diff.domain.agent_abstract;

import multi_step_temp_diff.domain.agent_parts.NstepExperience;

import java.util.List;

public interface ReplayBufferInterface<S> {
    void  addExperience(NstepExperience<S> experience);
    List<NstepExperience<S>> getMiniBatch(int batchLength);
    int size();
}
