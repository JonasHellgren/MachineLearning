package multi_step_temp_diff.domain.agent_parts;

import java.util.List;

public interface PrioritizationStrategyInterface<S> {
    void modify(List<NstepExperience<S>> buffer);
}
