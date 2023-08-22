package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy.PrioritizationStrategyInterface;

import java.util.List;

@AllArgsConstructor
public class ExperiencePrioritizationSetter<S> {

    List<NstepExperience<S>> buffer;
    PrioritizationStrategyInterface<S> prioritizationStrategy;

    public void setPrios() {
        prioritizationStrategy.modify(buffer);
    }



}
