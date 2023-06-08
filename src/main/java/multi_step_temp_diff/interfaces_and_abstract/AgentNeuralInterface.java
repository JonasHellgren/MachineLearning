package multi_step_temp_diff.interfaces_and_abstract;

import multi_step_temp_diff.models.NstepExperience;

import java.util.List;

public interface AgentNeuralInterface extends AgentInterface {
    void learn(List<NstepExperience> miniBatch);
}
