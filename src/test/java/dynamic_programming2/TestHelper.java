package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.Node;

public class TestHelper {
    public static final int X_MAX = 3, Y_MAX = 1;

    public static  DirectedGraph createExampleGraph() {
        DirectedGraph graph = DirectedGraph.newWithSize(X_MAX, Y_MAX);
        graph.addEdgeWithReward(Edge.of(Node.of(2, 0), Node.of(3, 0)), 3d);
        graph.addEdgeWithReward(Edge.of(Node.of(2, 1), Node.of(3, 0)), 1d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 0), Node.of(2, 0)), 2d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 0), Node.of(2, 1)), 3d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 1), Node.of(2, 0)), -3);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 1), Node.of(2, 1)), 5d);
        graph.addEdgeWithReward(Edge.of(Node.of(0, 0), Node.of(1, 0)), 1d);
        graph.addEdgeWithReward(Edge.of(Node.of(0, 0), Node.of(1, 1)), 3d);
        return graph;
    }

    public static  DirectedGraph createExampleGraphUpIsNegative() {
        DirectedGraph graph = DirectedGraph.newWithSize(X_MAX, Y_MAX);
        graph.addEdgeWithReward(Edge.of(Node.of(2, 0), Node.of(3, 0)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(2, 1), Node.of(3, 0)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 0), Node.of(2, 0)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 0), Node.of(2, 1)), -1d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 1), Node.of(2, 0)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(1, 1), Node.of(2, 1)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(0, 0), Node.of(1, 0)), 0d);
        graph.addEdgeWithReward(Edge.of(Node.of(0, 0), Node.of(1, 1)), -1d);
        return graph;
    }

}
