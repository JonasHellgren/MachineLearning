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
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.interfaces.SettingsEnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.aggregates.DisCoMemory;
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

    public static final double SMALLEST_DENOM = 1e-5;
    public static final double SOC_MIN = 0d;
    public static final double STD_TAR = 0d;

    StateI<V> state;
    SettingsEnvironmentI settings;
    DisCoMemory<V> actorMean, actorLogStd, critic;
    NormDistributionSampler sampler = new NormDistributionSampler();
    EntropyCalculatorContActions entropyCalculator = new EntropyCalculatorContActions();
    NormalDistributionGradientCalculator gradientCalculator =
            new NormalDistributionGradientCalculator(SMALLEST_DENOM);
    @Getter
    AgentParameters parameters;
    ActorMemoryUpdater<V> actorMemoryUpdater;
    LossTracker lossTracker = new LossTracker();

    public static <V> AgentACDCSafe<V> of(AgentParameters parameters,
                                          SettingsEnvironmentI settings,
                                          StateI<V> state) {
        return AgentACDCSafe.<V>builder()
                .parameters(parameters)
                .settings(settings)
                .state(state)
                .build();
    }

    @Builder
    AgentACDCSafe(@NonNull AgentParameters parameters,
                  @NonNull SettingsEnvironmentI settings,
                  @NonNull StateI<V> state) {
        this.state = state;
        this.settings = settings;
        int nThetas = state.nContinuousFeatures() + 1;
        this.parameters = parameters;
        var p = parameters;
        this.actorMean = new DisCoMemory<>(nThetas, p.learningRateActorMean(), p.gradMaxActor());
        this.actorLogStd = new DisCoMemory<>(nThetas, p.learningRateActorStd(), p.gradMaxActor());
        this.critic = new DisCoMemory<>(nThetas, p.learningRateCritic(), p.gradMaxCritic());
        this.actorMemoryUpdater = new ActorMemoryUpdater<>(actorMean, actorLogStd, parameters);
        initMemories(state);
    }

    private void initMemories(@NotNull StateI<V> state) {
        getInitializer(state, actorMean, parameters.targetMean()).initialize();
        getInitializer(state, actorLogStd, parameters.targetLogStd()).initialize();
        getInitializer(state, critic, parameters.targetCritic()).initialize();
    }

    @Override
    public Action chooseAction(StateI<V> state) {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state));
        return Action.ofDouble(a);
    }

    @Override
    public Action chooseActionNominal(StateI<V> state) {
        return Action.ofDouble(parameters.absActionNominal());
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
        lossTracker.addMeanAndStdLoss(actorMean.lossLastUpdate(), actorLogStd.lossLastUpdate());
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
        return lossTracker.averageMeanLosses() + lossTracker.averageStdLosses();
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
        return Pair.create(actorMean.read(state), Math.exp(actorLogStd.read(state)));
    }

    private DisCoMemoryInitializer<V> getInitializer(StateI<V> state,
                                                     DisCoMemory<V> memory1,
                                                     double tarValue) {
        return DisCoMemoryInitializer.<V>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        doublesStartEndStep(0, settings.timeEnd(), settings.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(settings.socMax())))
                .valTarMeanStd(Pair.create(tarValue,STD_TAR))
                .state(state)
                .build();
    }

    /**
     * std memory stores log std, to print std, exp(log std) is needed for each element
     */

    @Override
    public String toString() {
        return System.lineSeparator() +
                "critic() = " + critic + System.lineSeparator() +
                "actor mean() = " + actorMean + System.lineSeparator() +
                "actor std() = " + actorLogStd.toStringWithValueMapper(arr ->
                Arrays.stream(arr, 0, arr.length).map(Math::exp).toArray());


    }

}
