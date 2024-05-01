package safe_rl.domain.trainers;

import com.google.common.collect.*;
import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.other.Conditionals;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.EpisodeInfoMultiStep;
import java.util.List;
import java.util.function.ToDoubleFunction;
import static common.math.MathUtils.clip;

/***
 * This class extracts mini batch for any time, weights in critic memory at this time is fitted
 * Using linear regression from multiple data points stabilizes fitting
 */

@Log
public class FitterUsingReplayBuffer<V> {

    public static final int N_FEAT = 1;
    public static final int MIN_NOF_POINTS = 2;
    AgentACDiscoI<V> agent;
    TrainerParameters parameters;
    int indexFeature;
    LinearBatchFitter fitterCritic, fitterActor;

    public FitterUsingReplayBuffer(AgentACDiscoI<V> agent, TrainerParameters trainerParameters, int indexFeature) {
        this.agent = agent;
        this.parameters = trainerParameters;
        this.fitterCritic = new LinearBatchFitter(trainerParameters.learningRateReplayBufferCritic());
        this.fitterActor = new LinearBatchFitter(trainerParameters.learningRateReplayBufferActor());
    }

    public void fit(ReplayBufferMultiStepExp<V> buffer) {
        var experiences = getExperiencesAtRandomTime(buffer);
        var stateAtTime = experiences.stream()
                .findAny().map(e -> e.state()).orElseThrow();
        fitCritic(experiences, stateAtTime);
        fitActorMean(experiences, stateAtTime);
    }

    private void fitActorMean(List<ExperienceMultiStep<V>> experiences, StateI<V> stateAtTime) {
        var actorMean=agent.getActorMean();
        RealVector paramsActor = new ArrayRealVector(actorMean.readThetas(stateAtTime));
        var batchDataActor = createData(experiences,lossFcn);
        paramsActor = fitterActor.fitFromErrors(paramsActor, batchDataActor);
        actorMean.save(stateAtTime, paramsActor.toArray());
    }

    private void fitCritic(List<ExperienceMultiStep<V>> experiences, StateI<V> stateAtTime) {
        var critic=agent.getCritic();
        RealVector paramsCritic = new ArrayRealVector(critic.readThetas(stateAtTime));
        var batchData = createData(experiences,targetFcn);
        paramsCritic = fitterCritic.fit(paramsCritic, batchData);
        critic.save(stateAtTime, paramsCritic.toArray());
    }

    private  List<ExperienceMultiStep<V>> getExperiencesAtRandomTime(ReplayBufferMultiStepExp<V> buffer) {
        var batch = buffer.getMiniBatch(parameters.miniBatchSize());
        var ei = new EpisodeInfoMultiStep<>(batch);
        var presentTimes = ei.getValuesOfSpecificDiscreteFeature(indexFeature)
                .stream().toList();
        int timeChosen = getMostFrequentTime(presentTimes);
        return ei.getExperiencesWithDiscreteFeatureValue(timeChosen, indexFeature);
    }

    ToDoubleFunction<ExperienceMultiStep<V>> targetFcn=exp -> exp.isStateFutureTerminalOrNotPresent()
            ? exp.sumOfRewards()
            : exp.sumOfRewards() + parameters.gammaPowN()* agent.readCritic(exp.stateFuture());

    ToDoubleFunction<ExperienceMultiStep<V>> lossFcn=exp -> {
        var grad = agent.gradientMeanAndStd(exp.state(), exp.actionApplied());
        double vState = agent.readCritic(exp.state());
        double advantage=valueTarget(exp)-vState;
        double gradMax= parameters.gradMeanActorMaxBufferFitting();
        return clip(-grad.getFirst()*advantage,-gradMax, gradMax);  //MINUS <=> maximize loss
    };


    private static int getMostFrequentTime(List<Integer> presentTimes) {
        Multiset<Integer> multiset = HashMultiset.create(presentTimes);
        var maxEntry = multiset.entrySet().stream()
                .max(Ordering.natural().onResultOf(Multiset.Entry::getCount));
        Conditionals.executeIfTrue(maxEntry.orElseThrow().getCount()< MIN_NOF_POINTS, () ->
                log.warning("Few items in time step for fitting"));
        return maxEntry.orElseThrow().getElement();
    }

    Pair<RealMatrix, RealVector> createData(List<ExperienceMultiStep<V>> experiencesAtChosenTime,
                                            ToDoubleFunction<ExperienceMultiStep<V>> entry) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, N_FEAT);
        var yVec = new ArrayRealVector(nPoints);
        int i = 0;
        for (ExperienceMultiStep<V> experience : experiencesAtChosenTime) {
            xMat.setRow(i, getFeatures(experience));
            yVec.setEntry(i, entry.applyAsDouble(experience));
            i++;
        }
        return Pair.create(xMat, yVec);
    }

    double[] getFeatures(ExperienceMultiStep<V> experience) {
        return new double[]{experience.state().continousFeatures()[indexFeature]};
    }

    private double valueTarget(ExperienceMultiStep<V> experience) {
        return experience.isStateFutureTerminalOrNotPresent()
                ? experience.sumOfRewards()
                : experience.sumOfRewards() + parameters.gammaPowN()* agent.readCritic(experience.stateFuture());
    }


}
