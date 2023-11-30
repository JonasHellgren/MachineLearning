package policy_gradient_problems.common_value_classes;

import lombok.Builder;

@Builder
public record ExperienceDiscreteAction(int state, int action, double reward, int stateNext, double value) {


    public ExperienceDiscreteAction copyWithValue(double value) {
        return ExperienceDiscreteAction.builder().state(state).action(action).reward(reward).stateNext(stateNext)
                .value(value).build();
    }

}
