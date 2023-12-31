package policy_gradient_problems.abstract_classes;

import common.ListUtils;
import common.RandUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;

import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common_helpers.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common_helpers.BucketLimitsHandler.throwIfBadLimits;

@Setter
@Getter
@AllArgsConstructor
public abstract class AgentA<V> implements AgentI<V> {

    StateI<V> state;

    abstract public  List<Double> getActionProbabilities();

    @SneakyThrows
    @Override
    public Action chooseAction() {
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne()));
    }


}
