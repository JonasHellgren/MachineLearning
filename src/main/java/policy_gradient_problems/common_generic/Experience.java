package policy_gradient_problems.common_generic;

import lombok.Builder;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;

/**
 * The consequence of taking action in state, value is/can be defined later from list of experiences
 */

@Builder
public record Experience<V>(
        StateI<V> state,
        Action action,
        double reward,
        StateI<V> stateNext,
        boolean isFail,
        boolean isTerminal,
        double value) {

    public static final double VALUE = 0d;

    public static <V> Experience<V> of(StateI<V> state,
                                       Action action,
                                       double reward,
                                       StateI<V> stateNext
    ) {
        return new Experience<>(state, action, reward, stateNext, false, false, VALUE);
    }


    public static <V> Experience<V> ofWithIsFail(StateI<V> state,
                                               Action action,
                                               double reward,
                                               StateI<V> stateNext,
                                               boolean isFail
    ) {
        return new Experience<>(state, action, reward, stateNext, isFail, false, VALUE);
    }

    public static <V> Experience<V> ofWithIsTerminal(StateI<V> state,
                                                 Action action,
                                                 double reward,
                                                 StateI<V> stateNext,
                                                 boolean isTerminal
    ) {
        return new Experience<>(state, action, reward, stateNext, false, isTerminal, VALUE);
    }

    public Experience<V> copyWithValue(double value) {
        return Experience.<V>builder()
                .state(state).action(action).reward(reward).stateNext(stateNext)
                .isFail(isFail).isTerminal(isTerminal)
                .value(value).build();
    }
}
