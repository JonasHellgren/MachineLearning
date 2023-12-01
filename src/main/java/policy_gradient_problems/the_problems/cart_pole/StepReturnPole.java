package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;

@Builder
public record StepReturnPole(
        CartPoleState newState,
        boolean isFail,
        boolean isTerminal,
        double reward
) {
}
