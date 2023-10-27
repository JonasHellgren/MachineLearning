package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Node;
import dynamic_programming.domain.ValueMemory;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    public Optional<Integer> bestAction(Node node) {
        List<ActionValuePair> pairList = createValuePairList(node);

        return pairList.isEmpty()
                ? Optional.empty()  //No feasible action(s) in node
                : Optional.of(getActionValuePairWithHighestValue(pairList).action);

    }

    @NotNull
    private List<ActionValuePair> createValuePairList(Node node) {
        return IntStream.range(0, graph.settings.getNofActions())
                .filter(a -> isNodeActionPresent(node, a))
                .mapToObj(a -> createActionValuePair(node, a))
                .toList();
    }

    @NotNull
    private ActionValuePair createActionValuePair(Node node, int a) {
        double gamma = graph.settings.gamma();
        double reward = graph.getReward(node, a).orElseThrow();
        Node nodeNew = graph.getNextNode(node, a);
        double value = memory.getValue(nodeNew).orElse(VALUE_IF_NOT_PRESENT);
        return new ActionValuePair(a, reward + gamma * value);
    }

    private boolean isNodeActionPresent(Node node, int a) {
        return graph.getReward(node, a).isPresent();
    }

    @NotNull
    private static ActionValuePair getActionValuePairWithHighestValue(List<ActionValuePair> pairList) {
        return pairList.stream().max(Comparator.comparing(v -> v.value())).orElseThrow();
    }


}
