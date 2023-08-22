package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.helpers_common.NStepTDHelper;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.helpers_common.NStepTDFunctionsAndPredicates.*;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 * <p>
 * Thanks to Callable multiple trainers can operate in parallel
 *
 * It is important to set nofEpis and maxTrainingTimeInMilliS in an adequate way. If maxTrainingTimeInMilliS is to small
 * nofEpis will be far from its maximum. The consequence is that decayProb (handling random action) will not perform as
 * expected. A log warning will appear if this is the case.
 */

@Builder
@Getter
@Setter
@Log
public class NStepNeuralAgentTrainer<S> implements Callable<NStepNeuralAgentTrainer<S>> {

    public static final double SEC2MIN = 1d / 60, MS2SEC = 1d / 1000;
    public static final int MAX_SIZE_BUFFER = 100_000;
    public static final double FRACTION_MINI_BATCH_FOR_TRACKING = 1.0;

    @Builder.Default
    NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.getDefault();
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agentNeural;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;
    @Builder.Default
    ReplayBufferInterface<S> buffer = ReplayBufferNStepUniform.newFromMaxSize(MAX_SIZE_BUFFER);

    AgentInfo<S> agentInfo;
    NStepTDHelper<S> helper;
    LogarithmicDecay decayProb;

    public void train() {
        agentInfo = new AgentInfo<>(agentNeural);
        helper = NStepTDHelper.newHelperFromSettings(settings, agentNeural.getAgentSettings());
        decayProb = NStepTDHelper.newLogDecayFromSettings(settings);
        CpuTimer timer = new CpuTimer(settings.maxTrainingTimeInMilliS());
        NetworkMemoryInterface<S> memoryTarget = agentNeural.getMemory().copy();

        while (noEpisodesOrTimeNotIsExceeded(timer)) {
            agentNeural.setState(startStateSupplier.get());
            helper.reset();
            do {
                int timeStep = helper.getTime();
                executeIfTrue(isNotAtTerminationTime.test(timeStep, helper.T), () -> {
                    StateInterface<S> stateBeforeUpdate = agentNeural.getState();
                    var stepReturn = helper.chooseActionStepAndUpdateAgent(agentNeural, environment, decayProb);
                    helper.storeExperience(stateBeforeUpdate, stepReturn);
                });
                helper.tau = timeForUpdate.apply(timeStep, settings.nofStepsBetweenUpdatedAndBackuped());
                executeIfTrue(isPossibleToGetExperience.test(helper.tau), () ->
                        buffer.addExperience(getExperienceAtTimeTau(),timer));
                executeIfTrue(isEnoughItemsInBuffer.test(buffer.size(), settings), () -> {
                    var miniBatch = buffer.getMiniBatch(settings.batchSize());
                    setValuesInExperiencesInMiniBatch(miniBatch, memoryTarget);
                    trainAgentMemoryFromExperiencesInMiniBatch(miniBatch);
                    trackTempDifferenceError(miniBatch);
                    maybeUpdateTargetMemory(memoryTarget);
                });
                helper.increaseTime();
            } while (isTimeForUpdateOkAndNotToLargeTime());
            logEpisode();
            helper.increaseEpisode();
            helper.updateSumRewardsTracker();
        }
        logFinishedTraining(timer);
    }

    private void maybeUpdateTargetMemory(NetworkMemoryInterface<S> memoryTarget) {
        executeIfTrue(helper.getTime() % settings.nofStepsBetweenTargetMemoryUpdate() == 0, () ->
                memoryTarget.copyWeights(agentNeural.getMemory()));
    }

    private boolean noEpisodesOrTimeNotIsExceeded(CpuTimer timer) {
        return helper.isNofEpisodesNotIsExceeded() && !timer.isTimeExceeded();
    }

    private void logEpisode() {
        executeIfTrue(helper.getEpisode() % settings.nofEpisodesBetweenLogs() == 0, () ->
                log.info("episode = " + helper.getEpisode() + ", time end = " + helper.getTime() + ". SumRewards = "
                        + helper.getSumRewards()));
    }

    private void logFinishedTraining(CpuTimer timer) {
        log.info("Training finished. Replay buffer size = " + buffer.size() + ". Time needed in minutes = " +
                timer.absoluteProgressInMillis() * MS2SEC * SEC2MIN);
        executeIfTrue(timer.isTimeExceeded(), () -> log.warning("Time exceeded before nof episodes"));
    }

    private boolean isTimeForUpdateOkAndNotToLargeTime() {
        return !isAtTimeJustBeforeTermination.test(helper.tau, helper.T) &&
                isNotToManySteps.test(helper.timeCounter, settings.maxStepsInEpisode());
    }

    private void trainAgentMemoryFromExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
            agentNeural.learn(miniBatch);
    }

    public void trackTempDifferenceError(List<NstepExperience<S>> miniBatch) {
        List<Double> errors=new ArrayList<>();
        int nofItems= (int) Math.max(1,(settings.fractionMiniBatchForTracking() * miniBatch.size()));  //at least one
        for (int i = 0; i < nofItems; i++) {
            var experience = miniBatch.get(MathUtils.clip(i, 0, miniBatch.size() - 1));
            experience.valueMemory = agentNeural.readValue(experience.stateToUpdate);
            experience.tdError= Math.abs(experience.valueMemory - experience.value);
            errors.add(experience.tdError);
        }
        var tracker = agentInfo.getTemporalDifferenceTracker();
        tracker.addValue(ListUtils.findAverage(errors).orElseThrow());
    }


    private void setValuesInExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch, NetworkMemoryInterface<S> memory) {
        double discount = helper.getDiscount();
        for (NstepExperience<S> exp : miniBatch) {
            exp.value = (exp.isBackupStatePresent)
                    ? exp.sumOfRewards + discount * memory.read(exp.stateToBackupFrom)
                    : exp.sumOfRewards;
        }
    }

    private NstepExperience<S> getExperienceAtTimeTau() {
        double sumOfRewards = helper.sumOfRewardsFromTimeToUpdatePlusOne();
        int timeBackUpFrom = helper.getTimeBackUpFrom();
        Optional<StateInterface<S>> stateAheadToBackupFrom =
                (isTimeToBackUpFromAtOrBeforeTermination.test(timeBackUpFrom, helper.T))
                        ? Optional.of(helper.stateAheadToBackupFrom())
                        : Optional.empty();

        StateInterface<S> stateToUpdate = helper.getStateToUpdate();
        return NstepExperience.<S>builder()
                .stateToUpdate(stateToUpdate).sumOfRewards(sumOfRewards)
                .stateToBackupFrom(stateAheadToBackupFrom.orElse(stateToUpdate))
                .isBackupStatePresent(stateAheadToBackupFrom.isPresent())
                .value(settings.initValue())
                .build();
    }

    @Override
    public NStepNeuralAgentTrainer<S> call() {
        train();
        return this;
    }


}
