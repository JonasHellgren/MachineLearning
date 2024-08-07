package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.Optional;

/**
 * The consequence of taking action in stateNew, value is/can be defined later from list of experiences
 */

@Builder
public record Experience<V>(
        @NonNull StateI<V> state,
        @NonNull Action action,
        double reward,
        StateI<V> stateNext,
        double probAction,
        boolean isFail,
        boolean isTerminal,
        double value) {

    public static final double DEFAULT_VALUE = 0d;
    public static final int PROB_ACTION = 1;

    public static <V> Experience<V> of(StateI<V> state,
                                       Action action,
                                       double reward,
                                       StateI<V> stateNext
    ) {
        return new Experience<>(state, action, reward, stateNext, PROB_ACTION, false, false, DEFAULT_VALUE);
    }


    public static <V> Experience<V> ofWithIsFail(StateI<V> state,
                                               Action action,
                                               double reward,
                                               StateI<V> stateNext,
                                               double probAction,
                                               boolean isFail
    ) {
        return new Experience<>(state, action, reward, stateNext, probAction, isFail, false, DEFAULT_VALUE);
    }

    public static <V> Experience<V> ofWithIsTerminal(StateI<V> state,
                                                 Action action,
                                                 double reward,
                                                 StateI<V> stateNext,
                                                 double probAction,
                                                 boolean isTerminal
    ) {
        return new Experience<>(state, action, reward, stateNext, probAction, false, isTerminal, DEFAULT_VALUE);
    }

    public static <V> Experience<V> ofWithIsSafeCorrected(StateI<V> state,
                                                     Action actionNotSafe,
                                                     double probActionNotSafe,
                                                     ExperienceSafe<V> experienceSafe
    ) {
        return Experience.<V>builder()
                .state(state).action(actionNotSafe).probAction(probActionNotSafe)
                .build();
    }

    public Experience<V> copyWithValue(double value) {
        return Experience.<V>builder()
                .state(state).action(action).reward(reward).stateNext(stateNext)
                .probAction(probAction).isFail(isFail).isTerminal(isTerminal)
                .value(value)
                .build();
    }
}
