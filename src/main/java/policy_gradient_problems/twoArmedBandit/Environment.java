package policy_gradient_problems.twoArmedBandit;

import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;

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
