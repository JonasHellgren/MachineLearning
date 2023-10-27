package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import dynamic_programming.helpers.ActionSelector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestActionSelector {

    public static final int X_MAX = 3, Y_MAX = 1;
    DirectedGraph graph;
    ActionSelector actionSelector;
    ValueMemory memory;

    @BeforeEach
    public void init() {
        graph = DirectedGraph.newWithSize(X_MAX, Y_MAX);
        graph.addReward(Edge.of(State.of(2, 0), State.of(3, 0)), 3d);
        graph.addReward(Edge.of(State.of(2, 1), State.of(3, 0)), 1d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 0)), 2d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 1)), 3d);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 0)), -3);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 1)), 5d);
        memory = new ValueMemory();
        memory.addValue(State.of(2, 1), 1);
        memory.addValue(State.of(2, 0), 3);
        actionSelector = new ActionSelector(graph, memory);
    }


    @Test
    public void whenState20or21_thenBestActionIs0() {
        int action20 = actionSelector.bestAction(State.of(2, 0)).orElseThrow();
        int action21 = actionSelector.bestAction(State.of(2, 1)).orElseThrow();
        Assertions.assertEquals(0, action20);
        Assertions.assertEquals(0, action21);
    }

    @Test
    public void whenState10_thenBestActionIs0() {
        int action10 = actionSelector.bestAction(State.of(1, 0)).orElseThrow();
        Assertions.assertEquals(0, action10);
    }

    @Test
    public void whenState11_thenBestActionIs1() {
        int action11 = actionSelector.bestAction(State.of(1, 1)).orElseThrow();
        Assertions.assertEquals(1, action11);
    }


}
