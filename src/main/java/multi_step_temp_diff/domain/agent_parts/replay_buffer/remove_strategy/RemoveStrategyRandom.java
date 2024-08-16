package multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy;

import common.other.RandUtilsML;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.List;

public class RemoveStrategyRandom<S> implements RemoveStrategyI<S> {
    @Override
    public void remove(List<NstepExperience<S>> buffer) {
        int indexToRemove = RandUtilsML.getRandomIntNumber(0, buffer.size());
        buffer.remove(indexToRemove);
    }
}
