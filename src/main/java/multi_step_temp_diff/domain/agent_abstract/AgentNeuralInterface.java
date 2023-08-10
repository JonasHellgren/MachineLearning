package multi_step_temp_diff.domain.agent_abstract;

import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_valueobj.AgentSettingsInterface;

import java.util.List;

public interface AgentNeuralInterface<S> extends AgentInterface<S> {
    void learn(List<NstepExperience<S>> miniBatch);
    AgentSettingsInterface getAgentSettings();
    void saveMemory(String fileName);
    void loadMemory(String fileName);

}
