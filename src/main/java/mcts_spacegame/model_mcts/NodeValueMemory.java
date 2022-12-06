package mcts_spacegame.model_mcts;

import mcts_spacegame.models_space.State;

import java.util.HashMap;

public class NodeValueMemory {

    public static final double DEFAULT_VALUE = 0d;
    HashMap<Integer,Double> memory;

    public NodeValueMemory() {
        this.memory = new HashMap<>();
    }

    public void clear() {
        memory.clear();
    }

    public void write(State state, double value) {
        memory.put(state.hashCode(),value);
    }

    public double read(State state) {
        return memory.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

}
