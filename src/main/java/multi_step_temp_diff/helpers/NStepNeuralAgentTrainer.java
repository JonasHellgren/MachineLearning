package multi_step_temp_diff.helpers;

import common.Conditionals;
import common.Counter;
import common.ListUtils;
import common.LogarithmicDecay;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.ReplayBufferInterface;
import multi_step_temp_diff.models.*;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Inspired by DQN - https://stackoverflow.com/questions/39848984/what-is-phi-in-deep-q-learning-algorithm
 */

@Builder
@Getter
@Setter
public class NStepNeuralAgentTrainer {
    private static final double ALPHA = 0.5;
    private static final int N = 3;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;
    private static final int BATCH_SIZE = 50;
    private static final int NOF_ITERATIONS = 1;
    private static final double PROB_START = 0.9;
    private static final double PROB_END = 0.01;

    @NonNull EnvironmentInterface environment;
    @NonNull AgentNeuralInterface agentNeural;

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
    @Builder.Default
    int startState = START_STATE;
    @Builder.Default
    int batchSize = BATCH_SIZE;
    @Builder.Default
    int nofTrainingIterations = NOF_ITERATIONS;

    AgentInfo agentInfo;
    ReplayBufferInterface buffer;

    public void train() {
        agentInfo=new AgentInfo(agentNeural);

        NStepTDHelper h = NStepTDHelper.builder()
                .alpha(alpha).n(nofStepsBetweenUpdatedAndBackuped)
                .episodeCounter(new Counter(0, nofEpisodes))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
        buffer = ReplayBufferNStep.newDefault();

        LogarithmicDecay decayProb = new LogarithmicDecay(probStart, probEnd, nofEpisodes);
        BiPredicate<Integer, Integer> isNotAtTerminationTime = (t, tTerm) -> t < tTerm;
        BiFunction<Integer, Integer, Integer> timeForUpdate = (t, n) -> t - n + 1;
        Predicate<Integer> isPossibleToGetExperience = (tau) -> tau >= 0;
        BiPredicate<Integer, Integer> isAtTimeJustBeforeTermination = (tau, tTerm) -> tau == tTerm - 1;


        while (!h.episodeCounter.isExceeded()) {
            agentNeural.setState(startState);
            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(), h.T), () ->
                        chooseActionStepAndStoreExperience(h, decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(), h.n);
                Conditionals.executeIfTrue(isPossibleToGetExperience.test(h.tau), () ->
                        buffer.addExperience(getExperienceAtTimeTau(h)));
                Conditionals.executeIfTrue(buffer.size() > batchSize, () -> {
                    List<NstepExperience> miniBatch = getMiniBatch(buffer);
                    trainAgentMemoryFromExperiencesInMiniBatch(miniBatch);
                });
                h.timeCounter.increase();
            } while (!isAtTimeJustBeforeTermination.test(h.tau, h.T));
            h.episodeCounter.increase();
        }
    }

    private void trainAgentMemoryFromExperiencesInMiniBatch(List<NstepExperience> miniBatch) {
        for (int i = 0; i < nofTrainingIterations; i++) {
            agentNeural.learn(miniBatch);
        }
    }

    private List<NstepExperience> getMiniBatch(ReplayBufferInterface buffer) {
        List<NstepExperience> miniBatch = buffer.getMiniBatch(batchSize);
        setValuesInExperiencesInMiniBatch(miniBatch);
        return miniBatch;
    }

    private void setValuesInExperiencesInMiniBatch(List<NstepExperience> miniBatch) {
        double discPowNofSteps = Math.pow(agentInfo.getDiscountFactor(), nofStepsBetweenUpdatedAndBackuped);
        for (NstepExperience exp : miniBatch) {
            exp.value = (exp.isBackupStatePresent)
                    ? exp.sumOfRewards + discPowNofSteps * agentNeural.readValue(exp.stateToBackupFrom)
                    : exp.sumOfRewards;
        }
    }

    public ReplayBufferInterface getBuffer() {
        return buffer;
    }

    static BiPredicate<Integer, Integer> isTimeToBackUpFromAtOrBeforeTermination = (t, tTerm) -> t < tTerm; //<=

    private NstepExperience getExperienceAtTimeTau(NStepTDHelper h) {
        double sumOfRewards = sumOfRewardsFromTimeToUpdatePlusOne(h);
        int tBackUpFrom = h.tau + h.n;
        Optional<Integer> stateAheadToBackupFrom = Optional.empty();
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom, h.T)) {
            stateAheadToBackupFrom = Optional.of(h.timeReturnMap.get(h.tau + h.n).newState);
        }

        final int stateToUpdate = h.statesMap.get(h.tau);
        return NstepExperience.builder()
                .stateToUpdate(stateToUpdate).sumOfRewards(sumOfRewards)
                .stateToBackupFrom(stateAheadToBackupFrom.orElse(NstepExperience.STATE_IF_NOT_PRESENT))
                .isBackupStatePresent(stateAheadToBackupFrom.isPresent())
                .build();
    }


    private void chooseActionStepAndStoreExperience(NStepTDHelper h, LogarithmicDecay scaler) {
        final int action = agentNeural.chooseAction(scaler.calcOut(h.episodeCounter.getCount()));
        StepReturn stepReturn = environment.step(agentNeural.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(), agentNeural.getState());
        agentNeural.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount() + 1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> rewardTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            rewardTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(rewardTerms);
    }


}
