package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;

import java.util.Optional;

public class ValueMemorySetter {


    DirectedGraph graph;

    public ValueMemorySetter(DirectedGraph graph) {
        this.graph = graph;
    }

    public ValueMemory createMemory() {

        ValueMemory memory=new ValueMemory();
        int x=graph.settings.xMax();
        int yMax=graph.settings.yMax();
        ActionSelector actionSelector = new ActionSelector(graph, memory);

        while (x>0) {
            int y=0;
            while (y<yMax) {
                State state = State.of(x, y);
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
        State stateNew = graph.getStateNew(state, action);
        double gamma=graph.settings.gamma();
        Double valueNewState = memory.getValue(stateNew).orElseThrow();
        Double reward = graph.getReward(state, action).orElseThrow();
        return reward + gamma * valueNewState;
    }


}
