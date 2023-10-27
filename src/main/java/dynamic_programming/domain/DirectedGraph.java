package dynamic_programming.domain;

import common.Conditionals;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The environmental presentation
 * Includes nodes and edges
 * An action is equal to the y-value of a node, so when taking an action the new node is normally the x-value+1
 * of the present node. The y-value of the new node is equal to the action.
 *
 * A graph is created by adding multiple edges, each edge has a defined reward
 */

@Log
public class DirectedGraph {

    public static final double GAMMA = 1d;
    public static final int INDEX_FIRST_X=0;

    public static DirectedGraph newWithSize(int xMax, int yMax) {
        return new DirectedGraph(GraphSettings.builder().xMax(xMax).yMax(yMax).gamma(GAMMA).build());
    }

    public GraphSettings settings;  //immutable record so public is ok
    final Map<Edge,Double> rewards;

    @Builder
    public DirectedGraph(GraphSettings settings) {
        this.settings=settings;
        this.rewards=new HashMap<>();
    }

    public int size() {
        return rewards.size();
    }

    public Set<Node> getNodeSet() {
        Set<Node> set0=rewards.keySet().stream().map(e -> e.n0()).collect(Collectors.toSet());
        Set<Node> set1=rewards.keySet().stream().map(e -> e.n1()).collect(Collectors.toSet());
        set0.addAll(set1);
        return set0;
    }

    public void addEdgeWithReward(Edge edge, double reward) {
        throwIfBadEdge(edge);
        logIfEdgeExists(edge);
        rewards.put(edge,reward);
    }

    public Optional<Double> getReward(Node node, int action) {
        Node nodeNew = getNextNode(node, action);
        return  Optional.ofNullable(rewards.get(Edge.of(node, nodeNew)));  //Optional.empty() if edge not present
    }

    public Optional<Double> getReward(Edge edge) {
        return  Optional.ofNullable(rewards.get(edge));
    }

    public Node getNextNode(Node node, int action) {
        return !node.isXBelowMax(settings.xMax())
                ? node
                : Node.of(node.x()+1,action);
    }


    private void logIfEdgeExists(Edge edge) {
        Conditionals.executeIfTrue(rewards.containsKey(edge), () -> log.warning("Edge always defined, edge = "+ edge) );
    }

    private void throwIfBadEdge(Edge edge) {
        if (!edge.isValid(settings.xMax(),settings.yMax())) {
            throw new IllegalArgumentException("Bad edge, edge = "+ edge);
        }
    }

}
