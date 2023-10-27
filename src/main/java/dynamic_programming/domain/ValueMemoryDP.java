package dynamic_programming.domain;

import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Memory for value function, looked up by node
 * The value corresponds to the sum of rewards on the optimal path
 */

@Log
public class ValueMemoryDP {

    public static ValueMemoryDP newEmpty() {
        return new ValueMemoryDP();
    }

    Map<NodeDP, Double> values;

    public ValueMemoryDP() {
        this.values = new HashMap<>();
    }

    public Map<NodeDP, Double> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public void addValue(NodeDP node, double value) {
        logIfAlreadyPresent(node);
        values.put(node, value);
    }


    public Optional<Double> getValue(NodeDP node) {
        return Optional.ofNullable(values.get(node));  //Optional.empty() if node not present
    }

    private void logIfAlreadyPresent(NodeDP node) {
        if (values.containsKey(node)) {
            log.warning("Memory already includes node, node =" + node);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (NodeDP node : values.keySet()) {
            sb.append(node).append(":").append(values.get(node)).append(System.lineSeparator());
        }
        return sb.toString();
    }


}
