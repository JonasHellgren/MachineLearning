package policy_gradient_problems.the_problems.sink_the_ship;

import common.ArrayUtil;
import common.NormDistributionSampler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.common.SubArrayExtractor;

import java.util.function.BiFunction;

import static common.MyFunctions.*;


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
    NormDistributionSampler sampler=new NormDistributionSampler();
    SubArrayExtractor subArrayExtractor;


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
        this.subArrayExtractor=new SubArrayExtractor(getThetaLength(),NOF_THETAS_PER_STATE);
    }

    public double chooseAction(int state) {
        throwIfBadState(state);
        Pair<Double,Double> meanStdPair = getMeanAndStdFromThetaVector(state);
        return sampler.sampleFromNormDistribution(meanStdPair);
    }

    public ArrayRealVector calcGradLogVector(int state, double action) {
        double[] thetasForState = calculateGradLogForState(state, action);
        double[] gradLogAllStates = subArrayExtractor.arrayWithZeroExceptAtSubArray(state, thetasForState);
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
        int indexFirstTheta = subArrayExtractor.getIndexFirstThetaForSubArray(state);
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

}
