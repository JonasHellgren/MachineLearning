package policy_gradient_problems.short_corridor;

import common.ArrayUtil;
import common.ListUtils;
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

    static ArrayRealVector thetaVector;

    public static AgentSC newDefault() {
        return newWithThetas(ArrayUtil.createArrayWithSameDoubleNumber(6,0.5));
    }

    public static AgentSC newWithThetas(double[] thetaArray) {
        return new AgentSC(thetaArray);
    }

    public AgentSC(double[] thetaArray) {
        thetaVector=new ArrayRealVector(thetaArray);
    }

    public int chooseAction(int stateObserved) {
        var limits = getLimits(getActionProbabilitiesInState(stateObserved));
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }


    public List<Double> getActionProbabilitiesInState(int stateObserved) {
        int beg=stateObserved*EnvironmentSC.NOF_ACTIONS;
        int end=beg+EnvironmentSC.NOF_ACTIONS;
        double[] thetaArrForStateObserved = ArrayUtils.subarray(thetaVector.toArray(), beg, end + 1);
        return actionProbabilities(thetaArrForStateObserved);
    }

    public ArrayRealVector gradLogVector(int action, int stateObserved) {
        return new ArrayRealVector(calculateGradLog(action, getActionProbabilitiesInState(stateObserved)));
    }


    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

}
