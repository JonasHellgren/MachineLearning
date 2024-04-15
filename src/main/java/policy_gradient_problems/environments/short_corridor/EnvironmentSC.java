package policy_gradient_problems.environments.short_corridor;

import common.math.MathUtils;
import common.RandUtils;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.StepReturn;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static common.MySetUtils.getSetFromRange;
import static common.RandUtils.getRandomDouble;
import static common.RandUtils.randomNumberBetweenZeroAndOne;

/***
 * See shortCorridor.md for description
 */

public class EnvironmentSC implements EnvironmentI<VariablesSC> {

    public static final int NOF_ACTIONS = 2;
    public static final int NOF_NON_TERMINAL_OBSERVABLE_STATES = 3;

    static final double PROB_DIRECT_TO_TERMINAL = 0.01;
    static final double REWARD_FOR_NON417STATE = -0.1;

    public static final Map<Integer, Double> STATE_REWARD_MAP = Map.of(4, 1d, 1, -1d, 7, -1d);
    static final Map<Integer, Integer> STATE_OBSERVEDSTATE_MAP = Map.of(1, -1, 2, 0, 3, 1, 4, -1, 5, 1, 6, 2, 7, -1);
    final Set<Integer> TERMINAL = Set.of(1, 4, 7);
    public static final Set<Integer> NON_TERMINAL = Set.of(2, 3, 5, 6);
    public static final Set<Integer> OBSERVABLE = Set.of(-1, 0, 1, 2);
    static final Set<Integer> OBSERVABLE_NON_TERMINAL =
            getSetFromRange(0, NOF_NON_TERMINAL_OBSERVABLE_STATES);
    double probDirectToTerminal;

    public EnvironmentSC(double probDirectToTerminal) {
        this();
        this.probDirectToTerminal = probDirectToTerminal;
    }

    public static EnvironmentSC create() {
        return new EnvironmentSC();
    }

    public EnvironmentSC() {
        this.probDirectToTerminal = PROB_DIRECT_TO_TERMINAL;
    }

    public StepReturn<VariablesSC> step(StateI<VariablesSC> state, Action action) {
        int posRealNew = getRealPosNew(getPos(state), action.asInt());
        double reward = getReward(posRealNew);
        return new StepReturn<>(StateSC.newFromRealPos(posRealNew), false, isTerminal(posRealNew), reward);
    }

    public boolean isTerminalObserved(int stateObserved) {
        return !OBSERVABLE_NON_TERMINAL.contains(stateObserved);
    }

    public static int getObservedPos(StateI<VariablesSC> state) {
        return getObservedPos(getPos(state));
    }

    public static int getPos(StateI<VariablesSC> state) {
        return state.getVariables().posReal();
    }

    public static int getRandomNonTerminalState() {
        RandUtils<Integer> randUtils = new RandUtils<>();
        return randUtils.getRandomItemFromList(new ArrayList<>(EnvironmentSC.NON_TERMINAL));
    }

    public static int getObservedPos(int pos) {
        throwIfBadState(pos);
        return STATE_OBSERVEDSTATE_MAP.get(pos);
    }

    private boolean isTerminal(int stateNew) {
        return TERMINAL.contains(stateNew);
    }

    private static void throwIfBadState(int state) {
        if (!STATE_OBSERVEDSTATE_MAP.containsKey(state)) {
            throw new IllegalArgumentException("Non valid state, state = " + state);
        }
    }

    private double getReward(int state) {
        return STATE_REWARD_MAP.getOrDefault(state, REWARD_FOR_NON417STATE);
    }

    private int getRealPosNew(int state, int action) {
        return randomNumberBetweenZeroAndOne() < probDirectToTerminal
                ? getRandomTerminalState()
                : getNonTerminalState(state, action);
    }

    private static int getRandomTerminalState() {
        return getRandomDouble(0, 1) < 0.5 ? 1 : 7;
    }

    private int getNonTerminalState(int state, int action) {
        return MathUtils.isZero(action) ? state - 1 : state + 1;
    }

}
