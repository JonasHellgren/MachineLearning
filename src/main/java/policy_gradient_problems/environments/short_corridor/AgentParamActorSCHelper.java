package policy_gradient_problems.environments.short_corridor;

import common.ListUtils;
import common.RandUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.helpers.ParamFunction;
import policy_gradient_problems.helpers.SubArrayExtractor;

import java.util.ArrayList;
import java.util.List;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.IndexFinder.findBucket;
import static common.ListUtils.toArray;
import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static policy_gradient_problems.helpers.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.helpers.BucketLimitsHandler.throwIfBadLimits;
import static policy_gradient_problems.helpers.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.helpers.SoftMaxEvaluator.getProbabilities;

public class AgentParamActorSCHelper {
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;
    public static final double THETA = 0.5;

    ParamFunction actor;
    SubArrayExtractor subArrayExtractor;

    public AgentParamActorSCHelper(ParamFunction actor) {
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
        if (!EnvironmentSC.SET_OBSERVABLE_STATES.contains(stateObserved)) {
            throw new IllegalArgumentException("Non valid obs state, state = " + stateObserved);
        }
    }

    static int getRandomNonTerminalState() {
        RandUtils<Integer> randUtils = new RandUtils<>();
        return randUtils.getRandomItemFromList(new ArrayList<>(EnvironmentSC.SET_NON_TERMINAL_STATES));
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    public Action chooseAction(StateI<VariablesSC> state) {
        //int observedStateOld = EnvironmentSC.getObservedPos(state);

        StateSC stateAsObs=(StateSC) state;
        int observedStateOld = stateAsObs.asObserved().getPos();
        throwIfBadObsState(observedStateOld);
        var limits = getLimits(calcActionProbsInObsState(observedStateOld));
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(toArray(limits), randomNumberBetweenZeroAndOne()));
    }

}
