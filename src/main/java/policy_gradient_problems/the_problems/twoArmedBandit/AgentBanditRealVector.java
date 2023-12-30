package policy_gradient_problems.the_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;
import policy_gradient_problems.common.ParamFunction;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.Arrays;
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
    public static final double[] VECTOR = new double[]{THETA0, THETA1};

    ParamFunction actor;
    int nofActions;

    @Builder
    public AgentBanditRealVector(ParamFunction actor, int nofActions) {
        super(StateBandit.newDefault());
        this.actor = (ParamFunction) defaultIfNullObject.apply(actor,new ParamFunction(VECTOR));
        this.nofActions = defaultIfNullInteger.apply(nofActions, VECTOR.length);
    }

    public static AgentBanditRealVector newDefault() {
        return AgentBanditRealVector.builder().build();
    }

    public static AgentBanditRealVector newWithThetas(double t0, double t1) {
        return AgentBanditRealVector.builder().actor(new ParamFunction(new double[]{t0, t1})).build();
    }


    @SneakyThrows
    @Override
    public TabularValueFunction getCritic() {
        throw new NoSuchMethodException("No critic for agent");
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    public ArrayRealVector calcGradLogVector(StateI<VariablesBandit> state, Action action) {
        return new ArrayRealVector(calculateGradLog(action.asInt(), getActionProbabilities()));
    }

    public List<Double> getActionProbabilities() {
        return actionProbabilities(actor.toArray());
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }
}
