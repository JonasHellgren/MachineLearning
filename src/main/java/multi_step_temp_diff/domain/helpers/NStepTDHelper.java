package multi_step_temp_diff.domain.helpers;

import common.Counter;
import common.LogarithmicDecay;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import java.util.HashMap;
import java.util.Map;

@Builder
public class NStepTDHelper<S> {

    private static final int MAX_VALUE = Integer.MAX_VALUE;
    private static final int TAU = 0;

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
    @NonNull Integer nofStepsBetweenUpdatedAndBackuped;

    public static <S> NStepTDHelper<S> newFromNofEpisodesAndNofStepsBetween(int nofEpis,
                                                                        Integer nofStepsBetweenUpdatedAndBackuped ) {
        return NStepTDHelper.<S>builder()
                .episodeCounter(new Counter(0, nofEpis))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .build();
    }

    public boolean isNofEpisodesNotIsExceeded() {
        return !episodeCounter.isExceeded();
    }

    public StateInterface<S> getStateToUpdate() {
        return statesMap.get(tau);
    }

    public int getTimeBackUpFrom() {
        return tau + nofStepsBetweenUpdatedAndBackuped;
    }

    public StateInterface<S> stateAheadToBackupFrom() {
        return  timeReturnMap.get(tau + nofStepsBetweenUpdatedAndBackuped).newState;
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

    @Override
    public String toString () {
        return "T = " + T + " tau =" + tau +
                ", statesMap = " + statesMap +
                ", timeReturnMap = " + timeReturnMap.keySet() +
                " episodeCounter = " + episodeCounter.getCount() +
                " timeCounter = " + timeCounter.getCount();
    }

    public void reset() {
        statesMap.clear();
        timeReturnMap.clear();
        timeCounter.reset();
        T=MAX_VALUE;
        tau= TAU;
    }

}
