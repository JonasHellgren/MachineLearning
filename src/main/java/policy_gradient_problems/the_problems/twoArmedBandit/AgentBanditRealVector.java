package policy_gradient_problems.the_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.StateI;

import java.util.List;

import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common.BucketLimitsHandler.*;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

/***
 * Bucket is defined in class BucketLimitsHandler
 */

@Getter
@Setter
public class AgentBanditRealVector  extends AgentA<VariablesBandit> implements AgentI<VariablesBandit> {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final ArrayRealVector ARRAY_REAL_VECTOR =
            new ArrayRealVector(new double[]{THETA0, THETA1});
    ArrayRealVector thetaVector;
    int nofActions;

    @Builder
    public AgentBanditRealVector(ArrayRealVector thetaVector, int nofActions) {
        super(StateBandit.newDefault());
        this.thetaVector = (ArrayRealVector) MyFunctions.defaultIfNullObject.apply(thetaVector,ARRAY_REAL_VECTOR);
        this.nofActions = MyFunctions.defaultIfNullInteger.apply(nofActions,ARRAY_REAL_VECTOR.getDimension());
    }
/*
    public AgentBanditRealVector(StateI<VariablesBandit> state) {
        super(state);
    }*/

    public static AgentBanditRealVector newDefault() {
        return AgentBanditRealVector.builder().build();
    }

    public static AgentBanditRealVector newWithThetas(double t0, double t1) {
        return AgentBanditRealVector.builder().thetaVector(new ArrayRealVector(new double[]{t0, t1})).build();
    }

    public int chooseActionOld() {
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }

    @SneakyThrows
    @Override
    public Action chooseAction() { //todo till AgentA
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne()));
    }

    @SneakyThrows
    @Override
    public void setState(StateI<VariablesBandit> state) {
        throw new NoSuchMethodException();
    }

    public List<Double> actionProbabilities() {
        return actionProbabilities(thetaVector.toArray());
    }

    public ArrayRealVector calcGradLogVector(int action) {
        return new ArrayRealVector(calculateGradLog(action, getActionProbabilities()));
    }

    private List<Double> getActionProbabilities() {
        return actionProbabilities(thetaVector.toArray());
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }
}
