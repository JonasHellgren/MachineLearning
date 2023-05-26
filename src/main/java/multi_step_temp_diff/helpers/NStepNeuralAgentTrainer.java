package multi_step_temp_diff.helpers;

import common.Conditionals;
import common.Counter;
import common.ListUtils;
import common.LogarithmicDecay;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.interfaces.ReplayBufferInterface;
import multi_step_temp_diff.models.AgentTabular;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Builder
public class NStepNeuralAgentTrainer {


    private static final double ALPHA = 0.5;
    private static final int N = 3;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;

    @NonNull
    EnvironmentInterface environment;
    @NonNull AgentInterface agent;

    @Builder.Default
    double alpha= ALPHA;
    @Builder.Default
    int nofStepsBetweenUpdatedAndBackuped= N;
    @Builder.Default
    int nofEpisodes= NOF_EPIS;
    @Builder.Default
    double probStart=0.5;
    @Builder.Default
    double probEnd=0.01;
    @Builder.Default
    int startState= START_STATE;

    ReplayBufferInterface buffer;

    public void train() {

        NStepTDHelper h= NStepTDHelper.builder()
                .alpha(alpha).n(nofStepsBetweenUpdatedAndBackuped)
                .episodeCounter(new Counter(0, nofEpisodes))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
        buffer=ReplayBufferNStep.newDefault();

        LogarithmicDecay decayProb=new LogarithmicDecay(probStart, probEnd,nofEpisodes);
        BiPredicate<Integer,Integer> isNotAtTerminationTime = (t, tTerm) -> t<tTerm;
        BiFunction<Integer,Integer,Integer> timeForUpdate = (t, n) -> t-n+1;
        Predicate<Integer> isUpdatePossible = (tau) -> tau>=0;
        BiPredicate<Integer,Integer> isAtTimeJustBeforeTermination = (tau,tTerm) -> tau == tTerm-1;

        while (!h.episodeCounter.isExceeded()) {
            agent.setState(startState);
            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(),h.T), () ->
                        chooseActionStepAndStoreExperience(h, decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(),h.n);
                Conditionals.executeIfTrue(isUpdatePossible.test(h.tau), () ->
                        updateStateValueForStatePresentAtTimeTau(h));
                h.timeCounter.increase();
            } while (!isAtTimeJustBeforeTermination.test(h.tau,h.T));

            h.episodeCounter.increase();
        }

    }

    public Map<Integer,Double> getStateValueMap() {
        AgentInfo agentInfo=new AgentInfo(agent);
        return agentInfo.stateValueMap(environment.stateSet());
    }

    public ReplayBufferInterface getBuffer() {
        return buffer;
    }

    static  BiPredicate<Integer,Integer> isTimeToBackUpFromAtOrBeforeTermination = (t, tTerm) -> t<=tTerm;

    private void updateStateValueForStatePresentAtTimeTau(NStepTDHelper h) {
        double sumOfRewards = sumOfRewardsFromTimeToUpdatePlusOne(h);
        Optional<Double> backupValue=Optional.empty();
        int tBackUpFrom=h.tau + h.n;
        Optional<Integer> stateAheadToBackupFrom=Optional.empty();
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom,h.T)) {
            stateAheadToBackupFrom = Optional.of(h.timeReturnMap.get(h.tau + h.n).newState);
            backupValue=Optional.of( Math.pow(agent.getDiscountFactor(), h.n) * agent.readValue(stateAheadToBackupFrom.get()));
        }

        final int stateToUpdate = h.statesMap.get(h.tau);
        double valuePresent = agent.readValue(stateToUpdate);
        AgentTabular agentCasted = (AgentTabular) agent;       //to access class specific methods
        double sumOfRewardsPlusBackupValue=sumOfRewards+backupValue.orElse(0d);
        agentCasted.writeValue(stateToUpdate, valuePresent + h.alpha * (sumOfRewardsPlusBackupValue - valuePresent));

        buffer.addExperience(NstepExperience.builder()
                .stateToUpdate(stateToUpdate).sumOfRewards(sumOfRewards)
                .stateToBackupFrom(stateAheadToBackupFrom.orElse(NstepExperience.STATE_IF_NOT_PRESENT))
                .isBackupStatePresent(stateAheadToBackupFrom.isPresent())
                .build());

    }


    private void chooseActionStepAndStoreExperience(NStepTDHelper h, LogarithmicDecay scaler) {
        final int action = agent.chooseAction(scaler.calcOut(h.episodeCounter.getCount()));
        StepReturn stepReturn = environment.step(agent.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(),agent.getState());
        agent.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount()+1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agent.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(returnTerms);
    }


}
