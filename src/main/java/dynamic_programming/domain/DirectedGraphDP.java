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

 * A graph is created by adding multiple edges, each edge has a defined reward
 */

@Log
public class DirectedGraphDP {

    public static final double GAMMA = 1d;
    public static final int INDEX_FIRST_X=0;

    public static DirectedGraphDP newWithSize(int xMax, int yMax) {
        return new DirectedGraphDP(GraphSettingsDP.builder().xMax(xMax).yMax(yMax).gamma(GAMMA).build());
    }

    public GraphSettingsDP settings;  //immutable record, so public is ok
    final Map<EdgeDP,Double> rewards;

    @Builder
    public DirectedGraphDP(GraphSettingsDP settings) {
        this.settings=settings;
        this.rewards=new HashMap<>();
    }

    public int size() {
        return rewards.size();
    }

    public Set<NodeDP> getNodeSet() {
        Set<NodeDP> set0=rewards.keySet().stream().map(e -> e.n0()).collect(Collectors.toSet());
        Set<NodeDP> set1=rewards.keySet().stream().map(e -> e.n1()).collect(Collectors.toSet());
        set0.addAll(set1);
        return set0;
    }

    public void addEdgeWithReward(EdgeDP edge, double reward) {
        logIfBadEdge(edge);
        logIfEdgeExists(edge);
        rewards.put(edge,reward);
    }

    public Optional<Double> getReward(NodeDP node, int action) {
        NodeDP nodeNew = getNextNode(node, action);
        return  Optional.ofNullable(rewards.get(EdgeDP.of(node, nodeNew)));  //Optional.empty() if edge not present
    }

    public Optional<Double> getReward(EdgeDP edge) {
        return  Optional.ofNullable(rewards.get(edge));
    }

    public NodeDP getNextNode(NodeDP node, int action) {
        return !node.isXBelowOrEqualMax(settings.xMax())
                ? node
                : NodeDP.of(node.x()+1,action);
    }

    private void logIfEdgeExists(EdgeDP edge) {
        Conditionals.executeIfTrue(rewards.containsKey(edge), () -> log.warning("Edge always defined, edge = "+ edge) );
    }

    private void logIfBadEdge(EdgeDP edge) {
        if (!edge.isValid(settings.xMax(),settings.yMax())) {
            log.warning("Bad edge, edge = "+ edge);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (EdgeDP edgeDP : rewards.keySet()) {
            sb.append(edgeDP.toString()).append(":").append(rewards.get(edgeDP)).append(System.lineSeparator());
        }
        return sb.toString();
    }

}
