package policy_gradient_problems.environments.sink_the_ship;

import common.list_arrays.ArrayUtil;
import common.other.NormDistributionSampler;
import common.dl4j.LossPPO;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import common.list_arrays.SubArrayExtractor;
import policy_gradient_problems.domain.param_memories.CriticMemoryParamOneHot;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static common.other.MyFunctions.*;
import static common.other.SoftMaxEvaluator.getProbabilities;


/***
 * See sinkShip.md for description
 */

@Getter
@Setter
public class AgentShipParam extends AgentA<VariablesShip> implements AgentParamActorTabCriticI<VariablesShip> {

    public static final double THETA_MEAN = 0.5;
    public static final double THETA_STD = Math.log(0.5);  //std=exp(log(0.5)=0.5 => a ~ N(0.5,0.5)
    public static final int NOF_THETAS_PER_STATE = 2;
    public static final double STD_MIN = 2.5e-2;
    public static final double SMALLEST_DENOM = 1e-2;
    public static final double MAX_GRAD_ELEMENT = 1;

    ActorMemoryParam actor;
    CriticMemoryParamOneHot critic;

    NormDistributionSampler sampler;
    SubArrayExtractor subArrayExtractor;

    public static AgentShipParam newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                new double[]{THETA_MEAN,THETA_STD,THETA_MEAN,THETA_STD});
    }

    public static AgentShipParam newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentShipParam(EnvironmentShip.getRandomPos(), thetaArray);
    }

    public AgentShipParam(int stateStart, double[] thetaArray) {
        super(new StateShip(new VariablesShip(stateStart)));
        this.actor = new ActorMemoryParam(thetaArray);
        this.critic =new CriticMemoryParamOneHot(EnvironmentShip.POSITIONS.size());
        this.sampler=new NormDistributionSampler();
        this.subArrayExtractor=new SubArrayExtractor(getThetaLength(),NOF_THETAS_PER_STATE);
    }

    @Override
    public Action chooseAction() {
        int pos = getState().getVariables().pos();
        throwIfBadState(pos);
        Pair<Double,Double> meanStdPair = meanAndStd(pos);
        return Action.ofDouble(sampler.sampleFromNormDistribution(meanStdPair));
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @SneakyThrows
    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        throw new NoSuchMethodException();
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesShip> state, Action action) {
        int pos = state.getVariables().pos();
        double[] thetasForState = calculateGradLogForState(pos, action.asDouble());
        double[] gradLogAllStates = subArrayExtractor.arrayWithZeroExceptAtSubArray(pos, thetasForState);
        return new ArrayRealVector(ArrayUtil.clip(gradLogAllStates,-MAX_GRAD_ELEMENT,MAX_GRAD_ELEMENT));
    }


    DoubleBinaryOperator scaleToAccountThatStdIsExpTheta= (g, k) -> g*k;

    private double[] calculateGradLogForState(int state, double action) {
        var meanAndStd= meanAndStd(state);
        double mean=meanAndStd.getFirst();
        double std=meanAndStd.getSecond();
        double denom = secondArgIfSmaller.apply(sqr2.apply(std), SMALLEST_DENOM);
        double gradMean=1/denom*(action-mean);
        double gradStd=zeroIfTrueElseNum.apply(std<STD_MIN,sqr2.apply(action-mean)/sqr3.apply(std)-1d/std);
        double gradStdTheta=scaleToAccountThatStdIsExpTheta.applyAsDouble(gradStd,1d/std);
        return new double[]{gradMean,gradStdTheta};
    }

    public Pair<Double,Double> meanAndStd(int state) {
        throwIfBadState(state);
        int indexFirstTheta = subArrayExtractor.getIndexFirstThetaForSubArray(state);
        double mean = actor.getValue(indexFirstTheta, LossPPO.MEAN_CONT_INDEX);
        double std = Math.exp(actor.getValue(indexFirstTheta,LossPPO.STD_CONT_INDEX));
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
