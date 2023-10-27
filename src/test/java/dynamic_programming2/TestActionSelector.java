package dynamic_programming2;

import dynamic_programming.domain.DirectedGraphDP;
import dynamic_programming.domain.EdgeDP;
import dynamic_programming.domain.NodeDP;
import dynamic_programming.domain.ValueMemoryDP;
import dynamic_programming.helpers.ActionSelectorDP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestActionSelector {

    public static final int X_MAX = 3, Y_MAX = 1;
    DirectedGraphDP graph;
    ActionSelectorDP actionSelector;
    ValueMemoryDP memory;

    @BeforeEach
    public void init() {
        graph = DirectedGraphDP.newWithSize(X_MAX, Y_MAX);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 0), NodeDP.of(3, 0)), 3d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 1), NodeDP.of(3, 0)), 1d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 0)), 2d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 1)), 3d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 0)), -3);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 1)), 5d);
        memory = new ValueMemoryDP();
        memory.addValue(NodeDP.of(2, 1), 1);
        memory.addValue(NodeDP.of(2, 0), 3);
        actionSelector = new ActionSelectorDP(graph, memory);
    }


    @Test
    public void whenState20or21_thenBestActionIs0() {
        int action20 = actionSelector.bestAction(NodeDP.of(2, 0)).orElseThrow();
        int action21 = actionSelector.bestAction(NodeDP.of(2, 1)).orElseThrow();
        Assertions.assertEquals(0, action20);
        Assertions.assertEquals(0, action21);
    }

    @Test
    public void whenState10_thenBestActionIs0() {
        int action10 = actionSelector.bestAction(NodeDP.of(1, 0)).orElseThrow();
        Assertions.assertEquals(0, action10);
    }

    @Test
    public void whenState11_thenBestActionIs1() {
        int action11 = actionSelector.bestAction(NodeDP.of(1, 1)).orElseThrow();
        Assertions.assertEquals(1, action11);
    }


}
