package safe_rl.domain.agent.aggregates.remove_strategy;

import common.other.RandUtils;
import safe_rl.domain.trainer.value_objects.Experience;

import java.util.List;

public class RemoveStrategyRandom<S> implements RemoveStrategyI<S> {
    @Override
    public void remove(List<Experience<S>> buffer) {
        int indexToRemove = RandUtils.getRandomIntNumber(0, buffer.size());
        buffer.remove(indexToRemove);
    }
}
