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
public class ValueMemory {

    public static ValueMemory newEmpty() {
        return new ValueMemory();
    }

    Map<Node, Double> values;

    public ValueMemory() {
        this.values = new HashMap<>();
    }

    public Map<Node, Double> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public void addValue(Node node, double value) {
        logIfAlreadyPresent(node);
        values.put(node, value);
    }

    private void logIfAlreadyPresent(Node node) {
        if (values.containsKey(node)) {
            log.warning("Memory already includes node, node =" + node);
        }
    }

    public Optional<Double> getValue(Node node) {
        return Optional.ofNullable(values.get(node));  //Optional.empty() if node not present
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (Node node : values.keySet()) {
            sb.append(node).append(":").append(values.get(node)).append(System.lineSeparator());
        }
        return sb.toString();
    }


}
