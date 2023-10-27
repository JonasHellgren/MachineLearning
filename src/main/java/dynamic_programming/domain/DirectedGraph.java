package dynamic_programming.domain;

import common.Conditionals;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log
public class DirectedGraph {

    public static final double GAMMA = 1d;

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

    public Set<State> getStateSet() {
        Set<State> set0=rewards.keySet().stream().map(e -> e.s0()).collect(Collectors.toSet());
        Set<State> set1=rewards.keySet().stream().map(e -> e.s1()).collect(Collectors.toSet());
        set0.addAll(set1);
        return set0;
    }

    public void addReward(Edge edge, double reward) {
        throwIfBadEdge(edge);
        logIfEdgeExists(edge);
        rewards.put(edge,reward);
    }

    public Optional<Double> getReward(State state, int action) {
        State stateNew= getNextState(state, action);
        return  Optional.ofNullable(rewards.get(Edge.of(state,stateNew)));  //Optional.empty() if edge not present
    }

    public Optional<Double> getReward(Edge edge) {
        return  Optional.ofNullable(rewards.get(edge));
    }

    public State getNextState(State state, int action) {
        return !state.isXBelowMax(settings.xMax())
                ? state
                : State.of(state.x()+1,action);
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
