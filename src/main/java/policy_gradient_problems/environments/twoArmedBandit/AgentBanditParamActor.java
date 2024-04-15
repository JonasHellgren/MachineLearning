package policy_gradient_problems.environments.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;
import static common.MyFunctions.*;
import static policy_gradient_problems.helpers.GradLogCalculator.calculateGradLog;
import static common.SoftMaxEvaluator.getProbabilities;


/***
 * Bucket is defined in class BucketLimitsHandler
 */

@Getter
@Setter
public class AgentBanditParamActor extends AgentA<VariablesBandit> implements AgentParamActorI<VariablesBandit> {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final double[] VECTOR = new double[]{THETA0, THETA1};

    ActorMemoryParam actor;

    @Builder
    public AgentBanditParamActor(ActorMemoryParam actor, int nofActions) {
        super(StateBandit.newDefault());
        this.actor = (ActorMemoryParam) defaultIfNullObject.apply(actor,new ActorMemoryParam(VECTOR));
    }

    public static AgentBanditParamActor newDefault() {
        return AgentBanditParamActor.builder().build();
    }

    public static AgentBanditParamActor newWithThetas(double t0, double t1) {
        return AgentBanditParamActor.builder().actor(new ActorMemoryParam(new double[]{t0, t1})).build();
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    public ArrayRealVector calcGradLogVector(StateI<VariablesBandit> state, Action action) {
        return new ArrayRealVector(calculateGradLog(action.asInt(), actionProbabilitiesInPresentState()));
    }

    public List<Double> actionProbabilitiesInPresentState() {
        return actionProbabilities(actor.toArray());
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    @SneakyThrows
    @Override
    public Pair<Double, Double> meanAndStd(int state) {
        throw new NoSuchMethodException();
    }
}
