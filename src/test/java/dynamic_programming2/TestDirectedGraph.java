package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDirectedGraph {

    public static final int X_MAX = 3, Y_MAX = 1;
    DirectedGraph graph;

    @BeforeEach
    public void init() {
        graph=DirectedGraph.newWithSize(X_MAX, Y_MAX);
    }

    @Test
    public void whenAddingOneReward_thenCorrect() {
        graph.addReward(Edge.newEdge(newState(0,0), newState(1,0)),1d);
        assertEquals(1,graph.size());
    }

    @Test
    public void whenAddingOneReward_thenCanGet() {
        Edge edge = Edge.newEdge(newState(0, 0), newState(1, 0));
        double reward = 1d;
        graph.addReward(edge, reward);
        assertTrue(graph.getReward(edge).isPresent());
        assertEquals(reward,graph.getReward(edge).get());
    }

    @Test
    public void whenAddingOneReward_thenCanGetByActionInState() {
        State s0 = newState(0, 0);
        State s1 = newState(1, 0);
        Edge edge = Edge.newEdge(s0, s1);
        double reward = 1d;
        graph.addReward(edge, reward);
        assertTrue(graph.getReward(s0,0).isPresent());
        assertEquals(reward,graph.getReward(s0,0).get());
        Assertions.assertFalse(graph.getReward(s1,0).isPresent());
    }

    @Test
    public void whenGetStateNew_thenCorrect() {
        State s00 = newState(0, 0);
        State s10 = newState(1, 0);
        State s11 = newState(1, 1);
        State s30 = newState(3, 0);

        assertEquals(s10,graph.getStateNew(s00,0));
        assertEquals(s11,graph.getStateNew(s00,1));
        assertEquals(s30,graph.getStateNew(s30,0));  //cant move on
    }

    @NotNull
    private static State newState(int x, int y) {
        return State.newState(x,y );
    }

}
