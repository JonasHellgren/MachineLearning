package safe_rl.domain.trainers;

import com.google.common.collect.*;
import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.other.Conditionals;
import common.other.RandUtils;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
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
public class CriticFitterUsingReplayBuffer<V> {

    public static final int N_FEAT = 1;
    public static final int FEATURE_INDEX_SOC = 0;
    public static final int MIN_NOF_POINTS = 2;
    DisCoMemory<V> critic;
    TrainerParameters trainerParameters;

    LinearBatchFitter linearFitter;
    RandUtils<Integer> intRand;

    public CriticFitterUsingReplayBuffer(DisCoMemory<V> critic, TrainerParameters trainerParameters) {
        this.critic = critic;
        this.trainerParameters = trainerParameters;
        this.linearFitter = new LinearBatchFitter(trainerParameters.learningRateReplayBufferCritic());
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
        RealVector weightsAndBias = new ArrayRealVector(critic.readThetas(anyStateWithTimeChosen));
        var batchData = createData(N_FEAT, experiencesAtChosenTime);
        weightsAndBias = linearFitter.fit(weightsAndBias, batchData);
        critic.save(anyStateWithTimeChosen, weightsAndBias.toArray());
    }

    private static int getMostFrequentTime(List<Integer> presentTimes) {
        Multiset<Integer> multiset = HashMultiset.create(presentTimes);
        var maxEntry = multiset.entrySet().stream()
                .max(Ordering.natural().onResultOf(Multiset.Entry::getCount));
        Conditionals.executeIfTrue(maxEntry.orElseThrow().getCount()< MIN_NOF_POINTS, () ->
                log.warning("Few items in time step for fitting"));
        return maxEntry.orElseThrow().getElement();
    }


    Pair<RealMatrix, RealVector> createData(int nFeat, List<ExperienceMultiStep<V>> experiencesAtChosenTime) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, nFeat);
        var yVec = new ArrayRealVector(nPoints);
        int i = FEATURE_INDEX_SOC;
        for (ExperienceMultiStep<V> experience : experiencesAtChosenTime) {
            double[] features = {experience.state().continousFeatures()[FEATURE_INDEX_SOC]};
            double target = experience.isStateFutureTerminalOrNotPresent()
                    ? experience.sumOfRewards()
                    : experience.sumOfRewards() + trainerParameters.gammaPowN()* critic.read(experience.stateFuture());
            xMat.setRow(i, features);
            yVec.setEntry(i, target);
            i++;
        }
        return Pair.create(xMat, yVec);
    }


}
