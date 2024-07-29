package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import safe_rl.domain.environment.aggregates.StateI;

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
