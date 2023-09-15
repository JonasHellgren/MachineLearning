package policy_gradient_upOrDown.domain;

import lombok.Builder;
import java.util.function.Supplier;

@Builder
public class Environment {

    public static final Supplier<Double> REWARD_ACTION_ZERO = () ->  1d;  //up
    public static final Supplier<Double> REWARD_ACTION_ONE = () -> -0.5;  //down

    @Builder.Default
    Supplier<Double> rewardActionZero = REWARD_ACTION_ZERO;
    @Builder.Default
    Supplier<Double> rewardActionOne = REWARD_ACTION_ONE;

    public static Environment newDefault() {
        return Environment.builder().build();
    }

    public double step(int action) {
        return  action==0
                ? rewardActionZero.get()
                : rewardActionOne.get();
    }

}
