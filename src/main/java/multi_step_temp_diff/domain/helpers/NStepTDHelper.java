package multi_step_temp_diff.domain.helpers;

import common.Counter;
import common.ListUtils;
import common.LogarithmicDecay;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.trainer_valueobj.NStepTabularTrainerSettingsInterface;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class NStepTDHelper<S> {

    private static final int MAX_VALUE = Integer.MAX_VALUE;
    private static final int TAU = 0;

    NStepTabularTrainerSettingsInterface settings;
    AgentInfo<S> agentInfo;

    @Builder.Default
    public int T= MAX_VALUE;  //time for termination
    @Builder.Default
    public int tau= TAU;  //the state visited at this time gets updated
    @Builder.Default
    public Map<Integer,StepReturn<S>> timeReturnMap= new HashMap<>(); //in episode experienced returns
    @Builder.Default
    public Map<Integer, StateInterface<S>> statesMap= new HashMap<>(); //in episode visited states
    @NonNull  public Counter episodeCounter;
    @NonNull  public Counter timeCounter;  //time step

    public static <S> NStepTDHelper<S> newHelperFromSettingsAndAgentInfo(
            NStepTabularTrainerSettingsInterface settings,AgentInfo<S> agentInfo) {
        return NStepTDHelper.<S>builder()
                .settings(settings).agentInfo(agentInfo)
                .episodeCounter(new Counter(0, settings.nofEpis()))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
    }

    public boolean isNofEpisodesNotIsExceeded() {
        return !episodeCounter.isExceeded();
    }

    public StateInterface<S> getStateToUpdate() {
        return statesMap.get(tau);
    }

    public int getTimeBackUpFrom() {
        return tau + settings.nofStepsBetweenUpdatedAndBackuped();
    }

    public StateInterface<S> stateAheadToBackupFrom() {
        return  timeReturnMap.get(tau + settings.nofStepsBetweenUpdatedAndBackuped()).newState;
    }

    public void increaseEpisode() {
        episodeCounter.increase();
    }

    public void increaseTime() {
        timeCounter.increase();
    }

    public int getTime() {
        return timeCounter.getCount();
    }

    public int getEpisode() {
        return episodeCounter.getCount();
    }

    public StepReturn<S> chooseActionStepAndUpdateAgent(AgentInterface<S> agent,
                                                   EnvironmentInterface<S> environment,
                                                   LogarithmicDecay decayProb) {
        final int action = agent.chooseAction(decayProb.calcOut(getEpisode()));
        var stepReturn = environment.step(agent.getState(), action);
        agent.updateState(stepReturn);
        return stepReturn;
    }

    public void storeExperience(StateInterface<S> state, StepReturn<S> stepReturn) {  //agent.getState()
        statesMap.put(getTime(), state);
        timeReturnMap.put(getTime() + 1, stepReturn);
        T = (stepReturn.isNewStateTerminal) ? getTime() + 1 : T;
    }

    public static  LogarithmicDecay newLogDecayFromSettings(NStepTabularTrainerSettingsInterface settings) {
        return new LogarithmicDecay(settings.probStart(), settings.probEnd(), settings.nofEpis());
    }

    public double getDiscount() {
        return Math.pow(agentInfo.getDiscountFactor(), settings.nofStepsBetweenUpdatedAndBackuped());
    }

    public double sumOfRewardsFromTimeToUpdatePlusOne() {
        Pair<Integer, Integer> iMinMax = new Pair<>(tau + 1,
                Math.min(tau + settings.nofStepsBetweenUpdatedAndBackuped(), T));
        List<Double> rewardTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            rewardTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - tau - 1) * timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(rewardTerms);
    }

    public void reset() {
        statesMap.clear();
        timeReturnMap.clear();
        timeCounter.reset();
        T=MAX_VALUE;
        tau= TAU;
    }

    @Override
    public String toString () {
        return "T = " + T + " tau =" + tau +
                ", statesMap = " + statesMap +
                ", timeReturnMap = " + timeReturnMap.keySet() +
                " episodeCounter = " + episodeCounter.getCount() +
                " timeCounter = " + timeCounter.getCount();
    }



}
