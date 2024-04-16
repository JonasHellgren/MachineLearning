package policy_gradient_problems.environments.twoArmedBandit;

import common.other.RandUtils;
import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import common.reinforcment_learning.value_classes.StepReturn;


/**
 * There are two actions, action 0 is try arm 0, action 1 is try arm 1
 * Each arm is assigned with a probability between 0 and 1 of winning a coin
 * The reward is one if an action leads to a coin
 */

@Builder
public class EnvironmentBandit implements EnvironmentI<VariablesBandit> {
    public static final int NOF_ACTIONS = 2;
    Double probWinAction0;
    Double probWinAction1;

    public static final double NOF_COINS_IF_WINNING = 1;

    public static EnvironmentBandit newWithProbabilities(double prob0, double prob1) {
        return EnvironmentBandit.builder().probWinAction0(prob0).probWinAction1(prob1).build();
    }

    public StepReturn<VariablesBandit> step(StateI<VariablesBandit> state, Action action) {
        double reward = (action.asInt() == 0) ? tryToGetCoin(probWinAction0): tryToGetCoin(probWinAction1);
        return StepReturn.<VariablesBandit>builder().reward(reward).build();
    }

    private static double tryToGetCoin(double probWinningAction) {
        return RandUtils.getRandomDouble(0, 1) < probWinningAction ? NOF_COINS_IF_WINNING : 0;
    }

}
