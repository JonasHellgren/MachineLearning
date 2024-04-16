package policy_gradient_problems.environments.multicoin_bandit;

import common.other.RandUtils;
import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import common.reinforcment_learning.value_classes.StepReturn;
import policy_gradient_problems.environments.twoArmedBandit.VariablesBandit;
import java.util.List;

@Builder
public class EnvironmentMultiCoinBandit implements EnvironmentI<VariablesBandit> {
    public static final int NOF_ACTIONS = 2;
    static final List<Double> COINS = List.of(-10d,1d, 5d, 10d);
    Double probWinAction0;
    Double probWinAction1;

    final RandUtils<Double> rand=new RandUtils<>();


    public static EnvironmentMultiCoinBandit newWithProbabilities(double prob0, double prob1) {
        return EnvironmentMultiCoinBandit.builder().probWinAction0(prob0).probWinAction1(prob1).build();
    }

    public List<Double> coins() {
        return COINS;
    }


    public StepReturn<VariablesBandit> step(StateI<VariablesBandit> state, Action action) {
        double reward = (action.asInt() == 0) ? tryToGetCoin(probWinAction0): tryToGetCoin(probWinAction1);
        return StepReturn.<VariablesBandit>builder().reward(reward).build();
    }

    private  double tryToGetCoin(double probWinningAction) {
        double coinValue=rand.getRandomItemFromList(COINS);
        return RandUtils.getRandomDouble(0, 1) < probWinningAction ? coinValue : 0;
    }
}
