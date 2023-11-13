package policy_gradient_problems.twoArmedBandit;

import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;


/**
 * There are two actions, action 0 is try arm 0, action 1 is try arm 1
 * Each arm is assigned with a probability between 0 and 1 of winning a coin
 * The reward is one if an action leads to a coin
 */

@Builder
public class Environment {


    @NonNull  Double probWinningAction0, probWinningAction1;

    public static final double NOF_COINS_IF_WINNING = 1;

    public static Environment newWithProbabilities(double prob0, double prob1) {
        return Environment.builder().probWinningAction0(prob0).probWinningAction1(prob1).build();
    }

    public double step(int action) {
        return  action==0
                ? tryToGetCoins(probWinningAction0)
                : tryToGetCoins(probWinningAction1);
    }

    private static double tryToGetCoins(double probWinningAction) {
        return RandUtils.getRandomDouble(0, 1) < probWinningAction ? NOF_COINS_IF_WINNING : 0;
    }

}
