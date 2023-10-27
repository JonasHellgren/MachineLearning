package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Node;
import dynamic_programming.domain.ValueMemory;

import java.util.Optional;


/***
 *   The memory includes all nodes that has at least one action, i.e. has a destination node
 *
 *  The most simple graph looks like
 *
 *              (1,1)
 *           /          \
 *      /                  \
 *  (0,0)------(1,0)-------(2,0)
 *
 *  It is debatable if the end node (2,0) shall be included in the memory
 *  The direction is to exclude it. So the number of nodes stored in the memory for the graph above is 3.
 *  The reason is the computational cost to identify and the virtually zero gain in doing it.
 *  The end node will have a value of zero regardless if it is included in the memory or not.
 *
 *
 */

public class ValueMemorySetter {

    public static final double VALUE_IF_STATE_NOT_PRESENT_IN_MEMORY = 0;
    DirectedGraph graph;

    public ValueMemorySetter(DirectedGraph graph) {
        this.graph = graph;
    }

    public ValueMemory createMemory() {

        var memory=new ValueMemory();
        int x=graph.settings.xMax();
        int yMax=graph.settings.yMax();
        var actionSelector = new ActionSelector(graph, memory);

        while (x>=0) {
            int y=0;
            while (y<=yMax) {
                var node = Node.of(x, y);
                Optional<Integer> aBest = actionSelector.bestAction(node);
                if (aBest.isPresent()) {
                    int action = aBest.get();
                    double V = getValue(memory, node, action);
                    memory.addValue(node, V);
                }
                y++;
            }
            x--;
        }

        return memory;
    }

    private double getValue(ValueMemory memory, Node node, int action) {
        Node nodeNew = graph.getNextNode(node, action);
        double gamma=graph.settings.gamma();
        Double valueNewNode = memory.getValue(nodeNew).orElse(VALUE_IF_STATE_NOT_PRESENT_IN_MEMORY);
        Double reward = graph.getReward(node, action).orElseThrow();
        return reward + gamma * valueNewNode;
    }


}
