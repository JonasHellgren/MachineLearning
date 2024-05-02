package safe_rl.domain.agents;

import common.dl4j.EntropyCalculatorContActions;
import common.math.NormalDistributionGradientCalculator;
import common.math.SafeGradientClipper;
import common.other.NormDistributionSampler;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.SettingsI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.helpers.DisCoMemoryInitializer;
import safe_rl.helpers.LossTracker;
import java.util.Arrays;
import java.util.List;

import static common.list_arrays.ListUtils.doublesStartEndStep;
import static common.other.MyFunctions.*;

/**
 * ACDC = actor critic with discrete and continuous memory
 * <p>
 * Memorize log std instead of the std directly in continuous action methods provides critical benefits,
 * particularly in ensuring that std remains always positive, enhancing numerical stability,
 * and improving the efficiency of learning gradients.
 */

@Getter
public class AgentACDCSafe<V> implements AgentACDiscoI<V> {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 0.01;
    public static final double SMALLEST_DENOM = 1e-5;
    public static final double MAX_GRAD_ELEMENT = 1;
    public static final double SOC_MIN = 0d;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_LOG_STD = 5d;
    public static final double TAR_CRITIC = 0d;
    public static final double STD_TAR = 0d;
    public static final double GRADIENT_MAX = 10d;
    public static final double ABS_TAR_MEAN = 1d;

    StateI<V> state;
    SettingsI settings;
    DisCoMemory<V> actorMean, actorLogStd, critic;
    NormDistributionSampler sampler = new NormDistributionSampler();
    EntropyCalculatorContActions entropyCalculator = new EntropyCalculatorContActions();
    NormalDistributionGradientCalculator gradientCalculator =
            new NormalDistributionGradientCalculator(SMALLEST_DENOM);
    double tarStdInit, tarMeanInit;
    double absActionNominal;
    LossTracker lossTracker=new LossTracker();
    SafeGradientClipper meanGradClipper, stdGradClipper;

    public static <V> AgentACDCSafe<V> newDefault(SettingsI settings, StateI<V> state) {
        return AgentACDCSafe.<V>builder()
                .learningRateActorMean(LEARNING_RATE)
                .learningRateActorStd(LEARNING_RATE)
                .learningRateCritic(LEARNING_RATE)
                .settings(settings)
                .state(state)
                .build();
    }

    @Builder
    public AgentACDCSafe(Double learningRateActorMean,
                         Double learningRateActorStd,
                         Double learningRateCritic,
                         @NonNull SettingsI settings,
                         Double targetMean,
                         Double absActionNominal,
                         Double targetLogStd,
                         Double targetCritic,
                         Double gradMaxActor0, Double gradMaxCritic0,
                         @NonNull StateI<V> state) {
        this.state = state;
        this.settings = settings;
        int nThetas = state.nContinousFeatures() + 1;
        double lram = defaultIfNullDouble.apply(learningRateActorMean, LEARNING_RATE);
        double lras = defaultIfNullDouble.apply(learningRateActorStd, LEARNING_RATE);
        double lrc = defaultIfNullDouble.apply(learningRateCritic, LEARNING_RATE);
        double gradMaxActor = defaultIfNullDouble.apply(gradMaxActor0, GRADIENT_MAX);
        double gradMaxCritic = defaultIfNullDouble.apply(gradMaxCritic0, GRADIENT_MAX);

        this.actorMean = new DisCoMemory<>(nThetas, lram, gradMaxActor);
        this.actorLogStd = new DisCoMemory<>(nThetas, lras, gradMaxActor);
        this.critic = new DisCoMemory<>(nThetas, lrc, gradMaxCritic);
        this.tarMeanInit = defaultIfNullDouble.apply(targetMean, TAR_MEAN);
        this.absActionNominal = defaultIfNullDouble.apply(absActionNominal, ABS_TAR_MEAN);
        double tarLogStdInit = defaultIfNullDouble.apply(targetLogStd, TAR_LOG_STD);
        tarStdInit = Math.exp(tarLogStdInit);
        double tarC = defaultIfNullDouble.apply(targetCritic, TAR_CRITIC);
        getInitializer(state, actorMean, tarMeanInit).initialize();
        getInitializer(state, actorLogStd, tarLogStdInit).initialize();
        getInitializer(state, critic, tarC).initialize();
        meanGradClipper=SafeGradientClipper.builder()
                .gradMin(-gradMaxActor).gradMax(gradMaxActor)
                .valueMin(tarMeanInit -2*tarStdInit).valueMax(tarMeanInit +2*tarStdInit) //debatable use 2 std
                .build();
        stdGradClipper=SafeGradientClipper.builder()
                .gradMin(-gradMaxActor).gradMax(gradMaxActor)
                .valueMin(Math.log(STD_MIN)).valueMax(tarLogStdInit)
                .build();
    }

    @Override
    public Action chooseAction(StateI<V> state) {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state));
        return Action.ofDouble(a);
    }

    @Override
    public Action chooseActionNominal(StateI<V> state) {
        return Action.ofDouble(absActionNominal);
    }

    @Override
    public Action chooseActionNoExploration(StateI<V> state) {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state).getFirst(),
                0);
        return Action.ofDouble(a);
    }


    @Override
    public Pair<Double, Double> fitActor(StateI<V> state, Action action, double adv) {
        var grad = gradientMeanAndStd(state, action);
        double gradMean0=grad.getFirst() * adv;
        double gradStd0=grad.getSecond() * adv;
        actorMean.fitFromError(state, meanGradClipper.modify(gradMean0,actorMean.read(state)));
        actorLogStd.fitFromError(state, stdGradClipper.modify(gradStd0,actorLogStd.read(state)));
        lossTracker.addMeanAndStdLoss(actorMean.lossLastUpdate(),actorLogStd.lossLastUpdate());
        return grad;
    }

    @Override
    public Pair<Double, Double> gradientMeanAndStd(StateI<V> state, Action action) {
        return gradientCalculator.gradient(action.asDouble(), actorMeanAndStd(state));
    }

    @Override
    public Pair<Double, Double> readActor(StateI<V> state) {
        return actorMeanAndStd(state);
    }

    @Override
    public void fitCritic(StateI<V> state, double error) {
        critic.fitFromError(state, error);
        lossTracker.addCriticLoss(critic.lossLastUpdate());
    }

    @Override
    public double readCritic(StateI<V> state) {
        return critic.read(state);
    }

    @Override
    public double lossCriticLastUpdates() {
        return lossTracker.averageCriticLosses();
    }

    @Override
    public void clearCriticLosses() {
        lossTracker.clearCriticLosses();
    }

    @Override
    public double lossActorLastUpdates() {
        return lossTracker.averageMeanLosses()+lossTracker.averageStdLosses();
    }

    @Override
    public void clearActorLosses() {
        lossTracker.clearActorLosses();
    }

    @Override
    public double entropy(StateI<V> state) {
        var mAndS = actorMeanAndStd(state);
        return entropyCalculator.entropy(mAndS.getSecond());
    }

    Pair<Double, Double> actorMeanAndStd(StateI<V> state) {
        return Pair.create(actorMean.read(state),Math.exp(actorLogStd.read(state)));
    }

    private DisCoMemoryInitializer<V> getInitializer(StateI<V> state,
                                                                   DisCoMemory<V> memory1,
                                                                   double tarValue) {
        return DisCoMemoryInitializer.<V>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        doublesStartEndStep(0, settings.timeEnd(), settings.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(settings.socMax())))
                .valTarMeanStd(Pair.create(tarValue, STD_TAR))
                .state(state)
                .build();
    }

    /**
     * std memory stores log std, to print std, exp(log std) is needed for each element
     */

    @Override
    public String toString() {
        return  System.lineSeparator() +
                "critic() = " + critic + System.lineSeparator() +
                "actor mean() = " + actorMean + System.lineSeparator() +
                "actor std() = " + actorLogStd.toStringWithValueMapper(arr ->
             Arrays.stream(arr, 0, arr.length).map(Math::exp).toArray());


    }

}
