package safe_rl.domain.agent.aggregates.remove_strategy;

import common.other.RandUtilsML;
import safe_rl.domain.trainer.value_objects.MultiStepResultItem;

import java.util.List;

public class RemoveStrategyRandomMultiStepExp<S> implements RemoveStrategyMultiStepExpI<S> {
    @Override
    public void remove(List<MultiStepResultItem<S>> buffer) {
        int indexToRemove = RandUtilsML.getRandomIntNumber(0, buffer.size());
        buffer.remove(indexToRemove);
    }
}
