package multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public interface PrioritizationStrategyInterface<S> {
    void modify(List<NstepExperience<S>> buffer);
}
