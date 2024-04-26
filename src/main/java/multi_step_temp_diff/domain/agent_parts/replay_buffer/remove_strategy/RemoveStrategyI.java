package multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public interface RemoveStrategyI<S> {
    void remove(List<NstepExperience<S>> buffer);
}
