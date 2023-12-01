package policy_gradient_problems.the_problems.cart_pole;

import common.SigmoidFunctions;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.the_problems.short_corridor.AgentSC;

import java.util.List;
import java.util.function.Function;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.IndexFinder.findBucket;
import static common.ListUtils.toArray;
import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.BucketLimitsHandler.throwIfBadLimits;

@AllArgsConstructor
public class AgentPole {

    public static final int LENGTH_THETA = 4;
    public static final double THETA = 1d;
    StatePole state;
    RealVector thetaVector;

    static  Function<Double,Double> logistic=(x) -> Math.exp(x)/(1+Math.exp(x));

    public static AgentPole newRandomStartStateDefaultThetas(ParametersPole parameters) {
        return new AgentPole(StatePole.newAngleAndPosRandom(parameters),
                new ArrayRealVector(createArrayWithSameDoubleNumber(LENGTH_THETA, THETA)));
    }

    public int chooseAction(StatePole state) {
        var limits = getLimits(calcActionProbabilitiesInState(state));
        throwIfBadLimits(limits);
        return findBucket(toArray(limits), randomNumberBetweenZeroAndOne());
    }

    public List<Double> calcActionProbabilitiesInState(StatePole state) {
        double prob0 = calcProbabilityAction0(state);
        return List.of(prob0,1-prob0);
    }

    public RealVector calcGradLogVector(StatePole state, int action) {
        RealVector x = state.asRealVector();
        double prob0 = calcProbabilityAction0(state);
        RealVector xTimesProb0= x.mapMultiply(prob0);

        return (action==0)
                ? x.subtract(xTimesProb0)
                : (ArrayRealVector) xTimesProb0.mapMultiply(-1d);
    }

    private double calcProbabilityAction0(StatePole state) {
        double ttx=thetaVector.dotProduct(state.asRealVector());
        return logistic.apply(ttx);
    }


}
