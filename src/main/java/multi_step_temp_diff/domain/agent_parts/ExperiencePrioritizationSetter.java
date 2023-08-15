package multi_step_temp_diff.domain.agent_parts;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExperiencePrioritizationSetter<S> {

    List<NstepExperience<S>> buffer;
    PrioritizationStrategyInterface<S> prioritizationStrategy;

    public void setPrios() {
        prioritizationStrategy.modify(buffer);
    }



}
