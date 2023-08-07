package multi_step_temp_diff.domain.agent_parts;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValueTracker {

    List<Double> valueHistory;

    public ValueTracker() {
        this.valueHistory = new ArrayList<>();
    }

    public void addValue(double difference) {
        valueHistory.add(difference);
    }

    public void reset() {
        valueHistory.clear();
    }

    public List<Double> getValueHistory() {
        return valueHistory;
    }

    public List<Double> getValueHistoryAbsoluteValues() {
        return valueHistory.stream().map(v -> Math.abs(v)).toList();
    }

}
