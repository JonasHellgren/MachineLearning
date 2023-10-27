package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import dynamic_programming.helpers.ValueMemorySetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestValueMemorySetter {

    public static final int X_MAX = 3, Y_MAX = 1;
    DirectedGraph graph;
    ValueMemory memory;
    ValueMemorySetter valueMemorySetter;

    @BeforeEach
    public void init() {
        graph = DirectedGraph.newWithSize(X_MAX, Y_MAX);
        graph.addReward(Edge.of(State.of(2, 0), State.of(3, 0)), 3d);
        graph.addReward(Edge.of(State.of(2, 1), State.of(3, 0)), 1d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 0)), 2d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 1)), 3d);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 0)), -3);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 1)), 5d);
        graph.addReward(Edge.of(State.of(0, 0), State.of(1, 0)), 1d);
        graph.addReward(Edge.of(State.of(0, 0), State.of(1, 1)), 3d);
        valueMemorySetter = new ValueMemorySetter(graph);
        memory = valueMemorySetter.createMemory();
    }


    @Test
    public void whenCreated_thenCorrectSize() {
        Assertions.assertEquals(5, memory.size());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs2() {
        int x = 2;
        Assertions.assertEquals(1, memory.getValue(State.of(x, 1)).orElseThrow());
        Assertions.assertEquals(3, memory.getValue(State.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs1() {
        int x = 1;
        Assertions.assertEquals(6, memory.getValue(State.of(x, 1)).orElseThrow());
        Assertions.assertEquals(5, memory.getValue(State.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs0() {
        int x = 0;
        Assertions.assertEquals(9, memory.getValue(State.of(x, 0)).orElseThrow());
    }


}
