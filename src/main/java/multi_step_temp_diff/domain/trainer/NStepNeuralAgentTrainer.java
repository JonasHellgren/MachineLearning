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
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 */

@Builder
@Getter
@Setter
public class NStepNeuralAgentTrainer<S> {
    private static final double ALPHA = 0.5;
    private static final int N = 3;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;
    private static final int BATCH_SIZE = 50;
    private static final int NOF_ITERATIONS = 1;
    private static final double PROB_START = 0.9;
    private static final double PROB_END = 0.01;
    public static final double INIT_VALUE = 0d;
    public static final int MAX_BUFFER_SIZE = 1_000;

    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agentNeural;

    @Builder.Default
    double alpha = ALPHA;
    @Builder.Default
    int nofStepsBetweenUpdatedAndBackuped = N;
    @Builder.Default
    int nofEpisodes = NOF_EPIS;
    @Builder.Default
    double probStart = PROB_START;
    @Builder.Default
    double probEnd = PROB_END;

    @NonNull Supplier<StateInterface<S>> startStateSupplier;
    @Builder.Default
    int batchSize = BATCH_SIZE;
    @Builder.Default
    int bufferSizeMax=MAX_BUFFER_SIZE;
    @Builder.Default
    int nofTrainingIterations = NOF_ITERATIONS;
    @Builder.Default
    int maxStepsInEpisode = Integer.MAX_VALUE;

    AgentInfo<S> agentInfo;
    ReplayBufferInterface<S> buffer;

    public void train() {
        agentInfo= new AgentInfo<>(agentNeural);

        NStepTDHelper<S> h = NStepTDHelper.<S>builder()
                .alpha(alpha) //todo remove
                .n(nofStepsBetweenUpdatedAndBackuped)
                .episodeCounter(new Counter(0, nofEpisodes))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
        buffer = ReplayBufferNStep.<S>builder().maxSize(bufferSizeMax).build();

        LogarithmicDecay decayProb = new LogarithmicDecay(probStart, probEnd, nofEpisodes);
        BiPredicate<Integer, Integer> isNotAtTerminationTime = (t, tTerm) -> t < tTerm;
        BiFunction<Integer, Integer, Integer> timeForUpdate = (t, n) -> t - n + 1;
        Predicate<Integer> isPossibleToGetExperience = (tau) -> tau >= 0;
        BiPredicate<Integer, Integer> isAtTimeJustBeforeTermination = (tau, tTerm) -> tau == tTerm - 1;
        Predicate<Counter> isNotToManySteps = (c) ->  c.getCount()< maxStepsInEpisode;

        while (!h.episodeCounter.isExceeded()) {
            StateInterface<S> state = startStateSupplier.get();
            agentNeural.setState(state);

       //     System.out.println("state = " + state);
          //  System.out.println("agentNeural.getState() = " + agentNeural.getState());

           // System.out.println("h.episodeCounter.getCount() = " + h.episodeCounter.getCount());

            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(), h.T), () ->
                        chooseActionStepAndStoreExperience(h, decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(), h.n);
                Conditionals.executeIfTrue(isPossibleToGetExperience.test(h.tau), () ->
                    buffer.addExperience(getExperienceAtTimeTau(h)));
                Conditionals.executeIfTrue(buffer.size() > batchSize, () -> {
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
        for (int i = 0; i < nofTrainingIterations; i++) {
            agentNeural.learn(miniBatch);
        }
    }

    private List<NstepExperience<S>> getMiniBatch(ReplayBufferInterface<S> buffer) {
        List<NstepExperience<S>> miniBatch = buffer.getMiniBatch(batchSize);
        setValuesInExperiencesInMiniBatch(miniBatch);
        return miniBatch;
    }

    private void setValuesInExperiencesInMiniBatch(List<NstepExperience<S>> miniBatch) {
        double discPowNofSteps = Math.pow(agentInfo.getDiscountFactor(), nofStepsBetweenUpdatedAndBackuped);
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
        double sumOfRewards = sumOfRewardsFromTimeToUpdatePlusOne(h);
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
                .value(INIT_VALUE)
                .build();
    }

    private void trackTempDifferenceError(List<NstepExperience<S>> miniBatch) {
        NstepExperience<S> experience=miniBatch.get(RandUtils.getRandomIntNumber(0,miniBatch.size()));
        double valueMemory=agentNeural.readValue(experience.stateToUpdate);
        double valueTarget=experience.value;
        TemporalDifferenceTracker tracker=agentInfo.getTemporalDifferenceTracker();
        tracker.addDifference(Math.abs(valueMemory-valueTarget));
    }


    private void chooseActionStepAndStoreExperience(NStepTDHelper<S> h, LogarithmicDecay scaler) {
        final int action = agentNeural.chooseAction(scaler.calcOut(h.episodeCounter.getCount()));
        StepReturn<S> stepReturn = environment.step(agentNeural.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(), agentNeural.getState());
        agentNeural.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount() + 1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper<S> h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> rewardTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            rewardTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(rewardTerms);
    }


}
