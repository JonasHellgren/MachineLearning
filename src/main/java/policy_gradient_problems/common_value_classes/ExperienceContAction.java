package policy_gradient_problems.common_value_classes;

import lombok.Builder;

@Builder
public record ExperienceContAction(int state, double action, double reward, int stateNext, double value)  {

    public ExperienceContAction copyWithValue(double value) {
        return ExperienceContAction.builder().state(state).action(action).reward(reward).stateNext(stateNext)
                .value(value).build();
    }

}
