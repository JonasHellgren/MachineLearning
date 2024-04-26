package safe_rl.domain.memories;

import common.other.RandUtils;
import safe_rl.domain.value_classes.Experience;

import java.util.List;

public class RemoveStrategyRandom<S> implements RemoveStrategyI<S> {
    @Override
    public void remove(List<Experience<S>> buffer) {
        int indexToRemove = RandUtils.getRandomIntNumber(0, buffer.size());
        buffer.remove(indexToRemove);
    }
}
