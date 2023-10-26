package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import dynamic_programming.helpers.ActionSelector;
import org.jetbrains.annotations.NotNull;
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
        graph.addReward(Edge.newEdge(newState(2, 0), newState(3, 0)), 3d);
        graph.addReward(Edge.newEdge(newState(2, 1), newState(3, 0)), 1d);
        graph.addReward(Edge.newEdge(newState(1, 0), newState(2, 0)), 2d);
        graph.addReward(Edge.newEdge(newState(1, 0), newState(2, 1)), 3d);
        graph.addReward(Edge.newEdge(newState(1, 1), newState(2, 0)), -3);
        graph.addReward(Edge.newEdge(newState(1, 1), newState(2, 1)), 5d);
        memory = new ValueMemory();
        memory.addValue(newState(2, 1), 1);
        memory.addValue(newState(2, 0), 3);
        actionSelector = new ActionSelector(graph, memory);

    }


    @Test
    public void whenState20or21_thenBestActionIs0() {
        int action20 = actionSelector.bestAction(newState(2, 0));
        int action21 = actionSelector.bestAction(newState(2, 1));
        Assertions.assertEquals(0, action20);
        Assertions.assertEquals(0, action21);
    }

    @Test
    public void whenState10_thenBestActionIs0() {
        int action10 = actionSelector.bestAction(newState(1, 0));
        Assertions.assertEquals(0, action10);
    }

    @Test
    public void whenState11_thenBestActionIs1() {
        int action11 = actionSelector.bestAction(newState(1, 1));
        Assertions.assertEquals(1, action11);
    }

    @Test
    public void whenState30_thenThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                actionSelector.bestAction(newState(3, 0)));
    }


    @NotNull
    private static State newState(int x, int y) {
        return State.newState(x, y);
    }

}
