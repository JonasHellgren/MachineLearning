package safe_rl.domain.trainers;
import com.google.common.collect.*;
import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.other.RandUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.domain.memories.ReplayBuffer;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.EpisodeInfo;

import java.util.List;
import java.util.Optional;

public class CriticFitterUsingReplayBuffer<V> {

    public static final int N_FEAT = 1;
    public static final int MIN_NOF_SAMPLES = 1;
    DisCoMemory<V> critic;
    TrainerParameters trainerParameters;

    LinearBatchFitter linearFitter;
    RandUtils<Integer> intRand;

    public CriticFitterUsingReplayBuffer(DisCoMemory<V>  critic, TrainerParameters trainerParameters) {
        this.critic = critic;
        this.trainerParameters = trainerParameters;
        this.linearFitter=new LinearBatchFitter(trainerParameters.learningRateNonNeuralActor()); //correct?
        this.intRand=new RandUtils<>();
    }

    public void fit(ReplayBuffer<V> buffer ) {

        var batch=buffer.getMiniBatch(trainerParameters.miniBatchSize());
        var ei=new EpisodeInfo<>(batch);
        List<Integer> presentTimes = ei.getValuesOfSpecificDiscreteFeature(0).stream().toList();
        int timeChosen = getMostFrequentTime(presentTimes);

        batch.forEach(System.out::println);
        System.out.println("presentTimes = " + presentTimes);
        System.out.println("timeChosen = " + timeChosen);
        List<Experience<V>> experiencesAtChosenTime = ei.getExperiencesWithDiscreteFeatureValue(timeChosen, 0);
        StateI<V> stateWithTimeChosen=experiencesAtChosenTime.stream().findAny().map(e -> e.state()).orElseThrow();
        RealVector weightsAndBias=new ArrayRealVector(critic.readThetas(stateWithTimeChosen));

        experiencesAtChosenTime.forEach(System.out::println);
        System.out.println("stateWithTimeChosen = " + stateWithTimeChosen);

        Pair<RealMatrix, RealVector> batchData=createData(N_FEAT,experiencesAtChosenTime);

        System.out.println("xMat = " + batchData.getFirst());
        System.out.println("yPred = " + batchData.getSecond());

        weightsAndBias=linearFitter.fit(weightsAndBias,batchData);
        critic.save(stateWithTimeChosen,weightsAndBias.toArray());
    }

    private static int getMostFrequentTime(List<Integer> presentTimes) {
        Multiset<Integer> multiset = HashMultiset.create(presentTimes);
        var maxEntry = multiset.entrySet().stream()
                .max(Ordering.natural().onResultOf(Multiset.Entry::getCount));
        return maxEntry.orElseThrow().getElement();
    }


    Pair<RealMatrix, RealVector> createData(int nFeat, List<Experience<V>> experiencesAtChosenTime) {
        int nPoints=experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, nFeat);
        var yVec = new ArrayRealVector(nPoints);
        int i=0;
        for (Experience<V> experience:experiencesAtChosenTime) {
            double[] features = {experience.state().continousFeatures()[0]};
            double target = experience.rewardApplied() +
                    trainerParameters.gamma()*critic.read(experience.stateNextApplied());
            xMat.setRow(i, features);
            yVec.setEntry(i, target);
            i++;
        }
        return Pair.create(xMat, yVec);
    }



}
