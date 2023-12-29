package policy_gradient_problems.common_value_classes;

import lombok.Builder;

@Builder
public record ExperienceOld(int state, int action, double reward, int stateNext, double value) {

    public ExperienceOld copyWithValue(double value) {
        return ExperienceOld.builder().state(state).action(action).reward(reward).stateNext(stateNext)
                .value(value).build();
    }

}
