package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;

public class TestHelper {
    public static final int X_MAX = 3, Y_MAX = 1;

    public static  DirectedGraph createExampleGraph() {
        DirectedGraph graph = DirectedGraph.newWithSize(X_MAX, Y_MAX);
        graph.addReward(Edge.of(State.of(2, 0), State.of(3, 0)), 3d);
        graph.addReward(Edge.of(State.of(2, 1), State.of(3, 0)), 1d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 0)), 2d);
        graph.addReward(Edge.of(State.of(1, 0), State.of(2, 1)), 3d);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 0)), -3);
        graph.addReward(Edge.of(State.of(1, 1), State.of(2, 1)), 5d);
        graph.addReward(Edge.of(State.of(0, 0), State.of(1, 0)), 1d);
        graph.addReward(Edge.of(State.of(0, 0), State.of(1, 1)), 3d);
        return graph;
    }

}
