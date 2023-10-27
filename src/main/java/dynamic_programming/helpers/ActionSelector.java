package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/***
 * Applies Bellman equation for selecting action
 * The action maximizing
 * V(s)=R(s,a)+gamma*V(T(s,a)
 * is best
 */

public class ActionSelector {

    public static final double VALUE_IF_NOT_PRESENT = 0d;

    record ActionValuePair(Integer action, Double value) {
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
        List<ActionValuePair> pairList = createValuePairList(state);
        throwIfEmptyPairList(state, pairList);
        ActionValuePair bestPair = getActionValuePairWithHighestValue(pairList);
        return bestPair.action;

    }

    @NotNull
    private List<ActionValuePair> createValuePairList(State state) {
        return IntStream.range(0, graph.settings.getNofActions())
                .filter(a -> isStateActionPresent(state, a))
                .mapToObj(a -> createActionValuePair(state, a))
                .toList();
    }

    @NotNull
    private ActionValuePair createActionValuePair(State state, int a) {
        double gamma = graph.settings.gamma();
        double reward = graph.getReward(state, a).orElseThrow();
        State stateNew = graph.getStateNew(state, a);
        double value = memory.getValue(stateNew).orElse(VALUE_IF_NOT_PRESENT);
        return new ActionValuePair(a, reward + gamma * value);
    }

    private boolean isStateActionPresent(State state, int a) {
        return graph.getReward(state, a).isPresent();
    }

    @NotNull
    private static ActionValuePair getActionValuePairWithHighestValue(List<ActionValuePair> pairList) {
        return pairList.stream().max(Comparator.comparing(v -> v.value())).orElseThrow();
    }

    private static void throwIfEmptyPairList(State state, List<ActionValuePair> pairList) {
        if (pairList.isEmpty()) {
            throw new IllegalArgumentException("No feasible actions in state =" + state);
        }
    }

}
