package policy_gradient_problems.sink_the_ship;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.Pair;
import org.opencv.core.Mat;
import policy_gradient_problems.short_corridor.EnvironmentSC;

import java.util.Random;
import java.util.function.Function;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static java.lang.System.arraycopy;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;

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

    public ArrayRealVector calcGradLogVector(int state, double action) {
        return new ArrayRealVector(createGradLogAllStates(state, calculateGradLogForState(state,action)));
    }

    Function<Double,Double> sqr=(n) -> Math.pow(n,2);

    private double[] calculateGradLogForState(int state, double action) {
        var meanAndStd=getMeanAndStdFromThetaVector(state);
        double mean=meanAndStd.getFirst();
        double std=meanAndStd.getSecond();
        double gradMean=1/sqr.apply(std)*(action-mean);
        double gradStd=sqr.apply(action-mean)/sqr.apply(std)-1d;
        return new double[]{gradMean,gradStd};
    }

    public Pair<Double,Double> getMeanAndStdFromThetaVector(int state) {
        throwIfBadState(state);
        int indexFirstTheta = getIndexFirstThetaForState(state);
        double mean = thetaVector.getEntry(indexFirstTheta);
        double std = Math.exp(thetaVector.getEntry(indexFirstTheta+NOF_THETAS_PER_STATE-1));
        return Pair.create(mean, std);
    }

    /**
     * https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
     */
    private double sampleFromNormDistribution(Pair<Double, Double> meanStdPair) {
        return meanStdPair.getFirst()+random.nextGaussian() * meanStdPair.getSecond();
    }

    private static double[] createGradLogAllStates(int stateObserved, double[] gradLogForStateObserved) {
        double[] gradLogAllStates = createArrayWithSameDoubleNumber(getThetaLength(), 0);
        int indexSource = 0;
        int indexFirstTheta = getIndexFirstThetaForState(stateObserved);
        arraycopy(gradLogForStateObserved, indexSource, gradLogAllStates, indexFirstTheta, NOF_THETAS_PER_STATE);
        return gradLogAllStates;
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
