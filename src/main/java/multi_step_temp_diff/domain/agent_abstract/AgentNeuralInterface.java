package multi_step_temp_diff.domain.agent_abstract;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;

import java.util.List;

/**
 * Additional functionality for agents with neural memory
 */


public interface AgentNeuralInterface<S> extends AgentInterface<S> {
    void learn(List<NstepExperience<S>> miniBatch);
    AgentSettingsInterface getAgentSettings();
    void saveMemory(String fileName);
    void loadMemory(String fileName);
    NetworkMemoryInterface<S> getMemory();

}
