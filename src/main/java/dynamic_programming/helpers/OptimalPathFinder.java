package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
public class OptimalPathFinder {

    DirectedGraph graph;
    ValueMemory memory;

    public OptimalPathFinder(DirectedGraph graph, ValueMemory memory) {
        logIfEmptyMemory(memory);
        this.graph = graph;
        this.memory = memory;
    }

    private static void logIfEmptyMemory(ValueMemory memory) {
        if (memory.isEmpty()) {
            log.warning("Empty memory");
        }
    }

    public List<Integer> getActionsOnPath() {
        List<Integer> actionList = new ArrayList<>();
        State state = getFirstState();
        ActionSelector actionSelector = new ActionSelector(graph, memory);
        Optional<Integer> actionOpt;
        do {
            actionOpt = actionSelector.bestAction(state);
            if (isNotReachedEndState(actionOpt.isPresent())) {
                actionList.add(actionOpt.orElseThrow());
                state = graph.getNextState(state, actionOpt.get());
            }
        } while (isNotReachedEndState(actionOpt.isPresent()));

        return actionList;
    }

    private State getFirstState() {
        var stateSet = graph.getStateSet();
        List<State> statesAtXIs0 = stateSet.stream().filter(s -> s.x() == 0).toList();
        throwIfNotOneStartState(statesAtXIs0);
        return statesAtXIs0.get(0);
    }

    public List<State> getStatesOnPath() {
        List<Integer> actionList=getActionsOnPath();

        List<State> stateList=new ArrayList<>();
        State state = getFirstState();
        for (int action:actionList) {
            stateList.add(state);
            state = graph.getNextState(state, action);
        }
        return stateList;
    }

    private static boolean isNotReachedEndState(boolean isActionPresent) {
        return isActionPresent;
    }

    private static void throwIfNotOneStartState(List<State> statesAtXIs0) {
        if (statesAtXIs0.size() != 1) {
            throw new IllegalArgumentException("Graph includes no or multiple start states, states = " + statesAtXIs0);
        }
    }


}
