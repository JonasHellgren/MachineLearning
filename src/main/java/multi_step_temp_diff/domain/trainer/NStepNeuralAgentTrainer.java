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
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.helpers.NStepTDHelper;
import multi_step_temp_diff.domain.agent_parts.TemporalDifferenceTracker;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static common.Conditionals.executeIfTrue;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 */

@Builder
@Getter
@Setter
public class NStepNeuralAgentTrainer<S> {

    @Builder.Default
    NStepNeuralAgentTrainerSettings settings=NStepNeuralAgentTrainerSettings.getDefault();
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agentNeural;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;

    AgentInfo<S> agentInfo;
    ReplayBufferInterface<S> buffer;
    NStepTDHelper<S> h;

    public void train() {
        agentInfo= new AgentInfo<>(agentNeural);

        h = NStepTDHelper.<S>builder()
                .n(settings.nofStepsBetweenUpdatedAndBackuped())
                .episodeCounter(new Counter(0, settings.nofEpis()))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
        buffer = ReplayBufferNStep.<S>builder().maxSize(settings.maxBufferSize()).build();

        var decayProb = new LogarithmicDecay(settings.probStart(), settings.probEnd(), settings.nofEpis());
        BiPredicate<Integer, Integer> isNotAtTerminationTime = (t, tTerm) -> t < tTerm;
        BiFunction<Integer, Integer, Integer> timeForUpdate = (t, n) -> t - n + 1;
        Predicate<Integer> isPossibleToGetExperience = (tau) -> tau >= 0;
        BiPredicate<Integer, Integer> isAtTimeJustBeforeTermination = (tau, tTerm) -> tau == tTerm - 1;
        Predicate<Counter> isNotToManySteps = (c) ->  c.getCount()< settings.maxStepsInEpisode();

        while (!h.episodeCounter.isExceeded()) {
            var state = startStateSupplier.get();
            agentNeural.setState(state);

       //     System.out.println("state = " + state);
          //  System.out.println("agentNeural.getState() = " + agentNeural.getState());

           // System.out.println("h.episodeCounter.getCount() = " + h.episodeCounter.getCount());

            h.reset();
            do {
                executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(), h.T), () ->
                        chooseActionStepAndStoreExperience(decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(), h.n);
                executeIfTrue(isPossibleToGetExperience.test(h.tau), () ->
                    buffer.addExperience(getExperienceAtTimeTau(h)));
                executeIfTrue(buffer.size() > settings.batchSize(), () -> {
                    List<NstepExperience<S>> miniBatch = getMiniBatch(buffer);
                    trainAgentMemoryFromExperiencesInMiniBatch(miniBatch);
                    trackTempDifferenceError(miniBatch);
                });
                h.timeCounter.increase();
            } while (!isAtTimeJustBeforeTermination.test(h.tau, h.T) && isNotToManySteps.test(h.timeCounter));
            h.episodeCounter.increase();
        }
    }

    private void trainAgentMemoryFromExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        for (int i = 0; i < settings.nofIterations(); i++) {
            agentNeural.learn(miniBatch);
        }
    }

    private List<NstepExperience<S>> getMiniBatch(ReplayBufferInterface<S> buffer) {
        var miniBatch = buffer.getMiniBatch(settings.batchSize());
        setValuesInExperiencesInMiniBatch(miniBatch);
        return miniBatch;
    }

    private void setValuesInExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        double discPowNofSteps = Math.pow(agentInfo.getDiscountFactor(), settings.nofStepsBetweenUpdatedAndBackuped());
        for (NstepExperience<S> exp : miniBatch) {

         //   System.out.println("exp.stateToBackupFrom = " + exp.stateToBackupFrom+", value = "+agentNeural.readValue(exp.stateToBackupFrom));
            exp.value = (exp.isBackupStatePresent)
                    ? exp.sumOfRewards + discPowNofSteps * agentNeural.readValue(exp.stateToBackupFrom)
                    : exp.sumOfRewards;
        }
    }

    public ReplayBufferInterface<S> getBuffer() {
        return buffer;
    }

    static BiPredicate<Integer, Integer> isTimeToBackUpFromAtOrBeforeTermination = (t, tTerm) -> t < tTerm; //<=

    private NstepExperience<S> getExperienceAtTimeTau(NStepTDHelper<S> h) {
        double sumOfRewards = sumOfRewardsFromTimeToUpdatePlusOne();
        int tBackUpFrom = h.tau + h.n;
        Optional<StateInterface<S>> stateAheadToBackupFrom = Optional.empty();
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom, h.T)) {
            stateAheadToBackupFrom = Optional.of(h.timeReturnMap.get(h.tau + h.n).newState);
        }

        final StateInterface<S> stateToUpdate = h.statesMap.get(h.tau);
      //  System.out.println("stateToUpdate = " + stateToUpdate+"sumOfRewards = " + sumOfRewards);
      //  stateAheadToBackupFrom.ifPresent(System.out::println);


        return NstepExperience.<S>builder()
                .stateToUpdate(stateToUpdate).sumOfRewards(sumOfRewards)
                .stateToBackupFrom(stateAheadToBackupFrom.orElse(stateToUpdate))  //todo good use stateToUpdate?
                .isBackupStatePresent(stateAheadToBackupFrom.isPresent())
                .value(settings.initValue())
                .build();
    }

    private void trackTempDifferenceError(List<NstepExperience<S>> miniBatch) {
        var experience=miniBatch.get(RandUtils.getRandomIntNumber(0,miniBatch.size()));
        double valueMemory=agentNeural.readValue(experience.stateToUpdate);
        double valueTarget=experience.value;
        var tracker=agentInfo.getTemporalDifferenceTracker();
        tracker.addDifference(Math.abs(valueMemory-valueTarget));
    }


    private void chooseActionStepAndStoreExperience(LogarithmicDecay scaler) {
        final int action = agentNeural.chooseAction(scaler.calcOut(h.episodeCounter.getCount()));
        var stepReturn = environment.step(agentNeural.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(), agentNeural.getState());
        agentNeural.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount() + 1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne() {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> rewardTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            rewardTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(rewardTerms);
    }


}
