package safe_rl.domain.trainers;

import com.google.common.collect.*;
import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.math.MathUtils;
import common.other.Conditionals;
import common.other.RandUtils;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.EpisodeInfoMultiStep;
import java.util.List;

/***
 * This class extracts mini batch for any time, weights in critic memory at this time is fitted
 * Using linear regression from multiple data points stabilizes fitting
 */

@Log
public class FitterUsingReplayBuffer<V> {

    public static final int N_FEAT = 1;
    public static final int FEATURE_INDEX_SOC = 0;
    public static final int MIN_NOF_POINTS = 2;
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;

    LinearBatchFitter linearFitterCritic, linearFitterActor;
    RandUtils<Integer> intRand;

    public FitterUsingReplayBuffer(AgentACDiscoI<V> agent, TrainerParameters trainerParameters) {
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.linearFitterCritic = new LinearBatchFitter(trainerParameters.learningRateReplayBufferCritic());
        this.linearFitterActor = new LinearBatchFitter(trainerParameters.learningRateReplayBufferActor());
        this.intRand = new RandUtils<>();
    }

    public void fit(ReplayBufferMultiStepExp<V> buffer) {
        var batch = buffer.getMiniBatch(trainerParameters.miniBatchSize());
        var ei = new EpisodeInfoMultiStep<>(batch);
        var presentTimes = ei.getValuesOfSpecificDiscreteFeature(FEATURE_INDEX_SOC)
                .stream().toList();
        int timeChosen = getMostFrequentTime(presentTimes);
        var experiencesAtChosenTime =
                ei.getExperiencesWithDiscreteFeatureValue(timeChosen, FEATURE_INDEX_SOC);
        var anyStateWithTimeChosen = experiencesAtChosenTime.stream()
                .findAny().map(e -> e.state()).orElseThrow();

        DisCoMemory<V> critic=agent.getCritic();
        RealVector weightsAndBias = new ArrayRealVector(critic.readThetas(anyStateWithTimeChosen));
        var batchData = createCriticData(N_FEAT, experiencesAtChosenTime);
        weightsAndBias = linearFitterCritic.fit(weightsAndBias, batchData);
        critic.save(anyStateWithTimeChosen, weightsAndBias.toArray());

        DisCoMemory<V> actorMean=agent.getActorMean();
        RealVector weightsAndBiasActor = new ArrayRealVector(actorMean.readThetas(anyStateWithTimeChosen));
        var batchDataActor = createActorData(N_FEAT, experiencesAtChosenTime);
        weightsAndBiasActor = linearFitterActor.fitFromErrors(weightsAndBiasActor, batchDataActor);
        actorMean.save(anyStateWithTimeChosen, weightsAndBiasActor.toArray());

    }

    private static int getMostFrequentTime(List<Integer> presentTimes) {
        Multiset<Integer> multiset = HashMultiset.create(presentTimes);
        var maxEntry = multiset.entrySet().stream()
                .max(Ordering.natural().onResultOf(Multiset.Entry::getCount));
        Conditionals.executeIfTrue(maxEntry.orElseThrow().getCount()< MIN_NOF_POINTS, () ->
                log.warning("Few items in time step for fitting"));
        return maxEntry.orElseThrow().getElement();
    }


    Pair<RealMatrix, RealVector> createCriticData(int nFeat, List<ExperienceMultiStep<V>> experiencesAtChosenTime) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, nFeat);
        var yVec = new ArrayRealVector(nPoints);
        DisCoMemory<V> critic=agent.getCritic();
        int i = 0;
        for (ExperienceMultiStep<V> experience : experiencesAtChosenTime) {
            double[] features = {experience.state().continousFeatures()[FEATURE_INDEX_SOC]};
            xMat.setRow(i, features);
            yVec.setEntry(i, valueTarget(critic, experience));
            i++;
        }
        return Pair.create(xMat, yVec);
    }

    private double valueTarget(DisCoMemory<V> critic, ExperienceMultiStep<V> experience) {
        return experience.isStateFutureTerminalOrNotPresent()
                ? experience.sumOfRewards()
                : experience.sumOfRewards() + trainerParameters.gammaPowN()* critic.read(experience.stateFuture());
    }

    Pair<RealMatrix, RealVector> createActorData(int nFeat, List<ExperienceMultiStep<V>> experiencesAtChosenTime) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, nFeat);
        var yVec = new ArrayRealVector(nPoints);
        int i = 0;
        double gradMax=trainerParameters.gradMeanActorMaxBufferFitting();
        for (ExperienceMultiStep<V> experience : experiencesAtChosenTime) {
            double[] features = {experience.state().continousFeatures()[FEATURE_INDEX_SOC]};
            var grad = agent.gradientMeanAndStd(experience.state(), experience.actionApplied());
            double vState = agent.readCritic(experience.state());
            double advantage=valueTarget(agent.getCritic(), experience)-vState;
            double error= MathUtils.clip(grad.getFirst()*advantage,-gradMax,gradMax);
            xMat.setRow(i, features);
            yVec.setEntry(i, error);
            i++;
        }
        return Pair.create(xMat, yVec);
    }




}
