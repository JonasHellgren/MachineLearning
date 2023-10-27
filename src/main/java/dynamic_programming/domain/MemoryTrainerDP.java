package dynamic_programming.domain;

import common.CpuTimer;
import dynamic_programming.helpers.ActionSelectorDP;
import lombok.extern.java.Log;

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

@Log
public class MemoryTrainerDP {

    public static final double VALUE_IF_STATE_NOT_PRESENT_IN_MEMORY = 0;
    DirectedGraphDP graph;

    public MemoryTrainerDP(DirectedGraphDP graph) {
        this.graph = graph;
    }

    public ValueMemoryDP createMemory() {

        var memory=new ValueMemoryDP();
        int x=graph.settings.xMax();
        int yMax=graph.settings.yMax();
        var actionSelector = new ActionSelectorDP(graph, memory);
        CpuTimer timer=new CpuTimer();

        while (x>=0) {
            int y=0;
            while (y<=yMax) {
                var node = NodeDP.of(x, y);
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

        log.info("DP memory training time (ms) = "+timer.absoluteProgressInMillis());
        return memory;

    }

    private double getValue(ValueMemoryDP memory, NodeDP node, int action) {
        NodeDP nodeNew = graph.getNextNode(node, action);
        double gamma=graph.settings.gamma();
        Double valueNewNode = memory.getValue(nodeNew).orElse(VALUE_IF_STATE_NOT_PRESENT_IN_MEMORY);
        Double reward = graph.getReward(node, action).orElseThrow();
        return reward + gamma * valueNewNode;
    }


}
