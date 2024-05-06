package safe_rl.domain.value_classes;

import lombok.Builder;
import safe_rl.domain.abstract_classes.StateI;

/**
 *
 * Used by MultiStepReturnEvaluator
 */

@Builder
public  record SingleResultMultiStepper<V> (
        Double sumRewardsNSteps,
        StateI<V> stateFuture,
        boolean isFutureStateOutside,
        boolean isFutureTerminal)
{
}
