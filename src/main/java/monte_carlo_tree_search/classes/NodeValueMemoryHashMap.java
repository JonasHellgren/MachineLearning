package monte_carlo_tree_search.classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.generic_interfaces.NodeValueMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/***
 * Memory based on hash map for storing state values
 */

public class NodeValueMemoryHashMap<SSV> implements NodeValueMemoryInterface<SSV> {

    public static final double DEFAULT_VALUE = 0d;
    HashMap<Integer,Double> memory;
    Set<StateInterface<SSV>> states;

    public static <SSV> NodeValueMemoryHashMap<SSV> newEmpty() {
        return new NodeValueMemoryHashMap<>();
    }

    public NodeValueMemoryHashMap() {
        this.memory = new HashMap<>();
        this.states=new HashSet<>();
    }

    public void clear() {
        memory.clear();
    }

    @Override
    public void write(StateInterface<SSV> state, double value) {
        memory.put(state.getVariables().hashCode(),value);
        states.add(state);
    }

    @Override
    public double read(StateInterface<SSV> state) {
        return memory.getOrDefault(state.getVariables().hashCode(), DEFAULT_VALUE);
    }

    @SneakyThrows
    @Override
    public void save(String fileName) {
        throw new NoSuchMethodException();
    }

    @SneakyThrows
    @Override
    public void load(String fileName) {
        throw new NoSuchMethodException();

    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        for (StateInterface<SSV> state:states) {
            sb.append("State = ").append(state).append(", value = ").append(read(state));
            sb.append(System.getProperty("line.separator"));
        }
    return sb.toString();
    }

}
