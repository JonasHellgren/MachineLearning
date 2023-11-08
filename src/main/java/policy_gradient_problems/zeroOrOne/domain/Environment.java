package policy_gradient_problems.zeroOrOne.domain;

import lombok.Builder;
import java.util.function.Supplier;

@Builder
public class Environment {

    public static final Supplier<Double> REWARD_ACTION_ZERO = () ->  0d;  //up
    public static final Supplier<Double> REWARD_ACTION_ONE = () ->  0d;  //down

    @Builder.Default
    Supplier<Double> rewardActionZero = REWARD_ACTION_ZERO;
    @Builder.Default
    Supplier<Double> rewardActionOne = REWARD_ACTION_ONE;

    public static Environment newActionZeroIsGood() {
        return Environment.builder()
                .rewardActionZero(() ->  1d).rewardActionOne(() ->  -1d)
                .build();
    }

    public static Environment newActionOneIsGood() {
        return Environment.builder()
                .rewardActionZero(() ->  -1d).rewardActionOne(() ->  1d)
                .build();
    }

    public double step(int action) {
        return  action==0
                ? rewardActionZero.get()
                : rewardActionOne.get();
    }

}
