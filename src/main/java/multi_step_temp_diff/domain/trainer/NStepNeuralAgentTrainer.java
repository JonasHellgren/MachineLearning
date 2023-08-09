package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.helpers_common.NStepTDHelper;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import java.util.*;
import java.util.function.Supplier;
import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.helpers_common.NStepTDFunctionsAndPredicates.*;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 */

@Builder
@Getter
@Setter
@Log
public class NStepNeuralAgentTrainer<S> {

    @Builder.Default
    NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.getDefault();
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agentNeural;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;

    AgentInfo<S> agentInfo;
    ReplayBufferInterface<S> buffer;
    NStepTDHelper<S> helper;
    LogarithmicDecay decayProb;

    public void train() {
        agentInfo=new AgentInfo<>(agentNeural);
        helper =  NStepTDHelper.newHelperFromSettings(settings,agentNeural.getAgentSettings());
        buffer = ReplayBufferNStep.newFromMaxSize(settings.maxBufferSize());
        decayProb = NStepTDHelper.newLogDecayFromSettings(settings);

        while (helper.isNofEpisodesNotIsExceeded()) {
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
            log.info("episode = " + helper.getEpisode()+ ", time end = " + helper.getTime());
            helper.increaseEpisode();
            helper.updateSumRewardsTracker();
        }
        log.info("Training finished. Replay buffer size = "+buffer.size());

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
        for (int i = 0; i < settings.nofIterations(); i++) {
            agentNeural.learn(miniBatch);
        }
    }

    private void trackTempDifferenceError(List<NstepExperience<S>> miniBatch) {
        var experience = miniBatch.get(RandUtils.getRandomIntNumber(0, miniBatch.size()));
        double valueMemory = agentNeural.readValue(experience.stateToUpdate);
        double valueTarget = experience.value;
        var tracker = agentInfo.getTemporalDifferenceTracker();
        tracker.addValue(Math.abs(valueMemory - valueTarget));
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



    /***trash
     *
     *
     //     System.out.println("state = " + state);
     //  System.out.println("agentNeural.getState() = " + agentNeural.getState());
     // System.out.println("h.getEpisode() = " + h.getEpisode());

     //   System.out.println("exp.stateToBackupFrom = " + exp.stateToBackupFrom+", value = "+agentNeural.readValue(exp.stateToBackupFrom));

     //  System.out.println("stateToUpdate = " + stateToUpdate+"sumOfRewards = " + sumOfRewards);
     //  stateAheadToBackupFrom.ifPresent(System.out::println);
     */

}
