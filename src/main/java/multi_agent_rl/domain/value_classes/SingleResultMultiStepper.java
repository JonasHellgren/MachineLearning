package multi_agent_rl.domain.value_classes;

import lombok.Builder;
import multi_agent_rl.domain.abstract_classes.StateI;

/**
 *
 * Used by MultiStepReturnEvaluator
 */

@Builder
public  record SingleResultMultiStepper<V,O> (
        Double sumRewardsNSteps,
        StateI<V,O> stateFuture,
        boolean isFutureStateOutside,
        boolean isFutureTerminal)
{
}
