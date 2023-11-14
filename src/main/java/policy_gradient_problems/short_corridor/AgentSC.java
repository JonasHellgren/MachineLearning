package policy_gradient_problems.short_corridor;

import common.ArrayUtil;
import common.ListUtils;
import common.MathUtils;
import common.RandUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.List;

import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.BucketLimitsHandler.throwIfBadLimits;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

public class AgentSC {

    public static final double THETA = 0.5;
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;
    int state;
    static ArrayRealVector thetaVector;

    public static AgentSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(ArrayUtil.createArrayWithSameDoubleNumber
                (getThetaLength(), THETA));
    }

    public static int getThetaLength() {
        return EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES * NOF_ACTIONS;
    }

    public static AgentSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentSC(getRandomNonTerminalState(), thetaArray);
    }

    public AgentSC(int stateStart, double[] thetaArray) {
        this.state=stateStart;
        thetaVector=new ArrayRealVector(thetaArray);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int chooseAction(int stateObserved) {
        throwIfBadObsState(stateObserved);
        var limits = getLimits(getActionProbabilitiesInState(stateObserved));
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }


    public List<Double> getActionProbabilitiesInState(int stateObserved) {
        throwIfBadObsState(stateObserved);
        int beg=stateObserved*EnvironmentSC.NOF_ACTIONS;
        int end=beg+EnvironmentSC.NOF_ACTIONS;
        double[] thetaArrForStateObserved = ArrayUtils.subarray(thetaVector.toArray(), beg, end);
        return actionProbabilities(thetaArrForStateObserved);
    }

    public ArrayRealVector gradLogVector(int stateObserved, int action) {
        double[] gradLogForStateObserved = calculateGradLog(action, getActionProbabilitiesInState(stateObserved));
        double[] gradLogAllStates=ArrayUtil.createArrayWithSameDoubleNumber(getThetaLength(), 0);
        int indexSource = 0;
        int indexDestination = stateObserved * NOF_ACTIONS;
        System.arraycopy(gradLogForStateObserved, indexSource,gradLogAllStates, indexDestination,NOF_ACTIONS);
        return new ArrayRealVector(gradLogAllStates);
    }


    private static void throwIfBadObsState(int stateObserved) {
        if (!EnvironmentSC.SET_OBSERVABLE_STATES.contains(stateObserved)) {
            throw new IllegalArgumentException("Non valid obs state, state = "+ stateObserved);
        }
    }


    private static int getRandomNonTerminalState() {
        return RandUtils.getRandomIntNumber(2, 6);
    }


    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

}
