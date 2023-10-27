package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;

import java.util.Optional;


/***
 *   The memory includes all states that has at least one action, i.e. has a destination state
 *
 *  The most simple graph looks like
 *
 *              (1,1)
 *           /          \
 *      /                  \
 *  (0,0)------(1,0)-------(2,0)
 *
 *  It is debatable if the end state (2,0) shall be included in the memory
 *  The direction is to exclude it. So the number of states stored in the memory for the graph above is 3.
 *  The reason is the computational cost to identify and the virtually zero gain in doing it.
 *  The end state will have a value of zero regardless if it is included in the memory or not.
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

        ValueMemory memory=new ValueMemory();
        int x=graph.settings.xMax();
        int yMax=graph.settings.yMax();
        ActionSelector actionSelector = new ActionSelector(graph, memory);

        while (x>=0) {
            int y=0;
            while (y<=yMax) {
                var state = State.of(x, y);
                Optional<Integer> aBest = actionSelector.bestAction(state);
                if (aBest.isPresent()) {
                    int action = aBest.get();
                    double V = getValue(memory, state, action);
                    memory.addValue(state, V);
                }
                y++;
            }
            x--;
        }

        return memory;
    }

    private double getValue(ValueMemory memory, State state, int action) {
        State stateNew = graph.getNextState(state, action);
        double gamma=graph.settings.gamma();
        Double valueNewState = memory.getValue(stateNew).orElse(VALUE_IF_STATE_NOT_PRESENT_IN_MEMORY);
        Double reward = graph.getReward(state, action).orElseThrow();
        return reward + gamma * valueNewState;
    }


}
