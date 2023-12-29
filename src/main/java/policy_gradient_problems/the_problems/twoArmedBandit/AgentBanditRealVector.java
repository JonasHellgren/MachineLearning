package policy_gradient_problems.the_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.AgentParamActorI;

import java.util.List;
import static common.MyFunctions.*;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;


/***
 * Bucket is defined in class BucketLimitsHandler
 */

@Getter
@Setter
public class AgentBanditRealVector  extends AgentA<VariablesBandit> implements AgentParamActorI<VariablesBandit> {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final ArrayRealVector VECTOR = new ArrayRealVector(new double[]{THETA0, THETA1});
    ArrayRealVector thetaVector;
    int nofActions;

    @Builder
    public AgentBanditRealVector(ArrayRealVector thetaVector, int nofActions) {
        super(StateBandit.newDefault());
        this.thetaVector = (ArrayRealVector) defaultIfNullObject.apply(thetaVector, VECTOR);
        this.nofActions = defaultIfNullInteger.apply(nofActions, VECTOR.getDimension());
    }

    public static AgentBanditRealVector newDefault() {
        return AgentBanditRealVector.builder().build();
    }

    public static AgentBanditRealVector newWithThetas(double t0, double t1) {
        return AgentBanditRealVector.builder().thetaVector(new ArrayRealVector(new double[]{t0, t1})).build();
    }


    public void changeActor(RealVector changeInThetaVector) {
        setThetaVector(getThetaVector().add(changeInThetaVector));
    }

    public ArrayRealVector calcGradLogVector(int action) {
        return new ArrayRealVector(calculateGradLog(action, getActionProbabilities()));
    }

    public List<Double> getActionProbabilities() {
        return actionProbabilities(thetaVector.toArray());
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }
}
