package policy_gradient_problems.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TrainingTracker {

    List<TrackingItem> trackingItemList;

    public TrainingTracker() {
        trackingItemList=new ArrayList<>();
    }

    public void addActionProbabilities(int state, List<Double> actionProbabilities) {
        var trackingItem=TrackingItem.newEmpty();
        trackingItem.addItem(state,actionProbabilities);
        trackingItemList.add(trackingItem);
    }

    public boolean isEmpty() {
        return trackingItemList.isEmpty();
    }


}
