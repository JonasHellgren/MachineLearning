package policy_gradient_problems.environments.short_corridor;

import common.list_arrays.ListUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import common.list_arrays.SubArrayExtractor;

import java.util.List;

import static common.list_arrays.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.list_arrays.IndexFinder.findBucket;
import static common.list_arrays.ListUtils.toArray;
import static common.other.RandUtils.randomNumberBetweenZeroAndOne;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static common.math.BucketLimitsHandler.getLimits;
import static common.math.BucketLimitsHandler.throwIfBadLimits;
import static policy_gradient_problems.helpers.GradLogCalculator.calculateGradLog;
import static common.other.SoftMaxEvaluator.getProbabilities;

public class AgentParamActorSCHelper {
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;
    public static final double THETA = 0.5;

    ActorMemoryParam actor;
    SubArrayExtractor subArrayExtractor;

    public AgentParamActorSCHelper(ActorMemoryParam actor) {
        this.actor = actor;
        this.subArrayExtractor = new SubArrayExtractor(getThetaLength(), NOF_ACTIONS);
    }

    public List<Double> calcActionProbsInObsState(int stateObserved) {
        throwIfBadObsState(stateObserved);
        int indexFirstTheta = subArrayExtractor.getIndexFirstThetaForSubArray(stateObserved);
        int indexEndTheta = indexFirstTheta + NOF_ACTIONS;
        return actionProbabilities(subarray(actor.toArray(), indexFirstTheta, indexEndTheta));
    }

    public ArrayRealVector calcGradLogVector(int stateObserved, int action) {
        double[] gradLogStateObs = calculateGradLog(action, calcActionProbsInObsState(stateObserved));
        return new ArrayRealVector(subArrayExtractor.arrayWithZeroExceptAtSubArray(stateObserved, gradLogStateObs));
    }

    public static int getThetaLength() {
        return EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES * NOF_ACTIONS;
    }

    public static double[] getArrayWithEqualThetas() {
        return createArrayWithSameDoubleNumber(
                AgentParamActorSCHelper.getThetaLength(),
                AgentParamActorSCHelper.THETA);
    }

    void throwIfBadObsState(int stateObserved) {
        if (!EnvironmentSC.OBSERVABLE.contains(stateObserved)) {
            throw new IllegalArgumentException("Non valid obs stateNew, stateNew = " + stateObserved);
        }
    }


    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    public Action chooseAction(StateI<VariablesSC> state) {
        StateSC stateAsObs=(StateSC) state;
        int observedStateOld = stateAsObs.getVariables().posObserved();
        throwIfBadObsState(observedStateOld);
        var limits = getLimits(calcActionProbsInObsState(observedStateOld));
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(toArray(limits), randomNumberBetweenZeroAndOne()));
    }

}
