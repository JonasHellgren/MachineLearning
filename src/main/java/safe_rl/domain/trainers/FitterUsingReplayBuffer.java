package safe_rl.domain.trainers;

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
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.EpisodeInfoMultiStep;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

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
    LinearBatchFitter fitterCritic, fitterActorMean, fitterActorStd;

    public FitterUsingReplayBuffer(AgentACDiscoI<V> agent,
                                   TrainerParameters trainerParameters,
                                   int indexFeature) {
        this.agent = agent;
        this.parameters = trainerParameters;
        this.indexFeature=indexFeature;
        this.fitterCritic = new LinearBatchFitter(parameters.learningRateReplayBufferCritic());
        this.fitterActorMean = new LinearBatchFitter(parameters.learningRateReplayBufferActor());
        this.fitterActorStd = new LinearBatchFitter(parameters.learningRateReplayBufferActorStd());

    }

    public void fit(ReplayBufferMultiStepExp<V> buffer) {
        var experiences = getExperiencesAtRandomTime(buffer);
        var stateAtTime = experiences.stream().findAny().map(e -> e.state()).orElseThrow();
        fitCritic(experiences, stateAtTime);
        fitActorMean(experiences, stateAtTime);
       fitActorStd(experiences,stateAtTime);
    }


    private void fitCritic(List<ExperienceMultiStep<V>> experiences, StateI<V> stateAtTime) {
        var critic=agent.getCritic();
        RealVector paramsCritic = new ArrayRealVector(critic.readThetas(stateAtTime));
        var batchData = createData(experiences,targetFcn,false);
        paramsCritic = fitterCritic.fit(paramsCritic, batchData);
        critic.save(stateAtTime, paramsCritic.toArray());
    }


    private void fitActorMean(List<ExperienceMultiStep<V>> experiences, StateI<V> stateAtTime) {
        var actorMean=agent.getActorMean();
        RealVector paramsActor = new ArrayRealVector(actorMean.readThetas(stateAtTime));
        var batchDataActor = createData(experiences, loss,true);
        paramsActor = fitterActorMean.fitFromErrors(paramsActor, batchDataActor);
        actorMean.save(stateAtTime, paramsActor.toArray());
    }

    private void fitActorStd(List<ExperienceMultiStep<V>> experiences, StateI<V> stateAtTime) {
        var actorMean=agent.getActorLogStd();
        RealVector paramsActor = new ArrayRealVector(actorMean.readThetas(stateAtTime));
        var batchDataActor = createData(experiences,lossAdvClipper,false);
        paramsActor = fitterActorStd.fitFromErrors(paramsActor, batchDataActor);
        actorMean.save(stateAtTime, paramsActor.toArray());
    }

    private  List<ExperienceMultiStep<V>> getExperiencesAtRandomTime(ReplayBufferMultiStepExp<V> buffer) {
        var batch = buffer.getMiniBatch(parameters.miniBatchSize());
        var ei = new EpisodeInfoMultiStep<>(batch);
        var presentTimes = ei.getValuesOfSpecificDiscreteFeature(indexFeature)
                .stream().toList();
        int timeChosen = getMostFrequentTime(presentTimes);
        return ei.getExperiencesWithDiscreteFeatureValue(timeChosen, indexFeature);
    }

    ToDoubleBiFunction<ExperienceMultiStep<V>,Boolean> targetFcn=(exp,isMean) ->
            exp.isStateFutureTerminalOrNotPresent()
            ? exp.sumOfRewards()
            : exp.sumOfRewards() + parameters.gammaPowN()* agent.readCritic(exp.stateFuture());

    ToDoubleBiFunction<ExperienceMultiStep<V>,Boolean> loss = (exp,isMean) -> {
        var grad = agent.gradientMeanAndStd(exp.state(), exp.actionApplied());
        double advantage=valueTarget(exp)-agent.readCritic(exp.state());
        double gradMax= parameters.gradMeanActorMaxBufferFitting();
        double gradVal=isMean?grad.getFirst():grad.getSecond();
        return clip(-gradVal*advantage,-gradMax, gradMax);  //MINUS <=> maximize loss
    };

    /**
     * This loss with alt clipper is slower hence only used for std (more motivated)
     */

    ToDoubleBiFunction<ExperienceMultiStep<V>,Boolean> lossAdvClipper = (exp,isMean) -> {
        var grad = agent.gradientMeanAndStd(exp.state(), exp.actionApplied());
        double advantage=valueTarget(exp)-agent.readCritic(exp.state());
        double gradVal=isMean?grad.getFirst():grad.getSecond();
        SafeGradientClipper clipper=isMean?agent.getMeanGradClipper():agent.getStdGradClipper();
        double actorMemValue=isMean
                ? agent.getActorMean().read(exp.state())
                : Math.exp(agent.getActorLogStd().read(exp.state()));
        return -clipper.modify(gradVal*advantage,actorMemValue);
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
                                            ToDoubleBiFunction<ExperienceMultiStep<V>,Boolean> entry,
                                            boolean isMean) {
        int nPoints = experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, N_FEAT);
        var yVec = new ArrayRealVector(nPoints);
        int i = 0;
        for (ExperienceMultiStep<V> experience : experiencesAtChosenTime) {
            xMat.setRow(i, getFeatures(experience));
            yVec.setEntry(i, entry.applyAsDouble(experience,isMean));
            i++;
        }
        return Pair.create(xMat, yVec);
    }

    double[] getFeatures(ExperienceMultiStep<V> experience) {
        return new double[]{experience.state().continuousFeatures()[indexFeature]};
    }

    private double valueTarget(ExperienceMultiStep<V> experience) {
        return experience.isStateFutureTerminalOrNotPresent()
                ? experience.sumOfRewards()
                : experience.sumOfRewards() + parameters.gammaPowN()* agent.readCritic(experience.stateFuture());
    }





}
