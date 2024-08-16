package policy_gradient_problems.domain.abstract_classes;

import common.list_arrays.ListUtils;
import common.other.RandUtilsML;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.agent_interfaces.AgentI;

import static common.list_arrays.IndexFinder.findBucket;
import static common.math.BucketLimitsHandler.getLimits;
import static common.math.BucketLimitsHandler.throwIfBadLimits;

/**
 * Generic abstract agent, handles a stateNew with variables V
 * All agents contains stateNew
 */

@Setter
@Getter
@AllArgsConstructor
public abstract class AgentA<V> implements AgentI<V> {

    StateI<V> state;

    @SneakyThrows
    @Override
    public Action chooseAction() {
        var limits = getLimits(actionProbabilitiesInPresentState());
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(ListUtils.toArray(limits), RandUtilsML.randomNumberBetweenZeroAndOne()));
    }
}
