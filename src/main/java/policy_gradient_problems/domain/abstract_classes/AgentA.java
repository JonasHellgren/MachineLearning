package policy_gradient_problems.domain.abstract_classes;

import common.ListUtils;
import common.RandUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.agent_interfaces.AgentI;

import static common.IndexFinder.findBucket;
import static common.BucketLimitsHandler.getLimits;
import static common.BucketLimitsHandler.throwIfBadLimits;

/**
 * Generic abstract agent, handles a state with variables V
 * All agents contains state
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
        return Action.ofInteger(findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne()));
    }
}
