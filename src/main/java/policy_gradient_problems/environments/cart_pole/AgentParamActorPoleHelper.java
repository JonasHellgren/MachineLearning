package policy_gradient_problems.environments.cart_pole;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;

import java.util.List;
import java.util.function.Function;

import static common.list_arrays.ArrayUtil.createArrayWithSameDoubleNumber;

@AllArgsConstructor
public class AgentParamActorPoleHelper {
    public static final int LENGTH_THETA = 4;
    public static final double THETA = 1d;

    ActorMemoryParam actor;
    public List<Double> calcActionProbabilitiesInState(StateI<VariablesPole> state) {
        double prob0 = calcProbabilityAction0(state);
        return List.of(prob0,1-prob0);
    }

    public RealVector calcGradLogVector(StateI<VariablesPole> state, int action) {
        var x = state.asRealVector();
        double prob0 = calcProbabilityAction0(state);
        var xTimesProb0= x.mapMultiply(prob0);
        return (action==0)
                ? x.subtract(xTimesProb0)
                : xTimesProb0.mapMultiply(-1d);
    }


    public static ArrayRealVector getInitThetaVector() {
        return new ArrayRealVector(createArrayWithSameDoubleNumber(
                AgentParamActorPoleHelper.LENGTH_THETA,
                AgentParamActorPoleHelper.THETA));
    }

    static Function<Double,Double> logistic=(x) -> Math.exp(x)/(1+Math.exp(x));

    private double calcProbabilityAction0(StateI<VariablesPole> state) {
        double ttx= actor.asRealVector().dotProduct(state.asRealVector());
        return logistic.apply(ttx);
    }


}
