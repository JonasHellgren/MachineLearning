package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public record ExperiencePole(StatePole state, int action, double reward, StatePole stateNext, boolean isFail, double value) {

    public static ExperiencePole of(StatePole state, int action, double reward, boolean isFail, StatePole stateNext) {
        return new ExperiencePole(state,action,reward,stateNext,isFail,0d);
    }

    public ExperiencePole copyWithValue(double value) {
        return ExperiencePole.builder().state(state).action(action).reward(reward).isFail(isFail).stateNext(stateNext)
                .value(value).build();
    }

}
