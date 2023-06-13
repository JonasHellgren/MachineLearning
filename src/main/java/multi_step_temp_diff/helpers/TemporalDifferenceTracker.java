package multi_step_temp_diff.helpers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TemporalDifferenceTracker {

    List<Double> temporalDifferenceList;

    public TemporalDifferenceTracker() {
        this.temporalDifferenceList = new ArrayList<>();
    }

    public void addDifference(double difference) {
        temporalDifferenceList.add(difference);
    }

    public void reset() {
        temporalDifferenceList.clear();
    }




}
