package dynamic_programming.domain;

import common.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log
public class DirectedGraph {

    public static final double GAMMA = 1d;

    public static DirectedGraph newWithSize(int xMax, int yMax) {
        return DirectedGraph.builder().xMax(xMax).yMax(yMax).gamma(GAMMA).build();
    }

    @NonNull  final Integer xMax, yMax;
    Double gamma;
    final Map<Edge,Double> rewards;

    @Builder
    public DirectedGraph(int xMax, int yMax, Double gamma) {
        this.xMax = xMax;
        this.yMax = yMax;
        this.gamma = gamma;
        this.rewards=new HashMap<>();
    }

    public Integer size() {
        return rewards.size();
    }

    public Integer getNofActions() {
        return yMax+1;
    }

    public Double getGamma() {
        return gamma;
    }

    public void addReward(Edge edge, double reward) {
        throwIfBadEdge(edge);
        logIfEdgeExists(edge);
        rewards.put(edge,reward);
    }

    public Optional<Double> getReward(State state, int action) {
        State stateNew=getStateNew(state, action);
        return  Optional.ofNullable(rewards.get(Edge.newEdge(state,stateNew)));  //Optional.empty() if edge not present
    }

    public Optional<Double> getReward(Edge edge) {
        return  Optional.ofNullable(rewards.get(edge));
    }

    public State getStateNew(State state,int action) {
        return !state.isXBelowMax(xMax)
                ? state
                : State.newState(state.x()+1,action);
    }


    private void logIfEdgeExists(Edge edge) {
        Conditionals.executeIfTrue(rewards.containsKey(edge), () -> log.warning("Edge always defined, edge = "+ edge) );
    }

    private void throwIfBadEdge(Edge edge) {
        if (!edge.isValid(xMax,yMax)) {
            throw new IllegalArgumentException("Bad edge, edge = "+ edge);
        }
    }

}
