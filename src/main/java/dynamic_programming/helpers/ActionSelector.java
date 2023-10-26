package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActionSelector {


    record ActionValuePair(Integer action, Double value)  {
        @Override
        public Double value() {
            return value;
        }
    }

    DirectedGraph graph;
    ValueMemory memory;

    public ActionSelector(DirectedGraph graph, ValueMemory memory) {
        this.graph = graph;
        this.memory = memory;
    }

    public int bestAction(State state) {

        double gamma=graph.getGamma();
        List<ActionSelector.ActionValuePair> pairList=new ArrayList<>();

        for (int a = 0; a < graph.getNofActions() ; a++) {
            if (graph.getReward(state,a).isPresent()) {
                double reward = graph.getReward(state, a).get();
                State stateNew=graph.getStateNew(state,a);
                double value=memory.getValue(stateNew).orElse(0d);
                pairList.add(new ActionValuePair(a,reward+gamma*value ));
            }
        }

        if (pairList.isEmpty()) {
            throw new IllegalArgumentException("No feasible actions in state ="+state);
        }

        ActionValuePair bestPair = pairList.stream().max(Comparator.comparing(v -> v.value())).get();
        return bestPair.action;

    }

}
