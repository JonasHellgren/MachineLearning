package multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy;

import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public class PrioritizationRanked<S> implements PrioritizationStrategyInterface<S> {
    @SneakyThrows
    @Override
    public void modify(List<NstepExperience<S>> buffer) {
        throw new NoSuchMethodException("not implemeted");
    }
}
