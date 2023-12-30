package policy_gradient_problems.common_generic;
import lombok.Builder;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;

@Builder
public record Experience<V> (
        StateI<V> state,
        Action action,
        double reward,
        StateI<V> stateNext,
        boolean isFail,
        double value)
{
    public Experience<V> copyWithValue(double value) {
        return Experience.<V>builder().state(state).action(action).reward(reward).stateNext(stateNext).isFail(isFail)
                .value(value).build();
    }
}
