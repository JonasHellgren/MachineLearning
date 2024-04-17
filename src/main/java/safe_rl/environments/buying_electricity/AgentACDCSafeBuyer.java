package safe_rl.environments.buying_electricity;

import com.google.common.base.Preconditions;
import common.other.NormDistributionSampler;
import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static common.dl4j.LossPPO.MEAN_CONT_INDEX;
import static common.dl4j.LossPPO.STD_CONT_INDEX;
import static common.other.MyFunctions.*;

/**
 * ACDC = actor critic with discrete and continuous memory
 */

public class AgentACDCSafeBuyer implements AgentACDiscoI<VariablesBuying> {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 2.5e-2;
    public static final double SMALLEST_DENOM = 1e-2;
    public static final double MAX_GRAD_ELEMENT = 1;

    StateI<VariablesBuying> state;
    DisCoMemory<VariablesBuying> actorMean;
    DisCoMemory<VariablesBuying> actorStd;
    DisCoMemory<VariablesBuying> critic;
    NormDistributionSampler sampler = new NormDistributionSampler();

    public static AgentACDCSafeBuyer newDefault() {
        return AgentACDCSafeBuyer.builder()
                .state(StateBuying.newZero())
                .learningRateActor(LEARNING_RATE).learningRateCritic(LEARNING_RATE)
                .build();
    }

    @Builder
    public AgentACDCSafeBuyer(Double learningRateActor, Double learningRateCritic, StateBuying state) {
        this.state = state;
        int nThetas = state.nContinousFeatures() + 1;
        this.actorMean=new DisCoMemory<>(nThetas,learningRateActor);
        this.actorStd=new DisCoMemory<>(nThetas,learningRateActor);
        this.critic=new DisCoMemory<>(nThetas,learningRateCritic);
        //todo init memories
    }

    @Override
    public Action chooseAction() {
        double a = sampler.sampleFromNormDistribution(meanAndStd(state));
        return Action.ofDouble(a);
    }

    @Override
    public StateI<VariablesBuying> getState() {
        return state;
    }

    @Override
    public void setState(StateI<VariablesBuying> state) {
        this.state=state;
    }

    @Override
    public void fitActor(Action action, double adv) {
        var gradLogMenAndStd=calcGradLog(state,action.asDouble());
        actorMean.fitFromError(state,gradLogMenAndStd.getFirst()*adv);
        actorStd.fitFromError(state,gradLogMenAndStd.getSecond()*adv);
    }

    @Override
    public Pair<Double,Double> readActor() {
        return meanAndStd(state);
    }

    @Override
    public void fitCritic(double error) {
        critic.fitFromError(state,error);
    }

    @Override
    public double readCritic() {
        return critic.read(state);
    }

    Pair<Double,Double> meanAndStd(StateI<VariablesBuying> state) {
        return Pair.create(actorMean.read(state), actorStd.read(state));
    }

    //todo below in sep class
    DoubleBinaryOperator scaleToAccountThatStdIsExpTheta= (g, k) -> g*k;

    Pair<Double,Double> calcGradLog(StateI<VariablesBuying> state, double action) {
        var meanAndStd= meanAndStd(state);
        double mean=meanAndStd.getFirst();
        double std=meanAndStd.getSecond();
        double denom = secondArgIfSmaller.apply(sqr2.apply(std), SMALLEST_DENOM);
        double gradMean=1/denom*(action-mean);
        double gradStd=zeroIfTrueElseNum.apply(std<STD_MIN,sqr2.apply(action-mean)/sqr3.apply(std)-1d/std);
        double gradStdTheta=scaleToAccountThatStdIsExpTheta.applyAsDouble(gradStd,1d/std);
        return Pair.create(gradMean,gradStdTheta);
    }

}
