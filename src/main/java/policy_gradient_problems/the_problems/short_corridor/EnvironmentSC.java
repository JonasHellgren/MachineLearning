package policy_gradient_problems.the_problems.short_corridor;

import common.MathUtils;

import java.util.Map;
import java.util.Set;

import static common.MySetUtils.getSetFromRange;
import static common.RandUtils.getRandomDouble;
import static common.RandUtils.randomNumberBetweenZeroAndOne;

/***
 * See shortCorridor.md for description
 */

public class EnvironmentSC {

    public static final int NOF_ACTIONS=2;
    public static final int NOF_NON_TERMINAL_OBSERVABLE_STATES =3;

    static final double PROB_DIRECT_TO_TERMINAL = 0.01;
    static final double REWARD_FOR_NON417STATE = -0.1;

    final Map<Integer,Double> STATE_REWARD_MAP=Map.of(4,1d, 1,-1d, 7,-1d);
    final Map<Integer,Integer> STATE_OBSERVEDSTATE_MAP=Map.of(1,-1, 2,0, 3,1, 4,-1, 5,1, 6,2, 7,-1);
    public final Set<Integer> SET_TERMINAL_STATES=Set.of(1,4,7);
    public static final Set<Integer> SET_NON_TERMINAL_STATES=Set.of(2,3,5,6);
    public static final Set<Integer> SET_OBSERVABLE_STATES=Set.of(-1,0,1,2);
    public static final Set<Integer> SET_OBSERVABLE_STATES_NON_TERMINAL=
            getSetFromRange(0,NOF_NON_TERMINAL_OBSERVABLE_STATES);


    public double probDirectToTerminal;

    public EnvironmentSC(double probDirectToTerminal) {
        this();
        this.probDirectToTerminal = probDirectToTerminal;
    }

    public EnvironmentSC() {
        this.probDirectToTerminal=PROB_DIRECT_TO_TERMINAL;
    }

    public static EnvironmentSC create()  {
        return new EnvironmentSC();

    }

    public StepReturnSC step(int state, int action) {
        int stateNew=getStateNew(state,action);
        double reward=getReward(stateNew);
        return StepReturnSC.of(stateNew, getObservedState(stateNew),isTerminal(stateNew),reward);
    }


    public boolean isTerminalObserved(int stateObserved) {
        return !SET_OBSERVABLE_STATES_NON_TERMINAL.contains(stateObserved);
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
        return randomNumberBetweenZeroAndOne() < probDirectToTerminal
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
