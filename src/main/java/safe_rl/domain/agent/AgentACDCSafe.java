package safe_rl.domain.agent;

import common.dl4j.EntropyCalculatorContActions;
import common.math.NormalDistributionGradientCalculator;
import common.math.SafeGradientClipper;
import common.other.NormDistributionSampler;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import safe_rl.domain.agent.aggregates.ActorMemoryUpdater;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.interfaces.SettingsEnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.aggregates.DisCoMemory;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.agent.aggregates.DisCoMemoryInitializer;
import safe_rl.domain.agent.helpers.LossTracker;
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
    SettingsEnvironmentI settings;
    DisCoMemory<V> actorMean, actorLogStd, critic;
    NormDistributionSampler sampler = new NormDistributionSampler();
    EntropyCalculatorContActions entropyCalculator = new EntropyCalculatorContActions();
    NormalDistributionGradientCalculator gradientCalculator =
            new NormalDistributionGradientCalculator(SMALLEST_DENOM);
    ActorMemoryUpdater<V> actorMemoryUpdater;
    double absActionNominal;
    LossTracker lossTracker=new LossTracker();

    public static <V> AgentACDCSafe<V> newDefault(SettingsEnvironmentI settings, StateI<V> state) {
        return AgentACDCSafe.<V>builder()
                .learningRateActorMean(LEARNING_RATE)
                .learningRateActorStd(LEARNING_RATE)
                .learningRateCritic(LEARNING_RATE)
                .settings(settings)
                .state(state)
                .build();
    }

    public static <V> AgentACDCSafe<V> newFromTrainerParams(TrainerParameters trainerParameters,
                                                             SettingsEnvironmentI settings,
                                                             StateI<V> state) {
        return AgentACDCSafe.<V>builder()
                .learningRateActorMean(trainerParameters.learningRateReplayBufferActor())
                .learningRateActorStd(trainerParameters.learningRateReplayBufferActor())
                .learningRateCritic(trainerParameters.learningRateReplayBufferCritic())
                .targetMean(trainerParameters.targetMean())
                .absActionNominal(trainerParameters.absActionNominal())
                .targetLogStd(trainerParameters.targetLogStd())
                .targetCritic(trainerParameters.targetCritic())
                .gradMaxActor0(trainerParameters.gradActorMax())
                .gradMaxCritic0(trainerParameters.gradCriticMax())
                .settings(settings)
                .state(state)
                .build();
    }

    @Builder
    AgentACDCSafe(Double learningRateActorMean,
                         Double learningRateActorStd,
                         Double learningRateCritic,
                         @NonNull SettingsEnvironmentI settings,
                         Double targetMean,
                         Double absActionNominal,
                         Double targetLogStd,
                         Double targetCritic,
                         Double gradMaxActor0, Double gradMaxCritic0,
                         @NonNull StateI<V> state) {
        this.state = state;
        this.settings = settings;
        int nThetas = state.nContinuousFeatures() + 1;
        double lram = defaultIfNullDouble.apply(learningRateActorMean, LEARNING_RATE);
        double lras = defaultIfNullDouble.apply(learningRateActorStd, LEARNING_RATE);
        double lrc = defaultIfNullDouble.apply(learningRateCritic, LEARNING_RATE);
        double gradMaxActor = defaultIfNullDouble.apply(gradMaxActor0, GRADIENT_MAX);
        double gradMaxCritic = defaultIfNullDouble.apply(gradMaxCritic0, GRADIENT_MAX);
        double tarMeanInit = defaultIfNullDouble.apply(targetMean, TAR_MEAN);
        double tarLogStdInit = defaultIfNullDouble.apply(targetLogStd, TAR_LOG_STD);

        this.actorMean = new DisCoMemory<>(nThetas, lram, gradMaxActor);
        this.actorLogStd = new DisCoMemory<>(nThetas, lras, gradMaxActor);
        this.critic = new DisCoMemory<>(nThetas, lrc, gradMaxCritic);
        this.absActionNominal = defaultIfNullDouble.apply(absActionNominal, ABS_TAR_MEAN);

        this.actorMemoryUpdater=new ActorMemoryUpdater<>(actorMean, actorLogStd, gradMaxActor, tarMeanInit,  tarLogStdInit);
        initMemories(targetCritic, state, tarMeanInit, tarLogStdInit);

    }

    private void initMemories(Double targetCritic, @NotNull StateI<V> state, double tarMeanInit, double tarLogStdInit) {
        double tarC = defaultIfNullDouble.apply(targetCritic, TAR_CRITIC);
        getInitializer(state, actorMean, tarMeanInit).initialize();
        getInitializer(state, actorLogStd, tarLogStdInit).initialize();
        getInitializer(state, critic, tarC).initialize();
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
    public SafeGradientClipper getMeanGradClipper() {
        return actorMemoryUpdater.getMeanGradClipper();
    }

    @Override
    public SafeGradientClipper getStdGradClipper() {
        return actorMemoryUpdater.getStdGradClipper();
    }

    @Override
    public Pair<Double, Double> fitActor(StateI<V> state, Action action, double adv) {
        var grad = gradientMeanAndStd(state, action);
        actorMemoryUpdater.fitActorMemory(state, adv, grad);
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
