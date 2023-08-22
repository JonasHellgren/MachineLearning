package multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public class PrioritizationProportional<S> implements PrioritizationStrategyInterface<S>{

    public static final int EPSILON = 0;
    double epsilon;

    public PrioritizationProportional() {
        this(EPSILON);
    }

    public PrioritizationProportional(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public void modify(List<NstepExperience<S>> buffer) {
        for (NstepExperience<S> experience:buffer) {
            experience.prioritization=Math.abs(experience.tdError)+epsilon;
        }
    }
}
