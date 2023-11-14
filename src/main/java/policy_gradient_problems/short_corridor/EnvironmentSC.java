package policy_gradient_problems.short_corridor;

import com.google.common.collect.ImmutableMap;
import common.MathUtils;
import common.RandUtils;
import org.nd4j.shade.errorprone.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentSC {

    public static final double PROB_DIRECT_TO_TERMINAL = 0.01;
    public static final double REWARD_S4 = 1d, REWARD_S1 = -1d, REWARD_S7 = -1d;
    public static final double REWARD_FOR_NON417STATE = -0.1;

    final Map<Integer,Double> stateRewardMap;
    final Map<Integer,Integer> stateObservedStateMap;


    public EnvironmentSC() {
        stateRewardMap=Map.of(4,REWARD_S4, 1,REWARD_S1, 7,REWARD_S7);
        stateObservedStateMap=Map.of(1,0, 2,1, 3,2, 4,0, 5,2, 6,3, 7,0);

    }

    public StepReturnSC step(int state, int action) {

        int stateNew=getStateNew(state,action);
        double reward=getReward(stateNew);

        return StepReturnSC.of(stateNew,reward);
    }

    public int observedState(int state) {
        throwIfBadState(state);
        return stateObservedStateMap.get(state);
    }

    private void throwIfBadState(int state) {
        if (!stateObservedStateMap.containsKey(state)) {
            throw new IllegalArgumentException("Non valid state, state = "+ state);
        }
    }

    private double getReward(int state) {
        return stateRewardMap.getOrDefault(state, REWARD_FOR_NON417STATE);
    }

    private int getStateNew(int state, int action) {
        return RandUtils.getRandomDouble(0,1)< PROB_DIRECT_TO_TERMINAL
                ? getRandomTerminalState()
                : getNonTerminalState(state,action);
    }

    private static int getRandomTerminalState() {
        return RandUtils.getRandomDouble(0, 1) < 0.5 ? 1 : 7;
    }

    private int getNonTerminalState(int state, int action) {
        return MathUtils.isNeg(action) ? state-1 : state+1;

    }

}
