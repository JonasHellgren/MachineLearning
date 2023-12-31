package policy_gradient_problems.common_helpers;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import policy_gradient_problems.common_value_classes.MeasuresAllStatesAtEpisode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Log
public class TrainingTracker {
    Map<Integer, MeasuresAllStatesAtEpisode> episodeMeasuresMap;

    public TrainingTracker() {
        episodeMeasuresMap = new HashMap<>();
    }

    public void addMeasures(int ei, int state, List<Double> measures) {

        if (episodeMeasuresMap.containsKey(ei)) {
            var ap = episodeMeasuresMap.get(ei);
            ap.addItem(state, measures);
        } else {
            var ap = new MeasuresAllStatesAtEpisode(state, measures);
            episodeMeasuresMap.put(ei, ap);
        }
    }

    /***
     * One trajectory per action is created
     */
    public List<List<Double>> getMeasureTrajectoriesForState(int state) {
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
        return episodeMeasuresMap.isEmpty();
    }

    @SneakyThrows
    private MeasuresAllStatesAtEpisode getMeasuresAllStatesAtEpisode0() {
        if (isEmpty()) {
            throw new InstantiationException("map is empty");
        }
        return episodeMeasuresMap.get(0);
    }

    private List<List<Double>> createListWithAddedTrajectories(int state,
                                                               final List<List<Double>> listOfTrajectoriesIn) {
        var listOfTrajectoriesOut = new ArrayList<>(listOfTrajectoriesIn);
        var ap = getMeasuresAllStatesAtEpisode0();
        throwIfStateNotPresent(state, ap);
        for (int a = 0; a < getNofActions(state, ap); a++) {
            listOfTrajectoriesOut.add(createTrajectoryForActionInState(state, a));
        }
        return listOfTrajectoriesOut;
    }

    private static int getNofActions(int state, MeasuresAllStatesAtEpisode ap) {
        return ap.stateMeasuresMap().get(state).size();
    }

    private int getNofStates() {
        var trackingItem = getMeasuresAllStatesAtEpisode0();
        return trackingItem.stateMeasuresMap().keySet().size();
    }

    private List<Double> createTrajectoryForActionInState(int state, int a) {
        List<Double> trajectory = new ArrayList<>();
        for (MeasuresAllStatesAtEpisode item : episodeMeasuresMap.values()) {
            trajectory.add(item.stateMeasuresMap().get(state).get(a));
        }
        return trajectory;
    }

    private static void throwIfStateNotPresent(int state, MeasuresAllStatesAtEpisode ap) {
        if (!ap.stateMeasuresMap().containsKey(state)) {
            throw new IllegalArgumentException("State not present in tracker, state = " + state);
        }
    }


}
