package dynamic_programming2;

import dynamic_programming.domain.DirectedGraphDP;
import dynamic_programming.domain.EdgeDP;
import dynamic_programming.domain.NodeDP;

public class TestHelper {
    public static final int X_MAX = 3, Y_MAX = 1;


    /**
     * createExampleGraph is illustrated in example_graph.png
     */

    public static DirectedGraphDP createExampleGraph() {
        DirectedGraphDP graph = DirectedGraphDP.newWithSize(X_MAX, Y_MAX);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 0), NodeDP.of(3, 0)), 3d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 1), NodeDP.of(3, 0)), 1d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 0)), 2d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 1)), 3d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 0)), -3);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 1)), 5d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(0, 0), NodeDP.of(1, 0)), 1d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(0, 0), NodeDP.of(1, 1)), 3d);
        return graph;
    }

    public static DirectedGraphDP createExampleGraphUpIsNegative() {
        DirectedGraphDP graph = DirectedGraphDP.newWithSize(X_MAX, Y_MAX);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 0), NodeDP.of(3, 0)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(2, 1), NodeDP.of(3, 0)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 0)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 0), NodeDP.of(2, 1)), -1d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 0)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(1, 1), NodeDP.of(2, 1)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(0, 0), NodeDP.of(1, 0)), 0d);
        graph.addEdgeWithReward(EdgeDP.of(NodeDP.of(0, 0), NodeDP.of(1, 1)), -1d);
        return graph;
    }

}
