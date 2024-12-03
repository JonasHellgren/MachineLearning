package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraphDP;
import dynamic_programming.domain.NodeDP;
import dynamic_programming.domain.ValueMemoryDP;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/***
 * Applies Bellman equation for selecting best action
 * The action maximizing
 * V(s)=R(s,a)+gammas*V(T(s,a)
 * is best
 */

public class ActionSelectorDP {

    public static final double VALUE_IF_NOT_PRESENT = 0d;

    record ActionValuePair(Integer action, Double value) {
        @Override
        public Double value() {
            return value;
        }
    }

    DirectedGraphDP graph;
    ValueMemoryDP memory;

    public ActionSelectorDP(DirectedGraphDP graph, ValueMemoryDP memory) {
        this.graph = graph;
        this.memory = memory;
    }

    public Optional<Integer> bestAction(NodeDP node) {
        List<ActionValuePair> pairList = createValuePairList(node);
        return pairList.isEmpty()
                ? Optional.empty()  //No feasible action(s) in node
                : Optional.of(getActionValuePairWithHighestValue(pairList).action);

    }

    @NotNull
    private List<ActionValuePair> createValuePairList(NodeDP node) {
        return IntStream.range(0, graph.settings.getNofActions())
                .filter(a -> isNodeActionPresent(node, a))
                .mapToObj(a -> createActionValuePair(node, a))
                .toList();
    }

    @NotNull
    private ActionValuePair createActionValuePair(NodeDP node, int a) {
        double gamma = graph.settings.gamma();
        double reward = graph.getReward(node, a).orElseThrow();
        NodeDP nodeNew = graph.getNextNode(node, a);
        double value = memory.getValue(nodeNew).orElse(VALUE_IF_NOT_PRESENT);
        return new ActionValuePair(a, reward + gamma * value);
    }

    private boolean isNodeActionPresent(NodeDP node, int a) {
        return graph.getReward(node, a).isPresent();
    }

    @NotNull
    private static ActionValuePair getActionValuePairWithHighestValue(List<ActionValuePair> pairList) {
        return pairList.stream().max(Comparator.comparing(v -> v.value())).orElseThrow();
    }


}
