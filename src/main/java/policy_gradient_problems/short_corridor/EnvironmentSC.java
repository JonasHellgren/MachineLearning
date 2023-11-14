package policy_gradient_problems.short_corridor;

import common.MathUtils;

import java.util.Map;
import java.util.Set;

import static common.RandUtils.getRandomDouble;

public class EnvironmentSC {

    public static final int NOF_ACTIONS=2;
    public static final int NOF_NON_TERMINAL_OBSERVABLE_STATES =3;

    static final double PROB_DIRECT_TO_TERMINAL = 0.01;
    static final double REWARD_S4 = 1d, REWARD_S1 = -1d, REWARD_S7 = -1d;
    static final double REWARD_FOR_NON417STATE = -0.1;

    final Map<Integer,Double> STATE_REWARD_MAP=Map.of(4,REWARD_S4, 1,REWARD_S1, 7,REWARD_S7);
    final Map<Integer,Integer> STATE_OBSERVEDSTATE_MAP=Map.of(1,-1, 2,0, 3,1, 4,-1, 5,1, 6,2, 7,-1);
    public final Set<Integer> SET_TERMINAL_STATES=Set.of(1,4,7);
    public static final Set<Integer> SET_OBSERVABLE_STATES=Set.of(-1,0,1,2);

    public double probDirectToTerminal;

    public EnvironmentSC(double probDirectToTerminal) {
        this();
        this.probDirectToTerminal = probDirectToTerminal;
    }

    public EnvironmentSC() {
        this.probDirectToTerminal=PROB_DIRECT_TO_TERMINAL;
    }

    public StepReturnSC step(int state, int action) {
        int stateNew=getStateNew(state,action);
        double reward=getReward(stateNew);
        return StepReturnSC.of(stateNew, getObservedState(stateNew),isTerminal(stateNew),reward);
    }

    private boolean isTerminal(int stateNew) {
        return SET_TERMINAL_STATES.contains(stateNew);
    }

    public int getObservedState(int state) {
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
