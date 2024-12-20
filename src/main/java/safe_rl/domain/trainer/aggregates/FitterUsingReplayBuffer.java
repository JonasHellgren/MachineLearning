package safe_rl.domain.trainer.aggregates;

import com.google.common.collect.*;
import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.math.SafeGradientClipper;
import common.other.Conditionals;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.mediators.MediatorMultiStepI;
import safe_rl.domain.trainer.value_objects.MultiStepResultItem;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.trainer.helpers.EpisodeInfoMultiStep;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

import static common.math.MathUtils.clip;

/***
 * This class extracts mini batch for any time, weights in critic memory at this time is fitted
 * Using linear regression from multiple data points stabilizes fitting
 */

@Log
public class FitterUsingReplayBuffer<V> {

    MediatorMultiStepI<V> mediator;

    public static final int N_FEAT = 1;
    public static final int MIN_NOF_POINTS = 2;
    int indexFeature;
    LinearBatchFitter fitterCritic, fitterActorMean, fitterActorStd;


    public FitterUsingReplayBuffer(MediatorMultiStepI<V> mediator,
                                   int indexFeature) {
        this.mediator=mediator;
        this.indexFeature = indexFeature;
        var p = getParameters();
        this.fitterCritic = new LinearBatchFitter(p.learningRateReplayBufferCritic());
        this.fitterActorMean = new LinearBatchFitter(p.learningRateReplayBufferActor());
        this.fitterActorStd = new LinearBatchFitter(p.learningRateReplayBufferActorStd());
    }

    public void fit(ReplayBufferMultiStepExp<V> buffer) {
        var experiences = getExperiencesAtRandomTime(buffer);
        var stateAtTime = experiences.stream().findAny().map(e -> e.state()).orElseThrow();
        fitCritic(experiences, stateAtTime);
        fitActorMean(experiences, stateAtTime);
        fitActorStd(experiences, stateAtTime);
    }


    private void fitCritic(List<MultiStepResultItem<V>> experiences, StateI<V> stateAtTime) {
        var critic = getAgent().getCritic();
        RealVector paramsCritic = new ArrayRealVector(critic.readThetas(stateAtTime));
        var batchData = createData(experiences, targetFcn, false);
        paramsCritic = fitterCritic.fit(paramsCritic, batchData);
        critic.save(stateAtTime, paramsCritic.toArray());
    }


    private void fitActorMean(List<MultiStepResultItem<V>> experiences, StateI<V> stateAtTime) {
        var actorMean = getAgent().getActorMean();
        RealVector paramsActor = new ArrayRealVector(actorMean.readThetas(stateAtTime));
        var batchDataActor = createData(experiences, loss, true);
        paramsActor = fitterActorMean.fitFromErrors(paramsActor, batchDataActor);
        actorMean.save(stateAtTime, paramsActor.toArray());
    }

    private void fitActorStd(List<MultiStepResultItem<V>> experiences, StateI<V> stateAtTime) {
        var actorMean = getAgent().getActorLogStd();
        RealVector paramsActor = new ArrayRealVector(actorMean.readThetas(stateAtTime));
        var batchDataActor = createData(experiences, lossAdvClipper, false);
        paramsActor = fitterActorStd.fitFromErrors(paramsActor, batchDataActor);
        actorMean.save(stateAtTime, paramsActor.toArray());
    }

    private List<MultiStepResultItem<V>> getExperiencesAtRandomTime(ReplayBufferMultiStepExp<V> buffer) {
        var batch = buffer.getMiniBatch(getParameters().miniBatchSize());
        var ei = new EpisodeInfoMultiStep<>(batch);
        var presentTimes = ei.getValuesOfSpecificDiscreteFeature(indexFeature)
                .stream().toList();
        int timeChosen = getMostFrequentTime(presentTimes);
        return ei.getExperiencesWithDiscreteFeatureValue(timeChosen, indexFeature);
    }

    ToDoubleBiFunction<MultiStepResultItem<V>, Boolean> targetFcn = (exp, isMean) ->
            exp.isStateFutureTerminalOrNotPresent()
                    ? exp.sumOfRewards()
                    : exp.sumOfRewards() + getParameters().gammaPowN() * getAgent().readCritic(exp.stateFuture());

    ToDoubleBiFunction<MultiStepResultItem<V>, Boolean> loss = (exp, isMean) -> {
        var agent = getAgent();
        var grad = agent.gradientMeanAndStd(exp.state(), exp.actionApplied());
        double advantage = valueTarget(exp) - agent.readCritic(exp.state());
        //double gradMax = parameters.gradActorMax();
        double gradMax = agent.getParameters().gradMaxActor();
        double gradVal = isMean ? grad.getFirst() : grad.getSecond();
        return clip(-gradVal * advantage, -gradMax, gradMax);  //MINUS <=> maximize loss
    };

    /**
     * This loss with alt clipper is slower hence only used for std (more motivated)
     */

    ToDoubleBiFunction<MultiStepResultItem<V>, Boolean> lossAdvClipper = (exp, isMean) -> {
        var agent = getAgent();
        var grad = agent.gradientMeanAndStd(exp.state(), exp.actionApplied());
        double advantage = valueTarget(exp) - agent.readCritic(exp.state());
        double gradVal = isMean ? grad.getFirst() : grad.getSecond();
        SafeGradientClipper clipper = isMean ? agent.getMeanGradClipper() : agent.getStdGradClipper();
        double actorMemValue = isMean
                ? agent.getActorMean().read(exp.state())
                : Math.exp(agent.getActorLogStd().read(exp.state()));
        return -clipper.modify(gradVal * advantage, actorMemValue);
    };


    private static int getMostFrequentTime(List<Integer> presentTimes) {
        Multiset<Integer> multiset = HashMultiset.create(presentTimes);
        var maxEntry = multiset.entrySet().stream()
                .max(Ordering.natural().onResultOf(Multiset.Entry::getCount));
        Conditionals.executeIfTrue(maxEntry.orElseThrow().getCount() < MIN_NOF_POINTS, () ->
                log.fine("Few items in time step for fitting"));
        return maxEntry.orElseThrow().getElement();
    }

    Pair<RealMatrix, RealVector> createData(List<MultiStepResultItem<V>> experiencesAtChosenTime,
                                            ToDoubleBiFunction<MultiStepResultItem<V>, Boolean> entry,
                                            boolean isMean) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, N_FEAT);
        var yVec = new ArrayRealVector(nPoints);
        int i = 0;
        for (MultiStepResultItem<V> experience : experiencesAtChosenTime) {
            xMat.setRow(i, getFeatures(experience));
            yVec.setEntry(i, entry.applyAsDouble(experience, isMean));
            i++;
        }
        return Pair.create(xMat, yVec);
    }

    double[] getFeatures(MultiStepResultItem<V> experience) {
        return new double[]{experience.state().continuousFeatures()[indexFeature]};
    }

    private double valueTarget(MultiStepResultItem<V> experience) {
        var agent = getAgent();
        var p=getParameters();
        return experience.isStateFutureTerminalOrNotPresent()
                ? experience.sumOfRewards()
                : experience.sumOfRewards() + p.gammaPowN() * agent.readCritic(experience.stateFuture());
    }


    private  TrainerParameters getParameters() {
        return mediator.getParameters();
    }

    private AgentACDiscoI<V> getAgent() {
        return mediator.getExternal().agent();
    }


}
