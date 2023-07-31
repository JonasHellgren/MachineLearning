package multi_step_temp_diff.domain.interfaces_and_abstract;

import multi_step_temp_diff.models.NstepExperience;

import java.util.List;

public interface AgentNeuralInterface<S> extends AgentInterface<S> {
    void learn(List<NstepExperience<S>> miniBatch);
}
