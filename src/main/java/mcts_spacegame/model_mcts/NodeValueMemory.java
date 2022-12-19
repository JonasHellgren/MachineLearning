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

public class NodeValueMemory<SSV> {

    public static final double DEFAULT_VALUE = 0d;
    HashMap<Integer,Double> memory;
    Set<StateInterface<SSV>> states;

    public static <SSV> NodeValueMemory<SSV> newEmpty() {
        return new NodeValueMemory<>();
    }

    public NodeValueMemory() {
        this.memory = new HashMap<>();
        this.states=new HashSet<>();
    }

    public void clear() {
        memory.clear();
    }

    public void write(StateInterface<SSV> state, double value) {
        memory.put(state.getVariables().hashCode(),value);
        states.add(state);
    }

    public double read(StateInterface<SSV> state) {
        return memory.getOrDefault(state.getVariables().hashCode(), DEFAULT_VALUE);
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
