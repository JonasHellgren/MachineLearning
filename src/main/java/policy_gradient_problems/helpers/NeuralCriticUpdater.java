package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.NeuralCriticI;
import policy_gradient_problems.domain.value_classes.MultiStepResults;

import static common.Conditionals.executeOneOfTwo;

/**
 * An episode gives a set of experiences: e0, e1, e2, ei,.....,e_i+n
 * Two cases are present for e_end: 1) it is fail state 2) not fail state
 * In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 * But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 * "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 * episode. Many environments stop after a specific amount of steps.
 */

@Log
@AllArgsConstructor
public class NeuralCriticUpdater<V> {

    @NonNull NeuralCriticI<V> agent;

    public void updateCritic(MultiStepResults msRes) {
        executeOneOfTwo(!msRes.stateValuesList().isEmpty(),
                () -> agent.fitCritic(msRes.stateValuesList(), msRes.valueTarList()),
                () -> log.warning("empty stateValuesList"));
    }
}
