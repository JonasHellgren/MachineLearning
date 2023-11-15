package policy_gradient_problems.common;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Getter
@Log
public class TrainingTracker {

    //todo TrackingItem -> ActionProbabilitiesAllStatesAtEpisode

    List<TrackingItem> trackingItemList;  //todo map<episode,ActionProbabilitiesAllStatesAtEpisode>

    public TrainingTracker() {
        trackingItemList = new ArrayList<>();
    }

    public void addActionProbabilities(int state, List<Double> actionProbabilities) {
        var trackingItem = TrackingItem.newEmpty();
        trackingItem.addItem(state, actionProbabilities);
        trackingItemList.add(trackingItem);
    }

    public boolean isEmpty() {
        return trackingItemList.isEmpty();
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

    private  int getNofStates() {
        var trackingItem = getAnyTrackingItem();
        return trackingItem.stateProbabilitiesMap().keySet().size();
    }

    private TrackingItem getAnyTrackingItem() {
        return trackingItemList.get(0);
    }

    private List<List<Double>> createListWithAddedTrajectories(int state,
                                                               final List<List<Double>> listOfTrajectoriesIn) {
        List<List<Double>> listOfTrajectoriesOut=new ArrayList<>(listOfTrajectoriesIn);
        TrackingItem anyTrackingItem = getAnyTrackingItem();
        int nofActions = anyTrackingItem.stateProbabilitiesMap().get(state).size();
        for (int a = 0; a < nofActions; a++) {
            List<Double> trajectory=new ArrayList<>();
            for (TrackingItem item : trackingItemList) {
                 trajectory.add(item.stateProbabilitiesMap().get(state).get(a));
            }
            listOfTrajectoriesOut.add(trajectory);
        }
        return listOfTrajectoriesOut;
    }


}
