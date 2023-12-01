package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;

@Builder
public record ExperiencePole(StatePole state, int action, double reward, StatePole stateNext, double value) {

    public ExperiencePole copyWithValue(double value) {
        return ExperiencePole.builder().state(state).action(action).reward(reward).stateNext(stateNext)
                .value(value).build();
    }

}
