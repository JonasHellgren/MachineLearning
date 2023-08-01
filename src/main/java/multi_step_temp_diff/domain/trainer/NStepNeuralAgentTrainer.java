package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.helpers.NStepTDHelper;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.Supplier;

import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.helpers.NStepTDFunctionsAndPredicates.*;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 */

@Builder
@Getter
@Setter
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
        agentInfo = new AgentInfo<>(agentNeural);
        helper = NStepTDHelper.newFromNofEpisodesAndNofStepsBetween(settings.nofEpis(),settings.nofStepsBetweenUpdatedAndBackuped());
        buffer = ReplayBufferNStep.newFromMaxSize(settings.maxBufferSize());
        decayProb = new LogarithmicDecay(settings.probStart(), settings.probEnd(), settings.nofEpis());

        while (helper.isNofEpisodesNotIsExceeded()) {
            agentNeural.setState(startStateSupplier.get());
            helper.reset();
            do {
                int timeStep = helper.timeCounter.getCount();
                executeIfTrue(isNotAtTerminationTime.test(timeStep, helper.T), () ->
                        chooseActionStepAndStoreExperience());
                helper.tau = timeForUpdate.apply(timeStep, settings);
                executeIfTrue(isPossibleToGetExperience.test(helper.tau), () ->
                        buffer.addExperience(getExperienceAtTimeTau()));
                executeIfTrue(isEnoughItemsInBuffer.test(buffer.size(), settings), () -> {
                    var miniBatch = getMiniBatch(buffer);
                    trainAgentMemoryFromExperiencesInMiniBatch(miniBatch);
                    trackTempDifferenceError(miniBatch);
                });
                helper.timeCounter.increase();
            } while (isTimeForUpdateOkAndNotToLargeTime());
            helper.episodeCounter.increase();
        }
    }

    private boolean isTimeForUpdateOkAndNotToLargeTime() {
        return !isAtTimeJustBeforeTermination.test(helper.tau, helper.T) &&
                isNotToManySteps.test(helper.timeCounter, settings.maxStepsInEpisode());
    }


    private void chooseActionStepAndStoreExperience() {
        final int action = agentNeural.chooseAction(decayProb.calcOut(helper.episodeCounter.getCount()));
        var stepReturn = environment.step(agentNeural.getState(), action);
        helper.statesMap.put(helper.timeCounter.getCount(), agentNeural.getState());
        agentNeural.updateState(stepReturn);
        helper.timeReturnMap.put(helper.timeCounter.getCount() + 1, stepReturn);
        helper.T = (stepReturn.isNewStateTerminal) ? helper.timeCounter.getCount() + 1 : helper.T;
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
        tracker.addDifference(Math.abs(valueMemory - valueTarget));
    }


    private void setValuesInExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        double discPowNofSteps = Math.pow(agentInfo.getDiscountFactor(), settings.nofStepsBetweenUpdatedAndBackuped());
        for (NstepExperience<S> exp : miniBatch) {
            exp.value = (exp.isBackupStatePresent)
                    ? exp.sumOfRewards + discPowNofSteps * agentNeural.readValue(exp.stateToBackupFrom)
                    : exp.sumOfRewards;
        }
    }

    private NstepExperience<S> getExperienceAtTimeTau() {
        double sumOfRewards = sumOfRewardsFromTimeToUpdatePlusOne();
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


    private double sumOfRewardsFromTimeToUpdatePlusOne() {
        Pair<Integer, Integer> iMinMax = new Pair<>(helper.tau + 1,
                Math.min(helper.tau + settings.nofStepsBetweenUpdatedAndBackuped(), helper.T));
        List<Double> rewardTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            rewardTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - helper.tau - 1) * helper.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(rewardTerms);
    }


    /***trash
     *
     *
     //     System.out.println("state = " + state);
     //  System.out.println("agentNeural.getState() = " + agentNeural.getState());
     // System.out.println("h.episodeCounter.getCount() = " + h.episodeCounter.getCount());

     //   System.out.println("exp.stateToBackupFrom = " + exp.stateToBackupFrom+", value = "+agentNeural.readValue(exp.stateToBackupFrom));

     //  System.out.println("stateToUpdate = " + stateToUpdate+"sumOfRewards = " + sumOfRewards);
     //  stateAheadToBackupFrom.ifPresent(System.out::println);
     */

}
