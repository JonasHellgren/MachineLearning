package safe_rl.environments.buying_electricity;

import common.other.MyFunctions;
import common.other.NormDistributionSampler;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.helpers.DisCoMemoryInitializer;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static common.list_arrays.ListUtils.createDoubleListStartEndStep;
import static common.other.MyFunctions.*;

/**
 * ACDC = actor critic with discrete and continuous memory
 */

public class AgentACDCSafeBuyer implements AgentACDiscoI<VariablesBuying> {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 2.5e-2;
    public static final double SMALLEST_DENOM = 1e-2;
    public static final double MAX_GRAD_ELEMENT = 1;
    public static final double SOC_MIN = 0d;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_STD = 0.5d;
    public static final double TAR_CRITIC = 0d;
    public static final double STD_TAR = 0d;

    StateI<VariablesBuying> state;
    BuySettings settings;
    DisCoMemory<VariablesBuying> actorMean;
    DisCoMemory<VariablesBuying> actorStd;
    DisCoMemory<VariablesBuying> critic;
    NormDistributionSampler sampler = new NormDistributionSampler();

    public static AgentACDCSafeBuyer newDefault(BuySettings settings) {
        return AgentACDCSafeBuyer.builder()
                .learningRateActor(LEARNING_RATE).learningRateCritic(LEARNING_RATE)
                .settings(settings)
                .state(StateBuying.newZero())
                .build();
    }

    @Builder
    public AgentACDCSafeBuyer(Double learningRateActor,
                              Double learningRateCritic,
                              @NonNull BuySettings settings,
                              Double targetMean,
                              Double targetStd,
                              Double targetCritic,
                              @NonNull StateBuying state) {
        this.state = state;
        this.settings = settings;
        int nThetas = state.nContinousFeatures() + 1;
        double lra= defaultIfNullDouble.apply(learningRateActor,LEARNING_RATE);
        double lrc= defaultIfNullDouble.apply(learningRateCritic,LEARNING_RATE);
        this.actorMean = new DisCoMemory<>(nThetas, lra);
        this.actorStd = new DisCoMemory<>(nThetas, lrc);
        this.critic = new DisCoMemory<>(nThetas, lrc);
        double tarM= defaultIfNullDouble.apply(targetMean,TAR_MEAN);
        double tarS= defaultIfNullDouble.apply(targetStd,TAR_STD);
        double tarC= defaultIfNullDouble.apply(targetCritic,TAR_CRITIC);
        var initializer = getInitializer(state, actorMean, tarM);
        initializer.initialize();
        initializer = getInitializer(state, actorStd, tarS);
        initializer.initialize();
        initializer = getInitializer(state, critic, tarC);
        initializer.initialize();
    }

    @Override
    public Action chooseAction() {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state));
        return Action.ofDouble(a);
    }

    @Override
    public StateI<VariablesBuying> getState() {
        return state;
    }

    @Override
    public void setState(StateI<VariablesBuying> state) {
        this.state = state;
    }

    @Override
    public Pair<Double, Double> fitActor(Action action, double adv) {
        var gradLogMenAndStd = calcGradLog(state, action.asDouble());
        actorMean.fitFromError(state, gradLogMenAndStd.getFirst() * adv);
        actorStd.fitFromError(state, gradLogMenAndStd.getSecond() * adv);
        return gradLogMenAndStd;
    }

    @Override
    public Pair<Double, Double> readActor() {
        return actorMeanAndStd(state);
    }

    @Override
    public void fitCritic(double error) {
        critic.fitFromError(state, error);
    }

    @Override
    public double readCritic() {
        return readCritic(state);
    }

    @Override
    public double readCritic(StateI<VariablesBuying> state) {
        return critic.read(state);
    }

    Pair<Double, Double> actorMeanAndStd(StateI<VariablesBuying> state) {
        return Pair.create(actorMean.read(state), actorStd.read(state));
    }

    private DisCoMemoryInitializer<VariablesBuying> getInitializer(StateBuying state,
                                                                   DisCoMemory<VariablesBuying> memory1,
                                                                   double tarValue) {
        return DisCoMemoryInitializer.<VariablesBuying>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        createDoubleListStartEndStep(0, settings.timeEnd(), settings.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(settings.socMax())))
                .valTarMeanStd(Pair.create(tarValue, STD_TAR))
                .state(state)
                .build();
    }


    //todo below in sep class
    DoubleBinaryOperator scaleToAccountThatStdIsExpTheta = (g, k) -> g * k;

    Pair<Double, Double>    calcGradLog(StateI<VariablesBuying> state, double action) {
        var meanAndStd = actorMeanAndStd(state);
        double mean = meanAndStd.getFirst();
        double std = meanAndStd.getSecond();
        double denom = secondArgIfSmaller.apply(sqr2.apply(std), SMALLEST_DENOM);
        double gradMean = 1 / denom * (action - mean);
        double gradStd = zeroIfTrueElseNum.apply(
                std < STD_MIN, sqr2.apply(action - mean) / sqr3.apply(std) - 1d / std);
        double gradStdTheta = scaleToAccountThatStdIsExpTheta.applyAsDouble(gradStd, 1d / std);
        return Pair.create(gradMean, gradStdTheta);
    }

}
