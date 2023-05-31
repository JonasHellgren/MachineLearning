package monte_carlo_tree_search.models_and_support_classes;

import monte_carlo_tree_search.interfaces.MapMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/***
 * Memory based on hash map for storing state values
 */

public class ValueMemoryHashMap<S> implements MapMemoryInterface<S> {

    public static final double DEFAULT_VALUE = 0d;
    HashMap<Integer,Double> memory;
    Set<StateInterface<S>> states;

    public static <S> ValueMemoryHashMap<S> newEmpty() {
        return new ValueMemoryHashMap<>();
    }

    public ValueMemoryHashMap() {
        this.memory = new HashMap<>();
        this.states=new HashSet<>();
    }

    public void clear() {
        memory.clear();
    }

    public void write(StateInterface<S> state, double value) {
        memory.put(state.getVariables().hashCode(),value);
        states.add(state);
    }

    @Override
    public double read(StateInterface<S> state) {
        return memory.getOrDefault(state.getVariables().hashCode(), DEFAULT_VALUE);
    }


    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        for (StateInterface<S> state:states) {
            sb.append("State = ").append(state).append(", value = ").append(read(state));
            sb.append(System.getProperty("line.separator"));
        }
    return sb.toString();
    }

}
