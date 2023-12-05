package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.IndexFinder.findBucket;
import static common.ListUtils.toArray;
import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.BucketLimitsHandler.throwIfBadLimits;

@AllArgsConstructor
@Setter
@Getter
public class AgentPole {
    public static final int LENGTH_THETA = 4;
    public static final double THETA = 1d;

    StatePole state;
    RealVector thetaVector;

    public static AgentPole newRandomStartStateDefaultThetas(ParametersPole parameters) {
        return new AgentPole(StatePole.newAngleAndPosRandom(parameters), getInitThetaVector());
    }

    public static AgentPole newAllZeroStateDefaultThetas() {
        return new AgentPole(StatePole.newUprightAndStill(),  getInitThetaVector());
    }

    public AgentPole copy() {
        return new AgentPole(state.copy(),thetaVector.copy());
    }

    public int chooseAction() {
        var limits = getLimits(calcActionProbabilitiesInState(state));
        throwIfBadLimits(limits);
        return findBucket(toArray(limits), randomNumberBetweenZeroAndOne());
    }

    public List<Double> calcActionProbabilitiesInState(StatePole state) {
        double prob0 = calcProbabilityAction0(state);
        return List.of(prob0,1-prob0);
    }

    public RealVector calcGradLogVector(StatePole state, int action) {
        var x = state.asRealVector();
        double prob0 = calcProbabilityAction0(state);
        var xTimesProb0= x.mapMultiply(prob0);
        return (action==0)
                ? x.subtract(xTimesProb0)
                : xTimesProb0.mapMultiply(-1d);
    }

    static  Function<Double,Double> logistic=(x) -> Math.exp(x)/(1+Math.exp(x));

    private double calcProbabilityAction0(StatePole state) {
        double ttx=thetaVector.dotProduct(state.asRealVector());
        return logistic.apply(ttx);
    }

    @NotNull
    private static ArrayRealVector getInitThetaVector() {
        return new ArrayRealVector(createArrayWithSameDoubleNumber(LENGTH_THETA, THETA));
    }


}
