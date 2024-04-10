package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.StateI;

@Builder
public  record ResultManySteps<V> (
        Double sumRewardsNSteps,
        StateI<V> stateFuture,
        boolean isFutureStateOutside,
        boolean isFutureTerminal)
{
}
