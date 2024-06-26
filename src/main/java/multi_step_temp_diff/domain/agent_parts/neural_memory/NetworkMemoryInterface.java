package multi_step_temp_diff.domain.agent_parts.neural_memory;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public interface NetworkMemoryInterface<S> extends PersistentMemoryInterface {
    double read(StateInterface<S> state);
    double learn(List<NstepExperience<S>> miniBatch);  //returning error=|target-net|
    void learnUsingWeights(List<NstepExperience<S>> miniBatch);
    NetworkMemoryInterface<S> copy();
    void copyWeights(NetworkMemoryInterface<S> netOther);
}
