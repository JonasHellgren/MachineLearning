package mcts_spacegame.model_mcts;

import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/***
 * Memory for storing state values
 */

public class NodeValueMemory {

    public static final double DEFAULT_VALUE = 0d;
    HashMap<Integer,Double> memory;
    Set<StateShip> states;

    public static NodeValueMemory newEmpty() {
        return new NodeValueMemory();
    }

    public NodeValueMemory() {
        this.memory = new HashMap<>();
        this.states=new HashSet<>();
    }

    public void clear() {
        memory.clear();
    }

    public void write(StateShip state, double value) {
        memory.put(state.hashCode(),value);
        states.add(state);
    }

    public double read(StateInterface<ShipVariables> state) {
        return memory.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        for (StateShip state:states) {
            sb.append("State = ").append(state).append(", value = ").append(memory.get(state.hashCode()));
            sb.append(System.getProperty("line.separator"));
        }
    return sb.toString();
    }

}
