package policy_gradient_problems.common;

import lombok.Builder;

@Builder
public record Experience(int state, int action, double reward, int stateNext,  double value) {


    public Experience copyWithValue(double value) {
        return Experience.builder().state(state).action(action).reward(reward).stateNext(stateNext)
                .value(value).build();


    }

}
