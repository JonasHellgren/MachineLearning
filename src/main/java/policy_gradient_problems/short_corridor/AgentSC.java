package policy_gradient_problems.short_corridor;

import common.ListUtils;
import common.RandUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import java.util.ArrayList;
import java.util.List;
import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.IndexFinder.findBucket;
import static common.ListUtils.toArray;
import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static java.lang.System.arraycopy;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static policy_gradient_problems.common.BucketLimitsHandler.*;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

/***
 * See shortCorridor.md for description
 */

@Getter
@Setter
public class AgentSC {
    public static final double THETA = 0.5;
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;

    int state;
    ArrayRealVector thetaVector;

    public static AgentSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                createArrayWithSameDoubleNumber(getThetaLength(), THETA));
    }

    public static AgentSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentSC(getRandomNonTerminalState(), thetaArray);
    }

    public AgentSC(int stateStart, double[] thetaArray) {
        this.state = stateStart;
        thetaVector = new ArrayRealVector(thetaArray);
    }

    public int chooseAction(int stateObserved) {
        throwIfBadObsState(stateObserved);
        var limits = getLimits(calcActionProbabilitiesInState(stateObserved));
        throwIfBadLimits(limits);
        return findBucket(toArray(limits), randomNumberBetweenZeroAndOne());
    }

    public List<Double> calcActionProbabilitiesInState(int stateObserved) {
        throwIfBadObsState(stateObserved);
        int indexFirstTheta = getIndexFirstThetaForState(stateObserved);
        int indexEndTheta = indexFirstTheta + NOF_ACTIONS;
        return actionProbabilities(subarray(thetaVector.toArray(), indexFirstTheta, indexEndTheta));
    }

    public ArrayRealVector calcGradLogVector(int stateObserved, int action) {
        double[] gradLogForStateObserved = calculateGradLog(action, calcActionProbabilitiesInState(stateObserved));
        return new ArrayRealVector(createGradLogAllStates(stateObserved, gradLogForStateObserved));
    }

    public static int getThetaLength() {
        return EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES * NOF_ACTIONS;
    }

    public void setStateAsRandomNonTerminal() {
        this.state = getRandomNonTerminalState();
    }

    private static double[] createGradLogAllStates(int stateObserved, double[] gradLogForStateObserved) {
        double[] gradLogAllStates = createArrayWithSameDoubleNumber(getThetaLength(), 0);
        int indexSource = 0;
        int indexFirstTheta = getIndexFirstThetaForState(stateObserved);
        arraycopy(gradLogForStateObserved, indexSource, gradLogAllStates, indexFirstTheta, NOF_ACTIONS);
        return gradLogAllStates;
    }

    private static int getIndexFirstThetaForState(int stateObserved) {
        return stateObserved * NOF_ACTIONS;
    }

    private static void throwIfBadObsState(int stateObserved) {
        if (!EnvironmentSC.SET_OBSERVABLE_STATES.contains(stateObserved)) {
            throw new IllegalArgumentException("Non valid obs state, state = " + stateObserved);
        }
    }

    private static int getRandomNonTerminalState() {
        RandUtils<Integer> randUtils = new RandUtils<>();
        return randUtils.getRandomItemFromList(new ArrayList<>(EnvironmentSC.SET_NON_TERMINAL_STATES));
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

}
