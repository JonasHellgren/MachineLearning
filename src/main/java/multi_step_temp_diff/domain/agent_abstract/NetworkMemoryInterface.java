package multi_step_temp_diff.domain.agent_abstract;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;

import java.util.List;

public interface NetworkMemoryInterface<S> extends PersistentMemoryInterface {
    double read(StateInterface<S> state);
    double learn(List<NstepExperience<S>> miniBatch);  //returning error=|target-net|
}
