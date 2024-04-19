package safe_rl.environments.buying_electricity;

import common.dl4j.EntropyCalculatorContActions;
import common.math.MathUtils;
import common.math.NormalDistributionGradientCalculator;
import common.other.NormDistributionSampler;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.helpers.DisCoMemoryInitializer;

import java.util.List;

import static common.list_arrays.ListUtils.doublesStartEndStep;
import static common.other.MyFunctions.*;

/**
 * ACDC = actor critic with discrete and continuous memory
 *
 * Memorize log std instead of the std directly in continuous action methods provides critical benefits,
 * particularly in ensuring that std remains always positive, enhancing numerical stability,
 * and improving the efficiency of learning gradients.
 */

@Getter
public class AgentACDCSafeBuyer implements AgentACDiscoI<VariablesBuying> {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 0.05;
    public static final double SMALLEST_DENOM = 1e-2;
    public static final double MAX_GRAD_ELEMENT = 1;
    public static final double SOC_MIN = 0d;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_LOG_STD = 5d;
    public static final double TAR_CRITIC = 0d;
    public static final double STD_TAR = 0d;
    public static final double DELTA_THETA_MAX = 10d;


    StateI<VariablesBuying> state;
    BuySettings settings;
    DisCoMemory<VariablesBuying> actorMean;
    DisCoMemory<VariablesBuying> actorLogStd;
    DisCoMemory<VariablesBuying> critic;
    NormDistributionSampler sampler = new NormDistributionSampler();
    EntropyCalculatorContActions entropyCalculator=new EntropyCalculatorContActions();
    NormalDistributionGradientCalculator gradientCalulator=
            new NormalDistributionGradientCalculator(SMALLEST_DENOM);
    double tarStdInit;

    public static AgentACDCSafeBuyer newDefault(BuySettings settings) {
        return AgentACDCSafeBuyer.builder()
                .learningRateActorMean(LEARNING_RATE)
                .learningRateActorStd(LEARNING_RATE)
                .learningRateCritic(LEARNING_RATE)
                .settings(settings)
                .state(StateBuying.newZero())
                .build();
    }

    @Builder
    public AgentACDCSafeBuyer(Double learningRateActorMean,
                              Double learningRateActorStd,
                              Double learningRateCritic,
                              @NonNull BuySettings settings,
                              Double targetMean,
                              Double targetLogStd,
                              Double targetCritic,
                              Double deltaThetaMax,
                              @NonNull StateBuying state) {
        this.state = state;
        this.settings = settings;
        int nThetas = state.nContinousFeatures() + 1;
        double lram = defaultIfNullDouble.apply(learningRateActorMean, LEARNING_RATE);
        double lras = defaultIfNullDouble.apply(learningRateActorStd, LEARNING_RATE);
        double lrc = defaultIfNullDouble.apply(learningRateCritic, LEARNING_RATE);
        double dThetaMax=defaultIfNullDouble.apply(deltaThetaMax, DELTA_THETA_MAX);
        this.actorMean = new DisCoMemory<>(nThetas, lram, dThetaMax);
        this.actorLogStd = new DisCoMemory<>(nThetas, lras, dThetaMax);
        this.critic = new DisCoMemory<>(nThetas, lrc, dThetaMax);
        double tarM = defaultIfNullDouble.apply(targetMean, TAR_MEAN);
        double tarLogStdInit = defaultIfNullDouble.apply(targetLogStd, TAR_LOG_STD);
        tarStdInit=Math.exp(tarLogStdInit);
        double tarC = defaultIfNullDouble.apply(targetCritic, TAR_CRITIC);
        var initializer = getInitializer(state, actorMean, tarM);
        initializer.initialize();
        initializer = getInitializer(state, actorLogStd, tarLogStdInit);
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
        var gradMeanAndLogStd=gradientCalulator.gradient(action.asDouble(), actorMeanAndStd(state));
        actorMean.fitFromError(state, gradMeanAndLogStd.getFirst() * adv);
        actorLogStd.fitFromError(state, gradMeanAndLogStd.getSecond() * adv);
        return gradMeanAndLogStd;
    }

    @Override
    public Pair<Double, Double> readActor() {
        return readActor(state);
    }

    @Override
    public Pair<Double, Double> readActor(StateI<VariablesBuying> state) {
        return  actorMeanAndStd(state);
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

    @Override
    public double lossCriticLastUpdate() {
        return critic.lossLastUpdate();
    }

    @Override
    public double lossActorLastUpdate() {
        return actorMean.lossLastUpdate();
    }

    @Override
    public double entropy() {
        var mAndS=actorMeanAndStd(state);
        return entropyCalculator.entropy(mAndS.getSecond());
    }

    Pair<Double, Double> actorMeanAndStd(StateI<VariablesBuying> state) {
        return Pair.create(
                actorMean.read(state),
                MathUtils.clip(Math.exp(actorLogStd.read(state)),STD_MIN, tarStdInit));
    }

    private DisCoMemoryInitializer<VariablesBuying> getInitializer(StateBuying state,
                                                                   DisCoMemory<VariablesBuying> memory1,
                                                                   double tarValue) {
        return DisCoMemoryInitializer.<VariablesBuying>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        doublesStartEndStep(0, settings.timeEnd(), settings.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(settings.socMax())))
                .valTarMeanStd(Pair.create(tarValue, STD_TAR))
                .state(state)
                .build();
    }




}
