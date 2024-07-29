package safe_rl.domain.agent.aggregates.remove_strategy;

import safe_rl.domain.trainer.value_objects.MultiStepResultItem;

import java.util.List;

public class RemoveStrategyOldestMultiStepExp<S> implements RemoveStrategyMultiStepExpI<S> {
    @Override
    public void remove(List<MultiStepResultItem<S>> buffer) {
        buffer.remove(0);
    }
}
