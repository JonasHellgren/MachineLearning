package safe_rl.domain.memories;

import common.other.RandUtils;
import safe_rl.domain.value_classes.MultiStepResultItem;

import java.util.List;

public class RemoveStrategyRandomMultiStepExp<S> implements RemoveStrategyMultiStepExpI<S> {
    @Override
    public void remove(List<MultiStepResultItem<S>> buffer) {
        int indexToRemove = RandUtils.getRandomIntNumber(0, buffer.size());
        buffer.remove(indexToRemove);
    }
}
