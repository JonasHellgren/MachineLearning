package policy_gradient_problems.common;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Log
public class TrainingTracker {
    Map<Integer, ActionProbabilitiesAllStatesAtEpisode> episodeActionProbabilitiesMap;

    public TrainingTracker() {
        episodeActionProbabilitiesMap = new HashMap<>();
    }

    public void addActionProbabilities(int ei, int state, List<Double> actionProbabilities) {

        if (episodeActionProbabilitiesMap.containsKey(ei)) {
            var ap = episodeActionProbabilitiesMap.get(ei);
            ap.addItem(state, actionProbabilities);
        } else {
            var ap = new ActionProbabilitiesAllStatesAtEpisode(state, actionProbabilities);
            episodeActionProbabilitiesMap.put(ei, ap);
        }
    }

    /***
     * One trajectory per action is created
     */
    public List<List<Double>> getProbabilitiesTrajectoriesForState(int state) {
        List<List<Double>> emptyListOfTrajectories = new ArrayList<>();

        if (isEmpty()) {
            log.warning("No tracking results available, need to train first");
            return emptyListOfTrajectories;
        }
        if (state > getNofStates() - 1) {
            log.warning("Non existing state");
            return emptyListOfTrajectories;
        }

        return createListWithAddedTrajectories(state, emptyListOfTrajectories);
    }

    public boolean isEmpty() {
        return episodeActionProbabilitiesMap.isEmpty();
    }

    @SneakyThrows
    private ActionProbabilitiesAllStatesAtEpisode getActionProbabilitiesAllStatesAtEpisode0() {
        if (isEmpty()) {
            throw new InstantiationException("episodeActionProbabilitiesMap is empty");
        }
        return episodeActionProbabilitiesMap.get(0);
    }

    private List<List<Double>> createListWithAddedTrajectories(int state,
                                                               final List<List<Double>> listOfTrajectoriesIn) {
        var listOfTrajectoriesOut = new ArrayList<>(listOfTrajectoriesIn);
        var ap = getActionProbabilitiesAllStatesAtEpisode0();
        throwIfStateNotPresent(state, ap);
        for (int a = 0; a < getNofActions(state, ap); a++) {
            listOfTrajectoriesOut.add(createTrajectoryForActionInState(state, a));
        }
        return listOfTrajectoriesOut;
    }

    private static int getNofActions(int state, ActionProbabilitiesAllStatesAtEpisode ap) {
        return ap.stateProbabilitiesMap().get(state).size();
    }

    private int getNofStates() {
        var trackingItem = getActionProbabilitiesAllStatesAtEpisode0();
        return trackingItem.stateProbabilitiesMap().keySet().size();
    }

    private List<Double> createTrajectoryForActionInState(int state, int a) {
        List<Double> trajectory = new ArrayList<>();
        for (ActionProbabilitiesAllStatesAtEpisode item : episodeActionProbabilitiesMap.values()) {
            trajectory.add(item.stateProbabilitiesMap().get(state).get(a));
        }
        return trajectory;
    }

    private static void throwIfStateNotPresent(int state, ActionProbabilitiesAllStatesAtEpisode ap) {
        if (!ap.stateProbabilitiesMap().containsKey(state)) {
            throw new IllegalArgumentException("State not present in tracker, state = " + state);
        }
    }


}
