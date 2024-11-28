package domain_design_tabular_q_learning.domain.agent.value_objects;

import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;

@Builder
public record AgentProperties(
        @With Pair<Double,Double> probRandomActionStartEnd,
        @With double learningRate,
        double defaultValue,
        @With  double gamma
       // ActionRoad[] actions
) {

    public static final double SMALL_DOUBLE = 1e-10;

    public static AgentProperties roadMaze() {
        return AgentProperties.builder()
                .probRandomActionStartEnd(Pair.create(0.5,1e-3)) //0.5,0.0
                .learningRate(0.9) //0.9,0.2 (std non zero)
                .defaultValue(-100)
                .gamma(1.0)
                .build();
    }

    public static AgentProperties tunnels() {
        return roadMaze();
    }

    public double probRandomActionExponentialDecay(double progress) {
        Double p0 = probRandomActionStartEnd.getFirst();
        Double p1 = Math.max(probRandomActionStartEnd.getSecond(), SMALL_DOUBLE);
        return p0 * Math.pow((p1 / p0), progress);
    }
}
