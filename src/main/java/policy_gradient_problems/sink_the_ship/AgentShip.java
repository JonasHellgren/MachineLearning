package policy_gradient_problems.sink_the_ship;

import common.ArrayUtil;
import common.MyFunctions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.Pair;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.MyFunctions.*;
import static java.lang.System.arraycopy;


/***
 * See sinkShip.md for description
 */

@Getter
@Setter
public class AgentShip {

    public static final double THETA_MEAN = 0.5,THETA_STD = Math.log(0.5);  //std=exp(log(0.5)=0.5 => a ~ N(0.5,0.5)
    public static final int NOF_THETAS_PER_STATE = 2;
    public static final double STD_MIN = 2.5e-2,  SMALLEST_DENOM = 1e-2,  MAX_GRAD_ELEMENT = 1;

    int state;
    ArrayRealVector thetaVector;
    Random random=new Random();

    public static AgentShip newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                new double[]{THETA_MEAN,THETA_STD,THETA_MEAN,THETA_STD});
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
        return sampleFromNormDistribution(meanStdPair);
    }

    public ArrayRealVector calcGradLogVector(int state, double action) {
        double[] gradLogAllStates = createGradLogAllStates(state, calculateGradLogForState(state, action));
        return new ArrayRealVector(ArrayUtil.clip(gradLogAllStates,-MAX_GRAD_ELEMENT,MAX_GRAD_ELEMENT));
    }

    public void setRandomState() {
        state=EnvironmentShip.getRandomState();
    }

    BiFunction<Double,Double,Double> scaleToAccountThatStdIsExpTheta= (g,k) -> g*k;

    private double[] calculateGradLogForState(int state, double action) {
        var meanAndStd=getMeanAndStdFromThetaVector(state);
        double mean=meanAndStd.getFirst();
        double std=meanAndStd.getSecond();
        double denom = secondArgIfSmaller.apply(sqr2.apply(std), SMALLEST_DENOM);
        double gradMean=1/denom*(action-mean);
        double gradStd=zeroIfTrueElseNum.apply(std<STD_MIN,sqr2.apply(action-mean)/sqr3.apply(std)-1d/std);
        double gradStdTheta=scaleToAccountThatStdIsExpTheta.apply(gradStd,1d/std);
        return new double[]{gradMean,gradStdTheta};
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
    //todo NormDistributionSampler
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
