package multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public class RemoveStrategyOldest<S> implements RemoveStrategyI<S> {

    public static final int INDEX_OF_OLDEST = 0;

    @Override
    public void remove(List<NstepExperience<S>> buffer) {
        buffer.remove(INDEX_OF_OLDEST);
    }
}
