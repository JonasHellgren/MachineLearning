package policy_gradient_problems.short_corridor;

import common.MathUtils;
import common.RandUtils;

import java.util.Map;
import java.util.Set;

import static common.RandUtils.getRandomDouble;

public class EnvironmentSC {

    public static final int NOF_ACTIONS=2;
    public final int NOF_OBSERVABLE_STATES=3;

    static final double PROB_DIRECT_TO_TERMINAL = 0.01;
    static final double REWARD_S4 = 1d, REWARD_S1 = -1d, REWARD_S7 = -1d;
    static final double REWARD_FOR_NON417STATE = -0.1;

    final Map<Integer,Double> STATE_REWARD_MAP;
    final Map<Integer,Integer> STATE_OBSERVEDSTATE_MAP;
    final Set<Integer> SET_TERMINAL_STATES;

    public double probDirectToTerminal;

    public EnvironmentSC(double probDirectToTerminal) {
        this();
        this.probDirectToTerminal = probDirectToTerminal;
    }

    public EnvironmentSC() {
        STATE_REWARD_MAP =Map.of(4,REWARD_S4, 1,REWARD_S1, 7,REWARD_S7);
        STATE_OBSERVEDSTATE_MAP=Map.of(1,0, 2,1, 3,2, 4,0, 5,2, 6,3, 7,0);
        SET_TERMINAL_STATES=Set.of(1,4,7);
        this.probDirectToTerminal=PROB_DIRECT_TO_TERMINAL;
    }

    public StepReturnSC step(int state, int action) {
        int stateNew=getStateNew(state,action);
        double reward=getReward(stateNew);
        return StepReturnSC.of(stateNew,observedState(stateNew),isTerminal(stateNew),reward);
    }

    private boolean isTerminal(int stateNew) {
        return SET_TERMINAL_STATES.contains(stateNew);
    }

    public int observedState(int state) {
        throwIfBadState(state);
        return STATE_OBSERVEDSTATE_MAP.get(state);
    }

    private void throwIfBadState(int state) {
        if (!STATE_OBSERVEDSTATE_MAP.containsKey(state)) {
            throw new IllegalArgumentException("Non valid state, state = "+ state);
        }
    }

    private double getReward(int state) {
        return STATE_REWARD_MAP.getOrDefault(state, REWARD_FOR_NON417STATE);
    }

    private int getStateNew(int state, int action) {
        return getRandomDouble(0,1)< probDirectToTerminal
                ? getRandomTerminalState()
                : getNonTerminalState(state,action);
    }

    private static int getRandomTerminalState() {
        return getRandomDouble(0, 1) < 0.5 ? 1 : 7;
    }

    private int getNonTerminalState(int state, int action) {
        return MathUtils.isZero(action) ? state-1 : state+1;

    }

}
