package policy_gradient_problems.sink_the_ship;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.Pair;

import java.util.Random;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static org.apache.commons.lang3.ArrayUtils.subarray;

public class AgentShip {

    public static final double THETA = 0.5;
    public static final int NOF_THETAS_PER_STATE = 2;

    int state;
    ArrayRealVector thetaVector;
    Random random=new Random();

    public static AgentShip newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                createArrayWithSameDoubleNumber(getThetaLength(), THETA));
    }

    public static AgentShip newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentShip(EnvironmentShip.getRandomState(), thetaArray);
    }

    public AgentShip(int stateStart, double[] thetaArray) {
        this.state = stateStart;
        thetaVector = new ArrayRealVector(thetaArray);
    }

    public double chooseAction(int state) {
        throwIfBadState(state);
        Pair<Double,Double> meanStdPair = getMeanAndStdFromThetaVector(state);
        //throwIfBadLimits(limits);
        return sampleFromNormDistribution(meanStdPair);
    }

    /**
     * https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
     */
    private double sampleFromNormDistribution(Pair<Double, Double> meanStdPair) {
        return meanStdPair.getFirst()+random.nextGaussian() * meanStdPair.getSecond();
    }

    public Pair<Double,Double> getMeanAndStdFromThetaVector(int state) {
        throwIfBadState(state);
        int indexFirstTheta = getIndexFirstThetaForState(state);
        double mean = thetaVector.getEntry(indexFirstTheta);
        double std = Math.exp(thetaVector.getEntry(indexFirstTheta+NOF_THETAS_PER_STATE-1));
        return Pair.create(mean, std);
    }

    private static void throwIfBadState(int state) {
        if (!EnvironmentShip.STATES.contains(state)) {
            throw new IllegalArgumentException("Non valid state, state = " + state);
        }
    }

    public static int getThetaLength() {
        return EnvironmentShip.STATES.size() * NOF_THETAS_PER_STATE;
    }

    private static int getIndexFirstThetaForState(int state) {
        return state * NOF_THETAS_PER_STATE;
    }

}
