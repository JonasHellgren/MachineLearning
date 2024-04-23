package safe_rl.environments.buying_electricity;

import common.dl4j.EntropyCalculatorContActions;
import common.math.MathUtils;
import common.math.NormalDistributionGradientCalculator;
import common.math.SafeGradientClipper;
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
import safe_rl.helpers.LossTracker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
public class AgentACDCSafeBuyer implements AgentACDiscoI<VariablesBuying> {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 0.01;
    public static final double SMALLEST_DENOM = 1e-5;
    public static final double MAX_GRAD_ELEMENT = 1;
    public static final double SOC_MIN = 0d;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_LOG_STD = 5d;
    public static final double TAR_CRITIC = 0d;
    public static final double STD_TAR = 0d;
    public static final double DELTA_THETA_MAX = 10d;

    StateI<VariablesBuying> state;
    BuySettings settings;
    DisCoMemory<VariablesBuying> actorMean, actorLogStd, critic;
    NormDistributionSampler sampler = new NormDistributionSampler();
    EntropyCalculatorContActions entropyCalculator = new EntropyCalculatorContActions();
    NormalDistributionGradientCalculator gradientCalculator =
            new NormalDistributionGradientCalculator(SMALLEST_DENOM);
    double tarStdInit;
    LossTracker lossTracker=new LossTracker();
    SafeGradientClipper meanGradClipper, stdGradClipper;


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
        double dThetaMax = defaultIfNullDouble.apply(deltaThetaMax, DELTA_THETA_MAX);
        this.actorMean = new DisCoMemory<>(nThetas, lram, dThetaMax);
        this.actorLogStd = new DisCoMemory<>(nThetas, lras, dThetaMax);
        this.critic = new DisCoMemory<>(nThetas, lrc, dThetaMax);
        double tarM = defaultIfNullDouble.apply(targetMean, TAR_MEAN);
        double tarLogStdInit = defaultIfNullDouble.apply(targetLogStd, TAR_LOG_STD);
        tarStdInit = Math.exp(tarLogStdInit);
        double tarC = defaultIfNullDouble.apply(targetCritic, TAR_CRITIC);
        getInitializer(state, actorMean, tarM).initialize();
        getInitializer(state, actorLogStd, tarLogStdInit).initialize();
        getInitializer(state, critic, tarC).initialize();
        meanGradClipper=SafeGradientClipper.builder()
                .gradMin(-dThetaMax).gradMax(dThetaMax)
                .valueMin(tarM-2*tarStdInit).valueMax(tarM+2*tarStdInit) //debatable use 2 std
                .build();
        stdGradClipper=SafeGradientClipper.builder()
                .gradMin(-dThetaMax).gradMax(dThetaMax)
                .valueMin(Math.log(STD_MIN)).valueMax(tarLogStdInit)
                .build();
    }

    @Override
    public Action chooseAction(StateI<VariablesBuying> state) {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state));
        return Action.ofDouble(a);
    }

    @Override
    public Action chooseActionNoExploration(StateI<VariablesBuying> state) {
        double a = sampler.sampleFromNormDistribution(actorMeanAndStd(state).getFirst(),0);
        return Action.ofDouble(a);
    }

    @Override
    public Pair<Double, Double> fitActor(StateI<VariablesBuying> state, Action action, double adv) {
        var gradMeanAndLogStd = gradientCalculator.gradient(action.asDouble(), actorMeanAndStd(state));
        double gradMean0=gradMeanAndLogStd.getFirst() * adv;
        double gradStd0=gradMeanAndLogStd.getSecond() * adv;
        actorMean.fitFromError(state, meanGradClipper.modify(gradMean0,actorMean.read(state)));
        actorLogStd.fitFromError(state, stdGradClipper.modify(gradStd0,actorLogStd.read(state)));
        lossTracker.addMeanAndStdLoss(actorMean.lossLastUpdate(),actorLogStd.lossLastUpdate());
        return gradMeanAndLogStd;
    }

    @Override
    public Pair<Double, Double> readActor(StateI<VariablesBuying> state) {
        return actorMeanAndStd(state);
    }

    @Override
    public void fitCritic(StateI<VariablesBuying> state, double error) {
        critic.fitFromError(state, error);
        lossTracker.addCriticLoss(critic.lossLastUpdate());
    }

    @Override
    public double readCritic(StateI<VariablesBuying> state) {
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
    public double entropy(StateI<VariablesBuying> state) {
        var mAndS = actorMeanAndStd(state);
        return entropyCalculator.entropy(mAndS.getSecond());
    }

    Pair<Double, Double> actorMeanAndStd(StateI<VariablesBuying> state) {
        return Pair.create(
                actorMean.read(state),
                MathUtils.clip(Math.exp(actorLogStd.read(state)), STD_MIN, tarStdInit));
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
