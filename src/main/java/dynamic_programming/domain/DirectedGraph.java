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


    @NonNull  final Integer xMax, yMax;
    @Builder.Default
    Double gamma=1d;
    final Map<Edge,Double> rewards;

    @Builder
    public DirectedGraph(int xMax, int yMax, Double gamma) {
        this.xMax = xMax;
        this.yMax = yMax;
        this.gamma = gamma;
        this.rewards=new HashMap<>();
    }

    public void addReward(Edge edge,double reward) {
        throwIfBadEdge(edge);
        logIfEdgeExists(edge);
        rewards.put(edge,reward);
    }

    public Optional<Double> rewardOpt(Edge edge) {
        return  Optional.ofNullable(rewards.get(edge));  //Optional.empty() if edge not present
    }



    private void logIfEdgeExists(Edge edge) {
        Conditionals.executeIfTrue(rewards.containsKey(edge), () -> log.warning("Edge always defined, edge = "+ edge) );
    }

    private void throwIfBadEdge(Edge edge) {
        if (edge.isValid(xMax,yMax)) {
            throw new IllegalArgumentException("Bad edge, edge = "+ edge);
        }
    }

}
