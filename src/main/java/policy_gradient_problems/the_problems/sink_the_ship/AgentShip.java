package policy_gradient_problems.the_problems.sink_the_ship;

import common.ArrayUtil;
import common.ListUtils;
import common.NormDistributionSampler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common.ParamFunction;
import policy_gradient_problems.common.SubArrayExtractor;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.List;
import java.util.function.BiFunction;

import static common.MyFunctions.*;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;


/***
 * See sinkShip.md for description
 */

@Getter
@Setter
public class AgentShip extends AgentA<VariablesShip> implements AgentParamActorTabCriticI<VariablesShip> {

    public static final double THETA_MEAN = 0.5,THETA_STD = Math.log(0.5);  //std=exp(log(0.5)=0.5 => a ~ N(0.5,0.5)
    public static final int NOF_THETAS_PER_STATE = 2;
    public static final double STD_MIN = 2.5e-2,  SMALLEST_DENOM = 1e-2,  MAX_GRAD_ELEMENT = 1;

    ParamFunction actor;
    TabularValueFunction critic;

    NormDistributionSampler sampler;
    SubArrayExtractor subArrayExtractor;

    public static AgentShip newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                new double[]{THETA_MEAN,THETA_STD,THETA_MEAN,THETA_STD});
    }

    public static AgentShip newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentShip(EnvironmentShip.getRandomPos(), thetaArray);
    }

    public AgentShip(int stateStart, double[] thetaArray) {
        super(new StateShip(new VariablesShip(stateStart)));
        this.actor = new ParamFunction(thetaArray);
        this.critic =new TabularValueFunction(EnvironmentShip.POSITIONS.size());
        this.sampler=new NormDistributionSampler();
        this.subArrayExtractor=new SubArrayExtractor(getThetaLength(),NOF_THETAS_PER_STATE);
    }

    @Override
    public Action chooseAction() {
        int pos = getState().getVariables().pos();
        throwIfBadState(pos);
        Pair<Double,Double> meanStdPair = getMeanAndStdFromThetaVector(pos);
        return Action.ofDouble(sampler.sampleFromNormDistribution(meanStdPair));
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public List<Double> getActionProbabilities() {
        var thetaArr= actor.toArray();
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesShip> state, Action action) {
        int pos = state.getVariables().pos();
        double[] thetasForState = calculateGradLogForState(pos, action.asDouble());
        double[] gradLogAllStates = subArrayExtractor.arrayWithZeroExceptAtSubArray(pos, thetasForState);
        return new ArrayRealVector(ArrayUtil.clip(gradLogAllStates,-MAX_GRAD_ELEMENT,MAX_GRAD_ELEMENT));
    }

    public void setRandomState() {
        setState(StateShip.newFromPos(EnvironmentShip.getRandomPos()));
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
        double mean = actor.getEntry(indexFirstTheta);
        double std = Math.exp(actor.getEntry(indexFirstTheta+NOF_THETAS_PER_STATE-1));
        return Pair.create(mean, std);
    }

    private static void throwIfBadState(int state) {
        if (!EnvironmentShip.POSITIONS.contains(state)) {
            throw new IllegalArgumentException("Non valid state, state = " + state);
        }
    }

    public static int getThetaLength() {
        return EnvironmentShip.POSITIONS.size() * NOF_THETAS_PER_STATE;
    }

    @Override
    public void changeCritic(int key, double change) {
        critic.setValue(key,getCriticValue(key)+change);
    }

    @Override
    public double getCriticValue(int key) {
        return critic.getValue(key);
    }
}
