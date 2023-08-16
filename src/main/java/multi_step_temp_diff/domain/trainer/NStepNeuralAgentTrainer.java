package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
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
 *
 * Thanks to Callable multiple trainers can operate in parallel
 */

@Builder
@Getter
@Setter
@Log
public class NStepNeuralAgentTrainer<S> implements Callable<NStepNeuralAgentTrainer<S>> {

    public static final double SEC2MIN = 1d / 60, MS2SEC = 1d/1000;
    public static final int MAX_SIZE_BUFFER = 100_000;
    @Builder.Default
    NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.getDefault();
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agentNeural;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;
    @Builder.Default
    ReplayBufferInterface<S> buffer=ReplayBufferNStepUniform.newFromMaxSize(MAX_SIZE_BUFFER);

    AgentInfo<S> agentInfo;
    NStepTDHelper<S> helper;
    LogarithmicDecay decayProb;

    public void train() {
        agentInfo=new AgentInfo<>(agentNeural);
        helper =  NStepTDHelper.newHelperFromSettings(settings,agentNeural.getAgentSettings());
        decayProb = NStepTDHelper.newLogDecayFromSettings(settings);
        CpuTimer timer=new CpuTimer(settings.maxTrainingTimeInMilliS());

        while (nofEpisodesOrTimeNotIsExceede(timer)) {
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
                        buffer.addExperience(getExperienceAtTimeTau()));
                executeIfTrue(isEnoughItemsInBuffer.test(buffer.size(), settings), () -> {
                    var miniBatch = getMiniBatch(buffer);
                    trainAgentMemoryFromExperiencesInMiniBatch(miniBatch);
                    trackTempDifferenceError(miniBatch);
                });
                helper.increaseTime();
            } while (isTimeForUpdateOkAndNotToLargeTime());

         //   helper.statesMap.keySet().forEach( t -> System.out.println(helper.statesMap.get(t)));

            logEpisode();
            helper.increaseEpisode();
            helper.updateSumRewardsTracker();
        }
        logFinishedTraining(timer);

        //  System.out.println("buffer = " + buffer);

    }

    private boolean nofEpisodesOrTimeNotIsExceede(CpuTimer timer) {
        return helper.isNofEpisodesNotIsExceeded() && !timer.isTimeExceeded();
    }

    private void logEpisode() {
        executeIfTrue(helper.getEpisode() % settings.nofEpisodesBetweenLogs()==0, () ->
        log.info("episode = " + helper.getEpisode()+ ", time end = " + helper.getTime()+". SumRewards = "
                +helper.getSumRewards()));
    }

    private void logFinishedTraining(CpuTimer timer) {
        log.info("Training finished. Replay buffer size = "+buffer.size()+". Time needed in minutes = "+
                timer.absoluteProgressInMillis() * MS2SEC * SEC2MIN);
    }

    private boolean isTimeForUpdateOkAndNotToLargeTime() {
        return !isAtTimeJustBeforeTermination.test(helper.tau, helper.T) &&
                isNotToManySteps.test(helper.timeCounter, settings.maxStepsInEpisode());
    }

    private List<NstepExperience<S>> getMiniBatch(ReplayBufferInterface<S> buffer) {
        var miniBatch = buffer.getMiniBatch(settings.batchSize());
        setValuesInExperiencesInMiniBatch(miniBatch);
        return miniBatch;
    }

    private void trainAgentMemoryFromExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        for (int i = 0; i < settings.nofIterations(); i++) { //todo remove
            agentNeural.learn(miniBatch);
        }
    }

    private void trackTempDifferenceError(List<NstepExperience<S>> miniBatch) {
        var experience = miniBatch.get(RandUtils.getRandomIntNumber(0, miniBatch.size()));
        double valueMemory = agentNeural.readValue(experience.stateToUpdate);
        double valueTarget = experience.value;
        double tdError = Math.abs(valueMemory - valueTarget);
        experience.tdError=tdError;
        var tracker = agentInfo.getTemporalDifferenceTracker();
        tracker.addValue(tdError);
    }


    private void setValuesInExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        double discount = helper.getDiscount();
        for (NstepExperience<S> exp : miniBatch) {
            exp.value = (exp.isBackupStatePresent)
                    ? exp.sumOfRewards + discount * agentNeural.readValue(exp.stateToBackupFrom)
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
