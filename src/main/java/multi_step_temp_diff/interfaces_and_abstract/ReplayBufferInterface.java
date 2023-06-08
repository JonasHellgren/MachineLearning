package multi_step_temp_diff.interfaces_and_abstract;

import multi_step_temp_diff.models.NstepExperience;

import java.util.List;

public interface ReplayBufferInterface {

    void  addExperience(NstepExperience experience);
    List<NstepExperience> getMiniBatch(int batchLength);
    int size();

}
